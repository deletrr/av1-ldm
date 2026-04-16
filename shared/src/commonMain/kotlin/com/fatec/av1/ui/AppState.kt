package com.fatec.av1.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.fatec.av1.data.RepositorioRemoto
import com.fatec.av1.model.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

// ═══════════════════════════════════════
//  AUTH STATE
// ═══════════════════════════════════════

class AuthState(private val scope: CoroutineScope) {
    var email by mutableStateOf("")
    var senha by mutableStateOf("")
    var nome by mutableStateOf("")
    var confirmaSenha by mutableStateOf("")
    var erro by mutableStateOf<String?>(null)
    var carregando by mutableStateOf(false)
    var logado by mutableStateOf(RepositorioRemoto.usuarioLogado())

    fun login() {
        if (email.isBlank() || senha.isBlank()) { erro = "Preencha todos os campos"; return }
        carregando = true; erro = null
        scope.launch {
            RepositorioRemoto.login(email, senha)
                .onSuccess { logado = true }
                .onFailure { erro = it.message ?: "Erro ao fazer login" }
            carregando = false
        }
    }

    fun registrar() {
        if (nome.isBlank() || email.isBlank() || senha.isBlank()) { erro = "Preencha todos os campos"; return }
        if (senha != confirmaSenha) { erro = "Senhas não coincidem"; return }
        if (senha.length < 6) { erro = "Senha deve ter ao menos 6 caracteres"; return }
        carregando = true; erro = null
        scope.launch {
            RepositorioRemoto.registrar(nome, email, senha)
                .onSuccess { logado = true }
                .onFailure { erro = it.message ?: "Erro ao registrar" }
            carregando = false
        }
    }

    fun logout() { RepositorioRemoto.logout(); logado = false; limpar() }
    fun limpar() { email = ""; senha = ""; nome = ""; confirmaSenha = ""; erro = null }
}

// ═══════════════════════════════════════
//  ARTISTA STATE
// ═══════════════════════════════════════

class ArtistaState(private val scope: CoroutineScope) {
    val lista = mutableStateListOf<Artista>()
    var nome by mutableStateOf("")
    var genero by mutableStateOf("")
    var dataInicioAno by mutableStateOf("")
    var dataInicioMes by mutableStateOf("")
    var dataInicioDia by mutableStateOf("")
    var ativo by mutableStateOf(true)
    var editando by mutableStateOf(false)
    private var editId = 0L

    fun carregar() {
        scope.launch { lista.clear(); lista.addAll(RepositorioRemoto.listarArtistas()) }
    }

    fun preencher(a: Artista) {
        editando = true; editId = a.id; nome = a.nome; genero = a.genero; ativo = a.ativo
        val p = a.dataInicio.split("/")
        if (p.size == 3) { dataInicioDia = p[0]; dataInicioMes = p[1]; dataInicioAno = p[2] }
    }

    fun limpar() {
        editando = false; editId = 0L; nome = ""; genero = ""
        dataInicioAno = ""; dataInicioMes = ""; dataInicioDia = ""; ativo = true
    }

    fun salvar() {
        // CORREÇÃO: Removido o escape ${'$'} para processar a data corretamente
        val data = "${dataInicioDia.padStart(2,'0')}/${dataInicioMes.padStart(2,'0')}/$dataInicioAno"
        val artista = Artista(id = editId, nome = nome, genero = genero, dataInicio = data, ativo = ativo)
        scope.launch { RepositorioRemoto.salvarArtista(artista); limpar(); carregar() }
    }

    fun deletar(a: Artista) {
        scope.launch { RepositorioRemoto.deletarArtista(a.id); carregar() }
    }
}

// ═══════════════════════════════════════
//  ALBUM STATE
// ═══════════════════════════════════════

data class MusicaItem(val titulo: String = "", val duracao: String = "")

class AlbumState(private val scope: CoroutineScope) {
    val lista = mutableStateListOf<AlbumComArtista>()
    val artistas = mutableStateListOf<Artista>()
    var titulo by mutableStateOf("")
    var artistaSelecionado by mutableStateOf<Artista?>(null)
    var lancamentoAno by mutableStateOf("")
    var lancamentoMes by mutableStateOf("")
    var lancamentoDia by mutableStateOf("")
    var disponivel by mutableStateOf(true)
    val musicas = mutableStateListOf<MusicaItem>()
    var editando by mutableStateOf(false)
    private var editId = 0L

    val duracaoTotal: String get() {
        var totalSeg = 0
        musicas.forEach { m ->
            val p = m.duracao.split(":")
            if (p.size == 2) { totalSeg += (p[0].toIntOrNull() ?: 0) * 60 + (p[1].toIntOrNull() ?: 0) }
        }
        // CORREÇÃO: Removido o escape ${'$'} para processar a duração corretamente
        return "${totalSeg / 60}:${(totalSeg % 60).toString().padStart(2, '0')}"
    }

    fun carregar() {
        scope.launch {
            artistas.clear(); artistas.addAll(RepositorioRemoto.listarArtistas())
            val albuns = RepositorioRemoto.listarAlbuns()
            lista.clear()
            lista.addAll(albuns.map { a ->
                val artNome = artistas.find { it.id == a.artistaId }?.nome ?: ""
                AlbumComArtista(a.id, a.titulo, a.artistaId, artNome, a.lancamento, a.duracao, a.disponivel, a.musicas)
            })
        }
    }

    fun preencher(a: AlbumComArtista) {
        editando = true; editId = a.id; titulo = a.titulo
        artistaSelecionado = artistas.find { it.id == a.artistaId }; disponivel = a.disponivel
        val p = a.lancamento.split("/")
        if (p.size == 3) { lancamentoDia = p[0]; lancamentoMes = p[1]; lancamentoAno = p[2] }
        musicas.clear(); musicas.addAll(a.musicas.map { MusicaItem(it.titulo, it.duracao) })
    }

    fun limpar() {
        editando = false; editId = 0L; titulo = ""; artistaSelecionado = null
        lancamentoAno = ""; lancamentoMes = ""; lancamentoDia = ""; disponivel = true; musicas.clear()
    }

    fun adicionarMusica() { musicas.add(MusicaItem()) }
    fun removerMusica(i: Int) { musicas.removeAt(i) }
    fun atualizarMusica(i: Int, m: MusicaItem) { musicas[i] = m }

    fun salvar() {
        // CORREÇÃO: Removido o escape ${'$'} para processar a data de lançamento corretamente
        val data = "${lancamentoDia.padStart(2,'0')}/${lancamentoMes.padStart(2,'0')}/$lancamentoAno"
        val mList = musicas.mapIndexed { i, m -> Musica(id = i.toLong(), titulo = m.titulo, duracao = m.duracao) }
        val album = Album(id = editId, titulo = titulo, artistaId = artistaSelecionado?.id ?: 0L, lancamento = data, duracao = duracaoTotal, disponivel = disponivel, musicas = mList)
        scope.launch { RepositorioRemoto.salvarAlbum(album); limpar(); carregar() }
    }

    fun deletar(a: AlbumComArtista) {
        scope.launch { RepositorioRemoto.deletarAlbum(a.id); carregar() }
    }
}

// ═══════════════════════════════════════
//  CLIPE STATE
// ═══════════════════════════════════════

class ClipeState(private val scope: CoroutineScope) {
    val lista = mutableStateListOf<Clipe>()
    val artistas = mutableStateListOf<Artista>()
    var titulo by mutableStateOf("")
    var artistaSelecionado by mutableStateOf<Artista?>(null)
    var duracao by mutableStateOf("")
    var lancamentoAno by mutableStateOf("")
    var lancamentoMes by mutableStateOf("")
    var lancamentoDia by mutableStateOf("")
    var disponivel by mutableStateOf(true)
    var editando by mutableStateOf(false)
    var busca by mutableStateOf("")
    private var editId = 0L

    val listaFiltrada: List<Clipe> get() =
        if (busca.isBlank()) lista else lista.filter { it.titulo.contains(busca, true) || it.artista.contains(busca, true) }

    fun atualizarBusca(v: String) { busca = v }

    fun carregar() {
        scope.launch {
            artistas.clear(); artistas.addAll(RepositorioRemoto.listarArtistas())
            lista.clear(); lista.addAll(RepositorioRemoto.listarClipes())
        }
    }

    fun preencher(c: Clipe) {
        editando = true; editId = c.id; titulo = c.titulo
        artistaSelecionado = artistas.find { it.nome == c.artista }
        duracao = c.duracao; disponivel = c.disponivel
        val p = c.dataLancamento.split("/")
        if (p.size == 3) { lancamentoDia = p[0]; lancamentoMes = p[1]; lancamentoAno = p[2] }
    }

    fun limpar() {
        editando = false; editId = 0L; titulo = ""; artistaSelecionado = null; duracao = ""
        lancamentoAno = ""; lancamentoMes = ""; lancamentoDia = ""; disponivel = true
    }

    fun salvar() {
        // CORREÇÃO: Removido o escape ${'$'}
        val data = "${lancamentoDia.padStart(2,'0')}/${lancamentoMes.padStart(2,'0')}/$lancamentoAno"
        val clipe = Clipe(id = editId, titulo = titulo, artista = artistaSelecionado?.nome ?: "", duracao = duracao, dataLancamento = data, disponivel = disponivel)
        scope.launch { RepositorioRemoto.salvarClipe(clipe); limpar(); carregar() }
    }

    fun deletar(c: Clipe) {
        scope.launch { RepositorioRemoto.deletarClipe(c.id); carregar() }
    }
}

// ═══════════════════════════════════════
//  PLAYLIST STATE
// ═══════════════════════════════════════

class PlaylistState(private val scope: CoroutineScope) {
    val lista = mutableStateListOf<Playlist>()
    var nome by mutableStateOf("")
    var descricao by mutableStateOf("")
    var criadaEmAno by mutableStateOf("")
    var criadaEmMes by mutableStateOf("")
    var criadaEmDia by mutableStateOf("")
    var publica by mutableStateOf(true)
    var editando by mutableStateOf(false)
    var busca by mutableStateOf("")
    private var editId = 0L

    val albumsSelecionados = mutableStateListOf<Long>()
    val musicasSelecionadas = mutableStateListOf<Long>()
    val albumsFiltrados = mutableStateListOf<AlbumComArtista>()
    val musicasFiltradas = mutableStateListOf<Musica>()
    val detalheAlbums = mutableStateListOf<AlbumComArtista>()
    val detalheMusicas = mutableStateListOf<Musica>()

    private val todosAlbuns = mutableStateListOf<AlbumComArtista>()
    private val todasMusicas = mutableStateListOf<Musica>()

    fun carregar() {
        scope.launch {
            lista.clear(); lista.addAll(RepositorioRemoto.listarPlaylists())
            val artistas = RepositorioRemoto.listarArtistas()
            val albuns = RepositorioRemoto.listarAlbuns()
            todosAlbuns.clear()
            todosAlbuns.addAll(albuns.map { a ->
                val n = artistas.find { it.id == a.artistaId }?.nome ?: ""
                AlbumComArtista(a.id, a.titulo, a.artistaId, n, a.lancamento, a.duracao, a.disponivel, a.musicas)
            })
            todasMusicas.clear()
            todosAlbuns.forEach { alb ->
                alb.musicas.forEach { m ->
                    todasMusicas.add(Musica(m.id, m.titulo, m.duracao, alb.id, alb.titulo, alb.nomeArtista))
                }
            }
            atualizarBusca("")
        }
    }

    fun atualizarBusca(v: String) {
        busca = v
        albumsFiltrados.clear(); musicasFiltradas.clear()
        if (v.isBlank()) { albumsFiltrados.addAll(todosAlbuns); musicasFiltradas.addAll(todasMusicas) }
        else {
            albumsFiltrados.addAll(todosAlbuns.filter { it.titulo.contains(v, true) || it.nomeArtista.contains(v, true) })
            musicasFiltradas.addAll(todasMusicas.filter { it.titulo.contains(v, true) || it.nomeArtista.contains(v, true) })
        }
    }

    fun toggleAlbum(id: Long) { if (id in albumsSelecionados) albumsSelecionados.remove(id) else albumsSelecionados.add(id) }
    fun toggleMusica(id: Long) { if (id in musicasSelecionadas) musicasSelecionadas.remove(id) else musicasSelecionadas.add(id) }

    fun preencher(p: Playlist) {
        editando = true; editId = p.id; nome = p.nome; descricao = p.descricao; publica = p.publica
        val parts = p.criadaEm.split("/")
        if (parts.size == 3) { criadaEmDia = parts[0]; criadaEmMes = parts[1]; criadaEmAno = parts[2] }
        albumsSelecionados.clear(); albumsSelecionados.addAll(p.albumIds)
        musicasSelecionadas.clear(); musicasSelecionadas.addAll(p.musicaIds)
    }

    fun limpar() {
        editando = false; editId = 0L; nome = ""; descricao = ""
        criadaEmAno = ""; criadaEmMes = ""; criadaEmDia = ""; publica = true
        albumsSelecionados.clear(); musicasSelecionadas.clear()
    }

    fun salvar() {
        // CORREÇÃO: Removido o escape ${'$'}
        val data = "${criadaEmDia.padStart(2,'0')}/${criadaEmMes.padStart(2,'0')}/$criadaEmAno"
        val total = albumsSelecionados.size + musicasSelecionadas.size
        val playlist = Playlist(editId, nome, descricao, data, publica, total, albumsSelecionados.toList(), musicasSelecionadas.toList())
        scope.launch { RepositorioRemoto.salvarPlaylist(playlist); limpar(); carregar() }
    }

    fun deletar(p: Playlist) {
        scope.launch { RepositorioRemoto.deletarPlaylist(p.id); carregar() }
    }

    fun carregarDetalhe(playlistId: Long) {
        val playlist = lista.find { it.id == playlistId } ?: return
        scope.launch {
            val artistas = RepositorioRemoto.listarArtistas()
            val albuns = RepositorioRemoto.listarAlbuns()
            val albunsMap = albuns.associateBy { it.id }
            detalheAlbums.clear()
            playlist.albumIds.forEach { aid ->
                albunsMap[aid]?.let { a ->
                    val n = artistas.find { it.id == a.artistaId }?.nome ?: ""
                    detalheAlbums.add(AlbumComArtista(a.id, a.titulo, a.artistaId, n, a.lancamento, a.duracao, a.disponivel, a.musicas))
                }
            }
            detalheMusicas.clear()
            playlist.musicaIds.forEach { mid ->
                albuns.forEach { alb ->
                    alb.musicas.find { it.id == mid }?.let { m ->
                        val n = artistas.find { it.id == alb.artistaId }?.nome ?: ""
                        detalheMusicas.add(Musica(m.id, m.titulo, m.duracao, alb.id, alb.titulo, n))
                    }
                }
            }
        }
    }
}