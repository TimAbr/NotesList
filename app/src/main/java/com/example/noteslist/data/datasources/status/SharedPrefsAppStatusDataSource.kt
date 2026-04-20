package com.example.noteslist.data.datasources.status

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import it.czerwinski.android.hilt.annotations.BoundTo
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
@BoundTo(supertype = AppStatusDataSource::class, component = SingletonComponent::class)
class SharedPrefsAppStatusDataSource @Inject constructor(
    @ApplicationContext private val context: Context,
) : AppStatusDataSource {

    private val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    override fun isFirstLaunch(): Boolean =
        prefs.getBoolean(KEY_FIRST_LAUNCH, true)

    override fun markFirstLaunchComplete() {
        prefs.edit().putBoolean(KEY_FIRST_LAUNCH, false).apply()
    }

    companion object {
        private const val PREFS_NAME = "app_prefs"
        private const val KEY_FIRST_LAUNCH = "is_first_launch"
    }
}
