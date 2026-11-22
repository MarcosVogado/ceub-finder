package com.example.ceubfinder.data.model

data class Objeto(
    val id: Long,
    val categoriaId: Long,
    val categoriaNome: String?,
    val nome: String,
    val descricao: String?,
    val localEspecifico: String,
    val pontoReferencia: String?,
    val detalhesLocal: String?,
    val dataEncontrado: String,
    val dataCadastro: String,
    val fotoPath: String?,
    val status: String, // "DISPONIVEL" or "DEVOLVIDO"
    val dataDevolucao: String?
)
