package qna.telegram.flows

import auth.domain.entities.User
import generated.RoleFilterBuilder

fun RoleFilterBuilder<User.Normal>.newResponseFlow() {
    anyState { }
}
