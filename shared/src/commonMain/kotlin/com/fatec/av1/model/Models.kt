package com.fatec.av1.model

import kotlinx.serialization.Serializable

@Serializable
data class Artista(
    val id: Long = 0L,
    val nome: String = "",
    val genero: String = "",
    val dataInicio: String = "",
    val ativo: Boolean = true
)

@Serializable
data class Album(
    val id: Long = 0L,
    val titulo: String = "",
    val artistaId: Long = 0L,
    val lancamento: String = "",
    val duracao: String = "0:00",
    val disponivel: Boolean = true,
    val musicas: List<Musica> = emptyList()
)

@Serializable
data class AlbumComArtista(
    val id: Long = 0L,
    val titulo: String = "",
    val artistaId: Long = 0L,
    val nomeArtista: String = "",
    val lancamento: String = "",
    val duracao: String = "0:00",
    val disponivel: Boolean = true,
    val musicas: List<Musica> = emptyList()
)

@Serializable
data class Musica(
    val id: Long = 0L,
    val titulo: String = "",
    val duracao: String = "0:00",
    val albumId: Long = 0L,
    val tituloAlbum: String = "",
    val nomeArtista: String = ""
)

@Serializable
data class Clipe(
    val id: Long = 0L,
    val titulo: String = "",
    val artista: String = "",
    val duracao: String = "",
    val dataLancamento: String = "",
    val disponivel: Boolean = true
)

@Serializable
data class Playlist(
    val id: Long = 0L,
    val nome: String = "",
    val descricao: String = "",
    val criadaEm: String = "",
    val publica: Boolean = true,
    val totalItens: Int = 0,
    val albumIds: List<Long> = emptyList(),
    val musicaIds: List<Long> = emptyList()
)
