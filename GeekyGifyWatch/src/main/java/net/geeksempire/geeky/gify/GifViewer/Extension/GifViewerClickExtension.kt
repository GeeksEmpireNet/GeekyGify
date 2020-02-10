/*
 * Copyright © 2020 By Geeks Empire.
 *
 * Created by Elias Fazel on 2/9/20 4:31 PM
 * Last modified 2/9/20 4:31 PM
 *
 * Licensed Under MIT License.
 * https://opensource.org/licenses/MIT
 */

package net.geeksempire.geeky.gify.GifViewer.Extension

import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.transition.ChangeBounds
import androidx.transition.ChangeImageTransform
import androidx.transition.TransitionManager
import androidx.transition.TransitionSet
import kotlinx.android.synthetic.main.gif_view.*
import net.geeksempire.geeky.gify.GifViewer.GifViewer
import net.geeksempire.geeky.gify.R
import net.geeksempire.geeky.gify.Utils.Calculations.calculateThirtyPercent

fun GifViewer.setupGifViewClickListener() {
    closeFragment.setOnClickListener {
        activity?.let {
            it.supportFragmentManager
                .beginTransaction()
                .setCustomAnimations(0, R.anim.slide_to_right)
                .remove(this@setupGifViewClickListener)
                .commit()
        }
    }

    var userProfileView: Boolean = false
    gifView.setOnClickListener {

        if (gifUserName != null && gifUserAvatarUrl != null) {

            if (userProfileView) {

                val animationSlideOut = AnimationUtils.loadAnimation(context, R.anim.slide_to_right)

                userAvatarView.startAnimation(animationSlideOut)
                userAvatarView.visibility = View.GONE

                userNameView.startAnimation(animationSlideOut)
                userNameView.visibility = View.GONE

                userVerifiedBadgeView.startAnimation(animationSlideOut)
                userVerifiedBadgeView.visibility = View.GONE

            } else {

                val animationSlideIn = AnimationUtils.loadAnimation(context, R.anim.slide_from_right)

                userAvatarView.startAnimation(animationSlideIn)
                userAvatarView.visibility = View.VISIBLE

                userNameView.startAnimation(animationSlideIn)
                userNameView.visibility = View.VISIBLE

                userVerifiedBadgeView.startAnimation(animationSlideIn)
                userVerifiedBadgeView.visibility = View.VISIBLE
            }

            userProfileView = !userProfileView
        }
    }

    var viewExpanded: Boolean = false
    gifView.setOnLongClickListener {

        TransitionManager.beginDelayedTransition(
            mainViewItem, TransitionSet()
                .addTransition(ChangeBounds())
                .addTransition(ChangeImageTransform())
        )

        val gifViewLayoutParameter: ViewGroup.LayoutParams = gifView.layoutParams
        gifViewLayoutParameter.width = if (viewExpanded) ViewGroup.LayoutParams.MATCH_PARENT else calculateThirtyPercent(gifView.width)
        gifViewLayoutParameter.height = if (viewExpanded) ViewGroup.LayoutParams.MATCH_PARENT else calculateThirtyPercent(gifView.height)
        gifView.layoutParams = gifViewLayoutParameter

        viewExpanded = !viewExpanded

        false
    }
}