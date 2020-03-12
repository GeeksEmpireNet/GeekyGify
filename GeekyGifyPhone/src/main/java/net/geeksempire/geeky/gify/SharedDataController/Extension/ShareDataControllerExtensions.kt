/*
 * Copyright © 2020 By Geeks Empire.
 *
 * Created by Elias Fazel on 3/11/20 5:52 PM
 * Last modified 3/11/20 5:29 PM
 *
 * Licensed Under MIT License.
 * https://opensource.org/licenses/MIT
 */

package net.geeksempire.geeky.gify.SharedDataController.Extension

import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import net.geeksempire.geeky.gify.R
import net.geeksempire.geeky.gify.SharedDataController.ReceivedDataController

fun ReceivedDataController.setupLoadingAnimation() {

    val waitingGifs = arrayOf(
        R.drawable.waiting_cube,
        R.drawable.waiting_earth,
        R.drawable.waiting_robot)

    Glide.with(context!!)
        .asGif()
        .diskCacheStrategy(DiskCacheStrategy.DATA)
        .load(waitingGifs.random())
        .into(receivedDataControllerBinding.waitingView)
}