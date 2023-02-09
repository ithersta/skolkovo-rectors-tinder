package fillingAccountInfo

import Strings.AccountInfo.ChooseCity
import Strings.AccountInfo.ChooseProfessionalAreas
import Strings.AccountInfo.WriteOrganization
import Strings.AccountInfo.WriteProfession
import Strings.AccountInfo.WriteProfessionalActivity
import Strings.Question.ChooseQuestionArea
import auth.User
import com.ithersta.tgbotapi.fsm.builders.RoleFilterBuilder
import com.ithersta.tgbotapi.fsm.entities.triggers.dataButton
import com.ithersta.tgbotapi.fsm.entities.triggers.onDataCallbackQuery
import com.ithersta.tgbotapi.fsm.entities.triggers.onEnter
import com.ithersta.tgbotapi.fsm.entities.triggers.onText
import dev.inmo.tgbotapi.extensions.api.answers.answer
import dev.inmo.tgbotapi.extensions.api.edit.reply_markup.editMessageReplyMarkup
import dev.inmo.tgbotapi.extensions.api.send.sendTextMessage
import dev.inmo.tgbotapi.extensions.utils.types.buttons.inlineKeyboard
import dev.inmo.tgbotapi.types.UserId
import dev.inmo.tgbotapi.utils.row
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import states.*

@Serializable
enum class QuestionArea {
    Science, Education, Innovations, Entrepreneurship, Finances, Youngsters, Staff, Campus, Society, Strategy, Others
}

@Serializable
sealed interface Query

@Serializable
@SerialName("s")
class SelectQuery(val area: QuestionArea) : Query

@Serializable
@SerialName("u")
class UnselectQuery(val area: QuestionArea) : Query

private var questionAreaToString = mapOf<QuestionArea, String>(
    QuestionArea.Science to Strings.Question.Science,
    QuestionArea.Education to Strings.Question.Education,
    QuestionArea.Innovations to Strings.Question.Innovations,
    QuestionArea.Entrepreneurship to Strings.Question.Entrepreneurship,
    QuestionArea.Finances to Strings.Question.Finances,
    QuestionArea.Youngsters to Strings.Question.Youngsters,
    QuestionArea.Staff to Strings.Question.Staff,
    QuestionArea.Campus to Strings.Question.Campus,
    QuestionArea.Society to Strings.Question.Society,
    QuestionArea.Strategy to Strings.Question.Strategy,
    QuestionArea.Others to Strings.Question.Others,
)

fun RoleFilterBuilder<DialogState, User, User.Unauthenticated, UserId>.fillingAccountInfoFlow() {
    state<ChooseCityState> {
        onEnter {
            sendTextMessage(
                it,
                ChooseCity
                // //TODO: здесь внедрить часть Глеба с выбором города из сложного кнопочного меню
            )
        }
        onText {
            // //TODO: здесь внедрить часть Глеба с выбором города из сложного кнопочного меню
            val city =
                it.content.text // /мб если хранить все города листом, то город учстника хранить не словами, а номером в листе?
            state.override { WriteProfessionState(city) }
        }
    }
    state<WriteProfessionState> {
        onEnter {
            sendTextMessage(
                it,
                WriteProfession
            )
        }
        onText {
            val profession = it.content.text // мб валидация нужна какая-нибудь?
            state.override { WriteOrganizationState(city, profession) }
        }
    }
    state<WriteOrganizationState> {
        onEnter {
            sendTextMessage(
                it,
                WriteOrganization
            )
        }
        onText {
            val organization = it.content.text // мб валидация нужна какая-нибудь?
            state.override { ChooseProfessionalAreasState(city, profession, organization, emptyList()) }
        }
    }
    state<ChooseProfessionalAreasState> {
        onEnter {
            sendTextMessage(
                it,
                ChooseProfessionalAreas
                // //TODO: где-то тут видимо прикол с множественным выбором и видимо обработчиками
                // /!!!!обработчик "Другое" просит ввода и принимает его
            )
            // //надо бы и приколы из другого отображать так же
            // //после того, как все нужные сферы выбраны, переходим в следующее состояние
            state.override {
                WriteProfessionalDescriptionState(
                    city,
                    profession,
                    organization,
                    emptyList()
                )
            } // пока пусть эмпти будет
        }
    }
    state<WriteProfessionalDescriptionState> {
        onEnter {
            sendTextMessage(
                it,
                WriteProfessionalActivity
            )
        }
        onText {
            val activity = it.content.text // мб валидация нужна какая-нибудь?
            state.override {
                ChooseQuestionAreasState(
                    city,
                    profession,
                    organization,
                    professionalAreas,
                    activity,
                    emptyList()
                )
            }
        }
    }
    state<ChooseQuestionAreasState> {
        onEnter {
            val keyboard = inlineKeyboard {
                QuestionArea.values().forEach { area ->
                    row {
                        if (area in state.snapshot.questionAreas) {
                            dataButton("✅${questionAreaToString.get(area)}", UnselectQuery(area))
                        } else {
                            dataButton(questionAreaToString.get(area)!!, SelectQuery(area))
                        }
                    }
                }
            }
            state.snapshot.messageId?.let { id ->
                runCatching {
                    editMessageReplyMarkup(it, id, keyboard)
                }
            } ?: run {
                val message = sendTextMessage(
                    it,
                    ChooseQuestionArea,
                    replyMarkup = keyboard
                )
                state.overrideQuietly {
                    ChooseQuestionAreasState(
                        city,
                        profession,
                        organization,
                        professionalAreas,
                        professionalDescription,
                        questionAreas,
                        message.messageId
                    )
                }
            }
        }
        // /создание пользователя в базе данных
        // /ну я думаю, обработчик будет собирать все номера в лист, а потом создавать в базе им штучки
        onDataCallbackQuery(SelectQuery::class) { (data, query) ->
            state.override {
                ChooseQuestionAreasState(
                    city,
                    profession,
                    organization,
                    professionalAreas,
                    professionalDescription,
                    questionAreas + data.area,
                    messageId
                )
            }
            answer(query)
        }
        onDataCallbackQuery(UnselectQuery::class) { (data, query) ->
            state.override {
                ChooseQuestionAreasState(
                    city,
                    profession,
                    organization,
                    professionalAreas,
                    professionalDescription,
                    questionAreas - data.area,
                    messageId
                )
            }
            answer(query)
        }
    }
}
