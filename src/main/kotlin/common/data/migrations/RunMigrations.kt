package common.data.migrations

import auth.data.tables.PhoneNumbers
import auth.data.tables.UserAreas
import auth.data.tables.Users
import locations.data.tables.LocationTable
import mute.data.tables.MuteSettings
import notifications.data.tables.NotificationPreferences
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.Transaction
import qna.data.tables.AcceptedResponses
import qna.data.tables.QuestionAreas
import qna.data.tables.Questions
import qna.data.tables.Responses

fun Transaction.runMigrations() {
    SchemaUtils.createMissingTablesAndColumns(
        LocationTable.Countries,
        LocationTable.Districts,
        LocationTable.Regions,
        LocationTable.Cities,
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
