package id.passage.example_android

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentContainerView
import id.passage.android.Passage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    private lateinit var fragmentContainerView: FragmentContainerView
    private lateinit var passage: Passage
    private val ioScope = CoroutineScope(Dispatchers.IO)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        fragmentContainerView = findViewById(R.id.nav_host_fragment_container)
        passage = Passage(this, "YOUR_APP_ID")
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        // Handling Magic link redirect url
        val magicLink = intent?.data?.getQueryParameter("psg_magic_link")
        val navHostFragment = fragmentContainerView.getFragment<Fragment>()
        val currentFragment = navHostFragment.childFragmentManager.fragments.first() ?: return
        if (magicLink != null) {
            val magicLinkFragment = currentFragment as? MagicLinkFragment ?: return
            magicLinkFragment.handleDeepLinkMagicLink(magicLink)
        }
        // Handling Hosted Login redirect url
        val code = intent?.data?.getQueryParameter("code")
        val state = intent?.data?.getQueryParameter("state")
        if (code != null && state != null) {
            try {
                ioScope.launch {
                    passage.hosted.finish(code, state)
                    val loginFragment = currentFragment as? LoginFragment ?: return@launch
                    loginFragment.handleDeepLink()
                }
            } catch (e: Exception) {
                // Report the error
            }
        }
    }
}

fun FragmentActivity.showAlert(
    title: String,
    message: String,
) {
    val dialogBuilder = AlertDialog.Builder(this)
    dialogBuilder
        .setMessage(message)
        .setCancelable(true)
        .setNegativeButton("Okay") { dialog, _ ->
            dialog.cancel()
        }
    val alert = dialogBuilder.create()
    alert.setTitle(title)
    alert.show()
}
