package org.nixdork.klog.frameworks.data

import io.kotest.core.spec.style.FunSpec
import org.nixdork.klog.adapters.model.PasswordCreateResetModel
import org.nixdork.klog.adapters.model.PersonModel
import org.nixdork.klog.adapters.model.TagModel
import org.nixdork.klog.common.generateHash
import org.nixdork.klog.common.generateSalt
import org.nixdork.klog.util.createAdmin
import org.nixdork.klog.util.createDatabaseTag
import org.nixdork.klog.util.createKotlinTag
import org.nixdork.klog.util.createPassword
import org.nixdork.klog.util.createRandomAuthor
import org.nixdork.klog.util.createRandomPassword
import org.nixdork.klog.util.faker
import org.nixdork.klog.util.insertPerson
import org.nixdork.klog.util.insertTag
import org.nixdork.klog.util.installPostgres

class ExposedEntriesRepositorySpec : FunSpec({
    installPostgres()

    lateinit var tagsRepo: ExposedTagsRepository
    lateinit var peopleRepo: ExposedPeopleRepository
    lateinit var entriesRepo: ExposedEntriesRepository

    lateinit var dbTag: TagModel
    lateinit var ktTag: TagModel

    lateinit var salt: String
    lateinit var hash: String
    lateinit var meAdmin: PersonModel
    lateinit var mePwd: PasswordCreateResetModel
    lateinit var anyAuthor: PersonModel
    lateinit var anyPwdAuthor: PasswordCreateResetModel

    beforeSpec {
        tagsRepo = ExposedTagsRepository()
        dbTag = faker.createDatabaseTag()
        ktTag = faker.createKotlinTag()
        insertTag(dbTag)
        insertTag(ktTag)

        peopleRepo = ExposedPeopleRepository()

        meAdmin = faker.createAdmin()
        mePwd = faker.createPassword(meAdmin.id)
        salt = generateSalt(16)
        hash = generateHash(mePwd.newPassword, salt)
        insertPerson(meAdmin, salt, hash)

        anyAuthor = faker.createRandomAuthor()
        anyPwdAuthor = faker.createRandomPassword(anyAuthor.id, anyAuthor.email)
        salt = generateSalt(16)
        hash = generateHash(anyPwdAuthor.newPassword, salt)
        insertPerson(anyAuthor, salt, hash)


    }

    context("GET") {
        test("get n entries return n or less entries") { }
        test("get all returns all entries") { }
        test("get by tag returns all entries for the tag") { }
        test("get archived entries returns entries grouped by date") { }
        test("given existing id return entry") { }
        test("given an id that does not exist return null") { }
        test("given existing slug return entry") { }
        test("given a slug that does not exist return null") { }
    }

    context("UPSERT") {
        test("update entry on pk conflict or insert otherwise") { }
        test("publish entry sets draft to false and published at date") { }
        test("update metadata given entry id") { }
    }

    context("DELETE") {
        test("delete entry given an existing id") { }
        test("do nothing given an id that does not exist") { }
        test("delete entry given an existing slug") { }
        test("do nothing given a slug that does not exist") { }
    }
})
