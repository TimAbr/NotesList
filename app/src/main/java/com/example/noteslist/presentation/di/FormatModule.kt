package com.example.noteslist.presentation.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object FormatModule {

    @Provides
    @DatePattern
    fun provideDatePattern(): String = "dd.MM.yyyy"

    @Provides
    @DateTimePattern
    fun provideDateTimePattern(): String = "dd.MM.yyyy HH:mm"
}
