package com.example.how_viii

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.graphics.Color
import androidx.room.Room
import com.example.how_viii.ui.theme.HOW_VIIITheme
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.util.Locale

class AddTransactionActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val db = DatabaseProvider.getDatabase(applicationContext)
        val dao = db.transactionDao()

        setContent {
            HOW_VIIITheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    AddTransactionScreen(
                        modifier = Modifier.padding(innerPadding),
                        dao = dao,
                        onFinish = { finish() }
                    )
                }
            }
        }
    }
}

@Composable
fun AddTransactionScreen(
    modifier: Modifier = Modifier,
    dao: TransactionDao,
    onFinish: () -> Unit
) {
    var amountText by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var selectedType by remember { mutableStateOf("R") } // R or D
    var descriptionError by remember { mutableStateOf<String?>(null) }
    var amountError by remember { mutableStateOf<String?>(null) }
    var hasAttemptedSubmit by remember { mutableStateOf(false) }

    val scope = rememberCoroutineScope()

    val formattedAmount = remember(amountText) {
        if (amountText.isEmpty()) "0,00" else {
            val cleanString = amountText.replace("\\D".toRegex(), "")
            val parsed = cleanString.toDoubleOrNull() ?: 0.0
            val formatted = parsed / 100
            NumberFormat.getCurrencyInstance(Locale("pt", "BR")).format(formatted).replace("R$", "").trim()
        }
    }

    fun validate(): Boolean {
        var isValid = true
        
        if (description.isEmpty()) {
            descriptionError = "A descrição deve conter pelo menos 1 caracter"
            isValid = false
        } else {
            descriptionError = null
        }
        
        val cleanAmount = amountText.replace("\\D".toRegex(), "")
        if (cleanAmount.isEmpty()) {
            amountError = "O valor deve ter pelo menos 1 dígito"
            isValid = false
        } else {
            amountError = null
        }
        
        return isValid
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("Nova Transação", style = MaterialTheme.typography.headlineMedium)
            Button(onClick = onFinish) {
                Text("Voltar")
            }
        }

        Column {
            OutlinedTextField(
                value = description,
                onValueChange = { 
                    description = it 
                    if (hasAttemptedSubmit) {
                        if (it.isNotEmpty()) descriptionError = null
                    }
                },
                label = { Text("Descrição") },
                isError = descriptionError != null,
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text)
            )
            if (descriptionError != null) {
                Text(
                    text = descriptionError!!,
                    color = Color.Red,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(start = 16.dp, top = 4.dp)
                )
            }
        }

        Column {
            OutlinedTextField(
                value = formattedAmount,
                onValueChange = { newValue ->
                    val clean = newValue.replace("\\D".toRegex(), "")
                    if (clean.length <= 15) {
                        amountText = clean
                        if (hasAttemptedSubmit) {
                            if (clean.isNotEmpty()) amountError = null
                        }
                    }
                },
                label = { Text("Valor") },
                isError = amountError != null,
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )
            if (amountError != null) {
                Text(
                    text = amountError!!,
                    color = Color.Red,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(start = 16.dp, top = 4.dp)
                )
            }
        }

        Row {
            RadioButton(
                selected = selectedType == "R",
                onClick = { selectedType = "R" }
            )
            Text("Receita", modifier = Modifier.padding(top = 12.dp))
            Spacer(modifier = Modifier.width(16.dp))
            RadioButton(
                selected = selectedType == "D",
                onClick = { selectedType = "D" }
            )
            Text("Despesa", modifier = Modifier.padding(top = 12.dp))
        }

        Button(
            onClick = {
                hasAttemptedSubmit = true
                if (validate()) {
                    val cleanString = amountText.replace("\\D".toRegex(), "")
                    val parsed = cleanString.toDoubleOrNull() ?: 0.0
                    val amount = parsed / 100
                    
                    val finalAmount = if (selectedType == "D") -amount else amount
                    
                    scope.launch {
                        dao.insert(
                            Transaction(
                                amount = finalAmount,
                                description = description,
                                category = if (selectedType == "R") "Receita" else "Despesa",
                                type = selectedType
                            )
                        )
                        onFinish()
                    }
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Salvar")
        }
    }
}