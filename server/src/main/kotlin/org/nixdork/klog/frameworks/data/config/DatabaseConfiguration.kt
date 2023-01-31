package org.nixdork.klog.frameworks.data.config

import com.zaxxer.hikari.HikariConfig
import mu.KotlinLogging
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.transactions.TransactionManager
import org.nixdork.klog.common.buildLogMessage
import org.nixdork.klog.common.toJdbcUrl
import org.nixdork.klog.frameworks.data.config.properties.DatabaseProperties
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
import java.sql.Connection
import javax.sql.DataSource

private const val TAG = "DatabaseConfiguration"
private val log = KotlinLogging.logger(TAG)

@Configuration
@EnableConfigurationProperties(value = [DatabaseProperties::class])
class DatabaseConfiguration(private val databaseProperties: DatabaseProperties) {
    init {
        log.info { "Wiring up Database" }
    }

    @Primary
    @Bean("dataSource")
    fun dataSource(): DataSource {
        val configuration = HikariConfig().apply {
            jdbcUrl = databaseProperties.toJdbcUrl()
            username = databaseProperties.username
            password = databaseProperties.password
            schema = databaseProperties.schema
            maximumPoolSize = databaseProperties.connectionPoolSize.toInt()
            transactionIsolation = "TRANSACTION_READ_COMMITTED"
        }

        return ShutdownHookHikariDataSource(emptyList(), configuration).also {
            Database.connect(it)
            TransactionManager.manager.defaultIsolationLevel = Connection.TRANSACTION_READ_COMMITTED
            log.info {
                buildLogMessage(
                    "Data source connected!",
                    "URL" to it.toJdbcUrl(),
                    "Auto Commit" to configuration.isAutoCommit,
                    "Transaction Isolation" to when (TransactionManager.manager.defaultIsolationLevel) {
                        Connection.TRANSACTION_READ_UNCOMMITTED -> "Read Uncommitted"
                        Connection.TRANSACTION_READ_COMMITTED -> "Read Committed"
                        Connection.TRANSACTION_REPEATABLE_READ -> "Repeatable Read"
                        Connection.TRANSACTION_SERIALIZABLE -> "Serializable"
                        else -> "None"
                    },
                    "Client Info" to it.connection.clientInfo,
                )
            }
        }
    }
}
