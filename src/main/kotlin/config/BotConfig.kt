package config

data class BotConfig(
    val adminId: Long?
)

fun readBotConfig() = BotConfig(
    adminId = System.getenv()["ADMIN_ID"]?.toLong()
)
