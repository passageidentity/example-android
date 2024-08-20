package id.passage.example_android

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Html
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import id.passage.android.Passage
import id.passage.android.exceptions.GetMagicLinkStatusException
import id.passage.android.exceptions.GetMagicLinkStatusInvalidException
import id.passage.android.exceptions.MagicLinkActivateException
import id.passage.android.exceptions.MagicLinkActivateInvalidException
import id.passage.android.exceptions.MagicLinkLoginException
import id.passage.android.exceptions.MagicLinkRegisterException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MagicLinkFragment : Fragment(R.layout.fragment_magiclink) {
    private lateinit var passage: Passage

    private lateinit var resendButton: Button
    private lateinit var detailsTextView: TextView

    private val args: MagicLinkFragmentArgs by navArgs()
    private val mainHandler = Handler(Looper.getMainLooper())
    private val ioScope = CoroutineScope(Dispatchers.IO)
    private val uiScope = CoroutineScope(Dispatchers.Main)
    private var newMagicLinkId: String? = null

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)
        passage = Passage(requireActivity(), "CFKsqjyERaYC3DLqEexaJzKW")
        setupView(view)
        setupListeners()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mainHandler.removeCallbacksAndMessages(null)
    }

    private fun setupView(view: View) {
        resendButton = view.findViewById(R.id.resendButton)
        detailsTextView = view.findViewById(R.id.detailsTextView)
        val textString = "A one-time link has been sent to<br><b>${args.identifier}</b><br>\nYou will be logged in here once you click that link."
        detailsTextView.text = Html.fromHtml(textString)
    }

    private fun setupListeners() {
        resendButton.setOnClickListener {
            resendMagicLink()
        }
        mainHandler.post(
            object : Runnable {
                override fun run() {
                    checkMagicLinkStatus()
                    mainHandler.postDelayed(this, 1000)
                }
            },
        )
    }

    fun handleDeepLinkMagicLink(magicLink: String) {
        mainHandler.removeCallbacksAndMessages(null)
        ioScope.launch {
            try {
                passage.magicLink.activate(magicLink) ?: return@launch
            } catch (e: MagicLinkActivateException) {
                when (e) {
                    is MagicLinkActivateInvalidException -> handleInvalidMagicLink()
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
                    val magicLink = passage.magicLink.register(args.identifier)
                    newMagicLinkId = magicLink.id
                } catch (e: MagicLinkRegisterException) {
                    Log.e("MagicLinkFragment", e.toString())
                }
            } else {
                try {
                    val magicLink = passage.magicLink.login(args.identifier)
                    newMagicLinkId = magicLink.id
                } catch (e: MagicLinkLoginException) {
                    Log.e("MagicLinkFragment", e.toString())
                }
            }
        }
    }

    private fun checkMagicLinkStatus() {
        ioScope.launch {
            try {
                passage.magicLink.status(newMagicLinkId ?: args.magicLinkId) ?: return@launch
                navigateToWelcome()
            } catch (e: GetMagicLinkStatusException) {
                when (e) {
                    is GetMagicLinkStatusInvalidException -> handleInvalidMagicLink()
                    else -> Log.e("MagicLinkFragment", e.toString())
                }
            }
        }
    }

    private fun handleInvalidMagicLink() {
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
