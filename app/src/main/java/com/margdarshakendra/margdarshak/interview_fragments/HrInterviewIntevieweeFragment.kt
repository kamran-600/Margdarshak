package com.margdarshakendra.margdarshak.interview_fragments

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.text.HtmlCompat
import androidx.core.text.HtmlCompat.FROM_HTML_MODE_LEGACY
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import cn.pedant.SweetAlert.SweetAlertDialog
import com.faltenreich.skeletonlayout.createSkeleton
import com.google.android.material.textview.MaterialTextView
import com.margdarshakendra.margdarshak.R
import com.margdarshakendra.margdarshak.StudentHomeFragment
import com.margdarshakendra.margdarshak.api.DashboardApi
import com.margdarshakendra.margdarshak.dashboard_bottom_fragments.HomeFragment
import com.margdarshakendra.margdarshak.databinding.FragmentHrInterviewIntevieweeBinding
import com.margdarshakendra.margdarshak.utils.Constants
import com.margdarshakendra.margdarshak.utils.Constants.TAG
import com.margdarshakendra.margdarshak.utils.NetworkResult
import com.margdarshakendra.margdarshak.utils.SharedPreference
import com.margdarshakendra.margdarshak.viewmodels.HrInterviewIntervieweeViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import javax.inject.Inject

@AndroidEntryPoint
class HrInterviewIntevieweeFragment : Fragment() {

    private lateinit var binding : FragmentHrInterviewIntevieweeBinding
    private val hrInterviewIntervieweeViewModel by viewModels<HrInterviewIntervieweeViewModel>()
    private val taskHandler = TaskHandler()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHrInterviewIntevieweeBinding.inflate(inflater,container,false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        taskHandler.startRepeatingTask(1000L)

       /* CoroutineScope(Dispatchers.Main).launch {
            val resp = dashboardApi.getHrIntervieweeQues()
            if(resp.isSuccessful && resp.body() != null){

                Log.d(TAG, resp.body()!!.value.toString())
                resp.body()!!.observe(viewLifecycleOwner){
                    binding.hrInterviewQuestion.text = HtmlCompat.fromHtml(it.data.message, FROM_HTML_MODE_LEGACY)
                    Log.d(TAG, it.data.message)
                }
            }
            else{
                Toast.makeText(requireContext(), resp.errorBody()!!.string(), Toast.LENGTH_SHORT).show()
            }
        }*/


        //val skeleton = binding.hrInterviewQuestion.createSkeleton()
        hrInterviewIntervieweeViewModel.hrIntervieweeQuesLiveData.observe(viewLifecycleOwner){
            //skeleton.showOriginal()
            when (it) {
                is NetworkResult.Success -> {
                    Log.d(TAG, it.data!!.toString())
                    if( ! binding.hrInterviewQuestion.text.equals(it.data.data.message))
                    binding.hrInterviewQuestion.text = HtmlCompat.fromHtml(it.data.data.message, FROM_HTML_MODE_LEGACY)

                }

                is NetworkResult.Error -> {
                    Log.d(TAG, it.message.toString())
                    if(it.message=="Attempt to read property \"question\" on null" ){
                        binding.hrInterviewQuestion.text = "Interview is started But Interviewer has not assigned you a Question yet"
                    }
                    else{
                        Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
                    }
                }

                is NetworkResult.Loading -> {
                    //skeleton.showSkeleton()
                }
            }
        }

    }

    inner class TaskHandler {
        private val handler = Handler(Looper.getMainLooper())
        private var runnable: Runnable? = null

        fun startRepeatingTask(intervalMillis: Long ) {
            // Ensure that any existing callback is removed before scheduling a new one
            stopRepeatingTask()

            // Create a new runnable with the specified task
            runnable = object : Runnable {
                override fun run() {
                    hrInterviewIntervieweeViewModel.getHrIntervieweeQues()
                    // Schedule the next execution after the specified interval
                    handler.postDelayed(this, intervalMillis)
                }
            }

            // Start the initial execution
            handler.postDelayed(runnable!!, 0L)
        }

        fun stopRepeatingTask() {
            // Remove the callback to avoid memory leaks
            runnable?.let {
                handler.removeCallbacks(it)
                runnable = null
            }
        }
    }

    override fun onPause() {
        super.onPause()
        taskHandler.stopRepeatingTask()
    }

    override fun onResume() {
        super.onResume()
        taskHandler.startRepeatingTask(1000L)
    }
}