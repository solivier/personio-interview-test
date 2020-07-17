package com.personio.Domain

import com.personio.Infrastructure.Graph
import java.lang.RuntimeException

data class Employee(val name: String, val isSupervisorOf: MutableList<String> = mutableListOf(), var isDirectlyManagedBy: String? = null) {
    fun supervise(employee: Employee) {
        if (employee.isDirectlyManagedBy != null) {
            throw RuntimeException("Employee is already managed by ${employee.isDirectlyManagedBy}")
        }

        this.isSupervisorOf.add(employee.name)
        employee.isDirectlyManagedBy = this.name
    }

    fun generateGraph(employeesList: List<Employee>, visited: List<String> = emptyList()): Graph {
        if (visited.contains(this.name)) {
            throw RuntimeException("cycle detected")
        }

        return Graph(this.name.toString(), employeesList.filter { it.isDirectlyManagedBy == this.name }.map { it.generateGraph(employeesList, visited.plus(this.name)) })
    }
}
