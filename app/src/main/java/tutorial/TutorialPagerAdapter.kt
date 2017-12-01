package tutorial

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter

/**
 * Created by isabella on 01/12/17.
 */
class TutorialPagerAdapter(fragmentManager: FragmentManager): FragmentPagerAdapter(fragmentManager) {
    override fun getItem(position: Int): Fragment {
        return when(position) {
            0 -> TutorialRulesFragment.newInstance()
            1 -> TutorialScoreFragment()
            else -> TutorialRulesFragment.newInstance()
        }
    }

    override fun getCount(): Int {
        return 2
    }
}