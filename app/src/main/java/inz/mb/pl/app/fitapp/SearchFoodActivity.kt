package inz.mb.pl.app.fitapp

import android.os.Bundle
import android.widget.SearchView
import com.google.android.material.tabs.TabLayoutMediator
import inz.mb.pl.app.fitapp.databinding.ActivitySearchFoodBinding
import inz.mb.pl.app.fitapp.ui.search_food.SearchFoodAdapter

class SearchFoodActivity : BaseActivity() {
    private val binding by lazy { ActivitySearchFoodBinding.inflate(layoutInflater) }
    lateinit var mainSearchView : SearchView
    private lateinit var nameOfDish : String
    var position : Int = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        mainSearchView = binding.searchView
        screenSetUp(window)
        nameOfDish = getNameOfDish()
        position = getNewPosition()
        toolbarSetUp()
        binding.searchFoodViewPager.adapter = SearchFoodAdapter(supportFragmentManager,lifecycle)
        val titles = resources.getStringArray(R.array.food_titles)
        TabLayoutMediator(binding.searchFoodTabLayout, binding.searchFoodViewPager){ tab, i -> tab.text = titles[i] }.attach()
    }

    override fun buttonSetUp() {

    }

    private fun toolbarSetUp(){
        binding.addFoodToolbar.title = nameOfDish
        binding.addFoodToolbar.setNavigationOnClickListener { finish() }
    }

    private fun getNameOfDish() : String = when {
        intent.hasExtra("name_of_dish") -> intent.extras?.getString("name_of_dish")!!
        else -> ""
    }

    private fun getNewPosition() : Int = when {
        intent.hasExtra("position") -> intent.extras?.getInt("position")!!
        else -> 0
    }

    override fun finish() {
        super.finish()
        overridePendingTransition(R.anim.slide_in_left,R.anim.slide_out_right)
    }
}