package org.nixdork.klog

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class KlogApplication

fun main(args: Array<String>) {
    runApplication<KlogApplication>(*args)
}
