package inz.mb.pl.app.fitapp

import android.os.Bundle
import android.util.Log
import com.google.android.material.tabs.TabLayout
import inz.mb.pl.app.fitapp.adapter.SectionsPagerAdapter
import inz.mb.pl.app.fitapp.databinding.ActivityGetStartedBinding
import inz.mb.pl.app.fitapp.parent.HelperActivity

import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayoutMediator
import inz.mb.pl.app.fitapp.ui.get_started.GetStartedFragmentOne
import inz.mb.pl.app.fitapp.ui.get_started.GetStartedFragmentTwo
import inz.mb.pl.app.fitapp.helper.Helper


class GetStartedActivity : HelperActivity() {
    private val binding by lazy { ActivityGetStartedBinding.inflate(layoutInflater) }
    lateinit var viewPager2: ViewPager2

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        setUpFragmentDots()
        screenSetUp(window)
        viewPager2Setup()
    }

    private fun setUpFragmentDots(){
        val sectionsPagerAdapter = SectionsPagerAdapter(this)
        viewPager2 = binding.viewPager
        viewPager2.adapter = sectionsPagerAdapter
        val tabs: TabLayout = binding.dots
        tabs.getTabAt(0)?.view?.isEnabled = false
        TabLayoutMediator(tabs, viewPager2) { _, _ -> }.attach()

    }

    fun setCurrentItem(item : Int, smoothScroll : Boolean){
        binding.viewPager.setCurrentItem(item, smoothScroll)
    }


    private fun viewPager2Setup(){
        viewPager2.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageScrollStateChanged(state: Int) {
                super.onPageScrollStateChanged(state)
                when (viewPager2.currentItem) {
                    0 -> Helper.FragmentOne = supportFragmentManager.findFragmentByTag("f" + viewPager2.currentItem) as GetStartedFragmentOne
                    1 -> Helper.FragmentOne = supportFragmentManager.findFragmentByTag("f" + (viewPager2.currentItem-1)) as GetStartedFragmentOne
                    2 -> Helper.FragmentTwo = supportFragmentManager.findFragmentByTag("f" + (viewPager2.currentItem-1)) as GetStartedFragmentTwo
                }
            }
        })
    }




}