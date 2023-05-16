package id.passage.example_android

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentContainerView

class MainActivity: AppCompatActivity() {

    private lateinit var fragmentContainerView: FragmentContainerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        fragmentContainerView = findViewById(R.id.nav_host_fragment_container)
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        val navHostFragment = fragmentContainerView.getFragment<Fragment>()
        val currentFragment = navHostFragment.childFragmentManager.fragments.first() ?: return
        val magicLinkFragment = currentFragment as? MagicLinkFragment ?: return
        val magicLink = intent?.data?.getQueryParameter("psg_magic_link") ?: return
        magicLinkFragment.handleDeepLinkMagicLink(magicLink)
    }

}

fun FragmentActivity.showAlert(title: String, message: String) {
    val dialogBuilder = AlertDialog.Builder(this)
    dialogBuilder.setMessage(message)
        .setCancelable(true)
        .setNegativeButton("Okay") { dialog, _ ->
            dialog.cancel()
        }
    val alert = dialogBuilder.create()
    alert.setTitle(title)
    alert.show()
}
