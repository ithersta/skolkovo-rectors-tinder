package changeAccountInfo

import changeAccountInfo.telegram.queries.*
import common.telegram.Query

object Strings {
    const val ChooseFieldToChange = "Выберите какие данные вы бы хотели изменить"

    object Fields {
        object Name {
            const val Name = "фамилия и имя"
            const val Message = "Введите новые имя и фамилию"
        }

        const val City = "город"
        object Job{
            const val Name="профессия"
            const val Message="Введите новую профессию"
        }
        object Organization{
            const val Name="организация"
            const val Message="Введите новое место работы"
        }

        object ActivityDescription{
            const val Name="описание деятельности"
            const val Message="Введите новое описание деятельности"
        }

        const val Areas="сферы компетенций"
    }

    val namesToQueries= mapOf<String, Query>(
        Fields.Name.Name to WaitingForNewName,
        Fields.City to WaitingForCity,
        Fields.Job.Name to WaitingForProfession,
        Fields.Organization.Name to WaitingForOrganization,
        Fields.ActivityDescription.Name to WaitingForProfessionalDescription,
        Fields.Areas to WaitingForQuestionAreas
    )

}