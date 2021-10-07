package com.notchdev.apdplayer

import android.media.MediaPlayer
import android.os.Bundle
import android.widget.SeekBar
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.notchdev.apdplayer.databinding.ActivityMainBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.*

class MainActivity : AppCompatActivity() {

    private var mPlayer: MediaPlayer? = null
    private lateinit var binding: ActivityMainBinding
    private lateinit var songList:List<Int>
    private var currentSongPos = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        songList = listOf(R.raw.excuses,R.raw.saada,R.raw.toxic)
        mPlayer = MediaPlayer.create(this,songList[0])

        mPlayer?.setOnCompletionListener {
            nextSong()
        }
        binding.apply {
            seekBar.max = mPlayer?.duration!!
            durationTv.text = getTimeString(mPlayer?.duration!!)
            progressTv.text = getTimeString(mPlayer?.currentPosition!!)
            playPauseBtn.setOnClickListener {
                if (mPlayer?.isPlaying ?: return@setOnClickListener) {
                    mPlayer?.pause()
                    playPauseBtn.setImageResource(R.drawable.ic_play)
                } else {
                    playPauseBtn.setImageResource(R.drawable.ic_pause)
                    playSong()
                }
            }

            prevSongBtn.setOnClickListener {
                previousSong()
            }

            nextSongBtn.setOnClickListener {
                nextSong()
            }
            seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged(p0: SeekBar?, currPos: Int, fromUser: Boolean) {
                    if (fromUser) {
                        mPlayer?.seekTo(currPos)
                        progressTv.text = getTimeString(currPos)
                    }
                }

                override fun onStartTrackingTouch(p0: SeekBar?) {
                }

                override fun onStopTrackingTouch(p0: SeekBar?) {
                }
            })
        }
    }

    private fun startSong(res:Int) {
        mPlayer?.stop()
        mPlayer = MediaPlayer.create(this,res)
        playSong()
    }

    fun playSong() {
        mPlayer?.start()
        binding.apply {
            seekBar.max = mPlayer?.duration!!
            progressTv.text = getTimeString(mPlayer?.duration!!)
        }

        lifecycleScope.launch(Dispatchers.Main) {
            while(mPlayer!=null) {
                val pos = mPlayer?.currentPosition
                binding.apply {
                    progressTv.text = getTimeString(pos!!)
                    seekBar.progress = pos
                    delay(100)
                }
            }
        }

    }

    private fun nextSong() {
        if (currentSongPos == songList.size - 1) {
            currentSongPos = 0
        } else {
            currentSongPos++
        }
        startSong(songList[currentSongPos])
    }

    private fun previousSong() {
        if(currentSongPos == 0) {
            currentSongPos = songList.size - 1
        } else {
            currentSongPos--
        }
        startSong(songList[currentSongPos])
    }

    fun stopSong() {
        mPlayer?.stop()
        mPlayer = null
        binding.apply {
            seekBar.progress = 0
            progressTv.text = "0:00"
        }
    }

    private fun getTimeString(millis: Int): String {

        val minutes = millis / 1000 / 60
        val seconds = millis / 1000 % 60

        var timeString = "$minutes:"
        if(seconds < 10 ) {
            timeString += "0"
        }
        timeString += "$seconds"
        return timeString
    }
}