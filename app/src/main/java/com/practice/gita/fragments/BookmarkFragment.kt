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
import com.practice.gita.adapters.BookMarkAdapter
import com.practice.gita.data.VerseItemDB
import com.practice.gita.databinding.FragmentBookmarkBinding
import com.practice.gita.utils.Constants
import com.practice.gita.utils.ResultOf
import com.practice.gita.viewModel.GitaViewModel
import com.practice.gita.viewModel.GitaViewModelFactory


class BookmarkFragment : Fragment() {

    private var _binding: FragmentBookmarkBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: GitaViewModel
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBookmarkBinding.inflate(inflater, container, false)

        val repository = (requireActivity().application as GitaApplication).gitaRepository

        viewModel = ViewModelProvider(
            (activity as MainActivity),
            GitaViewModelFactory(repository)
        )[GitaViewModel::class.java]

        viewModel.getBookmark()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.bookmarkTryAgain.setOnClickListener { viewModel.getBookmark() }

        bindObserver()
    }

    private fun bindObserver() {
        viewModel.bookmarkLiveData.observe(viewLifecycleOwner) { result ->
            when (result) {
                is ResultOf.Loading -> {
                    binding.bookmarkListRv.visibility = View.GONE
                    binding.bookmarkLlError.visibility = View.GONE
                    binding.bookmarkProgressBar.visibility = View.VISIBLE
                    binding.bookmarkTvNoBookmarks.visibility = View.GONE
                }

                is ResultOf.Failure -> {
                    binding.bookmarkListRv.visibility = View.GONE
                    binding.bookmarkLlError.visibility = View.VISIBLE
                    binding.bookmarkProgressBar.visibility = View.GONE
                    binding.bookmarkTvNoBookmarks.visibility = View.GONE
                }

                is ResultOf.Success -> {
                    binding.bookmarkLlError.visibility = View.GONE
                    binding.bookmarkProgressBar.visibility = View.GONE
                    val list = result.value
                    if (list.isNotEmpty()) {
                        binding.bookmarkListRv.visibility = View.VISIBLE
                        binding.bookmarkTvNoBookmarks.visibility = View.GONE
                        val adapter = BookMarkAdapter(requireContext(), list)
                        binding.bookmarkListRv.adapter = adapter
                        binding.bookmarkListRv.scheduleLayoutAnimation()
                        adapter.setOnClickListener(object : BookMarkAdapter.OnItemClickListener {
                            override fun onClick(verseItemDB: VerseItemDB) {
                                viewModel.loadingState()
                                viewModel.verse = verseItemDB.verse_number
                                viewModel.chapter = verseItemDB.chapter_number
                                viewModel.maxVerse =
                                    Constants.maxVersesNumberList[verseItemDB.chapter_number - 1]
                                findNavController().navigate(R.id.action_bookmarkFragment_to_verseFragment)
                            }
                        })
                    } else {
                        binding.bookmarkListRv.visibility = View.GONE
                        binding.bookmarkTvNoBookmarks.visibility = View.VISIBLE
                    }
                }
            }
        }
    }
}