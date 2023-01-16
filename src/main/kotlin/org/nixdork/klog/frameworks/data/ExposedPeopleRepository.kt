package org.nixdork.klog.frameworks.data

import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.update
import org.nixdork.klog.adapters.data.PeopleRepository
import org.nixdork.klog.adapters.model.PersonModel
import org.nixdork.klog.adapters.model.PersonLoginModel
import org.nixdork.klog.common.upsert
import org.nixdork.klog.frameworks.data.dao.People
import org.nixdork.klog.frameworks.data.dao.Person
import org.springframework.stereotype.Component
import java.time.Instant
import java.time.OffsetDateTime
import java.util.UUID
import org.nixdork.klog.adapters.model.PasswordCreateResetModel
import org.nixdork.klog.adapters.model.VerifyLoginModel
import org.nixdork.klog.common.generateHash
import org.nixdork.klog.common.generateSalt

@Component
class ExposedPeopleRepository : PeopleRepository {
    override fun getAllPeople(): List<PersonModel> = transaction { Person.all().map { it.toModel() } }

    override fun getPersonByEmail(email: String): PersonModel? =
        transaction { Person.find(People.email eq email).singleOrNull()?.toModel() }

    override fun getPersonById(id: UUID): PersonModel? = transaction { Person.findById(id)?.toModel() }

    override fun getPasswordByEmail(email: String): VerifyLoginModel? =
        transaction { Person.find(People.email eq email).singleOrNull()?.toVerifyLoginModel() }

    override fun verifyPassword(password: PersonLoginModel): Boolean =
        transaction {
            val pwd = getPasswordByEmail(password.email)?.let { it.hash to it.salt }
            requireNotNull(pwd) { "Person not found!" }
            requireNotNull(pwd.first) { "Password missing!" }
            requireNotNull(pwd.second) { "Password missing!" }
            pwd.first!! == generateHash(password.password, pwd.second!!)
        }

    override fun updateLastLogin(personId: UUID): PersonModel? =
        transaction {
            People.update({ People.id eq personId }) { it[lastLoginAt] = OffsetDateTime.now().toInstant() }
            Person.findById(personId)?.toModel()
        }

    override fun upsertPerson(person: PersonModel): PersonModel =
        transaction {
            People.upsert {
                it[id] = person.id
                it[email] = person.email
                it[name] = person.name
                it[role] = person.role
                it[uri] = person.uri
                it[avatar] = person.avatar
            }.resultedValues!!
                .single()
                .let {
                    Person.wrapRow(it).toModel()
                }
        }

    override fun upsertPassword(password: PasswordCreateResetModel): PersonModel =
        transaction {
            val salt = getPasswordByEmail(password.email)?.salt ?: generateSalt(16)
            People.upsert {
                it[id] = password.id
                it[email] = password.email
                it[hash] = generateHash(password.newPassword, salt)
                it[People.salt] = salt
                it[pwat] = Instant.now()
            }.resultedValues!!
                .single()
                .let {
                    Person.wrapRow(it).toModel()
                }
        }

    override fun deletePersonById(personId: UUID) {
        transaction { People.deleteWhere { People.id eq personId } }
    }

    override fun deletePersonByEmail(email: String) {
        transaction { People.deleteWhere { People.email eq email } }
    }
}