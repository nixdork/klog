package org.nixdork.klog.frameworks.data.config

import mu.KotlinLogging
import org.flywaydb.core.Flyway
import org.flywaydb.core.api.MigrationInfo
import org.nixdork.klog.frameworks.data.config.properties.DatabaseProperties
import org.springframework.boot.autoconfigure.flyway.FlywayMigrationInitializer
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import javax.sql.DataSource

private const val TAG = "FlywayConfiguration"
private val log = KotlinLogging.logger(TAG)

@Configuration
class FlywayConfiguration(private val dataSource: DataSource) {
    init {
        log.info { "Wiring up Flyway" }
    }

    private fun MigrationInfo.statusIndication() = if (installedOn == null) "+" else "✓"

    @Bean("flyway")
    fun flyway(properties: DatabaseProperties): Flyway =
        Flyway.configure().dataSource(dataSource).schemas(properties.schema).load()

    @Bean("flywayInitializer")
    fun flywayInitializer(flyway: Flyway): FlywayMigrationInitializer =
        FlywayMigrationInitializer(flyway).apply {
            flyway.info().all().forEach {
                log.info { "Flyway Migration: ${it.statusIndication()} ${it.type} ${it.script}" }
            }
        }
}
