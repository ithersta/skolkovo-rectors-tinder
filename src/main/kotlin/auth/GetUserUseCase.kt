package auth

import auth.domain.entities.User
import org.koin.core.annotation.Single

@Single
class GetUserUseCase {
    operator fun invoke(id: Long): User {
        return User.Unauthenticated // TODO: Replace with actual implementation
    }
}
