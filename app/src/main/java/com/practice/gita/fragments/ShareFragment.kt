package com.practice.gita.fragments

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.practice.gita.GitaApplication
import com.practice.gita.R
import com.practice.gita.activities.MainActivity
import com.practice.gita.adapters.ColorAdapter
import com.practice.gita.databinding.FragmentShareBinding
import com.practice.gita.utils.Constants.Companion.colorList
import com.practice.gita.utils.ResultOf
import com.practice.gita.viewModel.GitaViewModel
import com.practice.gita.viewModel.GitaViewModelFactory
import java.io.File
import java.io.FileOutputStream
import java.io.IOException


class ShareFragment : Fragment() {
    private var _binding: FragmentShareBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: GitaViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentShareBinding.inflate(inflater, container, false)
        val repository = (requireActivity().application as GitaApplication).gitaRepository
        viewModel = ViewModelProvider(
            (activity as MainActivity), GitaViewModelFactory(repository)
        )[GitaViewModel::class.java]

        val drawable = ContextCompat.getDrawable(
            binding.shareIamgeAppName.context,
            R.drawable.share_layout_app_name_background
        )
        drawable?.setTint(Color.parseColor(colorList[0].first))
        binding.shareIamgeAppName.background = drawable
        binding.shareLlImage.setBackgroundColor(Color.parseColor(colorList[0].first))
        binding.shareVerseText.setTextColor(Color.parseColor(colorList[0].second))

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setColorRecyclerView()

        binding.shareBackButton.setOnClickListener {
            findNavController().navigateUp()
        }
        binding.shareVerseRight.setOnClickListener {
            val image = binding.shareLlImage
            val bitmap = Bitmap.createBitmap(
                image.width, image.height, Bitmap.Config.ARGB_8888
            )
            val canvas = Canvas(bitmap)
            image.draw(canvas)

            try {
                val cachePath = File(requireActivity().applicationContext.cacheDir, "images")
                cachePath.mkdirs()
                val stream =
                    FileOutputStream("$cachePath/image.png")
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
                stream.close()

                val newFile = File(cachePath, "image.png")
                val contentUri =
                    FileProvider.getUriForFile(
                        requireActivity().applicationContext,
                        "com.practice.gita.fileProvider",
                        newFile
                    )

                if (contentUri != null) {
                    val shareIntent = Intent()
                    shareIntent.setAction(Intent.ACTION_SEND)
                    shareIntent.setType("image/png")
                    shareIntent.putExtra(Intent.EXTRA_STREAM, contentUri)
                    startActivity(Intent.createChooser(shareIntent, "Choose an app"))
                }

            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
        bindObservers()
    }

    private fun bindObservers() {
        viewModel.verseLiveData.observe(viewLifecycleOwner) { result ->
            if (result is ResultOf.Success) {
                binding.shareVerseText.text = result.value.text.trim()
                binding.shareVerseSlug.text =
                    getString(
                        R.string.chapter_verse,
                        result.value.chapter_number.toString(),
                        result.value.verse_number.toString()
                    )
            } else
                findNavController().navigateUp()
        }
    }

    private fun setColorRecyclerView() {
        val adapter = ColorAdapter(0, requireContext(), colorList)
        binding.shareColorRecyclerView.adapter = adapter
        adapter.setOnClickListener(object : ColorAdapter.OnItemClickListener {
            override fun onClick(color: Pair<String, String>) {
                val drawable = ContextCompat.getDrawable(
                    binding.shareIamgeAppName.context,
                    R.drawable.share_layout_app_name_background
                )
                drawable?.setTint(Color.parseColor(color.first))
                binding.shareIamgeAppName.background = drawable
                binding.shareLlImage.setBackgroundColor(Color.parseColor(color.first))
                binding.shareVerseText.setTextColor(Color.parseColor(color.second))
            }
        })
    }

}
