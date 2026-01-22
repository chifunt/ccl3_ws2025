package com.chifunt.chromaticharptabs.ui.screens

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.font.FontWeight
import androidx.core.content.ContextCompat
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.chifunt.chromaticharptabs.R
import com.chifunt.chromaticharptabs.data.notation.HarmonicaNoteMap
import com.chifunt.chromaticharptabs.ui.AppViewModelProvider
import com.chifunt.chromaticharptabs.ui.audio.MicrophonePitchDetector
import com.chifunt.chromaticharptabs.ui.audio.SineTonePlayer
import com.chifunt.chromaticharptabs.ui.components.practice.PracticeHeader
import com.chifunt.chromaticharptabs.ui.components.practice.PracticeLineCounter
import com.chifunt.chromaticharptabs.ui.components.practice.PracticeNavigationRow
import com.chifunt.chromaticharptabs.ui.components.practice.PracticeNotationArea
import com.chifunt.chromaticharptabs.ui.viewmodels.PracticeViewModel
import com.chifunt.chromaticharptabs.ui.theme.RosePineBase
import com.chifunt.chromaticharptabs.ui.theme.RosePineDawnGold
import com.chifunt.chromaticharptabs.ui.theme.RosePineDawnLove
import com.chifunt.chromaticharptabs.ui.theme.RosePineDawnPine
import com.chifunt.chromaticharptabs.ui.theme.RosePineDawnSubtle
import com.chifunt.chromaticharptabs.ui.theme.RosePineGold
import com.chifunt.chromaticharptabs.ui.theme.RosePineLove
import com.chifunt.chromaticharptabs.ui.theme.RosePinePine
import com.chifunt.chromaticharptabs.ui.theme.RosePineSubtle
import com.chifunt.chromaticharptabs.ui.haptics.LocalHapticsEnabled
import kotlin.math.abs
import kotlin.math.ln
import kotlinx.coroutines.delay

@Composable
fun PracticeScreen(
    modifier: Modifier = Modifier,
    practiceViewModel: PracticeViewModel = viewModel(factory = AppViewModelProvider.Factory),
    onBack: () -> Unit
) {
    val state by practiceViewModel.uiState.collectAsStateWithLifecycle()
    val spacingSmall = dimensionResource(R.dimen.spacing_small)
    val spacingMedium = dimensionResource(R.dimen.spacing_medium)
    val tonePlayer = remember { SineTonePlayer() }
    val context = LocalContext.current
    val haptic = LocalHapticFeedback.current
    val hapticsEnabled = LocalHapticsEnabled.current
    val isDarkTheme = MaterialTheme.colorScheme.background == RosePineBase
    val goldColor = if (isDarkTheme) RosePineGold else RosePineDawnGold
    val pineColor = if (isDarkTheme) RosePinePine else RosePineDawnPine
    val subtleColor = if (isDarkTheme) RosePineSubtle else RosePineDawnSubtle
    val loveColor = if (isDarkTheme) RosePineLove else RosePineDawnLove
    val currentLine = state.lines.getOrNull(state.currentIndex).orEmpty()
    var micEnabled by rememberSaveable { mutableStateOf(false) }
    var detectedPitch by remember { mutableFloatStateOf(Float.NaN) }
    var currentNoteIndex by remember(state.currentIndex) { mutableIntStateOf(0) }
    var isTargetPlaying by remember(state.currentIndex) { mutableStateOf(false) }
    var isWrongNotePlaying by remember(state.currentIndex) { mutableStateOf(false) }
    var suppressNextLineHighlight by remember { mutableStateOf(false) }
    var wasCorrect by remember(state.currentIndex, currentNoteIndex) { mutableStateOf(false) }
    var showSettings by remember { mutableStateOf(false) }
    var noteSize by rememberSaveable { mutableFloatStateOf(32f) }
    var autoAdvanceLine by rememberSaveable { mutableStateOf(true) }
    var advanceOnNoteStart by rememberSaveable { mutableStateOf(true) }
    val toleranceCents = 50.0
    val lineScale = remember { Animatable(1f) }
    var linePulse by remember { mutableStateOf(false) }
    val micScale = remember { Animatable(1f) }
    val micHalo = remember { Animatable(0f) }
    val lineColor = if (linePulse) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
    val slideOffsetPx = with(LocalDensity.current) { 6.dp.roundToPx() }

    val micPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        micEnabled = granted
    }
    val micDetector = remember {
        MicrophonePitchDetector { pitch ->
            detectedPitch = pitch ?: Float.NaN
        }
    }

    DisposableEffect(Unit) {
        onDispose { tonePlayer.release() }
    }

    DisposableEffect(micEnabled) {
        if (micEnabled) {
            micDetector.start()
        } else {
            micDetector.stop()
            detectedPitch = Float.NaN
            isTargetPlaying = false
            isWrongNotePlaying = false
            suppressNextLineHighlight = false
        }
        onDispose { micDetector.stop() }
    }

    LaunchedEffect(state.currentIndex) {
        linePulse = true
        lineScale.snapTo(1f)
        lineScale.animateTo(1.08f, tween(durationMillis = 120))
        lineScale.animateTo(1f, tween(durationMillis = 160))
        delay(140)
        linePulse = false
    }
    LaunchedEffect(micEnabled) {
        if (micEnabled) {
            micScale.snapTo(1f)
            micHalo.snapTo(0f)
            micScale.animateTo(
                targetValue = 1.18f,
                animationSpec = tween(durationMillis = 180, easing = FastOutSlowInEasing)
            )
            micScale.animateTo(
                targetValue = 1f,
                animationSpec = tween(durationMillis = 160, easing = FastOutSlowInEasing)
            )
            micHalo.animateTo(
                targetValue = 1f,
                animationSpec = tween(durationMillis = 280, easing = FastOutSlowInEasing)
            )
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(
                PaddingValues(
                    start = spacingMedium,
                    end = spacingMedium,
                    top = spacingSmall,
                    bottom = spacingMedium
                )
            ),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        PracticeHeader(
            spacingSmall = spacingSmall,
            spacingMedium = spacingMedium,
            micEnabled = micEnabled,
            micScale = micScale.value,
            micHalo = micHalo.value,
            onMicToggle = { enabled ->
                if (hapticsEnabled) {
                    haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                }
                if (!enabled) {
                    micEnabled = false
                    return@PracticeHeader
                }
                val permission = android.Manifest.permission.RECORD_AUDIO
                val permissionState = ContextCompat.checkSelfPermission(
                    context,
                    permission
                )
                if (permissionState == android.content.pm.PackageManager.PERMISSION_GRANTED) {
                    micEnabled = true
                } else {
                    micPermissionLauncher.launch(permission)
                }
            },
            onBack = onBack,
            showSettings = showSettings,
            onShowSettingsChange = { showSettings = it },
            noteSize = noteSize,
            onNoteSizeChange = { noteSize = it },
            autoAdvanceLine = autoAdvanceLine,
            onAutoAdvanceLineChange = { autoAdvanceLine = it },
            advanceOnNoteStart = advanceOnNoteStart,
            onAdvanceOnNoteStartChange = { advanceOnNoteStart = it }
        )

        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = state.title, fontWeight = FontWeight.SemiBold, fontSize = 18.sp)

            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                PracticeNotationArea(
                    lines = state.lines,
                    currentIndex = state.currentIndex,
                    spacingMedium = spacingMedium,
                    slideOffsetPx = slideOffsetPx,
                    noteSize = noteSize.dp,
                    micEnabled = micEnabled,
                    currentNoteIndex = currentNoteIndex,
                    isTargetPlaying = isTargetPlaying,
                    isWrongNotePlaying = isWrongNotePlaying,
                    suppressNextLineHighlight = suppressNextLineHighlight,
                    goldColor = goldColor,
                    pineColor = pineColor,
                    subtleColor = subtleColor,
                    loveColor = loveColor,
                    onNotePress = { note ->
                        HarmonicaNoteMap.frequencyFor(note)?.let { tonePlayer.start(it) }
                    },
                    onNoteRelease = { tonePlayer.stop() }
                )
            }

            PracticeLineCounter(
                currentIndex = state.currentIndex,
                total = state.lines.size,
                lineColor = lineColor,
                lineScale = lineScale.value
            )

            Spacer(Modifier.height(spacingMedium))

            PracticeNavigationRow(
                spacingMedium = spacingMedium,
                onPrev = {
                    suppressNextLineHighlight = false
                    currentNoteIndex = 0
                    practiceViewModel.previousLine()
                },
                onNext = {
                    suppressNextLineHighlight = false
                    currentNoteIndex = 0
                    practiceViewModel.nextLine()
                },
                hasPrev = state.currentIndex > 0,
                hasNext = state.currentIndex < state.lines.lastIndex
            )
        }
    }

    LaunchedEffect(micEnabled, detectedPitch, state.currentIndex, currentNoteIndex) {
        if (!micEnabled || currentLine.isEmpty()) return@LaunchedEffect
        if (currentNoteIndex > currentLine.lastIndex) return@LaunchedEffect
        val targetNote = currentLine.getOrNull(currentNoteIndex) ?: return@LaunchedEffect
        val targetFrequency = HarmonicaNoteMap.frequencyFor(targetNote) ?: return@LaunchedEffect
        val pitch = detectedPitch.takeIf { it.isFinite() }
        if (suppressNextLineHighlight) {
            if (pitch != null) {
                isTargetPlaying = false
                isWrongNotePlaying = false
                return@LaunchedEffect
            }
            suppressNextLineHighlight = false
        }
        val isCorrect = pitch != null &&
            abs(centsDifference(pitch.toDouble(), targetFrequency)) <= toleranceCents
        if (isCorrect) {
            if (!wasCorrect && hapticsEnabled) {
                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
            }
            wasCorrect = true
            isTargetPlaying = true
            isWrongNotePlaying = false
            if (
                autoAdvanceLine &&
                advanceOnNoteStart &&
                currentNoteIndex == currentLine.lastIndex &&
                state.currentIndex < state.lines.lastIndex
            ) {
                practiceViewModel.nextLine()
                currentNoteIndex = 0
                isTargetPlaying = false
                suppressNextLineHighlight = true
            }
        } else {
            wasCorrect = false
            isWrongNotePlaying = pitch != null
            if (suppressNextLineHighlight) {
                suppressNextLineHighlight = false
            }
            if (isTargetPlaying) {
                currentNoteIndex += 1
                if (
                    autoAdvanceLine &&
                    currentNoteIndex > currentLine.lastIndex &&
                    state.currentIndex < state.lines.lastIndex
                ) {
                    practiceViewModel.nextLine()
                }
                isTargetPlaying = false
            }
        }
    }
}

private fun centsDifference(detectedFrequency: Double, targetFrequency: Double): Double {
    return 1200.0 * ln(detectedFrequency / targetFrequency) / ln(2.0)
}
