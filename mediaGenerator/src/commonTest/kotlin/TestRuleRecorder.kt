@file:OptIn(ExperimentalTestApi::class)

import androidx.compose.ui.InternalComposeUiApi
import androidx.compose.ui.graphics.asSkiaBitmap
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.SkikoComposeUiTest
import androidx.compose.ui.test.captureToImage
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.unit.IntSize
import org.bytedeco.javacv.FFmpegFrameRecorder
import org.bytedeco.javacv.Java2DFrameConverter
import org.jetbrains.skiko.toBufferedImage
import ua.syt0r.kanji.mediaGenerator.RecordingConfiguration
import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO


interface TestRuleRecorder {
    fun screenshot(name: String)
    fun startVideoCapture(name: String)
    fun recordVideoFrame()
    fun stopVideoCapture()
}

val DefaultSize = IntSize(1920, 1080)

class JavaCvTestRuleRecorder(
    private val uiTest: SkikoComposeUiTest,
    private val configuration: RecordingConfiguration = RecordingConfiguration.Default,
    private val outputDir: File = File("out")
) : TestRuleRecorder {

    private lateinit var recorder: FFmpegFrameRecorder
    private val converter = Java2DFrameConverter()

    @OptIn(InternalComposeUiApi::class)
    private val size: IntSize = IntSize(
        width = uiTest.scene.size!!.width,
        height = uiTest.scene.size!!.height
    )

    override fun screenshot(name: String) {
        ImageIO.write(getCurrentImage(), "png", File(outputDir, "$name.png"))
    }

    @OptIn(InternalComposeUiApi::class)
    override fun startVideoCapture(name: String) {
        recorder = configuration.buildRecorder(
            file = File(outputDir, "$name.mp4"),
            width = size.width,
            height = size.height
        )
        recorder.start()
    }

    override fun recordVideoFrame() {
        val image = getCurrentImage()
        val clone = BufferedImage(image.width, image.height, BufferedImage.TYPE_3BYTE_BGR)
        val graphics = clone.graphics
        graphics.drawImage(image, 0, 0, null)
        graphics.dispose()

        val frame = converter.convert(clone)

        recorder.record(frame)
    }

    override fun stopVideoCapture() {
        recorder.stop()
    }

    private fun getCurrentImage(): BufferedImage = uiTest.onRoot()
        .captureToImage()
        .asSkiaBitmap()
        .toBufferedImage()

}