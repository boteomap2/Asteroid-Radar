package com.udacity.asteroidradar.main

import android.os.Bundle
import android.view.*
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.udacity.asteroidradar.R
import com.udacity.asteroidradar.databinding.FragmentMainBinding

class MainFragment : Fragment(), MenuProvider {

    private val viewModel: MainViewModel by lazy {
        val factory = MainViewModel.Factory(requireActivity().application)
        ViewModelProvider(this, factory)[MainViewModel::class.java]
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        val binding = FragmentMainBinding.inflate(inflater)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel

        val adapter = AsteroidAdapter(AsteroidAdapter.OnItemClickListener {
            viewModel.asteroidClicked(it)
        })
        binding.asteroidRecycler.adapter = adapter

        viewModel.asteroidList.observe(viewLifecycleOwner) {
            adapter.submitList(it)
        }

        viewModel.selectedAsteroid.observe(viewLifecycleOwner) {
            it?.let {
                findNavController().navigate(MainFragmentDirections.actionShowDetail(it))
                viewModel.navigateDone()
            }
        }

        activity?.addMenuProvider(this, viewLifecycleOwner, Lifecycle.State.RESUMED)

        return binding.root
    }

    override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
        menuInflater.inflate(R.menu.main_overflow_menu, menu)
    }

    override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
        when (menuItem.itemId) {
            R.id.show_week_menu -> viewModel.onFilterChange(Filter.WEEK)
            R.id.show_today_menu -> viewModel.onFilterChange(Filter.TODAY)
            R.id.show_saved_menu -> viewModel.onFilterChange(Filter.SAVED)
        }
        return true
    }
}
