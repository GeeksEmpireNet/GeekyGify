/*
 * Copyright © 2020 By Geeks Empire. 
 *
 * Created by Elias Fazel on 2/18/20 5:56 PM
 * Last modified 2/18/20 5:56 PM
 *
 * Licensed Under MIT License.
 * https://opensource.org/licenses/MIT
 */

package net.geeksempire.geeky.gify.DataController

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.StrictMode
import android.os.StrictMode.VmPolicy
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import kotlinx.android.synthetic.main.offline_indicator.view.*
import kotlinx.android.synthetic.main.received_data_controller.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import net.geeksempire.geeky.gify.DataController.Extension.setupLoadingAnimation
import net.geeksempire.geeky.gify.DataController.Parameter.DataParameter
import net.geeksempire.geeky.gify.Networking.DownloadGif
import net.geeksempire.geeky.gify.R
import net.geeksempire.geeky.gify.Utils.SystemCheckpoint.SystemCheckpoint
import net.geeksempire.geeky.gify.Utils.UI.SnackbarView

class ReceivedDataController : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val vmBuilder = VmPolicy.Builder()
        StrictMode.setVmPolicy(vmBuilder.build())
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.received_data_controller, container, false)

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val systemCheckpoint = SystemCheckpoint(context!!)

        arguments?.let { receivedData ->

            if (systemCheckpoint.networkConnection()) {
                setupLoadingAnimation()

                val linkToGif = receivedData.getString(DataParameter.LINK_TO_GIF)!!
                val additionalText = receivedData.getString(DataParameter.ADDITIONAL_TEXT)!!

                Log.d(this.javaClass.simpleName, linkToGif)
                Log.d(this.javaClass.simpleName, additionalText)

                CoroutineScope(Dispatchers.Default).launch {
                    val gifFile = DownloadGif(context!!).downloadGifFile(linkToGif).await()

                    Intent(Intent.ACTION_SEND).apply {

                        if (gifFile.exists()) {

                            this.type = "image/*"

                            this.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(gifFile))
                            this.putExtra(Intent.EXTRA_TEXT, additionalText)

                            this.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                            this.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)

                            startActivity(Intent.createChooser(this, additionalText))
                        } else {

                            SnackbarView().snackBarViewFail((activity as AppCompatActivity),
                                mainView,
                                getString(R.string.downloadErrorOccurred))

                        }
                    }
                }

            } else {
                activity!!.window.statusBarColor = context!!.getColor(R.color.cyberGreen)
                activity!!.window.navigationBarColor = context!!.getColor(R.color.cyberGreen)

                val offlineIndicator = LayoutInflater.from(context!!).inflate(
                    R.layout.offline_indicator, mainView, false)

                mainView.addView(offlineIndicator)

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
}