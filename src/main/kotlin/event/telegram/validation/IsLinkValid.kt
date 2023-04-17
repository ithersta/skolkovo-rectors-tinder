package event.telegram.validation

object IsLinkValid {
    operator fun invoke(link: String): Boolean {
        val regex = Regex(pattern = "^(https?:\\/\\/)?([\\w-]{1,32}\\.[\\w-]{1,32})[^\\s@]*\$")
        return regex.matches(input = link)
    }
}
