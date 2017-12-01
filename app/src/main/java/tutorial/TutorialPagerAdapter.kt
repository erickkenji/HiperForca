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
            0 -> TutorialWelcome.newInstance()
            1 -> TutorialRulesFragment.newInstance()
            2 -> TutorialScoreFragment()
            else -> TutorialRulesFragment.newInstance()
        }
    }

    override fun getCount(): Int {
        return 3
    }

    override fun getPageWidth(position: Int): Float {
        return 0.93f
    }
}