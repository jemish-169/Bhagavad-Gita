package com.practice.gita.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.practice.gita.GitaApplication
import com.practice.gita.R
import com.practice.gita.activities.MainActivity
import com.practice.gita.adapters.HomeAdapter
import com.practice.gita.data.ChapterSummaryItem
import com.practice.gita.databinding.FragmentHomeBinding
import com.practice.gita.utils.Constants.Companion.getLastRead
import com.practice.gita.utils.Constants.Companion.maxVersesNumberList
import com.practice.gita.utils.ResultOf
import com.practice.gita.viewModel.GitaViewModel
import com.practice.gita.viewModel.GitaViewModelFactory


class HomeFragment : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: GitaViewModel
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)

        val repository = (requireActivity().application as GitaApplication).gitaRepository

        viewModel = ViewModelProvider(
            (activity as MainActivity),
            GitaViewModelFactory(repository)
        )[GitaViewModel::class.java]

        viewModel.getChapterSummary()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.homeTryAgain.setOnClickListener { viewModel.getChapterSummary() }
        binding.lastReadCv.setOnClickListener {
            viewModel.loadingState()
            findNavController().navigate(R.id.action_homeFragment_to_verseFragment)
        }
        bindObservers()
    }

    private fun bindObservers() {

        viewModel.chapterSummaryListLiveData.observe(viewLifecycleOwner) { result ->
            when (result) {
                is ResultOf.Loading -> {
                    binding.homeListRv.visibility = View.GONE
                    binding.homeProgressBar.visibility = View.VISIBLE
                    binding.homeLlError.visibility = View.GONE
                }

                is ResultOf.Failure -> {
                    binding.homeListRv.visibility = View.GONE
                    binding.homeProgressBar.visibility = View.GONE
                    binding.homeLlError.visibility = View.VISIBLE
                }

                is ResultOf.Success -> {
                    binding.homeListRv.visibility = View.VISIBLE
                    binding.homeProgressBar.visibility = View.GONE
                    binding.homeLlError.visibility = View.GONE
                    val homeAdapter =
                        HomeAdapter(requireContext(), result.value)
                    binding.homeListRv.adapter = homeAdapter
                    binding.homeListRv.scheduleLayoutAnimation()

                    homeAdapter.setOnClickListener(object : HomeAdapter.OnItemClickListener {
                        override fun onClick(chapterSummaryItem: ChapterSummaryItem) {
                            viewModel.loadingState()
                            viewModel.verse = 1
                            viewModel.chapter = chapterSummaryItem.chapter_number
                            viewModel.maxVerse =
                                maxVersesNumberList[chapterSummaryItem.chapter_number - 1]
                            viewModel.chapterSummaryItem = chapterSummaryItem
                            findNavController().navigate(R.id.action_homeFragment_to_verseFragment)
                        }
                    })
                }
            }
        }
    }

    override fun onResume() {
        val lastRead = getLastRead(requireContext())
        if (lastRead.third.isEmpty()) {
            binding.lastReadCv.visibility = View.GONE
            binding.homeLastReadLine.visibility = View.GONE
        }
        else {
            viewModel.chapter = lastRead.first
            viewModel.verse = lastRead.second
            viewModel.maxVerse = maxVersesNumberList[lastRead.first - 1]
            binding.lastReadItemVerse.text = lastRead.third
            binding.lastReadItemNumber.text =
                getString(R.string.last_read, lastRead.first.toString(), lastRead.second.toString())
        }
        super.onResume()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}