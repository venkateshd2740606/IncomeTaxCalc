package com.incometaxcalc.di

import com.incometaxcalc.data.repository.ChallengeRepositoryImpl
import com.incometaxcalc.data.repository.GameRepositoryImpl
import com.incometaxcalc.data.repository.PreferencesRepositoryImpl
import com.incometaxcalc.data.repository.ProgressionRepositoryImpl
import com.incometaxcalc.domain.repository.ChallengeRepository
import com.incometaxcalc.domain.repository.GameRepository
import com.incometaxcalc.domain.repository.PreferencesRepository
import com.incometaxcalc.domain.repository.ProgressionRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    @Binds @Singleton abstract fun bindGameRepository(impl: GameRepositoryImpl): GameRepository
    @Binds @Singleton abstract fun bindChallengeRepository(impl: ChallengeRepositoryImpl): ChallengeRepository
    @Binds @Singleton abstract fun bindProgressionRepository(impl: ProgressionRepositoryImpl): ProgressionRepository
    @Binds @Singleton abstract fun bindPreferencesRepository(impl: PreferencesRepositoryImpl): PreferencesRepository
}
