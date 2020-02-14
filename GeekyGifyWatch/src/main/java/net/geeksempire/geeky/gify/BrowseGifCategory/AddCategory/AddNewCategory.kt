/*
 * Copyright © 2020 By Geeks Empire.
 *
 * Created by Elias Fazel on 2/13/20 7:13 PM
 * Last modified 2/13/20 7:13 PM
 *
 * Licensed Under MIT License.
 * https://opensource.org/licenses/MIT
 */

package net.geeksempire.geeky.gify.BrowseGifCategory.AddCategory

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.core.content.ContextCompat.getSystemService
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.add_new_category_view.*
import kotlinx.android.synthetic.main.browse_gif_category_view.*
import kotlinx.coroutines.*
import net.geeksempire.geeky.gify.BrowseGifCategory.RoomDatabase.GifCategoryDataModel
import net.geeksempire.geeky.gify.BrowseGifCategory.RoomDatabase.GifCategoryDatabase
import net.geeksempire.geeky.gify.BrowseGifCategory.Utils.GifCategoryFragmentStateListener
import net.geeksempire.geeky.gify.R


class AddNewCategory (private val gifCategoryFragmentStateListener: GifCategoryFragmentStateListener?) : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.add_new_category_view, container, false)

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val inputMethodManager = getSystemService(context!!, InputMethodManager::class.java) as InputMethodManager
        inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0)

        typeNewCategoryName.requestFocus()
        typeNewCategoryName.setOnEditorActionListener { view, actionId, event ->

            when (actionId) {
                EditorInfo.IME_ACTION_DONE -> {
                    inputMethodManager.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0)

                    if (!view.text.isBlank()) {
                        CoroutineScope(SupervisorJob() + Dispatchers.IO).launch {

                            GifCategoryDatabase(context!!)
                                .initialGifCategoryDatabase()
                                .initDataAccessObject()
                                .insertNewGifCategoryData(
                                    GifCategoryDataModel(
                                        view.text.toString(),
                                        System.currentTimeMillis())
                                )

                            withContext(Dispatchers.Main) {

                                activity?.let {
                                    it.supportFragmentManager
                                        .beginTransaction()
                                        .setCustomAnimations(0, R.anim.slide_to_right)
                                        .remove(this@AddNewCategory)
                                        .commit()
                                }

                                gifCategoryFragmentStateListener?.onFragmentDetach()
                            }
                        }
                    } else {

                        activity?.let {
                            it.supportFragmentManager
                                .beginTransaction()
                                .setCustomAnimations(0, R.anim.slide_to_right)
                                .remove(this@AddNewCategory)
                                .commit()
                        }

                        Toast.makeText(context, getString(R.string.errorOccurred), Toast.LENGTH_LONG).show()
                    }
                }
            }
            false
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()


        activity!!.fragmentNewCategory!!.visibility = View.GONE
    }
}