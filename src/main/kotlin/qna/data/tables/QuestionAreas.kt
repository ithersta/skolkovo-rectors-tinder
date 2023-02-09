package qna.data.tables

import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.Table
import qna.domain.entities.QuestionArea

object QuestionAreas : Table() {
    val questionId: Column<EntityID<Long>> = reference("question_id", Questions)
    val area: Column<QuestionArea> = enumeration<QuestionArea>("area").index()
    override val primaryKey = PrimaryKey(questionId, area)
}
