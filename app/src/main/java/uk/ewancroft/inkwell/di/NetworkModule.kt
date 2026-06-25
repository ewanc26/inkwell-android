/**
 * Hilt module wiring network-layer singletons into the Dagger graph.
 *
 * Both AtProtoClient and ConstellationClient are stateless enough to be
 * application-wide singletons — no per-request state leaks between them.
 */
package uk.ewancroft.inkwell.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import uk.ewancroft.inkwell.data.remote.AtProtoClient
import uk.ewancroft.inkwell.data.remote.ConstellationClient
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideAtProtoClient(): AtProtoClient = AtProtoClient()

    @Provides
    @Singleton
    fun provideConstellationClient(): ConstellationClient = ConstellationClient()
}
