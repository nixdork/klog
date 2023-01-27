package org.nixdork.klog.frameworks.data.config

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import org.nixdork.klog.common.DATASOURCE_CONNECTION_TIMEOUT
import org.nixdork.klog.common.DATASOURCE_KEEP_ALIVE_TIME
import org.nixdork.klog.common.DATASOURCE_LEAK_DETECTION_THRESHOLD
import org.nixdork.klog.common.DATASOURCE_MAX_LIFETIME
import org.nixdork.klog.common.DATASOURCE_MAX_POOL_SIZE
import org.nixdork.klog.common.DATASOURCE_VALIDATION_TIMEOUT
import java.time.Duration

@Suppress("LongParameterList")
class ShutdownHookHikariDataSource(
    private val shutdownHooks: List<Runnable>,
    config: HikariConfig,
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
            connectionTimeout: Long = Duration.ofSeconds(DATASOURCE_CONNECTION_TIMEOUT).toMillis(),
            keepaliveTime: Long = Duration.ofSeconds(DATASOURCE_KEEP_ALIVE_TIME).toMillis(),
            maxLifetime: Long = Duration.ofMinutes(DATASOURCE_MAX_LIFETIME).toMillis(),
            maximumPoolSize: Int = 10,
            leakDetectionThreshold: Long = Duration.ofSeconds(DATASOURCE_LEAK_DETECTION_THRESHOLD).toMillis(),
            validationTimeout: Long = Duration.ofSeconds(DATASOURCE_VALIDATION_TIMEOUT).toMillis(),
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
                validationTimeout,
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
    val connectionTimeout: Long = Duration.ofSeconds(DATASOURCE_CONNECTION_TIMEOUT).toMillis(),
    val keepaliveTime: Long = Duration.ofSeconds(DATASOURCE_KEEP_ALIVE_TIME).toMillis(),
    val maxLifetime: Long = Duration.ofMinutes(DATASOURCE_MAX_LIFETIME).toMillis(),
    val maximumPoolSize: Int = DATASOURCE_MAX_POOL_SIZE,
    val leakDetectionThreshold: Long = Duration.ofSeconds(DATASOURCE_LEAK_DETECTION_THRESHOLD).toMillis(),
    val validationTimeout: Long = Duration.ofSeconds(DATASOURCE_VALIDATION_TIMEOUT).toMillis(),
)
