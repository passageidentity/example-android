package id.passage.example_android

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import id.passage.android.Passage
import id.passage.android.exceptions.PassageTokenException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class WelcomeFragment: Fragment(R.layout.fragment_welcome) {

    private lateinit var passage: Passage

    private lateinit var logoutButton: Button
    private val uiScope = CoroutineScope(Dispatchers.Main)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        passage = Passage(requireActivity())

        logoutButton = view.findViewById(R.id.logoutButton)

        logoutButton.setOnClickListener {
            logOut()
        }
    }

    private fun logOut() {
        uiScope.launch {
            try {
                passage.signOutCurrentUser()
                findNavController().popBackStack()
            } catch (e: PassageTokenException) {
                Log.e("WelcomeFragment", e.toString())
            }
        }
    }

}
