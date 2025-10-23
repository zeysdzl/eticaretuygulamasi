package com.zsd.eticaretuygulamasi.retrofit

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class ApiUtils {
    companion object {
        private const val BASE_URL = "http://kasimadalan.pe.hu/"

        fun getUrunlerDao(): UrunlerDao {
            return Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(UrunlerDao::class.java)
        }
    }
}