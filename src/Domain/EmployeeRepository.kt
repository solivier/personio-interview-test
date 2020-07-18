package com.personio.Domain

interface EmployeeRepository {
    fun save(employee: Employee)
    fun all(): List<Employee>
    fun empty()
}
