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
package com.example.androiddevchallenge.util

import androidx.compose.ui.util.fastForEachIndexed
import java.math.BigDecimal
import java.util.concurrent.TimeUnit

fun String.fillWithZeros() = this.padStart(MAX_LENGTH_TIMER, ZERO_STRING.first())
fun String.removeLast() = if (isNotEmpty()) this.take(this.length - 1) else this
fun String.firstInputIsZero(input: String) = this.isEmpty() && input == ZERO_STRING
fun Long.isNotZero(): Boolean = this != ZERO_LONG
fun Long.isZero(): Boolean = this == ZERO_LONG
fun Int.isZero(): Boolean = this == ZERO_INT

fun String.toMillis(): Long {
    var timeInMillis = 0L
    this.fillWithZeros().chunked(2).fastForEachIndexed { i, s ->
        when (i) {
            0 -> timeInMillis += TimeUnit.HOURS.toMillis(s.toLong())
            1 -> timeInMillis += TimeUnit.MINUTES.toMillis(s.toLong())
            2 -> timeInMillis += TimeUnit.SECONDS.toMillis(s.toLong())
        }
    }
    return timeInMillis
}

fun Float.roundUp(): Int = this.toBigDecimal().setScale(0, BigDecimal.ROUND_UP).intValueExact()

fun Int.toStringOrEmpty(): String = if (this.isZero()) EMPTY else this.toString()
fun Int.toFormattedString(): String =
    if (this in 0..9) "$ZERO_STRING$this" else this.toStringOrEmpty()

fun Int.minuteToString(hasHour: Boolean): String =
    if (hasHour) this.toFormattedString() else this.toStringOrEmpty()

fun String.removeExtraColon(): String =
    if (this.first().toString() == COLON) takeLast(length - 1) else this

fun Long.toHhMmSs(): String {
    val seconds = (this % 60).toInt().toFormattedString()
    val minutes = ((this / 60) % 60).toInt()
    val hours = ((this / (60 * 60)) % 24).toInt().toStringOrEmpty()
    var formattedTime = "$hours$COLON${minutes.minuteToString(hours.isNotEmpty())}$COLON$seconds"
    while (formattedTime.isNotEmpty() && formattedTime.first().toString() == COLON) {
        formattedTime = formattedTime.removeExtraColon()
    }
    return formattedTime
}
