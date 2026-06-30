package com.incometaxcalc.engine

import com.incometaxcalc.domain.model.IncomeTaxCalcLevel
import com.incometaxcalc.domain.model.Difficulty
import com.incometaxcalc.domain.model.GenerationProfile
import kotlin.random.Random

object IncomeTaxCalcGenerator {

    fun generate(
        seed: Long,
        levelNumber: Int,
        difficulty: Difficulty,
        generationProfile: GenerationProfile = GenerationProfile()
    ): IncomeTaxCalcLevel {
        var attemptSeed = seed
        repeat(64) {
            val level = buildLevel(attemptSeed, levelNumber, difficulty, generationProfile)
            if (IncomeTaxCalcEngine.validateLevel(level)) return level
            attemptSeed++
        }
        return TutorialLevels.getTutorialLevel(1)!!.copy(
            seed = seed,
            levelNumber = levelNumber,
            difficulty = difficulty,
            isTutorial = false,
            isEndless = difficulty == Difficulty.ENDLESS
        )
    }

    fun generateForChallenge(
        seed: Long,
        levelNumber: Int,
        difficulty: Difficulty
    ): IncomeTaxCalcLevel = generate(seed, levelNumber, difficulty)

    fun seedFromLevelNumber(levelNumber: Int, difficulty: Difficulty): Long {
        val difficultyOffset = difficulty.ordinal * 100_000L
        return levelNumber.toLong() * 9973L + difficultyOffset + 42L
    }

    fun formatShareText(seed: Long, levelNumber: Int, difficulty: Difficulty): String =
        "Color Sort Level\nSeed: $seed\nLevel: $levelNumber\nDifficulty: ${difficulty.name}"

    private fun buildLevel(
        seed: Long,
        levelNumber: Int,
        difficulty: Difficulty,
        profile: GenerationProfile,
        forceSimple: Boolean = false
    ): IncomeTaxCalcLevel {
        val random = Random(seed)
        val colorCount = colorCountFor(difficulty, profile.colorCountModifier, forceSimple)
        val emptyTubes = emptyTubeCountFor(difficulty, profile.emptyTubeModifier, forceSimple)
        val capacity = IncomeTaxCalcLevel.DEFAULT_CAPACITY
        val totalTubes = colorCount + emptyTubes

        val solved = List(colorCount) { color ->
            List(capacity) { color }
        } + List(emptyTubes) { emptyList<Int>() }

        val shuffled = scrambleFromSolved(solved, random, shuffleMovesFor(difficulty, forceSimple))
        val level = IncomeTaxCalcLevel(
            seed = seed,
            levelNumber = levelNumber,
            difficulty = difficulty,
            initialTubes = shuffled,
            tubeCapacity = capacity,
            colorCount = colorCount,
            isEndless = difficulty == Difficulty.ENDLESS
        )
        if (IncomeTaxCalcEngine.validateLevel(level)) return level

        val minimal = scrambleFromSolved(solved, random, 6)
        return IncomeTaxCalcLevel(
            seed = seed,
            levelNumber = levelNumber,
            difficulty = difficulty,
            initialTubes = minimal,
            tubeCapacity = capacity,
            colorCount = colorCount,
            isEndless = difficulty == Difficulty.ENDLESS
        )
    }

    private fun scrambleFromSolved(
        solved: List<List<Int>>,
        random: Random,
        moves: Int
    ): List<List<Int>> {
        var tubes = solved.map { it.toMutableList() }
        var made = 0
        var attempts = 0
        while (made < moves && attempts < moves * 20) {
            attempts++
            val from = random.nextInt(tubes.size)
            val to = random.nextInt(tubes.size)
            if (from == to) continue
            val game = IncomeTaxCalcEngine.createInitialGame(
                IncomeTaxCalcLevel(
                    seed = 0,
                    levelNumber = 1,
                    difficulty = Difficulty.EASY,
                    initialTubes = tubes.map { it.toList() },
                    colorCount = tubes.count { it.isNotEmpty() }
                )
            )
            if (!IncomeTaxCalcEngine.canPour(game, from, to)) continue
            val updated = IncomeTaxCalcEngine.pour(game, from, to)
            tubes = updated.tubes.map { it.toMutableList() }
            made++
        }
        return tubes.map { it.toList() }
    }

    private fun colorCountFor(difficulty: Difficulty, modifier: Int, forceSimple: Boolean): Int {
        if (forceSimple) return 2
        val base = when (difficulty) {
            Difficulty.BEGINNER -> 2
            Difficulty.EASY -> 3
            Difficulty.MEDIUM -> 4
            Difficulty.HARD -> 5
            Difficulty.EXPERT -> 6
            Difficulty.MASTER -> 7
            Difficulty.ENDLESS -> 4
        }
        return (base + modifier).coerceIn(2, 8)
    }

    private fun emptyTubeCountFor(difficulty: Difficulty, modifier: Int, forceSimple: Boolean): Int {
        if (forceSimple) return 1
        val base = when (difficulty) {
            Difficulty.BEGINNER, Difficulty.EASY -> 1
            Difficulty.MEDIUM, Difficulty.HARD -> 2
            else -> 2
        }
        return (base + modifier).coerceIn(1, 3)
    }

    private fun shuffleMovesFor(difficulty: Difficulty, forceSimple: Boolean): Int = when {
        forceSimple -> 8
        difficulty == Difficulty.BEGINNER -> 12
        difficulty == Difficulty.EASY -> 18
        difficulty == Difficulty.MEDIUM -> 28
        difficulty == Difficulty.HARD -> 38
        difficulty == Difficulty.EXPERT -> 48
        difficulty == Difficulty.MASTER -> 60
        else -> 30
    }
}
