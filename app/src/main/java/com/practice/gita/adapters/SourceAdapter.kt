package com.practice.gita.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.practice.gita.R
import com.practice.gita.databinding.SpinnerListItemBinding

class SourceAdapter(
    private val context: Context,
    private var list: ArrayList<Pair<String, String>>,
    private val mSelectedName: String
) :
    RecyclerView.Adapter<SourceAdapter.ViewHolder>() {

    private var onItemClickListener: OnItemClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = SpinnerListItemBinding.inflate(LayoutInflater.from(context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(list[position])
    }

    inner class ViewHolder(val binding: SpinnerListItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Pair<String,String>) {
            if (list.size == 7)
                binding.writerLanguage.text =
                    context.getString(
                        R.string.writer_name_in_language,
                        item.first,
                        "Translation",
                        item.second
                    )
            else
                binding.writerLanguage.text =
                    context.getString(
                        R.string.writer_name_in_language,
                        item.first,
                        "Commentary",
                        item.second
                    )
            if (item.second == mSelectedName) binding.writerNameSelected.visibility =
                View.VISIBLE

            binding.root.setOnClickListener {
                if (onItemClickListener != null) {
                    onItemClickListener!!.onClick(position, item.second)
                }
            }
        }
    }

    fun setOnClickListener(onItemClickListener: OnItemClickListener) {
        this.onItemClickListener = onItemClickListener
    }

    interface OnItemClickListener {
        fun onClick(position: Int, name: String)
    }
}