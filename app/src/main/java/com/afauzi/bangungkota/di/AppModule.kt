package com.afauzi.bangungkota.di

import com.afauzi.bangungkota.data.repository.auth.AuthRepository
import com.afauzi.bangungkota.data.repository.auth.AuthRepositoryImpl
import com.afauzi.bangungkota.data.repository.event.EventRepository
import com.afauzi.bangungkota.data.repository.event.EventRepositoryImpl
import com.afauzi.bangungkota.data.repository.post.PostRepository
import com.afauzi.bangungkota.data.repository.post.PostRepositoryImpl
import com.afauzi.bangungkota.data.repository.post.reply.ReplyPostRepository
import com.afauzi.bangungkota.data.repository.post.reply.ReplyPostRepositoryImpl
import com.afauzi.bangungkota.data.repository.user.UserRepository
import com.afauzi.bangungkota.data.repository.user.UserRepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Provides
    @Singleton
    fun provideUserRepository(): UserRepository {
        return UserRepositoryImpl()
    }

    @Provides
    @Singleton
    fun provideAuthRepository(): AuthRepository {
        return AuthRepositoryImpl()
    }

    @Provides
    @Singleton
    fun provideEventRepository(): EventRepository {
        return EventRepositoryImpl()
    }

    @Provides
    @Singleton
    fun providePostRepository(): PostRepository {
        return PostRepositoryImpl()
    }

    @Provides
    @Singleton
    fun provideReplyPostRepository(): ReplyPostRepository {
        return ReplyPostRepositoryImpl()
    }
}