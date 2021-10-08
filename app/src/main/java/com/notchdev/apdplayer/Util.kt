package com.notchdev.apdplayer

import android.widget.ImageView
import androidx.databinding.BindingAdapter

fun Int.getTimeInString() :String {
    val minutes = this / 1000 / 60
    val seconds = this / 1000 % 60

    var timeString = "$minutes:"
    if(seconds < 10 ) {
        timeString += "0"
    }
    timeString += "$seconds"
    return timeString
}

@BindingAdapter("iconRes")
fun ImageView.setIconRes(state: PlayState?) {
    if(state != null && state.name == PlayState.PLAY.name) {
        setImageResource(R.drawable.ic_pause)
    } else {
        setImageResource(R.drawable.ic_play)
    }
}