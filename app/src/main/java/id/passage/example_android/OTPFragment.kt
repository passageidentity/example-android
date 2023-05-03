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
        val otpId = args.otpId
        CoroutineScope(Dispatchers.IO).launch {
            val authResult = try {
                passage.oneTimePasscodeActivate(otp, otpId)
            } catch (e: Exception) {
                return@launch
            }
            navigateToWelcome()
        }
    }

    private fun navigateToWelcome() {
        CoroutineScope(Dispatchers.Main).launch {
            val action = OTPFragmentDirections.actionOTPFragmentToWelcomeFragment()
            findNavController().navigate(action)
        }
    }

}
