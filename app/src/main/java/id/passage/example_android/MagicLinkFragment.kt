package id.passage.example_android

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import id.passage.android.MagicLink
import id.passage.android.Passage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MagicLinkFragment: Fragment(R.layout.fragment_magiclink) {

    private lateinit var passage: Passage

    private lateinit var resendButton: Button

    private val args: MagicLinkFragmentArgs by navArgs()
    private val mainHandler = Handler(Looper.getMainLooper())
    private val ioScope = CoroutineScope(Dispatchers.IO)
    private val uiScope = CoroutineScope(Dispatchers.Main)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        passage = Passage(requireActivity())

        resendButton = view.findViewById(R.id.resendButton)

        resendButton.setOnClickListener {
            resendMagicLink()
        }

        mainHandler.post(object : Runnable {
            override fun run() {
                checkMagicLinkStatus()
                mainHandler.postDelayed(this, 1000)
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mainHandler.removeCallbacksAndMessages(null)
    }

    fun handleDeepLinkMagicLink(magicLink: String) {
        mainHandler.removeCallbacksAndMessages(null)
        ioScope.launch {
            val authResult = passage.magicLinkActivate(magicLink) ?: return@launch
            navigateToWelcome()
        }
    }

    private fun resendMagicLink() {
        ioScope.launch {
            if (args.isNewUser) {
                passage.newRegisterMagicLink(args.identifier)
            } else {
                passage.newLoginMagicLink(args.identifier)
            }
        }
    }

    private fun checkMagicLinkStatus() {
        ioScope.launch {
            val authResult = passage.getMagicLinkStatus(args.magicLinkId) ?: return@launch
            navigateToWelcome()
        }
    }

    private fun navigateToWelcome() {
        uiScope.launch {
            val action = MagicLinkFragmentDirections.actionMagicLinkFragmentToWelcomeFragment()
            findNavController().navigate(action)
        }
    }

}
