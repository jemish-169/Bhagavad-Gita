package com.practice.gita.adapters

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.practice.gita.R
import com.practice.gita.databinding.ItemShareColorBinding

class ColorAdapter(
    private var mSelectedPosition: Int,
    private val context: Context,
    private var list: ArrayList<Pair<String, String>>,
) :
    RecyclerView.Adapter<ColorAdapter.ViewHolder>() {

    private lateinit var binding: ItemShareColorBinding
    private var onItemClickListener: OnItemClickListener? = null


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        binding = ItemShareColorBinding.inflate(LayoutInflater.from(context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(position)
    }

    inner class ViewHolder(val binding: ItemShareColorBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(position: Int) {
            if (mSelectedPosition == position)
                binding.selectedColorUnderline.visibility = View.VISIBLE
            else
                binding.selectedColorUnderline.visibility = View.INVISIBLE
            val drawable = ContextCompat.getDrawable(
                context,
                R.drawable.round_item_share_color_background
            )
            drawable?.setTint(Color.parseColor(list[position].first))
            binding.itemViewToSetBack.background = drawable

            binding.root.setOnClickListener {
                if (onItemClickListener != null) {
                    setSelectedPosition(position)
                    onItemClickListener!!.onClick(list[position])
                }
            }
        }
    }

    private fun setSelectedPosition(position: Int) {
        if (mSelectedPosition != position) {
            notifyItemChanged(mSelectedPosition)
            mSelectedPosition = position
            notifyItemChanged(mSelectedPosition)
        }
    }

    fun setOnClickListener(onItemClickListener: OnItemClickListener) {
        this.onItemClickListener = onItemClickListener
    }

    interface OnItemClickListener {
        fun onClick(color: Pair<String, String>)
    }
}