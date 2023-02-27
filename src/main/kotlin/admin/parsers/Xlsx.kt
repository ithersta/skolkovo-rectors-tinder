package admin.parsers

import auth.domain.entities.PhoneNumber
import org.apache.poi.ss.usermodel.CellType
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import java.io.InputStream

object Xlsx {
    sealed interface Result<T> {
        class OK<T>(val value: T) : Result<T>
        class BadFormat<T>(val errors: List<Int>) : Result<T>
        class InvalidFile<T> : Result<T>
    }

    fun getPhoneNumbersFromXLSX(inputStream: InputStream): Result<Collection<PhoneNumber>> =
        runCatching {
            val workbook = XSSFWorkbook(inputStream)
            val ans = workbook.getSheetAt(0)
                .drop(1)
                .map {
                    when (it.getCell(0).cellType) {
                        CellType.NUMERIC -> it.getCell(0).numericCellValue.toString()
                        CellType.STRING -> it.getCell(0).stringCellValue
                        else -> error("wrong cell type")
                    }
                }
                .dropLastWhile { it == null }
                .map {
                    runCatching {
                        val phoneNumber = PhoneNumber.of(it.removePrefix("+"))!!
                        phoneNumber
                    }.getOrNull()
                }.let {
                    it to it.mapIndexedNotNull { index, e -> (index + 2).takeIf { e == null } }
                }
            return if (ans.second.isEmpty()) {
                Result.OK(ans.first.filterNotNull())
            } else {
                Result.BadFormat(ans.second)
            }
        }.getOrElse {
            println(it)
            Result.InvalidFile()
        }
}
