package com.incometaxcalc.multiplayer

import com.incometaxcalc.domain.model.IncomeTaxCalcGame
import com.incometaxcalc.domain.model.Difficulty
import com.incometaxcalc.domain.model.MultiplayerMode
import com.incometaxcalc.domain.model.MultiplayerSession
import com.incometaxcalc.engine.IncomeTaxCalcEngine
import com.incometaxcalc.engine.IncomeTaxCalcGenerator
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PuzzleBotSession @Inject constructor() {
    private val _session = MutableStateFlow<MultiplayerSession?>(null)
    val session: StateFlow<MultiplayerSession?> = _session.asStateFlow()

    private var playerGame: IncomeTaxCalcGame? = null
    private var botGame: IncomeTaxCalcGame? = null
    private var playerName = "You"
    private val botName = "AI Bot"

    fun start(player: String, difficulty: Difficulty, seed: Long = System.currentTimeMillis()) {
        playerName = player
        val level = IncomeTaxCalcGenerator.generate(seed, 1, difficulty)
        val game = IncomeTaxCalcEngine.createInitialGame(level)
        playerGame = game
        botGame = game
        _session.value = MultiplayerSession(
            mode = MultiplayerMode.SAME_DEVICE,
            localPlayerName = playerName,
            remotePlayerName = botName,
            activePlayerName = playerName,
            isActive = true,
            seed = seed,
            difficulty = difficulty
        )
    }

    fun getPlayerGame(): IncomeTaxCalcGame? = playerGame

    fun applyPlayerTubeClick(tubeId: Int): IncomeTaxCalcGame? {
        val game = playerGame ?: return null
        val updated = IncomeTaxCalcEngine.onTubeSelected(game, tubeId)
        playerGame = updated
        botGame = updated
        return updated
    }

    fun applyBotMove(): IncomeTaxCalcGame? {
        val game = botGame ?: return null
        val hint = IncomeTaxCalcEngine.getHintMove(game) ?: return game
        var updated = IncomeTaxCalcEngine.onTubeSelected(game, hint.first)
        if (updated.selectedTubeId != null) {
            updated = IncomeTaxCalcEngine.onTubeSelected(updated, hint.second)
        }
        playerGame = updated
        botGame = updated
        val session = _session.value
        if (session != null) {
            _session.value = session.copy(
                remoteScore = session.remoteScore + if (updated.isCompleted) 1 else 0,
                activePlayerName = playerName
            )
        }
        return updated
    }

    fun onPlayerWon() {
        val session = _session.value ?: return
        _session.value = session.copy(
            localScore = session.localScore + 1,
            activePlayerName = playerName
        )
        startNewRound(session)
    }

    fun onBotWon() {
        val session = _session.value ?: return
        _session.value = session.copy(
            remoteScore = session.remoteScore + 1,
            activePlayerName = playerName
        )
        startNewRound(session)
    }

    private fun startNewRound(session: MultiplayerSession) {
        val newSeed = session.seed + session.localScore + session.remoteScore
        val level = IncomeTaxCalcGenerator.generate(newSeed, session.localScore + session.remoteScore + 1, session.difficulty)
        val game = IncomeTaxCalcEngine.createInitialGame(level)
        playerGame = game
        botGame = game
    }

    fun isBotThinking(): Boolean = false

    fun end() {
        _session.value = null
        playerGame = null
        botGame = null
    }
}
