/*
 * Copyright © 2020 By Geeks Empire.
 *
 * Created by Elias Fazel on 2/14/20 4:26 PM
 * Last modified 2/14/20 3:29 PM
 *
 * Licensed Under MIT License.
 * https://opensource.org/licenses/MIT
 */

package net.geeksempire.geeky.gify.Utils.SharingProcess

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.ResultReceiver
import android.view.View
import android.view.animation.AnimationUtils
import androidx.fragment.app.FragmentActivity
import androidx.wear.widget.ConfirmationOverlay
import com.google.android.wearable.intent.RemoteIntent
import kotlinx.android.synthetic.main.gif_view.*
import kotlinx.android.synthetic.main.gif_view.view.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import net.geeksempire.geeky.gify.Networking.DownloadGif
import net.geeksempire.geeky.gify.R

class ControlGifShare (var fragmentActivity: FragmentActivity) : SharingInterface {

    fun initializeGifShare(gifLinkToShare: String, additionalText: String?) {

        val shareView = fragmentActivity.fullShareView
        shareView.startAnimation(AnimationUtils.loadAnimation(fragmentActivity, R.anim.slide_from_right))
        shareView.visibility = View.VISIBLE

        shareView.shareToPhone.setOnClickListener {

            startShareToPhoneProcess(
                gifLinkToShare,
                additionalText
            )
        }

        shareView.shareToWatch.setOnClickListener {

            startShareToOtherApplications(
                gifLinkToShare,
                additionalText
            )
        }
    }

    override fun sharingProcessCallback(gifLinkToShare: String, additionalText: String?) {

        startShareToOtherApplications(
            gifLinkToShare,
            additionalText
        )
    }

    private fun startShareToPhoneProcess(gifLinkToShare: String, additionalText: String?) {

        val resultReceiver = object : ResultReceiver(Handler()) {
            override fun onReceiveResult(resultCode: Int, resultData: Bundle?) {
                if (resultCode == RemoteIntent.RESULT_OK) {

                    val confirmationOverlay = ConfirmationOverlay()
                        .setMessage(fragmentActivity.getString(R.string.gifReadyOnPhone))
                        .setDuration(1500 * 1)
                        .setType(ConfirmationOverlay.SUCCESS_ANIMATION)
                    confirmationOverlay.showOn(fragmentActivity)
                    confirmationOverlay.setFinishedAnimationListener {

                    }

                } else if (resultCode == RemoteIntent.RESULT_FAILED) {

                    val confirmationOverlay = ConfirmationOverlay()
                        .setMessage(fragmentActivity.getString(R.string.errorOccurred))
                        .setDuration(1500 * 1)
                        .setType(ConfirmationOverlay.FAILURE_ANIMATION)
                    confirmationOverlay.showOn(fragmentActivity)
                    confirmationOverlay.setFinishedAnimationListener {

                        RemoteIntent.startRemoteActivity(
                            fragmentActivity,
                            Intent(Intent.ACTION_VIEW)
                                .addCategory(Intent.CATEGORY_BROWSABLE)
                                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                .setData(
                                    Uri.parse("https://play.google.com/store/apps/details?id=${fragmentActivity.packageName}")),
                            null)

                        this@ControlGifShare
                            .sharingProcessCallback(gifLinkToShare, additionalText)
                    }

                }
            }
        }

        val remoteIntent = Intent(Intent.ACTION_VIEW)
            .addCategory(Intent.CATEGORY_BROWSABLE)
            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            .setData(
                Uri.parse("https://www.geekygify.xyz/controlgeekygifshare.html?${Intent.EXTRA_STREAM}=${gifLinkToShare}" +
                        "&" +
                        "${Intent.EXTRA_TEXT}=${additionalText}"))

        RemoteIntent.startRemoteActivity(
            fragmentActivity,
            remoteIntent,
            resultReceiver)
    }

    private fun startShareToOtherApplications (gifLinkToShare: String, additionalText: String?) {

        CoroutineScope(Dispatchers.Default).launch {

            Intent(Intent.ACTION_SEND).apply {

                val gifFile = DownloadGif(fragmentActivity).downloadGifFile(gifLinkToShare).await()
                if (gifFile.exists()) {

                    this.type = "image/*"

                    this.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(gifFile))
                    this.putExtra(Intent.EXTRA_TEXT, additionalText)

                    this.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    this.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)

                    fragmentActivity.startActivity(Intent.createChooser(this, additionalText))
                } else {

                    val confirmationOverlay = ConfirmationOverlay()
                        .setMessage(fragmentActivity.getString(R.string.downloadErrorOccurred))
                        .setDuration(1500 * 1)
                        .setType(ConfirmationOverlay.FAILURE_ANIMATION)
                    confirmationOverlay.showOn(fragmentActivity)
                }
            }
        }
    }
}