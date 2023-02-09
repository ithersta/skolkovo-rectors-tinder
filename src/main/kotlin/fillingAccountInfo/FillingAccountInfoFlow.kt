package fillingAccountInfo

import Strings.AccountInfo.ChooseCity
import Strings.AccountInfo.ChooseProfessionalAreas
import Strings.AccountInfo.WriteName
import Strings.AccountInfo.WriteOrganization
import Strings.AccountInfo.WriteProfession
import Strings.AccountInfo.WriteProfessionalActivity
import Strings.AccountInfo.WriteProfessionalArea
import Strings.AccountInfo.professionalAreas
import Strings.FinishChoosing
import Strings.Question.ChooseQuestionArea
import Strings.questionAreaToString
import Strings.stringToQuestionArea
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
class SelectQuery(val area: String) : Query

@Serializable
@SerialName("u")
class UnselectQuery(val area: String) : Query

@Serializable
@SerialName("f")
object FinishQuery : Query

fun RoleFilterBuilder<DialogState, User, User.Unauthenticated, UserId>.fillingAccountInfoFlow() {
    state<WriteNameState> {
        onEnter {
            sendTextMessage(
                it,
                WriteName
            )
        }
        onText {
            val name = it.content.text
            state.override { ChooseCityState(name) }
        }
    }
    state<ChooseCityState> {
        onEnter {
            sendTextMessage(
                it,
                ChooseCity
                ////TODO: здесь внедрить часть Глеба с выбором города из сложного кнопочного меню
            )
        }
        onText {
            ////TODO: здесь внедрить часть Глеба с выбором города из сложного кнопочного меню
            val city =
                it.content.text///мб если хранить все города листом, то город учстника хранить не словами, а номером в листе?
            state.override { WriteProfessionState(name, city) }
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
            val profession = it.content.text//мб валидация нужна какая-нибудь?
            state.override { WriteOrganizationState(name, city, profession) }
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
            val organization = it.content.text//мб валидация нужна какая-нибудь?
            state.override { ChooseProfessionalAreasState(name, city, profession, organization, emptyList()) }
        }
    }
    state<ChooseProfessionalAreasState> {
        onEnter {
            val keyboard = inlineKeyboard {
                professionalAreas.forEach { area ->
                    row {
                        if (area in state.snapshot.professionalAreas) {
                            dataButton("✅${area}", UnselectQuery(area))
                        } else {
                            dataButton(area, SelectQuery(area))
                        }
                    }
                }
                row {
                    dataButton(FinishChoosing, FinishQuery)
                }
            }
            state.snapshot.messageId?.let { id ->
                runCatching {
                    editMessageReplyMarkup(it, id, keyboard)
                }
            } ?: run {
                val message =
                    sendTextMessage(
                        it,
                        ChooseProfessionalAreas,
                        ///!!!!обработчик "Другое" просит ввода и принимает его
                        replyMarkup = keyboard
                    )
                state.overrideQuietly {
                    ChooseProfessionalAreasState(
                        name,
                        city,
                        profession,
                        organization,
                        professionalAreas,
                        message.messageId
                    )
                }
            }
        }
        onDataCallbackQuery(SelectQuery::class) { (data, query) ->
            if (data.area.equals(professionalAreas.last())) {
                state.override {
                    ChooseProfessionalAreasState(
                        name,
                        city,
                        profession,
                        organization,
                        professionalAreas + data.area,
                        messageId
                    )
                }
            } else {
                state.override {
                    AddProfessionalAreasState(
                        name,
                        city,
                        profession,
                        organization,
                        professionalAreas + data.area,
                        messageId
                    )
                }

            }
            answer(query)
        }
        onDataCallbackQuery(UnselectQuery::class) { (data, query) ->
            state.override {
                ChooseProfessionalAreasState(
                    name,
                    city,
                    profession,
                    organization,
                    professionalAreas - data.area,
                    messageId
                )
            }
            answer(query)
        }
        onDataCallbackQuery(FinishQuery::class) { (_, query) ->
            state.override {
                WriteProfessionalDescriptionState(
                    name,
                    city,
                    profession,
                    organization,
                    professionalAreas
                )
            }
            answer(query)
        }
    }
    state<AddProfessionalAreasState> {
        onEnter {
            sendTextMessage(
                it,
                WriteProfessionalArea
            )
        }
        onText {
            val area = it.content.text
            state.override {
                ChooseProfessionalAreasState(
                    name,
                    city,
                    profession,
                    organization,
                    professionalAreas + area,
                    messageId
                )
            }
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
            val activity = it.content.text//мб валидация нужна какая-нибудь?
            state.override {
                ChooseQuestionAreasState(
                    name,
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
                        val areaToString = questionAreaToString.get(area)
                        if (area in state.snapshot.questionAreas) {

                            dataButton("✅${areaToString}", UnselectQuery(areaToString!!))
                        } else {
                            dataButton(areaToString!!, SelectQuery(areaToString))
                        }
                    }
                }
                row {
                    dataButton(FinishChoosing, FinishQuery)
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
                        name,
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
        onDataCallbackQuery(SelectQuery::class) { (data, query) ->
            state.override {
                val area = stringToQuestionArea.get(data.area)!!
                ChooseQuestionAreasState(
                    name,
                    city,
                    profession,
                    organization,
                    professionalAreas,
                    professionalDescription,
                    questionAreas + area,
                    messageId
                )
            }
            answer(query)
        }
        onDataCallbackQuery(UnselectQuery::class) { (data, query) ->
            state.override {
                val area = stringToQuestionArea.get(data.area)!!
                ChooseQuestionAreasState(
                    name,
                    city,
                    profession,
                    organization,
                    professionalAreas,
                    professionalDescription,
                    questionAreas - area,
                    messageId
                )
            }
            answer(query)
        }
        onDataCallbackQuery(FinishQuery::class) { (_, query) ->
            state.override {
                AddAccountInfoToDataBaseState(
                    name,
                    city,
                    profession,
                    organization,
                    professionalAreas,
                    professionalDescription,
                    questionAreas,
                )
            }
            answer(query)
        }
    }
    state<AddAccountInfoToDataBaseState> {
        /////добавление пользователя в базу данных
        ////добавление этому пользователю сфер
    }
}