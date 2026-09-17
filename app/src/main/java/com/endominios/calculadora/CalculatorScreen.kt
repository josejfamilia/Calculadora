package com.endominios.calculadora

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.endominios.calculadora.ui.theme.CalcBackgroundBottom
import com.endominios.calculadora.ui.theme.CalcBackgroundTop
import com.endominios.calculadora.ui.theme.CalcFunctionButton
import com.endominios.calculadora.ui.theme.CalcFunctionButtonPressed
import com.endominios.calculadora.ui.theme.CalcNumberButton
import com.endominios.calculadora.ui.theme.CalcNumberButtonPressed
import com.endominios.calculadora.ui.theme.CalcOperator
import com.endominios.calculadora.ui.theme.CalcOperatorPressed
import com.endominios.calculadora.ui.theme.CalcTextPrimary
import com.endominios.calculadora.ui.theme.CalcTextSecondary

@Composable
fun CalculatorScreen(modifier: Modifier = Modifier) {
    var state by remember { mutableStateOf(CalculatorState()) }
    val onAction: (CalculatorAction) -> Unit = { action -> state = state.reduce(action) }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(CalcBackgroundTop, CalcBackgroundBottom)))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .safeDrawingPadding()
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.Bottom
        ) {
            CalculatorHeader()

            CalculatorDisplay(
                state = state,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            )

            Spacer(height = 20.dp)

            ButtonsGrid(
                state = state,
                onAction = onAction,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            CreditsFooter(modifier = Modifier.padding(bottom = 16.dp))
        }
    }
}

@Composable
private fun CalculatorHeader(modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(top = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        CalculatorLogo(size = 30.dp)
        Text(
            text = "Calculadora",
            color = CalcTextPrimary,
            fontSize = 18.sp,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
private fun CalculatorLogo(size: Dp, modifier: Modifier = Modifier) {
    Canvas(modifier = modifier.size(size)) {
        val w = this.size.width
        val h = this.size.height

        drawRoundRect(
            color = CalcNumberButton,
            topLeft = Offset(w * 0.12f, h * 0.05f),
            size = Size(w * 0.76f, h * 0.9f),
            cornerRadius = CornerRadius(w * 0.14f)
        )
        drawRoundRect(
            color = Color.Black,
            topLeft = Offset(w * 0.22f, h * 0.14f),
            size = Size(w * 0.56f, h * 0.22f),
            cornerRadius = CornerRadius(w * 0.08f)
        )

        val cellW = w * 0.16f
        val cellH = h * 0.13f
        val gapX = w * 0.05f
        val gapY = h * 0.06f
        val gridLeft = w * 0.22f
        val gridTop = h * 0.46f
        val cornerRadius = CornerRadius(w * 0.03f)

        for (row in 0 until 3) {
            for (col in 0 until 3) {
                val x = gridLeft + col * (cellW + gapX)
                val y = gridTop + row * (cellH + gapY)
                val color = if (col == 2) CalcOperator else CalcNumberButtonPressed
                drawRoundRect(
                    color = color,
                    topLeft = Offset(x, y),
                    size = Size(cellW, cellH),
                    cornerRadius = cornerRadius
                )
            }
        }
    }
}

@Composable
private fun CreditsFooter(modifier: Modifier = Modifier) {
    Text(
        text = "Creado por Endominios SRL",
        color = CalcTextSecondary,
        fontSize = 12.sp,
        fontWeight = FontWeight.Light,
        textAlign = TextAlign.Center,
        modifier = modifier.fillMaxWidth()
    )
}

@Composable
private fun Spacer(height: androidx.compose.ui.unit.Dp) {
    Box(modifier = Modifier.height(height))
}

@Composable
private fun CalculatorDisplay(state: CalculatorState, modifier: Modifier = Modifier) {
    val display = state.mainDisplay
    val fontSize = when {
        display.length <= 6 -> 88.sp
        display.length <= 9 -> 64.sp
        else -> 46.sp
    }

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.Bottom,
        horizontalAlignment = Alignment.End
    ) {
        Text(
            text = state.expressionDisplay,
            color = CalcTextSecondary,
            fontSize = 26.sp,
            fontWeight = FontWeight.Light,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        Spacer(height = 4.dp)
        AnimatedContent(
            targetState = display,
            transitionSpec = {
                (fadeIn(animationSpec = spring(stiffness = Spring.StiffnessMedium)))
                    .togetherWith(fadeOut(animationSpec = spring(stiffness = Spring.StiffnessMedium)))
            },
            label = "display"
        ) { value ->
            Text(
                text = value,
                color = CalcTextPrimary,
                fontSize = fontSize,
                fontWeight = FontWeight.Light,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
private fun ButtonsGrid(
    state: CalculatorState,
    onAction: (CalculatorAction) -> Unit,
    modifier: Modifier = Modifier
) {
    val isOperationPending = { op: CalculatorOperation -> state.operation == op && state.number2.isEmpty() }

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Row(horizontalArrangement = Arrangement.spacedBy(14.dp)) {
            CalculatorButton(
                symbol = if (state.isError || (state.number1.isEmpty() && state.number2.isEmpty() && state.operation == null)) "AC" else "C",
                backgroundColor = CalcFunctionButton,
                pressedColor = CalcFunctionButtonPressed,
                contentColor = Color.Black,
                modifier = Modifier.weight(1f),
                onClick = { onAction(CalculatorAction.Clear) }
            )
            CalculatorButton(
                symbol = "+/-",
                backgroundColor = CalcFunctionButton,
                pressedColor = CalcFunctionButtonPressed,
                contentColor = Color.Black,
                fontSize = 28.sp,
                modifier = Modifier.weight(1f),
                onClick = { onAction(CalculatorAction.ToggleSign) }
            )
            CalculatorButton(
                symbol = "%",
                backgroundColor = CalcFunctionButton,
                pressedColor = CalcFunctionButtonPressed,
                contentColor = Color.Black,
                modifier = Modifier.weight(1f),
                onClick = { onAction(CalculatorAction.Percent) }
            )
            OperatorButton(
                symbol = "÷",
                active = isOperationPending(CalculatorOperation.Divide),
                modifier = Modifier.weight(1f),
                onClick = { onAction(CalculatorAction.Operation(CalculatorOperation.Divide)) }
            )
        }

        Row(horizontalArrangement = Arrangement.spacedBy(14.dp)) {
            NumberButton(7, Modifier.weight(1f), onAction)
            NumberButton(8, Modifier.weight(1f), onAction)
            NumberButton(9, Modifier.weight(1f), onAction)
            OperatorButton(
                symbol = "×",
                active = isOperationPending(CalculatorOperation.Multiply),
                modifier = Modifier.weight(1f),
                onClick = { onAction(CalculatorAction.Operation(CalculatorOperation.Multiply)) }
            )
        }

        Row(horizontalArrangement = Arrangement.spacedBy(14.dp)) {
            NumberButton(4, Modifier.weight(1f), onAction)
            NumberButton(5, Modifier.weight(1f), onAction)
            NumberButton(6, Modifier.weight(1f), onAction)
            OperatorButton(
                symbol = "−",
                active = isOperationPending(CalculatorOperation.Subtract),
                modifier = Modifier.weight(1f),
                onClick = { onAction(CalculatorAction.Operation(CalculatorOperation.Subtract)) }
            )
        }

        Row(horizontalArrangement = Arrangement.spacedBy(14.dp)) {
            NumberButton(1, Modifier.weight(1f), onAction)
            NumberButton(2, Modifier.weight(1f), onAction)
            NumberButton(3, Modifier.weight(1f), onAction)
            OperatorButton(
                symbol = "+",
                active = isOperationPending(CalculatorOperation.Add),
                modifier = Modifier.weight(1f),
                onClick = { onAction(CalculatorAction.Operation(CalculatorOperation.Add)) }
            )
        }

        Row(horizontalArrangement = Arrangement.spacedBy(14.dp)) {
            CalculatorButton(
                symbol = "0",
                backgroundColor = CalcNumberButton,
                pressedColor = CalcNumberButtonPressed,
                contentColor = CalcTextPrimary,
                shape = RoundedCornerShape(50),
                contentAlignment = Alignment.CenterStart,
                contentPadding = 30.dp,
                modifier = Modifier.weight(2f),
                onClick = { onAction(CalculatorAction.Number(0)) }
            )
            CalculatorButton(
                symbol = ".",
                backgroundColor = CalcNumberButton,
                pressedColor = CalcNumberButtonPressed,
                contentColor = CalcTextPrimary,
                modifier = Modifier.weight(1f),
                onClick = { onAction(CalculatorAction.Decimal) }
            )
            OperatorButton(
                symbol = "=",
                active = false,
                highlighted = true,
                modifier = Modifier.weight(1f),
                onClick = { onAction(CalculatorAction.Calculate) }
            )
        }
    }
}

@Composable
private fun NumberButton(
    digit: Int,
    modifier: Modifier,
    onAction: (CalculatorAction) -> Unit
) {
    CalculatorButton(
        symbol = digit.toString(),
        backgroundColor = CalcNumberButton,
        pressedColor = CalcNumberButtonPressed,
        contentColor = CalcTextPrimary,
        modifier = modifier,
        onClick = { onAction(CalculatorAction.Number(digit)) }
    )
}

@Composable
private fun OperatorButton(
    symbol: String,
    active: Boolean,
    modifier: Modifier = Modifier,
    highlighted: Boolean = false,
    onClick: () -> Unit
) {
    CalculatorButton(
        symbol = symbol,
        backgroundColor = if (active) CalcTextPrimary else CalcOperator,
        pressedColor = if (active) CalcTextPrimary else CalcOperatorPressed,
        contentColor = if (active) CalcOperator else CalcTextPrimary,
        fontWeight = if (highlighted) FontWeight.Medium else FontWeight.Normal,
        modifier = modifier,
        onClick = onClick
    )
}

@Composable
private fun CalculatorButton(
    symbol: String,
    backgroundColor: Color,
    pressedColor: Color,
    contentColor: Color,
    modifier: Modifier = Modifier,
    shape: androidx.compose.ui.graphics.Shape = CircleShape,
    contentAlignment: Alignment = Alignment.Center,
    contentPadding: androidx.compose.ui.unit.Dp = 0.dp,
    fontSize: TextUnit = 34.sp,
    fontWeight: FontWeight = FontWeight.Normal,
    onClick: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.90f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "buttonScale"
    )
    val haptics = LocalHapticFeedback.current

    Box(
        modifier = modifier
            .aspectRatio(if (shape is RoundedCornerShape) 2f else 1f)
            .scale(scale)
            .clip(shape)
            .background(if (isPressed) pressedColor else backgroundColor)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = {
                    haptics.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                    onClick()
                }
            )
            .padding(start = contentPadding),
        contentAlignment = contentAlignment
    ) {
        Text(
            text = symbol,
            color = contentColor,
            fontSize = fontSize,
            fontWeight = fontWeight
        )
    }
}
