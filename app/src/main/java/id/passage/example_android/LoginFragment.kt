package id.passage.example_android

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import id.passage.android.Passage
import id.passage.android.PassageAuthFallbackMethod
import id.passage.android.PassageAuthFallbackResult
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class LoginFragment: Fragment(R.layout.fragment_login) {

    private lateinit var title: TextView
    private lateinit var editText: EditText
    private lateinit var continueButton: Button
    private lateinit var switchButton: Button

    private lateinit var passage: Passage

    private val ioScope = CoroutineScope(Dispatchers.IO)
    private val uiScope = CoroutineScope(Dispatchers.Main)

    private var isShowingLogin = true
        set(value) {
            field = value
            if (value) {
                title.text = "Log In"
                switchButton.text = "Don't have an account? Register"
            } else {
                title.text = "Register"
                switchButton.text = "Already have an account? Log in"
            }
        }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupView(view)
        setupListeners()

        passage = Passage(requireActivity())

        ioScope.launch {
            val currentUser = passage.getCurrentUser()
            if (currentUser == null) {
                showLogin()
            } else {
                navigateToWelcome()
            }
        }

    }

    private fun setupView(view: View) {
        view.visibility = View.INVISIBLE
        title = view.findViewById(R.id.title)
        editText = view.findViewById(R.id.editText)
        continueButton = view.findViewById(R.id.continueButton)
        switchButton = view.findViewById(R.id.switchButton)
    }

    private fun setupListeners() {
        continueButton.setOnClickListener {
            onClickContinue()
        }
        switchButton.setOnClickListener {
            isShowingLogin = !isShowingLogin
        }
        editText.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus && isShowingLogin) {
                attemptPasskeyAutofill()
            }
        }
    }

    private fun showLogin() {
        uiScope.launch {
            isShowingLogin = true
            view?.visibility = View.VISIBLE
        }
    }

    private fun onClickContinue() {
        editText.clearFocus()
        val identifier = editText.text?.toString() ?: return
        ioScope.launch {
            try {
                val (authResult, fallbackResult) = if (isShowingLogin) {
                    passage.login(identifier)
                } else {
                    passage.register(identifier)
                }
                if (authResult != null) {
                    val currentUser = passage.getCurrentUser() ?: return@launch
                    navigateToWelcome()
                } else if (fallbackResult != null) {
                    handleFallbackAuthResult(fallbackResult)
                }
            } catch (e: Exception) {
                Log.e("example_app", e.message ?: e.toString())
            }
        }
    }

    private fun attemptPasskeyAutofill() {
        ioScope.launch {
            val authResult = passage.autofillPasskeyLogin() ?: return@launch
            val currentUser = passage.getCurrentUser() ?: return@launch
            navigateToWelcome()
        }
    }

    private fun handleFallbackAuthResult(fallbackResult: PassageAuthFallbackResult) {
        when (fallbackResult.method) {
            PassageAuthFallbackMethod.otp -> navigateToOTP(fallbackResult.id)
            PassageAuthFallbackMethod.magicLink -> navigateToMagicLink(fallbackResult.id)
            PassageAuthFallbackMethod.none -> Log.d("example_app", "This will throw an error")
        }
    }

    private fun navigateToWelcome() {
        uiScope.launch {
            val action = LoginFragmentDirections.actionLoginFragmentToWelcomeFragment()
            findNavController().navigate(action)
        }
    }

    private fun navigateToOTP(otpId: String) {
        uiScope.launch {
            val action = LoginFragmentDirections.actionLoginFragmentToOTPFragment(otpId)
            findNavController().navigate(action)
        }
    }

    private fun navigateToMagicLink(magicLinkId: String) {
        uiScope.launch {
            val isNewUser = !isShowingLogin
            val action = LoginFragmentDirections.actionLoginFragmentToMagicLinkFragment(
                isNewUser,
                magicLinkId
            )
            findNavController().navigate(action)
        }
    }

}
