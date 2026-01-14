package com.informatique.electronicmeetingsplatform.di.module

import com.informatique.electronicmeetingsplatform.data.remote.auth.AuthApiService
import com.informatique.electronicmeetingsplatform.data.remote.auth.AuthApiServiceImpl
import com.informatique.electronicmeetingsplatform.data.repository.auth.AuthRepository
import com.informatique.electronicmeetingsplatform.data.repository.auth.AuthRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Hilt module for authentication dependencies
 * Provides bindings for auth-related interfaces and implementations
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class AuthModule {

    /**
     * Binds AuthApiServiceImpl to AuthApiService interface
     */
    @Binds
    @Singleton
    abstract fun bindAuthApiService(
        authApiServiceImpl: AuthApiServiceImpl
    ): AuthApiService

    /**
     * Binds AuthRepositoryImpl to AuthRepository interface
     */
    @Binds
    @Singleton
    abstract fun bindAuthRepository(
        authRepositoryImpl: AuthRepositoryImpl
    ): AuthRepository
}

