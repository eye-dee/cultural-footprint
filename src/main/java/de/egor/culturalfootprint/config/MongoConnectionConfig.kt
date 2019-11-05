package de.egor.culturalfootprint.config

import com.mongodb.reactivestreams.client.MongoClient
import org.litote.kmongo.coroutine.CoroutineDatabase
import org.litote.kmongo.coroutine.coroutine
import org.litote.kmongo.reactivestreams.withKMongo
import org.springframework.boot.autoconfigure.mongo.MongoProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
open class MongoConnectionConfig {

    @Bean
    open fun mongoDatabase(mongoClient: MongoClient, mongoProperties: MongoProperties): CoroutineDatabase {
        return mongoClient.getDatabase(mongoProperties.database).withKMongo().coroutine
    }
}
