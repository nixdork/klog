package org.nixdork.klog.frameworks.data.dao

import org.jetbrains.exposed.dao.UUIDEntity
import org.jetbrains.exposed.dao.UUIDEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.UUIDTable
import java.util.UUID

object People : UUIDTable("person") {
    val name = text("name")
    val email = text("email")
    val uri = text("uri").nullable()  // FIXME make it a link
}

class Person(id: EntityID<UUID>): UUIDEntity(id) {
    companion object : UUIDEntityClass<Person>(People)

    var name by People.name
    var email by People.email
    var uri by People.uri
}
