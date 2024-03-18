package com.practice.gita.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.practice.gita.data.VerseItemDB
import com.practice.gita.databinding.BookmarkItemRvBinding

class BookMarkAdapter(
    private val context: Context,
    private val chapterList: List<VerseItemDB>,
) : RecyclerView.Adapter<BookMarkAdapter.ViewHolder>() {

    private lateinit var binding: BookmarkItemRvBinding
    private var onItemClickListener: OnItemClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookMarkAdapter.ViewHolder {
        binding = BookmarkItemRvBinding.inflate(LayoutInflater.from(context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: BookMarkAdapter.ViewHolder, position: Int) {
        holder.bind(chapterList[position])
    }

    override fun getItemCount(): Int {
        return chapterList.size
    }

    inner class ViewHolder(private val binding: BookmarkItemRvBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(verseItemDB: VerseItemDB) {
            binding.bookmarkItemNumber.text =
                buildString {
                    append(verseItemDB.chapter_number)
                    append(".")
                    append(verseItemDB.verse_number)
                }
            binding.bookmarkItemVerse.text = verseItemDB.text
            binding.root.setOnClickListener {
                if (onItemClickListener != null) {
                    onItemClickListener?.onClick(verseItemDB)
                }
            }
        }
    }

    fun setOnClickListener(onItemClickListener: OnItemClickListener) {
        this.onItemClickListener = onItemClickListener
    }

    interface OnItemClickListener {
        fun onClick(verseItemDB: VerseItemDB)
    }
}