package com.informatique.electronicmeetingsplatform.ui

import com.informatique.electronicmeetingsplatform.common.dataStores.LanguageDataStore
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@EntryPoint
@InstallIn(SingletonComponent::class)
interface BaseActivityEntryPoint {
    fun languageDataStore(): LanguageDataStore
}