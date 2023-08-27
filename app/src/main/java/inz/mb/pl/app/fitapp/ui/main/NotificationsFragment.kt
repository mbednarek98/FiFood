package inz.mb.pl.app.fitapp.ui.main

import android.app.ProgressDialog
import android.content.Context
import android.graphics.*
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import inz.mb.pl.app.fitapp.adapter.CalendarAdapter
import inz.mb.pl.app.fitapp.databinding.FragmentNotificationsBinding
import inz.mb.pl.app.fitapp.parent.CalendarFragment
import inz.mb.pl.app.fitapp.helper.Helper
import inz.mb.pl.app.fitapp.MainActivity
import androidx.recyclerview.widget.LinearLayoutManager
import inz.mb.pl.app.fitapp.adapter.dish_adapters.DishAdapter
import android.os.AsyncTask
import android.util.Log
import android.widget.TextView
import androidx.appcompat.widget.AppCompatSpinner
import com.google.common.reflect.TypeToken
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.net.URL
import org.json.JSONObject
import java.lang.Exception
import java.net.URLConnection
import com.google.gson.Gson
import inz.mb.pl.app.fitapp.R
import inz.mb.pl.app.fitapp.database.dish.DishDTO
import inz.mb.pl.app.fitapp.interfaces.CalorieCalcInterface
import inz.mb.pl.app.fitapp.interfaces.DialogInterface
import inz.mb.pl.app.fitapp.interfaces.QueryInterface
import inz.mb.pl.app.fitapp.interfaces.SpinnerInterface
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch


class NotificationsFragment : CalendarFragment(), QueryInterface, CalorieCalcInterface, DialogInterface, SpinnerInterface {
    private var _binding: FragmentNotificationsBinding? = null
    private lateinit var mainActivity: MainActivity
    private val binding get() = _binding!!
    private val job = SupervisorJob()
    private val ioScope by lazy { CoroutineScope(job + Dispatchers.IO) }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        auth = Firebase.auth
        mainActivity = activity as MainActivity
        _binding = FragmentNotificationsBinding.inflate(inflater, container, false)
        setupCreateView()
        toolBarSetup(binding.toolbar)
        return binding.root
    }


    override fun buttonSetUp(){
        with(binding){
            leftButton.setOnClickListener { previousWeekAction() }
            rightButton.setOnClickListener { nextWeekAction() }
            autoAssignDietButton.setOnClickListener { setGetAPIDialog()}
        }
    }

    private fun setupDishesRecycleView(){
        val layoutManager = LinearLayoutManager(requireContext())
        val itemAdapter = DishAdapter(requireContext(),Helper.selectedDate, mainActivity.user.dishesCount, mainActivity)
        binding.dishesRecyclerView.adapter = itemAdapter
        binding.dishesRecyclerView.layoutManager = layoutManager
    }

    override fun setWeekView() {
        monthYearText?.text = monthYearFromDate(Helper.selectedDate)
        calendarRecyclerView?.layoutManager = GridLayoutManager(this.requireContext(), 7)
        calendarRecyclerView?.adapter = CalendarAdapter(daysInWeekArray(Helper.selectedDate), this)
        setupDishesRecycleView()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onResume() {
        super.onResume()
        setupDishesRecycleView()
    }


     override fun initWidgets() {
        calendarRecyclerView = binding.calendarRecyclerView
        monthYearText = binding.monthYearTV
    }



    inner class JsonTask : AsyncTask<String?, Void?, JSONObject?>() {
        private var pd : ProgressDialog = ProgressDialog(this@NotificationsFragment.requireContext())
        override fun onPreExecute() {
            super.onPreExecute()
            pd.setMessage(resources.getString(R.string.please_wait))
            pd.show()
        }


        override fun doInBackground(vararg params: String?): JSONObject? {
            val urlConn: URLConnection?
            var bufferedReader: BufferedReader? = null
            return try {
                val url = URL(params[0])
                urlConn = url.openConnection()
                bufferedReader = BufferedReader(InputStreamReader(urlConn.getInputStream()))
                val stringBuffer = StringBuffer()
                var line: String?
                while (bufferedReader.readLine().also { line = it } != null) { stringBuffer.append(line) }
                JSONObject(stringBuffer.toString())
            } catch (ex: Exception) {
                null
            } finally {
                when {
                    bufferedReader != null -> {
                        try { bufferedReader.close() } catch (e: IOException) { e.printStackTrace() }
                    }
                }
            }
        }


        override fun onPostExecute(data: JSONObject?) {
            if (data != null) {
                val type = object : TypeToken<Map<String?, List<DishDTO>?>?>() {}.type
                val map : Map<String, List<DishDTO>> = Gson().fromJson(data.toString(), type)
                Log.e("niedziala",map.toString())
                val list = ArrayList<DishDTO>()
                map.forEach { entry ->
                    entry.value.forEach { it.date = Helper.selectedDate }
                    list.addAll(entry.value)
                }
                var listOfIds = ArrayList<Long>()
                ioScope.launch {
                    listOfIds = insertAllDish(list) as ArrayList<Long>
                    for((index,value) in listOfIds.withIndex()){
                        list[index].id = value.toInt()
                    }
                    Helper.dishDTOList.addAll(list)
                }
                pd.dismiss()
                setupDishesRecycleView()
            }
        }
    }

    private fun setGetAPIDialog() {
        val builder = createNewAlertDialog(resources.getString(R.string.auto_assign))
        val inflater = this.layoutInflater
        val dialogView = inflater.inflate(R.layout.change_two_spinner_dialog, null)
        val holder = TwoSpinnerViewHolder(dialogView)
        with(holder){
            firstTextView.text = resources.getString(R.string.accessibility)
            assignNewSpinnerAdapter(fistSpinner, R.array.accessibility_array, R.array.accessibility_desc_array, 0)
            secondTextView.text = resources.getString(R.string.easiness)
            assignTextSpinnerAdapter(secondSpinner, R.array.easiness_array)
        }
        with(builder){
            setView(dialogView)
            setPositiveButton(getDialogContext().getString(R.string.done)) { _, _ ->
                val user = (activity as MainActivity).user
                val url = "https://frozen-springs-73195.herokuapp.com/getdiet?intake=${calculateDailyCaloricIntake(user)}&count=${user.dishesCount}}&type=${user.dishesType}&accessibility=${holder.fistSpinner.selectedItemPosition+1}&easiness=${holder.secondSpinner.selectedItemPosition+1}"
                JsonTask().execute(url);
            }
            setNegativeButton(getDialogContext().getString(R.string.cancel)) { _, _ ->}
        }.create().show()
    }

    override fun getDialogContext(): Context = requireContext()

}

class TwoSpinnerViewHolder(view: View){
    val firstTextView: TextView = view.findViewById(R.id.dialog_two_spinner_text_view)
    val secondTextView: TextView = view.findViewById(R.id.dialog_two_spinner_second_text_view)
    val fistSpinner:AppCompatSpinner = view.findViewById(R.id.dialog_two_spinner)
    val secondSpinner:AppCompatSpinner = view.findViewById(R.id.dialog_two_spinner_second)
}


