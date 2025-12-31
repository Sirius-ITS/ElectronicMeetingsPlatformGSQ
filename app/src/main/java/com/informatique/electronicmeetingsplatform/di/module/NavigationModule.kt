package com.informatique.mtcit.di.module

import com.informatique.electronicmeetingsplatform.navigation.NavigationManager
import com.informatique.electronicmeetingsplatform.navigation.NavigationManagerImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NavigationModule {

    @Provides
    @Singleton
    fun provideNavigationManager(): NavigationManagerImpl {
        return NavigationManagerImpl()
    }

    @Provides
    @Singleton
    fun provideNavigationManagerInterface(
        impl: NavigationManagerImpl
    ): NavigationManager = impl
}