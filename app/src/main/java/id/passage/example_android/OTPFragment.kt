package id.passage.example_android

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import id.passage.android.Passage
import id.passage.android.exceptions.NewLoginOneTimePasscodeException
import id.passage.android.exceptions.NewRegisterOneTimePasscodeException
import id.passage.android.exceptions.OneTimePasscodeActivateException
import id.passage.android.exceptions.OneTimePasscodeActivateInvalidRequestException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class OTPFragment: Fragment(R.layout.fragment_otp) {

    private lateinit var passage: Passage

    private val args: OTPFragmentArgs by navArgs()

    private lateinit var editText: EditText
    private lateinit var continueButton: Button

    private val ioScope = CoroutineScope(Dispatchers.IO)
    private val uiScope = CoroutineScope(Dispatchers.Main)

    private var newOTPId: String? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        passage = Passage(requireActivity())

        editText = view.findViewById(R.id.editText)
        continueButton = view.findViewById(R.id.continueButton)

        continueButton.setOnClickListener {
            submitOneTimePasscode()
        }
    }

    private fun submitOneTimePasscode() {
        val otp = editText.text?.toString() ?: return
        val otpId = newOTPId ?: args.otpId
        ioScope.launch {
            try {
                passage.oneTimePasscodeActivate(otp, otpId)
            } catch (e: OneTimePasscodeActivateException) {
                handleActivationException(e)
                return@launch
            }
            navigateToWelcome()
        }
    }

    private fun resendOneTimePasscode() {
        ioScope.launch {
            if (args.isNewUser) {
                try {
                    val newOTP = passage.newRegisterOneTimePasscode(args.identifier)
                    newOTPId = newOTP.otpId
                } catch (e: NewRegisterOneTimePasscodeException) {
                    Log.e("OTPFragment", e.toString())
                }
            } else {
                try {
                    val newOTP = passage.newLoginOneTimePasscode(args.identifier)
                    newOTPId = newOTP.otpId
                } catch(e: NewLoginOneTimePasscodeException) {
                    Log.e("OTPFragment", e.toString())
                }
            }
        }
    }

    private fun navigateToWelcome() {
        uiScope.launch {
            val action = OTPFragmentDirections.actionOTPFragmentToWelcomeFragment()
            findNavController().navigate(action)
        }
    }

    private fun handleActivationException(e: OneTimePasscodeActivateException) {
        var alertTitle = "Something went wrong"
        val alertMessage = "Please try again."
        when (e) {
            is OneTimePasscodeActivateInvalidRequestException -> {
                alertTitle = "Invalid passcode"
            }
        }
        uiScope.launch {
            activity?.showAlert(alertTitle, alertMessage)
        }
    }

}
