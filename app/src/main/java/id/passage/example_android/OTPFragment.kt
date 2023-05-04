package id.passage.example_android

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import id.passage.android.Passage
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
        ioScope.launch {
            val authResult = try {
                passage.oneTimePasscodeActivate(otp, args.otpId)
            } catch (e: Exception) {
                return@launch
            }
            navigateToWelcome()
        }
    }

    private fun resendOneTimePasscode() {
        ioScope.launch {
            if (args.isNewUser) {
                passage.newRegisterOneTimePasscode(args.identifier)
            } else {
                passage.newLoginOneTimePasscode(args.identifier)
            }
        }
    }

    private fun navigateToWelcome() {
        uiScope.launch {
            val action = OTPFragmentDirections.actionOTPFragmentToWelcomeFragment()
            findNavController().navigate(action)
        }
    }

}
