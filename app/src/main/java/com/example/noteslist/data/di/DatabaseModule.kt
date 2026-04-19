package com.example.noteslist.data.di

import android.content.Context
import androidx.room.Room
import com.example.noteslist.data.datasources.notes.local.AppDatabase
import com.example.noteslist.data.datasources.notes.local.AppDatabaseDemoCallback
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideAppDatabase(
        @ApplicationContext context: Context,
        demoCallback: AppDatabaseDemoCallback
    ): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "notes_database"
        )
            .addCallback(demoCallback)
            .build()
    }


}
