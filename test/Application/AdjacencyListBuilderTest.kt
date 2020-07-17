package com.personio.Application

import kotlin.test.*

import kotlin.test.assertEquals

class AdjacencyListBuilderTest {
    @Test
    fun testAdjacencyListFromMap() {
        val employeesHierarchy = listOf(
            "Pete" to "Nick",
            "Barbara" to "Nick",
            "Nick" to "Sophie",
            "Sophie" to "Jonas"
        )

        val mapper = AdjacencyListBuilder()
        val actual = mapper.toAdjacencyList(employeesHierarchy)

        val expected = listOf(
            "Pete" to listOf(),
            "Barbara" to listOf(),
            "Nick" to listOf("Pete", "Barbara"),
            "Sophie" to listOf("Nick"),
            "Jonas" to listOf("Sophie")
        )

        assertEquals(expected.toSet(), actual.toSet())
    }
}