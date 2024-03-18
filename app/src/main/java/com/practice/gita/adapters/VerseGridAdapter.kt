package com.practice.gita.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.practice.gita.R
import com.practice.gita.databinding.ItemVerseGridBinding

class VerseGridAdapter(
    private val context: Context,
    private var list: List<Int>,
    private var mSelectedVerse: Int
) : RecyclerView.Adapter<VerseGridAdapter.ViewHolder>() {
    private lateinit var binding: ItemVerseGridBinding
    private var onItemClickListener: OnItemClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        binding = ItemVerseGridBinding.inflate(LayoutInflater.from(context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount() = list.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(position)
    }

    inner class ViewHolder(binding: ItemVerseGridBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(position: Int) {
            binding.root.text = list[position].toString()
            if (mSelectedVerse-1 == position) {
                binding.root.background =
                    ContextCompat.getDrawable(
                        context,
                        R.drawable.item_circular_color_accent_border
                    )
            } else {
                binding.root.background = null

            }
            binding.root.setOnClickListener {
                if (onItemClickListener != null) {
                    setSelectedPosition(position)
                    onItemClickListener!!.onClick(position+1)
                }
            }
        }
    }

    private fun setSelectedPosition(position: Int) {
        if (mSelectedVerse-1 != position) {
            notifyItemChanged(mSelectedVerse)
            mSelectedVerse = position
            notifyItemChanged(mSelectedVerse)
        }
    }

    fun setOnClickListener(onItemClickListener: OnItemClickListener) {
        this.onItemClickListener = onItemClickListener
    }

    interface OnItemClickListener {
        fun onClick(position: Int)
    }
}