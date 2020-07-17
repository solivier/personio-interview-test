package com.personio.Infrastructure

data class EmployeeNode(val value: String, val children: List<EmployeeNode> = emptyList())
