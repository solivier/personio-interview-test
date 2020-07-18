package com.personio.Application

import com.personio.Domain.CycleException
import com.personio.Domain.EmployeeRepository
import com.personio.Infrastructure.EmployeeNode

class HierarchyTreeBuilder(val repository: EmployeeRepository) {
    fun invoke(): EmployeeNode {
        val root = repository.all().findLast { e -> e.isDirectlyManagedBy == null }
            ?: throw CycleException("A cycle have been detected in the hierarchy")

        return root!!.generateGraph(repository.all().filter { it.name != root.name })
    }

    fun getSupervisors(name: String): EmployeeNode {
        val leaf = repository.all().findLast { e -> e.name == name }
        val leafNode = EmployeeNode(leaf!!.name)

        if (null == leaf.isDirectlyManagedBy) {
            return leafNode
        }

        val supervisorOne = repository.all().findLast { it.name == leaf.isDirectlyManagedBy }

        if (supervisorOne!!.isDirectlyManagedBy == null) {
            return EmployeeNode(supervisorOne.name, listOf(leafNode))
        }

        val supervisorTwo = repository.all().findLast { it.name == supervisorOne.isDirectlyManagedBy }

        return EmployeeNode(supervisorTwo!!.name, listOf(EmployeeNode(supervisorOne.name, listOf(leafNode))))
    }
}
