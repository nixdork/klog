package org.nixdork.klog

import io.kotest.core.config.AbstractProjectConfig
import io.kotest.core.spec.IsolationMode

object KotestProjectConfig : AbstractProjectConfig() {
    override val parallelism = 3
    override val isolationMode = IsolationMode.SingleInstance
}