package com.informatique.electronicmeetingsplatform.di.module

import android.content.Context
import android.content.SharedPreferences
import androidx.work.WorkManager
import com.informatique.electronicmeetingsplatform.business.validation.CrossFieldValidator
import com.informatique.electronicmeetingsplatform.business.validation.FormValidator
import com.informatique.electronicmeetingsplatform.common.AndroidResourceProvider
import com.informatique.electronicmeetingsplatform.common.ResourceProvider
import com.informatique.electronicmeetingsplatform.common.dispatcher.DispatcherProvider
import com.informatique.electronicmeetingsplatform.common.networkhelper.NetworkHelper
import com.informatique.electronicmeetingsplatform.common.networkhelper.NetworkHelperImpl
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.Dispatchers
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class ApplicationModule {

    companion object {
        @Provides
    @Singleton
    fun provideFormValidator(
        crossFieldValidator: CrossFieldValidator
    ): FormValidator = FormValidator(crossFieldValidator)

//        @Provides
//        @Singleton
//        fun provideLogger(): Logger = AppLogger()

        @Provides
        @Singleton
        fun provideDispatcherProvider(): DispatcherProvider = object : DispatcherProvider {
            override val main = Dispatchers.Main
            override val io = Dispatchers.IO
            override val default = Dispatchers.Default
        }

        @Provides
        @Singleton
        fun provideNetworkHelper(
            @ApplicationContext context: Context
        ): NetworkHelper {
            return NetworkHelperImpl(context)
        }

        @Provides
        @Singleton
        fun provideWorkManager(
            @ApplicationContext context: Context
        ): WorkManager {
            return WorkManager.getInstance(context)
        }

        @Provides
        @Singleton
        fun provideSharedPreferences(
            @ApplicationContext context: Context
        ): SharedPreferences {
            return context.getSharedPreferences("my_prefs", Context.MODE_PRIVATE)
        }
    }

    /**
     * Binds AndroidResourceProvider implementation to ResourceProvider interface
     */
    @Binds
    @Singleton
    abstract fun bindResourceProvider(
        androidResourceProvider: AndroidResourceProvider
    ): ResourceProvider
}