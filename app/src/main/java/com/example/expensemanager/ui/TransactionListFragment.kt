package com.example.expensemanager.ui

import android.app.AlertDialog
import android.content.Context
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.expensemanager.R
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import kotlinx.android.synthetic.main.fragment_transaction_list.*
import kotlinx.android.synthetic.main.set_balance_details.view.*
import org.eazegraph.lib.models.PieModel


class TransactionListFragment : Fragment() {
    //declaring the view model
    private lateinit var viewModel: TransactionListViewModel
    var cashAmount:Float = 0F
    var bankAmount:Float = 0F
    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)

        //setHasOptionsMenu(true)
        //(activity as AppCompatActivity?)!!.setSupportActionBar(addAppBar)

        viewModel = ViewModelProvider(this)
            .get(TransactionListViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_transaction_list, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        //recycler view for showing all the transactions
        with(transaction_list){
            layoutManager = LinearLayoutManager(activity)
            adapter = TransactionAdapter {
                findNavController().navigate(
                    TransactionListFragmentDirections.actionTransactionListFragmentToTransactionDetailFragment(
                        it
                    )
                )
            }
        }
        //code for the floating action button in the main screen
        add_transaction.setOnClickListener{
            findNavController().navigate(
                //here id is passed 0 because the transaction is being added the first time
                TransactionListFragmentDirections.actionTransactionListFragmentToTransactionDetailFragment(
                    0
                )
            )
        }

        addAppBar.setOnMenuItemClickListener { menuItem ->
            when(menuItem.itemId){
                R.id.calendar_button -> {
                    findNavController().navigate(TransactionListFragmentDirections.actionTransactionListFragmentToCalanderViewFragment())
                    true
                }
                R.id.monthly_cards -> {
                    findNavController().navigate(TransactionListFragmentDirections.actionTransactionListFragmentToMonthlyCardsFragment())
                    true
                }
                else -> false
            }
        }
        see_all_transactions.setOnClickListener {
            findNavController().navigate(TransactionListFragmentDirections.actionTransactionListFragmentToAllTransactionsFragment())
        }


        //submitting the new list of upcoming Transactions after getting it from the db
        viewModel.upcomingTransactions.observe(viewLifecycleOwner, Observer {
            (transaction_list.adapter as TransactionAdapter).submitList(it)
        })

        val sharedPreferences: SharedPreferences =
                this.requireActivity().getSharedPreferences("OnboardingDetails", Context.MODE_PRIVATE)

        val monthlyBudget = sharedPreferences.getFloat("monthlyBudget",0F)
        var totalBalance = monthlyBudget*12
        net_balance.text = totalBalance.toString()
        Log.d("netbalance",totalBalance.toString())
        //the net balance (yearly) is calculated wrt the transactions already done
        viewModel.sumOfTransactions.observe(viewLifecycleOwner, Observer {
            if (it != null) {
                totalBalance += it
                net_balance.text = totalBalance.toString()
            }
        })
        //setting pie chart initially to 0


        val budgetPreferences: SharedPreferences =
                this.requireActivity().getSharedPreferences("Balance_details", Context.MODE_PRIVATE)
        val editor: SharedPreferences.Editor = budgetPreferences.edit()


        setPieChart(budgetPreferences,editor)
        //observing the cash details and the bank details to update the text view and the pie chart
        observeBalance(budgetPreferences,editor)
        //GraphCardView code
        //button for setting the balance details
        set_balance_details.setOnClickListener {
            setBalanceDetails(budgetPreferences,editor)
        }
    }
    //dialog box for setting the balance details
    private fun setBalanceDetails(budgetPreferences: SharedPreferences,editor: SharedPreferences.Editor) {
        val dialog = LayoutInflater.from(requireContext()).inflate(
            R.layout.set_balance_details,
            null
        )
        //AlertDialogBuilder
        val mBuilder = AlertDialog.Builder(requireContext())
            .setView(dialog)
        //show dialog
        val  mAlertDialog = mBuilder.show()

        dialog.save_details.setOnClickListener {
            cashAmount = dialog.cash_amount.editText?.text.toString().toFloat()
            bankAmount = dialog.bank_amount.editText?.text.toString().toFloat()
            //saving the cashAmount and bankAmount to shared preferences for future use
            editor.putFloat("cashAmount", cashAmount).apply()
            editor.putFloat("bankAmount", bankAmount).apply()

            //setting the pie chart with new values
            setPieChart(budgetPreferences, editor)
            mAlertDialog.dismiss()
        }
        dialog.cancel_details.setOnClickListener { mAlertDialog.dismiss() }
        mAlertDialog.show()
    }

    private fun observeBalance(budgetPreferences: SharedPreferences,editor: SharedPreferences.Editor) {
        //getting the cashAmount and bankAmount and updating the views with live data

        var cashAmount = budgetPreferences.getFloat("cashAmount", 0F)
        var bankAmount = budgetPreferences.getFloat("bankAmount", 0F)

        viewModel.cashAmount.observe(viewLifecycleOwner, Observer {
            if (it != null) {
                cashAmount += it
                cash.text = "CASH : ${cashAmount}"
                Log.d("observeCash",cashAmount.toString())
                editor.putFloat("cashAmount",cashAmount).apply()//find solution to this
                setPieChart(budgetPreferences,editor)
            }
        })

        viewModel.bankAmount.observe(viewLifecycleOwner, Observer {
            if (it != null) {
                bankAmount+=it
                bank.text = "BANK : ${bankAmount}"
                Log.d("observeBank",bankAmount.toString())
                editor.putFloat("cashAmount",cashAmount).apply()
                setPieChart(budgetPreferences,editor)
            }
        })
        setPieChart(budgetPreferences,editor)
    }
    //https://www.geeksforgeeks.org/how-to-add-a-pie-chart-into-an-android-application/ use this for reference
    private fun setPieChart(budgetPreferences: SharedPreferences,editor: SharedPreferences.Editor) {

        val cashAmount = budgetPreferences.getFloat("cashAmount", 0f)
        val bankAmount = budgetPreferences.getFloat("bankAmount", 0f)
        Log.d("pieCank",cashAmount.toString())
        Log.d("pieBank",bankAmount.toString())
        cash.text = "CASH : ${cashAmount}"
        bank.text = "BANK : ${bankAmount}"

        val pieEntries = arrayListOf<PieEntry>()
        pieEntries.add(PieEntry(cashAmount))
        pieEntries.add(PieEntry(bankAmount))
        pieChart.animateXY(1000, 1000)

        // setup PieChart Entries Colors
        val pieDataSet = PieDataSet(pieEntries, "This is Pie Chart Label")
        pieDataSet.setColors(
            ContextCompat.getColor(requireActivity(), R.color.blue1),
            ContextCompat.getColor(requireActivity(), R.color.blue2)
        )
        val pieData = PieData(pieDataSet)

        // setip text in pieChart centre
        //piechart.setHoleColor(R.color.teal_700)
        pieChart.setHoleColor(getColorWithAlpha(Color.BLACK, 0.0f))
        // hide the piechart entries tags
        pieChart.legend.isEnabled = false
//        now hide the description of piechart
        pieChart.description.isEnabled = false
        pieChart.description.text = "Expanses"
        pieChart.holeRadius = 40f
        // this enabled the values on each pieEntry
        pieData.setDrawValues(true)
        pieChart.data = pieData
//        piechart?.addPieSlice(
//                PieModel("Cash", cashAmount, Color.parseColor("#F44336"))
//        )
//        piechart?.addPieSlice(
//                PieModel("Bank", bankAmount, Color.parseColor("#2196F3"))
//        )
//        piechart?.startAnimation()
    }
    fun getColorWithAlpha(color: Int, ratio: Float): Int {
        var newColor = 0
        val alpha = Math.round(Color.alpha(color) * ratio)
        val r = Color.red(color)
        val g = Color.green(color)
        val b = Color.blue(color)
        newColor = Color.argb(alpha, r, g, b)
        return newColor
    }
}
