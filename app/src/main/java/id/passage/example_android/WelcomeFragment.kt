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
import id.passage.android.PassageUser
import id.passage.android.exceptions.PassageTokenException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

class WelcomeFragment: Fragment(R.layout.fragment_welcome) {

    private lateinit var passage: Passage

    private lateinit var logoutButton: Button
    private lateinit var emailTextView: TextView
    private lateinit var devicesLinearLayout: LinearLayoutCompat

    private val uiScope = CoroutineScope(Dispatchers.Main)
    private val ioScope = CoroutineScope(Dispatchers.IO)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        passage = Passage(requireActivity())

        setupView(view)
        setupListeners()
        getUser()
    }

    private fun setupView(view: View) {
        logoutButton = view.findViewById(R.id.logoutButton)
        emailTextView = view.findViewById(R.id.emailTextView)
        devicesLinearLayout = view.findViewById(R.id.devicesLinearLayout)
    }

    private fun setupListeners() {
        logoutButton.setOnClickListener {
            logOut()
        }
    }

    private fun getUser() {
        ioScope.launch {
            val user = passage.getCurrentUser()
            user?.let {
                displayUserInfo(it)
            } ?: logOut()
        }
    }

    private fun displayUserInfo(user: PassageUser) {
        uiScope.launch {
            // Display user identifier
            emailTextView.text = user.email ?: user.phone ?: ""
            // Display user passkeys with created at dates
            user.webauthnDevices?.forEach { device ->
                var text = "Unnamed device"
                device.friendlyName?.let { text = it }
                device.createdAt?.let { text += " created ${formatDate(it)}" }
                val textView = createTextView(text)
                devicesLinearLayout.addView(textView)
            }
        }
    }

    private fun createTextView(text: String): TextView {
        val textView = TextView(activity)
        textView.layoutParams = LinearLayoutCompat.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
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

    private fun logOut() {
        uiScope.launch {
            try {
                passage.signOutCurrentUser()
                findNavController().popBackStack(R.id.loginFragment, false)
            } catch (e: PassageTokenException) {
                Log.e("WelcomeFragment", e.toString())
            }
        }
    }

}
