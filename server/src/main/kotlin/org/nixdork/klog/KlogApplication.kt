package org.nixdork.klog

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration
import org.springframework.boot.runApplication
import java.util.TimeZone

val DefaultTimeZone = TimeZone.getTimeZone("UTC")!!

@SpringBootApplication(
    scanBasePackages = ["org.nixdork.klog"],
    exclude = [DataSourceAutoConfiguration::class],
)
class KlogApplication

@Suppress("SpreadOperator")
fun main(args: Array<String>) {
    TimeZone.setDefault(DefaultTimeZone)
    runApplication<KlogApplication>(*args)
}
