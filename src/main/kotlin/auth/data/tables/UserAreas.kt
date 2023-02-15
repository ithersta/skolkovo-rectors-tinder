package auth.data.tables

import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.Table
import qna.domain.entities.QuestionArea

object UserAreas : Table() {
    val userId: Column<EntityID<Long>> = reference("user_id", Users)
    val area: Column<QuestionArea> = enumeration<QuestionArea>("area").index()
    override val primaryKey = PrimaryKey(userId, area)
}
