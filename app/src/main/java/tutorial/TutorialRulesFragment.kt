package tutorial

import android.os.Bundle
import android.support.v4.app.Fragment
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.neo.hiperforca.R
import com.example.neo.hiperforca.core.GlobalConstants
import kotlinx.android.synthetic.main.fragment_tutorial_rules.*
import com.example.neo.hiperforca.core.fromHtml


/**
 * Created by isabella on 01/12/17.
 */
class TutorialRulesFragment: Fragment() {
    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater?.inflate(R.layout.fragment_tutorial_rules, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        fragment_tutorial_rules_first.text = getString(R.string.tutorial_rules_first_rule).fromHtml()
        fragment_tutorial_rules_second.text = getString(R.string.tutorial_rules_second_rule).fromHtml()
        fragment_tutorial_rules_third.text = getString(R.string.tutorial_rules_third_rule).fromHtml()
        fragment_tutorial_rules_fourth.text = getString(R.string.tutorial_rules_fourth_rule).fromHtml()
        fragment_tutorial_rules_fifth.text = getString(R.string.tutorial_rules_fifth_rule).format(GlobalConstants.NUMBER_OF_ATTEMPTS + 1).fromHtml()
        fragment_tutorial_rules_sixth.text = getString(R.string.tutorial_rules_sixth_rule).format(GlobalConstants.SECONDS_TO_PLAY).fromHtml()
        super.onViewCreated(view, savedInstanceState)
    }

    companion object {
        fun newInstance(): TutorialRulesFragment {
            return TutorialRulesFragment()
        }
    }
}