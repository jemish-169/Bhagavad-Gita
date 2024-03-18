package com.practice.gita.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.practice.gita.data.ChapterSummaryItem
import com.practice.gita.databinding.SummaryItemRvBinding

class SummaryAdapter(
    private val context: Context,
    private val chapterList: List<ChapterSummaryItem>
) : RecyclerView.Adapter<SummaryAdapter.ViewHolder>() {

    private lateinit var binding: SummaryItemRvBinding
    private var onItemClickListener: OnItemClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SummaryAdapter.ViewHolder {
        binding = SummaryItemRvBinding.inflate(LayoutInflater.from(context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SummaryAdapter.ViewHolder, position: Int) {
        holder.bind(chapterList[position])
    }

    override fun getItemCount(): Int {
        return chapterList.size
    }

    inner class ViewHolder(private val binding: SummaryItemRvBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(chapterSummaryItem: ChapterSummaryItem) {
            binding.summaryChapterName.text =
                buildString {
                    append(chapterSummaryItem.chapter_number)
                    append(". ")
                    append(chapterSummaryItem.name)
                }
            binding.summaryChapterSummary.text = chapterSummaryItem.chapter_summary
            itemView.setOnClickListener {
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