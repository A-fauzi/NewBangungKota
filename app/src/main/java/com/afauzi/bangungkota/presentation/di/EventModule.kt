package com.afauzi.bangungkota.presentation.di

import com.afauzi.bangungkota.data.repository.event.EventRepository
import com.afauzi.bangungkota.data.repository.event.EventRepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object EventModule {

//    @Provides
//    @Singleton
//    fun provideEventRepository(): EventRepository {
//        return EventRepositoryImpl()
//    }

}