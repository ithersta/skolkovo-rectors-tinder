package config

import java.io.File

fun readToken() = System.getenv()["TOKEN"] ?: File(System.getenv("TOKEN_FILE")).readText()
