package org.nixdork.klog

import io.kotest.core.config.AbstractProjectConfig
import io.kotest.core.spec.IsolationMode
import io.kotest.extensions.spring.SpringExtension

object KotestProjectConfig : AbstractProjectConfig() {
    override val parallelism = 3
    override val isolationMode = IsolationMode.InstancePerTest
    override fun extensions() = listOf(SpringExtension)
}