package config

data class BotConfig(
    val adminId: Long?,
    val backupUserId: Long
)

fun readBotConfig() = BotConfig(
    adminId = System.getenv()["ADMIN_ID"]?.toLong(),
    backupUserId = System.getenv()["BACKUP_USER_ID"]?.toLong() ?: 105293829L
)
