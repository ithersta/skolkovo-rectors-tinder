package common.domain

interface Transaction {
    operator fun <R> invoke(action: () -> R): R
}
