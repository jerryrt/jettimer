/*
 * Copyright 2021 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.androiddevchallenge.ui.main

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material.MaterialTheme
import androidx.compose.material.ProgressIndicatorDefaults
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import com.example.androiddevchallenge.R
import com.example.androiddevchallenge.ui.component.StartButton
import com.example.androiddevchallenge.ui.component.Timer
import com.example.androiddevchallenge.ui.theme.JettimerTheme
import com.example.androiddevchallenge.util.ThemedPreview
import com.example.androiddevchallenge.util.isZero
import com.example.androiddevchallenge.util.toHhMmSs
import kotlinx.coroutines.delay

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun MainScreen(viewModel: MainViewModel, modifier: Modifier, navigateToAdd: () -> Unit) {
    val time = viewModel.getTimer()
    if (time.isZero()) {
        navigateToAdd()
        return
    }
    val (isFinish, setFinish) = remember { mutableStateOf(false) }
    val tick: Long by viewModel.tick.observeAsState(0)
    BoxWithConstraints {
        val offsetY = with(LocalDensity.current) { maxHeight.toPx().toInt()/2 }
        AnimatedVisibility(
            visible = !isFinish,
            exit = slideOutVertically(targetOffsetY = { -offsetY }) + fadeOut(),
            enter = slideInVertically(initialOffsetY = { -offsetY }),
            initiallyVisible = false
        ) {
            MainScreenBody(
                time = time,
                tick = tick,
                modifier = modifier
                    .background(color = MaterialTheme.colors.primary)
                    .fillMaxSize(),
                onStart = { viewModel.start(time) },
                onDelete = {
                    setFinish(true)
                }
            )
        }
    }
    LaunchedEffect(isFinish) {
        if (isFinish) {
            delay(100)
            viewModel.clearTimer()
            navigateToAdd()
        }
    }
}

@Composable
fun MainScreenBody(
    time: Long,
    tick: Long,
    modifier: Modifier = Modifier,
    onStart: () -> Unit,
    onDelete: () -> Unit
) {
    ConstraintLayout(modifier = modifier) {
        val (startButton, timer, label, delete) = createRefs()
        val progress = tick.toFloat() / time.toFloat()
        val animatedProgress by animateFloatAsState(
            targetValue = progress,
            animationSpec = ProgressIndicatorDefaults.ProgressAnimationSpec
        )
        Timer(
            progress = animatedProgress,
            modifier = Modifier
                .size(200.dp)
                .constrainAs(timer) {
                    linkTo(
                        start = parent.start,
                        top = parent.top,
                        end = parent.end,
                        bottom = startButton.top
                    )
                }
        )
        Text(
            text = tick.toHhMmSs(),
            style = MaterialTheme.typography.h3.copy(
                fontWeight = FontWeight.W400,
                letterSpacing = 1.sp
            ),
            color = MaterialTheme.colors.secondaryVariant,
            modifier = Modifier.constrainAs(label) {
                linkTo(
                    start = timer.start,
                    top = timer.top,
                    end = timer.end,
                    bottom = timer.bottom
                )
            }
        )
        StartButton(
            visible = true,
            onClick = onStart,
            modifier = Modifier
                .size(60.dp)
                .constrainAs(startButton) {
                    bottom.linkTo(parent.bottom, margin = 16.dp)
                    linkTo(start = parent.start, end = parent.end)
                }
        )
        TextButton(onClick = onDelete, modifier = Modifier.constrainAs(delete) {
            linkTo(top = startButton.top, bottom = startButton.bottom)
            start.linkTo(parent.start, margin = 16.dp)
        }) {
            Text(
                text = stringResource(R.string.label_delete),
                style = MaterialTheme.typography.body2.copy(
                    letterSpacing = 0.sp,
                    fontWeight = FontWeight.W400
                ),
                color = JettimerTheme.colors.textPrimaryColor
            )
        }
    }
}

@Preview("Main screen body")
@Composable
fun PreviewHomeScreenBody() {
    ThemedPreview {
        MainScreenBody(time = 36000, tick = 3000, onStart = {}, onDelete = {})
    }
}

@Preview("Main screen body dark")
@Composable
fun PreviewHomeScreenBodyDark() {
    ThemedPreview(darkTheme = true) {
        MainScreenBody(time = 36000, tick = 3000, onStart = {}, onDelete = {})
    }
}
