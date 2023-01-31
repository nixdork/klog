package org.nixdork.klog.adapters.data

import org.nixdork.klog.adapters.model.PasswordCreateResetModel
import org.nixdork.klog.adapters.model.PersonLoginModel
import org.nixdork.klog.adapters.model.PersonModel
import org.nixdork.klog.adapters.model.VerifyLoginModel
import java.util.UUID

interface PeopleRepository {
    fun getAllPeople(): List<PersonModel>
    fun getPersonByEmail(email: String): PersonModel?
    fun getPersonById(id: UUID): PersonModel?
    fun getPasswordByEmail(email: String): VerifyLoginModel?
    fun verifyPassword(password: PersonLoginModel): Boolean

    fun updateLastLogin(personId: UUID): PersonModel?
    fun upsertPerson(person: PersonModel): PersonModel
    fun upsertPassword(password: PasswordCreateResetModel): PersonModel

    fun deletePersonById(personId: UUID)
    fun deletePersonByEmail(email: String)
}
