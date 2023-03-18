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
            val ans = XSSFWorkbook(inputStream).use { workbook ->
                workbook.getSheetAt(0)
                    .drop(1)
                    .map {
                        val cell = it.getCell(0)
                        when (cell.cellType) {
                            CellType.NUMERIC -> cell.numericCellValue.toULong().toString()
                            CellType.STRING -> cell.stringCellValue
                            else -> error("wrong cell type")
                        }
                    }
                    .dropLastWhile { it.isNullOrBlank() }
                    .map {
                        PhoneNumber.of(it.removePrefix("+"))
                    }.let {
                        it to it.mapIndexedNotNull { index, e -> (index + 2).takeIf { e == null } }
                    }
            }
            return if (ans.second.isEmpty()) {
                Result.OK(ans.first.filterNotNull())
            } else {
                Result.BadFormat(ans.second)
            }
        }.getOrElse {
            Result.InvalidFile()
        }
}
