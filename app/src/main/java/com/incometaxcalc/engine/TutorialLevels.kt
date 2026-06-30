package com.incometaxcalc.engine

import com.incometaxcalc.domain.model.IncomeTaxCalcLevel
import com.incometaxcalc.domain.model.Difficulty

object TutorialLevels {

    val all: List<IncomeTaxCalcLevel> = listOf(
        level1Basics(),
        level2TwoColors(),
        level3HiddenOrder(),
        level4Stacking(),
        level5Challenge()
    )

    fun getTutorialLevel(index: Int): IncomeTaxCalcLevel? = all.getOrNull(index)

    private fun level1Basics(): IncomeTaxCalcLevel = IncomeTaxCalcLevel(
        seed = 1,
        levelNumber = 1,
        difficulty = Difficulty.BEGINNER,
        isTutorial = true,
        colorCount = 2,
        initialTubes = listOf(
            listOf(0, 1),
            listOf(1, 0),
            emptyList()
        )
    )

    private fun level2TwoColors(): IncomeTaxCalcLevel = IncomeTaxCalcLevel(
        seed = 2,
        levelNumber = 2,
        difficulty = Difficulty.BEGINNER,
        isTutorial = true,
        colorCount = 2,
        initialTubes = listOf(
            listOf(0, 0, 1),
            listOf(1, 1, 0),
            emptyList()
        )
    )

    private fun level3HiddenOrder(): IncomeTaxCalcLevel = IncomeTaxCalcLevel(
        seed = 3,
        levelNumber = 3,
        difficulty = Difficulty.EASY,
        isTutorial = true,
        colorCount = 3,
        initialTubes = listOf(
            listOf(0, 1, 2),
            listOf(2, 0, 1),
            listOf(1, 2, 0),
            emptyList()
        )
    )

    private fun level4Stacking(): IncomeTaxCalcLevel = IncomeTaxCalcLevel(
        seed = 4,
        levelNumber = 4,
        difficulty = Difficulty.EASY,
        isTutorial = true,
        colorCount = 3,
        initialTubes = listOf(
            listOf(0, 1, 1, 2),
            listOf(2, 0, 0, 1),
            listOf(1, 2, 2, 0),
            emptyList(),
            emptyList()
        )
    )

    private fun level5Challenge(): IncomeTaxCalcLevel = IncomeTaxCalcLevel(
        seed = 5,
        levelNumber = 5,
        difficulty = Difficulty.MEDIUM,
        isTutorial = true,
        colorCount = 4,
        initialTubes = listOf(
            listOf(0, 1, 2, 3),
            listOf(3, 0, 1, 2),
            listOf(2, 3, 0, 1),
            listOf(1, 2, 3, 0),
            emptyList(),
            emptyList()
        )
    )
}
