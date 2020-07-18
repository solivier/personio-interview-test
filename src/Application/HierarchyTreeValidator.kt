package com.personio.Application

import com.personio.Domain.ManagedTwiceException
import com.personio.Domain.TwoCeoException

class HierarchyTreeValidator {
    fun validate(adjacencyList: List<Pair<String, List<String>>>) {
        val employeeToHierarchy = adjacencyList.firsts().map { employee -> adjacencyList.seconds().filter { supervisedList -> supervisedList.contains(employee) }.size }
        val res = employeeToHierarchy.filter { isSupervisedBy -> isSupervisedBy > 1 }

        if (res.isNotEmpty()) {
            throw ManagedTwiceException("Employee is managed twice !")
        }

        val res2 = employeeToHierarchy.filter { isSupervisedBy -> isSupervisedBy == 0 }

        if (res2.size > 1) {
            throw TwoCeoException("There are two CEO !")
        }
    }
}

fun List<Pair<String, List<String>>>.firsts() = this.map { e -> e.first }
fun List<Pair<String, List<String>>>.seconds() = this.map { e -> e.second }
