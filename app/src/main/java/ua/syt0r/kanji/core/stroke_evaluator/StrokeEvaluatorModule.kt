package ua.syt0r.kanji.core.stroke_evaluator

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object StrokeEvaluatorModule {

    @Provides
    @Singleton
    fun provide(): KanjiStrokeEvaluator {
        return DefaultKanjiStrokeEvaluator()
    }

}