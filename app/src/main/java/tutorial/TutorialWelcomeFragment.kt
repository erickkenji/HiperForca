package tutorial

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.neo.hiperforca.R

/**
 * Created by isabella on 01/12/17.
 */
class TutorialWelcomeFragment : Fragment() {
    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater?.inflate(R.layout.fragment_tutorial_welcome, container, false)
    }

    companion object {
        fun newInstance(): TutorialWelcomeFragment {
            return TutorialWelcomeFragment()
        }
    }
}