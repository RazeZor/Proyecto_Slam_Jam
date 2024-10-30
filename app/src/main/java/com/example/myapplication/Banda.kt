package com.example.myapplication

data class Banda(
    val idBanda: String = "",
    val lider: String = "",
    val nombreBanda: String = "",
    val genero: String = "",
    val descripcion: String = "",
    val integrantes: List<String> = emptyList()
)
