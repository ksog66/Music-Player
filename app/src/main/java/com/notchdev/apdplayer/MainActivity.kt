package com.notchdev.apdplayer

import android.media.MediaPlayer
import android.os.Bundle
import android.widget.SeekBar
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.notchdev.apdplayer.databinding.ActivityMainBinding
import kotlinx.coroutines.*
import java.util.*

class MainActivity : AppCompatActivity() {


    private var mPlayer: MediaPlayer? = null
    private lateinit var binding: ActivityMainBinding
    private lateinit var songList: List<Int>

    private val playViewModel: PlayingViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        binding.lifecycleOwner = this
        setContentView(binding.root)



        songList = listOf(R.raw.excuses, R.raw.saada, R.raw.toxic)

        observeEvents()
        binding.apply {
            playPauseBtn.setOnClickListener {
                if (mPlayer?.isPlaying ?: return@setOnClickListener) {
                    playViewModel.changeState(PlayState.PAUSE)
                } else {
                    playViewModel.changeState(PlayState.PLAY)
                    playSong()
                }
            }

            prevSongBtn.setOnClickListener {
                playViewModel.prevSong()
            }

            nextSongBtn.setOnClickListener {
                playViewModel.nextSong()
            }
            seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged(p0: SeekBar?, currPos: Int, fromUser: Boolean) {
                    if (fromUser) {
                        mPlayer?.seekTo(currPos)
                        progressTv.text = currPos.getTimeInString()
                    }
                }

                override fun onStartTrackingTouch(p0: SeekBar?) {
                }

                override fun onStopTrackingTouch(p0: SeekBar?) {
                }
            })
        }
    }

    private fun observeEvents() {
        playViewModel.currProgress.observe({ lifecycle }) {
            binding.currProgress = it.getTimeInString()
        }
        playViewModel.playState.observe({ lifecycle }) {
            it?.let {
                binding.playPauseState = it
                if (it.name == PlayState.PLAY.name) {
                    playSong()
                } else {
                    mPlayer?.pause()
                }
            }
        }
        playViewModel.currPos.observe({ lifecycle }) {
            initMediaPlayer(it)
        }
    }

    private fun initMediaPlayer(songPos: Int) {
        mPlayer?.stop()
        mPlayer = null
        mPlayer = MediaPlayer.create(this, songList[songPos])
        updateUI()
    }

    private fun updateUI() {
        binding.apply {
            seekBar.max = mPlayer?.duration!!
            progressTv.text = mPlayer?.currentPosition!!.getTimeInString()
            durationTv.text = mPlayer?.duration!!.getTimeInString()
        }
    }

    fun playSong() {
        mPlayer?.start()
        mPlayer?.setOnCompletionListener {
            playViewModel.nextSong()
        }
        binding.playPauseBtn.setImageResource(R.drawable.ic_pause)
        updateUI()
        lifecycleScope.launch(Dispatchers.Main) {
            while(mPlayer!=null) {
                val pos = mPlayer?.currentPosition
                binding.apply {
                    progressTv.text = pos!!.getTimeInString()
                    seekBar.progress = pos
                    delay(100)
                }
            }
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        mPlayer?.release()
        mPlayer = null
    }
}