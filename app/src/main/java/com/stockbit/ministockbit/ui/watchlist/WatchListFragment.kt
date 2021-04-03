package com.stockbit.ministockbit.ui.watchlist

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.stockbit.ministockbit.databinding.FragmentWatchListBinding
import org.koin.android.viewmodel.ext.android.viewModel

class WatchListFragment : Fragment() {
    private lateinit var binding: FragmentWatchListBinding
    private val watchListAdapter = WatchListAdapter()
    private val viewModel: WatchListViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (savedInstanceState == null) viewModel.load()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentWatchListBinding.inflate(layoutInflater, container, false)
        binding.recyclerView.adapter = watchListAdapter
        watchListAdapter.onLoadMoreListener = { viewModel.load() }
        watchListAdapter.onReloadClickListener = { viewModel.load() }
        viewModel.state.observe(viewLifecycleOwner) {
            it.data.consume { data -> watchListAdapter.update(data) }
            it.error?.consume { message ->
                Toast.makeText(
                    requireContext(),
                    message,
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
        binding.swipeRefresh.setOnRefreshListener {
            viewModel.load(false)
            binding.swipeRefresh.isRefreshing = false
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (savedInstanceState != null) viewModel.state.value?.data?.peek()?.let {
            watchListAdapter.update(it)
        }
    }
}