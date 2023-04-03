package common.data.migrations

import auth.data.tables.PhoneNumbers
import auth.data.tables.UserAreas
import auth.data.tables.Users
import mute.data.tables.MuteSettings
import notifications.data.tables.NotificationPreferences
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.Transaction
import organizations.data.tables.Cities
import organizations.data.tables.Organizations
import qna.data.tables.AcceptedResponses
import qna.data.tables.QuestionAreas
import qna.data.tables.Questions
import qna.data.tables.Responses

fun Transaction.runMigrations() {
    SchemaUtils.createMissingTablesAndColumns(
        Cities,
        Organizations,
        PhoneNumbers,
        Users,
        UserAreas,
        Questions,
        QuestionAreas,
        Responses,
        AcceptedResponses,
        MuteSettings,
        NotificationPreferences
    )
    exec("ALTER TABLE QUESTIONS DROP COLUMN IF EXISTS IS_BLOCKED_CITY")
}
