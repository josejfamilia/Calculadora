package com.endominios.calculadora

import kotlin.math.abs

private const val MAX_DIGITS = 12

sealed class CalculatorOperation(val symbol: String) {
    object Add : CalculatorOperation("+")
    object Subtract : CalculatorOperation("−")
    object Multiply : CalculatorOperation("×")
    object Divide : CalculatorOperation("÷")
}

sealed class CalculatorAction {
    data class Number(val digit: Int) : CalculatorAction()
    data class Operation(val operation: CalculatorOperation) : CalculatorAction()
    object Decimal : CalculatorAction()
    object Clear : CalculatorAction()
    object Calculate : CalculatorAction()
    object ToggleSign : CalculatorAction()
    object Percent : CalculatorAction()
}

data class CalculatorState(
    val number1: String = "",
    val number2: String = "",
    val operation: CalculatorOperation? = null,
    val isError: Boolean = false
)

fun CalculatorState.reduce(action: CalculatorAction): CalculatorState = when (action) {
    is CalculatorAction.Number -> enterDigit(action.digit)
    is CalculatorAction.Decimal -> enterDecimal()
    is CalculatorAction.Operation -> enterOperation(action.operation)
    is CalculatorAction.Clear -> CalculatorState()
    is CalculatorAction.Calculate -> performCalculation()
    is CalculatorAction.ToggleSign -> toggleSign()
    is CalculatorAction.Percent -> applyPercent()
}

private fun CalculatorState.enterDigit(digit: Int): CalculatorState {
    if (isError) return CalculatorState().enterDigit(digit)
    return if (operation == null) {
        if (number1 == "0") copy(number1 = digit.toString())
        else if (number1.length < MAX_DIGITS) copy(number1 = number1 + digit)
        else this
    } else {
        if (number2 == "0") copy(number2 = digit.toString())
        else if (number2.length < MAX_DIGITS) copy(number2 = number2 + digit)
        else this
    }
}

private fun CalculatorState.enterDecimal(): CalculatorState {
    if (isError) return CalculatorState().enterDecimal()
    return if (operation == null) {
        when {
            number1.isEmpty() -> copy(number1 = "0.")
            !number1.contains(".") -> copy(number1 = "$number1.")
            else -> this
        }
    } else {
        when {
            number2.isEmpty() -> copy(number2 = "0.")
            !number2.contains(".") -> copy(number2 = "$number2.")
            else -> this
        }
    }
}

private fun CalculatorState.enterOperation(op: CalculatorOperation): CalculatorState {
    if (isError || number1.isEmpty()) return this
    return if (number2.isNotEmpty()) {
        performCalculation().copy(operation = op)
    } else {
        copy(operation = op)
    }
}

private fun CalculatorState.performCalculation(): CalculatorState {
    val op = operation ?: return this
    val n1 = number1.toDoubleOrNull() ?: return this
    val n2 = number2.toDoubleOrNull() ?: return this

    if (op is CalculatorOperation.Divide && n2 == 0.0) {
        return CalculatorState(number1 = "Error", isError = true)
    }

    val result = when (op) {
        is CalculatorOperation.Add -> n1 + n2
        is CalculatorOperation.Subtract -> n1 - n2
        is CalculatorOperation.Multiply -> n1 * n2
        is CalculatorOperation.Divide -> n1 / n2
    }
    return CalculatorState(number1 = result.toDisplayString())
}

private fun CalculatorState.toggleSign(): CalculatorState {
    if (isError) return this
    return when {
        operation != null && number2.isEmpty() -> this // aún no hay segundo valor que modificar
        number2.isNotEmpty() -> copy(number2 = number2.toggled())
        number1.isNotEmpty() -> copy(number1 = number1.toggled())
        else -> this
    }
}

private fun CalculatorState.applyPercent(): CalculatorState {
    if (isError) return this
    return when {
        operation != null && number2.isEmpty() -> this // aún no hay segundo valor que modificar
        number2.isNotEmpty() -> copy(number2 = ((number2.toDoubleOrNull() ?: return this) / 100).toDisplayString())
        number1.isNotEmpty() -> copy(number1 = ((number1.toDoubleOrNull() ?: return this) / 100).toDisplayString())
        else -> this
    }
}

private fun String.toggled(): String =
    if (startsWith("-")) removePrefix("-") else "-$this"

private fun Double.toDisplayString(): String {
    val text = if (this == this.toLong().toDouble() && abs(this) < 1e12) {
        this.toLong().toString()
    } else {
        "%.8f".format(this).trimEnd('0').trimEnd('.')
    }
    return if (text.length > MAX_DIGITS + 2) "Error" else text
}

/** Número principal mostrado en pantalla. */
val CalculatorState.mainDisplay: String
    get() {
        // Tras elegir una operación, la segunda entrada empieza vacía
        // en vez de seguir mostrando el primer número.
        if (operation != null && number2.isEmpty()) return "0"
        return number2.ifEmpty { number1 }.ifEmpty { "0" }
    }

/** Línea superior con la operación en curso, vacía si no hay ninguna. */
val CalculatorState.expressionDisplay: String
    get() = if (operation != null) "$number1 ${operation.symbol}" else ""
