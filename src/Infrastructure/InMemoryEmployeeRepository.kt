package com.personio.Infrastructure

import com.personio.Domain.Employee
import com.personio.Domain.EmployeeRepository

class InMemoryEmployeeRepository : EmployeeRepository {
    private val employees: MutableList<Employee> = mutableListOf()

    override fun save(employee: Employee) {
        this.employees.add(employee)
    }

    override fun all(): List<Employee> {
        return employees
    }

    override fun empty() = employees.clear()
}
