package hr.from.ivantoplak.podplay.di

import android.content.ComponentName
import android.content.Context
import androidx.core.app.NotificationManagerCompat
import androidx.work.WorkManager
import androidx.work.WorkRequest
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import hr.from.ivantoplak.podplay.intent.PendingIntentFactory
import hr.from.ivantoplak.podplay.intent.PendingIntentFactoryImpl
import hr.from.ivantoplak.podplay.notification.NotificationFactory
import hr.from.ivantoplak.podplay.notification.NotificationFactoryImpl
import hr.from.ivantoplak.podplay.notification.Notifications
import hr.from.ivantoplak.podplay.notification.NotificationsImpl
import hr.from.ivantoplak.podplay.service.media.MetadataProvider
import hr.from.ivantoplak.podplay.service.media.PodplayMediaServiceConnection
import hr.from.ivantoplak.podplay.service.media.PodplayMediaService
import hr.from.ivantoplak.podplay.work.*
import hr.from.ivantoplak.podplay.work.EpisodeUpdateWorkRequestFactory.REPEAT_INTERVAL
import hr.from.ivantoplak.podplay.work.EpisodeUpdateWorkRequestFactory.REPEAT_UNIT
import javax.inject.Singleton
import javax.xml.parsers.DocumentBuilder
import javax.xml.parsers.DocumentBuilderFactory

@Module
@InstallIn(ApplicationComponent::class)
abstract class AppModule {

    companion object {

        @Singleton
        @Provides
        fun provideDocumentBuilder(): DocumentBuilder =
            DocumentBuilderFactory.newInstance().newDocumentBuilder()

        @Singleton
        @Provides
        fun provideNotificationManagerCompat(@ApplicationContext context: Context): NotificationManagerCompat =
            NotificationManagerCompat.from(context)

        @Singleton
        @Provides
        fun provideWorkManager(@ApplicationContext context: Context): WorkManager =
            WorkManager.getInstance(context)

        @Singleton
        @Provides
        fun provideEpisodeUpdateWorkRequest(): WorkRequest =
            EpisodeUpdateWorkRequestFactory.createWorkRequest(REPEAT_INTERVAL, REPEAT_UNIT)

        @Singleton
        @Provides
        fun provideMetadataProvider(): MetadataProvider = MetadataProvider()

        @Singleton
        @Provides
        fun provideMusicServiceConnection(@ApplicationContext context: Context): PodplayMediaServiceConnection =
            PodplayMediaServiceConnection(
                context,
                ComponentName(context, PodplayMediaService::class.java)
            )
    }

    @Singleton
    @Binds
    abstract fun providePendingIntentFactory(impl: PendingIntentFactoryImpl): PendingIntentFactory

    @Singleton
    @Binds
    abstract fun provideNotificationFactory(impl: NotificationFactoryImpl): NotificationFactory

    @Singleton
    @Binds
    abstract fun provideNotifications(impl: NotificationsImpl): Notifications

    @Singleton
    @Binds
    abstract fun provideWork(impl: WorkImpl): Work

    @Singleton
    @Binds
    abstract fun provideEpisodeUpdateScheduler(impl: EpisodeUpdateSchedulerImpl): EpisodeUpdateScheduler
}