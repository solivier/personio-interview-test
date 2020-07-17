package Domain

import java.lang.RuntimeException

data class Employee(val name: String, val isSupervisorOf: MutableList<String> = mutableListOf(), var isDirectlyManagedBy: String? = null) {
    fun supervise(employee: Employee) {
        if (employee.isDirectlyManagedBy != null) {
            throw RuntimeException("Employee is already managed by ${employee.isDirectlyManagedBy}")
        }

        this.isSupervisorOf.add(employee.name)
        employee.isDirectlyManagedBy = this.name
    }
}
