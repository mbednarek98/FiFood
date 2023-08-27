package inz.mb.pl.app.fitapp.ui.search_food

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView.OnQueryTextListener
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import inz.mb.pl.app.fitapp.SearchFoodActivity
import inz.mb.pl.app.fitapp.adapter.search_food_adapters.SearchFoodFragmentAdapter
import inz.mb.pl.app.fitapp.database.FirestoreClass
import inz.mb.pl.app.fitapp.databinding.FragmentSearchFoodBinding
import inz.mb.pl.app.fitapp.database.saved_dish.SavedDishDTO
import inz.mb.pl.app.fitapp.helper.Helper


class SearchFoodFragment : Fragment() {
    private var _binding : FragmentSearchFoodBinding? = null
    lateinit var auth : FirebaseAuth
    private lateinit var searchFoodActivity : SearchFoodActivity
    private val binding get() = _binding!!
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        auth = Firebase.auth
        searchFoodActivity = activity as SearchFoodActivity
        searchFoodActivity.mainSearchView.setOnQueryTextListener(object : OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                FirestoreClass().searchDishes(this@SearchFoodFragment,query)
                return false
            }
            override fun onQueryTextChange(p0: String?): Boolean = false

        })
        _binding =  FragmentSearchFoodBinding.inflate(inflater, container, false)
        setUpListView(Helper.savedDishesList)
        return binding.root
    }

    fun setUpListView(list : ArrayList<SavedDishDTO>){
        val layoutManager = LinearLayoutManager(requireContext())
        binding.searchFoodListView.adapter = SearchFoodFragmentAdapter(requireContext(),searchFoodActivity,list,this.layoutInflater)
        binding.searchFoodListView.layoutManager = layoutManager
    }

    override fun onResume() {
        super.onResume()
        setUpListView(Helper.savedDishesList)
        searchFoodActivity.mainSearchView.setOnQueryTextListener(object :
            OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                FirestoreClass().searchDishes(this@SearchFoodFragment,query)
                return false
            }
            override fun onQueryTextChange(p0: String?): Boolean = false
        })
    }



}