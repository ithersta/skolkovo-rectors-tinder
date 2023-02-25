package qna.flows

import auth.domain.entities.User
import com.ithersta.tgbotapi.fsm.builders.RoleFilterBuilder
import common.telegram.DialogState
import dev.inmo.tgbotapi.types.UserId
import menus.states.MenuState

fun RoleFilterBuilder<DialogState, User, User.Normal, UserId>.getListOfRespondentNoAnswerFlow() {
    //сначала список всех тем вопросов по сферам пользователя(пагинация)
    //потом при нажатии на тему вопроса список всех пользователей(пока что имён), которые хотят ответить на вопрос(при этом они не приняты/отклонены)(пагинация)
    //при нажатии на имя пользователя выводить ?? (сообщение с его инфой из профиля и кнопки да/нет ???)
    state<MenuState.GetListOfRespondents>{

    }
}
