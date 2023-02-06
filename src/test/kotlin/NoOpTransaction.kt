import common.domain.Transaction

object NoOpTransaction : Transaction {
    override fun <R> invoke(action: () -> R): R {
        return action()
    }
}
