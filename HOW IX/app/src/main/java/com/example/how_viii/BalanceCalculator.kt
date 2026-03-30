package com.example.how_viii

object BalanceCalculator {
    fun calculate(transactions: List<Transaction>): Double {
        return transactions.sumOf { it.amount }
    }
}