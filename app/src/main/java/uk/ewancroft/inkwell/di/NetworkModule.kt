package uk.ewancroft.inkwell.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import uk.ewancroft.inkwell.data.remote.AtProtoApi
import uk.ewancroft.inkwell.data.remote.ConstellationClient
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideAtProtoApi(): AtProtoApi = AtProtoApi()

    @Provides
    @Singleton
    fun provideConstellationClient(): ConstellationClient = ConstellationClient()
}
