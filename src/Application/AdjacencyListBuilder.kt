package com.personio.Application

class AdjacencyListBuilder {
    fun toAdjacencyList(supervisedToSupervisor: List<Pair<String, String>>): List<Pair<String, List<String>>> {
        val employees = supervisedToSupervisor.flatMap { e -> listOf(e.first, e.second) }.toSet()
        if (supervisedToSupervisor.map { it.second to it.first }.intersect(supervisedToSupervisor).isNotEmpty()) {
            throw RuntimeException("Cycle detected !")
        }

        return employees.map { supervisor -> supervisor to findIsSupervisorOf(supervisor, supervisedToSupervisor) }
    }

    private fun findIsSupervisorOf(employee: String, employeesHierarchy: List<Pair<String, String>>): List<String> {
        return employeesHierarchy.filter { entry -> entry.second == employee }.map { entry -> entry.first }
    }
}
