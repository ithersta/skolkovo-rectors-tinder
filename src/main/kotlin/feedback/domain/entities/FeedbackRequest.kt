package feedback.domain.entities

import auth.domain.entities.PhoneNumber

class FeedbackRequest(
    val responseId: Long,
    val respondentName: String,
    val respondentPhoneNumber: PhoneNumber,
    val respondentOrganization: String,
    val questionAuthorUserId: Long
)
