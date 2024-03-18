package com.practice.gita.fragments

import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.practice.gita.R
import com.practice.gita.databinding.FragmentSettingBinding
import com.practice.gita.dialogs.SelectSourceDialog
import com.practice.gita.utils.Constants.Companion.commentaryWriters
import com.practice.gita.utils.Constants.Companion.getAuthors
import com.practice.gita.utils.Constants.Companion.setAuthors
import com.practice.gita.utils.Constants.Companion.translationWriters


class SettingFragment : Fragment() {
    private var _binding: FragmentSettingBinding? = null
    private val binding get() = _binding!!

    private var mSelectedCommentaryWriter = ""
    private var mSelectedTranslationWriter = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSettingBinding.inflate(inflater, container, false)

        var versionName = ""
        try {
            val packageInfo =
                requireActivity().packageManager.getPackageInfo(requireActivity().packageName, 0)
            versionName = packageInfo.versionName
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }
        binding.settingVersionName.text = versionName

        binding.settingsLlShareApp.setOnClickListener {
            val appName = getString(R.string.app_name)
            val textIntent = Intent(Intent.ACTION_SEND)
            textIntent.type = "text/plain"

            textIntent.putExtra(Intent.EXTRA_SUBJECT, appName)
            val shareText = getString(R.string.share_app_text, requireActivity().packageName)
            textIntent.putExtra(Intent.EXTRA_TEXT, shareText)

            val chooserIntent = Intent.createChooser(textIntent, appName)
            startActivity(chooserIntent)
        }
        binding.settingsLlPrivacyPolicy.setOnClickListener {
            startActivity(
                Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse(resources.getString(R.string.privacy_policy_link))
                )
            )
        }
        binding.settingsLlDataSource.setOnClickListener {
            startActivity(
                Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse(resources.getString(R.string.data_source_link))
                )
            )
        }
        val authors = getAuthors(requireActivity().applicationContext)
        mSelectedCommentaryWriter = authors.first
        mSelectedTranslationWriter = authors.second

        binding.commentaryWriter.text = mSelectedCommentaryWriter
        binding.translationWriter.text = mSelectedTranslationWriter

        binding.translationWriter.setOnClickListener {
            val listDialog = object : SelectSourceDialog(
                requireContext(),
                translationWriters,
                mSelectedTranslationWriter
            ) {
                override fun onItemSelected(name: String) {
                    mSelectedTranslationWriter = name
                    binding.translationWriter.text = name
                    setWriters()
                }
            }
            listDialog.show()
        }
        binding.commentaryWriter.setOnClickListener {
            val listDialog = object : SelectSourceDialog(
                requireContext(),
                commentaryWriters,
                mSelectedCommentaryWriter
            ) {
                override fun onItemSelected(name: String) {
                    mSelectedCommentaryWriter = name
                    binding.commentaryWriter.text = name
                    setWriters()
                }
            }
            listDialog.show()
        }

        return binding.root
    }

    private fun setWriters() {
        setAuthors(
            mSelectedCommentaryWriter,
            mSelectedTranslationWriter,
            requireContext()
        )
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
}