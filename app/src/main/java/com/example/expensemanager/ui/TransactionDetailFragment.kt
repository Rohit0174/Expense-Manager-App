package com.example.expensemanager.ui

import android.app.DatePickerDialog
import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.EditText
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.expensemanager.R
import com.example.expensemanager.data.Transaction
import com.example.expensemanager.data.TransactionMode


import kotlinx.android.synthetic.main.fragment_transaction_detail.*
import kotlinx.android.synthetic.main.fragment_transaction_list.*
import java.text.SimpleDateFormat
import java.util.*


class TransactionDetailFragment : Fragment() {

    private lateinit var viewModel: TransactionDetailViewModel

    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)

        viewModel = ViewModelProvider(this)
            .get(TransactionDetailViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_transaction_detail, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        dateselect.editText?.transformIntoDatePicker(requireContext(), "yyyy-MM-dd")
        dateselect.editText?.transformIntoDatePicker(requireContext(), "yyyy-MM-dd", Date())


        val transactionModes = mutableListOf<String>()
        TransactionMode.values().forEach { transactionModes.add(it.name)}
        val arrayAdapter = ArrayAdapter(requireActivity(), android.R.layout.simple_spinner_item, transactionModes)
        transaction_mode.adapter = arrayAdapter

        transaction_mode?.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(parent: AdapterView<*>?) {

            }
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
//                updateTaskPriorityView(position)
            }
        }
        val id = TransactionDetailFragmentArgs.fromBundle(requireArguments()).id
        viewModel.setTransactionId(id)

        viewModel.transaction.observe(viewLifecycleOwner, Observer {
            it?.let{ setData(it) }
        })

        save_transaction_income.setOnClickListener {
            saveTaskIncome()
        }
        save_transaction_expense.setOnClickListener {
            saveTaskExpense()
        }



//        delete_transaction.setOnClickListener {
//            deleteTask()
//        }

        addAppBar_detail.setOnMenuItemClickListener { menuItem ->
            when(menuItem.itemId){
                R.id.delete_button-> {
                    deleteTask()
                    true
                }
                else -> false
            }
        }

    }

    private fun setData(transaction: Transaction){
        transaction_title.editText?.setText(transaction.title)
        transaction_amount.editText?.setText(transaction.amount.toString())
        transaction_mode.setSelection(transaction.transactionMode)
        dateselect.editText?.setText(transaction.date)
    }


    //saves data in the database
    private fun saveTaskIncome(){
        val title = transaction_title.editText?.text.toString()
        val amount = transaction_amount.editText?.text.toString()
        val transactionMode = transaction_mode.selectedItemPosition
        val selectDate = dateselect.editText?.text.toString()
        val transaction = Transaction(viewModel.transactionId.value!!, title, amount.toFloat(),selectDate,transactionMode,0)   //change the date here!!!!!!!!!!!
        viewModel.saveTransaction(transaction)

        requireActivity().onBackPressed()
    }
    private fun saveTaskExpense(){
        val title = transaction_title.editText?.text.toString()
        val amount = (0-transaction_amount.editText?.text.toString().toFloat()).toString()
        val transactionMode = transaction_mode.selectedItemPosition
        val selectDate = dateselect.editText?.text.toString()
        val transaction = Transaction(viewModel.transactionId.value!!, title, amount.toFloat(),selectDate,transactionMode,1)   //change the date here!!!!!!!!!!!
        viewModel.saveTransaction(transaction)

        requireActivity().onBackPressed()
    }
    private fun deleteTask(){
        viewModel.deleteTransaction()

        requireActivity().onBackPressed()
    }

    fun EditText.transformIntoDatePicker(context: Context, format: String, maxDate: Date? = null) {
        isFocusableInTouchMode = false
        isClickable = true
        isFocusable = false

        val myCalendar = Calendar.getInstance()
        val datePickerOnDataSetListener =
                DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
                    myCalendar.set(Calendar.YEAR, year)
                    myCalendar.set(Calendar.MONTH, monthOfYear)
                    myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                    val sdf = SimpleDateFormat(format, Locale.UK)
                    setText(sdf.format(myCalendar.time))
                }

        setOnClickListener {
            DatePickerDialog(
                    context, datePickerOnDataSetListener, myCalendar
                    .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                    myCalendar.get(Calendar.DAY_OF_MONTH)
            ).run {
//                maxDate?.time?.also { datePicker.maxDate = it }
                show()
            }
        }
    }

//    private fun updateTaskPriorityView(priority: Int){
//        when(priority){
//            PriorityLevel.High.ordinal ->{
//                task_priority_view.setBackgroundColor(
//                    ContextCompat.getColor(activity!!,
//                        R.color.colorPriorityHigh
//                    ))
//            }
//            PriorityLevel.Medium.ordinal ->{
//                task_priority_view.setBackgroundColor(
//                    ContextCompat.getColor(activity!!,
//                        R.color.colorPriorityMedium
//                    ))
//            }
//            else ->  task_priority_view.setBackgroundColor(
//                ContextCompat.getColor(activity!!,
//                    R.color.colorPriorityLow
//                ))
//        }
//    }
}
