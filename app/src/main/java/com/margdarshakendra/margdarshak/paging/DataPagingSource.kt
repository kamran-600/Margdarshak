package com.margdarshakendra.margdarshak.paging

import android.util.Log
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.margdarshakendra.margdarshak.api.DashboardApi
import com.margdarshakendra.margdarshak.models.HiringDataResponse
import com.margdarshakendra.margdarshak.utils.Constants.TAG
import org.json.JSONObject
import retrofit2.Response

class DataPagingSource(
    private val dashboardApi: DashboardApi,
    private val mode: String?,
    private val employerId: Int?= null,
    private val postId: Int?= null
) : PagingSource<Int, HiringDataResponse.ApiData.HiringData>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, HiringDataResponse.ApiData.HiringData> {
        try {
            val position = params.key ?: 1
            val response: Response<HiringDataResponse> =
                if (mode == "hiring") dashboardApi.getHiringData(
                    mode,
                    position
                ) else if (mode == "counselling") dashboardApi.getCounsellingData(
                    mode,
                    position
                ) else dashboardApi.getHiringFilteredData(employerId!!, postId!!, pageNo = position)
            if(response.isSuccessful && response.body() != null){
                Log.d(TAG, "$mode  Paging Source"+response.body().toString())
                val responseData = response.body()!!.data
                //if(responseData.data.isEmpty()) return LoadResult.Error(Throwable("Response data is null"))
                return LoadResult.Page(
                    data = responseData.data,
                    prevKey = if(position == 1) null else (position - 1),
                    nextKey = if(position == responseData.last_page) null else (position + 1)
                )
            }
            else if(response.errorBody() != null){
                val jsonError =
                    JSONObject(response.errorBody()!!.charStream().readText())
                Log.d(TAG, jsonError.toString())
                return LoadResult.Error(Throwable(jsonError.getString("message")))
            }
        }catch (e : Exception){
            return LoadResult.Error(e)
        }
        return LoadResult.Invalid()

    }

    override fun getRefreshKey(state: PagingState<Int, HiringDataResponse.ApiData.HiringData>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
     }
}