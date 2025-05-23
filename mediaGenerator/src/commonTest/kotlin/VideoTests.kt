import androidx.compose.animation.Crossfade
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowColumn
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.LocalTextStyle
import androidx.compose.material.Text
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.ImageLoader
import coil3.PlatformContext
import coil3.compose.asPainter
import coil3.executeBlocking
import coil3.request.ImageRequest
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.runBlocking
import media.Res
import media.kanji_dojo_icon
import media.video_footer
import media.video_platforms
import org.jetbrains.compose.resources.painterResource
import org.junit.BeforeClass
import org.koin.compose.koinInject
import org.koin.core.context.startKoin
import org.koin.dsl.module
import ua.syt0r.kanji.core.app_data.AppDataRepository
import ua.syt0r.kanji.core.app_data.data.JapaneseWord
import ua.syt0r.kanji.core.app_data.data.formattedVocabReading
import ua.syt0r.kanji.core.sync.SyncFeatureState
import ua.syt0r.kanji.core.sync.SyncManager
import ua.syt0r.kanji.di.appModules
import ua.syt0r.kanji.presentation.common.ui.FuriganaText
import ua.syt0r.kanji.presentation.common.ui.kanji.StrokeWidth
import ua.syt0r.kanji.presentation.common.ui.kanji.defaultStrokeColor
import ua.syt0r.kanji.presentation.common.ui.kanji.drawKanjiStroke
import ua.syt0r.kanji.presentation.common.ui.kanji.parseKanjiStrokes
import ua.syt0r.kanji.presentation.screen.main.screen.practice_common.CharacterWriterDecorations
import kotlin.test.Test

class VideoTests {

    companion object {

        @BeforeClass
        @JvmStatic
        fun setup() {
            val testModule = module {
                single<SyncManager> {
                    mockk {
                        every { state } returns MutableStateFlow(SyncFeatureState.Disabled)
                    }
                }
            }
            startKoin { modules(appModules.plus(testModule)) }
        }

    }

    val animationSpeedMultiplier = 1

    data class VideoContent(
        val completable: CompletableDeferred<Unit> = CompletableDeferred(),
        val content: @Composable (CompletableDeferred<Unit>) -> Unit
    )

    data class VideoLetterDescriptor(
        val letter: String,
        val priorityExamples: List<Any> = emptyList(), // string or WordProvider
        val examplesMapper: (List<JapaneseWord>) -> List<JapaneseWord> = { it },
        val image: String? = null
    )

    data class WordProvider(
        val reading: String,
        val provider: (List<JapaneseWord>) -> JapaneseWord
    )

    data class VideoLetterData(
        val letter: String,
        val meanings: List<String>,
        val strokes: List<Path>,
        val examples: List<JapaneseWord>,
        val image: Painter?
    )

    val v1 = listOf<VideoLetterDescriptor>(
        VideoLetterDescriptor("止", listOf("止まる", "止まり")),
        VideoLetterDescriptor("口", listOf("口", "北口", "南口")),
        VideoLetterDescriptor("入", listOf("入口")),
        VideoLetterDescriptor("出", listOf("出口")),
        VideoLetterDescriptor("禁", listOf("禁止", "禁煙", "禁物"))
    )

    val v2 = listOf<VideoLetterDescriptor>(
        VideoLetterDescriptor(
            "料",
            listOf("無料", "有料")
        ),
        VideoLetterDescriptor("時", listOf("時間", "営業時間")),
        VideoLetterDescriptor(
            letter = "開",
            examplesMapper = {
                val m1 = it.first().copy(glossary = listOf("to open (e.g. business, etc.)"))
                listOf(m1).plus(it.drop(1))
            }
        ),
        VideoLetterDescriptor("閉", listOf("閉まる")),
        VideoLetterDescriptor("手", listOf("手", "お手洗い"))
    )

    @OptIn(ExperimentalTestApi::class)
    @Test
    fun writing() = composableRecorderTest {

        val appDataRepository = koinInject<AppDataRepository>()

        val contentList = remember {
            val imageLoader = ImageLoader.Builder(PlatformContext.INSTANCE).build()
            val letterDataList = v2.map { (letter, priorityWords, examplesMapper, imageUrl) ->
                runBlocking {
                    VideoLetterData(
                        letter = letter,
                        meanings = appDataRepository.getMeanings(letter),
                        strokes = parseKanjiStrokes(appDataRepository.getStrokes(letter)),
                        examples = priorityWords
                            .map {
                                when (it) {
                                    is String -> appDataRepository.findWords(null, it, null).first()
                                    is WordProvider -> appDataRepository
                                        .findWords(null, it.reading, null)
                                        .run { it.provider(this) }

                                    else -> error("not supported")
                                }
                            }
                            .plus(appDataRepository.getWordExamples(letter))
                            .distinctBy { it.id }
                            .take(5)
                            .let { examplesMapper(it) },
                        image = imageUrl?.let {
                            val request = ImageRequest.Builder(PlatformContext.INSTANCE)
                                .data(imageUrl)
                                .build()
                            val result = imageLoader.executeBlocking(request)
                            result.image!!.asPainter(PlatformContext.INSTANCE)
                        }
                    )
                }

            }
            val intro = VideoContent {

                LaunchedEffect(Unit) {
                    println("intro content")
                    delay(3000)
                    it.complete(Unit)
                }

                VideoIntro(
                    title = "Japanese kanji",
                    subtitle = "Part #2",
                    topic = "Street signs 2",
                    letters = letterDataList.map { it.letter }
                )

            }

            val writingContent = letterDataList.mapIndexed { letterIndex, letterData ->

                VideoContent {

                    val stokeAnimationStart = CompletableDeferred<Unit>()
                    val stokeAnimationFinish = CompletableDeferred<Unit>()

                    LaunchedEffect(Unit) {
                        println("kanji content")
                        delay(2000)
                        stokeAnimationStart.complete(Unit)
                        stokeAnimationFinish.await()
                        delay(2000)
                        it.complete(Unit)
                    }

                    KanjiView(
                        letterIndex = letterIndex,
                        data = letterData,
                        stokeAnimationStart = stokeAnimationStart,
                        stokeAnimationFinish = stokeAnimationFinish
                    )

                }

            }

            val outro = VideoContent {
                LaunchedEffect(Unit) {
                    println("outro content")
                    delay(6000)
                    it.complete(Unit)
                }
                VideoOutro(
                    letterDataList = letterDataList
                )
            }

            listOf(intro) + writingContent + outro
        }

        var currentContent by remember {
            mutableStateOf<Pair<Int, VideoContent>?>(null)
        }

        Box(
            modifier = Modifier
        ) {

            val showPermanentContent = remember { derivedStateOf { currentContent != null } }
            val permanentContentAlpha = animateFloatAsState(
                if (showPermanentContent.value) 1f else 0f
            )
            val footerColor = animateColorAsState(
                if (currentContent?.first == 0) MaterialTheme.colorScheme.primary
                else lightBgColor
            )

            Icon(
                painter = painterResource(Res.drawable.video_footer),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
                    .graphicsLayer { alpha = permanentContentAlpha.value },
                tint = footerColor.value
            )

            Column(
                modifier = Modifier
                    .graphicsLayer { alpha = permanentContentAlpha.value }
                    .fillMaxWidth()
                    .padding(top = 80.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {

                AppLogo(
                    Modifier.align(Alignment.CenterHorizontally)
                )

                HorizontalDivider(
                    Modifier.padding(horizontal = 80.dp)
                )

                Crossfade(
                    targetState = currentContent,
                    modifier = Modifier.weight(1f)
                ) {
                    it?.second?.run { content(completable) }
                }

            }

        }

        TestLaunchedEffect {

            it.startVideoCapture("v2")
            it.recordVideoFrame()

            contentList.forEachIndexed { i, data ->
                currentContent = i to data

                while (!data.completable.isCompleted) {
                    mainClock.advanceTimeBy(1000L / 60 * animationSpeedMultiplier)
                    it.recordVideoFrame()
                }
            }

            it.stopVideoCapture()

        }

    }

}

val lightBgColor = Color(0xffD9D9D9)
val onLightBgColor = Color(0xff919191)

@Composable
private fun AppLogo(modifier: Modifier) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = modifier
    ) {
        Icon(
            painter = painterResource(Res.drawable.kanji_dojo_icon),
            contentDescription = null,
            modifier = Modifier.size(36.dp),
            tint = Color.Unspecified
        )
        Text(
            text = "Kanji Dojo",
            fontSize = 24.sp,
            fontWeight = FontWeight.Thin
        )
    }
}

@Composable
private fun VideoIntro(
    title: String,
    subtitle: String,
    topic: String,
    letters: List<String>
) {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 60.dp)
            .padding(horizontal = 80.dp),
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {

        Text(
            text = title,
            fontSize = 36.sp,
            fontWeight = FontWeight.SemiBold
        )

        Text(
            text = subtitle,
            fontSize = 26.sp,
            fontWeight = FontWeight.ExtraLight,
            color = onLightBgColor
        )

        Spacer(Modifier.height(100.dp))

        Text(
            text = topic,
            fontSize = 50.sp,
            fontWeight = FontWeight.SemiBold
        )

        Text(
            text = letters.joinToString("、"),
            fontSize = 26.sp,
            fontWeight = FontWeight.ExtraLight,
            color = onLightBgColor
        )

    }

}

@Composable
private fun KanjiView(
    letterIndex: Int,
    data: VideoTests.VideoLetterData,
    stokeAnimationStart: CompletableDeferred<Unit>,
    stokeAnimationFinish: CompletableDeferred<Unit>
) {

    val letter: String = data.letter
    val meanings: List<String> = data.meanings
    val strokes: List<Path> = data.strokes
    val examples: List<JapaneseWord> = data.examples

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {

        Text(
            text = letter,
            fontWeight = FontWeight.SemiBold,
            fontSize = 84.sp
        )

        FlowRow(
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(horizontal = 80.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            maxLines = 1
        ) {
            meanings.forEach {
                Text(
                    text = it,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.ExtraLight,
                    textAlign = TextAlign.End,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.alignByBaseline()
                )
            }
        }

        val exampleReadingStyle = LocalTextStyle.current.copy(
            fontWeight = FontWeight.SemiBold,
            fontSize = 24.sp
        )

        val exampleMeaningStyle = exampleReadingStyle.copy(
            fontWeight = FontWeight.ExtraLight,
            textAlign = TextAlign.End
        )

        FlowColumn(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 80.dp)
        ) {

            examples.forEach {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val reading = it.reading.furigana ?: formattedVocabReading(
                        it.reading.kanaReading,
                        it.reading.kanjiReading
                    )
                    FuriganaText(
                        furiganaString = reading,
                        modifier = Modifier
                            .weight(1f)
                            .alignByBaseline(),
                        textStyle = exampleReadingStyle
                    )
                    Text(
                        text = it.glossary.first(),
                        modifier = Modifier
                            .weight(1f)
                            .alignByBaseline(),
                        style = exampleMeaningStyle
                    )
                }
            }
        }

        Box(
            modifier = Modifier.fillMaxWidth()
        ) {


            val image = remember { mutableStateOf<Painter?>(data.image) }

            LaunchedEffect(Unit) {
                if (image.value != null) {
                    delay(500)
                    image.value = null
                }
            }

            CharacterWriterDecorations(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 230.dp)
                    .requiredSize(300.dp),
                state = rememberUpdatedState(null)
            ) {

                AutoAnimatedLetter(
                    strokes = strokes,
                    modifier = Modifier.fillMaxSize(),
                    animationDelay = stokeAnimationStart,
                    onAnimationCompleted = { stokeAnimationFinish.complete(Unit) }
                )

            }

            Text(
                text = letterIndex.plus(1).toString(),
                color = onLightBgColor,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(bottom = 200.dp)
                    .padding(start = 35.dp)
                    .border(3.dp, onLightBgColor, CircleShape)
                    .padding(8.dp)
                    .size(26.dp)
                    .wrapContentSize()
            )

        }

    }

}

@Composable
fun AutoAnimatedLetter(
    strokes: List<Path>,
    modifier: Modifier,
    animationDelay: CompletableDeferred<Unit>,
    onAnimationCompleted: () -> Unit,
    strokeColor: Color = defaultStrokeColor(),
    strokeWidth: Float = StrokeWidth
) {

    val strokesToDraw = remember { mutableStateOf(emptyList<Path>()) }
    val lastStrokeAnimationProgress = remember { Animatable(1f) }

    LaunchedEffect(strokes) {
        animationDelay.await()
        for (strokesCount in 1..strokes.size) {
            val paths = strokes.subList(0, strokesCount)
            strokesToDraw.value = paths

            lastStrokeAnimationProgress.snapTo(0f)
            lastStrokeAnimationProgress.animateTo(1f, tween(600))
        }

        onAnimationCompleted()
    }

    Box(
        modifier = modifier
    ) {

        Canvas(
            modifier = Modifier.fillMaxSize()
        ) {

            clipRect {

                val strokesList = strokesToDraw.value

                strokesList.dropLast(1).forEach {
                    drawKanjiStroke(
                        path = it,
                        color = strokeColor,
                        width = strokeWidth
                    )
                }

                strokesList.lastOrNull()?.also {
                    drawKanjiStroke(
                        path = it,
                        color = strokeColor,
                        width = strokeWidth,
                        drawProgress = lastStrokeAnimationProgress.value
                    )
                }

            }
        }
    }

}

@Composable
fun VideoOutro(letterDataList: List<VideoTests.VideoLetterData>) {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 80.dp)
    ) {

        FlowColumn(
            modifier = Modifier
                .weight(1f)
                .wrapContentHeight()
        ) {

            val exampleReadingStyle = LocalTextStyle.current.copy(
                fontWeight = FontWeight.SemiBold,
                fontSize = 24.sp
            )

            val exampleMeaningStyle = exampleReadingStyle.copy(
                fontWeight = FontWeight.ExtraLight,
                textAlign = TextAlign.End
            )

            letterDataList.forEachIndexed { i, it ->

                Row(
                    horizontalArrangement = Arrangement.spacedBy(20.dp)
                ) {

                    Text(
                        text = it.letter,
                        style = exampleReadingStyle,
                        fontSize = 34.sp,
                        modifier = Modifier.alignByBaseline(),
                    )

                    val word = it.examples.first()
                    val reading = word.reading.furigana
                        ?: formattedVocabReading(
                            word.reading.kanaReading,
                            word.reading.kanjiReading
                        )
                    FuriganaText(
                        furiganaString = reading,
                        modifier = Modifier.alignByBaseline(),
                        textStyle = exampleReadingStyle
                    )
                    Text(
                        text = word.glossary.first(),
                        modifier = Modifier
                            .weight(1f)
                            .alignByBaseline(),
                        style = exampleMeaningStyle
                    )
                }
            }

        }

        Text(
            text = "Learn how to write these and many other kanji",
            fontSize = 36.sp,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        Text(
            text = "Open-source application, available on multiple platforms",
            fontSize = 26.sp,
            fontWeight = FontWeight.ExtraLight,
            color = onLightBgColor
        )

        Spacer(Modifier.height(60.dp))

        Icon(
            painter = painterResource(Res.drawable.video_platforms),
            contentDescription = null,
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .height(60.dp),
            tint = Color.Unspecified
        )

        Spacer(Modifier.height(300.dp))

    }
}