package com.ayberk.emarket

import android.app.Application
import android.util.Log
import androidx.room.Room
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.ayberk.emarket.data.room.AppDatabase

class MyApplication : Application() {

    // Veritabanı nesnesi
    lateinit var db: AppDatabase

    override fun onCreate() {
        super.onCreate()

        // Veritabanı silme işlemi (isteğe bağlı)
        deleteDatabase()

        // Migration işlemi
        val migration1To2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                // Yeni 'quantity' sütununu ekliyoruz
                database.execSQL("ALTER TABLE cart ADD COLUMN quantity INTEGER NOT NULL DEFAULT 1")
            }
        }

        // Veritabanı oluşturuluyor ve migration ekleniyor
        db = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java,
            "cart_database"
        )
            .addMigrations(migration1To2)  // Migration'ı ekliyoruz
            .fallbackToDestructiveMigration()  // Veritabanı uyumsuzluklarını silerek geçiş yapar
            .build()

        // Veritabanı işlemleri burada devam edebilir
    }

    // Veritabanı dosyasını manuel olarak silme
    private fun deleteDatabase() {
        try {
            val context = applicationContext
            val dbFile = context.getDatabasePath("cart_database")
            if (dbFile.exists()) {
                // Veritabanı dosyasını sil
                dbFile.delete()
                Log.d("MyApplication", "Veritabanı başarıyla silindi.")
            }
        } catch (e: Exception) {
            Log.e("MyApplication", "Veritabanı silinirken hata oluştu", e)
        }
    }
}
