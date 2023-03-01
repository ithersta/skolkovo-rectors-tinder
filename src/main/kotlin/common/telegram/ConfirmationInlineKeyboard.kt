package common.telegram

import dev.inmo.tgbotapi.extensions.utils.types.buttons.flatInlineKeyboard
import generated.dataButton

fun confirmationInlineKeyboard(
    positiveData: Query,
    negativeData: Query,
    positiveText: String = CommonStrings.Button.Yes,
    negativeText: String = CommonStrings.Button.No
) = flatInlineKeyboard {
    dataButton(negativeText, negativeData)
    dataButton(positiveText, positiveData)
}
