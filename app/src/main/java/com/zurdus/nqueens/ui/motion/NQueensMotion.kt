package com.zurdus.nqueens.ui.motion

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalInspectionMode
import kotlinx.coroutines.delay
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random
import kotlin.time.Duration.Companion.milliseconds

@Composable
internal fun AnimatedEntrance(
    enter: EnterTransition,
    modifier: Modifier = Modifier,
    content: @Composable AnimatedVisibilityScope.() -> Unit,
) {
    val isPreview = LocalInspectionMode.current
    val visibilityState = remember(isPreview) {
        MutableTransitionState(isPreview).apply {
            targetState = true
        }
    }

    AnimatedVisibility(
        visibleState = visibilityState,
        modifier = modifier,
        enter = enter,
        exit = ExitTransition.None,
        content = content,
    )
}

@Composable
internal fun AnimatedHeartbeat(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    val sizeScale = remember { Animatable(HEARTBEAT_REST_SCALE) }

    LaunchedEffect(Unit) {
        delay(HEARTBEAT_INITIAL_DELAY_MILLIS.milliseconds)

        while (true) {
            sizeScale.animateTo(
                targetValue = HEARTBEAT_MAXIMUM_SCALE,
                animationSpec = tween(
                    durationMillis = HEARTBEAT_INFLATE_DURATION_MILLIS,
                    easing = FastOutSlowInEasing,
                ),
            )
            delay(HEARTBEAT_REST_DELAY_MILLIS.milliseconds)

            sizeScale.animateTo(
                targetValue = HEARTBEAT_REST_SCALE,
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioHighBouncy,
                    stiffness = Spring.StiffnessLow,
                ),
            )

            delay(HEARTBEAT_REST_DELAY_MILLIS.milliseconds)
        }
    }

    BoxWithConstraints(
        modifier = modifier,
        contentAlignment = Alignment.Center,
    ) {
        val maximumSize = minOf(maxWidth, maxHeight)
        val restingSize = maximumSize / HEARTBEAT_MAXIMUM_SCALE

        Box(modifier = Modifier.size(restingSize * sizeScale.value)) {
            content()
        }
    }
}

@Composable
internal fun AnimatedSparks(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    val activeSparks = remember { mutableStateListOf<Spark>() }

    LaunchedEffect(Unit) {
        var nextSparkId = 0L

        while (true) {
            val sparkCount = Random.Default.nextInt(
                from = MINIMUM_SPARKS_PER_CLUSTER,
                until = MAXIMUM_ACTIVE_SPARKS + 1,
            )

            repeat(sparkCount) {
                delay(
                    Random.Default.nextLong(
                        from = SPARK_MINIMUM_DELAY_MILLIS,
                        until = SPARK_MAXIMUM_DELAY_MILLIS + 1,
                    ),
                )

                if (activeSparks.size < MAXIMUM_ACTIVE_SPARKS) {
                    activeSparks += Spark(
                        id = nextSparkId++,
                        angleRadians = Random.Default.nextDouble(
                            from = 0.0,
                            until = 2 * PI,
                        ).toFloat(),
                    )
                }
            }

            delay(SPARK_CLUSTER_REST_MILLIS)
        }
    }

    BoxWithConstraints(
        modifier = modifier,
        contentAlignment = Alignment.Center,
    ) {
        val placementRadius = with(LocalDensity.current) {
            minOf(maxWidth, maxHeight).toPx() * SPARK_PLACEMENT_RADIUS_FRACTION
        }

        activeSparks.forEach { spark ->
            key(spark.id) {
                AnimatedSpark(
                    spark = spark,
                    placementRadius = placementRadius,
                    onFinished = { activeSparks.remove(spark) },
                    content = content,
                )
            }
        }
    }
}

@Composable
private fun AnimatedSpark(
    spark: Spark,
    placementRadius: Float,
    onFinished: () -> Unit,
    content: @Composable () -> Unit,
) {
    val progress = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        progress.animateTo(
            targetValue = 1f,
            animationSpec = tween(
                durationMillis = SPARK_ENTER_DURATION_MILLIS,
                easing = LinearOutSlowInEasing,
            ),
        )
        progress.animateTo(
            targetValue = 0f,
            animationSpec = tween(
                durationMillis = SPARK_EXIT_DURATION_MILLIS,
                easing = FastOutLinearInEasing,
            ),
        )
        onFinished()
    }

    Box(
        modifier = Modifier.graphicsLayer {
            translationX = cos(spark.angleRadians) * placementRadius
            translationY = sin(spark.angleRadians) * placementRadius
            scaleX = progress.value
            scaleY = progress.value
            alpha = progress.value
        },
    ) {
        content()
    }
}

internal object NQueensMotion {
    val queenEnter: EnterTransition =
        fadeIn(
            animationSpec = tween(QUEEN_ENTER_FADE_DURATION_MILLIS),
        ) + scaleIn(
            animationSpec = spring(
                dampingRatio = SOFT_POP_DAMPING_RATIO,
                stiffness = QUEEN_SPRING_STIFFNESS,
            ),
            initialScale = QUEEN_ENTER_INITIAL_SCALE,
        )

    val queenExit: ExitTransition =
        fadeOut(
            animationSpec = tween(QUEEN_EXIT_DURATION_MILLIS),
        ) + scaleOut(
            animationSpec = tween(QUEEN_EXIT_DURATION_MILLIS),
            targetScale = QUEEN_EXIT_TARGET_SCALE,
        )

    val celebrationEnter: EnterTransition =
        fadeIn(
            animationSpec = tween(
                durationMillis = CELEBRATION_FADE_DURATION_MILLIS,
                delayMillis = CELEBRATION_FADE_DELAY_MILLIS,
            ),
        ) + scaleIn(
            animationSpec = spring(
                dampingRatio = SOFT_POP_DAMPING_RATIO,
                stiffness = CELEBRATION_SPRING_STIFFNESS,
            ),
            initialScale = CELEBRATION_INITIAL_SCALE,
        )
}

private data class Spark(
    val id: Long,
    val angleRadians: Float,
)

private const val CELEBRATION_FADE_DELAY_MILLIS = 40
private const val CELEBRATION_FADE_DURATION_MILLIS = 180
private const val CELEBRATION_INITIAL_SCALE = 0.62f
private const val CELEBRATION_SPRING_STIFFNESS = 450f
private const val HEARTBEAT_INITIAL_DELAY_MILLIS = 500L
private const val HEARTBEAT_INFLATE_DURATION_MILLIS = 300
private const val HEARTBEAT_MAXIMUM_SCALE = 1.08f
private const val HEARTBEAT_REST_SCALE = 1f
private const val HEARTBEAT_REST_DELAY_MILLIS = 900L
private const val MAXIMUM_ACTIVE_SPARKS = 3
private const val MINIMUM_SPARKS_PER_CLUSTER = 2
private const val QUEEN_ENTER_FADE_DURATION_MILLIS = 100
private const val QUEEN_ENTER_INITIAL_SCALE = 0.68f
private const val QUEEN_EXIT_DURATION_MILLIS = 100
private const val QUEEN_EXIT_TARGET_SCALE = 0.76f
private const val QUEEN_SPRING_STIFFNESS = 700f
private const val SOFT_POP_DAMPING_RATIO = 0.7f
private const val SPARK_CLUSTER_REST_MILLIS = 900L
private const val SPARK_ENTER_DURATION_MILLIS = 90
private const val SPARK_EXIT_DURATION_MILLIS = 650
private const val SPARK_MAXIMUM_DELAY_MILLIS = 650L
private const val SPARK_MINIMUM_DELAY_MILLIS = 300L
private const val SPARK_PLACEMENT_RADIUS_FRACTION = 0.38f
