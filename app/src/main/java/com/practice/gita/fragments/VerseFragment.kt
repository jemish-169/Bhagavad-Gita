package com.practice.gita.fragments

import android.content.Intent
import android.content.pm.PackageManager
import android.media.MediaPlayer
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.activity.OnBackPressedCallback
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.practice.gita.GitaApplication
import com.practice.gita.R
import com.practice.gita.activities.MainActivity
import com.practice.gita.adapters.VerseGridAdapter
import com.practice.gita.databinding.FragmentVerseBinding
import com.practice.gita.utils.Constants.Companion.ENGINE_NAME
import com.practice.gita.utils.Constants.Companion.TTS_LANGUAGE
import com.practice.gita.utils.Constants.Companion.getAudioURL
import com.practice.gita.utils.Constants.Companion.getAuthors
import com.practice.gita.utils.Constants.Companion.setLastRead
import com.practice.gita.utils.ResultOf
import com.practice.gita.viewModel.GitaViewModel
import com.practice.gita.viewModel.GitaViewModelFactory
import java.io.IOException


class VerseFragment : Fragment() {
    private var _binding: FragmentVerseBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: GitaViewModel
    private var id = 1
    private var isLeft: Boolean? = false
    private var mediaPlayer: MediaPlayer? = null
    private lateinit var googleTTS: TextToSpeech

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentVerseBinding.inflate(inflater, container, false)
        val repository = (requireActivity().application as GitaApplication).gitaRepository
        viewModel = ViewModelProvider(
            (activity as MainActivity), GitaViewModelFactory(repository)
        )[GitaViewModel::class.java]

        if (isGoogleEngineInstalled()) {
            createGoogleTTS()
        }

        val callBack = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (binding.verseGridTotalCount.isVisible) {
                    binding.verseGridTotalCount.visibility = View.GONE
                    binding.verseFabPlayAudio.visibility = View.VISIBLE
                } else
                    findNavController().navigateUp()
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callBack)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val authors = getAuthors(requireActivity().applicationContext)

        binding.verseBtnTryAgain.setOnClickListener {
            viewModel.loadingState()
            viewModel.getVerse(authors.first, authors.second)
        }

        binding.verseBackButton.setOnClickListener {
            findNavController().navigateUp()
        }

        viewModel.getVerse(authors.first, authors.second)

        bindObservers()

        binding.verseLeftArrow.setOnClickListener {
            isLeft = true
            viewModel.loadingState()
            --viewModel.verse
            viewModel.getVerse(authors.first, authors.second)
            stopAudio()
        }
        binding.verseRightArrow.setOnClickListener {
            isLeft = false
            viewModel.loadingState()
            ++viewModel.verse
            viewModel.getVerse(authors.first, authors.second)
            stopAudio()
        }

        binding.verseFlBookmark.setOnClickListener {
            val bookmarkButton = binding.verseBookmark
            if (bookmarkButton.isSelected) bookmarkButton.setImageResource(R.drawable.bookmark_icon)
            else {
                bookmarkButton.setImageResource(R.drawable.filled_bookmark)
            }
            viewModel.updateVerse(id, !bookmarkButton.isSelected)
            bookmarkButton.isSelected = !bookmarkButton.isSelected
        }

        binding.shareVerse.setOnClickListener {
            isLeft = true
            findNavController().navigate(R.id.action_verseFragment_to_shareFragment)
        }

        binding.verseSeeList.setOnClickListener {
            if (binding.verseGridTotalCount.isVisible) {
                binding.verseGridTotalCount.visibility = View.GONE
                binding.verseFabPlayAudio.visibility = View.VISIBLE
            } else {
                setRecyclerView(authors)
                binding.verseGridTotalCount.scheduleLayoutAnimation()
                binding.verseGridTotalCount.visibility = View.VISIBLE
                binding.verseFabPlayAudio.visibility = View.GONE
            }

        }

        binding.verseFabPlayAudio.setOnClickListener {
            if (googleTTS.isSpeaking || mediaPlayer != null) {
                stopAudio()
            } else {
                binding.verseFabPlayAudio.setImageResource(R.drawable.round_more_horiz_24)
                playAudioFromUrl()
            }
        }

    }

    private fun bindObservers() {
        viewModel.verseLiveData.observe(viewLifecycleOwner) { result ->

            when (result) {
                is ResultOf.Loading -> {
                    binding.verseLlError.visibility = View.GONE
                    binding.llCtrlButtons.visibility = View.GONE
                    binding.verseScrollView.visibility = View.GONE
                    binding.verseAppBar.visibility = View.GONE
                    binding.verseProgressBar.visibility = View.VISIBLE
                    binding.verseFabPlayAudio.visibility = View.GONE
                }

                is ResultOf.Failure -> {
                    binding.verseLlError.visibility = View.VISIBLE
                    binding.llCtrlButtons.visibility = View.GONE
                    binding.verseAppBar.visibility = View.GONE
                    binding.verseScrollView.visibility = View.GONE
                    binding.verseProgressBar.visibility = View.GONE
                    binding.verseFabPlayAudio.visibility = View.GONE
                }

                is ResultOf.Success -> {
                    binding.verseLlError.visibility = View.GONE
                    binding.verseScrollView.visibility = View.VISIBLE
                    binding.verseAppBar.visibility = View.VISIBLE
                    binding.verseProgressBar.visibility = View.GONE
                    binding.llCtrlButtons.visibility = View.VISIBLE
                    binding.verseFabPlayAudio.visibility = View.VISIBLE

                    if (isLeft == true)
                        binding.verseScrollView.startAnimation(
                            AnimationUtils.loadAnimation(
                                requireContext(),
                                R.anim.nav_slide_in_left
                            )
                        )
                    else if (isLeft == false)
                        binding.verseScrollView.startAnimation(
                            AnimationUtils.loadAnimation(
                                requireContext(),
                                R.anim.nav_slide_in_right
                            )
                        )
                    val verseItem = result.value
                    id = verseItem.id
                    when (viewModel.verse) {
                        1 -> {
                            binding.verseLeftArrow.visibility = View.INVISIBLE
                        }

                        viewModel.maxVerse -> {
                            binding.verseRightArrow.visibility = View.INVISIBLE
                        }

                        else -> {
                            binding.verseLeftArrow.visibility = View.VISIBLE
                            binding.verseRightArrow.visibility = View.VISIBLE
                        }
                    }
                    binding.verseCommentary.text = verseItem.commentary.description.trim()
                    binding.verseCommentaryAuthor.text = verseItem.commentary.author_name.trim()
                    binding.verseTranslationAuthor.text = verseItem.translation.author_name.trim()
                    binding.verseTranslation.text = verseItem.translation.description.trim()
                    binding.verseSlug.text = getString(
                        R.string.chapter_verse,
                        result.value.chapter_number.toString(),
                        result.value.verse_number.toString()
                    )
                    binding.verseText.text = verseItem.text.trim()
                    binding.verseWordMeaning.text = verseItem.word_meanings.trim()
                    binding.verseTransliteration.text = verseItem.transliteration.trim()
                    binding.verseBookmark.isSelected = verseItem.isBookmarked
                    binding.verseBookmark.setImageResource(
                        if (verseItem.isBookmarked) {
                            R.drawable.filled_bookmark
                        } else R.drawable.bookmark_icon
                    )
                }
            }
        }
    }

    private fun setRecyclerView(authors: Pair<String, String>) {
        val list = (1..viewModel.maxVerse).toList()

        val adapter = VerseGridAdapter(requireContext(), list, viewModel.verse)
        binding.verseGridTotalCount.layoutManager =
            GridLayoutManager(requireContext(), getColumnsNo())
        binding.verseGridTotalCount.adapter = adapter
        adapter.setOnClickListener(object : VerseGridAdapter.OnItemClickListener {
            override fun onClick(position: Int) {
                if (position == viewModel.verse) {
                    isLeft = null
                } else if (position < viewModel.verse) {
                    stopAudio()
                    isLeft = true
                    viewModel.loadingState()
                    viewModel.verse = position
                    viewModel.getVerse(authors.first, authors.second)
                } else {
                    stopAudio()
                    isLeft = false
                    viewModel.loadingState()
                    viewModel.verse = position
                    viewModel.getVerse(authors.first, authors.second)
                }
                binding.verseGridTotalCount.visibility = View.GONE
                binding.verseFabPlayAudio.visibility = View.VISIBLE
            }
        })
    }

    private fun playGoogleTTS() {
        googleTTS.speak(
            binding.verseTranslation.text.toString(),
            TextToSpeech.QUEUE_FLUSH,
            Bundle(),
            getString(R.string.app_name)
        )
    }

    private fun playAudioFromUrl() {
        val url = getAudioURL(viewModel.chapter, viewModel.verse)
        mediaPlayer?.release()
        mediaPlayer = MediaPlayer().apply {
            try {
                setDataSource(url)
                setOnPreparedListener {
                    start()
                    binding.verseFabPlayAudio.setImageResource(R.drawable.round_pause_24)
                }
                prepareAsync()
                setOnCompletionListener {
                    release()
                    mediaPlayer = null
                    binding.verseFabPlayAudio.setImageResource(R.drawable.round_more_horiz_24)
                    playGoogleTTS()
                }
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    private fun stopAudio() {
        if (googleTTS.isSpeaking || mediaPlayer != null) {
            binding.verseFabPlayAudio.setImageResource(R.drawable.round_play_arrow_24)
            googleTTS.stop()
            mediaPlayer?.release()
            mediaPlayer = null
        }
    }

    private fun setUtteranceProgressListenerOnTheTTS() {
        val utteranceProgressListener: UtteranceProgressListener =
            object : UtteranceProgressListener() {
                override fun onStart(s: String) {
                    binding.verseFabPlayAudio.setImageResource(R.drawable.round_pause_24)
                }

                override fun onDone(s: String) {
                    binding.verseFabPlayAudio.setImageResource(R.drawable.round_play_arrow_24)
                }

                @Deprecated("Deprecated in Java")
                override fun onError(s: String) {
                }
            }
        googleTTS.setOnUtteranceProgressListener(utteranceProgressListener)
    }

    private fun isGoogleEngineInstalled(): Boolean {
        val ttsIntent = Intent()
        ttsIntent.setAction(TextToSpeech.Engine.ACTION_CHECK_TTS_DATA)
        val pm = requireActivity().packageManager
        val list = pm.queryIntentActivities(ttsIntent, PackageManager.GET_META_DATA)
        var googleIsInstalled = false
        for (i in list.indices) {
            val resolveInfoUnderScrutiny = list[i]
            val engineName = resolveInfoUnderScrutiny.activityInfo.applicationInfo.packageName
            if (engineName == ENGINE_NAME) {
                googleIsInstalled = true
            }
        }
        return googleIsInstalled
    }

    private fun createGoogleTTS() {
        val ttsIntent = Intent()
        ttsIntent.setAction(TextToSpeech.Engine.ACTION_CHECK_TTS_DATA)
        val pm = requireActivity().packageManager
        val list = pm.queryIntentActivities(ttsIntent, PackageManager.GET_META_DATA)
        for (i in list) {
            val engineName = i.activityInfo.applicationInfo.packageName
            if (engineName == ENGINE_NAME) {
                googleTTS = TextToSpeech(requireContext(), { status ->
                    if (status != TextToSpeech.ERROR) {
                        setParticularHindiVoice()
                        setUtteranceProgressListenerOnTheTTS()
                    }
                }, ENGINE_NAME)
            }
        }
    }

    private fun setParticularHindiVoice() {
        for (v in googleTTS.voices!!)
            if (v.name == TTS_LANGUAGE)
                googleTTS.setVoice(v)
    }

    private fun getColumnsNo(): Int {
        val displayMetrics = resources.displayMetrics
        val dpWidth = displayMetrics.widthPixels / displayMetrics.density
        val numberOfCols = (dpWidth - 32) / 55
        return numberOfCols.toInt()
    }

    override fun onDestroyView() {
        val verse = binding.verseText.text.toString()
        if (viewModel.verseLiveData.value is ResultOf.Failure) viewModel.verse--
        if (verse.isNotEmpty())
            setLastRead(viewModel.chapter, viewModel.verse, verse, requireContext())
        stopAudio()
        super.onDestroyView()
        _binding = null
    }
}