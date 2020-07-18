package com.personio.Infrastructure

import org.jetbrains.exposed.sql.Table

object Employee : Table() {
    val name = text("name")
    val isDirectlyManagedBy = text("is_directly_managed_by").nullable()
}
