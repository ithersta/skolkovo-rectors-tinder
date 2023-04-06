package common.data.migrations

import auth.data.tables.UserAreas
import auth.data.tables.Users
import mute.data.tables.MuteSettings
import notifications.data.tables.NotificationPreferences
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.Transaction
import organizations.data.OrganizationFiller
import organizations.data.tables.Cities
import organizations.data.tables.Organizations
import qna.data.tables.AcceptedResponses
import qna.data.tables.QuestionAreas
import qna.data.tables.Questions
import qna.data.tables.Responses

fun Transaction.runMigrations(organizationFiller: OrganizationFiller) {
    SchemaUtils.createMissingTablesAndColumns(
        Cities,
        Organizations,
        Users,
        UserAreas,
        Questions,
        QuestionAreas,
        Responses,
        AcceptedResponses,
        MuteSettings,
        NotificationPreferences
    )
    OrganizationFiller::class.java.getResourceAsStream("/organizations.json")
        ?.let { organizationFiller.run { loadFromJson(it) } }
    exec("ALTER TABLE QUESTIONS DROP COLUMN IF EXISTS IS_BLOCKED_CITY")
}
