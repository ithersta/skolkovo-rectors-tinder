package auth

import org.koin.core.annotation.Single

@Single
class GetUserUseCase {
    operator fun invoke(id: Long): User {
        return User.Normal // TODO: Replace with actual implementation
    }
}
