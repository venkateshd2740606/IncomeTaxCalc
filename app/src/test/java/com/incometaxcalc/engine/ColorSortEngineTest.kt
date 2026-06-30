package com.incometaxcalc.engine

import com.incometaxcalc.domain.model.Difficulty
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class IncomeTaxCalcEngineTest {

    @Test
    fun tutorialLevel_isValidAndSolvable() {
        val level = TutorialLevels.getTutorialLevel(0)!!
        assertTrue(IncomeTaxCalcEngine.validateLevel(level))
    }

    @Test
    fun pour_updatesTubeState() {
        val level = TutorialLevels.getTutorialLevel(0)!!
        var game = IncomeTaxCalcEngine.createInitialGame(level)
        assertTrue(IncomeTaxCalcEngine.canPour(game, 0, 2))
        game = IncomeTaxCalcEngine.pour(game, 0, 2)
        assertEquals(1, game.moves)
        assertTrue(game.tubes[2].isNotEmpty())
    }

    @Test
    fun solveTutorial_completesGame() {
        val level = TutorialLevels.getTutorialLevel(0)!!
        var game = IncomeTaxCalcEngine.createInitialGame(level)
        val solution = IncomeTaxCalcEngine.solve(game)!!
        solution.forEach { (from, to) ->
            game = IncomeTaxCalcEngine.pour(game, from, to)
        }
        assertTrue(IncomeTaxCalcEngine.isWon(game))
    }

    @Test
    fun generatedLevel_isValid() {
        val level = IncomeTaxCalcGenerator.generate(12345L, 1, Difficulty.EASY)
        assertTrue(IncomeTaxCalcEngine.validateLevel(level))
    }

    @Test
    fun tubeSelection_poursWhenSecondTubeSelected() {
        val level = TutorialLevels.getTutorialLevel(0)!!
        var game = IncomeTaxCalcEngine.createInitialGame(level)
        game = IncomeTaxCalcEngine.onTubeSelected(game, 0)
        assertEquals(0, game.selectedTubeId)
        game = IncomeTaxCalcEngine.onTubeSelected(game, 2)
        assertEquals(1, game.moves)
    }

    @Test
    fun generator_sameSeed_producesSameLevel() {
        val a = IncomeTaxCalcGenerator.generate(999L, 5, Difficulty.MEDIUM)
        val b = IncomeTaxCalcGenerator.generate(999L, 5, Difficulty.MEDIUM)
        assertEquals(a.initialTubes, b.initialTubes)
    }
}
