package admin

import auth.domain.entities.PhoneNumber

object Strings {
    const val WaitDocument = "��������� ������ � ���������� �������� ����������"
    const val TemplateFileName = "������"
    const val InvalidFile = "���� ��������� ��� �� �������� .xlsx ��������"
    const val addingUsers = "��� ������������� ���� ���������"
    fun blockedUsers(blocked: List<PhoneNumber>) = "�� �������� �������� ��������������� �������������: " +
        blocked.joinToString(separator = ", ") { it.value }

    fun badFormat(errors: List<Int>) =
        "������������ ������ � ${errors.size} ${pluralize(errors.size, "�������", "��������", "��������")} �������:" +
            errors.joinToString(separator = ", ") { it.toString() }

    fun pluralize(count: Int, one: String, few: String, many: String) = when {
        count % 10 == 1 && count % 100 != 11 -> one
        count % 10 in 2..4 && count % 100 !in 12..14 -> few
        else -> many
    }
}
