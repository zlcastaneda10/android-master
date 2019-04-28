package com.team8.locky.ui.mainmenu

import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.team8.locky.R

class MainMenuFragment : Fragment() {

    companion object {
        fun newInstance() = MainMenuFragment()
    }

    private lateinit var viewModel: MainMenuViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.main_menu_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(MainMenuViewModel::class.java)
        // TODO: Use the ViewModel
    }

}
