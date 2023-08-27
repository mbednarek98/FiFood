package inz.mb.pl.app.fitapp.ui.main

import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import inz.mb.pl.app.fitapp.databinding.FragmentDashboardBinding
import androidx.recyclerview.widget.GridLayoutManager
import androidx.core.widget.addTextChangedListener
import inz.mb.pl.app.fitapp.MainActivity
import inz.mb.pl.app.fitapp.R
import java.time.LocalDate
import inz.mb.pl.app.fitapp.adapter.CalendarAdapter
import inz.mb.pl.app.fitapp.adapter.exercise_adapters.ExerciseAdapter
import inz.mb.pl.app.fitapp.adapter.exercise_adapters.ExerciseTypeAdapter
import inz.mb.pl.app.fitapp.helper.Helper
import inz.mb.pl.app.fitapp.interfaces.DialogInterface
import inz.mb.pl.app.fitapp.interfaces.SpinnerInterface
import inz.mb.pl.app.fitapp.database.exercise.ExerciseDTO
import inz.mb.pl.app.fitapp.parent.CalendarFragment
import inz.mb.pl.app.fitapp.interfaces.QueryInterface
import kotlinx.coroutines.*
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.collections.HashMap


class DashboardFragment : CalendarFragment(), DialogInterface,
    SpinnerInterface, QueryInterface {
    private var _binding: FragmentDashboardBinding? = null
    private val job = SupervisorJob()
    private val ioScope by lazy { CoroutineScope(job + Dispatchers.IO) }
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        auth = Firebase.auth
        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        Helper.selectedDate = LocalDate.now()
        setupCreateView()
        println(Helper.exerciseDTOList)
        toolBarSetup(binding.toolbar)
        return binding.root
    }

    override fun buttonSetUp(){
        with(binding){
            leftButton.setOnClickListener { previousWeekAction() }
            rightButton.setOnClickListener { nextWeekAction() }
            addNewWorkoutButton.setOnClickListener { onClickExerciseDialog() }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun initWidgets() {
        calendarRecyclerView = binding.calendarRecyclerView
        monthYearText = binding.monthYearTV
    }

    override fun setWeekView() {
        monthYearText?.text = monthYearFromDate(Helper.selectedDate)
        calendarRecyclerView?.layoutManager = GridLayoutManager(this.requireContext(), 7)
        calendarRecyclerView?.adapter = CalendarAdapter(daysInWeekArray(Helper.selectedDate), this)
        setExerciseAdapter()
    }

    private fun onClickExerciseDialog(exerciseDTO : ExerciseDTO? = null) {
        val dialog = createNewAlertDialog(resources.getString(R.string.new_exercise))
        val inflater = this.layoutInflater
        val dialogView = inflater.inflate(R.layout.exercise_dialog, null)
        val type = dialogView.findViewById<LinearLayout>(R.id.type_layout)
        val imgType = dialogView.findViewById<ImageView>(R.id.type_image)
        val textType = dialogView.findViewById<TextView>(R.id.type_inside_text)
        if(exerciseDTO == null) setImgAndTextViewForType(imgType,textType,0)
        var exerciseData = ExerciseData(0,0)
        val timeMinutes = dialogView.findViewById<NumberPicker>(R.id.minutes_number_picker)
        setUpDigits(timeMinutes)
        val timeSeconds = dialogView.findViewById<NumberPicker>(R.id.seconds_number_picker)
        setUpDigits(timeSeconds)
        type.setOnClickListener {
                val restOfFunction: Runnable = Runnable {
                    println(exerciseData.positionName)
                    setImgAndTextViewForType(imgType,textType,exerciseData.positionName)
                }
                exerciseData = settingTypeOfExercise(restOfFunction)
        }
        if(exerciseDTO != null) {
            val time = LocalTime.parse("00:${exerciseDTO.timeOfExercise}")
            timeMinutes.value = time.minute
            timeSeconds.value = time.second
            exerciseData = ExerciseData(exerciseDTO.typeOfExercise,exerciseDTO.name)
            setImgAndTextViewForType(imgType,textType,exerciseDTO.typeOfExercise)
        }
        with(dialog){
            setView(dialogView)
            setPositiveButton(resources.getString(R.string.done)) { _, _ ->
                val newStringMinute =  if(timeMinutes.value < 10) "0${timeMinutes.value}" else  "${timeMinutes.value}"
                val newStringSecond = if(timeSeconds.value < 10) "0${timeSeconds.value}" else "${timeSeconds.value}"
                val newTime = "${newStringMinute}:${newStringSecond}"
                val calculatedKcalBurned = kcalBurnedEquation(exerciseData.positionValue,exerciseData.positionName,newTime)
                when (exerciseDTO) {
                    null -> {

                        val newExercise = ExerciseDTO(typeOfExercise = exerciseData.positionName, name =  exerciseData.positionValue, timeOfExercise =  newTime, kcalBurned = calculatedKcalBurned, date = Helper.selectedDate)
                        Helper.exerciseDTOList.add(newExercise)
                        ioScope.launch {
                            val newId = addNewExercise(newExercise)
                            Helper.exerciseDTOList[Helper.exerciseDTOList.indexOf(newExercise)].id = newId?.toInt() ?: 0
                        }.start()
                        setExerciseAdapter()
                    }
                    else -> {
                        val newExercise = ExerciseDTO(exerciseDTO.id,  exerciseData.positionName, exerciseData.positionValue, newTime, calculatedKcalBurned, Helper.selectedDate)
                        Helper.exerciseDTOList[Helper.exerciseDTOList.indexOf(exerciseDTO)] = newExercise
                        ioScope.launch {
                            updateExercise(newExercise)
                        }
                    }
                }
                setExerciseAdapter() }
            setNegativeButton(resources.getString(R.string.cancel)) { _, _ -> }
            create().show()
        }
    }

    private fun setUpDigits(numberPicker: NumberPicker, lowerRange : Int = 0, upperRange : Int  = 59, ifWheel : Boolean = true){
        numberPicker.maxValue = upperRange
        numberPicker.minValue = lowerRange
        numberPicker.value = 0
        numberPicker.wrapSelectorWheel = ifWheel
    }

    private fun kcalBurnedEquation(name : Int, type : Int, time : String) : Int{
        val typeString = requireContext().resources.getStringArray(R.array.exercise_group_image_array)[type]
        val rMETSTypeString = "${typeString}_MET_array"
        val idMETS = requireContext().resources.getIdentifier(rMETSTypeString, "array", requireContext().packageName)
        val exerciseMET = requireContext().resources.getStringArray(idMETS)[name].toDouble()
        val localTime = LocalTime.parse("00:$time",DateTimeFormatter.ofPattern("H:mm:ss"))
        val min = localTime.minute
        val sec = (localTime.second/60).toDouble()
        println(exerciseMET)
        return  ((3.5 * ((activity as MainActivity?)?.weightChangeDTO?.weight ?: 50.0) * exerciseMET * (min+sec) )/200).toInt()
    }

    private fun setImgAndTextViewForType(imgView : ImageView, textView: TextView, position: Int){
        val id = requireContext().resources.getIdentifier(resources.getStringArray(R.array.exercise_group_image_array).toList()[position], "drawable", requireContext().packageName)
        imgView.setBackgroundResource(id)
        textView.text = resources.getStringArray(R.array.exercise_group_array).toList()[position]
    }


    private fun setExerciseAdapter(){
        val adapter = ExerciseAdapter(requireContext(),Helper.exercisePerDate(Helper.selectedDate))
        binding.exerciseListView.adapter =  adapter
        binding.exerciseListView.setOnItemClickListener { _, _, i, _ ->
            onClickExerciseDialog(Helper.exercisePerDate(Helper.selectedDate)[i])
        }
        binding.exerciseListView.setOnItemLongClickListener { _, _, i, _ ->
            val builder = AlertDialog.Builder(getDialogContext())
            with(builder) {
                setTitle(resources.getString(R.string.alert))
                setMessage(resources.getString(R.string.alert_exercise_desc))
                setPositiveButton(android.R.string.ok) { _, _ ->
                    val exerciseList = Helper.exercisePerDate(Helper.selectedDate)
                    println(exerciseList[i].id)
                    Helper.exerciseDTOList.remove(exerciseList[i])
                    adapter.remove(exerciseList[i])
                    adapter.notifyDataSetChanged()
                    ioScope.launch {
                        removeExercise(exerciseList[i])
                    }
                }
                setNegativeButton(android.R.string.cancel) { _, _ ->  }
                create().show()
            }
            return@setOnItemLongClickListener true
        }
    }

    private fun settingTypeOfExercise(runnable: java.lang.Runnable) : ExerciseData {
        val dialog = createNewAlertDialog(resources.getString(R.string.new_exercise))
        val inflater = this.layoutInflater
        val dialogView = inflater.inflate(R.layout.exercise_type_dialog, null)
        val listview : ExpandableListView = dialogView.findViewById(R.id.list_view_type_of_exercise)
        val search = dialogView.findViewById<EditText>(R.id.search_edit_text)
        val listOfGroups = resources.getStringArray(R.array.exercise_group_array)
        val listOfURLGroups = resources.getStringArray(R.array.exercise_group_image_array)
        val map : HashMap<String,List<String>> = setUpHashMap(listOfGroups.toList(),listOfURLGroups.toList())
        val adapter = ExerciseTypeAdapter(requireContext(), listOfGroups.toList(), listOfURLGroups.toList(), map)
        var newListOfURLGroups: MutableList<String> = listOfURLGroups.toMutableList()
        var newListOfGroups : MutableList<String> = listOfGroups.toMutableList()
        val exerciseData  = ExerciseData(0,0)
        listview.setAdapter(adapter)
        search.addTextChangedListener {
            val query = search.text.toString()
            newListOfURLGroups = ArrayList()
            newListOfGroups = ArrayList()
            for((index, value) in listOfGroups.withIndex()){
                if(value.lowercase().contains(query.lowercase())){
                    newListOfGroups.add(listOfGroups[index])
                    newListOfURLGroups.add(listOfURLGroups[index])
                }
            }
            val newMap : HashMap<String,List<String>> = setUpHashMap(newListOfGroups,newListOfURLGroups)
            val newAdapter = ExerciseTypeAdapter(requireContext(), newListOfGroups.toList(), newListOfURLGroups.toList(), newMap)
            listview.setAdapter(newAdapter)
        }

        with(dialog) {
            setView(dialogView)
            setNegativeButton(android.R.string.cancel) { _, _ ->
            }
            val alertDialog : AlertDialog = create()
            alertDialog.show()
            listview.setOnChildClickListener { _, _, i, i2, _ ->
                exerciseData.positionName = listOfGroups.indexOf(newListOfGroups[i])
                exerciseData.positionValue = i2
                alertDialog.cancel()
                runnable.run()
                return@setOnChildClickListener true
            }
        }


        return exerciseData
    }


        private fun setUpHashMap(listOfGroup: List<String>, listOfURLGroups: List<String>) : HashMap<String,List<String>>{
        val map : HashMap<String,List<String>> = HashMap()
        for((index,value) in listOfURLGroups.withIndex()){
                val nameOfStringArray =
                    value + "_description_array"
                val idDesc = requireContext().resources.getIdentifier(
                    nameOfStringArray,
                    "array",
                    requireContext().packageName
                )
                map[listOfGroup[index]] = requireContext().resources.getStringArray(idDesc).toList()
            }
        return map
    }




    override fun getDialogContext(): Context = this.requireContext()
    inner class ExerciseData(var positionName : Int, var positionValue : Int)
}