package inz.mb.pl.app.fitapp.adapter


import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import inz.mb.pl.app.fitapp.GetStartedActivity
import inz.mb.pl.app.fitapp.ui.get_started.GetStartedFragmentOne
import inz.mb.pl.app.fitapp.ui.get_started.GetStartedFragmentThree
import inz.mb.pl.app.fitapp.ui.get_started.GetStartedFragmentTwo

class SectionsPagerAdapter(activity : GetStartedActivity) :
    FragmentStateAdapter(activity) {

    override fun getItemCount(): Int = 3

    override fun createFragment(position: Int): Fragment {
        when(position){
            0 -> return GetStartedFragmentOne.newInstance(position)
            1 -> return GetStartedFragmentTwo.newInstance(position)
            2 -> return GetStartedFragmentThree.newInstance(position)
        }
        return GetStartedFragmentOne.newInstance(position + 1)
    }
}