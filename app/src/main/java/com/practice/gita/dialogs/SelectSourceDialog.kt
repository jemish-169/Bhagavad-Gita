package com.practice.gita.dialogs

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import com.practice.gita.adapters.SourceAdapter
import com.practice.gita.databinding.DialogListBinding

abstract class SelectSourceDialog(
    context: Context,
    private var list: ArrayList<Pair<String,String>>,
    private val mSelectedWriter: String
) : Dialog(context) {

    private lateinit var binding: DialogListBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DialogListBinding.inflate(layoutInflater)

        setContentView(binding.root)
        setCancelable(true)
        setCanceledOnTouchOutside(true)
        setUpRecyclerview()
    }

    private fun setUpRecyclerview() {
        val adapter = SourceAdapter(context, list, mSelectedWriter)
        binding.dialogRvList.adapter = adapter
        adapter.setOnClickListener(object : SourceAdapter.OnItemClickListener {
            override fun onClick(position: Int, name: String) {
                dismiss()
                onItemSelected(name)
            }
        })
    }

    protected abstract fun onItemSelected(name: String)
}