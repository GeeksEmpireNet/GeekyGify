/*
 * Copyright © 2020 By Geeks Empire. 
 *
 * Created by Elias Fazel on 3/10/20 2:40 PM
 * Last modified 3/10/20 1:50 PM
 *
 * Licensed Under MIT License.
 * https://opensource.org/licenses/MIT
 */

package net.geeksempire.geeky.gify.SharedDataController

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.StrictMode
import android.os.StrictMode.VmPolicy
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import kotlinx.android.synthetic.main.offline_indicator.view.*
import kotlinx.coroutines.*
import net.geeksempire.geeky.gify.R
import net.geeksempire.geeky.gify.SharedDataController.Extension.setupLoadingAnimation
import net.geeksempire.geeky.gify.SharedDataController.Parameter.DataParameter
import net.geeksempire.geeky.gify.Utils.Networking.DownloadGif
import net.geeksempire.geeky.gify.Utils.SystemCheckpoint.SystemCheckpoint
import net.geeksempire.geeky.gify.Utils.UI.SnackbarInteraction
import net.geeksempire.geeky.gify.Utils.UI.SnackbarView
import net.geeksempire.geeky.gify.databinding.ReceivedDataControllerBinding
import java.io.File

class ReceivedDataController : Fragment() {

    private lateinit var receivedDataControllerBinding: ReceivedDataControllerBinding

    var gifFile: File? = null

    lateinit var linkToDownloadGif: String
    lateinit var additionalText: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val vmBuilder = VmPolicy.Builder()
        StrictMode.setVmPolicy(vmBuilder.build())
    }

    override fun onCreateView(layoutInflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        receivedDataControllerBinding = ReceivedDataControllerBinding.inflate(layoutInflater, container, false)

        return receivedDataControllerBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val systemCheckpoint = SystemCheckpoint(context!!)

        arguments?.let {

            linkToDownloadGif = it.getString(DataParameter.LINK_TO_GIF) ?: "https://media.giphy.com/media/ZCemAxolHlLetaTqLh/giphy.gif"
            additionalText = it.getString(DataParameter.ADDITIONAL_TEXT).toString()

            if (systemCheckpoint.networkConnection()) {
                setupLoadingAnimation()

                CoroutineScope(Dispatchers.Default).launch {
                    gifFile = DownloadGif(context!!).downloadGifFile(linkToDownloadGif).await()

                    if (gifFile!!.exists()) {
                        withContext(SupervisorJob() + Dispatchers.Main) {
                            receivedDataControllerBinding.shareButton.visibility = View.VISIBLE
                        }

                        Intent(Intent.ACTION_SEND).apply {

                            this.type = "image/*"

                            this.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(gifFile))
                            this.putExtra(Intent.EXTRA_TEXT, additionalText)

                            this.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                            this.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)

                            startActivity(Intent.createChooser(this, additionalText))
                        }
                    } else {

                        withContext(Dispatchers.Main) {
                            SnackbarView().snackBarViewFail((activity as AppCompatActivity),
                                receivedDataControllerBinding.mainViewReceivedDataController,
                                getString(R.string.downloadErrorOccurred), object: SnackbarInteraction{})
                        }
                    }
                }

            } else {
                activity!!.window.statusBarColor = context!!.getColor(R.color.cyberGreen)
                activity!!.window.navigationBarColor = context!!.getColor(R.color.cyberGreen)

                val offlineIndicator = LayoutInflater.from(context!!).inflate(
                    R.layout.offline_indicator, receivedDataControllerBinding.mainViewReceivedDataController, false)

                receivedDataControllerBinding.mainViewReceivedDataController.addView(offlineIndicator)

                Glide.with(context!!)
                    .asGif()
                    .diskCacheStrategy(DiskCacheStrategy.DATA)
                    .load(R.drawable.no_internet_connection)
                    .into(offlineIndicator.offlineWait)

                offlineIndicator.offlineWait.setOnClickListener {
                    startActivity(Intent(Settings.ACTION_SETTINGS).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))

                    activity!!.finish()
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()

        receivedDataControllerBinding.shareButton.setOnClickListener {

            gifFile?.let {

                if (it.exists()) {

                    Intent(Intent.ACTION_SEND).apply {

                        this.type = "image/*"

                        this.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(it))
                        this.putExtra(Intent.EXTRA_TEXT, additionalText)

                        this.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        this.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)

                        startActivity(Intent.createChooser(this, additionalText))

                    }

                } else {

                    SnackbarView().snackBarViewFail((activity as AppCompatActivity),
                        receivedDataControllerBinding.mainViewReceivedDataController,
                        getString(R.string.downloadErrorOccurred), object: SnackbarInteraction{})

                }
            }
        }
    }
}
