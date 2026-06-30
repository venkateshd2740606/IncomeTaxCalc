package com.incometaxcalc.domain.repository

import com.incometaxcalc.domain.model.Achievement
import com.incometaxcalc.domain.model.ChallengeRecord
import com.incometaxcalc.domain.model.ChallengeType
import com.incometaxcalc.domain.model.IncomeTaxCalcGame
import com.incometaxcalc.domain.model.IncomeTaxCalcLevel
import com.incometaxcalc.domain.model.Difficulty
import com.incometaxcalc.domain.model.EconomyState
import com.incometaxcalc.domain.model.PuzzleProfile
import com.incometaxcalc.domain.model.UserStats
import kotlinx.coroutines.flow.Flow

interface GameRepository {
    suspend fun createNewGame(difficulty: Difficulty, levelNumber: Int): IncomeTaxCalcGame
    suspend fun createGameFromSeed(seed: Long, levelNumber: Int, difficulty: Difficulty): IncomeTaxCalcGame
    suspend fun createTutorialGame(tutorialIndex: Int): IncomeTaxCalcGame?
    suspend fun createEndlessGame(wave: Int): IncomeTaxCalcGame
    suspend fun saveGame(game: IncomeTaxCalcGame): Long
    suspend fun getGame(gameId: Long): IncomeTaxCalcGame?
    suspend fun getInProgressGame(): IncomeTaxCalcGame?
    fun observeInProgressGame(): Flow<IncomeTaxCalcGame?>
    suspend fun completeGame(game: IncomeTaxCalcGame): IncomeTaxCalcGame
    suspend fun abandonGame(gameId: Long)
    suspend fun getLevel(seed: Long, levelNumber: Int, difficulty: Difficulty): IncomeTaxCalcLevel
}

interface ChallengeRepository {
    suspend fun getChallenge(type: ChallengeType, key: String): ChallengeRecord?
    suspend fun createChallenge(type: ChallengeType, key: String, difficulty: Difficulty): ChallengeRecord
    suspend fun resolveActiveChallenge(type: ChallengeType): ChallengeRecord
    fun observeActiveChallenge(type: ChallengeType): Flow<ChallengeRecord?>
    suspend fun completeChallenge(record: ChallengeRecord, timeSeconds: Long, moves: Int): ChallengeRecord
    fun observeChallengeHistory(type: ChallengeType): Flow<List<ChallengeRecord>>
    suspend fun getCurrentStreak(type: ChallengeType): Int
    suspend fun getChallengeGame(record: ChallengeRecord): IncomeTaxCalcGame
}

interface ProgressionRepository {
    fun observeStats(): Flow<UserStats>
    suspend fun getStats(): UserStats
    suspend fun updateStatsAfterGame(game: IncomeTaxCalcGame)
    suspend fun grantChallengeRewards(rewardCoins: Int, rewardXp: Int)
    fun observePuzzleProfile(): Flow<PuzzleProfile>
    suspend fun getPuzzleProfile(): PuzzleProfile
    fun observeAchievements(): Flow<List<Achievement>>
    suspend fun checkAndUnlockAchievements(
        game: IncomeTaxCalcGame,
        sameDevicePlayed: Boolean = false
    ): List<Achievement>
    fun observeEconomy(): Flow<EconomyState>
    suspend fun getEconomy(): EconomyState
    suspend fun spendCoins(amount: Int): Boolean
    suspend fun earnCoins(amount: Int)
    suspend fun unlockTheme(themeId: String): Boolean
}

interface PreferencesRepository {
    fun getUserPreferences(): Flow<com.incometaxcalc.domain.model.UserPreferences>
    suspend fun updatePreferences(transform: (com.incometaxcalc.domain.model.UserPreferences) -> com.incometaxcalc.domain.model.UserPreferences)
    suspend fun getCampaignLevel(difficulty: Difficulty): Int
    suspend fun advanceCampaignLevel(difficulty: Difficulty): Int
}
