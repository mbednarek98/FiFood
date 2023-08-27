package inz.mb.pl.app.fitapp.ui.main

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.res.Resources
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.*
import android.widget.EditText
import android.widget.NumberPicker
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.components.LimitLine
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.ValueFormatter
import com.google.android.material.progressindicator.LinearProgressIndicator
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import inz.mb.pl.app.fitapp.parent.BasicFragment
import inz.mb.pl.app.fitapp.MainActivity
import inz.mb.pl.app.fitapp.R
import inz.mb.pl.app.fitapp.adapter.FoodTileAdapter
import inz.mb.pl.app.fitapp.database.water_change.WaterChangeDTO
import inz.mb.pl.app.fitapp.database.weight_change.WeightChangeDTO
import inz.mb.pl.app.fitapp.databinding.FragmentHomeBinding
import inz.mb.pl.app.fitapp.enums.DBTypeEnum
import inz.mb.pl.app.fitapp.helper.Helper
import inz.mb.pl.app.fitapp.helper.Helper.WEIGHT_LOWER_RANGE
import inz.mb.pl.app.fitapp.helper.Helper.WEIGHT_UPPER_RANGE
import inz.mb.pl.app.fitapp.interfaces.CalorieCalcInterface
import inz.mb.pl.app.fitapp.interfaces.DialogInterface
import inz.mb.pl.app.fitapp.interfaces.QueryInterface
import inz.mb.pl.app.fitapp.interfaces.TimeInterface
import inz.mb.pl.app.fitapp.models.User
import kotlinx.coroutines.*
import java.lang.Runnable
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.*
import kotlin.collections.ArrayList
import kotlin.math.pow


class HomeFragment : BasicFragment(), TimeInterface, QueryInterface, CalorieCalcInterface, DialogInterface {

    private var user: User? = null
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private val job = SupervisorJob()
    private val ioScope by lazy { CoroutineScope(job + Dispatchers.IO) }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        auth = Firebase.auth
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        user = (activity as MainActivity?)?.user
        setUpFragmentView(user)
        buttonSetUp()
        toolBarSetup(binding.toolbar)
        return binding.root
    }

    private fun setUpFragmentView(user: User?) {
        when {
            user != null -> {
                setUpBurnKcal()
                setUpFoodRecycleView()
                setUpWeightTab()
                setUpCircleKcal()
                setUpWaterIntake()
                setupChart()
            }
        }
    }

    private fun setUpFoodRecycleView(){
        binding.modelListRc.adapter = FoodTileAdapter((activity as MainActivity), requireContext(), user?.dishesCount ?: 0, calculateDailyCaloricIntake(user!!))
        binding.modelListRc.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
    }

    override fun onResume() {
        super.onResume()
        user = (activity as MainActivity?)?.user
        setUpFoodRecycleView()
        refreshData()
    }


    @SuppressLint("ClickableViewAccessibility")
    private fun buttonSetUp() {
        with(binding) {
            editTextWeight.setOnClickListener { onClickWeightDialog() }
            buttonAdd.setOnClickListener { addMlValue() }
            buttonAdd.setOnTouchListener(AddWaterHandler(0))
            buttonRemove.setOnClickListener { removeMlValue() }
            buttonRemove.setOnTouchListener(AddWaterHandler())
            editTextMl.setOnClickListener { onClickWaterDialog() }
        }
    }

    inner class AddWaterHandler(var stringType: Int = 1) : View.OnTouchListener {
        private var mHandler: Handler? = null
        @SuppressLint("ClickableViewAccessibility")
        override fun onTouch(v: View?, event: MotionEvent): Boolean {
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    if (mHandler != null) return true
                    mHandler = Handler(Looper.getMainLooper())
                    mHandler!!.postDelayed(mAction, 200)
                }
                MotionEvent.ACTION_UP -> {
                    if (mHandler == null) return true
                    mHandler!!.removeCallbacks(mAction)
                    mHandler = null
                }
            }
            return false
        }

        var mAction: Runnable = object : Runnable {
            override fun run() {
                if (stringType == 1) removeMlValue()
                else addMlValue()
                mHandler!!.postDelayed(this, 200)
            }
        }
    }

    private fun setUpBurnKcal(){
        var count = 0
        for(exercise in Helper.exercisePerDate(LocalDate.now())){
            count += exercise.kcalBurned
        }
        binding.editTextBurned.text = count.toString()
    }

    private fun setUpWeightTab(){
        val weightChange = (activity as MainActivity?)?.weightChangeDTO
        when {
            weightChange != null -> {
                assignWeightSetup(
                    weightChange.weight.toString(),
                    resources.getString(R.string.updated_day_time, parseLocalDateTimeToDateString(weightChange.dateTime),
                        parseLocalDateTimeToTimeString(weightChange.dateTime)),resources.getString(R.string.weight_kg_change, formatToTwoDecimals(weightChange.weightChange)))
            }
            else -> {
                assignWeightSetup()
            }
        }
        refreshData()
    }

    private fun assignWeightSetup(weight : String = user?.weight.toString(), lastUpdate : String = "", weightCalculation : String = resources.getString(R.string.weight_kg_change, 0.00.toString())) {
        with(binding){
            editTextWeight.text = weight
            editTextLastUpdate.text = lastUpdate
            editTextWeightCalc.text = weightCalculation
        }

    }

    private fun refreshData() = with(binding) {
            editTextBmi.text = calculateBMI()
            editTextLiter.text = calculateDailyWaterIntake()
            val calorieIntake = calculateDailyCaloricIntake(user!!)
            val kcalEaten = calculateKcalEaten(calorieIntake)
            val intakeEaten = calorieIntake-kcalEaten
            editTextKcal.text = intakeEaten.toString()
            editTextEaten.text = kcalEaten.toString()
            val calc = calculateProgress(calorieIntake,kcalEaten)
            progressBarKcla.progress = calc
            editTextHeight.text = user?.height.toString()
        }

    private fun calculateBMI() : String = formatToTwoDecimals(user?.weight!!/((user?.height!!.toDouble()/100).pow(2)))
    private fun formatToTwoDecimals(number : Double) : String = String.format(Locale.US, "%.2f",number)
    private fun setUpCircleKcal() = with(binding) {
        val weight = (activity as MainActivity?)?.weightChangeDTO
        if(weight != null) user?.weight = weight.weight
        val calorieIntake = calculateDailyCaloricIntake(user!!)
        val kcalEaten = calculateKcalEaten(calorieIntake)
        var intakeEaten = calorieIntake-kcalEaten
        if(intakeEaten == 0) intakeEaten = 0
        editTextKcal.text = intakeEaten.toString()
        editTextEaten.text = kcalEaten.toString()
        val calc = calculateProgress(calorieIntake,kcalEaten)
        progressBarKcla.progress = calc
    }

    private fun calculateKcalEaten(calorieIntake: Int) : Int{
        var count = 0
        var countCarb = 0.0
        var countProtein = 0.0
        var countFat = 0.0
        for(dish in Helper.dishItemPerDate(LocalDate.now())){
            count += dish.kcalEaten
            countCarb += dish.carbs
            countProtein += dish.protein
            countFat += dish.fat
        }
        calculateBar(calorieIntake, countCarb, 0.4, binding.editTextCarbLeft, binding.editProgressCarbs)
        calculateBar(calorieIntake, countProtein, 0.1, binding.editTextProteinLeft, binding.editProgressProtein)
        calculateBar(calorieIntake, countFat, 0.25, binding.editTextFatLeft, binding.editProgressFat)
        return count
    }

    private fun calculateBar(calorieIntake: Int, takeFrom: Double, percent: Double, textView: TextView, progressBar: LinearProgressIndicator){
        val gramIntake = calorieIntake*0.129
        val intakePercent = (gramIntake*percent).toInt()
        var actualGram = (intakePercent-takeFrom)
        if(actualGram < 0) actualGram = 0.0
        textView.text = requireContext().resources.getString(R.string.g_left,formatToTwoDecimals(actualGram))
        progressBar.progress = ((takeFrom*100)/ intakePercent).toInt()
    }


    private fun calculateProgress(calorieIntake: Int, calorieTake : Int) : Int = when (calorieTake) {
            0 -> 1
            else -> (calorieTake * 100) / calorieIntake
        }


    private fun onClickWaterDialog(){
        val dialog = createNewAlertDialog(
            resources.getString(R.string.new_water),
        )
        val inflater = this.layoutInflater
        val dialogView = inflater.inflate(R.layout.water_dialog, null)
        dialog.setView(dialogView)
        val waterEditText = dialogView.findViewById<EditText>(R.id.water_edit_text)
        dialog.setPositiveButton(resources.getString(R.string.done)) { _, _ ->
            val waterText = waterEditText.text.toString()
            binding.editTextMl.text = waterText
            changeWaterGraphic(waterText.toInt())
            setWaterChangeDB(waterText.toInt())
        }
        dialog.setNegativeButton(resources.getString(R.string.cancel)) { _, _ -> }
        dialog.create().show()
    }



    private fun onClickWeightDialog(){
        val dialog = createNewAlertDialog(
            resources.getString(R.string.new_weight),
        )
        val inflater = this.layoutInflater
        val dialogView = inflater.inflate(R.layout.number_picker_dialog, null)
        dialog.setView(dialogView)
        val digitsAfter = numberPickerInflate(dialogView,false)
        val digitsBefore = numberPickerInflate(dialogView)
        dialog.setPositiveButton(resources.getString(R.string.done)) { _, _ -> setPositiveButton("${digitsBefore.value}.${digitsAfter.value}")
            }
        dialog.setNegativeButton(resources.getString(R.string.cancel)) { _, _ -> }
        dialog.create().show()
    }


    private fun setPositiveButton(newWeight : String){
            val weightDiff : String = formatToTwoDecimals(newWeight.toDouble() - ((activity as MainActivity?)?.weightChangeDTO?.weight ?: 0.0))
            assignNewWeightDataToView(newWeight,weightDiff)
            refreshData()
            assignNewWeightToDB(newWeight,weightDiff)
    }

    private fun assignNewWeightDataToView(newWeight : String, weightDifference : String){
        with(binding) {
            editTextWeightCalc.text = resources.getString(R.string.weight_kg_change, weightDifference)
            user?.weight = newWeight.toDouble()
            editTextWeight.text = newWeight
            editTextLastUpdate.text = resources.getString(
                R.string.updated_day_time,
                resources.getString(R.string.today_day),
                parseLocalDateTimeToTimeString(LocalDateTime.now().toString())
            )
        }
    }

    private fun assignNewWeightToDB(newWeight : String, weightDifference : String){
        (activity as MainActivity?)?.weightChangeDTO = WeightChangeDTO(weight = newWeight.toDouble(), weightChange = weightDifference.toDouble(),
            dateTime = LocalDateTime.now().toString())
        ioScope.launch {
            insertWeightChange((activity as MainActivity?)?.weightChangeDTO!!)
        }
    }

    private fun numberPickerInflate(dialogView : View, isBefore : Boolean = true) : NumberPicker{
        val weightText = binding.editTextWeight.text
        val indexOfDecimal = weightText.indexOf(".")
        return when {
            isBefore -> {
                val digitsBefore = dialogView.findViewById<NumberPicker>(R.id.number_before_coma)
                setUpDigits(digitsBefore, WEIGHT_LOWER_RANGE, WEIGHT_UPPER_RANGE,false, weightText.substring(0,indexOfDecimal))
                digitsBefore

            }
            else -> {
                val digitsAfter = dialogView.findViewById<NumberPicker>(R.id.number_after_coma)
                setUpDigits(digitsAfter, numberString = weightText.substring(indexOfDecimal+1))
                digitsAfter
            }
        }
    }

    private fun setUpDigits(numberPicker: NumberPicker, lowerRange : Int = 0, upperRange : Int = 9, ifWheel : Boolean = true, numberString : String){
        numberPicker.maxValue = upperRange
        numberPicker.minValue = lowerRange
        numberPicker.value = numberString.toInt()
        numberPicker.wrapSelectorWheel = ifWheel
    }

    private fun setUpWaterIntake(){
        with(binding){
            val waterChange = (activity as MainActivity?)?.waterChangeDTO
            editTextLiter.text = calculateDailyWaterIntake()
            when {
                waterChange != null -> {
                    editTextMl.text = waterChange.waterChange.toString()
                    changeWaterGraphic(waterChange.waterChange)
                }
                else -> {
                    editTextMl.text = "0"
                }
            }
        }
    }

    private fun calculateDailyWaterIntake():String = formatToTwoDecimals(user?.weight!! * 0.03)




    private fun addMlValue(){
        var amount = binding.editTextMl.text.toString().toInt()
        amount += 50
        changeWaterGraphic(amount)
        setWaterChangeDB(amount)

    }
    private fun removeMlValue(){
        var amount = binding.editTextMl.text.toString().toInt()
        when {
            amount >= 50 -> amount -= 50
            amount <= 50 -> amount = 0
        }
        changeWaterGraphic(amount)
        setWaterChangeDB(amount)
    }

    private fun changeWaterGraphic(amount: Int){
        val percentAmount = (amount / (binding.editTextLiter.text.toString().toDouble()*1000))
        println(percentAmount)
        val waterArray = requireContext().resources.getStringArray(R.array.water_array)
        var id = requireContext().resources.getIdentifier(waterArray[0], "drawable", requireContext().packageName)
        when {
            percentAmount > 0 && percentAmount < 0.2 -> {
                id = requireContext().resources.getIdentifier(waterArray[1], "drawable", requireContext().packageName)
            }
            percentAmount >= 0.2 && percentAmount < 0.4 -> {
                id = requireContext().resources.getIdentifier(waterArray[2], "drawable", requireContext().packageName)
            }
            percentAmount >= 0.4 && percentAmount < 0.6 -> {
                id = requireContext().resources.getIdentifier(waterArray[3], "drawable", requireContext().packageName)
            }
            percentAmount >= 0.6 && percentAmount < 0.8 -> {
                id = requireContext().resources.getIdentifier(waterArray[4], "drawable", requireContext().packageName)
            }
            percentAmount in 0.8..1.0 -> {
                id = requireContext().resources.getIdentifier(waterArray[5], "drawable", requireContext().packageName)
            }
            percentAmount > 1.0 -> {
                id = requireContext().resources.getIdentifier(waterArray[6], "drawable", requireContext().packageName)
            }
        }
        binding.waterBtlImg.setBackgroundResource(id)
    }


    private fun setWaterChangeDB(amount : Int){
        binding.editTextMl.text = amount.toString()
        val date = LocalDate.now()
        (activity as MainActivity?)?.waterChangeDTO = WaterChangeDTO(waterChange = amount, dateTime = date.toString(), day = date.dayOfMonth, month = date.monthValue, year = date.year)
        ioScope.launch {
            checkIfUpdateOrInsertWaterChange(date,amount)
        }
    }

    private suspend fun checkIfUpdateOrInsertWaterChange(date : LocalDate, amount : Int){
        val waterChange = selectData(date.dayOfMonth, date.monthValue, date.year, DBTypeEnum.WATER) as WaterChangeDTO?
        when {
            waterChange != null -> updateWaterChange(waterChange.id, amount)
            else -> insertWaterChange((activity as MainActivity?)?.waterChangeDTO!!)
        }
    }

    //TODO get weekly info
    private fun setupChart() {
        val barChart = binding.chart
        val kcalList = getListOfDishCount(LocalDate.now())
        val listKcalEaten = arrayListOf<BarEntry>()
        val listKcalBurned = arrayListOf<BarEntry>()
        val names = arrayListOf("")
        val mapOfValues = getListOfDaysAndExerciseCount(LocalDate.now())
        var count = 0f
        kcalList.forEach { kc ->
            listKcalEaten.add(BarEntry(count++, kc.toFloat()))
            count++
        }
        count = 0f
        mapOfValues.forEach { (s, i) ->
            listKcalBurned.add(BarEntry(count++, i.toFloat()))
            names.add(s)
            count++
        }
        barChart.setDrawBarShadow(false)
        barChart.setDrawValueAboveBar(true)
        barChart.setMaxVisibleValueCount(50)
        barChart.setPinchZoom(false)
        barChart.setTouchEnabled(false)
        barChart.setScaleEnabled(false)
        barChart.setPinchZoom(false)
        barChart.description.isEnabled = false
        barChart.setDrawGridBackground(false)
        barChart.setFitBars(true)
        barChart.animateY(2000)

        val barDataSet = BarDataSet(listKcalEaten, "Kcal zjedzone")
        barDataSet.color = ContextCompat.getColor(requireContext(),R.color.palette_light_blue)
        val barDataSet1 = BarDataSet(listKcalBurned, "Kcal spalone")
        barDataSet1.color = ContextCompat.getColor(requireContext(),R.color.palette_red)
        val data = BarData(barDataSet, barDataSet1)
        data.barWidth = 0.43f
        barChart.data = data
        barChart.groupBars(1f, 0.1f, 0.02f)

        val xAxis = barChart.xAxis
        xAxis.valueFormatter = MyXAxisFormatter(names)
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.granularity = 1f
        xAxis.setCenterAxisLabels(true)
        xAxis.axisMinimum = 1f
        xAxis.axisMaximum = 8f
        xAxis.textSize = 12f
        val axisLeft = barChart.axisLeft
        axisLeft.setDrawGridLines(false)
        axisLeft.setDrawLabels(false)
        axisLeft.setDrawAxisLine(false)
        val maxCapacity = calculateDailyCaloricIntake(user!!)
        axisLeft.addLimitLine(LimitLine(maxCapacity.toFloat(), "$maxCapacity"))
        val axisRight = barChart.axisRight
        axisRight.setDrawGridLines(false)
        axisRight.axisMaximum = maxCapacity * 1.5f
        axisLeft.axisMaximum = maxCapacity * 1.5f
        barChart.invalidate()
    }

    inner class MyXAxisFormatter(names: ArrayList<String>) : ValueFormatter() {
        private val myNames = names
        override fun getAxisLabel(value: Float, axis: AxisBase?): String {
            return myNames.getOrNull(value.toInt()) ?: value.toString()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun getInterfaceResources(): Resources = resources
    override fun getDialogContext(): Context = requireContext()

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) user = data?.extras?.getParcelable("ActivityResult") ?: User()
        println(user?.height)

    }
}

