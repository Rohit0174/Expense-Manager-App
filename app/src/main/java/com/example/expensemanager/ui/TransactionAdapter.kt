package com.example.expensemanager.ui
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat

import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.expensemanager.R
import com.example.expensemanager.data.Transaction
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.list_item.*


class TransactionAdapter(private val listener: (Long) -> Unit):
    ListAdapter<Transaction, TransactionAdapter.ViewHolder>(
        DiffCallback()
    ){

    override fun onCreateViewHolder(parent: ViewGroup,
                                    viewType: Int): ViewHolder {
        val itemLayout = LayoutInflater.from(parent.context)
            .inflate(R.layout.list_item, parent, false)

        return ViewHolder(itemLayout)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ViewHolder (override val containerView: View) : RecyclerView.ViewHolder(containerView),
        LayoutContainer {
        init{
            itemView.setOnClickListener{
                listener.invoke(getItem(adapterPosition).id)
            }
        }

        fun bind(transaction: Transaction){
            transaction_title.text = transaction.title
            transaction_amount.text = transaction.amount.toString()
            transaction_date.text = transaction.date
            when(transaction.transactionType){
                0-> {
                    transaction_amount.setTextColor(ContextCompat.getColor(containerView.context,R.color.colorIncome))
                    type_view.setBackgroundColor(ContextCompat.getColor(containerView.context,R.color.colorIncome))
                    rupees.setTextColor(ContextCompat.getColor(containerView.context,R.color.colorIncome))
                }
                1-> {
                    transaction_amount.setTextColor(ContextCompat.getColor(containerView.context,R.color.colorExpense))
                    type_view.setBackgroundColor(ContextCompat.getColor(containerView.context,R.color.colorExpense))
                    rupees.setTextColor(ContextCompat.getColor(containerView.context,R.color.colorExpense))
                }
            }
            when(transaction.transactionMode){
                0 -> {
                    transaction_mode.text = "Cash"
                }
                1 -> {
                    transaction_mode.text = "Bank"
                }
            }
        }
    }
}

class DiffCallback : DiffUtil.ItemCallback<Transaction>() {
    override fun areItemsTheSame(oldItem: Transaction, newItem: Transaction): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Transaction, newItem: Transaction): Boolean {
        return oldItem == newItem
    }
}