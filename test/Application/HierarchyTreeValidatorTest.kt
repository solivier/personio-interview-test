package com.personio.Application

import kotlin.test.*

class HierarchyTreeValidatorTest {
    @Test
    fun testThatEmployeeIsNotManagedTwice() {
        val employeesHierarchy = listOf(
            "Pete" to "Nick",
            "Pete" to "Sophie",
            "Barbara" to "Nick",
            "Nick" to "Sophie",
            "Sophie" to "Jonas"
        )
        val mapper = AdjacencyListBuilder()
        val actual = mapper.toAdjacencyList(employeesHierarchy)

        val validator = HierarchyTreeValidator()
        assertFails {
            validator.validate(actual)
        }
    }

    @Test
    fun testThatThereIsNoTwoCEO() {
        val employeesHierarchy = listOf(
            "Pete" to "Nick",
            "Barbara" to "Nick",
            "Nick" to "Sophie",
            "Sophie" to "Jonas",
            "Sophie" to "Thomas"
        )
        val mapper = AdjacencyListBuilder()
        val actual = mapper.toAdjacencyList(employeesHierarchy)

        val validator = HierarchyTreeValidator()
        assertFails {
            validator.validate(actual)
        }
    }

    @Test
    fun testThatThereIsNoCycleCase() {
        val employeesHierarchy = listOf(
            "Nick" to "Jonas",
            "Pete" to "Nick",
            "Jonas" to "Sophie",
            "Sophie" to "Jonas"
        )
        val mapper = AdjacencyListBuilder()
        assertFails {
            mapper.toAdjacencyList(employeesHierarchy)
        }
    }
}
