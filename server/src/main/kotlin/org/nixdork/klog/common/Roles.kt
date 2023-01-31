package org.nixdork.klog.common

const val ROLE_CONTRIBUTOR = "ROLE_CONTRIBUTOR"
const val ROLE_ADMIN = "ROLE_ADMIN"

/**
 * Role definitions for Role-based access control (RBAC)
 */
enum class Roles(val role: String, val description: String) {
    CONTRIBUTOR(ROLE_CONTRIBUTOR, description = "Contributor: Controls own entries + profile"),
    ADMIN(ROLE_ADMIN, description = "Admin: Full control"),
}

fun Roles.shortName(): String = role.removePrefix("ROLE_")
