/*
 * Copyright © 2020 By Geeks Empire.
 *
 * Created by Elias Fazel on 2/7/20 10:53 AM
 * Last modified 2/7/20 10:37 AM
 *
 * Licensed Under MIT License.
 * https://opensource.org/licenses/MIT
 */

package net.geeksempire.geeky.gify.BrowseGif.ViewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import net.geeksempire.geeky.gify.BrowseGif.Adapter.Data.BrowseGifItemData
import net.geeksempire.geeky.gify.BrowseGif.Data.GiphyJsonDataStructure
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

class BrowseGifViewModel : ViewModel() {

    val gifsListData: MutableLiveData<ArrayList<BrowseGifItemData>> by lazy {
        MutableLiveData<ArrayList<BrowseGifItemData>>()
    }

    fun setupGifsBrowserData(rawDataJsonObject: JSONObject, colorsList: ArrayList<String>) = CoroutineScope(SupervisorJob() + Dispatchers.IO).launch {

        try {
            val gifJsonArray: JSONArray = rawDataJsonObject.getJSONArray(GiphyJsonDataStructure.DATA)

            val browseGifItemData = ArrayList<BrowseGifItemData>()

            for (i in 0 until gifJsonArray.length()) {
                val jsonObject : JSONObject = gifJsonArray[i] as JSONObject
                val jsonObjectImage = jsonObject.getJSONObject(GiphyJsonDataStructure.DATA_IMAGES)

                val jsonObjectImageOriginal= jsonObjectImage.getJSONObject(GiphyJsonDataStructure.DATA_ORIGINAL)
                val jsonObjectImageOriginalLink = jsonObjectImageOriginal.getString(GiphyJsonDataStructure.DATA_URL)

                val jsonObjectImagePreview = jsonObjectImage.getJSONObject(GiphyJsonDataStructure.DATA_PREVIEW_GIF)
                val jsonObjectImagePreviewLink = jsonObjectImagePreview.getString(GiphyJsonDataStructure.DATA_URL)

                val aBackgroundColor = colorsList.random()
                browseGifItemData.add(
                    BrowseGifItemData(jsonObjectImagePreviewLink,
                        jsonObjectImageOriginalLink,
                        aBackgroundColor)
                )
                colorsList.remove(aBackgroundColor)
            }

            gifsListData.postValue(browseGifItemData)

        } catch (e: JSONException) {
            e.printStackTrace()
        }
    }
}