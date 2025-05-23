@file:OptIn(ExperimentalTestApi::class)

import androidx.compose.ui.InternalComposeUiApi
import androidx.compose.ui.graphics.asSkiaBitmap
import androidx.compose.ui.graphics.toAwtImage
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.SkikoComposeUiTest
import androidx.compose.ui.test.captureToImage
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.unit.IntSize
import org.bytedeco.ffmpeg.global.avutil
import org.bytedeco.javacv.FFmpegFrameRecorder
import org.bytedeco.javacv.Java2DFrameConverter
import org.jetbrains.skia.impl.BufferUtil
import org.jetbrains.skiko.toBitmap
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
        val image = uiTest.onRoot()
            .captureToImage()
            .toAwtImage()

        val bitmap = image.toBitmap()

        val pixels = BufferUtil.getByteBufferFromPointer(
            ptr = bitmap.peekPixels()!!.addr,
            size = bitmap.rowBytes * bitmap.height
        )

        recorder.recordImage(
            image.width,
            image.height,
            8,
            4,
            image.width * 4,
            avutil.AV_PIX_FMT_BGRA,
            pixels
        )
    }

    override fun stopVideoCapture() {
        recorder.stop()
    }

    private fun getCurrentImage(): BufferedImage = uiTest.onRoot()
        .captureToImage()
        .asSkiaBitmap()
        .toBufferedImage()

}