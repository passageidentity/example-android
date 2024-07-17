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
import id.passage.android.exceptions.PassageException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class LoginFragment: Fragment(R.layout.fragment_login) {

    private lateinit var passage: Passage

    private lateinit var title: TextView
    private lateinit var editText: EditText
    private lateinit var authWithPasskeysButton: Button
    private lateinit var authWithOTPButton: Button
    private lateinit var authWithMagiclinkButton: Button
    private lateinit var switchButton: Button

    private val ioScope = CoroutineScope(Dispatchers.IO)
    private val uiScope = CoroutineScope(Dispatchers.Main)
    private val identifier: String get() = editText.text?.toString() ?: ""

    private var isShowingLogin = true
        set(value) {
            field = value
            if (value) {
                title.text = "Log In"
                switchButton.text = "Don't have an account? Register"
                authWithPasskeysButton.text = "Login with Passkeys"
                authWithOTPButton.text = "Login with One-Time Passcode"
                authWithMagiclinkButton.text = "Login with Magic link"
            } else {
                title.text = "Register"
                switchButton.text = "Already have an account? Log in"
                authWithPasskeysButton.text = "Register with Passkeys"
                authWithOTPButton.text = "Register with One-Time Passcode"
                authWithMagiclinkButton.text = "Register with Magic link"
            }
        }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupView(view)
        setupListeners()

        passage = Passage(requireActivity())
        passage.overrideBasePath("https://auth-uat.passage.dev/v1")

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
        authWithPasskeysButton = view.findViewById(R.id.authWithPasskeysButton)
        authWithOTPButton = view.findViewById(R.id.authWithOTPButton)
        authWithMagiclinkButton = view.findViewById(R.id.authWithMagicLinkButton)
        switchButton = view.findViewById(R.id.switchButton)
    }

    private fun setupListeners() {
        authWithPasskeysButton.setOnClickListener {
            onClickWithPasskeys()
        }
        authWithOTPButton.setOnClickListener {
            onClickWithOTP()
        }
        authWithMagiclinkButton.setOnClickListener {
//            onClickWithMagicLink()
            onClickHosted()
        }
        switchButton.setOnClickListener {
            isShowingLogin = !isShowingLogin
        }
    }

    private fun showLogin() {
        uiScope.launch {
            isShowingLogin = true
            view?.visibility = View.VISIBLE
        }
    }

    private fun onClickWithPasskeys() {
        editText.clearFocus()
        if (identifier.isEmpty()) return
        ioScope.launch {
            try {
                if (isShowingLogin) {
                    passage.loginWithPasskey(identifier)
                } else {
                    passage.registerWithPasskey(identifier)
                }
                navigateToWelcome()
            } catch (e: Exception) {
                handleException(e)
            }
        }
    }

    private fun onClickWithOTP() {
        editText.clearFocus()
        if (identifier.isEmpty()) return
        ioScope.launch {
            try {
                if (isShowingLogin) {
                    val otp = passage.newLoginOneTimePasscode(identifier)
                    navigateToOTP(otp.otpId)
                } else {
                    val otp = passage.newRegisterOneTimePasscode(identifier)
                    navigateToOTP(otp.otpId)
                }
            } catch (e: Exception) {
                handleException(e)
            }
        }
    }

    private fun onClickWithMagicLink() {
        editText.clearFocus()
        if (identifier.isEmpty()) return
        ioScope.launch {
            try {
                if (isShowingLogin) {
                    val magicLink = passage.newLoginMagicLink(identifier)
                    navigateToMagicLink(magicLink.id)
                } else {
                    val magicLink = passage.newRegisterMagicLink(identifier)
                    navigateToMagicLink(magicLink.id)
                }
            } catch (e: Exception) {
                handleException(e)
            }
        }
    }

    private fun onClickHosted() {
        uiScope.launch {
            try {
                passage.hostedAuthStart()
            } catch (e: Exception) {
                handleException(e)
            }
        }
    }

    private fun navigateToWelcome() {
        uiScope.launch {
            editText.setText("")
            val action = LoginFragmentDirections.actionLoginFragmentToWelcomeFragment()
            findNavController().navigate(action)
        }
    }

    private fun navigateToOTP(otpId: String) {
        uiScope.launch {
            val isNewUser = !isShowingLogin
            val action = LoginFragmentDirections.actionLoginFragmentToOTPFragment(
                otpId,
                identifier,
                isNewUser
            )
            findNavController().navigate(action)
        }
    }

    private fun navigateToMagicLink(magicLinkId: String) {
        uiScope.launch {
            val isNewUser = !isShowingLogin
            val action = LoginFragmentDirections.actionLoginFragmentToMagicLinkFragment(
                magicLinkId,
                identifier,
                isNewUser
            )
            findNavController().navigate(action)
        }
    }

    private fun handleException(e: Exception) {
        when (e) {
            is PassageException -> {
                uiScope.launch {
                    activity?.showAlert("Oops!", e.message.toString())
                }
            }
            else -> {
                Log.e("LoginFragment", e.toString())
            }
        }
    }

}
