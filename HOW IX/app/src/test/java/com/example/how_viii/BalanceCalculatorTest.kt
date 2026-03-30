package com.example.how_viii

import org.junit.Assert.assertEquals
import org.junit.Test

class BalanceCalculatorTest {

    @Test
    fun calculateTotalBalance_isCorrect() {
        val transactions = listOf(
            Transaction(amount = 1000.0, description = "Salário", category = "Receita", type = "R"),
            Transaction(amount = -300.0, description = "Aluguel", category = "Moradia", type = "D"),
            Transaction(amount = 50.0, description = "Extra", category = "Receita", type = "R")
        )

        val result = BalanceCalculator.calculate(transactions)

        assertEquals(750.0, result, 0.001) 
    }

    @Test
    fun calculateTotalBalance_emptyList_returnsZero() {
        val transactions = emptyList<Transaction>()

        val result = BalanceCalculator.calculate(transactions)

        assertEquals(0.0, result, 0.001)
    }

    @Test
    fun calculateTotalBalance_onlyExpenses_returnsNegative() {
        val transactions = listOf(
            Transaction(amount = -100.0, description = "Conta", category = "Despesa", type = "D"),
            Transaction(amount = -50.0, description = "Lanche", category = "Despesa", type = "D")
        )

        val result = BalanceCalculator.calculate(transactions)

        // ASSERT
        assertEquals(-150.0, result, 0.001)
    }
}