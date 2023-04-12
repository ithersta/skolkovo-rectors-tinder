package changeinfo.telegram

import changeinfo.telegram.queries.*
import common.telegram.Query

object Strings {
    const val ChooseFieldToChange = "Выберите, какие данные вы бы хотели изменить"

    object Fields {
        object Name {
            const val Button = "фамилия и имя"
            const val Message = "Введите новые имя и фамилию"
        }

        object City {
            const val Button = "город"
            const val OrganizationQuestion = "Изменилось ли ваше место работы?"
        }

        object Job {
            const val Button = "профессия"
            const val Message = "Введите новую профессию"
        }

        object Organization {
            const val Button = "организация"
            const val Type = "Выберите тип организации – нового места работы"
            const val SorryMessage = "Внимание! " +
                "\nЕсли у вас изменился также и город работы, сначала необходимо изменить его."
        }

        object ActivityDescription {
            const val Button = "описание деятельности"
            const val Message = "Введите новое описание деятельности"
        }

        const val Areas = "сферы компетенций"
    }

    private const val Back = "Не нужно изменять данные"

    val namesToQueries = mapOf<String, Query>(
        Fields.Name.Button to WaitingForNewName,
        Fields.City.Button to WaitingForCity,
        Fields.Job.Button to WaitingForProfession,
        Fields.Organization.Button to WaitingForOrganization,
        Fields.ActivityDescription.Button to WaitingForProfessionalDescription,
        Fields.Areas to WaitingForQuestionAreas,
        Back to BackToMain
    )
}
