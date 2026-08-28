package com.zurdus.nqueens.feature.game.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfoV2
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.window.core.layout.WindowSizeClass.Companion.HEIGHT_DP_MEDIUM_LOWER_BOUND
import androidx.window.core.layout.WindowSizeClass.Companion.WIDTH_DP_MEDIUM_LOWER_BOUND
import com.zurdus.nqueens.R
import com.zurdus.nqueens.ui.motion.AnimatedEntrance
import com.zurdus.nqueens.ui.motion.AnimatedHeartbeat
import com.zurdus.nqueens.ui.motion.AnimatedSparks
import com.zurdus.nqueens.ui.motion.NQueensMotion
import com.zurdus.nqueens.ui.preview.NQueensPreview
import com.zurdus.nqueens.ui.preview.NQueensPreviews

@Composable
internal fun GameVictoryDialog(
    showDialog: Boolean,
    boardSize: Int,
    onClose: () -> Unit,
    onPlayAgain: () -> Unit,
    onChooseBoardSize: () -> Unit,
) {
    if (!showDialog) return

    Dialog(
        onDismissRequest = onClose,
        properties = DialogProperties(
            usePlatformDefaultWidth = false,
        ),
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .windowInsetsPadding(WindowInsets.safeDrawing)
                .padding(24.dp),
            contentAlignment = Alignment.Center,
        ) {
            GameVictoryDialogContent(
                boardSize = boardSize,
                onClose = onClose,
                onPlayAgain = onPlayAgain,
                onChooseBoardSize = onChooseBoardSize,
                modifier = Modifier
                    .widthIn(max = MAX_VICTORY_DIALOG_WIDTH)
                    .fillMaxWidth(),
            )
        }
    }
}

@Composable
private fun GameVictoryDialogContent(
    boardSize: Int,
    onClose: () -> Unit,
    onPlayAgain: () -> Unit,
    onChooseBoardSize: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val windowSizeClass = currentWindowAdaptiveInfoV2().windowSizeClass

    val useCompactHeightLayout =
        !windowSizeClass.isHeightAtLeastBreakpoint(HEIGHT_DP_MEDIUM_LOWER_BOUND)
    val useHorizontalActions =
        windowSizeClass.isWidthAtLeastBreakpoint(WIDTH_DP_MEDIUM_LOWER_BOUND)

    Surface(
        modifier = modifier
            .then(
                if (useCompactHeightLayout) Modifier.fillMaxHeight() else Modifier,
            )
            .testTag(GAME_VICTORY_DIALOG_TEST_TAG),
        color = MaterialTheme.colorScheme.surfaceContainerHigh,
        shape = RoundedCornerShape(24.dp),
        tonalElevation = 6.dp,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .then(
                    if (useCompactHeightLayout) Modifier.fillMaxHeight() else Modifier,
                )
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            GameVictoryCloseButton(
                onClick = onClose,
                modifier = Modifier.align(Alignment.End),
            )

            if (useCompactHeightLayout) {
                GameVictoryCompactHeightContent(
                    boardSize = boardSize,
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                )
            } else {
                GameVictoryHero(modifier = Modifier.size(112.dp))

                Spacer(modifier = Modifier.height(24.dp))

                GameVictoryMessage(boardSize = boardSize)
            }

            Spacer(modifier = Modifier.height(24.dp))

            GameVictoryActions(
                onPlayAgain = onPlayAgain,
                onChooseBoardSize = onChooseBoardSize,
                useHorizontalLayout = useHorizontalActions,
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}

@Composable
private fun GameVictoryCloseButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    IconButton(
        onClick = onClick,
        modifier = modifier.testTag(GAME_VICTORY_CLOSE_TEST_TAG),
    ) {
        Icon(
            imageVector = Icons.Default.Close,
            contentDescription = stringResource(R.string.game_view_solution),
        )
    }
}

@Composable
private fun GameVictoryCompactHeightContent(
    boardSize: Int,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .clipToBounds(),
            contentAlignment = Alignment.Center,
        ) {
            GameVictoryHero(
                modifier = Modifier
                    .sizeIn(maxWidth = 112.dp, maxHeight = 112.dp)
                    .fillMaxHeight()
                    .aspectRatio(1f),
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        GameVictoryMessage(boardSize = boardSize)
    }
}

@Composable
private fun GameVictoryHero(
    modifier: Modifier = Modifier,
) {
    AnimatedEntrance(
        enter = NQueensMotion.celebrationEnter,
        modifier = modifier,
    ) {
        BoxWithConstraints(
            modifier = Modifier
                .fillMaxSize()
                .clearAndSetSemantics {
                    testTag = GAME_VICTORY_HERO_TEST_TAG
                },
            contentAlignment = Alignment.Center,
        ) {
            val maximumEmojiSize = with(LocalDensity.current) {
                (maxWidth - 8.dp).coerceAtLeast(0.dp).toSp()
            }
            val emojiSize = minOf(
                MaterialTheme.typography.displayMedium.fontSize.value,
                (maximumEmojiSize * 0.7f).value,
            ).sp
            val sparkleSize = with(LocalDensity.current) {
                (maxWidth * SPARKLE_SIZE_FRACTION).toSp()
            }

            AnimatedHeartbeat(modifier = Modifier.fillMaxSize()) {
                GameVictoryPrize(emojiSize = emojiSize)
            }

            AnimatedSparks(modifier = Modifier.fillMaxSize()) {
                GameVictorySparkle(fontSize = sparkleSize)
            }
        }
    }
}

@Composable
private fun GameVictoryPrize(
    emojiSize: TextUnit,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.tertiaryContainer,
        contentColor = MaterialTheme.colorScheme.onTertiaryContainer,
        shape = CircleShape,
    ) {
        Box(contentAlignment = Alignment.Center) {
            Text(
                text = TROPHY_GLYPH,
                modifier = Modifier.padding(4.dp),
                fontSize = emojiSize,
                fontWeight = FontWeight.Bold,
                lineHeight = emojiSize,
            )
        }
    }
}

@Composable
private fun GameVictorySparkle(
    fontSize: TextUnit,
    modifier: Modifier = Modifier,
) {
    Text(
        text = SPARKLE_GLYPH,
        modifier = modifier,
        color = MaterialTheme.colorScheme.onTertiaryContainer,
        fontSize = fontSize,
        fontWeight = FontWeight.Bold,
        lineHeight = fontSize,
    )
}

@Composable
private fun GameVictoryMessage(
    boardSize: Int,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = stringResource(R.string.game_status_solved_title),
            modifier = Modifier
                .semantics { heading() }
                .testTag(GAME_VICTORY_TITLE_TEST_TAG),
            style = MaterialTheme.typography.displaySmall,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = stringResource(
                R.string.game_status_solved_message,
                boardSize,
            ),
            modifier = Modifier.testTag(GAME_VICTORY_MESSAGE_TEST_TAG),
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
        )
    }
}

@Composable
private fun GameVictoryActions(
    onPlayAgain: () -> Unit,
    onChooseBoardSize: () -> Unit,
    useHorizontalLayout: Boolean,
    modifier: Modifier = Modifier,
) {
    if (useHorizontalLayout) {
        Row(
            modifier = modifier,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            GameVictoryChooseSizeButton(
                onClick = onChooseBoardSize,
                modifier = Modifier.weight(1f),
            )
            GameVictoryPlayAgainButton(
                onClick = onPlayAgain,
                modifier = Modifier.weight(1f),
            )
        }
    } else {
        Column(
            modifier = modifier,
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            GameVictoryPlayAgainButton(
                onClick = onPlayAgain,
                modifier = Modifier.fillMaxWidth(),
            )
            GameVictoryChooseSizeButton(
                onClick = onChooseBoardSize,
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}

@Composable
private fun GameVictoryPlayAgainButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Button(
        onClick = onClick,
        modifier = modifier.testTag(GAME_VICTORY_PLAY_AGAIN_TEST_TAG),
    ) {
        Text(text = stringResource(R.string.game_play_again))
    }
}

@Composable
private fun GameVictoryChooseSizeButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    OutlinedButton(
        onClick = onClick,
        modifier = modifier.testTag(GAME_VICTORY_CHOOSE_SIZE_TEST_TAG),
    ) {
        Text(text = stringResource(R.string.game_choose_board_size))
    }
}

@NQueensPreviews
@Composable
private fun GameVictoryDialogPreview() {
    NQueensPreview {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            contentAlignment = Alignment.Center,
        ) {
            GameVictoryDialogContent(
                boardSize = 8,
                onClose = {},
                onPlayAgain = {},
                onChooseBoardSize = {},
                modifier = Modifier
                    .widthIn(max = MAX_VICTORY_DIALOG_WIDTH)
                    .fillMaxWidth(),
            )
        }
    }
}

internal const val GAME_VICTORY_CHOOSE_SIZE_TEST_TAG = "game_victory_choose_size"
internal const val GAME_VICTORY_CLOSE_TEST_TAG = "game_victory_close"
internal const val GAME_VICTORY_DIALOG_TEST_TAG = "game_victory_dialog"
internal const val GAME_VICTORY_HERO_TEST_TAG = "game_victory_hero"
internal const val GAME_VICTORY_MESSAGE_TEST_TAG = "game_victory_message"
internal const val GAME_VICTORY_PLAY_AGAIN_TEST_TAG = "game_victory_play_again"
internal const val GAME_VICTORY_TITLE_TEST_TAG = "game_victory_title"

private val MAX_VICTORY_DIALOG_WIDTH = 560.dp
private const val SPARKLE_GLYPH = "✦"
private const val SPARKLE_SIZE_FRACTION = 0.18f
private const val TROPHY_GLYPH = "🏆"
