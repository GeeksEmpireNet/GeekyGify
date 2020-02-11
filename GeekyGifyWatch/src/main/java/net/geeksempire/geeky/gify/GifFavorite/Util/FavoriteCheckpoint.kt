/*
 * Copyright © 2020 By Geeks Empire.
 *
 * Created by Elias Fazel on 2/11/20 11:17 AM
 * Last modified 2/11/20 11:17 AM
 *
 * Licensed Under MIT License.
 * https://opensource.org/licenses/MIT
 */

package net.geeksempire.geeky.gify.GifFavorite.Util

import android.content.Context
import com.like.LikeButton
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import net.geeksempire.geeky.gify.RoomDatabase.DatabaseInformation
import net.geeksempire.geeky.gify.RoomDatabase.GifFavorite.GifFavoriteDatabase

class FavoriteCheckpoint (var context: Context) {

    fun checkIfFavorite(likeButton: LikeButton, gifUrl: String) = CoroutineScope(
        Dispatchers.IO).launch {

        if (context.getDatabasePath(DatabaseInformation.GIF_FAVORITE_DATABASE_NAME).exists()) {

            val gifFavoriteDataInterface = GifFavoriteDatabase(context).initialGifFavoriteDatabase()

            val gifFavorited = gifFavoriteDataInterface
                .initDataAccessObject()
                .getFavoriteGif(gifUrl)?.GifFavorited ?: false

            withContext(Dispatchers.Main) {

                likeButton.isLiked = gifFavorited
            }

            gifFavoriteDataInterface.close()
        }
    }
}