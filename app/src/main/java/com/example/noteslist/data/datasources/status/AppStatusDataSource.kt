package com.example.noteslist.data.datasources.status

interface AppStatusDataSource {
    fun isFirstLaunch(): Boolean
    fun markFirstLaunchComplete()
}
