package com.personio.Application

import com.personio.Domain.EmployeeRepository
import com.personio.Infrastructure.Graph

class BuildHierarchyTree(val repository: EmployeeRepository) {
    fun invoke(): Graph {
        val root = repository.all().findLast { e -> e.isDirectlyManagedBy == null }

        return root!!.generateGraph(repository.all().filter { it.name != root.name })
    }

    fun getSupervisors(name: String): Graph {
        val leaf = repository.all().findLast { e -> e.name == name }
        val leafNode = Graph(leaf!!.name)

        if (null == leaf.isDirectlyManagedBy) {
            return leafNode
        }

        val supervisorOne = repository.all().findLast { it.name == leaf.isDirectlyManagedBy }

        if (supervisorOne!!.isDirectlyManagedBy == null) {
            return Graph(supervisorOne.name, listOf(leafNode))
        }

        val supervisorTwo = repository.all().findLast { it.name == supervisorOne.isDirectlyManagedBy }

        return Graph(supervisorTwo!!.name, listOf(Graph(supervisorOne.name, listOf(leafNode))))
    }
}
