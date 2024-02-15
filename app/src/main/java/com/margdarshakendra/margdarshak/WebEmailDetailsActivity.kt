package com.margdarshakendra.margdarshak

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.text.HtmlCompat
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.margdarshakendra.margdarshak.databinding.ActivityWebEmailDetailsBinding
import com.margdarshakendra.margdarshak.utils.HtmlImageGetter

class WebEmailDetailsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityWebEmailDetailsBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWebEmailDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val imageUrl = intent?.getStringExtra("image")
        val userName = intent?.getStringExtra("userName")
        val subject = intent?.getStringExtra("subject")
        val emailBody = intent?.getStringExtra("body")
        val dateTime = intent?.getStringExtra("dateTime")
        val views = intent?.getStringExtra("views")
        val lastViewedDate = intent?.getStringExtra("lastViewedDate")
        if(lastViewedDate == null) binding.lastViewedDate.visibility = View.GONE

        imageUrl?.let {
            Glide.with(this).load(it).into(binding.image)
        }
        binding.subject.text = subject
        binding.dateTime.text = dateTime
        binding.userName.text = userName
        views?.let {
            binding.views.text = "$it views"
        }
        lastViewedDate?.let {
            binding.lastViewedDate.text = "Last Viewed : $it"
        }

        emailBody?.let {
            val glide = Glide.with(this)
            val imageGetter = HtmlImageGetter(
                lifecycleScope, resources, glide, binding.emailBody)
            val styledText = HtmlCompat.fromHtml(
                emailBody,
                HtmlCompat.FROM_HTML_MODE_LEGACY,
                imageGetter,
                null
            ).trim()
            binding.emailBody.text = styledText

        }


    }
}