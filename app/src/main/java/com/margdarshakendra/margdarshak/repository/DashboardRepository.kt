package com.margdarshakendra.margdarshak.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.margdarshakendra.margdarshak.api.DashboardApi
import com.margdarshakendra.margdarshak.models.AttitudeQuestionsResponse
import com.margdarshakendra.margdarshak.models.AttitudeRatingQuesResponse
import com.margdarshakendra.margdarshak.models.CRMResponse
import com.margdarshakendra.margdarshak.models.CallRequest
import com.margdarshakendra.margdarshak.models.CallResponse
import com.margdarshakendra.margdarshak.models.ClientDataResponse
import com.margdarshakendra.margdarshak.models.CounsellingDataResponse
import com.margdarshakendra.margdarshak.models.DataRequest
import com.margdarshakendra.margdarshak.models.HiringDataResponse
import com.margdarshakendra.margdarshak.models.QuestionsResponse
import com.margdarshakendra.margdarshak.models.SaveAptitudeAnswerRequest
import com.margdarshakendra.margdarshak.models.SaveAptitudeAnswerResponse
import com.margdarshakendra.margdarshak.models.SaveAttitudeQuestionsRequest
import com.margdarshakendra.margdarshak.models.SaveAttitudeQuestionsResponse
import com.margdarshakendra.margdarshak.models.SaveAttitudeRatingQuesRequest
import com.margdarshakendra.margdarshak.models.SaveAttitudeRatingQuesResponse
import com.margdarshakendra.margdarshak.models.SendEmailRequest
import com.margdarshakendra.margdarshak.models.SmsRequest
import com.margdarshakendra.margdarshak.models.SmsResponse
import com.margdarshakendra.margdarshak.models.StartAssessmentRequest
import com.margdarshakendra.margdarshak.models.StartAssessmentResponse
import com.margdarshakendra.margdarshak.models.StartAttitudeAssessmentResponse
import com.margdarshakendra.margdarshak.models.TemplateResponse
import com.margdarshakendra.margdarshak.models.UserAccessResponse
import com.margdarshakendra.margdarshak.utils.Constants
import com.margdarshakendra.margdarshak.utils.NetworkResult
import org.json.JSONObject
import javax.inject.Inject

class DashboardRepository @Inject constructor(private val dashboardApi: DashboardApi) {

    private val _userAccessResponseLiveData = MutableLiveData<NetworkResult<UserAccessResponse>>()
    val userAccessResponseLiveData: LiveData<NetworkResult<UserAccessResponse>>
        get() = _userAccessResponseLiveData

    private val _hiringDataResponseLiveData = MutableLiveData<NetworkResult<HiringDataResponse>>()
    val hiringDataResponseLiveData: LiveData<NetworkResult<HiringDataResponse>>
        get() = _hiringDataResponseLiveData

    private val _callResponseLiveData = MutableLiveData<NetworkResult<CallResponse>>()
    val callResponseLiveData: LiveData<NetworkResult<CallResponse>>
        get() = _callResponseLiveData

    private val _crmResponseLiveData = MutableLiveData<NetworkResult<CRMResponse>>()
    val crmResponseLiveData: LiveData<NetworkResult<CRMResponse>>
        get() = _crmResponseLiveData

    private val _smsResponseLiveData = MutableLiveData<NetworkResult<SmsResponse>>()
    val smsResponseLiveData: LiveData<NetworkResult<SmsResponse>>
        get() = _smsResponseLiveData

    private val _emailSmsResponseLiveData = MutableLiveData<NetworkResult<SmsResponse>>()
    val emailSmsResponseLiveData: LiveData<NetworkResult<SmsResponse>>
        get() = _emailSmsResponseLiveData

    private val _templateResponseLiveData = MutableLiveData<NetworkResult<TemplateResponse>>()
    val templateResponseLiveData: LiveData<NetworkResult<TemplateResponse>>
        get() = _templateResponseLiveData

    private val _counsellingDataResponseLiveData =
        MutableLiveData<NetworkResult<CounsellingDataResponse>>()
    val counsellingDataResponseLiveData: LiveData<NetworkResult<CounsellingDataResponse>>
        get() = _counsellingDataResponseLiveData

    private val _clientDataResponseLiveData = MutableLiveData<NetworkResult<ClientDataResponse>>()
    val clientDataResponseLiveData: LiveData<NetworkResult<ClientDataResponse>>
        get() = _clientDataResponseLiveData


    private val _startAptitudeAssessmentResponseLiveData =
        MutableLiveData<NetworkResult<StartAssessmentResponse>>()
    val startAptitudeAssessmentResponseLiveData: LiveData<NetworkResult<StartAssessmentResponse>>
        get() = _startAptitudeAssessmentResponseLiveData

    private val _questionsDataResponseLiveData =
        MutableLiveData<NetworkResult<QuestionsResponse>>()
    val questionsDataResponseLiveData: LiveData<NetworkResult<QuestionsResponse>>
        get() = _questionsDataResponseLiveData


    private val _saveAptitudeAnswerResponseLiveData =
        MutableLiveData<NetworkResult<SaveAptitudeAnswerResponse>>()
    val saveAptitudeAnswerResponseLiveData: LiveData<NetworkResult<SaveAptitudeAnswerResponse>>
        get() = _saveAptitudeAnswerResponseLiveData

    private val _startAttitudeAssessmentResponseLiveData =
        MutableLiveData<NetworkResult<StartAttitudeAssessmentResponse>>()
    val startAttitudeAssessmentResponseLiveData: LiveData<NetworkResult<StartAttitudeAssessmentResponse>>
        get() = _startAttitudeAssessmentResponseLiveData

    private val _attitudeQuestionsDataResponseLiveData =
        MutableLiveData<NetworkResult<AttitudeQuestionsResponse>>()
    val attitudeQuestionsDataResponseLiveData: LiveData<NetworkResult<AttitudeQuestionsResponse>>
        get() = _attitudeQuestionsDataResponseLiveData

    private val _saveAttitudeQuestionsResponseLiveData =
        MutableLiveData<NetworkResult<SaveAttitudeQuestionsResponse>>()
    val saveAttitudeQuestionsResponseLiveData: LiveData<NetworkResult<SaveAttitudeQuestionsResponse>>
        get() = _saveAttitudeQuestionsResponseLiveData

    private val _attitudeRatingQuestionsDataResponseLiveData =
        MutableLiveData<NetworkResult<AttitudeRatingQuesResponse>>()
    val attitudeRatingQuestionsDataResponseLiveData: LiveData<NetworkResult<AttitudeRatingQuesResponse>>
        get() = _attitudeRatingQuestionsDataResponseLiveData

    private val _saveAttitudeRatingQuesAndFinishTestResponseLiveData =
        MutableLiveData<NetworkResult<SaveAttitudeRatingQuesResponse>>()
    val saveAttitudeRatingQuesAndFinishTestResponseLiveData: LiveData<NetworkResult<SaveAttitudeRatingQuesResponse>>
        get() = _saveAttitudeRatingQuesAndFinishTestResponseLiveData




    suspend fun getUserAccess() {
        try {
            val userAccessResponse = dashboardApi.getUserAccess()

            if (userAccessResponse.isSuccessful && userAccessResponse.body() != null) {
                _userAccessResponseLiveData.postValue(NetworkResult.Success(userAccessResponse.body()!!))
            } else if (userAccessResponse.errorBody() != null) {
                val jsonLoginError =
                    JSONObject(userAccessResponse.errorBody()!!.charStream().readText())
                _userAccessResponseLiveData.postValue(NetworkResult.Error(jsonLoginError.getString("message")))

            }
        } catch (e: Exception) {
            Log.d(Constants.TAG, e.message.toString())
            _userAccessResponseLiveData.postValue(NetworkResult.Error(e.message))
        }
    }

    suspend fun getHiringData(dataRequest: DataRequest) {

        try {
            val hiringDataResponse = dashboardApi.getHiringData(dataRequest)

            if (hiringDataResponse.isSuccessful && hiringDataResponse.body() != null) {
                _hiringDataResponseLiveData.postValue(NetworkResult.Success(hiringDataResponse.body()!!))
            } else if (hiringDataResponse.errorBody() != null) {
                val jsonLoginError =
                    JSONObject(hiringDataResponse.errorBody()!!.charStream().readText())
                _hiringDataResponseLiveData.postValue(NetworkResult.Error(jsonLoginError.getString("message")))

            }
        } catch (e: Exception) {
            Log.d(Constants.TAG, e.message.toString())
            _hiringDataResponseLiveData.postValue(NetworkResult.Error(e.message))
        }
    }

    suspend fun makeCall(callRequest: CallRequest) {

        try {
            val callResponse = dashboardApi.makeCall(callRequest)

            if (callResponse.isSuccessful && callResponse.body() != null) {
                _callResponseLiveData.postValue(NetworkResult.Success(callResponse.body()!!))
            } else if (callResponse.errorBody() != null) {
                val jsonLoginError =
                    JSONObject(callResponse.errorBody()!!.charStream().readText())
                _callResponseLiveData.postValue(NetworkResult.Error(jsonLoginError.getString("message")))

            }
        } catch (e: Exception) {
            Log.d(Constants.TAG, e.message.toString())
            _callResponseLiveData.postValue(NetworkResult.Error(e.message))
        }
    }

    suspend fun getCrmContact() {

        try {
            val crmResponse = dashboardApi.getCrmContact()

            if (crmResponse.isSuccessful && crmResponse.body() != null) {
                _crmResponseLiveData.postValue(NetworkResult.Success(crmResponse.body()!!))
            } else if (crmResponse.errorBody() != null) {
                val jsonLoginError =
                    JSONObject(crmResponse.errorBody()!!.charStream().readText())
                _crmResponseLiveData.postValue(NetworkResult.Error(jsonLoginError.getString("message")))

            }
        } catch (e: Exception) {
            Log.d(Constants.TAG, e.message.toString())
            _crmResponseLiveData.postValue(NetworkResult.Error(e.message))
        }
    }

    suspend fun sendSms(smsRequest: SmsRequest) {

        try {
            val smsResponse = dashboardApi.sendMessage(smsRequest)

            if (smsResponse.isSuccessful && smsResponse.body() != null) {
                _smsResponseLiveData.postValue(NetworkResult.Success(smsResponse.body()!!))
            } else if (smsResponse.errorBody() != null) {
                val jsonLoginError =
                    JSONObject(smsResponse.errorBody()!!.charStream().readText())
                _smsResponseLiveData.postValue(NetworkResult.Error(jsonLoginError.getString("message")))

            }
        } catch (e: Exception) {
            Log.d(Constants.TAG, e.message.toString())
            _smsResponseLiveData.postValue(NetworkResult.Error(e.message))
        }
    }

    suspend fun sendEmailMessage(sendEmailRequest: SendEmailRequest) {

        try {
            val smsResponse = dashboardApi.sendEmailMessage(sendEmailRequest)

            if (smsResponse.isSuccessful && smsResponse.body() != null) {
                _emailSmsResponseLiveData.postValue(NetworkResult.Success(smsResponse.body()!!))
            } else if (smsResponse.errorBody() != null) {
                val jsonLoginError =
                    JSONObject(smsResponse.errorBody()!!.charStream().readText())
                _emailSmsResponseLiveData.postValue(NetworkResult.Error(jsonLoginError.getString("message")))

            }
        } catch (e: Exception) {
            Log.d(Constants.TAG, e.message.toString())
            _emailSmsResponseLiveData.postValue(NetworkResult.Error(e.message))
        }
    }

    suspend fun getTemplateContent(tempID: Int, uid: Int) {

        try {
            val templateResponse = dashboardApi.getTemplateContent(tempID, uid)

            if (templateResponse.isSuccessful && templateResponse.body() != null) {
                _templateResponseLiveData.postValue(NetworkResult.Success(templateResponse.body()!!))
            } else if (templateResponse.errorBody() != null) {
                val jsonLoginError =
                    JSONObject(templateResponse.errorBody()!!.charStream().readText())
                _templateResponseLiveData.postValue(NetworkResult.Error(jsonLoginError.getString("message")))

            }
        } catch (e: Exception) {
            Log.d(Constants.TAG, e.message.toString())
            _templateResponseLiveData.postValue(NetworkResult.Error(e.message))
        }
    }


    suspend fun getCounsellingData(dataRequest: DataRequest) {

        try {
            val counsellingDataResponse = dashboardApi.getCounsellingData(dataRequest)

            if (counsellingDataResponse.isSuccessful && counsellingDataResponse.body() != null) {
                _counsellingDataResponseLiveData.postValue(
                    NetworkResult.Success(
                        counsellingDataResponse.body()!!
                    )
                )
            } else if (counsellingDataResponse.errorBody() != null) {
                val jsonLoginError =
                    JSONObject(counsellingDataResponse.errorBody()!!.charStream().readText())
                _counsellingDataResponseLiveData.postValue(
                    NetworkResult.Error(
                        jsonLoginError.getString(
                            "message"
                        )
                    )
                )

            }
        } catch (e: Exception) {
            Log.d(Constants.TAG, e.message.toString())
            _counsellingDataResponseLiveData.postValue(NetworkResult.Error(e.message))
        }
    }

    suspend fun getClientData(dataRequest: DataRequest) {

        try {
            val clientDataResponse = dashboardApi.getClientData(dataRequest)

            if (clientDataResponse.isSuccessful && clientDataResponse.body() != null) {
                _clientDataResponseLiveData.postValue(
                    NetworkResult.Success(
                        clientDataResponse.body()!!
                    )
                )
            } else if (clientDataResponse.errorBody() != null) {
                val jsonLoginError =
                    JSONObject(clientDataResponse.errorBody()!!.charStream().readText())
                _clientDataResponseLiveData.postValue(
                    NetworkResult.Error(
                        jsonLoginError.getString(
                            "message"
                        )
                    )
                )

            }
        } catch (e: Exception) {
            Log.d(Constants.TAG, e.message.toString())
            _clientDataResponseLiveData.postValue(NetworkResult.Error(e.message))
        }
    }

    suspend fun getStartAptitudeAssessment(startAssessmentRequest: StartAssessmentRequest) {

        try {
            val startAssessmentResponse =
                dashboardApi.getStartAptitudeAssessment(startAssessmentRequest)

            if (startAssessmentResponse.isSuccessful && startAssessmentResponse.body() != null) {
                _startAptitudeAssessmentResponseLiveData.postValue(
                    NetworkResult.Success(
                        startAssessmentResponse.body()!!
                    )
                )
            } else if (startAssessmentResponse.errorBody() != null) {
                val jsonLoginError =
                    JSONObject(startAssessmentResponse.errorBody()!!.charStream().readText())
                _startAptitudeAssessmentResponseLiveData.postValue(
                    NetworkResult.Error(
                        jsonLoginError.getString(
                            "message"
                        )
                    )
                )

            }
        } catch (e: Exception) {
            Log.d(Constants.TAG, e.message.toString())
            _startAptitudeAssessmentResponseLiveData.postValue(NetworkResult.Error(e.message))
        }
    }

    suspend fun getAptitudeQuestions(result_id: Int, page_no: Int) {

        try {
            val questionsResponse = dashboardApi.getAptitudeQuestions(result_id, page_no)

            if (questionsResponse.isSuccessful && questionsResponse.body() != null) {
                _questionsDataResponseLiveData.postValue(
                    NetworkResult.Success(
                        questionsResponse.body()!!
                    )
                )
            } else if (questionsResponse.errorBody() != null) {
                val jsonLoginError =
                    JSONObject(questionsResponse.errorBody()!!.charStream().readText())
                _questionsDataResponseLiveData.postValue(
                    NetworkResult.Error(
                        jsonLoginError.getString(
                            "message"
                        )
                    )
                )

            }
        } catch (e: Exception) {
            Log.d(Constants.TAG, e.message.toString())
            _questionsDataResponseLiveData.postValue(NetworkResult.Error(e.message))
        }
    }

    suspend fun saveAptitudeAnswer(saveAptitudeAnswerRequest: SaveAptitudeAnswerRequest) {

        try {
            val saveAptitudeAnswerResponse =
                dashboardApi.saveAptitudeAnswer(saveAptitudeAnswerRequest)

            if (saveAptitudeAnswerResponse.isSuccessful && saveAptitudeAnswerResponse.body() != null) {
                _saveAptitudeAnswerResponseLiveData.postValue(
                    NetworkResult.Success(
                        saveAptitudeAnswerResponse.body()!!
                    )
                )
            } else if (saveAptitudeAnswerResponse.errorBody() != null) {
                val jsonLoginError =
                    JSONObject(saveAptitudeAnswerResponse.errorBody()!!.charStream().readText())
                _saveAptitudeAnswerResponseLiveData.postValue(
                    NetworkResult.Error(
                        jsonLoginError.getString(
                            "message"
                        )
                    )
                )

            }
        } catch (e: Exception) {
            Log.d(Constants.TAG, e.message.toString())
            _saveAptitudeAnswerResponseLiveData.postValue(NetworkResult.Error(e.message))
        }
    }

    suspend fun getStartAttitudeAssessment(startAssessmentRequest: StartAssessmentRequest) {

        try {
            val startAttitudeAssessmentResponse =
                dashboardApi.getStartAttitudeAssessment(startAssessmentRequest)

            if (startAttitudeAssessmentResponse.isSuccessful && startAttitudeAssessmentResponse.body() != null) {
                _startAttitudeAssessmentResponseLiveData.postValue(
                    NetworkResult.Success(
                        startAttitudeAssessmentResponse.body()!!
                    )
                )
            } else if (startAttitudeAssessmentResponse.errorBody() != null) {
                val jsonLoginError =
                    JSONObject(
                        startAttitudeAssessmentResponse.errorBody()!!.charStream().readText()
                    )
                _startAttitudeAssessmentResponseLiveData.postValue(
                    NetworkResult.Error(
                        jsonLoginError.getString(
                            "message"
                        )
                    )
                )

            }
        } catch (e: Exception) {
            Log.d(Constants.TAG, e.message.toString())
            _startAttitudeAssessmentResponseLiveData.postValue(NetworkResult.Error(e.message))
        }
    }


    suspend fun getAttitudeQuestions(page_no: Int) {

        try {
            val attitudeQuestionsResponse = dashboardApi.getAttitudeQuestions(page_no)

            if (attitudeQuestionsResponse.isSuccessful && attitudeQuestionsResponse.body() != null) {
                _attitudeQuestionsDataResponseLiveData.postValue(
                    NetworkResult.Success(
                        attitudeQuestionsResponse.body()!!
                    )
                )
            } else if (attitudeQuestionsResponse.errorBody() != null) {
                val jsonLoginError =
                    JSONObject(attitudeQuestionsResponse.errorBody()!!.charStream().readText())
                _attitudeQuestionsDataResponseLiveData.postValue(
                    NetworkResult.Error(
                        jsonLoginError.getString(
                            "message"
                        )
                    )
                )

            }
        } catch (e: Exception) {
            Log.d(Constants.TAG, e.message.toString())
            _attitudeQuestionsDataResponseLiveData.postValue(NetworkResult.Error(e.message))
        }
    }


    suspend fun saveAttitudeQuestions(saveAttitudeQuestionsRequest: SaveAttitudeQuestionsRequest) {

        try {
            val saveAttitudeQuestionsResponse =
                dashboardApi.saveAttitudeQuestions(saveAttitudeQuestionsRequest)

            if (saveAttitudeQuestionsResponse.isSuccessful && saveAttitudeQuestionsResponse.body() != null) {
                _saveAttitudeQuestionsResponseLiveData.postValue(
                    NetworkResult.Success(
                        saveAttitudeQuestionsResponse.body()!!
                    )
                )
            } else if (saveAttitudeQuestionsResponse.errorBody() != null) {
                val jsonLoginError =
                    JSONObject(saveAttitudeQuestionsResponse.errorBody()!!.charStream().readText())
                _saveAttitudeQuestionsResponseLiveData.postValue(
                    NetworkResult.Error(
                        jsonLoginError.getString(
                            "message"
                        )
                    )
                )

            }
        } catch (e: Exception) {
            Log.d(Constants.TAG, e.message.toString())
            _saveAttitudeQuestionsResponseLiveData.postValue(NetworkResult.Error(e.message))
        }
    }


    suspend fun getAttitudeRatingQuestions(result_id: Int) {

        try {
            val attitudeRatingQuestionsResponse = dashboardApi.getAttitudeRatingQuestions(result_id)

            if (attitudeRatingQuestionsResponse.isSuccessful && attitudeRatingQuestionsResponse.body() != null) {
                _attitudeRatingQuestionsDataResponseLiveData.postValue(
                    NetworkResult.Success(
                        attitudeRatingQuestionsResponse.body()!!
                    )
                )
            } else if (attitudeRatingQuestionsResponse.errorBody() != null) {
                val jsonLoginError =
                    JSONObject(
                        attitudeRatingQuestionsResponse.errorBody()!!.charStream().readText()
                    )
                _attitudeRatingQuestionsDataResponseLiveData.postValue(
                    NetworkResult.Error(
                        jsonLoginError.getString(
                            "message"
                        )
                    )
                )

            }
        } catch (e: Exception) {
            Log.d(Constants.TAG, e.message.toString())
            _attitudeRatingQuestionsDataResponseLiveData.postValue(NetworkResult.Error(e.message))
        }
    }


    suspend fun saveAttitudeRatingQuesAndFinishTest(result_id: Int, saveAttitudeRatingQuesRequest: SaveAttitudeRatingQuesRequest) {

        try {
            val saveAttitudeRatingQuesResponse =
                dashboardApi.saveAttitudeRatingQuesAndFinishTest(result_id, saveAttitudeRatingQuesRequest)

            if (saveAttitudeRatingQuesResponse.isSuccessful && saveAttitudeRatingQuesResponse.body() != null) {
                _saveAttitudeRatingQuesAndFinishTestResponseLiveData.postValue(
                    NetworkResult.Success(
                        saveAttitudeRatingQuesResponse.body()!!
                    )
                )
            } else if (saveAttitudeRatingQuesResponse.errorBody() != null) {
                val jsonLoginError =
                    JSONObject(saveAttitudeRatingQuesResponse.errorBody()!!.charStream().readText())
                _saveAttitudeRatingQuesAndFinishTestResponseLiveData.postValue(
                    NetworkResult.Error(
                        jsonLoginError.getString(
                            "message"
                        )
                    )
                )

            }
        } catch (e: Exception) {
            Log.d(Constants.TAG, e.message.toString())
            _saveAttitudeRatingQuesAndFinishTestResponseLiveData.postValue(NetworkResult.Error(e.message))
        }
    }


}