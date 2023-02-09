package common.data

import common.domain.Transaction
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.transactions.transaction
import org.koin.core.annotation.Single

@Single
class TransactionImpl(private val database: Database) : Transaction {
    override fun <R> invoke(action: () -> R): R {
        return transaction(database) { action() }
    }
}
