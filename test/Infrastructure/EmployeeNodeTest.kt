package com.personio.Infrastructure

import kotlin.test.assertEquals
import kotlin.test.*

class EmployeeNodeTest {
    @Test
    fun testJson() {
        val graph = EmployeeNode(
            "Jonas",
            listOf(
                EmployeeNode(
                    "Sophie",
                    listOf(
                        EmployeeNode(
                            "Nick",
                            listOf(
                                EmployeeNode("Barbara"),
                                EmployeeNode("Pete")
                            )
                        )
                    )
                )
            )
        )

        val actual = graph.toJson()
        val expected = "{\n" +
                "  \"Jonas\" : {\n" +
                "    \"Sophie\" : {\n" +
                "      \"Nick\" : {\n" +
                "        \"Barbara\" : { },\n" +
                "        \"Pete\" : { }\n" +
                "      }\n" +
                "    }\n" +
                "  }\n" +
                "}"

        assertEquals(expected, actual)
    }
}