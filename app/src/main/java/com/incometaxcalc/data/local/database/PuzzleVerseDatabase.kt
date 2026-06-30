package com.incometaxcalc.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.incometaxcalc.data.local.database.dao.AchievementDao
import com.incometaxcalc.data.local.database.dao.ChallengeDao
import com.incometaxcalc.data.local.database.dao.EconomyDao
import com.incometaxcalc.data.local.database.dao.GameDao
import com.incometaxcalc.data.local.database.dao.ProfileDao
import com.incometaxcalc.data.local.database.dao.StatsDao
import com.incometaxcalc.data.local.database.entity.ProfileEntity
import com.incometaxcalc.data.local.database.entity.AchievementEntity
import com.incometaxcalc.data.local.database.entity.ChallengeEntity
import com.incometaxcalc.data.local.database.entity.EconomyEntity
import com.incometaxcalc.data.local.database.entity.GameEntity
import com.incometaxcalc.data.local.database.entity.StatsEntity

@Database(
    entities = [
        GameEntity::class,
        StatsEntity::class,
        AchievementEntity::class,
        ChallengeEntity::class,
        EconomyEntity::class,
        ProfileEntity::class
    ],
    version = 3,
    exportSchema = true
)
abstract class IncomeTaxCalcDatabase : RoomDatabase() {
    abstract fun gameDao(): GameDao
    abstract fun statsDao(): StatsDao
    abstract fun achievementDao(): AchievementDao
    abstract fun challengeDao(): ChallengeDao
    abstract fun economyDao(): EconomyDao
    abstract fun profileDao(): ProfileDao
}
