package com.zsd.eticaretuygulamasi.di

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.zsd.eticaretuygulamasi.data.datasource.UrunlerDataSource
import com.zsd.eticaretuygulamasi.data.repo.*
import com.zsd.eticaretuygulamasi.retrofit.ApiUtils
import com.zsd.eticaretuygulamasi.retrofit.UrunlerDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class AppModule {


    @Provides
    @Singleton
    fun provideUrunlerRepository(uds: UrunlerDataSource): UrunlerRepository {
        return UrunlerRepository(uds)
    }

    @Provides
    @Singleton
    fun provideUrunlerDataSource(udao: UrunlerDao): UrunlerDataSource {
        return UrunlerDataSource(udao)
    }

    @Provides
    @Singleton
    fun provideUrunlerDao(): UrunlerDao {
        return ApiUtils.getUrunlerDao()
    }



    @Provides
    @Singleton
    @Named("favori_prefs")
    fun provideFavoriSharedPreferences(@ApplicationContext context: Context): SharedPreferences {
        return context.getSharedPreferences("Favoriler", Context.MODE_PRIVATE)
    }

    @Provides
    @Singleton
    @Named("history_prefs")
    fun provideHistorySharedPreferences(@ApplicationContext context: Context): SharedPreferences {
        return context.getSharedPreferences("History", Context.MODE_PRIVATE)
    }

    @Provides
    @Singleton
    @Named("adres_prefs")
    fun provideAdresSharedPreferences(@ApplicationContext context: Context): SharedPreferences {
        return context.getSharedPreferences("Adresler", Context.MODE_PRIVATE)
    }

    @Provides
    @Singleton
    @Named("siparis_prefs")
    fun provideSiparisSharedPreferences(@ApplicationContext context: Context): SharedPreferences {
        return context.getSharedPreferences("Siparisler", Context.MODE_PRIVATE)
    }

    @Provides
    @Singleton
    @Named("odeme_prefs")
    fun provideOdemeSharedPreferences(@ApplicationContext context: Context): SharedPreferences {
        return context.getSharedPreferences("OdemeYontemleri", Context.MODE_PRIVATE)
    }


    @Provides
    @Singleton
    @Named("profil_prefs")
    fun provideProfilSharedPreferences(@ApplicationContext context: Context): SharedPreferences {
        return context.getSharedPreferences("ProfilBilgileri", Context.MODE_PRIVATE)
    }


    @Provides
    @Singleton
    fun provideGson(): Gson {
        return Gson()
    }


    @Provides
    @Singleton
    fun provideFavorilerRepository(@Named("favori_prefs") sharedPreferences: SharedPreferences): FavsRepository {
        return FavsRepository(sharedPreferences)
    }

    @Provides
    @Singleton
    fun provideHistoryRepository(
        @Named("history_prefs") sharedPreferences: SharedPreferences,
        gson: Gson
    ): HistoryRepository {
        return HistoryRepository(sharedPreferences, gson)
    }

    @Provides
    @Singleton
    fun provideAdresRepository(
        @Named("adres_prefs") sharedPreferences: SharedPreferences,
        gson: Gson
    ): AdresRepository {
        return AdresRepository(sharedPreferences, gson)
    }

    @Provides
    @Singleton
    fun provideSiparisRepository(
        @Named("siparis_prefs") sharedPreferences: SharedPreferences,
        gson: Gson
    ): SiparisRepository {
        return SiparisRepository(sharedPreferences, gson)
    }

    @Provides
    @Singleton
    fun provideOdemeRepository(
        @Named("odeme_prefs") sharedPreferences: SharedPreferences,
        gson: Gson
    ): OdemeRepository {
        return OdemeRepository(sharedPreferences, gson)
    }


    @Provides
    @Singleton
    fun provideProfilRepository(
        @Named("profil_prefs") sharedPreferences: SharedPreferences,
        gson: Gson
    ): ProfilRepository {
        return ProfilRepository(sharedPreferences, gson)
    }
}