package com.personio.Domain

interface EmployeeRepository {
    fun save(employee: Employee)
    fun get(name: String): Employee
    fun all(): List<Employee>
    fun empty()
}
