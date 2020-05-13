package de.egor.culturalfootprint

import de.flapdoodle.embed.mongo.MongodStarter
import de.flapdoodle.embed.mongo.config.MongodConfigBuilder
import de.flapdoodle.embed.mongo.config.Net
import de.flapdoodle.embed.mongo.distribution.Version
import de.flapdoodle.embed.process.runtime.Network
import org.litote.kmongo.coroutine.coroutine
import org.litote.kmongo.reactivestreams.KMongo

open class AbstractRepositoryTest {
    companion object {
        private const val ip = "localhost"
        private const val port = 27017

        private val mongodConfig = MongodConfigBuilder().version(Version.Main.PRODUCTION)
            .net(Net(ip, port, Network.localhostIsIPv6()))
            .build()

        private val starter = MongodStarter.getDefaultInstance()
        val mongodExecutable = starter.prepare(mongodConfig)
            .also {
                it.start()
            }

        private val mongoClient = KMongo.createClient().coroutine
        val db = mongoClient.getDatabase("cultural")
    }
}
