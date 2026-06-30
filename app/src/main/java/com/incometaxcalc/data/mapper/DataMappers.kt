package com.incometaxcalc.data.mapper

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.incometaxcalc.data.local.database.entity.AchievementEntity
import com.incometaxcalc.data.local.database.entity.ChallengeEntity
import com.incometaxcalc.data.local.database.entity.EconomyEntity
import com.incometaxcalc.data.local.database.entity.GameEntity
import com.incometaxcalc.data.local.database.entity.ProfileEntity
import com.incometaxcalc.data.local.database.entity.StatsEntity
import com.incometaxcalc.domain.model.PuzzleArchetype
import com.incometaxcalc.domain.model.PuzzleProfile
import com.incometaxcalc.domain.model.PuzzleProfileMetrics
import com.incometaxcalc.domain.model.SkillCategory
import com.incometaxcalc.domain.model.Achievement
import com.incometaxcalc.domain.model.ChallengeRecord
import com.incometaxcalc.domain.model.ChallengeType
import com.incometaxcalc.domain.model.IncomeTaxCalcGame
import com.incometaxcalc.domain.model.IncomeTaxCalcLevel
import com.incometaxcalc.domain.model.Difficulty
import com.incometaxcalc.domain.model.EconomyState
import com.incometaxcalc.domain.model.GameStatus
import com.incometaxcalc.domain.model.UserStats

object DataMappers {
    private val gson = Gson()
    private val intListType = object : TypeToken<List<List<Int>>>() {}.type

    fun toEntity(game: IncomeTaxCalcGame): GameEntity = GameEntity(
        id = game.id,
        seed = game.level.seed,
        levelNumber = game.level.levelNumber,
        difficulty = game.level.difficulty.name,
        status = game.status.name,
        tubeStateJson = gson.toJson(game.tubes),
        selectedTubeId = game.selectedTubeId ?: -1,
        moves = game.moves,
        hintsUsed = game.hintsUsed,
        elapsedSeconds = game.elapsedSeconds,
        createdAt = game.createdAt,
        lastPlayedAt = game.lastPlayedAt,
        completedAt = game.completedAt,
        isTutorial = game.level.isTutorial,
        isEndless = game.level.isEndless,
        challengeType = game.level.challengeType?.name,
        challengeKey = game.level.challengeKey,
        levelJson = gson.toJson(
            LevelJson(
                initialTubes = game.level.initialTubes,
                tubeCapacity = game.level.tubeCapacity,
                colorCount = game.level.colorCount
            )
        ),
        coinsEarned = game.coinsEarned,
        xpEarned = game.xpEarned
    )

    fun fromEntity(entity: GameEntity): IncomeTaxCalcGame {
        val levelJson = gson.fromJson(entity.levelJson, LevelJson::class.java)
        val tubes: List<List<Int>> = gson.fromJson(entity.tubeStateJson, intListType)
        val level = IncomeTaxCalcLevel(
            id = entity.id,
            seed = entity.seed,
            levelNumber = entity.levelNumber,
            difficulty = Difficulty.valueOf(entity.difficulty),
            initialTubes = levelJson.initialTubes,
            tubeCapacity = levelJson.tubeCapacity,
            colorCount = levelJson.colorCount,
            isTutorial = entity.isTutorial,
            isEndless = entity.isEndless,
            challengeType = entity.challengeType?.let { ChallengeType.valueOf(it) },
            challengeKey = entity.challengeKey
        )
        return IncomeTaxCalcGame(
            id = entity.id,
            level = level,
            status = GameStatus.valueOf(entity.status),
            tubes = tubes,
            selectedTubeId = entity.selectedTubeId.takeIf { it >= 0 },
            moves = entity.moves,
            hintsUsed = entity.hintsUsed,
            elapsedSeconds = entity.elapsedSeconds,
            createdAt = entity.createdAt,
            lastPlayedAt = entity.lastPlayedAt,
            completedAt = entity.completedAt,
            coinsEarned = entity.coinsEarned,
            xpEarned = entity.xpEarned
        )
    }

    fun toStatsEntity(stats: UserStats): StatsEntity = StatsEntity(
        gamesPlayed = stats.gamesPlayed,
        gamesWon = stats.gamesWon,
        gamesAbandoned = stats.gamesAbandoned,
        totalPlayTimeSeconds = stats.totalPlayTimeSeconds,
        fastestTimeBeginner = stats.fastestTimeBeginner,
        fastestTimeEasy = stats.fastestTimeEasy,
        fastestTimeMedium = stats.fastestTimeMedium,
        fastestTimeHard = stats.fastestTimeHard,
        fastestTimeExpert = stats.fastestTimeExpert,
        fastestTimeMaster = stats.fastestTimeMaster,
        currentStreak = stats.currentStreak,
        longestStreak = stats.longestStreak,
        lastPlayedDate = stats.lastPlayedDate,
        xpPoints = stats.xpPoints,
        level = stats.level,
        hintsUsedTotal = stats.hintsUsedTotal,
        perfectGames = stats.perfectGames,
        poursTotal = stats.poursTotal,
        endlessHighScore = stats.endlessHighScore
    )

    fun fromStatsEntity(entity: StatsEntity?): UserStats {
        if (entity == null) return UserStats()
        return UserStats(
            gamesPlayed = entity.gamesPlayed,
            gamesWon = entity.gamesWon,
            gamesAbandoned = entity.gamesAbandoned,
            totalPlayTimeSeconds = entity.totalPlayTimeSeconds,
            fastestTimeBeginner = entity.fastestTimeBeginner,
            fastestTimeEasy = entity.fastestTimeEasy,
            fastestTimeMedium = entity.fastestTimeMedium,
            fastestTimeHard = entity.fastestTimeHard,
            fastestTimeExpert = entity.fastestTimeExpert,
            fastestTimeMaster = entity.fastestTimeMaster,
            currentStreak = entity.currentStreak,
            longestStreak = entity.longestStreak,
            lastPlayedDate = entity.lastPlayedDate,
            xpPoints = entity.xpPoints,
            level = entity.level,
            hintsUsedTotal = entity.hintsUsedTotal,
            perfectGames = entity.perfectGames,
            poursTotal = entity.poursTotal,
            endlessHighScore = entity.endlessHighScore
        )
    }

    fun toChallengeEntity(record: ChallengeRecord): ChallengeEntity = ChallengeEntity(
        key = record.key,
        type = record.type.name,
        seed = record.seed,
        difficulty = record.difficulty.name,
        isCompleted = record.isCompleted,
        completionTime = record.completionTime,
        moves = record.moves,
        rewardCoins = record.rewardCoins,
        rewardXp = record.rewardXp,
        streakDay = record.streakDay
    )

    fun fromChallengeEntity(entity: ChallengeEntity): ChallengeRecord = ChallengeRecord(
        key = entity.key,
        type = ChallengeType.valueOf(entity.type),
        seed = entity.seed,
        difficulty = Difficulty.valueOf(entity.difficulty),
        isCompleted = entity.isCompleted,
        completionTime = entity.completionTime,
        moves = entity.moves,
        rewardCoins = entity.rewardCoins,
        rewardXp = entity.rewardXp,
        streakDay = entity.streakDay
    )

    fun toEconomyEntity(state: EconomyState): EconomyEntity = EconomyEntity(
        coins = state.coins,
        totalCoinsEarned = state.totalCoinsEarned,
        totalCoinsSpent = state.totalCoinsSpent,
        unlockedThemes = gson.toJson(state.unlockedThemeIds.toList())
    )

    fun fromEconomyEntity(entity: EconomyEntity?): EconomyState {
        if (entity == null) return EconomyState()
        val type = object : TypeToken<List<String>>() {}.type
        val unlocked: List<String> = gson.fromJson(entity.unlockedThemes, type) ?: emptyList()
        return EconomyState(
            coins = entity.coins,
            totalCoinsEarned = entity.totalCoinsEarned,
            totalCoinsSpent = entity.totalCoinsSpent,
            unlockedThemeIds = unlocked.toSet()
        )
    }

    fun mergeAchievement(def: Achievement, entity: AchievementEntity?): Achievement =
        def.copy(
            isUnlocked = entity?.isUnlocked ?: false,
            unlockedAt = entity?.unlockedAt,
            progress = entity?.progress ?: 0
        )

    fun toProfileEntity(profile: PuzzleProfile): ProfileEntity = ProfileEntity(
        gamesAnalyzed = profile.metrics.gamesAnalyzed,
        totalSolveTimeSeconds = profile.metrics.totalSolveTimeSeconds,
        totalMoves = profile.metrics.totalMoves,
        totalOptimalMoves = profile.metrics.totalOptimalMoves,
        totalHintsUsed = profile.metrics.totalHintsUsed,
        fastCompletions = profile.metrics.fastCompletions,
        slowCompletions = profile.metrics.slowCompletions,
        perfectCompletions = profile.metrics.perfectCompletions,
        complexChainWins = profile.metrics.complexChainWins,
        inefficientWins = profile.metrics.inefficientWins,
        hintHeavyWins = profile.metrics.hintHeavyWins,
        archetype = profile.archetype.name,
        strength = profile.strength.name,
        weakness = profile.weakness.name,
        adaptiveColorModifier = profile.adaptiveColorModifier
    )

    fun fromProfileEntity(entity: ProfileEntity?): PuzzleProfile {
        if (entity == null) return PuzzleProfile()
        val metrics = PuzzleProfileMetrics(
            gamesAnalyzed = entity.gamesAnalyzed,
            totalSolveTimeSeconds = entity.totalSolveTimeSeconds,
            totalMoves = entity.totalMoves,
            totalOptimalMoves = entity.totalOptimalMoves,
            totalHintsUsed = entity.totalHintsUsed,
            fastCompletions = entity.fastCompletions,
            slowCompletions = entity.slowCompletions,
            perfectCompletions = entity.perfectCompletions,
            complexChainWins = entity.complexChainWins,
            inefficientWins = entity.inefficientWins,
            hintHeavyWins = entity.hintHeavyWins
        )
        return PuzzleProfile(
            metrics = metrics,
            archetype = runCatching { PuzzleArchetype.valueOf(entity.archetype) }
                .getOrDefault(PuzzleArchetype.EXPLORER),
            strength = runCatching { SkillCategory.valueOf(entity.strength) }
                .getOrDefault(SkillCategory.PATTERN_RECOGNITION),
            weakness = runCatching { SkillCategory.valueOf(entity.weakness) }
                .getOrDefault(SkillCategory.TIME_PRESSURE),
            adaptiveColorModifier = entity.adaptiveColorModifier
        )
    }

    data class LevelJson(
        val initialTubes: List<List<Int>>,
        val tubeCapacity: Int,
        val colorCount: Int
    )
}
