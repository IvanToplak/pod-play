package hr.from.ivantoplak.podplay.di

import android.content.Context
import androidx.room.Room
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import hr.from.ivantoplak.podplay.db.PodPlayDatabase
import hr.from.ivantoplak.podplay.db.PodcastDao
import hr.from.ivantoplak.podplay.repository.ItunesRepo
import hr.from.ivantoplak.podplay.repository.ItunesRepoImpl
import hr.from.ivantoplak.podplay.repository.PodcastRepo
import hr.from.ivantoplak.podplay.repository.PodcastRepoImpl
import hr.from.ivantoplak.podplay.service.network.FeedService
import hr.from.ivantoplak.podplay.service.network.ItunesService
import hr.from.ivantoplak.podplay.service.network.RssFeedService
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

private const val BASE_URL = "https://itunes.apple.com"

@Module
@InstallIn(ApplicationComponent::class)
abstract class DataModule {

    companion object {

        @Singleton
        @Provides
        fun provideItunesService(): ItunesService = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create<ItunesService>(
                ItunesService::class.java)

        @Singleton
        @Provides
        fun provideHttpClient(): OkHttpClient = OkHttpClient()

        @Singleton
        @Provides
        fun provideDatabase(@ApplicationContext context: Context): PodPlayDatabase =
            Room.databaseBuilder(context, PodPlayDatabase::class.java, PodPlayDatabase.NAME)
                .build()

        @Singleton
        @Provides
        fun providePodcastDao(database: PodPlayDatabase): PodcastDao = database.podcastDao()
    }

    @Singleton
    @Binds
    abstract fun provideItunesRepo(repoImpl: ItunesRepoImpl): ItunesRepo

    @Singleton
    @Binds
    abstract fun provideFeedService(serviceImpl: RssFeedService): FeedService

    @Singleton
    @Binds
    abstract fun providePodcastRepo(repoImpl: PodcastRepoImpl): PodcastRepo
}