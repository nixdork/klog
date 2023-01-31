package org.nixdork.klog.util

import io.github.serpro69.kfaker.Faker
import org.nixdork.klog.adapters.model.EntryMetadataModel
import org.nixdork.klog.adapters.model.EntryModel
import org.nixdork.klog.adapters.model.PasswordCreateResetModel
import org.nixdork.klog.adapters.model.PersonLoginModel
import org.nixdork.klog.adapters.model.PersonModel
import org.nixdork.klog.adapters.model.TagModel
import org.nixdork.klog.common.Roles
import org.nixdork.klog.common.slugify
import java.time.OffsetDateTime
import java.time.ZoneOffset
import java.util.UUID

val faker by lazy { Faker() }

fun Faker.createRandomTag() =
    TagModel(
        id = UUID.randomUUID(),
        term = ancient.unique.primordial(),
        permalink = internet.domain(),
    )

fun Faker.createDatabaseTag() = createRandomTag().copy(term = "database")

fun Faker.createKotlinTag() = createRandomTag().copy(term = "kotlin")

fun Faker.createRandomAdmin(uuid: UUID = UUID.randomUUID(), email: String = internet.email()) =
    PersonModel(
        id = uuid,
        name = cosmere.allomancers(),
        email = email,
        role = Roles.ADMIN,
        uri = internet.domain(),
        avatar = cosmere.shardWorlds(),
        createdAt = OffsetDateTime.now(ZoneOffset.UTC),
    )

fun Faker.createAdmin(uuid: UUID = UUID.randomUUID()) =
    PersonModel(
        id = uuid,
        name = "Jonas Gorauskas",
        email = "jgorauskas@gmail.com",
        role = Roles.ADMIN,
        uri = "https://nixdork.org",
        avatar = "${cosmere.shardWorlds()}.png",
    )

fun Faker.createRandomAuthor(uuid: UUID = UUID.randomUUID(), email: String = internet.email()) =
    createRandomAdmin(uuid, email).copy(role = Roles.CONTRIBUTOR)

fun Faker.createRandomPassword(uuid: UUID = UUID.randomUUID(), email: String = internet.email()) =
    PasswordCreateResetModel(
        id = uuid,
        email = email,
        newPassword = random.randomString(
            length = 16,
            indexChars = true,
            auxiliaryChars = true,
            punctuationChars = true,
            numericalChars = true,
        ),
    )

fun Faker.createPassword(uuid: UUID = UUID.randomUUID()) =
    PasswordCreateResetModel(
        id = uuid,
        email = "jgorauskas@gmail.com",
        newPassword = "abcdef1234567890",
    )

fun Faker.resetRandomPassword(
    uuid: UUID = UUID.randomUUID(),
    currentPassword: String,
): PasswordCreateResetModel =
    PasswordCreateResetModel(
        id = uuid,
        email = internet.email(),
        password = currentPassword,
        newPassword = random.randomString(
            length = 16,
            indexChars = true,
            auxiliaryChars = true,
            punctuationChars = true,
            numericalChars = true,
        ),
    )

fun Faker.resetPassword(uuid: UUID = UUID.randomUUID()) =
    createPassword(uuid).copy(newPassword = "ABCDEF1234567890")

fun Faker.createRandomLogin(uuid: UUID = UUID.randomUUID(), email: String = internet.email()): PersonLoginModel =
    PersonLoginModel(
        id = uuid,
        email = email,
        password = random.randomString(
            length = 16,
            indexChars = true,
            auxiliaryChars = true,
            punctuationChars = true,
            numericalChars = true,
        ),
        role = Roles.CONTRIBUTOR,
    )

fun Faker.createLogin(pwd: PasswordCreateResetModel) =
    this.createRandomLogin(pwd.id, pwd.email).copy(password = pwd.newPassword, role = Roles.ADMIN)

fun Faker.createRandomEntry(
    author: PersonModel,
    tags: Set<TagModel>,
    howManyMetadata: Int? = 2,
): EntryModel {
    val uuid = UUID.randomUUID()
    val metadata = this.createRandomMetadata(howManyMetadata!!, uuid)
    val fakeTitle = faker.book.unique.title()
    return EntryModel(
        id = uuid,
        title = fakeTitle,
        slug = fakeTitle.slugify(),
        permalink = faker.internet.domain(),
        primaryAuthor = author,
        draft = false,
        publishedAt = OffsetDateTime.now().minusDays((2..10).random().toLong()),
        content = faker.generateParagraphs(),
        summary = summarize(faker.generateParagraphs()),
        tags = tags,
        metadata = metadata,
    )
}

internal fun Faker.createRandomMetadata(howMany: Int, entryId: UUID) =
    (0 until howMany).map {
        EntryMetadataModel(
            id = UUID.randomUUID(),
            entryId = entryId,
            key = this.random.randomString(
                length = 8,
                indexChars = true,
                auxiliaryChars = true,
                punctuationChars = true,
                numericalChars = true,
            ),
            value = this.random.randomString(
                length = 16,
                indexChars = true,
                auxiliaryChars = true,
                punctuationChars = true,
                numericalChars = true,
            ),
        )
    }.toSet()

internal fun Faker.generateParagraphs(
    howManyParagraphs: Int = 5,
    minSentencesPerParagraph: Int = 3,
    maxSentencesPerParagraph: Int = 5,
    minWordsPerSentence: Int = 5,
    maxWordsPerSentence: Int = 10,
): String {
    return (0 until howManyParagraphs).map {
        val howManySentencesPerParagraph = (minSentencesPerParagraph..maxSentencesPerParagraph).random()
        (0 until howManySentencesPerParagraph).map {
            val howManyWordsPerSentence = (minWordsPerSentence..maxWordsPerSentence).random()
            (0 until howManyWordsPerSentence).map {
                "${this.lorem.words()} ${this.lorem.supplemental()}"
            }.joinToString(" ") + this.lorem.punctuation()
        }.joinToString(" ")
    }.joinToString("\n\n")
}

internal fun summarize(content: String) =
    content.replace("\n", " ").split(" ").take(20).joinToString(" ")
