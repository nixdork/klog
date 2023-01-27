package org.nixdork.klog.frameworks.data

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.booleans.shouldBeFalse
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.date.shouldHaveSameDayAs
import io.kotest.matchers.ints.shouldBeExactly
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import org.nixdork.klog.adapters.model.PasswordCreateResetModel
import org.nixdork.klog.adapters.model.PersonLoginModel
import org.nixdork.klog.adapters.model.PersonModel
import org.nixdork.klog.common.generateHash
import org.nixdork.klog.common.generateSalt
import org.nixdork.klog.util.createAdmin
import org.nixdork.klog.util.createLogin
import org.nixdork.klog.util.createPassword
import org.nixdork.klog.util.createRandomAdmin
import org.nixdork.klog.util.createRandomAuthor
import org.nixdork.klog.util.createRandomLogin
import org.nixdork.klog.util.createRandomPassword
import org.nixdork.klog.util.faker
import org.nixdork.klog.util.insertPerson
import org.nixdork.klog.util.installPostgres
import org.nixdork.klog.util.resetPassword
import java.time.OffsetDateTime
import java.util.UUID

class ExposedPeopleRepositorySpec : FunSpec({
    installPostgres()

    lateinit var salt: String
    lateinit var hash: String
    lateinit var peopleRepo: ExposedPeopleRepository
    lateinit var meAdmin: PersonModel
    lateinit var mePwd: PasswordCreateResetModel
    lateinit var meReset: PasswordCreateResetModel
    lateinit var meLogin: PersonLoginModel
    lateinit var anyAdmin: PersonModel
    lateinit var anyPwd: PasswordCreateResetModel
    lateinit var anyLogin: PersonLoginModel
    lateinit var anyAuthor: PersonModel
    lateinit var anyPwdAuthor: PasswordCreateResetModel

    beforeSpec {
        peopleRepo = ExposedPeopleRepository()

        meAdmin = faker.createAdmin()
        mePwd = faker.createPassword(meAdmin.id)
        meReset = faker.resetPassword(meAdmin.id)
        salt = generateSalt(16)
        hash = generateHash(mePwd.newPassword, salt)
        insertPerson(meAdmin, salt, hash)
        meLogin = faker.createLogin(mePwd)

        anyAdmin = faker.createRandomAdmin()
        anyPwd = faker.createRandomPassword(anyAdmin.id, anyAdmin.email)
        salt = generateSalt(16)
        hash = generateHash(anyPwd.newPassword, salt)
        insertPerson(anyAdmin, salt, hash)
        anyLogin = faker.createLogin(anyPwd)

        anyAuthor = faker.createRandomAuthor()
        anyPwdAuthor = faker.createRandomPassword(anyAuthor.id, anyAuthor.email)
        salt = generateSalt(16)
        hash = generateHash(anyPwdAuthor.newPassword, salt)
        insertPerson(anyAuthor, salt, hash)
    }

    context("GET") {
        test("get all returns all people") {
            val people = peopleRepo.getAllPeople()
            people.count() shouldBeExactly 3
        }

        test("given existing email return person") {
            val author = peopleRepo.getPersonByEmail(anyAuthor.email)!!
            val authorPwd = peopleRepo.getPasswordByEmail(anyAuthor.email)!!
            author.email shouldBe anyAuthor.email
            author.name shouldBe anyAuthor.name
            authorPwd.hash shouldBe generateHash(anyPwdAuthor.newPassword, authorPwd.salt!!)
        }

        test("given an email that does not exist return null") {
            peopleRepo.getPersonByEmail(faker.internet.safeEmail()) shouldBe null
        }

        test("given existing id return person") {
            val author = peopleRepo.getPersonById(anyAuthor.id)!!
            author.email shouldBe anyAuthor.email
            author.name shouldBe anyAuthor.name
        }

        test("given an id that does not exist return null") {
            peopleRepo.getPersonById(UUID.randomUUID()) shouldBe null
        }

        test("given valid password return true") {
            peopleRepo.verifyPassword(anyLogin).shouldBeTrue()
            peopleRepo.verifyPassword(meLogin).shouldBeTrue()
        }

        test("given invalid password return false") {
            peopleRepo.verifyPassword(faker.createRandomLogin(anyPwdAuthor.id, anyPwdAuthor.email))
                .shouldBeFalse()
        }
    }

    context("UPSERT") {
        test("update person on pk conflict or insert otherwise") {
            val updated = peopleRepo.upsertPerson(anyAuthor.copy(name = faker.cosmere.unique.knightsRadiant()))
            val current = peopleRepo.getPersonById(anyAuthor.id)!!
            updated.id shouldBe current.id
            current.name shouldNotBe anyAuthor.name
            updated.email shouldBe current.email
            current.createdAt?.shouldHaveSameDayAs(updated.createdAt!!)
            current.updatedAt shouldNotBe null
        }

        test("update password on pk conflict or insert otherwise") {
            val before = peopleRepo.getPasswordByEmail(meAdmin.email)!!
            val update = peopleRepo.upsertPassword(meReset)
            val current = peopleRepo.getPasswordByEmail(meAdmin.email)!!
            before.email shouldBe update.email
            update.email shouldBe current.email
            current.hash shouldNotBe before.hash
            current.salt shouldBe before.salt
        }

        test("given person id update last login date") {
            val before = peopleRepo.getPersonById(meAdmin.id)!!
            val updated = peopleRepo.updateLastLogin(meAdmin.id)!!
            (before.lastLoginAt ?: OffsetDateTime.MIN) < updated.lastLoginAt
        }
    }

    context("DELETE") {
        test("delete person given an existing id") {
            val rp = peopleRepo.upsertPerson(faker.createRandomAuthor()) // random person
            peopleRepo.getPersonById(rp.id) shouldNotBe null
            peopleRepo.deletePersonById(rp.id)
            peopleRepo.getPersonById(rp.id) shouldBe null
        }

        test("do nothing given an id that does not exist") {
            val id = UUID.randomUUID()
            peopleRepo.getPersonById(UUID.randomUUID()) shouldBe null
            peopleRepo.deletePersonById(id)
            peopleRepo.getPersonById(UUID.randomUUID()) shouldBe null
        }

        test("delete person given an existing email") {
            val rp = peopleRepo.upsertPerson(faker.createRandomAuthor()) // random person
            peopleRepo.getPersonByEmail(rp.email) shouldNotBe null
            peopleRepo.deletePersonByEmail(rp.email)
            peopleRepo.getPersonByEmail(rp.email) shouldBe null
        }

        test("do nothing given an email that does not exist") {
            val email = faker.internet.safeEmail()
            peopleRepo.getPersonByEmail(email) shouldBe null
            peopleRepo.deletePersonByEmail(email)
            peopleRepo.getPersonByEmail(email) shouldBe null
        }
    }
})
