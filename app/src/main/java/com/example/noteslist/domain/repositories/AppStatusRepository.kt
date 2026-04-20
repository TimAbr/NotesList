package com.example.noteslist.domain.repositories

interface AppStatusRepository {
    fun isFirstLaunch(): Boolean
    fun markFirstLaunchComplete()
}
