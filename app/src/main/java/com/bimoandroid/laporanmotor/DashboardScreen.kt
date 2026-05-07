package com.bimoandroid.laporanmotor

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    userName: String,
    onLogout: () -> Unit,
    onAddReport: () -> Unit
) {

    Scaffold(

        topBar = {

            TopAppBar(

                title = {
                    Text("Dashboard")
                },

                actions = {

                    IconButton(
                        onClick = onLogout
                    ) {

                        Icon(
                            imageVector =
                                Icons.Default.ExitToApp,
                            contentDescription = "Logout"
                        )
                    }
                }
            )
        },

        floatingActionButton = {

            FloatingActionButton(
                onClick = onAddReport
            ) {

                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Tambah"
                )
            }
        }

    ) { paddingValues ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {

            Text(
                text = "Selamat Datang,",
                style = MaterialTheme.typography.titleLarge
            )

            Text(
                text = userName,
                style = MaterialTheme.typography.headlineMedium
            )

            Spacer(modifier = Modifier.height(20.dp))

            Card(
                modifier = Modifier.fillMaxWidth()
            ) {

                Column(
                    modifier = Modifier.padding(16.dp)
                ) {

                    Text(
                        text = "Total Laporan"
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "0",
                        style = MaterialTheme.typography.headlineLarge
                    )
                }
            }
        }
    }
}