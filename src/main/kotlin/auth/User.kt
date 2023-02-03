package auth

sealed interface User {
    object Unauthenticated : User
    object Normal : User
    object Admin : User
}
