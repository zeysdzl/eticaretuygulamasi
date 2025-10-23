package com.zsd.eticaretuygulamasi.di

import android.content.Context // Context için import
import android.content.SharedPreferences // SharedPreferences için import
import com.zsd.eticaretuygulamasi.data.datasource.UrunlerDataSource
import com.zsd.eticaretuygulamasi.data.repo.FavorilerRepository // FavorilerRepository importu
import com.zsd.eticaretuygulamasi.data.repo.UrunlerRepository
import com.zsd.eticaretuygulamasi.retrofit.ApiUtils
import com.zsd.eticaretuygulamasi.retrofit.UrunlerDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext // ApplicationContext için import
import dagger.hilt.components.SingletonComponent
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

    // SharedPreferences nesnesini Hilt'e tanıtıyoruz
    @Provides
    @Singleton
    fun provideSharedPreferences(@ApplicationContext context: Context): SharedPreferences {
        // "Favoriler" adında bir SharedPreferences dosyası oluşturur/açar
        return context.getSharedPreferences("Favoriler", Context.MODE_PRIVATE)
    }

    // FavorilerRepository'yi Hilt'e tanıtıyoruz (SharedPreferences otomatik enjekte edilecek)
    @Provides
    @Singleton
    fun provideFavorilerRepository(sharedPreferences: SharedPreferences): FavorilerRepository {
        return FavorilerRepository(sharedPreferences)
    }
}

