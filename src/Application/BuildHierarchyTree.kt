package com.personio.Application

import com.personio.Domain.EmployeeRepository
import com.personio.Infrastructure.Graph

class BuildHierarchyTree(val repository: EmployeeRepository) {
    fun invoke(): Graph {
        val root = repository.all().findLast { e -> e.isDirectlyManagedBy == null }

        return root!!.generateGraph(repository.all().filter { it.name != root.name })
    }
}
