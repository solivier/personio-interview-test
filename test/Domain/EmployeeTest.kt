package com.personio.Domain

import kotlin.test.*
import kotlin.test.assertEquals

class EmployeeTest {
    @Test
    fun superviseTest() {
        val nick = Employee("Nick")
        val barbara = Employee("Barbara")
        val peter = Employee("Peter")

        nick.supervise(barbara)
        nick.supervise(peter)
        assertEquals(nick.isDirectlyManagedBy, null)
        assertEquals(nick.isSupervisorOf.size, 2)
        assertEquals(barbara.isDirectlyManagedBy, "Nick")
        assertEquals(peter.isDirectlyManagedBy, "Nick")
    }
}
