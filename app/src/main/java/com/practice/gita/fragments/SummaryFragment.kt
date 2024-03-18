package com.practice.gita.fragments

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.practice.gita.GitaApplication
import com.practice.gita.activities.MainActivity
import com.practice.gita.adapters.SummaryAdapter
import com.practice.gita.data.ChapterSummaryItem
import com.practice.gita.databinding.DialogVerseOpenBinding
import com.practice.gita.databinding.FragmentSummaryBinding
import com.practice.gita.utils.Constants.Companion.getImage
import com.practice.gita.utils.ResultOf
import com.practice.gita.viewModel.GitaViewModel
import com.practice.gita.viewModel.GitaViewModelFactory


class SummaryFragment : Fragment() {
    private var _binding: FragmentSummaryBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: GitaViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSummaryBinding.inflate(inflater, container, false)
        val repository = (requireActivity().application as GitaApplication).gitaRepository
        viewModel =
            ViewModelProvider(
                (activity as MainActivity),
                GitaViewModelFactory(repository)
            )[GitaViewModel::class.java]
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.getChapterSummary()
        binding.summaryLlError.setOnClickListener {
            viewModel.getChapterSummary()
        }
        bindObservers()
    }

    private fun bindObservers() {
        viewModel.chapterSummaryListLiveData.observe(viewLifecycleOwner) { result ->
            when (result) {
                is ResultOf.Loading -> {
                    binding.summaryListRv.visibility = View.GONE
                    binding.summaryProgressBar.visibility = View.VISIBLE
                    binding.summaryLlError.visibility = View.GONE

                }

                is ResultOf.Failure -> {
                    binding.summaryListRv.visibility = View.GONE
                    binding.summaryProgressBar.visibility = View.GONE
                    binding.summaryLlError.visibility = View.VISIBLE
                }

                is ResultOf.Success -> {
                    binding.summaryListRv.visibility = View.VISIBLE
                    binding.summaryProgressBar.visibility = View.GONE
                    binding.summaryLlError.visibility = View.GONE

                    val summaryAdapter =
                        SummaryAdapter(requireContext(), result.value)
                    binding.summaryListRv.adapter = summaryAdapter
                    binding.summaryListRv.scheduleLayoutAnimation()

                    summaryAdapter.setOnClickListener(object : SummaryAdapter.OnItemClickListener {
                        override fun onClick(chapterSummaryItem: ChapterSummaryItem) {
                            val dialogBinding = DialogVerseOpenBinding.inflate(layoutInflater)
                            val builder =
                                AlertDialog.Builder(this@SummaryFragment.context)
                                    .setView(dialogBinding.root)
                            dialogBinding.dialogVerseImg.setImageResource(
                                getImage(
                                    chapterSummaryItem.chapter_number
                                )
                            )
                            dialogBinding.dialogVerseSummary.text =
                                chapterSummaryItem.chapter_summary
                            dialogBinding.dialogVerseName.text = chapterSummaryItem.name
                            dialogBinding.dialogVerseMeaning.text = chapterSummaryItem.name_meaning
                            val dialog = builder.create()
                            dialog.show()
                            dialogBinding.dialogVerseClose.setOnClickListener {
                                dialog.dismiss()
                            }
                        }

                    })
                }
            }
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}