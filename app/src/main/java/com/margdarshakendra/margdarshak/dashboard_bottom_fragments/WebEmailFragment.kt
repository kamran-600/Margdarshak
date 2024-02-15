package com.margdarshakendra.margdarshak.dashboard_bottom_fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import android.widget.Toast
import androidx.core.app.ActivityOptionsCompat
import androidx.core.content.ContextCompat
import androidx.core.util.Pair
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import cn.pedant.SweetAlert.SweetAlertDialog
import com.faltenreich.skeletonlayout.SkeletonConfig
import com.faltenreich.skeletonlayout.SkeletonLayout
import com.faltenreich.skeletonlayout.applySkeleton
import com.margdarshakendra.margdarshak.R
import com.margdarshakendra.margdarshak.WebEmailDetailsActivity
import com.margdarshakendra.margdarshak.adapters.WebEmailAdapter
import com.margdarshakendra.margdarshak.databinding.FragmentWebEmailBinding
import com.margdarshakendra.margdarshak.utils.Constants
import com.margdarshakendra.margdarshak.utils.NetworkResult
import com.margdarshakendra.margdarshak.viewmodels.WebEmailViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class WebEmailFragment : Fragment() {
    private lateinit var binding: FragmentWebEmailBinding
    private val webEmailViewModel by viewModels<WebEmailViewModel>()

    private lateinit var webEmailAdapter: WebEmailAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentWebEmailBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.swipeRefresh.setOnRefreshListener {
            if(binding.category.text.equals("Inbox")){
                webEmailViewModel.getWebEmails("inbox")
            }
            else{
                webEmailViewModel.getWebEmails("sentbox")
            }
        }

        binding.moreOptionsMenu.setOnClickListener {
            val popupMenu = PopupMenu(requireContext(), it)
            popupMenu.menuInflater.inflate(R.menu.web_email_menu, popupMenu.menu)

            popupMenu.setOnMenuItemClickListener {
                when (it.itemId) {
                    R.id.importLatestEmails -> {
                        binding.category.text = "Inbox"
                        webEmailViewModel.getImportedWebEmails()
                    }

                    R.id.inbox -> {
                        binding.category.text = "Inbox"
                        webEmailViewModel.getWebEmails("inbox")
                    }

                    R.id.sentbox -> {
                        binding.category.text = "Sentbox"
                        webEmailViewModel.getWebEmails("sentbox")
                    }
                }


                true
            }
            popupMenu.show()

        }

        val sweetProgressDialog = SweetAlertDialog(requireContext(),SweetAlertDialog.PROGRESS_TYPE)
        sweetProgressDialog.setCanceledOnTouchOutside(false)
        webEmailViewModel.importedWebEmailsLiveData.observe(viewLifecycleOwner){
            sweetProgressDialog.dismiss()
            when (it) {
                is NetworkResult.Success -> {
                    Log.d(Constants.TAG, it.data!!.toString())
                    Toast.makeText(requireContext(), it.data.message, Toast.LENGTH_SHORT).show()
                    //webEmailViewModel.importedWebEmailsLiveData.removeObservers(viewLifecycleOwner)
                    webEmailViewModel.getWebEmails("inbox")


                }

                is NetworkResult.Error -> {
                    Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
                    Log.d(Constants.TAG, it.message.toString())
                    //webEmailViewModel.importedWebEmailsLiveData.removeObservers(viewLifecycleOwner)
                }

                is NetworkResult.Loading -> {
                    sweetProgressDialog.show()
                }
            }
        }


        webEmailViewModel.getWebEmails("inbox")
        webEmailAdapter = WebEmailAdapter(requireContext(), ::sendRowData)
        binding.webEmailRecView.adapter = webEmailAdapter
        binding.webEmailRecView.setHasFixedSize(true)

        val skeleton = binding.webEmailRecView.applySkeleton(R.layout.single_row_web_email, 12, SkeletonConfig(
            maskColor = ContextCompat.getColor(requireContext(), SkeletonLayout.DEFAULT_MASK_COLOR),
            maskCornerRadius = 150f,
            showShimmer = SkeletonLayout.DEFAULT_SHIMMER_SHOW,
            shimmerColor = ContextCompat.getColor(requireContext(), SkeletonLayout.DEFAULT_SHIMMER_COLOR),
            shimmerDurationInMillis = SkeletonLayout.DEFAULT_SHIMMER_DURATION_IN_MILLIS,
            shimmerDirection = SkeletonLayout.DEFAULT_SHIMMER_DIRECTION,
            shimmerAngle = SkeletonLayout.DEFAULT_SHIMMER_ANGLE
        )
        )
        webEmailViewModel.webEmailsLiveData.observe(viewLifecycleOwner) {
            skeleton.showOriginal()

            when (it) {
                is NetworkResult.Success -> {
                    binding.swipeRefresh.isRefreshing = false
                    Log.d(Constants.TAG, it.data!!.toString())

                    webEmailAdapter.submitList(null)
                    webEmailAdapter.submitList(it.data.message)

                }

                is NetworkResult.Error -> {
                    binding.swipeRefresh.isRefreshing = false
                    Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
                    Log.d(Constants.TAG, it.message.toString())
                }

                is NetworkResult.Loading -> {
                    skeleton.showSkeleton()
                }
            }


        }


    }

    private fun sendRowData(
        pic: String?,
        imageView: View,
        name: String?,
        nameTextView: View,
        subject: String?,
        subjectTextView: View,
        body: String?,
        dateTime: String?,
        dateTimeTextView: View,
        views: String?,
        viewsTextView: View?,
        lastViewedDate: String?,
        rootView: View
    ) {
        val intent = Intent(requireActivity(), WebEmailDetailsActivity::class.java)
        intent.putExtra("image", pic)
        intent.putExtra("userName", name)
        intent.putExtra("subject", subject)
        intent.putExtra("body", body)
        intent.putExtra("dateTime", dateTime)
        intent.putExtra("views", views)
        intent.putExtra("lastViewedDate", lastViewedDate)

        val optionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(
            requireActivity(),
           // Pair.create(imageView, "imageTransition"),
           // Pair.create(nameTextView, "nameTransition"),
            //Pair.create(subjectTextView, "subjectTransition"),
            Pair.create(dateTimeTextView, "dateTimeTransition"),
           // Pair.create(viewsTextView, "viewsTransition"),
            Pair.create(rootView, "rootTransition")
        )
        requireActivity().startActivity(intent, optionsCompat.toBundle())
    }
}