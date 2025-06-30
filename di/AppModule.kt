package com.kotlingdgocucb.elimuApp.di

import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import androidx.room.Room
import com.google.android.libraries.identity.googleid.GetSignInWithGoogleOption
import com.kotlingdgocucb.elimuApp.data.datasource.local.datastore.dataStore
import com.kotlingdgocucb.elimuApp.data.datasource.local.room.ElimuDao
import com.kotlingdgocucb.elimuApp.data.datasource.local.room.ElimuDatabase
import com.kotlingdgocucb.elimuApp.data.datasource.remote.NetworkModule
import com.kotlingdgocucb.elimuApp.data.repository.*
import com.kotlingdgocucb.elimuApp.domain.usecase.*
import com.kotlingdgocucb.elimuApp.domain.utils.Constants
import com.kotlingdgocucb.elimuApp.ui.viewmodel.*
import io.ktor.client.HttpClient
import org.koin.android.ext.koin.androidApplication
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val appModule = module {

    // DataStore
    single { androidContext().dataStore }

    // Network
    single<HttpClient> { NetworkModule.provideHttpClient() }

    // Database
    single {
        Room.databaseBuilder(
            androidApplication(),
            ElimuDatabase::class.java,
            Constants.DATABASE_NAME
        ).build()
    }

    single<ElimuDao> { get<ElimuDatabase>().elimuDao() }

    // Repositories
    singleOf<ElimuRepository>(::ElimuRepositoryImpl)
    singleOf<VideoRepository>(::VideoRepositoryImpl)
    singleOf<MentorRepository>(::MentorRepositoryImpl)
    singleOf<ReviewRepository>(::ReviewRepositoryImpl)
    singleOf<ProgressRepository>(::ProgressRepositoryImpl)
    singleOf<UserRepository>(::UserRepositoryImpl)

    // Use Cases
    factoryOf(::SetCurrentUserUseCase)
    factoryOf(::GetCurrentUserUseCase)
    factoryOf(::CreateUserUseCase)
    factoryOf(::GetAllVideosUseCase)
    factoryOf(::GetVideoByIdUseCase)
    factoryOf(::GetMentorsUseCase)
    factoryOf(::GetReviewsUseCase)
    factoryOf(::GetAverageRatingUseCase)
    factoryOf(::PostReviewUseCase)
    factoryOf(::ProgressUseCase)

    // Authentication
    factory {
        CredentialManager.create(androidContext())
    }

    factory {
        val googleOptions = GetSignInWithGoogleOption.Builder(
            Constants.GOOGLE_CLIENT_ID
        ).build()

        GetCredentialRequest.Builder()
            .addCredentialOption(googleOptions)
            .build()
    }

    // ViewModels
    viewModel { AuthentificationViewModel(get(), get(), get()) }
    viewModel { VideoViewModel(get()) }
    viewModel { MentorViewModel(get()) }
    viewModel { ReviewsViewModel(get(), get(), get()) }
    viewModel { ProgressViewModel(get()) }
}