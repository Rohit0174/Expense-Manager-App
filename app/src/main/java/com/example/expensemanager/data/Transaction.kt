package com.example.expensemanager.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

enum class TransactionMode{
    CASH,BANK
}
@Entity(tableName = "Transaction")
data class Transaction(@PrimaryKey(autoGenerate = true) val id: Long,
                val title: String,
                val amount: Float,
                val date:String,
                val transactionMode: Int,
                val transactionType:Int
                )
