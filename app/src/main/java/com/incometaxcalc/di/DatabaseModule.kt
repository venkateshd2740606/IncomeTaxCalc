package com.incometaxcalc.di

import android.content.Context
import androidx.room.Room
import com.incometaxcalc.data.local.database.IncomeTaxCalcDatabase
import com.incometaxcalc.data.local.database.dao.AchievementDao
import com.incometaxcalc.data.local.database.dao.ChallengeDao
import com.incometaxcalc.data.local.database.dao.EconomyDao
import com.incometaxcalc.data.local.database.dao.GameDao
import com.incometaxcalc.data.local.database.dao.ProfileDao
import com.incometaxcalc.data.local.database.dao.StatsDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): IncomeTaxCalcDatabase =
        Room.databaseBuilder(context, IncomeTaxCalcDatabase::class.java, "IncomeTaxCalc.db")
            .fallbackToDestructiveMigration()
            .build()

    @Provides fun provideGameDao(db: IncomeTaxCalcDatabase): GameDao = db.gameDao()
    @Provides fun provideStatsDao(db: IncomeTaxCalcDatabase): StatsDao = db.statsDao()
    @Provides fun provideAchievementDao(db: IncomeTaxCalcDatabase): AchievementDao = db.achievementDao()
    @Provides fun provideChallengeDao(db: IncomeTaxCalcDatabase): ChallengeDao = db.challengeDao()
    @Provides fun provideEconomyDao(db: IncomeTaxCalcDatabase): EconomyDao = db.economyDao()
    @Provides fun provideProfileDao(db: IncomeTaxCalcDatabase): ProfileDao = db.profileDao()
}
