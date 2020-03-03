/*
 * Copyright © 2020 By Geeks Empire.
 *
 * Created by Elias Fazel on 3/3/20 4:54 AM
 * Last modified 3/3/20 4:12 AM
 *
 * Licensed Under MIT License.
 * https://opensource.org/licenses/MIT
 */

package net.geeksempire.geeky.gify.ServerConnection.ViewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import net.geeksempire.geeky.gify.BrowseGif.Data.GiphyJsonDataStructure
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

class BrowseGifViewModel : ViewModel() {

    val gifsListData: MutableLiveData<ArrayList<BrowseGifItemData>> by lazy {
        MutableLiveData<ArrayList<BrowseGifItemData>>()
    }

    val gifsListError: MutableLiveData<String> by lazy {
        MutableLiveData<String>()
    }

    fun setupGifsBrowserData(rawDataJsonObject: JSONObject, colorsList: ArrayList<String>) = CoroutineScope(SupervisorJob() + Dispatchers.IO).launch {

        try {
            val gifJsonArray: JSONArray = rawDataJsonObject.getJSONArray(GiphyJsonDataStructure.DATA)

            val browseGifItemData = ArrayList<BrowseGifItemData>()

            for (i in 0 until gifJsonArray.length()) {
                val jsonObject : JSONObject = gifJsonArray[i] as JSONObject

                val linkToGif = jsonObject.getString(GiphyJsonDataStructure.DATA_URL)

                val jsonObjectImage = jsonObject.getJSONObject(GiphyJsonDataStructure.DATA_IMAGES)

                val jsonObjectImageOriginal= jsonObjectImage.getJSONObject(GiphyJsonDataStructure.DATA_IMAGES_ORIGINAL)
                val jsonObjectImageOriginalLink = jsonObjectImageOriginal.getString(
                    GiphyJsonDataStructure.DATA_IMAGES_URL)

                val jsonObjectImagePreview = jsonObjectImage.getJSONObject(GiphyJsonDataStructure.DATA_IMAGES_PREVIEW_GIF)
                val jsonObjectImagePreviewLink = jsonObjectImagePreview.getString(
                    GiphyJsonDataStructure.DATA_IMAGES_URL)

                val aBackgroundColor = colorsList.random()
                browseGifItemData.add(
                    BrowseGifItemData(linkToGif,
                        jsonObjectImagePreviewLink,
                        jsonObjectImageOriginalLink,
                        if (!jsonObject.isNull(GiphyJsonDataStructure.DATA_USER)) {
                            val useJsonObject = jsonObject.getJSONObject(GiphyJsonDataStructure.DATA_USER)
                            GifUserProfile(userName = useJsonObject.getString(GiphyJsonDataStructure.DATA_USER_NAME),
                                userAvatarUrl = useJsonObject.getString(GiphyJsonDataStructure.DATA_USER_AVATAR_URL),
                                isUserVerified = useJsonObject.getBoolean(GiphyJsonDataStructure.DATA_USER_IS_VERIFIED))
                        } else {
                            null
                        },
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