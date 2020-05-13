package de.egor.culturalfootprint.client.telegram.repository

import de.egor.culturalfootprint.AbstractRepositoryTest
import de.egor.culturalfootprint.client.telegram.model.UserEntity
import de.egor.culturalfootprint.model.RawRecord
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.runBlocking
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

internal class UserRepositoryTest : AbstractRepositoryTest() {
    private val properties = UserRepositoryProperties()

    private val userRepository = UserRepository(db, properties)

    @BeforeEach
    internal fun setUpEach() {
        runBlocking {
            db.getCollection<RawRecord>(properties.collection).deleteMany("{}")
        }
    }

    @Test
    fun `should insert new user`() {
        runBlocking {
            val expected = UserEntity(
                chatId = 1,
                firstName = "firstName",
                lastName = "lastName",
                username = "userName"
            )

            assertThat(userRepository.updateOrInsert(expected))
                .isNotNull
                .isEqualTo(expected)
        }
    }

    @Test
    fun `should update user`() {
        runBlocking {
            val expected = UserEntity(
                chatId = 1,
                firstName = "firstName",
                lastName = "lastName",
                username = "userName"
            )

            assertThat(userRepository.updateOrInsert(expected))
                .isNotNull
                .isEqualTo(expected)

            val updated = expected.copy(
                firstName = "firstName1",
                lastName = "lastName1",
                username = "userName1"
            )

            assertThat(userRepository.updateOrInsert(updated))
                .isNotNull
                .isEqualTo(updated)
        }
    }

    @Test
    fun `should find all users`() {
        runBlocking {
            val user1 = UserEntity(chatId = 1, firstName = "firstName", lastName = "lastName", username = "userName")
            val user2 = UserEntity(chatId = 2, firstName = "firstName2", lastName = "lastName2", username = "userName2")

            assertThat(userRepository.updateOrInsert(user1)).isNotNull
            assertThat(userRepository.updateOrInsert(user2)).isNotNull

            assertThat(userRepository.findAll().toList())
                .hasSize(2)
                .containsExactlyInAnyOrder(user1, user2)
        }
    }
}
