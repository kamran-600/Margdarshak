package com.margdarshakendra.margdarshak.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.margdarshakendra.margdarshak.api.DashboardApi
import com.margdarshakendra.margdarshak.models.AttitudeQuestionsResponse
import com.margdarshakendra.margdarshak.models.AttitudeRatingQuesResponse
import com.margdarshakendra.margdarshak.models.CRMResponse
import com.margdarshakendra.margdarshak.models.CalenderSchedulesResponse
import com.margdarshakendra.margdarshak.models.CallRequest
import com.margdarshakendra.margdarshak.models.CallResponse
import com.margdarshakendra.margdarshak.models.ClientDataResponse
import com.margdarshakendra.margdarshak.models.CounsellingDataResponse
import com.margdarshakendra.margdarshak.models.DataRequest
import com.margdarshakendra.margdarshak.models.EditOrganisedStudyScheduleResponse
import com.margdarshakendra.margdarshak.models.GetInteractiveQuestionRequest
import com.margdarshakendra.margdarshak.models.GetOraniserLangaugesReponse
import com.margdarshakendra.margdarshak.models.GetOrganisedStudySchedulesResponse
import com.margdarshakendra.margdarshak.models.GetOrganiserCoursesResponse
import com.margdarshakendra.margdarshak.models.GetOrganiserLessonsRequest
import com.margdarshakendra.margdarshak.models.GetOrganiserLessonsResponse
import com.margdarshakendra.margdarshak.models.GetOrganiserStudyTimeRequest
import com.margdarshakendra.margdarshak.models.GetOrganiserStudyTimeResponse
import com.margdarshakendra.margdarshak.models.GetOrganiserSubjectsRequest
import com.margdarshakendra.margdarshak.models.GetOrganiserSubjectsResponse
import com.margdarshakendra.margdarshak.models.GetOrganiserUtilRequest
import com.margdarshakendra.margdarshak.models.GetOrganiserWeightagesResponse
import com.margdarshakendra.margdarshak.models.HiringDataResponse
import com.margdarshakendra.margdarshak.models.InteractiveCoursesResponse
import com.margdarshakendra.margdarshak.models.InteractiveSubjectsResponse
import com.margdarshakendra.margdarshak.models.InteractiveTeachersRequest
import com.margdarshakendra.margdarshak.models.InteractiveTeachersResponse
import com.margdarshakendra.margdarshak.models.InteractiveTestDetailsResponse
import com.margdarshakendra.margdarshak.models.LogoutRequest
import com.margdarshakendra.margdarshak.models.LogoutResponse
import com.margdarshakendra.margdarshak.models.McqResultsRequest
import com.margdarshakendra.margdarshak.models.McqResultsResponse
import com.margdarshakendra.margdarshak.models.OrganisedStudyScheduleRequest
import com.margdarshakendra.margdarshak.models.OrganisedStudyScheduleResponse
import com.margdarshakendra.margdarshak.models.PerformanceLessonWiseResponse
import com.margdarshakendra.margdarshak.models.PerformanceSubjectWiseResponse
import com.margdarshakendra.margdarshak.models.QuestionsResponse
import com.margdarshakendra.margdarshak.models.SaveAptitudeAnswerRequest
import com.margdarshakendra.margdarshak.models.SaveAptitudeAnswerResponse
import com.margdarshakendra.margdarshak.models.SaveAttitudeQuestionsRequest
import com.margdarshakendra.margdarshak.models.SaveAttitudeQuestionsResponse
import com.margdarshakendra.margdarshak.models.SaveAttitudeRatingQuesRequest
import com.margdarshakendra.margdarshak.models.SaveAttitudeRatingQuesResponse
import com.margdarshakendra.margdarshak.models.SaveFcmTokenRequest
import com.margdarshakendra.margdarshak.models.SaveFcmTokenResponse
import com.margdarshakendra.margdarshak.models.SaveInteractiveAnsResponse
import com.margdarshakendra.margdarshak.models.SaveInteractiveAnswerRequest
import com.margdarshakendra.margdarshak.models.SaveSkillTestAnswerRequest
import com.margdarshakendra.margdarshak.models.SaveSkillTestAnswerResponse
import com.margdarshakendra.margdarshak.models.SendEmailRequest
import com.margdarshakendra.margdarshak.models.SkillResponse
import com.margdarshakendra.margdarshak.models.SkillTestQuestionResponse
import com.margdarshakendra.margdarshak.models.SmsRequest
import com.margdarshakendra.margdarshak.models.SmsResponse
import com.margdarshakendra.margdarshak.models.StartAssessmentRequest
import com.margdarshakendra.margdarshak.models.StartAssessmentResponse
import com.margdarshakendra.margdarshak.models.StartAttitudeAssessmentResponse
import com.margdarshakendra.margdarshak.models.StartInteractiveTestRequest
import com.margdarshakendra.margdarshak.models.StartInteractiveTestResponse
import com.margdarshakendra.margdarshak.models.StartSkillTestRequest
import com.margdarshakendra.margdarshak.models.StartSkillTestResponse
import com.margdarshakendra.margdarshak.models.TemplateResponse
import com.margdarshakendra.margdarshak.models.UpdateTimerRequest
import com.margdarshakendra.margdarshak.models.UpdateTimerResponse
import com.margdarshakendra.margdarshak.models.UserAccessResponse
import com.margdarshakendra.margdarshak.utils.Constants.TAG
import com.margdarshakendra.margdarshak.utils.NetworkResult
import org.json.JSONObject
import javax.inject.Inject

class DashboardRepository @Inject constructor(private val dashboardApi: DashboardApi) {

    private val _userAccessResponseLiveData = MutableLiveData<NetworkResult<UserAccessResponse>>()
    val userAccessResponseLiveData get() = _userAccessResponseLiveData

    private val _logoutResponseLiveData = MutableLiveData<NetworkResult<LogoutResponse>>()
    val logoutResponseLiveData: LiveData<NetworkResult<LogoutResponse>>
        get() = _logoutResponseLiveData

    private val _hiringDataResponseLiveData = MutableLiveData<NetworkResult<HiringDataResponse>>()
    val hiringDataResponseLiveData get() = _hiringDataResponseLiveData

    private val _hiringDataListLiveData = MutableLiveData<List<HiringDataResponse.Data>>()
    val hiringDataListLiveData get() = _hiringDataListLiveData


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
    val counsellingDataResponseLiveData get() = _counsellingDataResponseLiveData

    private val _clientDataResponseLiveData = MutableLiveData<NetworkResult<ClientDataResponse>>()
    val clientDataResponseLiveData get() = _clientDataResponseLiveData


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


    private val _skillsResponseLiveData = MutableLiveData<NetworkResult<SkillResponse>>()
    val skillsResponseLiveData get() = _skillsResponseLiveData

    private val _startSkillTestResponseLiveData =
        MutableLiveData<NetworkResult<StartSkillTestResponse>>()
    val startSkillTestResponseLiveData: LiveData<NetworkResult<StartSkillTestResponse>>
        get() = _startSkillTestResponseLiveData


    private val _skillTestQuestionResponseLiveData =
        MutableLiveData<NetworkResult<SkillTestQuestionResponse>>()
    val skillTestQuestionResponseLiveData: LiveData<NetworkResult<SkillTestQuestionResponse>>
        get() = _skillTestQuestionResponseLiveData

    private val _updateTimerResponseLiveData =
        MutableLiveData<NetworkResult<UpdateTimerResponse>>()
    val updateTimerResponseLiveData: LiveData<NetworkResult<UpdateTimerResponse>>
        get() = _updateTimerResponseLiveData

    private val _saveSkillTestAnswerResponseLiveData =
        MutableLiveData<NetworkResult<SaveSkillTestAnswerResponse>>()
    val saveSkillTestAnswerResponseLiveData: LiveData<NetworkResult<SaveSkillTestAnswerResponse>>
        get() = _saveSkillTestAnswerResponseLiveData

    private val _organiserCoursesLiveData =
        MutableLiveData<NetworkResult<GetOrganiserCoursesResponse>>()
    val organiserCoursesLiveData: LiveData<NetworkResult<GetOrganiserCoursesResponse>>
        get() = _organiserCoursesLiveData

    private val _organiserLanguagesLiveData =
        MutableLiveData<NetworkResult<GetOraniserLangaugesReponse>>()
    val organiserLanguagesLiveData: LiveData<NetworkResult<GetOraniserLangaugesReponse>>
        get() = _organiserLanguagesLiveData

    private val _organiserWeightagesLiveData =
        MutableLiveData<NetworkResult<GetOrganiserWeightagesResponse>>()
    val organiserWeightagesLiveData: LiveData<NetworkResult<GetOrganiserWeightagesResponse>>
        get() = _organiserWeightagesLiveData

    private val _organiserSubjectsLiveData =
        MutableLiveData<NetworkResult<GetOrganiserSubjectsResponse>>()
    val organiserSubjectsLiveData: LiveData<NetworkResult<GetOrganiserSubjectsResponse>>
        get() = _organiserSubjectsLiveData

    private val _organiserLessonsLiveData =
        MutableLiveData<NetworkResult<GetOrganiserLessonsResponse>>()
    val organiserLessonsLiveData: LiveData<NetworkResult<GetOrganiserLessonsResponse>>
        get() = _organiserLessonsLiveData

    private val _organiserStudyTimeLiveData =
        MutableLiveData<NetworkResult<GetOrganiserStudyTimeResponse>>()
    val organiserStudyTimeLiveData: LiveData<NetworkResult<GetOrganiserStudyTimeResponse>>
        get() = _organiserStudyTimeLiveData

    private val _organisedStudySchedulesLiveData =
        MutableLiveData<NetworkResult<GetOrganisedStudySchedulesResponse>>()
    val organisedStudySchedulesLiveData get() = _organisedStudySchedulesLiveData

    private val _scheduleOrganisedStudyLiveData =
        MutableLiveData<NetworkResult<OrganisedStudyScheduleResponse>>()
    val scheduleOrganisedStudyLiveData: LiveData<NetworkResult<OrganisedStudyScheduleResponse>>
        get() = _scheduleOrganisedStudyLiveData

    private val _editScheduledOrganisedStudyLiveData =
        MutableLiveData<NetworkResult<EditOrganisedStudyScheduleResponse>>()
    val editScheduledOrganisedStudyLiveData: LiveData<NetworkResult<EditOrganisedStudyScheduleResponse>>
        get() = _editScheduledOrganisedStudyLiveData

    private val _interactiveCoursesLiveData =
        MutableLiveData<NetworkResult<InteractiveCoursesResponse>>()

    val interactiveCoursesLiveData: LiveData<NetworkResult<InteractiveCoursesResponse>>
        get() = _interactiveCoursesLiveData

    private val _interactiveSubjectsLiveData =
        MutableLiveData<NetworkResult<InteractiveSubjectsResponse>>()

    val interactiveSubjectsLiveData: LiveData<NetworkResult<InteractiveSubjectsResponse>>
        get() = _interactiveSubjectsLiveData


    private val _interactiveTeachersLiveData =
        MutableLiveData<NetworkResult<InteractiveTeachersResponse>>()

    val interactiveTeachersLiveData: LiveData<NetworkResult<InteractiveTeachersResponse>>
        get() = _interactiveTeachersLiveData


    private val _interactiveTestDetailsLiveData =
        MutableLiveData<NetworkResult<InteractiveTestDetailsResponse>>()

    val interactiveTestDetailsLiveData: LiveData<NetworkResult<InteractiveTestDetailsResponse>>
        get() = _interactiveTestDetailsLiveData


    private val _startInteractiveTestLiveData =
        MutableLiveData<NetworkResult<StartInteractiveTestResponse>>()

    val startInteractiveTestLiveData: LiveData<NetworkResult<StartInteractiveTestResponse>>
        get() = _startInteractiveTestLiveData


    private val _interactiveTestQuestionLiveData =
        MutableLiveData<NetworkResult<GetInteractiveQuestionRequest>>()

    val interactiveTestQuestionLiveData: LiveData<NetworkResult<GetInteractiveQuestionRequest>>
        get() = _interactiveTestQuestionLiveData


    private val _saveInteractiveAnsResponseLiveData =
        MutableLiveData<NetworkResult<SaveInteractiveAnsResponse>>()

    val saveInteractiveAnsResponseLiveData: LiveData<NetworkResult<SaveInteractiveAnsResponse>>
        get() = _saveInteractiveAnsResponseLiveData

    private val _resultLessonsLiveData =
        MutableLiveData<NetworkResult<GetOrganiserLessonsResponse>>()

    val resultLessonsLiveData: LiveData<NetworkResult<GetOrganiserLessonsResponse>>
        get() = _resultLessonsLiveData


    private val _mcqResultsLiveData =
        MutableLiveData<NetworkResult<McqResultsResponse>>()

    val mcqResultsLiveData: LiveData<NetworkResult<McqResultsResponse>>
        get() = _mcqResultsLiveData


    private val _subjectWisePerformanceLiveData =
        MutableLiveData<NetworkResult<PerformanceSubjectWiseResponse>>()

    val subjectWisePerformanceLiveData: LiveData<NetworkResult<PerformanceSubjectWiseResponse>>
        get() = _subjectWisePerformanceLiveData


    private val _lessonWisePerformanceLiveData =
        MutableLiveData<NetworkResult<PerformanceLessonWiseResponse>>()

    val lessonWisePerformanceLiveData: LiveData<NetworkResult<PerformanceLessonWiseResponse>>
        get() = _lessonWisePerformanceLiveData


    private val _saveFcmTokenLiveData =
        MutableLiveData<NetworkResult<SaveFcmTokenResponse>>()

    val saveFcmTokenLiveData: LiveData<NetworkResult<SaveFcmTokenResponse>>
        get() = _saveFcmTokenLiveData


    private val _calenderSchedulesLiveData =
        MutableLiveData<NetworkResult<CalenderSchedulesResponse>>()

    val calenderSchedulesLiveData: LiveData<NetworkResult<CalenderSchedulesResponse>>
        get() = _calenderSchedulesLiveData

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
            Log.d(TAG, e.message.toString())
            _userAccessResponseLiveData.postValue(NetworkResult.Error(e.message))
        }
    }

    suspend fun logout(logoutRequest: LogoutRequest) {
        try {
            val logoutResponse = dashboardApi.logout(logoutRequest)

            if (logoutResponse.isSuccessful && logoutResponse.body() != null) {
                _logoutResponseLiveData.postValue(NetworkResult.Success(logoutResponse.body()!!))
            } else if (logoutResponse.errorBody() != null) {
                val jsonLoginError =
                    JSONObject(logoutResponse.errorBody()!!.charStream().readText())
                _logoutResponseLiveData.postValue(NetworkResult.Error(jsonLoginError.getString("message")))

            }
        } catch (e: Exception) {
            Log.d(TAG, e.message.toString())
            _logoutResponseLiveData.postValue(NetworkResult.Error(e.message))
        }
    }

    suspend fun getHiringData(dataRequest: DataRequest) {
        Log.d(TAG, "getHiringData method called")

        try {
            val hiringDataResponse = dashboardApi.getHiringData(dataRequest)

            if (hiringDataResponse.isSuccessful && hiringDataResponse.body() != null) {
                _hiringDataListLiveData.postValue(hiringDataResponse.body()!!.data)
                Log.d(TAG, "hiring list \n" + _hiringDataListLiveData.value.toString())
                _hiringDataResponseLiveData.postValue(NetworkResult.Success(hiringDataResponse.body()!!))
            } else if (hiringDataResponse.errorBody() != null) {
                val jsonHiringError =
                    JSONObject(hiringDataResponse.errorBody()!!.charStream().readText())
                _hiringDataResponseLiveData.postValue(
                    NetworkResult.Error(
                        jsonHiringError.getString(
                            "message"
                        )
                    )
                )

            }
        } catch (e: Exception) {
            Log.d(TAG, e.message.toString())
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
            Log.d(TAG, e.message.toString())
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
            Log.d(TAG, e.message.toString())
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
            Log.d(TAG, e.message.toString())
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
            Log.d(TAG, e.message.toString())
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
            Log.d(TAG, e.message.toString())
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
            Log.d(TAG, e.message.toString())
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
            Log.d(TAG, e.message.toString())
            _clientDataResponseLiveData.postValue(NetworkResult.Error(e.message))
        }
    }

    suspend fun getStartAptitudeAssessment(startAssessmentRequest: StartAssessmentRequest) {
        _startAptitudeAssessmentResponseLiveData.postValue(NetworkResult.Loading())
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
            Log.d(TAG, e.message.toString())
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
            Log.d(TAG, e.message.toString())
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
            Log.d(TAG, e.message.toString())
            _saveAptitudeAnswerResponseLiveData.postValue(NetworkResult.Error(e.message))
        }
    }

    suspend fun getStartAttitudeAssessment(startAssessmentRequest: StartAssessmentRequest) {
        _startAttitudeAssessmentResponseLiveData.postValue(NetworkResult.Loading())
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
            Log.d(TAG, e.message.toString())
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
            Log.d(TAG, e.message.toString())
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
            Log.d(TAG, e.message.toString())
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
            Log.d(TAG, e.message.toString())
            _attitudeRatingQuestionsDataResponseLiveData.postValue(NetworkResult.Error(e.message))
        }
    }


    suspend fun saveAttitudeRatingQuesAndFinishTest(
        result_id: Int,
        saveAttitudeRatingQuesRequest: SaveAttitudeRatingQuesRequest
    ) {

        try {
            val saveAttitudeRatingQuesResponse =
                dashboardApi.saveAttitudeRatingQuesAndFinishTest(
                    result_id,
                    saveAttitudeRatingQuesRequest
                )

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
            Log.d(TAG, e.message.toString())
            _saveAttitudeRatingQuesAndFinishTestResponseLiveData.postValue(NetworkResult.Error(e.message))
        }
    }

    suspend fun getSkills() {

        try {
            val skillsResponse = dashboardApi.getSkills()

            if (skillsResponse.isSuccessful && skillsResponse.body() != null) {
                _skillsResponseLiveData.postValue(
                    NetworkResult.Success(
                        skillsResponse.body()!!
                    )
                )
            } else if (skillsResponse.errorBody() != null) {
                val jsonLoginError =
                    JSONObject(skillsResponse.errorBody()!!.charStream().readText())
                _skillsResponseLiveData.postValue(
                    NetworkResult.Error(
                        jsonLoginError.getString(
                            "message"
                        )
                    )
                )

            }
        } catch (e: Exception) {
            Log.d(TAG, e.message.toString())
            _skillsResponseLiveData.postValue(NetworkResult.Error(e.message))
        }
    }

    suspend fun getStartSkillTest(startSkillTestRequest: StartSkillTestRequest) {
        _startSkillTestResponseLiveData.postValue(NetworkResult.Loading())
        try {
            val startSkillTestResponse = dashboardApi.getStartSkillTest(startSkillTestRequest)

            if (startSkillTestResponse.isSuccessful && startSkillTestResponse.body() != null) {
                _startSkillTestResponseLiveData.postValue(
                    NetworkResult.Success(
                        startSkillTestResponse.body()!!
                    )
                )
            } else if (startSkillTestResponse.errorBody() != null) {
                val jsonLoginError =
                    JSONObject(startSkillTestResponse.errorBody()!!.charStream().readText())
                _startSkillTestResponseLiveData.postValue(
                    NetworkResult.Error(
                        jsonLoginError.getString(
                            "message"
                        )
                    )
                )

            }
        } catch (e: Exception) {
            Log.d(TAG, e.message.toString())
            _startSkillTestResponseLiveData.postValue(NetworkResult.Error(e.message))
        }
    }

    suspend fun getSkillTestQuestions(result_id: Int, page_no: Int) {

        try {
            val skillTestQuestionResponse =
                dashboardApi.getSkillTestQuestions(result_id, page_no)

            if (skillTestQuestionResponse.isSuccessful && skillTestQuestionResponse.body() != null) {
                _skillTestQuestionResponseLiveData.postValue(
                    NetworkResult.Success(
                        skillTestQuestionResponse.body()!!
                    )
                )
            } else if (skillTestQuestionResponse.errorBody() != null) {
                val jsonSkillTestQuestionError =
                    JSONObject(skillTestQuestionResponse.errorBody()!!.charStream().readText())
                _skillTestQuestionResponseLiveData.postValue(
                    NetworkResult.Error(
                        jsonSkillTestQuestionError.getString(
                            "message"
                        )
                    )
                )

            }
        } catch (e: Exception) {
            Log.d(TAG, e.message.toString())
            _skillTestQuestionResponseLiveData.postValue(NetworkResult.Error(e.message))
        }
    }

    suspend fun updateTimer(updateTimerRequest: UpdateTimerRequest) {

        try {
            val updateTimerResponse =
                dashboardApi.updateTimer(updateTimerRequest)

            if (updateTimerResponse.isSuccessful && updateTimerResponse.body() != null) {
                _updateTimerResponseLiveData.postValue(
                    NetworkResult.Success(
                        updateTimerResponse.body()!!
                    )
                )
            } else if (updateTimerResponse.errorBody() != null) {
                val jsonSkillTestQuestionError =
                    JSONObject(updateTimerResponse.errorBody()!!.charStream().readText())
                _updateTimerResponseLiveData.postValue(
                    NetworkResult.Error(
                        jsonSkillTestQuestionError.getString(
                            "message"
                        )
                    )
                )

            }
        } catch (e: Exception) {
            Log.d(TAG, e.message.toString())
            _updateTimerResponseLiveData.postValue(NetworkResult.Error(e.message))
        }
    }

    suspend fun saveSkillTestAnswer(saveSkillTestAnswerRequest: SaveSkillTestAnswerRequest) {

        try {
            val saveSkillTestAnswerResponse =
                dashboardApi.saveSkillTestAnswer(saveSkillTestAnswerRequest)

            if (saveSkillTestAnswerResponse.isSuccessful && saveSkillTestAnswerResponse.body() != null) {
                _saveSkillTestAnswerResponseLiveData.postValue(
                    NetworkResult.Success(
                        saveSkillTestAnswerResponse.body()!!
                    )
                )
            } else if (saveSkillTestAnswerResponse.errorBody() != null) {
                val jsonSkillTestQuestionError =
                    JSONObject(saveSkillTestAnswerResponse.errorBody()!!.charStream().readText())
                _saveSkillTestAnswerResponseLiveData.postValue(
                    NetworkResult.Error(
                        jsonSkillTestQuestionError.getString(
                            "message"
                        )
                    )
                )

            }
        } catch (e: Exception) {
            Log.d(TAG, e.message.toString())
            _saveSkillTestAnswerResponseLiveData.postValue(NetworkResult.Error(e.message))
        }
    }

    suspend fun getOrganiserCourses(getOrganiserUtilRequest: GetOrganiserUtilRequest) {
        _organiserCoursesLiveData.postValue(NetworkResult.Loading())
        try {
            val coursesResponse =
                dashboardApi.getOrganiserCoursesRequest(getOrganiserUtilRequest)

            if (coursesResponse.isSuccessful && coursesResponse.body() != null) {
                _organiserCoursesLiveData.postValue(
                    NetworkResult.Success(
                        coursesResponse.body()!!
                    )
                )
            } else if (coursesResponse.errorBody() != null) {
                val jsonSkillTestQuestionError =
                    JSONObject(coursesResponse.errorBody()!!.charStream().readText())
                _organiserCoursesLiveData.postValue(
                    NetworkResult.Error(
                        jsonSkillTestQuestionError.getString(
                            "message"
                        )
                    )
                )

            }
        } catch (e: Exception) {
            Log.d(TAG, e.message.toString())
            _organiserCoursesLiveData.postValue(NetworkResult.Error(e.message))
        }
    }

    suspend fun getOrganiserLanguages(getOrganiserUtilRequest: GetOrganiserUtilRequest) {
        _organiserLanguagesLiveData.postValue(NetworkResult.Loading())

        try {
            val languagesResponse =
                dashboardApi.getOrganiserLanguagesRequest(getOrganiserUtilRequest)

            if (languagesResponse.isSuccessful && languagesResponse.body() != null) {
                _organiserLanguagesLiveData.postValue(
                    NetworkResult.Success(
                        languagesResponse.body()!!
                    )
                )
            } else if (languagesResponse.errorBody() != null) {
                val jsonSkillTestQuestionError =
                    JSONObject(languagesResponse.errorBody()!!.charStream().readText())
                _organiserLanguagesLiveData.postValue(
                    NetworkResult.Error(
                        jsonSkillTestQuestionError.getString(
                            "message"
                        )
                    )
                )

            }
        } catch (e: Exception) {
            Log.d(TAG, e.message.toString())
            _organiserLanguagesLiveData.postValue(NetworkResult.Error(e.message))
        }
    }

    suspend fun getOrganiserWeightages(getOrganiserUtilRequest: GetOrganiserUtilRequest) {
        _organiserWeightagesLiveData.postValue(NetworkResult.Loading())

        try {
            val weightagesResponse =
                dashboardApi.getOrganiserWeightagesRequest(getOrganiserUtilRequest)

            if (weightagesResponse.isSuccessful && weightagesResponse.body() != null) {
                _organiserWeightagesLiveData.postValue(
                    NetworkResult.Success(
                        weightagesResponse.body()!!
                    )
                )
            } else if (weightagesResponse.errorBody() != null) {
                val jsonSkillTestQuestionError =
                    JSONObject(weightagesResponse.errorBody()!!.charStream().readText())
                _organiserWeightagesLiveData.postValue(
                    NetworkResult.Error(
                        jsonSkillTestQuestionError.getString(
                            "message"
                        )
                    )
                )

            }
        } catch (e: Exception) {
            Log.d(TAG, e.message.toString())
            _organiserWeightagesLiveData.postValue(NetworkResult.Error(e.message))
        }
    }

    suspend fun getOrganiserSubjects(getOrganiserSubjectsRequest: GetOrganiserSubjectsRequest) {
        _organiserSubjectsLiveData.postValue(NetworkResult.Loading())

        try {
            val subjectsResponse =
                dashboardApi.getOrganiserSubjectsRequest(getOrganiserSubjectsRequest)

            if (subjectsResponse.isSuccessful && subjectsResponse.body() != null) {
                _organiserSubjectsLiveData.postValue(
                    NetworkResult.Success(
                        subjectsResponse.body()!!
                    )
                )
            } else if (subjectsResponse.errorBody() != null) {
                val jsonSkillTestQuestionError =
                    JSONObject(subjectsResponse.errorBody()!!.charStream().readText())
                _organiserSubjectsLiveData.postValue(
                    NetworkResult.Error(
                        jsonSkillTestQuestionError.getString(
                            "message"
                        )
                    )
                )

            }
        } catch (e: Exception) {
            Log.d(TAG, e.message.toString())
            _organiserSubjectsLiveData.postValue(NetworkResult.Error(e.message))
        }
    }

    suspend fun getOrganiserLessons(getOrganiserLessonsRequest: GetOrganiserLessonsRequest) {
        _organiserLessonsLiveData.postValue(NetworkResult.Loading())

        try {
            val lessonsResponse =
                dashboardApi.getOrganiserLessonsRequest(getOrganiserLessonsRequest)

            if (lessonsResponse.isSuccessful && lessonsResponse.body() != null) {
                _organiserLessonsLiveData.postValue(
                    NetworkResult.Success(
                        lessonsResponse.body()!!
                    )
                )
            } else if (lessonsResponse.errorBody() != null) {
                val jsonError =
                    JSONObject(lessonsResponse.errorBody()!!.charStream().readText())
                _organiserLessonsLiveData.postValue(
                    NetworkResult.Error(
                        jsonError.getString(
                            "message"
                        )
                    )
                )

            }
        } catch (e: Exception) {
            Log.d(TAG, e.message.toString())
            _organiserLessonsLiveData.postValue(NetworkResult.Error(e.message))
        }
    }


    suspend fun getOrganiserStudyTime(getOrganiserStudyTimeRequest: GetOrganiserStudyTimeRequest) {
        _organiserStudyTimeLiveData.postValue(NetworkResult.Loading())

        try {
            val studyTimeResponse =
                dashboardApi.getOrganiserStudyTime(getOrganiserStudyTimeRequest)

            if (studyTimeResponse.isSuccessful && studyTimeResponse.body() != null) {
                _organiserStudyTimeLiveData.postValue(
                    NetworkResult.Success(
                        studyTimeResponse.body()!!
                    )
                )
            } else if (studyTimeResponse.errorBody() != null) {
                Log.d(TAG, studyTimeResponse.errorBody()!!.string())
                val jsonError =
                    JSONObject(studyTimeResponse.errorBody()!!.charStream().readText())
                _organiserStudyTimeLiveData.postValue(
                    NetworkResult.Error(
                        jsonError.getString(
                            "message"
                        )
                    )
                )

            }
        } catch (e: Exception) {
            Log.d(TAG, e.message.toString())
            _organiserStudyTimeLiveData.postValue(NetworkResult.Error(e.message))
        }
    }


    suspend fun getOrganisedStudySchedules() {
        _organisedStudySchedulesLiveData.postValue(NetworkResult.Loading())

        try {
            val getOrganisedStudySchedulesResponse =
                dashboardApi.getOrganisedStudySchedules()

            if (getOrganisedStudySchedulesResponse.isSuccessful && getOrganisedStudySchedulesResponse.body() != null) {
                _organisedStudySchedulesLiveData.postValue(
                    NetworkResult.Success(
                        getOrganisedStudySchedulesResponse.body()!!
                    )
                )
            } else if (getOrganisedStudySchedulesResponse.errorBody() != null) {
                Log.d(TAG, getOrganisedStudySchedulesResponse.errorBody()!!.string())
                val jsonError =
                    JSONObject(
                        getOrganisedStudySchedulesResponse.errorBody()!!.charStream().readText()
                    )
                _organisedStudySchedulesLiveData.postValue(
                    NetworkResult.Error(
                        jsonError.getString(
                            "message"
                        )
                    )
                )

            }
        } catch (e: Exception) {
            Log.d(TAG, e.message.toString())
            _organisedStudySchedulesLiveData.postValue(NetworkResult.Error(e.message))
        }
    }

    suspend fun scheduleOrganisedStudy(organisedStudyScheduleRequest: OrganisedStudyScheduleRequest) {
        _scheduleOrganisedStudyLiveData.postValue(NetworkResult.Loading())

        try {
            val organisedStudyScheduleResponse =
                dashboardApi.scheduleOrganisedStudy(organisedStudyScheduleRequest)

            if (organisedStudyScheduleResponse.isSuccessful && organisedStudyScheduleResponse.body() != null) {
                _scheduleOrganisedStudyLiveData.postValue(
                    NetworkResult.Success(
                        organisedStudyScheduleResponse.body()!!
                    )
                )
            } else if (organisedStudyScheduleResponse.errorBody() != null) {
                Log.d(TAG, organisedStudyScheduleResponse.errorBody()!!.string())
                val jsonError =
                    JSONObject(organisedStudyScheduleResponse.errorBody()!!.charStream().readText())
                _scheduleOrganisedStudyLiveData.postValue(
                    NetworkResult.Error(
                        jsonError.getString(
                            "message"
                        )
                    )
                )

            }
        } catch (e: Exception) {
            Log.d(TAG, e.message.toString())
            _scheduleOrganisedStudyLiveData.postValue(NetworkResult.Error(e.message))
        }
    }

    suspend fun editOrganisedStudySchedules(scheduleID: Int) {
        _editScheduledOrganisedStudyLiveData.postValue(NetworkResult.Loading())

        try {
            val editOrganisedStudyScheduleResponse =
                dashboardApi.editOrganisedStudySchedules(scheduleID)

            if (editOrganisedStudyScheduleResponse.isSuccessful && editOrganisedStudyScheduleResponse.body() != null) {
                _editScheduledOrganisedStudyLiveData.postValue(
                    NetworkResult.Success(
                        editOrganisedStudyScheduleResponse.body()!!
                    )
                )
            } else if (editOrganisedStudyScheduleResponse.errorBody() != null) {
                Log.d(TAG, editOrganisedStudyScheduleResponse.errorBody()!!.string())
                val jsonError =
                    JSONObject(
                        editOrganisedStudyScheduleResponse.errorBody()!!.charStream().readText()
                    )
                _editScheduledOrganisedStudyLiveData.postValue(
                    NetworkResult.Error(
                        jsonError.getString(
                            "message"
                        )
                    )
                )

            }
        } catch (e: Exception) {
            Log.d(TAG, e.message.toString())
            _editScheduledOrganisedStudyLiveData.postValue(NetworkResult.Error(e.message))
        }
    }

    suspend fun getInteractiveCoursesRequest(getOrganiserUtilRequest: GetOrganiserUtilRequest) {
        _interactiveCoursesLiveData.postValue(NetworkResult.Loading())

        try {
            val interactiveCoursesResponse =
                dashboardApi.getInteractiveCoursesRequest(getOrganiserUtilRequest)

            if (interactiveCoursesResponse.isSuccessful && interactiveCoursesResponse.body() != null) {
                _interactiveCoursesLiveData.postValue(
                    NetworkResult.Success(
                        interactiveCoursesResponse.body()!!
                    )
                )
            } else if (interactiveCoursesResponse.errorBody() != null) {
                Log.d(TAG, interactiveCoursesResponse.errorBody()!!.string())
                val jsonError =
                    JSONObject(
                        interactiveCoursesResponse.errorBody()!!.charStream().readText()
                    )
                _interactiveCoursesLiveData.postValue(
                    NetworkResult.Error(
                        jsonError.getString(
                            "message"
                        )
                    )
                )

            }
        } catch (e: Exception) {
            Log.d(TAG, e.message.toString())
            _interactiveCoursesLiveData.postValue(NetworkResult.Error(e.message))
        }
    }


    suspend fun getInteractiveSubjectsRequest(getOrganiserSubjectsRequest: GetOrganiserSubjectsRequest) {
        _interactiveSubjectsLiveData.postValue(NetworkResult.Loading())

        try {
            val interactiveSubjectsResponse =
                dashboardApi.getInteractiveSubjectsRequest(getOrganiserSubjectsRequest)

            if (interactiveSubjectsResponse.isSuccessful && interactiveSubjectsResponse.body() != null) {
                _interactiveSubjectsLiveData.postValue(
                    NetworkResult.Success(
                        interactiveSubjectsResponse.body()!!
                    )
                )
            } else if (interactiveSubjectsResponse.errorBody() != null) {
                Log.d(TAG, interactiveSubjectsResponse.errorBody()!!.string())
                val jsonError =
                    JSONObject(
                        interactiveSubjectsResponse.errorBody()!!.charStream().readText()
                    )
                _interactiveSubjectsLiveData.postValue(
                    NetworkResult.Error(
                        jsonError.getString(
                            "message"
                        )
                    )
                )

            }
        } catch (e: Exception) {
            Log.d(TAG, e.message.toString())
            _interactiveSubjectsLiveData.postValue(NetworkResult.Error(e.message))
        }
    }


    suspend fun getInteractiveTeachersRequest(interactiveTeachersRequest: InteractiveTeachersRequest) {
        _interactiveTeachersLiveData.postValue(NetworkResult.Loading())

        try {
            val interactiveTeachersResponse =
                dashboardApi.getInteractiveTeachersRequest(interactiveTeachersRequest)

            if (interactiveTeachersResponse.isSuccessful && interactiveTeachersResponse.body() != null) {
                _interactiveTeachersLiveData.postValue(
                    NetworkResult.Success(
                        interactiveTeachersResponse.body()!!
                    )
                )
            } else if (interactiveTeachersResponse.errorBody() != null) {
                Log.d(TAG, interactiveTeachersResponse.errorBody()!!.string())
                val jsonError =
                    JSONObject(
                        interactiveTeachersResponse.errorBody()!!.charStream().readText()
                    )
                _interactiveTeachersLiveData.postValue(
                    NetworkResult.Error(
                        jsonError.getString(
                            "message"
                        )
                    )
                )

            }
        } catch (e: Exception) {
            Log.d(TAG, e.message.toString())
            _interactiveTeachersLiveData.postValue(NetworkResult.Error(e.message))
        }
    }


    suspend fun getInteractiveTestDetailsRequest(lessonId: Int) {
        _interactiveTestDetailsLiveData.postValue(NetworkResult.Loading())

        try {
            val interactiveTestDetailsResponse =
                dashboardApi.getInteractiveTestDetailsRequest(lessonId)

            if (interactiveTestDetailsResponse.isSuccessful && interactiveTestDetailsResponse.body() != null) {
                _interactiveTestDetailsLiveData.postValue(
                    NetworkResult.Success(
                        interactiveTestDetailsResponse.body()!!
                    )
                )
            } else if (interactiveTestDetailsResponse.errorBody() != null) {
                Log.d(TAG, interactiveTestDetailsResponse.errorBody()!!.string())
                val jsonError =
                    JSONObject(
                        interactiveTestDetailsResponse.errorBody()!!.charStream().readText()
                    )
                _interactiveTestDetailsLiveData.postValue(
                    NetworkResult.Error(
                        jsonError.getString(
                            "message"
                        )
                    )
                )

            }
        } catch (e: Exception) {
            Log.d(TAG, e.message.toString())
            _interactiveTestDetailsLiveData.postValue(NetworkResult.Error(e.message))
        }
    }


    suspend fun startInteractiveTestRequest(
        lessonId: Int,
        startInteractiveTestRequest: StartInteractiveTestRequest
    ) {
        _startInteractiveTestLiveData.postValue(NetworkResult.Loading())

        try {
            val startInteractiveTestResponse =
                dashboardApi.startInteractiveTestRequest(lessonId, startInteractiveTestRequest)

            if (startInteractiveTestResponse.isSuccessful && startInteractiveTestResponse.body() != null) {
                _startInteractiveTestLiveData.postValue(
                    NetworkResult.Success(
                        startInteractiveTestResponse.body()!!
                    )
                )
            } else if (startInteractiveTestResponse.errorBody() != null) {
                Log.d(TAG, startInteractiveTestResponse.errorBody()!!.string())
                val jsonError =
                    JSONObject(
                        startInteractiveTestResponse.errorBody()!!.charStream().readText()
                    )
                _startInteractiveTestLiveData.postValue(
                    NetworkResult.Error(
                        jsonError.getString(
                            "message"
                        )
                    )
                )

            }
        } catch (e: Exception) {
            Log.d(TAG, e.message.toString())
            _startInteractiveTestLiveData.postValue(NetworkResult.Error(e.message))
        }
    }


    suspend fun getInteractiveQuestions(resultId: Int, quesNo: Int) {
        _interactiveTestQuestionLiveData.postValue(NetworkResult.Loading())

        try {
            val interactiveQuestionRequest =
                dashboardApi.getInteractiveQuestions(resultId, quesNo)

            if (interactiveQuestionRequest.isSuccessful && interactiveQuestionRequest.body() != null) {
                _interactiveTestQuestionLiveData.postValue(
                    NetworkResult.Success(
                        interactiveQuestionRequest.body()!!
                    )
                )
            } else if (interactiveQuestionRequest.errorBody() != null) {
                Log.d(TAG, interactiveQuestionRequest.errorBody()!!.string())
                val jsonError =
                    JSONObject(
                        interactiveQuestionRequest.errorBody()!!.charStream().readText()
                    )
                _interactiveTestQuestionLiveData.postValue(
                    NetworkResult.Error(
                        jsonError.getString(
                            "message"
                        )
                    )
                )

            }
        } catch (e: Exception) {
            Log.d(TAG, e.message.toString())
            _interactiveTestQuestionLiveData.postValue(NetworkResult.Error(e.message))
        }
    }


    suspend fun saveInteractiveAnswerRequest(saveInteractiveQuestionRequest: SaveInteractiveAnswerRequest) {
        _saveInteractiveAnsResponseLiveData.postValue(NetworkResult.Loading())

        try {
            val saveInteractiveAnsResponse =
                dashboardApi.saveInteractiveAnswerRequest(saveInteractiveQuestionRequest)

            if (saveInteractiveAnsResponse.isSuccessful && saveInteractiveAnsResponse.body() != null) {
                _saveInteractiveAnsResponseLiveData.postValue(
                    NetworkResult.Success(
                        saveInteractiveAnsResponse.body()!!
                    )
                )
            } else if (saveInteractiveAnsResponse.errorBody() != null) {
                Log.d(TAG, saveInteractiveAnsResponse.errorBody()!!.string())
                val jsonError =
                    JSONObject(
                        saveInteractiveAnsResponse.errorBody()!!.charStream().readText()
                    )
                _saveInteractiveAnsResponseLiveData.postValue(
                    NetworkResult.Error(
                        jsonError.getString(
                            "message"
                        )
                    )
                )

            }
        } catch (e: Exception) {
            Log.d(TAG, e.message.toString())
            _saveInteractiveAnsResponseLiveData.postValue(NetworkResult.Error(e.message))
        }
    }


    suspend fun getResultLessons(getOrganiserLessonsRequest: GetOrganiserLessonsRequest) {
        _resultLessonsLiveData.postValue(NetworkResult.Loading())

        try {
            val resultLessonResponse =
                dashboardApi.getResultLessons(getOrganiserLessonsRequest)

            if (resultLessonResponse.isSuccessful && resultLessonResponse.body() != null) {
                _resultLessonsLiveData.postValue(
                    NetworkResult.Success(
                        resultLessonResponse.body()!!
                    )
                )
            } else if (resultLessonResponse.errorBody() != null) {
                Log.d(TAG, resultLessonResponse.errorBody()!!.string())
                val jsonError =
                    JSONObject(
                        resultLessonResponse.errorBody()!!.charStream().readText()
                    )
                _resultLessonsLiveData.postValue(
                    NetworkResult.Error(
                        jsonError.getString(
                            "message"
                        )
                    )
                )

            }
        } catch (e: Exception) {
            Log.d(TAG, e.message.toString())
            _resultLessonsLiveData.postValue(NetworkResult.Error(e.message))
        }
    }


    suspend fun getMcqResults(mcqResultsRequest: McqResultsRequest) {
        _mcqResultsLiveData.postValue(NetworkResult.Loading())

        try {
            val resultsResponse =
                dashboardApi.getMcqResults(mcqResultsRequest)

            if (resultsResponse.isSuccessful && resultsResponse.body() != null) {
                _mcqResultsLiveData.postValue(
                    NetworkResult.Success(
                        resultsResponse.body()!!
                    )
                )
            } else if (resultsResponse.errorBody() != null) {
                Log.d(TAG, resultsResponse.errorBody()!!.string())
                val jsonError =
                    JSONObject(
                        resultsResponse.errorBody()!!.charStream().readText()
                    )
                _mcqResultsLiveData.postValue(
                    NetworkResult.Error(
                        jsonError.getString(
                            "message"
                        )
                    )
                )

            }
        } catch (e: Exception) {
            Log.d(TAG, e.message.toString())
            _mcqResultsLiveData.postValue(NetworkResult.Error(e.message))
        }
    }


    suspend fun getSubjectWisePerformance(getOrganiserUtilRequest: GetOrganiserUtilRequest) {
        _subjectWisePerformanceLiveData.postValue(NetworkResult.Loading())

        try {
            val subjectWisePerformanceResponse =
                dashboardApi.getSubjectWisePerformance(getOrganiserUtilRequest)

            if (subjectWisePerformanceResponse.isSuccessful && subjectWisePerformanceResponse.body() != null) {
                _subjectWisePerformanceLiveData.postValue(
                    NetworkResult.Success(
                        subjectWisePerformanceResponse.body()!!
                    )
                )
            } else if (subjectWisePerformanceResponse.errorBody() != null) {
                Log.d(TAG, subjectWisePerformanceResponse.errorBody()!!.string())
                val jsonError =
                    JSONObject(
                        subjectWisePerformanceResponse.errorBody()!!.charStream().readText()
                    )
                _subjectWisePerformanceLiveData.postValue(
                    NetworkResult.Error(
                        jsonError.getString(
                            "message"
                        )
                    )
                )

            }
        } catch (e: Exception) {
            Log.d(TAG, e.message.toString())
            _subjectWisePerformanceLiveData.postValue(NetworkResult.Error(e.message))
        }
    }


    suspend fun getLessonWisePerformance(getOrganiserLessonsRequest: GetOrganiserLessonsRequest) {
        _lessonWisePerformanceLiveData.postValue(NetworkResult.Loading())

        try {
            val lessonWisePerformanceResponse =
                dashboardApi.getLessonWisePerformance(getOrganiserLessonsRequest)

            if (lessonWisePerformanceResponse.isSuccessful && lessonWisePerformanceResponse.body() != null) {
                _lessonWisePerformanceLiveData.postValue(
                    NetworkResult.Success(
                        lessonWisePerformanceResponse.body()!!
                    )
                )
            } else if (lessonWisePerformanceResponse.errorBody() != null) {
                Log.d(TAG, lessonWisePerformanceResponse.errorBody()!!.string())
                val jsonError =
                    JSONObject(
                        lessonWisePerformanceResponse.errorBody()!!.charStream().readText()
                    )
                _lessonWisePerformanceLiveData.postValue(
                    NetworkResult.Error(
                        jsonError.getString(
                            "message"
                        )
                    )
                )

            }
        } catch (e: Exception) {
            Log.d(TAG, e.message.toString())
            _lessonWisePerformanceLiveData.postValue(NetworkResult.Error(e.message))
        }
    }


    suspend fun saveFcmTokenRequest(saveFcmTokenRequest: SaveFcmTokenRequest) {
        _saveFcmTokenLiveData.postValue(NetworkResult.Loading())

        try {
            val saveFcmTokenResponse =
                dashboardApi.saveFcmTokenRequest(saveFcmTokenRequest)

            if (saveFcmTokenResponse.isSuccessful && saveFcmTokenResponse.body() != null) {
                _saveFcmTokenLiveData.postValue(
                    NetworkResult.Success(
                        saveFcmTokenResponse.body()!!
                    )
                )
            } else if (saveFcmTokenResponse.errorBody() != null) {
                Log.d(TAG, saveFcmTokenResponse.errorBody()!!.string())
                val jsonError =
                    JSONObject(
                        saveFcmTokenResponse.errorBody()!!.charStream().readText()
                    )
                _saveFcmTokenLiveData.postValue(
                    NetworkResult.Error(
                        jsonError.getString(
                            "message"
                        )
                    )
                )

            }
        } catch (e: Exception) {
            Log.d(TAG, e.message.toString())
            _saveFcmTokenLiveData.postValue(NetworkResult.Error(e.message))
        }
    }


    suspend fun getCalenderSchedules(getOrganiserUtilRequest: GetOrganiserUtilRequest) {
        _calenderSchedulesLiveData.postValue(NetworkResult.Loading())

        try {
            val calenderSchedulesResponse =
                dashboardApi.getCalenderSchedules(getOrganiserUtilRequest)

            if (calenderSchedulesResponse.isSuccessful && calenderSchedulesResponse.body() != null) {
                _calenderSchedulesLiveData.postValue(
                    NetworkResult.Success(
                        calenderSchedulesResponse.body()!!
                    )
                )
            } else if (calenderSchedulesResponse.errorBody() != null) {
                Log.d(TAG, calenderSchedulesResponse.errorBody()!!.string())
                val jsonError =
                    JSONObject(
                        calenderSchedulesResponse.errorBody()!!.charStream().readText()
                    )
                _calenderSchedulesLiveData.postValue(
                    NetworkResult.Error(
                        jsonError.getString(
                            "message"
                        )
                    )
                )

            }
        } catch (e: Exception) {
            Log.d(TAG, e.message.toString())
            _calenderSchedulesLiveData.postValue(NetworkResult.Error(e.message))
        }
    }


}