package com.margdarshakendra.margdarshak

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LifecycleCoroutineScope
import androidx.lifecycle.lifecycleScope
import cn.pedant.SweetAlert.SweetAlertDialog
import com.faltenreich.skeletonlayout.createSkeleton
import com.margdarshakendra.margdarshak.databinding.ActivityPdfViewerBinding
import com.margdarshakendra.margdarshak.utils.Constants.TAG
import com.rajat.pdfviewer.HeaderData
import com.rajat.pdfviewer.PdfRendererView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import java.io.IOException

class PdfViewerActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPdfViewerBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPdfViewerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Retrieve the PDF URL from the intent
        val pdfUrl = intent.getStringExtra("pdfUrl")
        Log.d(TAG, pdfUrl.toString())

        /*val path="https://github.github.com/training-kit/downloads/github-git-cheat-sheet.pdf"

        binding.pdfWebView.settings.loadWithOverviewMode = true
        binding.pdfWebView.settings.javaScriptEnabled = true
        val url = "https://docs.google.com/gview?embedded=true&url=$pdfUrl"
        binding.pdfWebView.loadUrl(url)*/

        if (!pdfUrl.isNullOrBlank()) {

            try{
                binding.pdfView.initWithUrl(pdfUrl, HeaderData(),lifecycleScope, lifecycle)
            }catch (e : Exception){
                val sweetAlertDialog = SweetAlertDialog(this, SweetAlertDialog.ERROR_TYPE)
                sweetAlertDialog.contentText = e.message
                sweetAlertDialog.show()
                sweetAlertDialog.setOnDismissListener {
                    finish()
                }
            }

            binding.pdfView.statusListener = object : PdfRendererView.StatusCallBack{
                override fun onPdfLoadStart() {
                    super.onPdfLoadStart()
                    binding.spinKit.visibility = View.VISIBLE
                }

                override fun onPdfLoadSuccess(absolutePath: String) {
                    super.onPdfLoadSuccess(absolutePath)
                    binding.spinKit.visibility = View.GONE
                }
            }
        }

    }
}
