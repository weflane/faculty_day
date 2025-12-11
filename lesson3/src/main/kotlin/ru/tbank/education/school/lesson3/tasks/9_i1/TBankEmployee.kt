interface TBankEmployee{
    fun answerClientCall()
}

// каждый из этих классов должен реализовать ВСЕ методы интерфейса
class Developer(name: String) : TBankEmployee {
    fun writeCode()
    fun deployToProduction()
    override fun answerClientCall()
}
class SupportOperator(name: String) : TBankEmployee {
    fun writeCode()
    override fun answerClientCall()
    fun processLoanRequest()
}
class LoanManager(name: String) : TBankEmployee {
    fun deployToProduction()
    override fun answerClientCall()
    fun processLoanRequest()
}

fun main() {
    val dev = Developer("Алексей")
    val support = SupportOperator("Мария")
    val loanManager = LoanManager("Игорь")

    // В реальности:
    // - разработчик не должен рассматривать кредиты
    // - оператор поддержки не должен деплоить в прод
    // - кредитный менеджер не должен писать код, и т.д.
    dev.writeCode()
    dev.processLoanRequest()

    support.answerClientCall()
    support.deployToProduction()

    loanManager.processLoanRequest()
    loanManager.writeCode()
}
