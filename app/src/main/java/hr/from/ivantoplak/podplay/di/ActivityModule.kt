package hr.from.ivantoplak.podplay.di

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import dagger.hilt.android.qualifiers.ActivityContext
import dagger.hilt.android.scopes.ActivityScoped
import hr.from.ivantoplak.podplay.router.Router
import hr.from.ivantoplak.podplay.router.RouterImpl
import hr.from.ivantoplak.podplay.ui.common.ScreenTitleProvider
import hr.from.ivantoplak.podplay.ui.common.ScreenTitleProviderImpl

@Module
@InstallIn(ActivityComponent::class)
object ActivityModule {

    @ActivityScoped
    @Provides
    fun provideRouter(@ActivityContext context: Context): Router =
        RouterImpl((context as AppCompatActivity).supportFragmentManager)


    @ActivityScoped
    @Provides
    fun provideScreenTitleProvider(): ScreenTitleProvider = ScreenTitleProviderImpl()
}