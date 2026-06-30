package com.incometaxcalc.data

import com.incometaxcalc.domain.model.Difficulty
import com.incometaxcalc.domain.model.GameStatus
import com.incometaxcalc.engine.IncomeTaxCalcEngine
import com.incometaxcalc.engine.IncomeTaxCalcGenerator
import com.incometaxcalc.util.ProgressionCalculator
import org.junit.Assert.assertTrue
import org.junit.Test

class ProgressionCalculatorTest {

    @Test
    fun xpForCompletedGame_isPositive() {
        val level = IncomeTaxCalcGenerator.generate(1L, 1, Difficulty.EASY)
        val game = IncomeTaxCalcEngine.createInitialGame(level).copy(status = GameStatus.COMPLETED)
        assertTrue(ProgressionCalculator.xpForGame(game) > 0)
    }

    @Test
    fun xpForGame_withHints_isLowerThanWithoutHints() {
        val level = IncomeTaxCalcGenerator.generate(1L, 1, Difficulty.EASY)
        val withHints = IncomeTaxCalcEngine.createInitialGame(level).copy(hintsUsed = 2, status = GameStatus.COMPLETED)
        val noHints = IncomeTaxCalcEngine.createInitialGame(level).copy(hintsUsed = 0, status = GameStatus.COMPLETED)
        assertTrue(ProgressionCalculator.xpForGame(noHints) >= ProgressionCalculator.xpForGame(withHints))
    }
}
