package com.personio.Infrastructure

import com.personio.Domain.Employee
import com.personio.Domain.EmployeeRepository
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction

class SqliteEmployeeRepository : EmployeeRepository {
    override fun save(employee: Employee) {
        transaction {
            SchemaUtils.create (com.personio.Infrastructure.Employee)
            com.personio.Infrastructure.Employee.insert {
                it[name] = employee.name
                it[isDirectlyManagedBy] = employee.isDirectlyManagedBy
            }
        }
    }

    override fun all(): List<Employee> {
        val employees: MutableList<Employee> = mutableListOf()
        transaction {
            com.personio.Infrastructure.Employee.selectAll().map {
                employees.add(
                    Employee(
                        name = it[com.personio.Infrastructure.Employee.name],
                        isDirectlyManagedBy = it[com.personio.Infrastructure.Employee.isDirectlyManagedBy]
                    )
                )
            }
        }
        return employees
    }

    override fun empty(): Unit {
        transaction {
            com.personio.Infrastructure.Employee.deleteAll()
        }
    }
}
