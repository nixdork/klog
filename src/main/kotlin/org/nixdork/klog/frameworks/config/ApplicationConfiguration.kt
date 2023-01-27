package org.nixdork.klog.frameworks.config

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.MapperFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.PropertyNamingStrategies
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.databind.json.JsonMapper
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.kotlinModule
import org.nixdork.klog.common.toUUID
import org.nixdork.klog.frameworks.config.properties.KlogProperties
import org.nixdork.klog.frameworks.config.properties.ServiceProperties
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
@EnableConfigurationProperties(value = [ServiceProperties::class, KlogProperties::class])
class ApplicationConfiguration(
    private val serviceProperties: ServiceProperties,
    private val klogProperties: KlogProperties,
) {
    @Bean("serviceConfiguration")
    fun serviceConfiguration(): ServiceConfiguration =
        ServiceConfiguration(
            name = serviceProperties.name,
            environment = serviceProperties.environment,
        )

    @Bean("objectMapper")
    fun objectMapper(): ObjectMapper =
        JsonMapper.builder()
            .addModule(kotlinModule())
            .addModule(JavaTimeModule())
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
            .configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_ENUMS, true)
            .propertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE)
            .serializationInclusion(JsonInclude.Include.NON_NULL)
            .serializationInclusion(JsonInclude.Include.NON_EMPTY)
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
            .build()

    @Bean("klogConfiguration")
    fun klogConfiguration(): KlogConfiguration =
        KlogConfiguration(
            title = klogProperties.title,
            subtitle = klogProperties.subtitle,
            domain = klogProperties.domain,
            primaryAuthor = klogProperties.primaryAuthor.toUUID(),
            generator = klogProperties.generator,
            icon = klogProperties.icon,
            logo = klogProperties.logo,
            style = klogProperties.style,
            rights = klogProperties.rights,
            homeEntries = klogProperties.homeEntries,
            passwordAge = klogProperties.passwordAge,
        )
}
