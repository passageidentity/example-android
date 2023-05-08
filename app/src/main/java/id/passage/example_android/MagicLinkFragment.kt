package id.passage.example_android

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import id.passage.android.Passage
import id.passage.android.exceptions.GetMagicLinkStatusException
import id.passage.android.exceptions.GetMagicLinkStatusInvalidException
import id.passage.android.exceptions.MagicLinkActivateException
import id.passage.android.exceptions.MagicLinkActivateInvalidException
import id.passage.android.exceptions.NewLoginMagicLinkException
import id.passage.android.exceptions.NewRegisterMagicLinkException
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
            try {
                passage.magicLinkActivate(magicLink) ?: return@launch
            } catch (e: MagicLinkActivateException) {
                when (e) {
                    is MagicLinkActivateInvalidException -> handleInvalidMagicLinkn()
                    else -> Log.e("MagicLinkFragment", e.toString())
                }
            }
            navigateToWelcome()
        }
    }

    private fun resendMagicLink() {
        ioScope.launch {
            if (args.isNewUser) {
                try {
                    passage.newRegisterMagicLink(args.identifier)
                } catch (e: NewRegisterMagicLinkException) {
                    Log.e("MagicLinkFragment", e.toString())
                }
            } else {
                try {
                    passage.newLoginMagicLink(args.identifier)
                } catch (e: NewLoginMagicLinkException) {
                    Log.e("MagicLinkFragment", e.toString())
                }
            }
        }
    }

    private fun checkMagicLinkStatus() {
        ioScope.launch {
            try {
                passage.getMagicLinkStatus(args.magicLinkId) ?: return@launch
            } catch (e: GetMagicLinkStatusException) {
                when (e) {
                    is GetMagicLinkStatusInvalidException -> handleInvalidMagicLinkn()
                    else -> Log.e("MagicLinkFragment", e.toString())
                }
            }
            navigateToWelcome()
        }
    }

    private fun handleInvalidMagicLinkn() {
        mainHandler.removeCallbacksAndMessages(null)
        uiScope.launch {
            activity?.showAlert("Oops!", "Looks like this magic link is invalid.")
        }
    }

    private fun navigateToWelcome() {
        uiScope.launch {
            val action = MagicLinkFragmentDirections.actionMagicLinkFragmentToWelcomeFragment()
            findNavController().navigate(action)
        }
    }

}
