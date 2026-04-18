package com.example.noteslist.presentation.di

import javax.inject.Qualifier

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class RelativeDate

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class DatePattern

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class DateTimePattern
