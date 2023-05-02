package id.passage.example_android

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import id.passage.android.Passage

class WelcomeFragment: Fragment(R.layout.fragment_welcome) {

    private lateinit var passage: Passage

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        passage = Passage(requireActivity())
    }

}
