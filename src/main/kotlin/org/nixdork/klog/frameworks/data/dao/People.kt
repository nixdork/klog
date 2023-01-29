package org.nixdork.klog.frameworks.data.dao

import org.jetbrains.exposed.dao.UUIDEntity
import org.jetbrains.exposed.dao.UUIDEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.UUIDTable
import org.nixdork.klog.adapters.model.PersonModel
import org.nixdork.klog.adapters.model.VerifyLoginModel
import org.nixdork.klog.common.CurrentOffsetDateTime
import org.nixdork.klog.common.PgEnum
import org.nixdork.klog.common.Roles
import org.nixdork.klog.common.offsetDateTime
import java.util.UUID

object People : UUIDTable("person") {
    val name = text("name").nullable()
    val email = text("email")
    val hash = text("hash").nullable()
    val salt = text("salt").nullable()
    val pwat = offsetDateTime("pwat").nullable()
    val role = customEnumeration(
        "role",
        "klog_role",
        { value -> Roles.valueOf(value.toString()) },
        { role -> PgEnum("klog_role", role) },
    )
    val uri = text("uri").nullable()
    val avatar = text("avatar").nullable()
    val lastLoginAt = offsetDateTime("last_login_at").nullable()
    val createdAt = offsetDateTime("created_at").defaultExpression(CurrentOffsetDateTime())
    val updatedAt = offsetDateTime("updated_at").nullable()
}

class Person(id: EntityID<UUID>) : UUIDEntity(id) {
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
            passwordAt = this.pwat,
            role = this.role,
            uri = this.uri,
            avatar = this.avatar,
            lastLoginAt = this.lastLoginAt,
            createdAt = this.createdAt,
            updatedAt = this.updatedAt,
        )

    fun toVerifyLoginModel(): VerifyLoginModel =
        VerifyLoginModel(
            id = this.id.value,
            email = this.email,
            hash = this.hash,
            salt = this.salt,
            role = this.role,
            lastLoginAt = this.lastLoginAt,
        )
}
