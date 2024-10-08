package id.passage.example_android

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import id.passage.android.Passage
import id.passage.android.exceptions.AddDevicePasskeyCancellationException
import id.passage.android.exceptions.AddDevicePasskeyException
import id.passage.android.exceptions.PassageTokenException
import id.passage.android.utils.CurrentUserInfo
import id.passage.android.utils.Passkey
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

class WelcomeFragment : Fragment(R.layout.fragment_welcome) {
    private lateinit var passage: Passage
    private var user: CurrentUserInfo? = null

    private lateinit var identifierTextView: TextView
    private lateinit var userPasskeysTextView: TextView
    private lateinit var devicesLinearLayout: LinearLayoutCompat
    private lateinit var addPasskeyButton: Button
    private lateinit var logoutButton: Button

    private val uiScope = CoroutineScope(Dispatchers.Main)
    private val ioScope = CoroutineScope(Dispatchers.IO)

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)

        passage = Passage(requireActivity(), "YOUR_APP_ID")

        setupView(view)
        setupListeners()
        getUser()
    }

    private fun setupView(view: View) {
        identifierTextView = view.findViewById(R.id.identifierTextView)
        userPasskeysTextView = view.findViewById(R.id.userPasskeysTextView)
        devicesLinearLayout = view.findViewById(R.id.devicesLinearLayout)
        addPasskeyButton = view.findViewById(R.id.addPasskeyButton)
        logoutButton = view.findViewById(R.id.logoutButton)
    }

    private fun setupListeners() {
        addPasskeyButton.setOnClickListener {
            addPasskey()
        }
        logoutButton.setOnClickListener {
            logOut()
        }
    }

    private fun getUser() {
        ioScope.launch {
            try {
                user = passage.currentUser.userInfo()
                user?.let {
                    displayUserInfo()
                } ?: logOut()
            } catch (e: Exception) {
                logOut()
            }
        }
    }

    private fun displayUserInfo() {
        val user = user ?: return
        uiScope.launch {
            // Display user identifier
            identifierTextView.text = user.email ?: user.phone ?: ""
            // Display user passkeys with created at dates
            user.webauthnDevices.forEach { appendPasskeyToList(it) }
        }
    }

    private fun createTextView(text: String): TextView {
        val textView = TextView(activity)
        textView.layoutParams =
            LinearLayoutCompat.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT,
            )
        textView.textAlignment = TextView.TEXT_ALIGNMENT_CENTER
        textView.text = text
        return textView
    }

    private fun formatDate(date: String): String {
        val formatter = DateTimeFormatter.ofPattern("MMMM d, yyyy", Locale.ENGLISH)
        val dateTime = LocalDateTime.parse(date, DateTimeFormatter.ISO_DATE_TIME)
        return formatter.format(dateTime.toLocalDate())
    }

    private fun addPasskey() {
        val user = user ?: return
        ioScope.launch {
            try {
                val webauthnDevice = passage.currentUser.addPasskey() ?: return@launch
                appendPasskeyToList(webauthnDevice)
            } catch (e: AddDevicePasskeyException) {
                when (e) {
                    is AddDevicePasskeyCancellationException -> {
                        // Do nothing
                    }
                    else -> {
                        uiScope.launch {
                            activity?.showAlert("Oops!", "Problem adding passkey. Please try again.")
                        }
                    }
                }
            }
        }
    }

    private fun appendPasskeyToList(passkey: Passkey) {
        uiScope.launch {
            var text = "Unnamed credential"
            passkey.friendlyName?.let { text = it }
            passkey.createdAt?.let { text += " created ${formatDate(it)}" }
            val textView = createTextView(text)
            devicesLinearLayout.addView(textView)
        }
    }

    private fun logOut() {
        uiScope.launch {
            try {
                passage.currentUser.logout()
                findNavController().popBackStack(R.id.loginFragment, false)
            } catch (e: PassageTokenException) {
                Log.e("WelcomeFragment", e.toString())
            }
        }
    }

}
