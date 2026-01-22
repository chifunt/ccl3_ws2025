package com.chifunt.chromaticharptabs.ui.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.outlined.Mic
import androidx.compose.material.icons.outlined.MicOff
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.IconToggleButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Switch
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
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
import com.chifunt.chromaticharptabs.ui.components.common.DebouncedFilledIconButton
import com.chifunt.chromaticharptabs.ui.components.notation.TabNotationInlineDisplay
import com.chifunt.chromaticharptabs.ui.viewmodels.PracticeViewModel
import com.chifunt.chromaticharptabs.ui.components.common.TopBackBar
import com.chifunt.chromaticharptabs.ui.theme.RosePineBase
import com.chifunt.chromaticharptabs.ui.theme.RosePineDawnGold
import com.chifunt.chromaticharptabs.ui.theme.RosePineDawnLove
import com.chifunt.chromaticharptabs.ui.theme.RosePineDawnPine
import com.chifunt.chromaticharptabs.ui.theme.RosePineDawnSubtle
import com.chifunt.chromaticharptabs.ui.theme.RosePineGold
import com.chifunt.chromaticharptabs.ui.theme.RosePineLove
import com.chifunt.chromaticharptabs.ui.theme.RosePinePine
import com.chifunt.chromaticharptabs.ui.theme.RosePineSubtle
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
        TopBackBar(
            onBack = onBack,
            actions = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconToggleButton(
                        checked = micEnabled,
                        onCheckedChange = { enabled ->
                            if (!enabled) {
                                micEnabled = false
                                return@IconToggleButton
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
                        }
                    ) {
                        if (micEnabled) {
                            Icon(
                                imageVector = Icons.Outlined.Mic,
                                contentDescription = stringResource(R.string.practice_mic_on),
                                tint = MaterialTheme.colorScheme.primary
                            )
                        } else {
                            Icon(
                                imageVector = Icons.Outlined.MicOff,
                                contentDescription = stringResource(R.string.practice_mic_off),
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                    IconButton(onClick = { showSettings = true }) {
                        Icon(
                            imageVector = Icons.Outlined.Settings,
                            contentDescription = stringResource(R.string.practice_settings)
                        )
                    }
                    DropdownMenu(
                        expanded = showSettings,
                        onDismissRequest = { showSettings = false }
                    ) {
                        Column(modifier = Modifier.padding(spacingMedium)) {
                            Text(
                                text = stringResource(R.string.practice_note_size),
                                fontWeight = FontWeight.Medium
                            )
                            Slider(
                                value = noteSize,
                                onValueChange = { noteSize = it },
                                valueRange = 24f..48f
                            )
                            Spacer(Modifier.height(spacingSmall))
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(text = stringResource(R.string.practice_auto_advance))
                                Spacer(Modifier.width(spacingSmall))
                                Switch(
                                    checked = autoAdvanceLine,
                                    onCheckedChange = { autoAdvanceLine = it }
                                )
                            }
                            Spacer(Modifier.height(spacingSmall))
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(text = stringResource(R.string.practice_advance_on_start))
                                Spacer(Modifier.width(spacingSmall))
                                Switch(
                                    checked = advanceOnNoteStart,
                                    onCheckedChange = { advanceOnNoteStart = it }
                                )
                            }
                        }
                    }
                }
            }
        )

        Scaffold { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(text = state.title, fontWeight = FontWeight.SemiBold, fontSize = 18.sp)

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    AnimatedContent(
                        targetState = state.currentIndex,
                        transitionSpec = {
                            if (targetState > initialState) {
                                (slideInVertically { slideOffsetPx } + fadeIn(tween(120))) togetherWith
                                    (slideOutVertically { -slideOffsetPx } + fadeOut(tween(120)))
                            } else {
                                (slideInVertically { -slideOffsetPx } + fadeIn(tween(120))) togetherWith
                                    (slideOutVertically { slideOffsetPx } + fadeOut(tween(120)))
                            }
                        },
                        label = "practiceLineTransition"
                    ) { index ->
                        TabNotationInlineDisplay(
                            lines = listOf(state.lines[index]),
                            lineSpacing = spacingMedium,
                            centered = true,
                            noteSize = noteSize.dp,
                            noteColorProvider = if (micEnabled) { lineIndex, noteIndex, _ ->
                                if (lineIndex != 0) return@TabNotationInlineDisplay null
                                when {
                                    noteIndex < currentNoteIndex -> subtleColor
                                    suppressNextLineHighlight && noteIndex == currentNoteIndex -> subtleColor
                                    noteIndex == currentNoteIndex && isTargetPlaying -> pineColor
                                    noteIndex == currentNoteIndex && isWrongNotePlaying -> loveColor
                                    noteIndex == currentNoteIndex -> goldColor
                                    else -> null
                                }
                            } else {
                                null
                            },
                            noteVisualProvider = if (micEnabled) { lineIndex, noteIndex, _ ->
                                if (lineIndex != 0) return@TabNotationInlineDisplay com.chifunt.chromaticharptabs.ui.components.notation.NoteVisualState()
                                com.chifunt.chromaticharptabs.ui.components.notation.NoteVisualState(
                                    isCorrect = noteIndex == currentNoteIndex && isTargetPlaying,
                                    isWrong = noteIndex == currentNoteIndex && isWrongNotePlaying
                                )
                            } else {
                                null
                            },
                            onNotePress = { note ->
                                HarmonicaNoteMap.frequencyFor(note)?.let { tonePlayer.start(it) }
                            },
                            onNoteRelease = { tonePlayer.stop() }
                        )
                    }
                }

                Text(
                    text = stringResource(
                        R.string.practice_line_counter,
                        state.currentIndex + 1,
                        state.lines.size
                    ),
                    fontWeight = FontWeight.Medium,
                    color = lineColor,
                    modifier = Modifier.graphicsLayer(
                        scaleX = lineScale.value,
                        scaleY = lineScale.value
                    )
                )

                Spacer(Modifier.height(spacingMedium))

                Row(
                    horizontalArrangement = Arrangement.spacedBy(spacingMedium),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    DebouncedFilledIconButton(
                        onClick = {
                            suppressNextLineHighlight = false
                            currentNoteIndex = 0
                            practiceViewModel.previousLine()
                        },
                        enabled = state.currentIndex > 0,
                        debounceMs = 0L,
                        modifier = Modifier
                            .weight(1f)
                            .height(dimensionResource(R.dimen.filter_chip_height))
                    ) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null)
                    }
                    DebouncedFilledIconButton(
                        onClick = {
                            suppressNextLineHighlight = false
                            currentNoteIndex = 0
                            practiceViewModel.nextLine()
                        },
                        enabled = state.currentIndex < state.lines.lastIndex,
                        debounceMs = 0L,
                        modifier = Modifier
                            .weight(1f)
                            .height(dimensionResource(R.dimen.filter_chip_height))
                    ) {
                        Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null)
                    }
                }
            }
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
            if (!wasCorrect) {
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
