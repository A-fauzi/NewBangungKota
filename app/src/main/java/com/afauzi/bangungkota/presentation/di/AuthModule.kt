package com.afauzi.bangungkota.presentation.di

import com.afauzi.bangungkota.data.repository.AuthRepository
import com.afauzi.bangungkota.data.repository.AuthRepositoryImpl
import com.afauzi.bangungkota.domain.usecase.SignInWithGoogleUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

// app/src/main/java/com/contoh/aplikasi/presentation/di/AuthModule.kt

@Module
@InstallIn(SingletonComponent::class)
object AuthModule {
    @Provides
    @Singleton
    fun provideAuthRepository(): AuthRepository {
        return AuthRepositoryImpl()
    }

    @Provides
    @Singleton
    fun provideSignInWithGoogleUseCase(repository: AuthRepository): SignInWithGoogleUseCase {
        return SignInWithGoogleUseCase(repository)
    }
}
