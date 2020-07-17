package com.personio.Infrastructure

data class Graph(val value: String, val children: List<Graph> = emptyList())
