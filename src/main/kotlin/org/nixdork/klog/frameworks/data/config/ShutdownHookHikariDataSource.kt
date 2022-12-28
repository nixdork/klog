package org.nixdork.klog.frameworks.data

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import java.time.Duration

@Suppress("LongParameterList")
class ShutdownHookHikariDataSource(
    private val shutdownHooks: List<Runnable>,
    config: HikariConfig
) : HikariDataSource(config) {
    override fun close() {
        shutdownHooks.forEach(Runnable::run)
        super.close()
    }

    companion object {
        fun build(
            url: String,
            username: String,
            password: String,
            autoCommit: Boolean = false,
            connectionTimeout: Long = Duration.ofSeconds(30).toMillis(),
            keepaliveTime: Long = Duration.ofSeconds(0).toMillis(),
            maxLifetime: Long = Duration.ofMinutes(30).toMillis(),
            maximumPoolSize: Int = 10,
            leakDetectionThreshold: Long = Duration.ofSeconds(10).toMillis(),
            validationTimeout: Long = Duration.ofSeconds(5).toMillis(),
            properties: Map<String, String> = emptyMap(),
            shutdownHooks: List<Runnable> = emptyList(),
        ): ShutdownHookHikariDataSource {
            val config = HikariConfig()

            config.jdbcUrl = url
            config.username = username
            config.password = password
            //  DO NOT configure schema, results in transactions being opened automatically,
            //  doesn't play nice with exposed: https://github.com/JetBrains/Exposed/issues/1183
            //  config.schema = databaseConfig.schema
            return HikariCPConfig(
                autoCommit,
                connectionTimeout,
                keepaliveTime,
                maxLifetime,
                maximumPoolSize,
                leakDetectionThreshold,
                validationTimeout
            ).let {
                ShutdownHookHikariDataSource(shutdownHooks, build(config, it, properties))
            }
        }

        private fun build(
            config: HikariConfig,
            hikari: HikariCPConfig,
            properties: Map<String, String> = emptyMap(),
        ): HikariConfig {
            with(hikari) {
                config.isAutoCommit = autoCommit
                config.connectionTimeout = connectionTimeout
                config.keepaliveTime = keepaliveTime
                config.maxLifetime = maxLifetime
                config.maximumPoolSize = maximumPoolSize
                config.minimumIdle = (maximumPoolSize / 2).coerceAtLeast(1)
                config.leakDetectionThreshold = leakDetectionThreshold
                config.validationTimeout = validationTimeout
            }

            properties.forEach { config.addDataSourceProperty(it.key, it.value) }

            return config
        }
    }
}

data class HikariCPConfig(
    val autoCommit: Boolean = false,
    val connectionTimeout: Long = Duration.ofSeconds(30).toMillis(),
    val keepaliveTime: Long = Duration.ofSeconds(0).toMillis(),
    val maxLifetime: Long = Duration.ofMinutes(30).toMillis(),
    val maximumPoolSize: Int = 10,
    val leakDetectionThreshold: Long = Duration.ofSeconds(10).toMillis(),
    val validationTimeout: Long = Duration.ofSeconds(5).toMillis(),
)
