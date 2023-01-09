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
import java.util.UUID

@Component
class ExposedPeopleRepository : PeopleRepository {
    override fun getAllPeople(): List<PersonModel> = transaction { Person.all().map { it.toModel() } }

    override fun getPersonByEmail(email: String): PersonModel? =
        transaction { Person.find(People.email eq email).singleOrNull()?.toModel() }

    override fun getPersonById(id: UUID): PersonModel? = transaction { Person.findById(id)?.toModel() }

    override fun getPasswordByEmail(email: String): PersonLoginModel? =
        transaction { Person.find(People.email eq email).singleOrNull()?.toPasswordModel() }

    override fun verifyPassword(password: PersonLoginModel): Boolean =
        transaction { password.hash == Person.find(People.email eq password.email).singleOrNull()?.hash }

    override fun updateLastLogin(personId: UUID): PersonModel? =
        transaction {
            People.update({ People.id eq personId }) { it[lastLoginAt] = Instant.now() }
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

    override fun upsertPassword(password: PersonLoginModel): PersonModel =
        transaction {
            People.upsert {
                it[id] = password.id
                it[email] = password.email
                it[hash] = password.hash
                it[salt] = password.salt
                it[role] = password.role
                it[pwat] = Instant.now()
            }.resultedValues!!
                .single()
                .let {
                    Person.wrapRow(it).toModel()
                }
        }

    override fun deletePerson(personId: UUID) {
        transaction { People.deleteWhere { People.id eq personId } }
    }
}