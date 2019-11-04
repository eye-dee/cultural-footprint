package de.egor.culturalfootprint.config

import com.mongodb.reactivestreams.client.MongoClient
import com.mongodb.reactivestreams.client.MongoDatabase
import org.springframework.boot.autoconfigure.mongo.MongoProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
open class MongoConnectionConfig {

    @Bean
    open fun mongoDatabase(mongoClient: MongoClient, mongoProperties: MongoProperties): MongoDatabase {
        return mongoClient.getDatabase(mongoProperties.database)
    }
}
