package tutorial

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.neo.hiperforca.GallowsController
import com.example.neo.hiperforca.R
import com.example.neo.hiperforca.core.GlobalConstants
import com.example.neo.hiperforca.core.fromHtml
import kotlinx.android.synthetic.main.fragment_tutorial_score.*

/**
 * Created by isabella on 01/12/17.
 */
class TutorialScoreFragment: Fragment() {
    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater?.inflate(R.layout.fragment_tutorial_score, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        fragment_tutorial_score_first.text = getString(R.string.tutorial_score_first_rule).fromHtml()
        fragment_tutorial_score_second.text = getString(R.string.tutorial_score_second_rule).format(37, GallowsController.calculateTimeBonus(37)).fromHtml()
        fragment_tutorial_score_third.text = getString(R.string.tutorial_score_third_rule).format(GlobalConstants.WORD_HIT_BONUS).fromHtml()
        fragment_tutorial_score_fourth.text = getString(R.string.tutorial_score_fourth_rule).format(GlobalConstants.WORD_ERROR_PENALTY).fromHtml()
        fragment_tutorial_score_fifth.text = getString(R.string.tutorial_score_fifth_rule).format(GlobalConstants.TIME_PENALTY).fromHtml()
        fragment_tutorial_score_sixth.text = getString(R.string.tutorial_score_sixth_rule).fromHtml()
        super.onViewCreated(view, savedInstanceState)
    }

    companion object {
        fun newInstance(): TutorialScoreFragment {
            return newInstance()
        }
    }
}