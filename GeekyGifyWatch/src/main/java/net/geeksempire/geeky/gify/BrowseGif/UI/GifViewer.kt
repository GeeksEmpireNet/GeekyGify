/*
 * Copyright © 2020 By Geeks Empire.
 *
 * Created by Elias Fazel on 2/8/20 10:44 AM
 * Last modified 2/8/20 10:38 AM
 *
 * Licensed Under MIT License.
 * https://opensource.org/licenses/MIT
 */

package net.geeksempire.geeky.gify.BrowseGif.UI

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import kotlinx.android.synthetic.main.browse_gif_list_view.*
import kotlinx.android.synthetic.main.gif_view.*
import net.geeksempire.geeky.gify.BrowseGif.Data.GiphyJsonDataStructure
import net.geeksempire.geeky.gify.BrowseGif.Extension.setupGifViewClickListener
import net.geeksempire.geeky.gify.R


class GifViewer : Fragment() {

    lateinit var gifLinkToDownload: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        gifLinkToDownload = arguments?.getString(GiphyJsonDataStructure.DATA_IMAGES_ORIGINAL) ?: "https://media3.giphy.com/media/QBStRoYK6d5VQD1pYX/giphy.gif"
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        return inflater.inflate(R.layout.gif_view, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        Glide
            .with(context!!)
            .asGif()
            .diskCacheStrategy(DiskCacheStrategy.DATA)
            .load(gifLinkToDownload)
            .into(gifView)

        closeFragment.setOnClickListener {
            activity?.let {
                it.supportFragmentManager
                    .beginTransaction()
                    .setCustomAnimations(0, R.anim.slide_to_right)
                    .remove(this@GifViewer)
                    .commit()
            }
        }

        setupGifViewClickListener()
    }

    override fun onStart() {
        super.onStart()
    }

    override fun onDestroyView() {
        super.onDestroyView()

        activity!!.fragmentGifViewer!!.visibility = View.GONE

    }
}