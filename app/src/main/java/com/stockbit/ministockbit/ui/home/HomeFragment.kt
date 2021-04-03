package com.stockbit.ministockbit.ui.home

import android.content.res.Configuration
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import com.stockbit.ministockbit.R
import com.stockbit.ministockbit.databinding.FragmentHomeBinding
import com.stockbit.ministockbit.ui.main.MainActivity
import com.stockbit.ministockbit.util.KeepStateNavigator


class HomeFragment : Fragment() {
    private lateinit var drawerToggle: ActionBarDrawerToggle

    private lateinit var binding: FragmentHomeBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = FragmentHomeBinding.inflate(layoutInflater, container, false)
        .also { binding = it }.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val navHostFragment = childFragmentManager.findFragmentById(R.id.homeNavHostFragment)
        val navigator = KeepStateNavigator(
            requireContext(),
            navHostFragment!!.childFragmentManager,
            R.id.homeNavHostFragment
        )
        val navController = navHostFragment.findNavController()
        navController.navigatorProvider.addNavigator(navigator)
        navController.setGraph(R.navigation.home_nav_graph)
        binding.bottomNavigation.setupWithNavController(navHostFragment.findNavController())
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val activity: MainActivity = this.activity as MainActivity
        activity.setSupportActionBar(binding.toolbar)
        activity.supportActionBar!!.apply {
            displayOptions = ActionBar.DISPLAY_USE_LOGO
            setDisplayHomeAsUpEnabled(true)
        }
        drawerToggle = ActionBarDrawerToggle(
            activity,
            binding.drawerLayout,
            binding.toolbar,
            R.string.open_drawer_description,
            R.string.close_drawer_description
        )
        binding.drawerLayout.addDrawerListener(drawerToggle)
        drawerToggle.syncState()
    }


    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        drawerToggle.onConfigurationChanged(newConfig)
    }
}