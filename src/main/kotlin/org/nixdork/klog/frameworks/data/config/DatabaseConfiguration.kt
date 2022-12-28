package org.nixdork.klog.frameworks.data.config

import com.zaxxer.hikari.HikariConfig
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.transactions.TransactionManager
import org.nixdork.klog.common.toJdbcUrl
import org.nixdork.klog.frameworks.data.ShutdownHookHikariDataSource
import org.nixdork.klog.frameworks.data.config.properties.DatabaseProperties
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
import mu.KotlinLogging
import java.sql.Connection
import javax.sql.DataSource

private const val TAG = "GetUserEvent"
private val log = KotlinLogging.logger(TAG)

@Configuration
@EnableConfigurationProperties(value = [DatabaseProperties::class])
class DatabaseConfiguration(private val databaseProperties: DatabaseProperties) {
    @Primary
    @Bean("dataSource")
    fun dataSource(): DataSource {
        val config = HikariConfig().apply {
            jdbcUrl = databaseProperties.toJdbcUrl()
            username = databaseProperties.username
            password = databaseProperties.password
            schema = databaseProperties.schema
            maximumPoolSize = databaseProperties.connectionPoolSize.toInt()
        }

        return ShutdownHookHikariDataSource(emptyList(), config).also {
            Database.connect(it)
            TransactionManager.manager.defaultIsolationLevel = Connection.TRANSACTION_READ_COMMITTED
            log.info { "Data source connected!" }
        }
    }
}
