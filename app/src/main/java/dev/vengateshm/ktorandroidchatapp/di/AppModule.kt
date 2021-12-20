package dev.vengateshm.ktorandroidchatapp.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dev.vengateshm.ktorandroidchatapp.data.remote.ChatSocketService
import dev.vengateshm.ktorandroidchatapp.data.remote.ChatSocketServiceImpl
import dev.vengateshm.ktorandroidchatapp.data.remote.MessageService
import dev.vengateshm.ktorandroidchatapp.data.remote.MessageServiceImpl
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.features.json.*
import io.ktor.client.features.json.serializer.*
import io.ktor.client.features.logging.*
import io.ktor.client.features.websocket.*
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideHttpClient(): HttpClient {
        return HttpClient(CIO) {
            install(Logging)
            install(WebSockets)
            install(JsonFeature) {
                serializer = KotlinxSerializer()
            }
        }
    }

    @Provides
    @Singleton
    fun providesMessageService(client: HttpClient): MessageService {
        return MessageServiceImpl(client)
    }

    @Provides
    @Singleton
    fun providesChatService(client: HttpClient): ChatSocketService {
        return ChatSocketServiceImpl(client)
    }
}