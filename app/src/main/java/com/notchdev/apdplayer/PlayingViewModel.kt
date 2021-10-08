package com.notchdev.apdplayer

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class PlayingViewModel: ViewModel() {

    private var songPos = 0

    private val _currPos = MutableLiveData(songPos)
    val currPos: LiveData<Int> = _currPos

    private val _currProgress = MutableLiveData(0)
    val currProgress: LiveData<Int> = _currProgress

    private val _playState = MutableLiveData(PlayState.PAUSE)
    val playState: LiveData<PlayState> = _playState

    fun nextSong() {
        if (_currPos.value == 2) {
            songPos = 0
        } else {
            ++songPos
        }
        _currPos.value = songPos
        _playState.value = PlayState.PLAY
    }

    fun prevSong() {
        if (_currPos.value == 0) {
            songPos = 2
        } else {
            --songPos
        }
        _currPos.value = songPos
        _playState.value = PlayState.PLAY
    }

    fun changeState(state: PlayState) {
        _playState.value = state
    }
}



enum class PlayState {
    PLAY,
    PAUSE
}