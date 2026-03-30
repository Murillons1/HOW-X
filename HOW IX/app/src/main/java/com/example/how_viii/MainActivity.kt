package com.example.how_viii

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.how_viii.ui.theme.HOW_VIIITheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val db = DatabaseProvider.getDatabase(applicationContext)
        val dao = db.transactionDao()
        
        setContent {
            HOW_VIIITheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    DashboardScreen(
                        modifier = Modifier.padding(innerPadding),
                        dao = dao,
                        onAddTransactionClick = {
                            startActivity(Intent(this, AddTransactionActivity::class.java))
                        },
                        onExtratoClick = {
                            startActivity(Intent(this, ExtratoActivity::class.java))
                        },
                        onLogoutClick = {
                            val intent = Intent(this, LoginActivity::class.java)
                            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                            startActivity(intent)
                            finish()
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun DashboardScreen(
    modifier: Modifier = Modifier,
    dao: TransactionDao,
    onAddTransactionClick: () -> Unit,
    onExtratoClick: () -> Unit,
    onLogoutClick: () -> Unit
) {
    val totalBalance by dao.getTotalBalance().collectAsState(initial = 0.0)
    val transactions by dao.getAll().collectAsState(initial = emptyList())
    
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End
        ) {
            TextButton(onClick = onLogoutClick) {
                Text("Sair")
            }
        }

        Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(24.dp)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.Center
            ) {
                Text(text = "Saldo Total", style = MaterialTheme.typography.titleMedium)
                Text(
                    text = "R$ %.2f".format(totalBalance ?: 0.0),
                    style = MaterialTheme.typography.displayMedium
                )
            }
        }
        
        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = onAddTransactionClick,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Adicionar Transação")
        }
        
        Spacer(modifier = Modifier.height(24.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onExtratoClick() }
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Extrato Recente",
                style = MaterialTheme.typography.titleLarge
            )
            Text(
                text = "Ver mais >",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.primary
            )
        }

        LazyColumn(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(transactions.take(3)) { transaction ->
                TransactionItem(transaction)
            }
        }
    }
}

@Composable
fun TransactionItem(transaction: Transaction) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(text = transaction.description, style = MaterialTheme.typography.bodyLarge)
                Text(text = transaction.category, style = MaterialTheme.typography.bodySmall)
            }
            Text(
                text = "R$ %.2f".format(transaction.amount),
                style = MaterialTheme.typography.bodyLarge,
                color = if (transaction.type == "R") Color(0xFF4CAF50) else Color(0xFFE53935)
            )
        }
    }
}