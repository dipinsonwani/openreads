package software.mdev.bookstracker.ui.bookslist.fragments

import android.app.Activity
import android.content.Context
import android.content.res.ColorStateList
import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.NavOptions
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import androidx.preference.PreferenceManager
import androidx.viewpager2.widget.ViewPager2
import software.mdev.bookstracker.R
import software.mdev.bookstracker.ui.bookslist.ListActivity
import kotlinx.android.synthetic.main.fragment_setup.*
import software.mdev.bookstracker.adapters.SetupAdapter
import software.mdev.bookstracker.other.Constants


class SetupFragment : Fragment(R.layout.fragment_setup) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        var sharedPreferencesName = (activity as ListActivity).getString(R.string.shared_preferences_name)
        val sharedPref = (activity as ListActivity).getSharedPreferences(sharedPreferencesName, Context.MODE_PRIVATE)

        if (!sharedPref.getBoolean(Constants.SHARED_PREFERENCES_KEY_FIRST_TIME_TOGGLE, true) &&
            sharedPref.getString(Constants.SHARED_PREFERENCES_KEY_APP_VERSION, "v0.0.0") ==
            resources.getString(R.string.app_version)
        ) {
            val navOptions = NavOptions.Builder()
                .setPopUpTo(R.id.setupFragment, true)
                .build()
            findNavController().navigate(
                R.id.action_setupFragment_to_readFragment,
                savedInstanceState,
                navOptions
            )
        }

        val images = listOf(
            R.drawable.ic_svg_books,
            R.drawable.ic_svg_study,
            R.drawable.ic_svg_presentation,
            0,
            R.drawable.ic_svg_like
        )

        val adapter = SetupAdapter(
            activity as ListActivity,
            images,
            resources.getString(R.string.app_version),
            view.context,
            vpSetup
        )
        vpSetup.adapter = adapter

        vpSetup.registerOnPageChangeCallback(object: ViewPager2.OnPageChangeCallback() {
            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {
                super.onPageScrolled(position, positionOffset, positionOffsetPixels)
            }

            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)

                changeFabColor(context)

                when(vpSetup.currentItem) {
                    0 -> {
                        selectIndicator(ivPosition0, 0)

                        fabLaunchApp.visibility = View.INVISIBLE
                        fabLaunchApp.scaleX = 0F
                        fabLaunchApp.scaleY = 0F

                        vpSetup.isUserInputEnabled = true
                    }
                    1 -> {
                        selectIndicator(ivPosition1, 0)

                        fabLaunchApp.visibility = View.INVISIBLE
                        fabLaunchApp.scaleX = 0F
                        fabLaunchApp.scaleY = 0F

                        vpSetup.isUserInputEnabled = true
                    }
                    2 -> {
                        selectIndicator(ivPosition2, 0)

                        fabLaunchApp.visibility = View.INVISIBLE
                        fabLaunchApp.scaleX = 0F
                        fabLaunchApp.scaleY = 0F

                        vpSetup.isUserInputEnabled = true
                    }
                    3 -> {
                        selectIndicator(ivPosition3, 0)

                        fabLaunchApp.animate().setDuration(250L).scaleX(0F).start()
                        fabLaunchApp.animate().setDuration(250L).scaleY(0F).start()

                        vpSetup.isUserInputEnabled = false
                    }
                    4 -> {
                        selectIndicator(ivPosition4, 0)

                        fabLaunchApp.scaleX = 0F
                        fabLaunchApp.scaleY = 0F
                        fabLaunchApp.visibility = View.VISIBLE

                        fabLaunchApp.animate().setDuration(250L).scaleX(1F).start()
                        fabLaunchApp.animate().setDuration(250L).scaleY(1F).start()

                        vpSetup.isUserInputEnabled = false
                    }
                }
            }

            override fun onPageScrollStateChanged(state: Int) {
                super.onPageScrollStateChanged(state)
            }
        })

        fabLaunchApp.setOnClickListener {
            hotReloadActivity(activity)
            saveAppsFirstlaunch()
            Navigation.findNavController(view).navigate(R.id.action_setupFragment_to_readFragment)
        }
    }

    private fun selectIndicator(selectedIndicator: View, position: Int) {
        var scaleBig = 0.6F
        var scaleSmall = 0.3F
        var listOfIndicators = listOf(
            ivPosition0,
            ivPosition1,
            ivPosition2,
            ivPosition3,
            ivPosition4
        )

        for (indicator in listOfIndicators) {
            if (indicator == selectedIndicator) {
                indicator.animate().scaleX(scaleBig).setDuration(350L).start()
                indicator.animate().scaleY(scaleBig).setDuration(350L).start()
            } else {
                indicator.animate().scaleX(scaleSmall).setDuration(200L).start()
                indicator.animate().scaleY(scaleSmall).setDuration(200L).start()
            }
        }
    }

    private fun hotReloadActivity(activity: Activity?) {
        if (activity == null) return
        val sharedPref = PreferenceManager.getDefaultSharedPreferences(context?.applicationContext)
        sharedPref.edit().putBoolean(Constants.SHARED_PREFERENCES_REFRESHED, true).apply()

        (activity as ListActivity).recreate()
    }

    private fun saveAppsFirstlaunch() {
        var sharedPreferencesName = (activity as ListActivity).getString(R.string.shared_preferences_name)
        val sharedPref = (activity as ListActivity).getSharedPreferences(sharedPreferencesName, Context.MODE_PRIVATE)

        val editor = sharedPref?.edit()

        editor?.apply {
            putBoolean(Constants.SHARED_PREFERENCES_KEY_FIRST_TIME_TOGGLE, false)
            putString(Constants.SHARED_PREFERENCES_KEY_APP_VERSION, resources.getString(R.string.app_version))
            putBoolean(Constants.SHARED_PREFERENCES_KEY_SHOW_OL_ALERT, true)
            apply()
        }
    }

    private fun changeFabColor(context: Context?) {
        if (context != null) {

            var color = getAccentColor(context)

            fabLaunchApp.backgroundTintList= ColorStateList.valueOf(color)
        }
    }

    private fun getAccentColor(context: Context): Int {

        var accentColor = ContextCompat.getColor(context, R.color.purple_500)

        var sharedPreferencesName = context.getString(R.string.shared_preferences_name)
        val sharedPref = context.getSharedPreferences(sharedPreferencesName, Context.MODE_PRIVATE)

        var accent = sharedPref?.getString(
            Constants.SHARED_PREFERENCES_KEY_ACCENT,
            Constants.THEME_ACCENT_DEFAULT
        ).toString()

        when(accent){
            Constants.THEME_ACCENT_LIGHT_GREEN -> accentColor = ContextCompat.getColor(context, R.color.light_green)
            Constants.THEME_ACCENT_ORANGE_500 -> accentColor = ContextCompat.getColor(context, R.color.orange_500)
            Constants.THEME_ACCENT_CYAN_500 -> accentColor = ContextCompat.getColor(context, R.color.cyan_500)
            Constants.THEME_ACCENT_GREEN_500 -> accentColor = ContextCompat.getColor(context, R.color.green_500)
            Constants.THEME_ACCENT_BROWN_400 -> accentColor = ContextCompat.getColor(context, R.color.brown_400)
            Constants.THEME_ACCENT_LIME_500 -> accentColor = ContextCompat.getColor(context, R.color.lime_500)
            Constants.THEME_ACCENT_PINK_300 -> accentColor = ContextCompat.getColor(context, R.color.pink_300)
            Constants.THEME_ACCENT_PURPLE_500 -> accentColor = ContextCompat.getColor(context, R.color.purple_500)
            Constants.THEME_ACCENT_TEAL_500 -> accentColor = ContextCompat.getColor(context, R.color.teal_500)
            Constants.THEME_ACCENT_YELLOW_500 -> accentColor = ContextCompat.getColor(context, R.color.yellow_500)
        }
        return accentColor
    }
}