package admin.telegram

import admin.domain.usecases.AddPhoneNumbersUseCase
import admin.parsers.Xlsx
import auth.domain.entities.User
import com.ithersta.tgbotapi.fsm.builders.StateMachineBuilder
import com.ithersta.tgbotapi.fsm.entities.triggers.onDocument
import com.ithersta.tgbotapi.fsm.entities.triggers.onEnter
import common.telegram.DialogState
import dev.inmo.tgbotapi.extensions.api.files.downloadFile
import dev.inmo.tgbotapi.extensions.api.send.media.sendDocument
import dev.inmo.tgbotapi.extensions.api.send.sendTextMessage
import dev.inmo.tgbotapi.requests.abstracts.InputFile
import dev.inmo.tgbotapi.types.UserId
import dev.inmo.tgbotapi.types.buttons.ReplyKeyboardRemove
import io.ktor.utils.io.streams.*
import menus.states.MenuState
import org.koin.core.component.inject

fun StateMachineBuilder<DialogState, User, UserId>.addUsersFlow() {
    val phoneNumbersAddAllUseCase: AddPhoneNumbersUseCase by inject()
    role<User.Admin> {
        state<MenuState.AddUser> {
            onEnter { user ->
                val document = InputFile.fromInput(
                    "${Strings.TemplateFileName}.xlsx"
                ) { this::class.java.getResourceAsStream("/phoneNumberExample.xlsx")!!.asInput() }
                sendDocument(
                    chatId = user,
                    text = Strings.WaitDocument,
                    document = document,
                    replyMarkup = ReplyKeyboardRemove()
                )
            }
            onDocument { message ->
                downloadFile(message.content.media).inputStream().use { inputStream ->
                    when (val phoneNumbers = Xlsx.getPhoneNumbersFromXLSX(inputStream)) {
                        is Xlsx.Result.OK -> {
                            when (val result = phoneNumbersAddAllUseCase(phoneNumbers.value)) {
                                is AddPhoneNumbersUseCase.Result.OK -> {
                                    sendTextMessage(
                                        message.chat,
                                        Strings.AddingUsers
                                    )
                                    state.override { DialogState.Empty }
                                }

                                is AddPhoneNumbersUseCase.Result.PhoneNumberNotAllowed -> {
                                    sendTextMessage(
                                        message.chat,
                                        Strings.blockedUsers(result.listOfNotActive)
                                    )
                                }
                            }
                        }

                        is Xlsx.Result.BadFormat -> sendTextMessage(
                            message.chat,
                            Strings.badFormat(phoneNumbers.errors)
                        )

                        is Xlsx.Result.InvalidFile -> sendTextMessage(
                            message.chat,
                            Strings.InvalidFile
                        )
                    }
                }
            }
        }
    }
}
