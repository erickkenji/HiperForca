package tutorial

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem
import com.example.neo.hiperforca.R
import kotlinx.android.synthetic.main.activity_tutorial.*

/**
 * Created by isabella on 26/11/17.
 */
class TutorialActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tutorial)
        activity_tutorial_pager.clipToPadding = false
        activity_tutorial_pager.pageMargin = 24
        activity_tutorial_pager.adapter = TutorialPagerAdapter(supportFragmentManager)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        return when (item?.itemId) {
            android.R.id.home -> {
                finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}