package com.endominios.calculadora.update

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.DialogProperties
import com.endominios.calculadora.ui.theme.CalcNumberButton
import com.endominios.calculadora.ui.theme.CalcOperator
import com.endominios.calculadora.ui.theme.CalcTextPrimary
import com.endominios.calculadora.ui.theme.CalcTextSecondary

/**
 * Diálogo bloqueante (sin cierre por atrás ni por click afuera) que se
 * muestra cuando [UpdateChecker] determina que la versión instalada quedó
 * obsoleta según Firebase Remote Config.
 */
@Composable
fun ForceUpdateDialog(message: String, onUpdateClick: () -> Unit) {
    AlertDialog(
        onDismissRequest = {},
        properties = DialogProperties(dismissOnBackPress = false, dismissOnClickOutside = false),
        containerColor = CalcNumberButton,
        title = {
            Text(
                text = "Actualización requerida",
                color = CalcTextPrimary,
                fontWeight = FontWeight.Medium,
                fontSize = 20.sp
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(text = message, color = CalcTextSecondary, fontSize = 15.sp)
            }
        },
        confirmButton = {
            Button(
                onClick = onUpdateClick,
                shape = RoundedCornerShape(50),
                colors = ButtonDefaults.buttonColors(containerColor = CalcOperator, contentColor = Color.White),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Actualizar ahora")
            }
        }
    )
}

fun openPlayStoreListing(context: Context) {
    val packageName = context.packageName
    try {
        context.startActivity(
            Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=$packageName"))
        )
    } catch (_: ActivityNotFoundException) {
        context.startActivity(
            Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=$packageName"))
        )
    }
}
