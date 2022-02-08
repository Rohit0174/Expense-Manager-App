package com.example.expensemanager.data

import android.app.Application
import androidx.lifecycle.LiveData


class TransactionListRepository(context: Application){
    private val transactionListDao: TransactionListDao = TransactionDatabase.getDatabase(context).transactionListDao()

    fun getTransactions(): LiveData<List<Transaction>> {
        return transactionListDao.getTransactions()
    }
    fun getCashAmount(): LiveData<Float>{
        return transactionListDao.getCashAmount()
    }
    fun getBankAmount(): LiveData<Float>{
        return transactionListDao.getBankAmount()
    }
    fun getSumOfTransactions():LiveData<Float>{
        return transactionListDao.getSumOfTransactions()
    }
    fun getTransactionByDate(date: String): LiveData<List<Transaction>> {
        return transactionListDao.getTransactionByDate(date)
    }
    fun getUpcomingTransactions():LiveData<List<Transaction>>{
        return transactionListDao.getUpcomingTransactions()
    }
}