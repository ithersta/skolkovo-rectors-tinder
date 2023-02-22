package config

private const val DEFAULT_BACKUP_USER_ID = 105293829L

data class BotConfig(
    val adminId: Long?,
    val backupUserId: Long
)

fun readBotConfig() = BotConfig(
    adminId = System.getenv()["ADMIN_ID"]?.toLong(),
    backupUserId = System.getenv()["BACKUP_USER_ID"]?.toLong() ?: DEFAULT_BACKUP_USER_ID
)
