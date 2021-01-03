import org.junit.Assert.assertEquals
import org.junit.Test
import ua.syt0r.svg_parser.SvgCommand
import ua.syt0r.svg_parser.SvgCommandParser

class ParserTest {

    @Test
    fun `when there are several commands joined under one command letter then should return new command for each`() {
        val path =
            "M  13.43,19.93 c 0.32,1.06 0.41,2.55 0.65,3.97 0.24,1.42 -0.44,58.808586 -0.44,64.308586"
        val expectedMoveCommands = 1
        val expectedCurveCommands = 2

        val parsedCommands = SvgCommandParser.parse(path)

        val moveCommands = parsedCommands.filterIsInstance<SvgCommand.MoveTo>().size
        val curveCommands = parsedCommands.filterIsInstance<SvgCommand.CubicBezierCurve>().size

        assertEquals(expectedMoveCommands, moveCommands)
        assertEquals(expectedCurveCommands, curveCommands)
    }

}