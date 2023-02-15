package qna.flows

import auth.domain.entities.User
import com.ithersta.tgbotapi.fsm.builders.RoleFilterBuilder
import com.ithersta.tgbotapi.fsm.entities.triggers.onEnter
import com.ithersta.tgbotapi.fsm.entities.triggers.onText
import common.telegram.DialogState
import dev.inmo.tgbotapi.types.UserId
import menus.states.MenuState

fun RoleFilterBuilder<DialogState, User, User.Normal, UserId>.askQuestionFlow() {
    state<MenuState.Questions.AskQuestion>{
        onEnter{
             //сформулируйте свой вопрос
        }
        onText {
            //перехожу в след state, который принимает string
        }
    }
    state<>{
        onEnter{
            //множественный выбор из областей (inline кнопки)
        }
        onText {
            //перехожу в след state, который принимает + list<Int>(?) (так как enum)
        }
    }
    state<>{
        onEnter{
            //цель вопроса (3 кнопки)
        }
        onText {
            //перехожу в след state, который принимает + list<Int>(?) (так как enum)
        }
    }
    state<> {
        onEnter {
            //кнопка отправить запрос в сообщество
        }
        onText{
            //добавление вопроса в бд
            //сообщение о том, что вопрос успешно отправлен
            //отправка вопроса в сообщество
        }
    }
}