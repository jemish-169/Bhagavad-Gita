package com.practice.gita.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.practice.gita.data.ChapterSummaryItem
import com.practice.gita.databinding.HomeItemRvBinding
import com.practice.gita.utils.Constants.Companion.maxVersesNumberList

class HomeAdapter(
    private val context: Context,
    private val chapterList: List<ChapterSummaryItem>
) : RecyclerView.Adapter<HomeAdapter.ViewHolder>() {

    private lateinit var binding: HomeItemRvBinding
    private var onItemClickListener: OnItemClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HomeAdapter.ViewHolder {
        binding = HomeItemRvBinding.inflate(LayoutInflater.from(context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: HomeAdapter.ViewHolder, position: Int) {
        holder.bind(chapterList[position])
    }

    override fun getItemCount(): Int {
        return chapterList.size
    }

    inner class ViewHolder(private val binding: HomeItemRvBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(chapterSummaryItem: ChapterSummaryItem) {
            binding.homeChapterNumber.text = chapterSummaryItem.chapter_number.toString()
            binding.homeVerseCount.text =
                maxVersesNumberList[chapterSummaryItem.chapter_number-1].toString()
            binding.homeChapterName.text = chapterSummaryItem.name_translated
            binding.root.setOnClickListener {
                if (onItemClickListener != null) {
                    onItemClickListener?.onClick(chapterSummaryItem)
                }
            }
        }
    }

    fun setOnClickListener(onItemClickListener: OnItemClickListener) {
        this.onItemClickListener = onItemClickListener
    }

    interface OnItemClickListener {
        fun onClick(chapterSummaryItem: ChapterSummaryItem)
    }
}