/*
 * Copyright © 2020 By Geeks Empire.
 *
 * Created by Elias Fazel on 2/7/20 10:53 AM
 * Last modified 2/7/20 10:53 AM
 *
 * Licensed Under MIT License.
 * https://opensource.org/licenses/MIT
 */

package net.geeksempire.geeky.gify.BrowseGif.Extension

data class GiphySearchParameter(var categoryName: String, var requestLimit: Int = 10, var requestOffset: Int = 0, var searchRating: String = "G", var searchLanguage: String = "en")

class SearchAddress {

    companion object {
        private const val GIPHY_ENDPOINT_API_KEY = "uvO9uLD5yDRHZ1NgI6zOroJKLsJbvNDQ"
    }

    fun generateSearchLink(giphySearchParameter: GiphySearchParameter) : String{

        return "https://api.giphy.com/v1/gifs/search?" +
                "api_key=${GIPHY_ENDPOINT_API_KEY}" +
                "&q=${giphySearchParameter.categoryName}" +
                "&limit=${giphySearchParameter.requestLimit}" +
                "&offset=${giphySearchParameter.requestOffset}" +
                "&rating=${giphySearchParameter.searchRating}" +
                "&lang=${giphySearchParameter.searchLanguage}"
    }
}