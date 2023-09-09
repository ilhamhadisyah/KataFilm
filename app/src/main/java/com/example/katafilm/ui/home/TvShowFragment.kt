package com.example.katafilm.ui.home

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.katafilm.databinding.FragmentTVShowBinding
import com.example.katafilm.model.Result
import com.example.katafilm.model.TrendingShowResponse
import com.example.katafilm.ui.detail.DetailActivity
import com.example.katafilm.ui.detail.ItemType
import com.example.katafilm.ui.home.adapter.ShowsAdapter

class TvShowFragment : Fragment() {

    private val viewModel by lazy {
        ViewModelProvider(requireActivity())[HomeViewModel::class.java]
    }
    private var _binding: FragmentTVShowBinding? = null
    private val binding get() = _binding!!
    private val moviesAdapter: ShowsAdapter by lazy {
        ShowsAdapter( arrayListOf())
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTVShowBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.fetchShows()
        binding.rvTvShow.adapter = moviesAdapter
        binding.rvTvShow.layoutManager = LinearLayoutManager(requireContext())
        moviesAdapter.setClickListener { id->
            val intent = Intent(requireContext(), DetailActivity::class.java)
            intent.putExtra(DetailActivity.ITEM_ID, id)
            intent.putExtra(DetailActivity.ITEM_TYPE, ItemType.TV_SHOW)
            context?.startActivity(intent)
        }
        viewModel.tvShowList.observe(
            viewLifecycleOwner,
            ::onGetMovieList
        )
    }

    private fun onGetMovieList(result: Result<TrendingShowResponse>) {
        when (result.status) {
            Result.Status.SUCCESS -> {
                result.data?.results?.let { list ->
                    moviesAdapter.updateData(list)
                }
                binding.shimmerAsProgressbar.apply {
                    visibility = View.GONE
                    stopShimmer()
                }
            }

            Result.Status.ERROR -> {
                result.message?.let {
                    Toast.makeText(context, "Error occurred", Toast.LENGTH_SHORT).show()
                }
                binding.shimmerAsProgressbar.apply {
                    visibility = View.GONE
                    stopShimmer()
                }
            }

            Result.Status.LOADING -> {
                binding.shimmerAsProgressbar.apply {
                    visibility = View.VISIBLE
                    startShimmer()
                }
            }
        }
    }
}