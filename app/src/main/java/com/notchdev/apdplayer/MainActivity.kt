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

class MainActivity : AppCompatActivity() {

    private var mPlayer: MediaPlayer? = null
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        mPlayer = MediaPlayer.create(this, R.raw.excuses)
        binding.apply {
            seekBar.max = mPlayer?.duration!!

            btnPlay.setOnClickListener {
                playSong()
            }
            btnPause.setOnClickListener {
                if (mPlayer?.isPlaying ?: return@setOnClickListener) {
                    mPlayer?.pause()
                }
            }
            btnStop.setOnClickListener {
                stopSong()
            }

            seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged(p0: SeekBar?, currPos: Int, fromUser: Boolean) {
                    if (fromUser) {
                        mPlayer?.seekTo(currPos)
                        tvCurTime.text = getTimeString(currPos)
                    }
                }

                override fun onStartTrackingTouch(p0: SeekBar?) {
                }

                override fun onStopTrackingTouch(p0: SeekBar?) {
                }
            })
        }
    }

    fun playSong() {
        mPlayer = mPlayer ?: MediaPlayer.create(this, R.raw.excuses)
        mPlayer?.start()
        binding.apply {
            seekBar.max = mPlayer?.duration!!
            tvCurTime.text = getTimeString(mPlayer?.duration!!)
        }

        lifecycleScope.launch(Dispatchers.Main) {
            while(mPlayer!=null) {
                val pos = mPlayer?.currentPosition
                binding.apply {
                    tvCurTime.text = getTimeString(pos!!)
                    seekBar.progress = pos
                    delay(100)
                }
            }
        }

    }

    fun stopSong() {
        mPlayer?.stop()
        mPlayer = null
        binding.apply {
            seekBar.progress = 0
            tvCurTime.text = "0:00"
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