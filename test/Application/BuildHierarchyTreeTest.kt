package com.personio.Application

import com.personio.Domain.Employee
import com.personio.Infrastructure.Graph
import com.personio.Infrastructure.InMemoryEmployeeRepository
import kotlin.test.assertEquals
import kotlin.test.*


class BuildHierarchyTreeTest {
    @Test
    fun it_generates_a_graph_from_a_flat_structure() {
        val nick = Employee("Nick")
        val barbara = Employee("Barbara")
        val pete = Employee("Pete")
        val sophie = Employee("Sophie")
        val jonas = Employee("Jonas")

        nick.supervise(barbara)
        nick.supervise(pete)
        sophie.supervise(nick)
        jonas.supervise(sophie)

        val repository = InMemoryEmployeeRepository()

        val employeesList = listOf(nick, barbara, pete, sophie, jonas)
        employeesList.forEach { repository.save(it) }

        val getTreeHierarchy = BuildHierarchyTree(repository)
        val actual = getTreeHierarchy.invoke()

        val expected = Graph(
            "Jonas",
            listOf(
                Graph(
                    "Sophie",
                    listOf(
                        Graph(
                            "Nick",
                            listOf(
                                Graph("Barbara"),
                                Graph("Pete")
                            )
                        )
                    )
                )
            )
        )

        assertEquals(expected, actual)
    }

    @Test
    fun it_returns_supervisors() {
        val nick = Employee("Nick")
        val barbara = Employee("Barbara")
        val pete = Employee("Pete")
        val sophie = Employee("Sophie")
        val jonas = Employee("Jonas")

        nick.supervise(barbara)
        nick.supervise(pete)
        sophie.supervise(nick)
        jonas.supervise(sophie)

        val repository = InMemoryEmployeeRepository()

        val employeesList = listOf(nick, barbara, pete, sophie, jonas)
        employeesList.forEach { repository.save(it) }

        val getTreeHierarchy = BuildHierarchyTree(repository)
        val actual = getTreeHierarchy.getSupervisors("Barbara")

        val expected = Graph(
            "Sophie",
            listOf(
                Graph(
                    "Nick",
                    listOf(
                        Graph("Barbara")
                    )
                )
            )
        )

        assertEquals(expected, actual)
    }
}