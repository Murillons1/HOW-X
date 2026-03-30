package com.example.how_viii

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.how_viii.ui.theme.HOW_VIIITheme

class ExtratoActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        
        // Use singleton database provider
        val db = DatabaseProvider.getDatabase(applicationContext)
        val dao = db.transactionDao()
        
        setContent {
            HOW_VIIITheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    ExtratoScreen(
                        modifier = Modifier.padding(innerPadding),
                        dao = dao,
                        onBackClick = { finish() }
                    )
                }
            }
        }
    }
}

@Composable
fun ExtratoScreen(
    modifier: Modifier = Modifier,
    dao: TransactionDao,
    onBackClick: () -> Unit
) {
    val transactions by dao.getAll().collectAsState(initial = emptyList())
    
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = "Extrato Completo", style = MaterialTheme.typography.headlineMedium)
            Button(onClick = onBackClick) {
                Text("Voltar")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(transactions) { transaction ->
                TransactionItem(transaction)
            }
        }
    }
}