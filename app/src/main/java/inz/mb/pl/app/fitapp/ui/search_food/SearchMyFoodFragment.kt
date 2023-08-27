package inz.mb.pl.app.fitapp.ui.search_food

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import inz.mb.pl.app.fitapp.R
import inz.mb.pl.app.fitapp.SearchFoodActivity
import inz.mb.pl.app.fitapp.adapter.operation_dish_viewholders.NewDishViewHolder
import inz.mb.pl.app.fitapp.adapter.operation_dish_viewholders.NewDishViewHolder.Companion.checkIfIsBlankDouble
import inz.mb.pl.app.fitapp.adapter.operation_dish_viewholders.NewDishViewHolder.Companion.checkIfIsBlankInt
import inz.mb.pl.app.fitapp.adapter.search_food_adapters.SearchMyFoodAdapter
import inz.mb.pl.app.fitapp.database.saved_dish.SavedDishDTO
import inz.mb.pl.app.fitapp.databinding.FragmentSearchMyFoodBinding
import inz.mb.pl.app.fitapp.helper.Helper
import inz.mb.pl.app.fitapp.interfaces.DialogInterface
import inz.mb.pl.app.fitapp.interfaces.QueryInterface
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch


class SearchMyFoodFragment : Fragment(), DialogInterface, QueryInterface {
    private var _binding : FragmentSearchMyFoodBinding? = null
    lateinit var auth : FirebaseAuth
    private lateinit var searchFoodActivity : SearchFoodActivity
    private val binding get() = _binding!!
    private val job = SupervisorJob()
    private val ioScope by lazy { CoroutineScope(job + Dispatchers.IO) }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        auth = Firebase.auth
        _binding =  FragmentSearchMyFoodBinding.inflate(inflater, container, false)
        searchFoodActivity = activity as SearchFoodActivity
        searchFoodActivity.mainSearchView.setOnQueryTextListener(object :
            SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                setUpListView(Helper.savedDishItemPerName(query))
                return false
            }
            override fun onQueryTextChange(p0: String?): Boolean = false

        })
        setUpListView(Helper.savedDishesList)
        binding.addNewFood.setOnClickListener { createNewDish() }
        return binding.root
    }

    fun setUpListView(list : ArrayList<SavedDishDTO>){
        val layoutManager = LinearLayoutManager(requireContext())
        binding.searchMyFoodListView.adapter = SearchMyFoodAdapter(requireContext(),searchFoodActivity,list,this.layoutInflater)
        binding.searchMyFoodListView.layoutManager = layoutManager
    }


    override fun onResume() {
        super.onResume()
        searchFoodActivity.mainSearchView.setOnQueryTextListener(object :
            SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                setUpListView(Helper.savedDishItemPerName(query))
                return false
            }
            override fun onQueryTextChange(p0: String?): Boolean = false
        })
    }

    private fun createNewDish(){
        val dialog = createNewAlertDialog(resources.getString(R.string.new_dish))
        val inflater = this.layoutInflater
        val dialogView = inflater.inflate(R.layout.add_food_dialog, null)
        val holder = NewDishViewHolder(dialogView)
        with(dialog){
                setView(dialogView)
                setPositiveButton(resources.getString(R.string.done)) { _, _ ->
                    val newSavedDish = SavedDishDTO(
                        carbs = checkIfIsBlankDouble(holder.dishCarb.text.toString()),
                        fat =  checkIfIsBlankDouble(holder.dishFat.text.toString()),
                        protein =  checkIfIsBlankDouble(holder.dishProtein.text.toString()),
                        kcalEaten = checkIfIsBlankInt(holder.dishKcal.text.toString()),
                        meal_type = holder.mealType.selectedItemPosition,
                        name = holder.dishName.text.toString(),
                        name_lowercase = holder.dishName.text.toString().lowercase(),
                        type = holder.dietType.selectedItemPosition,
                        typeOfUnits = holder.unitType.selectedItemPosition
                    )
                    if(!Helper.savedDishesList.contains(newSavedDish)){
                        ioScope.launch {
                            val id = addNewSavedDish(newSavedDish)
                            newSavedDish.id = id?.toInt() ?: 0
                            Helper.savedDishesList.add(newSavedDish)
                        }.start()
                        setUpListView(Helper.savedDishesList)
                    }
                }
                setNegativeButton(resources.getString(R.string.cancel)) {_, _ ->}
        }.show()
    }





    override fun getDialogContext(): Context = requireContext()


}