package org.nixdork.klog.frameworks.data.dao

import org.jetbrains.exposed.dao.UUIDEntity
import org.jetbrains.exposed.dao.UUIDEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.UUIDTable
import org.nixdork.klog.adapters.model.PersonModel
import java.util.UUID
import org.jetbrains.exposed.sql.javatime.timestamp
import org.nixdork.klog.adapters.model.PersonPasswordModel
import org.nixdork.klog.common.PgEnum
import org.nixdork.klog.common.Roles
import org.nixdork.klog.common.toOffsetDateTime

object People : UUIDTable("person") {
    val name = text("name")
    val email = text("email")
    val hash = text("hash")
    val salt = text("salt")
    val pwat = timestamp("pwat").nullable()
    val role = customEnumeration("role",
        "klog_role",
        { value -> Roles.valueOf(value.toString()) },
        { role -> PgEnum("klog_role", role) }
    )
    val uri = text("uri").nullable()
    val avatar = text("avatar").nullable()
    val lastLoginAt = timestamp("last_login_at").nullable()
    val createdAt = timestamp("created_at")
    val updatedAt = timestamp("updated_at").nullable()
}

class Person(id: EntityID<UUID>): UUIDEntity(id) {
    companion object : UUIDEntityClass<Person>(People)

    var name by People.name
    var email by People.email
    var hash by People.hash
    var salt by People.salt
    var pwat by People.pwat
    var role by People.role
    var uri by People.uri
    var avatar by People.avatar
    var lastLoginAt by People.lastLoginAt
    var createdAt by People.createdAt
    var updatedAt by People.updatedAt

    fun toModel(): PersonModel =
        PersonModel(
            id = this.id.value,
            name = this.name,
            email = this.email,
            passwordAt = this.pwat?.toOffsetDateTime(),
            role = this.role,
            uri = this.uri,
            avatar = this.avatar,
            lastLoginAt = this.lastLoginAt?.toOffsetDateTime(),
            createdAt = this.createdAt.toOffsetDateTime(),
            updatedAt = this.updatedAt?.toOffsetDateTime()
        )

    fun toPasswordModel(): PersonPasswordModel =
        PersonPasswordModel(
            id = this.id.value,
            email = this.email,
            hash = this.hash,
            salt = this.salt,
            passwordAt = this.pwat?.toOffsetDateTime(),
            role = this.role,
            lastLoginAt = this.lastLoginAt?.toOffsetDateTime(),
            updatedAt = this.updatedAt?.toOffsetDateTime()
        )
}
