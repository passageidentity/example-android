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
import id.passage.android.exceptions.LoginNoExistingUserException
import id.passage.android.exceptions.PassageException
import id.passage.android.exceptions.RegisterUserExistsException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class LoginFragment: Fragment(R.layout.fragment_login) {

    private lateinit var passage: Passage

    private lateinit var title: TextView
    private lateinit var editText: EditText
    private lateinit var continueButton: Button
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
        ioScope.launch {
            if (isShowingLogin) {
                login()
            } else {
                register()
            }
        }
    }

    private suspend fun login() {
        if (identifier.isEmpty()) return
        try {
            val (authResult, fallbackResult) = passage.login(identifier)
            if (authResult != null) {
                navigateToWelcome()
            } else if (fallbackResult != null) {
                handleFallbackAuthResult(fallbackResult)
            }
        } catch (e: PassageException) {
            handleLoginException(e)
        }
    }

    private suspend fun register() {
        if (identifier.isEmpty()) return
        try {
            val (authResult, fallbackResult) = passage.register(identifier)
            if (authResult != null) {
                navigateToWelcome()
            } else if (fallbackResult != null) {
                handleFallbackAuthResult(fallbackResult)
            }
        } catch (e: PassageException) {
            handleRegisterException(e)
        }
    }

    private fun attemptPasskeyAutofill() {
        ioScope.launch {
            passage.autofillPasskeyLogin() ?: return@launch
            navigateToWelcome()
        }
    }

    private fun handleFallbackAuthResult(fallbackResult: PassageAuthFallbackResult) {
        when (fallbackResult.method) {
            PassageAuthFallbackMethod.otp -> navigateToOTP(fallbackResult.id)
            PassageAuthFallbackMethod.magicLink -> navigateToMagicLink(fallbackResult.id)
            PassageAuthFallbackMethod.none -> Log.d("LoginFragment", "This will throw an error")
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

    private fun handleLoginException(e: PassageException) {
        when (e) {
            is LoginNoExistingUserException -> {
                uiScope.launch {
                    activity?.showAlert("Oops!", "User with this email or phone does not exist.")
                }
            }
            else -> {
                Log.e("LoginFragment", e.toString())
            }
        }
    }

    private fun handleRegisterException(e: PassageException) {
        when (e) {
            is RegisterUserExistsException -> {
                uiScope.launch {
                    activity?.showAlert("Oops!", "User with this email or phone already exists.")
                }
            }
            else -> {
                Log.e("LoginFragment", e.toString())
            }
        }
    }

}
