package com.fatec.av1.data

import com.fatec.av1.model.*
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.logging.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.datetime.Clock
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.*

object RepositorioRemoto {
///  EM PRODUÇÃO UTILIZE VARIAVEIS DE AMBIENTE PARA API KEY
///
///  VOCE CONSEGUE EXTRAIR O ID E A APIKEY DO ARQUIVO google-services.JSON

    private const val PROJECT_ID = "SUA_ID_AQUI"
    private const val BASE_URL = "https://firestore.googleapis.com/v1/projects/$PROJECT_ID/databases/(default)/documents"
    private const val AUTH_URL = "https://identitytoolkit.googleapis.com/v1/accounts"
    private const val API_KEY = "SUA_API_KEY_AQUI"

    // idToken e uid persistidos em memória durante a sessão
    private var idToken: String = ""
    private var uid: String = ""

    private val json = Json {
        ignoreUnknownKeys = true
        isLenient = true
        encodeDefaults = true
    }

    private val client = HttpClient {
        install(ContentNegotiation) { json(json) }
        install(Logging) { level = LogLevel.INFO }
    }

    // ════════════════════════════════════════════
    //  AUTH  (Firebase Auth REST)
    // ════════════════════════════════════════════

    @Serializable
    private data class AuthRequest(val email: String, val password: String, val returnSecureToken: Boolean = true)

    @Serializable
    private data class AuthResponse(
        val idToken: String = "",
        val localId: String = "",
        val email: String = "",
        val error: AuthError? = null
    )

    @Serializable
    private data class AuthError(val message: String = "", val code: Int = 0)

    @Serializable
    private data class AuthErrorWrapper(val error: AuthError = AuthError())

    private fun mapFirebaseError(msg: String): String = when {
        msg.contains("EMAIL_NOT_FOUND") -> "Email não encontrado"
        msg.contains("INVALID_PASSWORD") -> "Senha incorreta"
        msg.contains("INVALID_EMAIL") -> "Email inválido"
        msg.contains("USER_DISABLED") -> "Conta desativada"
        msg.contains("EMAIL_EXISTS") -> "Email já cadastrado"
        msg.contains("WEAK_PASSWORD") -> "Senha muito fraca (mínimo 6 caracteres)"
        msg.contains("TOO_MANY_ATTEMPTS") -> "Muitas tentativas. Tente mais tarde"
        msg.contains("INVALID_LOGIN_CREDENTIALS") -> "Email ou senha incorretos"
        else -> "Erro de autenticação: $msg"
    }

    suspend fun login(email: String, senha: String): Result<Unit> = runCatching {
        val response = client.post("$AUTH_URL:signInWithPassword?key=$API_KEY") {
            contentType(ContentType.Application.Json)
            setBody(AuthRequest(email, senha))
        }
        val bodyText = response.bodyAsText()
        if (response.status != HttpStatusCode.OK) {
            val errBody = runCatching { json.decodeFromString<AuthErrorWrapper>(bodyText) }.getOrNull()
            val errMsg = errBody?.error?.message ?: bodyText
            throw Exception(mapFirebaseError(errMsg))
        }
        val resp = json.decodeFromString<AuthResponse>(bodyText)
        if (resp.idToken.isEmpty()) throw Exception("Token não recebido do servidor")
        idToken = resp.idToken
        uid = resp.localId
    }

    suspend fun registrar(nome: String, email: String, senha: String): Result<Unit> = runCatching {
        val response = client.post("$AUTH_URL:signUp?key=$API_KEY") {
            contentType(ContentType.Application.Json)
            setBody(AuthRequest(email, senha))
        }
        val bodyText = response.bodyAsText()
        if (response.status != HttpStatusCode.OK) {
            val errBody = runCatching { json.decodeFromString<AuthErrorWrapper>(bodyText) }.getOrNull()
            val errMsg = errBody?.error?.message ?: bodyText
            throw Exception(mapFirebaseError(errMsg))
        }
        val resp = json.decodeFromString<AuthResponse>(bodyText)
        if (resp.idToken.isEmpty()) throw Exception("Token não recebido do servidor")
        idToken = resp.idToken
        uid = resp.localId

        // Cria perfil do usuário no Firestore
        runCatching {
            criarDocumento("usuarios", uid, wrapFields(buildJsonObject {
                put("nome", stringField(nome))
                put("email", stringField(email))
            }))
        }
        Unit
    }

    fun logout() { idToken = ""; uid = "" }
    fun usuarioLogado() = idToken.isNotEmpty()
    fun userId() = uid

    // ════════════════════════════════════════════
    //  FIRESTORE HELPERS
    // ════════════════════════════════════════════

    private suspend fun criarDocumento(colecao: String, docId: String, body: JsonObject): HttpResponse {
        val url = "$BASE_URL/$colecao/$docId?key=$API_KEY"
        return client.patch(url) {
            header(HttpHeaders.Authorization, "Bearer $idToken")
            contentType(ContentType.Application.Json)
            setBody(body)
        }
    }

    private suspend fun obterDocumentos(colecao: String): JsonObject {
        val url = "$BASE_URL/$colecao?key=$API_KEY"
        val response = client.get(url) {
            header(HttpHeaders.Authorization, "Bearer $idToken")
        }
        if (response.status != HttpStatusCode.OK) return buildJsonObject { }
        val bodyText = response.bodyAsText()
        return runCatching { json.decodeFromString<JsonObject>(bodyText) }.getOrElse { buildJsonObject { } }
    }

    private suspend fun deletarDocumento(colecao: String, docId: String) {
        client.delete("$BASE_URL/$colecao/$docId?key=$API_KEY") {
            header(HttpHeaders.Authorization, "Bearer $idToken")
        }
    }

    // ════════════════════════════════════════════
    //  CONVERSORES  Firestore → Kotlin
    // ════════════════════════════════════════════

    private fun JsonObject.str(field: String): String =
        this["fields"]?.jsonObject?.get(field)?.jsonObject?.get("stringValue")?.jsonPrimitive?.content ?: ""

    private fun JsonObject.long(field: String): Long =
        this["fields"]?.jsonObject?.get(field)?.jsonObject?.let {
            it["integerValue"]?.jsonPrimitive?.content?.toLongOrNull()
                ?: it["doubleValue"]?.jsonPrimitive?.content?.toDoubleOrNull()?.toLong()
        } ?: 0L

    private fun JsonObject.bool(field: String): Boolean =
        this["fields"]?.jsonObject?.get(field)?.jsonObject?.get("booleanValue")?.jsonPrimitive?.booleanOrNull ?: false

    private fun JsonObject.int(field: String): Int = long(field).toInt()

    private fun JsonObject.longList(field: String): List<Long> =
        this["fields"]?.jsonObject?.get(field)?.jsonObject?.get("arrayValue")
            ?.jsonObject?.get("values")?.jsonArray?.mapNotNull {
                it.jsonObject["integerValue"]?.jsonPrimitive?.content?.toLongOrNull()
            } ?: emptyList()

    private fun JsonObject.mapList(field: String): List<JsonObject> =
        this["fields"]?.jsonObject?.get(field)?.jsonObject?.get("arrayValue")
            ?.jsonObject?.get("values")?.jsonArray?.mapNotNull {
                it.jsonObject["mapValue"]?.jsonObject
            } ?: emptyList()

    private fun parseDocuments(response: JsonObject): List<JsonObject> =
        response["documents"]?.jsonArray?.map { it.jsonObject } ?: emptyList()

    // ════════════════════════════════════════════
    //  BUILDERS  Kotlin → Firestore JSON
    // ════════════════════════════════════════════

    private fun stringField(v: String) = buildJsonObject { put("stringValue", v) }
    private fun intField(v: Long) = buildJsonObject { put("integerValue", v.toString()) }
    private fun boolField(v: Boolean) = buildJsonObject { put("booleanValue", v) }

    private fun arrayOfLongs(list: List<Long>) = buildJsonObject {
        put("arrayValue", buildJsonObject {
            put("values", buildJsonArray {
                list.forEach { add(buildJsonObject { put("integerValue", it.toString()) }) }
            })
        })
    }

    private fun wrapFields(fields: JsonObject) = buildJsonObject { put("fields", fields) }

    // ════════════════════════════════════════════
    //  ARTISTAS
    // ════════════════════════════════════════════

    suspend fun listarArtistas(): List<Artista> {
        val resp = obterDocumentos("artistas")
        return parseDocuments(resp).map { doc ->
            Artista(doc.long("id"), doc.str("nome"), doc.str("genero"), doc.str("dataInicio"), doc.bool("ativo"))
        }
    }

    suspend fun salvarArtista(a: Artista): Long {
        val id = if (a.id == 0L) now() else a.id
        criarDocumento("artistas", id.toString(), wrapFields(buildJsonObject {
            put("id", intField(id))
            put("nome", stringField(a.nome))
            put("genero", stringField(a.genero))
            put("dataInicio", stringField(a.dataInicio))
            put("ativo", boolField(a.ativo))
            put("userId", stringField(uid))
        }))
        return id
    }

    suspend fun deletarArtista(id: Long) = deletarDocumento("artistas", id.toString())

    // ════════════════════════════════════════════
    //  ÁLBUNS
    // ════════════════════════════════════════════

    suspend fun listarAlbuns(): List<Album> {
        val resp = obterDocumentos("albuns")
        return parseDocuments(resp).map { doc ->
            Album(
                doc.long("id"), doc.str("titulo"), doc.long("artistaId"), doc.str("lancamento"),
                doc.str("duracao"), doc.bool("disponivel"),
                doc.mapList("musicas").map { m ->
                    val fields = m["fields"]?.jsonObject
                    Musica(
                        fields?.get("id")?.jsonObject?.get("integerValue")?.jsonPrimitive?.content?.toLongOrNull() ?: 0L,
                        fields?.get("titulo")?.jsonObject?.get("stringValue")?.jsonPrimitive?.content ?: "",
                        fields?.get("duracao")?.jsonObject?.get("stringValue")?.jsonPrimitive?.content ?: ""
                    )
                }
            )
        }
    }

    suspend fun salvarAlbum(a: Album): Long {
        val id = if (a.id == 0L) now() else a.id
        val musicasArray = buildJsonArray {
            a.musicas.forEach { m ->
                add(buildJsonObject {
                    put("mapValue", buildJsonObject {
                        put("fields", buildJsonObject {
                            put("id", intField(m.id))
                            put("titulo", stringField(m.titulo))
                            put("duracao", stringField(m.duracao))
                        })
                    })
                })
            }
        }
        criarDocumento("albuns", id.toString(), wrapFields(buildJsonObject {
            put("id", intField(id))
            put("titulo", stringField(a.titulo))
            put("artistaId", intField(a.artistaId))
            put("lancamento", stringField(a.lancamento))
            put("duracao", stringField(a.duracao))
            put("disponivel", boolField(a.disponivel))
            put("musicas", buildJsonObject { put("arrayValue", buildJsonObject { put("values", musicasArray) }) })
            put("userId", stringField(uid))
        }))
        return id
    }

    suspend fun deletarAlbum(id: Long) = deletarDocumento("albuns", id.toString())

    // ════════════════════════════════════════════
    //  CLIPES
    // ════════════════════════════════════════════

    suspend fun listarClipes(): List<Clipe> {
        val resp = obterDocumentos("clipes")
        return parseDocuments(resp).map { doc ->
            Clipe(
                doc.long("id"), doc.str("titulo"), doc.str("artista"),
                doc.str("duracao"), doc.str("dataLancamento"), doc.bool("disponivel")
            )
        }
    }

    suspend fun salvarClipe(c: Clipe): Long {
        val id = if (c.id == 0L) now() else c.id
        criarDocumento("clipes", id.toString(), wrapFields(buildJsonObject {
            put("id", intField(id))
            put("titulo", stringField(c.titulo))
            put("artista", stringField(c.artista))
            put("duracao", stringField(c.duracao))
            put("dataLancamento", stringField(c.dataLancamento))
            put("disponivel", boolField(c.disponivel))
            put("userId", stringField(uid))
        }))
        return id
    }

    suspend fun deletarClipe(id: Long) = deletarDocumento("clipes", id.toString())

    // ════════════════════════════════════════════
    //  PLAYLISTS
    // ════════════════════════════════════════════

    suspend fun listarPlaylists(): List<Playlist> {
        val resp = obterDocumentos("playlists")
        return parseDocuments(resp).map { doc ->
            Playlist(
                doc.long("id"), doc.str("nome"), doc.str("descricao"), doc.str("criadaEm"),
                doc.bool("publica"), doc.int("totalItens"), doc.longList("albumIds"), doc.longList("musicaIds")
            )
        }
    }

    suspend fun salvarPlaylist(p: Playlist): Long {
        val id = if (p.id == 0L) now() else p.id
        criarDocumento("playlists", id.toString(), wrapFields(buildJsonObject {
            put("id", intField(id))
            put("nome", stringField(p.nome))
            put("descricao", stringField(p.descricao))
            put("criadaEm", stringField(p.criadaEm))
            put("publica", boolField(p.publica))
            put("totalItens", intField(p.totalItens.toLong()))
            put("albumIds", arrayOfLongs(p.albumIds))
            put("musicaIds", arrayOfLongs(p.musicaIds))
            put("userId", stringField(uid))
        }))
        return id
    }

    suspend fun deletarPlaylist(id: Long) = deletarDocumento("playlists", id.toString())

    private fun now(): Long = Clock.System.now().toEpochMilliseconds()
}
