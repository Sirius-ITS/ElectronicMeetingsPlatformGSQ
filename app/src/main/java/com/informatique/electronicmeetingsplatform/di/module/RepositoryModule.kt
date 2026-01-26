package com.informatique.electronicmeetingsplatform.di.module

import com.informatique.electronicmeetingsplatform.data.remote.auth.AuthApiService
import com.informatique.electronicmeetingsplatform.data.remote.auth.AuthApiServiceImpl
import com.informatique.electronicmeetingsplatform.data.remote.meeting.MeetingApiService
import com.informatique.electronicmeetingsplatform.data.remote.meeting.MeetingApiServiceImpl
import com.informatique.electronicmeetingsplatform.data.repository.auth.AuthRepository
import com.informatique.electronicmeetingsplatform.data.repository.auth.AuthRepositoryImpl
import com.informatique.electronicmeetingsplatform.data.repository.meeting.MeetingRepository
import com.informatique.electronicmeetingsplatform.data.repository.meeting.MeetingRepositoryImpl
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
abstract class RepositoryModule {

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

    /**
     * Binds MeetingApiServiceImpl to MeetingApiService interface
     */
    @Binds
    @Singleton
    abstract fun bindMeetingApiService(
        meetingApiServiceImpl: MeetingApiServiceImpl
    ): MeetingApiService

    /**
     * Binds MeetingRepositoryImpl to MeetingRepository interface
     */
    @Binds
    @Singleton
    abstract fun bindMeetingRepository(
        meetingRepositoryImpl: MeetingRepositoryImpl
    ): MeetingRepository
}

