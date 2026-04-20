package com.example.noteslist.data.repositories

import com.example.noteslist.data.datasources.status.AppStatusDataSource
import com.example.noteslist.domain.repositories.AppStatusRepository
import dagger.hilt.components.SingletonComponent
import it.czerwinski.android.hilt.annotations.BoundTo
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
@BoundTo(supertype = AppStatusRepository::class, component = SingletonComponent::class)
class AppStatusRepositoryImpl @Inject constructor(
    private val dataSource: AppStatusDataSource,
) : AppStatusRepository {

    override fun isFirstLaunch(): Boolean = dataSource.isFirstLaunch()

    override fun markFirstLaunchComplete() = dataSource.markFirstLaunchComplete()
}
