package com.margdarshakendra.margdarshak

import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.support.v4.media.session.MediaControllerCompat
import android.util.Log
import android.view.View
import android.widget.MediaController
import androidx.appcompat.app.AppCompatActivity
import com.margdarshakendra.margdarshak.databinding.ActivityVideoViewBinding
import com.margdarshakendra.margdarshak.utils.Constants.TAG

class VideoViewActivity : AppCompatActivity() {

    private lateinit var binding: ActivityVideoViewBinding
    private var playbackPosition = 0
    private var isPlaying = false

    /*companion object {
        private const val PLAYBACK_POSITION_KEY = "playback_position"
        private const val IS_PLAYING_KEY = "is_playing"
    }*/

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityVideoViewBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val videoUrl = intent.getStringExtra("videoUrl")!!
        Log.d(TAG, videoUrl)

        val videoUri = Uri.parse(videoUrl)
        Log.d(TAG, videoUri.toString())

        val mediaController = MediaController(this )
        mediaController.setAnchorView(binding.videoView)

        binding.videoView.setMediaController(mediaController)

        binding.videoView.setVideoURI(videoUri)
        binding.videoView.requestFocus()

        binding.spinKit.visibility = View.VISIBLE
        binding.videoView.setOnPreparedListener {
            binding.spinKit.visibility = View.GONE
            playVideo()
        }





        binding.videoView.setOnInfoListener { mp, what, extra ->
            when (what) {
                MediaPlayer.MEDIA_INFO_BUFFERING_START -> {
                    binding.spinKit.visibility = View.VISIBLE
                    return@setOnInfoListener true
                }

                MediaPlayer.MEDIA_INFO_BUFFERING_END -> {
                    binding.spinKit.visibility = View.GONE
                    return@setOnInfoListener true
                }

                else -> false
            }
        }

    }

    /*override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt(PLAYBACK_POSITION_KEY, binding.videoView.currentPosition)
        outState.putBoolean(IS_PLAYING_KEY, binding.videoView.isPlaying)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        playbackPosition = savedInstanceState.getInt(PLAYBACK_POSITION_KEY)
        isPlaying = savedInstanceState.getBoolean(IS_PLAYING_KEY)
    }
*/

    private fun playVideo() {
        isPlaying = true
        binding.videoView.start()
    }
}