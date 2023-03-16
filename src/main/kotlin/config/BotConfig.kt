package config

private const val DEFAULT_BACKUP_USER_ID = 105293829L
private const val DEFAULT_CURATOR_ID = 0L

data class BotConfig(
    val adminId: Long?,
    val curatorId: Long,
    val backupUserId: Long
)

fun readBotConfig() = BotConfig(
    adminId = System.getenv()["ADMIN_ID"]?.toLong(),
    curatorId = System.getenv()["CURATOR_ID"]?.toLong() ?: DEFAULT_CURATOR_ID,
    backupUserId = System.getenv()["BACKUP_USER_ID"]?.toLong() ?: DEFAULT_BACKUP_USER_ID
)
