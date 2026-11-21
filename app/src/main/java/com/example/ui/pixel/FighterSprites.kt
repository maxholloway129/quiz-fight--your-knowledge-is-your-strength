package com.example.ui.pixel

import androidx.compose.ui.graphics.ImageBitmap
import com.example.model.FighterPose

/**
 * Original 16-bit retro pixel-art fighter sprites (28x36 grid per frame).
 *
 * Player 1 (Student Fighter, left side, facing RIGHT):
 * - Lean, slim, athletic build
 * - White sleeveless vest
 * - Dark navy-blue baggy pants
 * - White sneakers
 *
 * Player 2 (Opponent, right side, facing LEFT):
 * - Green baggy jacket
 * - Green cap
 * - White pants
 * - Brown shoes
 */
object FighterSprites {

    private val p1Palette = mapOf(
        '.' to 0x00000000,
        'O' to 0xFF120C1E.toInt(), // crisp dark pixel outline
        'H' to 0xFF231B36.toInt(), // spiky dark hair
        'h' to 0xFF3E315E.toInt(), // hair highlight
        'R' to 0xFFFF3B5C.toInt(), // headband accent
        'r' to 0xFFB51738.toInt(), // headband shadow
        'S' to 0xFFF5BC8C.toInt(), // skin tone
        's' to 0xFFD48D5C.toInt(), // skin shadow
        'E' to 0xFF140E21.toInt(), // eyes
        'W' to 0xFFF7F9FF.toInt(), // white sleeveless vest / white sneakers
        'w' to 0xFFC8D3EB.toInt(), // vest / sneaker shading
        'N' to 0xFF1D326B.toInt(), // dark navy-blue baggy pants
        'n' to 0xFF101D45.toInt(), // navy pants shadow
        'b' to 0xFF304E9E.toInt(), // navy pants highlight
        'K' to 0xFFFFD147.toInt(), // golden energy / belt / wrap accent
        'g' to 0xFF7B87A6.toInt()  // sneaker sole
    )

    private val p2Palette = mapOf(
        '.' to 0x00000000,
        'O' to 0xFF120C1E.toInt(), // crisp dark pixel outline
        'G' to 0xFF29C76F.toInt(), // green cap & baggy jacket
        'g' to 0xFF178A4A.toInt(), // green shadow
        'v' to 0xFF0E5C31.toInt(), // cap visor / deep green
        'H' to 0xFF261E30.toInt(), // hair under cap
        'S' to 0xFFE6AC7E.toInt(), // skin
        's' to 0xFFBF7E52.toInt(), // skin shadow
        'E' to 0xFF140E21.toInt(), // eyes
        'K' to 0xFF1F2333.toInt(), // dark tee inside jacket
        'W' to 0xFFF7F4EB.toInt(), // white pants
        'w' to 0xFFC7C1B2.toInt(), // white pants shadow
        'B' to 0xFF8C4E27.toInt(), // brown shoes
        'b' to 0xFF542C12.toInt(), // brown shoe sole/shadow
        'Y' to 0xFFFFB830.toInt()  // jacket zipper / gold detail
    )

    // --- PLAYER 1 FRAMES (28 cols x 36 rows, facing RIGHT) ---
    private val p1IdleA = listOf(
        "............................",
        ".........OOOOO..............",
        "........OHhhhHO.............",
        ".......OHHhhhHHO............",
        ".......ORRRRRRROO...........",
        "......OrSSSSSSSO............",
        ".......OSSSSESSOO...........",
        ".......OSSSSSSSO............",
        "........OSSSSSO.............",
        "........OOsSsOO.............",
        ".......OWWWWWWO.............",
        "......OWWWWWWWWO............",
        ".....OsOWWWWWWOOsO..........",
        ".....OsOWWWWWWOSSSO.........",
        "......sOWWWWWWOSSKO.........",
        ".......OWWWWWWO.OO..........",
        ".......OWwwwwWO.............",
        ".......ONNNNNNO.............",
        "......ONbNNNNbNO............",
        "......ONNNNNNNNO............",
        ".....ONNNNnONNNNO...........",
        ".....ONNNO..ONNNO...........",
        ".....ONNNO..ONNNO...........",
        ".....ONnNO..ONnNO...........",
        ".....ONnNO..ONnNO...........",
        "......OOOO...OOOO...........",
        "......OWWO...OWWO...........",
        ".....OWWWWO.OWWWWO..........",
        ".....OggggO.OggggO..........",
        "......OOOO...OOOO..........."
    )

    private val p1IdleB = listOf(
        "............................",
        "............................",
        ".........OOOOO..............",
        "........OHhhhHO.............",
        ".......OHHhhhHHO............",
        ".......ORRRRRRROO...........",
        "......OrSSSSSSSO............",
        ".......OSSSSESSOO...........",
        ".......OSSSSSSSO............",
        "........OSSSSSO.............",
        ".......OWWWWWWO.............",
        "......OWWWWWWWWO............",
        ".....OsOWWWWWWOOsO..........",
        ".....OsOWWWWWWOSSSO.........",
        "......sOWWWWWWOSSKO.........",
        ".......OWWWWWWO.OO..........",
        ".......OWwwwwWO.............",
        ".......ONNNNNNO.............",
        "......ONbNNNNbNO............",
        "......ONNNNNNNNO............",
        ".....ONNNNnONNNNO...........",
        ".....ONNNO..ONNNO...........",
        ".....ONNNO..ONNNO...........",
        ".....ONnNO..ONnNO...........",
        ".....ONnNO..ONnNO...........",
        "......OOOO...OOOO...........",
        "......OWWO...OWWO...........",
        ".....OWWWWO.OWWWWO..........",
        ".....OggggO.OggggO..........",
        "......OOOO...OOOO..........."
    )

    private val p1Windup = listOf(
        "............................",
        ".......OOOOO................",
        "......OHhhhHO...............",
        ".....OHHhhhHHO..............",
        ".....ORRRRRRROO.............",
        "....OrSSSSSSSO..............",
        ".....OSSSSESSOO.............",
        ".....OSSSSSSSO..............",
        "......OSSSSSO...............",
        ".....OOWWWWWO...............",
        "....OSSOWWWWWO..............",
        "...OSSKOWWWWWWO.............",
        "....OO.OWWWWWWO.............",
        ".......OWwwwwWO.............",
        "......ONNNNNNNO.............",
        ".....ONbNNNNbNNO............",
        "....ONNNNOONNNNO............",
        "....ONNNO..ONNNO............",
        "....ONNNO...ONNNO...........",
        "....ONnNO...ONnNO...........",
        ".....OOOO....OOOO...........",
        ".....OWWO....OWWO...........",
        "....OWWWWO..OWWWWO..........",
        "....OggggO..OggggO..........",
        ".....OOOO....OOOO..........."
    )

    private val p1AttackStrike = listOf(
        "............................",
        "...........OOOOO............",
        "..........OHhhhHO...........",
        ".........OHHhhhHHO..........",
        ".........ORRRRRRROO.........",
        "........OrSSSSSSSO..........",
        ".........OSSSSESSOO.........",
        ".........OSSSSSSSO..........",
        "..........OSSSSSO...........",
        ".........OWWWWWWWOOOOOOOO...",
        "........OWWWWWWWWSSSSSSSSO..",
        "........OWWWWWWWWOSSKKKKKO..",
        "........OWWWWWWWOOOOOOOOO...",
        ".........OWwwwwWO...........",
        "........ONNNNNNNNO..........",
        ".......ONbNNNNNbNNO.........",
        "......ONNNNO..ONNNNO........",
        ".....ONNNO.....ONNNO........",
        "....ONNNO.......ONNNO.......",
        "....ONnNO.......ONnNO.......",
        "....OOOO.........OOOO.......",
        "....OWWO.........OWWO.......",
        "...OWWWWO.......OWWWWO......",
        "...OggggO.......OggggO......",
        "....OOOO.........OOOO......."
    )

    private val p1AttackMiss = listOf(
        "............................",
        ".............OOOOO..........",
        "............OHhhhHO.........",
        "...........OHHhhhHHO........",
        "...........ORRRRRRROO.......",
        "..........OrSSSSSSSO........",
        "...........OSSSSESSOO.......",
        "...........OSSSSSSSO........",
        "............OSSSSSO.........",
        "..........OWWWWWWWOOOOO.....",
        ".........OWWWWWWWWSSSSSO....",
        "........sOWWWWWWWWOSSSO.....",
        "........sOWwwwwWO.OOOO......",
        ".........ONNNNNNNO..........",
        "........ONbNNNNbNO..........",
        ".......ONNNNOONNNNO.........",
        "......ONNNO...ONNNO.........",
        ".....ONnNO.....ONnNO........",
        ".....OOOO.......OOOO........",
        ".....OWWO.......OWWO........",
        "....OWWWWO.....OWWWWO.......",
        "....OggggO.....OggggO.......",
        ".....OOOO.......OOOO........"
    )

    private val p1Hit = listOf(
        "............................",
        ".....OOOOO..................",
        "....OHhhhHO.................",
        "...OHHhhhHHO................",
        "...ORRRRRRROO...............",
        "...OSSSSSSSO................",
        "....OSSSSsSOO...............",
        "....OSSSSSSO................",
        ".....OSSSSO.................",
        "...OOOWWWWWWO...............",
        "..OSSOWWWWWWWWO.............",
        "...OO.OWWWWWWOOsO...........",
        "......OWwwwwWOSSSO..........",
        "......ONNNNNNO.OO...........",
        ".....ONbNNNNbNO.............",
        "....ONNNNOONNNNO............",
        "....ONNNO..ONNNO............",
        "...ONNNO....ONNNO...........",
        "...ONnNO....ONnNO...........",
        "....OOOO.....OOOO...........",
        "....OWWO.....OWWO...........",
        "...OWWWWO...OWWWWO..........",
        "...OggggO...OggggO..........",
        "....OOOO.....OOOO..........."
    )

    private val p1ComboPose = listOf(
        "...........OOO..............",
        "..........OKKKO.............",
        ".........OSSSKO.............",
        ".........OSSSSO.............",
        ".........OOOOO..............",
        "........OHhhhHO.............",
        ".......OHHhhhHHO............",
        ".......ORRRRRRROO...........",
        "......OrSSSSSSSO............",
        ".......OSSSSESSOO...........",
        ".......OSSSSSSSO............",
        "........OWWWWWWO............",
        ".......OWWWWWWWWO...........",
        "......OsOWWWWWWOOsO.........",
        "......OsOWWWWWWOSSKO........",
        ".......sOWwwwwWO.OO.........",
        "........ONNNNNNO............",
        ".......ONbNNNNbNO...........",
        "......ONNNNOONNNNO..........",
        "......ONNNO..ONNNO..........",
        ".....ONNNO....ONNNO.........",
        ".....ONnNO....ONnNO.........",
        "......OOOO.....OOOO.........",
        "......OWWO.....OWWO.........",
        ".....OWWWWO...OWWWWO........",
        ".....OggggO...OggggO........",
        "......OOOO.....OOOO........."
    )

    private val p1Victory = listOf(
        "....................OOO.....",
        "...................OKKKO....",
        ".........OOOOO....OSSSKO....",
        "........OHhhhHO...OSSSSO....",
        ".......OHHhhhHHO..OOOOO.....",
        ".......ORRRRRRROO.OSOO......",
        "......OrSSSSSSSO.OSSO.......",
        ".......OSSSSESSOOSSO........",
        ".......OSSSSSSSOSSO.........",
        "........OSSSSSOSSO..........",
        ".......OWWWWWWWWO...........",
        "......OWWWWWWWWO............",
        ".....OsOWWWWWWO.............",
        ".....OsOWwwwwWO.............",
        "......OOONNNNNNO............",
        ".......ONbNNNNbNO...........",
        "......ONNNNOONNNNO..........",
        "......ONNNO..ONNNO..........",
        "......ONNNO..ONNNO..........",
        "......ONnNO..ONnNO..........",
        ".......OOOO...OOOO..........",
        ".......OWWO...OWWO..........",
        "......OWWWWO.OWWWWO.........",
        "......OggggO.OggggO.........",
        ".......OOOO...OOOO.........."
    )

    private val p1Defeat = listOf(
        "............................",
        "............................",
        "............................",
        "............................",
        "............................",
        "............................",
        "............................",
        "............................",
        "............................",
        "............................",
        "............................",
        "............................",
        "........OOOOO...............",
        ".......OHhhhHO..............",
        "......OHHhhhHHO.............",
        "......ORRRRRRRO.............",
        ".....OrSSSSSSSO.............",
        "......OSSSSsSSO.............",
        ".......OSSSSSO..............",
        "......OWWWWWWO..............",
        ".....OWWWWWWWWO.............",
        "....OsOWWWWWWOOsO...........",
        "....OsOWwwwwWOssO...........",
        ".....ONNNNNNNNNO............",
        "....ONbNNNNNNbNNO...........",
        "....ONNNNOOOONNNNO..........",
        "....OWWWWO..OWWWWO..........",
        "....OggggO..OggggO..........",
        ".....OOOO....OOOO..........."
    )

    // --- PLAYER 2 FRAMES (28 cols x 36 rows, drawn facing RIGHT then flipped horizontally to face LEFT) ---
    private val p2IdleABase = listOf(
        "............................",
        "........OOOOOOO.............",
        ".......OGGGGGGGGO...........",
        "......OGGGGGGGGGGO..........",
        ".....OvvvvvvGGGGGO..........",
        ".......OHHSSSSSSSO..........",
        ".......OSSSSESSOO...........",
        ".......OSSSSSSSO............",
        "........OSSSSSO.............",
        "......OGGGGGGGGGGO..........",
        ".....OGGGGYKKYGGGGO.........",
        "....OGGGGGYKKYGGGGGO........",
        "....OGggGGYKKYGGSSSO........",
        ".....OOOGgGKKGgGOOO.........",
        ".......OGgGGGGgGO...........",
        ".......OWWWWWWWWO...........",
        "......OWwWWWWWWwWO..........",
        "......OWWWWWWWWWWO..........",
        ".....OWWWWO..OWWWWO.........",
        ".....OWWWO....OWWWO.........",
        ".....OWWWO....OWWWO.........",
        ".....OWwWO....OWwWO.........",
        ".....OWwWO....OWwWO.........",
        "......OOOO.....OOOO.........",
        "......OBBO.....OBBO.........",
        ".....OBBBBO...OBBBBO........",
        ".....ObbbbO...ObbbbO........",
        "......OOOO.....OOOO........."
    )

    private val p2IdleBBase = listOf(
        "............................",
        "............................",
        "........OOOOOOO.............",
        ".......OGGGGGGGGO...........",
        "......OGGGGGGGGGGO..........",
        ".....OvvvvvvGGGGGO..........",
        ".......OHHSSSSSSSO..........",
        ".......OSSSSESSOO...........",
        ".......OSSSSSSSO............",
        "........OSSSSSO.............",
        "......OGGGGGGGGGGO..........",
        ".....OGGGGYKKYGGGGO.........",
        "....OGGGGGYKKYGGGGGO........",
        "....OGggGGYKKYGGSSSO........",
        ".....OOOGgGKKGgGOOO.........",
        ".......OGgGGGGgGO...........",
        ".......OWWWWWWWWO...........",
        "......OWwWWWWWWwWO..........",
        "......OWWWWWWWWWWO..........",
        ".....OWWWWO..OWWWWO.........",
        ".....OWWWO....OWWWO.........",
        ".....OWWWO....OWWWO.........",
        ".....OWwWO....OWwWO.........",
        "......OOOO.....OOOO.........",
        "......OBBO.....OBBO.........",
        ".....OBBBBO...OBBBBO........",
        ".....ObbbbO...ObbbbO........",
        "......OOOO.....OOOO........."
    )

    private val p2AttackStrikeBase = listOf(
        "............................",
        "..........OOOOOOO...........",
        ".........OGGGGGGGGO.........",
        "........OGGGGGGGGGGO........",
        ".......OvvvvvvGGGGGO........",
        ".........OHHSSSSSSSO........",
        ".........OSSSSESSOO.........",
        ".........OSSSSSSSO..........",
        "..........OSSSSSO...........",
        "........OGGGGGGGGGGOOOOOO...",
        ".......OGGGGYKKYGGGGSSSSSO..",
        ".......OGGGGYKKYGGGGSSSSSO..",
        ".......OGggGYKKYGGGOOOOOO...",
        "........OGgGGGGgGO..........",
        "........OWWWWWWWWO..........",
        ".......OWwWWWWWWwWO.........",
        "......OWWWWO..OWWWWO........",
        ".....OWWWO.....OWWWO........",
        "....OWWWO.......OWWWO.......",
        "....OWwWO.......OWwWO.......",
        "....OOOO.........OOOO.......",
        "....OBBO.........OBBO.......",
        "...OBBBBO.......OBBBBO......",
        "...ObbbbO.......ObbbbO......",
        "....OOOO.........OOOO......."
    )

    private val p2DodgeBase = listOf(
        "............................",
        "............................",
        "............................",
        "........OOOOOOO.............",
        ".......OGGGGGGGGO...........",
        "......OGGGGGGGGGGO..........",
        ".....OvvvvvvGGGGGO..........",
        ".......OHHSSSSSSSO..........",
        ".......OSSSSESSOO...........",
        ".......OSSSSSSSO............",
        "......OGGGGGGGGGGO..........",
        ".....OGGGGYKKYGGGGO.........",
        ".....OGggGYKKYGGgGO.........",
        "......OGgGGGGGGgGO..........",
        "......OWWWWWWWWWWO..........",
        ".....OWwWWWWWWWWwWO.........",
        "....OWWWWO....OWWWWO........",
        "....OWWWO......OWWWO........",
        "....OWwWO......OWwWO........",
        ".....OOOO.......OOOO........",
        ".....OBBO.......OBBO........",
        "....OBBBBO.....OBBBBO.......",
        "....ObbbbO.....ObbbbO.......",
        ".....OOOO.......OOOO........"
    )

    private val p2HitBase = listOf(
        "............................",
        ".....OOOOOOO................",
        "....OGGGGGGGGO..............",
        "...OGGGGGGGGGGO.............",
        "..OvvvvvvGGGGGO.............",
        "....OHHSSSSSSSO.............",
        "....OSSSSsSSOO..............",
        "....OSSSSSSSO...............",
        ".....OSSSSSO................",
        "..OOOGGGGGGGGGGO............",
        ".OSSOGGGGYKKYGGGGO..........",
        "..OO.OGggGYKKYGGgGO.........",
        "......OGgGGGGGGgGO..........",
        "......OWWWWWWWWWWO..........",
        ".....OWwWWWWWWWWwWO.........",
        "....OWWWWO..OWWWWO..........",
        "...OWWWO......OWWWO.........",
        "...OWwWO......OWwWO.........",
        "....OOOO.......OOOO.........",
        "....OBBO.......OBBO.........",
        "...OBBBBO.....OBBBBO........",
        "...ObbbbO.....ObbbbO........",
        "....OOOO.......OOOO........."
    )

    private val p2VictoryBase = listOf(
        "..................OOO.......",
        ".................OSSSO......",
        "........OOOOOOO..OSSO.......",
        ".......OGGGGGGGGOSSO........",
        "......OGGGGGGGGGGOO.........",
        ".....OvvvvvvGGGGGO..........",
        ".......OHHSSSSSSSO..........",
        ".......OSSSSESSOO...........",
        ".......OSSSSSSSO............",
        "........OSSSSSO.............",
        "......OGGGGGGGGGGO..........",
        ".....OGGGGYKKYGGGGO.........",
        "....OGGGGGYKKYGGGGGO........",
        "....OGggGGYKKYGGgGO.........",
        ".....OOOGgGGGGgGO...........",
        ".......OWWWWWWWWO...........",
        "......OWwWWWWWWwWO..........",
        ".....OWWWWO..OWWWWO.........",
        ".....OWWWO....OWWWO.........",
        ".....OWwWO....OWwWO.........",
        "......OOOO.....OOOO.........",
        "......OBBO.....OBBO.........",
        ".....OBBBBO...OBBBBO........",
        ".....ObbbbO...ObbbbO........",
        "......OOOO.....OOOO........."
    )

    private val p2DefeatBase = listOf(
        "............................",
        "............................",
        "............................",
        "............................",
        "............................",
        "............................",
        "............................",
        "............................",
        "............................",
        "............................",
        ".......OOOOOOO..............",
        "......OGGGGGGGGO............",
        ".....OGGGGGGGGGGO...........",
        "....OvvvvvvGGGGGO...........",
        "......OHHSSSSSSSO...........",
        "......OSSSSsSSOO............",
        ".......OSSSSSSO.............",
        ".....OGGGGGGGGGGO...........",
        "....OGGGGYKKYGGGGO..........",
        "....OGggGGGGGGggGO..........",
        "....OWWWWWWWWWWWWO..........",
        "...OWwWWWWWWWWWWwWO.........",
        "...OWWWWO....OWWWWO.........",
        "...OBBBBO....OBBBBO.........",
        "...ObbbbO....ObbbbO.........",
        "....OOOO......OOOO.........."
    )

    private val p1Cache = mutableMapOf<Pair<FighterPose, Int>, ImageBitmap>()
    private val p2Cache = mutableMapOf<Pair<FighterPose, Int>, ImageBitmap>()

    fun getPlayer1Sprite(pose: FighterPose, frameIndex: Int): ImageBitmap {
        val normalizedFrame = frameIndex % 2
        return p1Cache.getOrPut(pose to normalizedFrame) {
            val grid = when (pose) {
                FighterPose.IDLE -> if (normalizedFrame == 0) p1IdleA else p1IdleB
                FighterPose.ATTACK_WINDUP -> p1Windup
                FighterPose.ATTACK_STRIKE -> p1AttackStrike
                FighterPose.ATTACK_MISS -> p1AttackMiss
                FighterPose.DODGE -> p1Windup
                FighterPose.FAILED_RECOVERY -> p1AttackMiss
                FighterPose.HIT_REACTION -> p1Hit
                FighterPose.COMBO_POSE -> p1ComboPose
                FighterPose.VICTORY -> if (normalizedFrame == 0) p1Victory else p1ComboPose
                FighterPose.DEFEAT -> p1Defeat
            }
            PixelArtEngine.createSpriteBitmap(padGrid(grid), p1Palette, flipHorizontal = false)
        }
    }

    fun getPlayer2Sprite(pose: FighterPose, frameIndex: Int): ImageBitmap {
        val normalizedFrame = frameIndex % 2
        return p2Cache.getOrPut(pose to normalizedFrame) {
            val grid = when (pose) {
                FighterPose.IDLE -> if (normalizedFrame == 0) p2IdleABase else p2IdleBBase
                FighterPose.ATTACK_WINDUP -> p2DodgeBase
                FighterPose.ATTACK_STRIKE -> p2AttackStrikeBase
                FighterPose.ATTACK_MISS -> p2DodgeBase
                FighterPose.DODGE -> p2DodgeBase
                FighterPose.FAILED_RECOVERY -> p2DodgeBase
                FighterPose.HIT_REACTION -> p2HitBase
                FighterPose.COMBO_POSE -> p2HitBase
                FighterPose.VICTORY -> if (normalizedFrame == 0) p2VictoryBase else p2IdleABase
                FighterPose.DEFEAT -> p2DefeatBase
            }
            PixelArtEngine.createSpriteBitmap(padGrid(grid), p2Palette, flipHorizontal = true)
        }
    }

    private fun padGrid(rows: List<String>): List<String> {
        val targetRows = 32
        val targetCols = 28
        val padded = MutableList(targetRows) { ".".repeat(targetCols) }
        val offsetY = (targetRows - rows.size).coerceAtLeast(0)
        for (i in rows.indices) {
            val destY = offsetY + i
            if (destY in 0 until targetRows) {
                val r = rows[i]
                padded[destY] = if (r.length >= targetCols) r.substring(0, targetCols) else r.padEnd(targetCols, '.')
            }
        }
        return padded
    }
}
