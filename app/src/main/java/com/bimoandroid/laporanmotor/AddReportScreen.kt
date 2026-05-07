package com.bimoandroid.laporanmotor

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import coil.compose.AsyncImage
import android.widget.Toast
import androidx.compose.ui.platform.LocalContext
import com.google.firebase.storage.FirebaseStorage
import java.util.UUID

@OptIn(ExperimentalMaterial3Api::class)
@Composable

fun AddReportScreen() {

    var kodeBarang by remember {
        mutableStateOf("")
    }

    var noPolisi by remember {
        mutableStateOf("")
    }

    var merk by remember {
        mutableStateOf("")
    }

    var type by remember {
        mutableStateOf("")
    }

    var expandedPerolehan by remember {
        mutableStateOf(false)
    }

    var perolehan by remember {
        mutableStateOf("")
    }

    val listPerolehan =
        listOf("APBD", "APBN")

    var expandedJenis by remember {
        mutableStateOf(false)
    }

    var jenisBarang by remember {
        mutableStateOf("")
    }

    val listJenis =
        listOf("R2", "R3", "R4")

    var imageUri by remember {
        mutableStateOf<Uri?>(null)
    }

    val launcher = rememberLauncherForActivityResult(
        contract =
            ActivityResultContracts.GetContent()
    ) { uri ->

        imageUri = uri
    }

    val context = LocalContext.current
    val storage = FirebaseStorage.getInstance()

    fun uploadImageToFirebase() {

        if (imageUri == null) {

            Toast.makeText(
                context,
                "Pilih gambar dulu",
                Toast.LENGTH_LONG
            ).show()

            return
        }

        val fileName =
            UUID.randomUUID().toString()

        val storageRef =
            storage.reference
                .child("kendaraan/$fileName.jpg")

        storageRef.putFile(imageUri!!)
            .addOnSuccessListener {

                storageRef.downloadUrl
                    .addOnSuccessListener { uri ->

                        val imageUrl = uri.toString()

                        Toast.makeText(
                            context,
                            "Upload berhasil",
                            Toast.LENGTH_LONG
                        ).show()

                        println(imageUrl)
                    }
            }
            .addOnFailureListener { error ->

                Toast.makeText(
                    context,
                    "Upload gagal: ${error.message}",
                    Toast.LENGTH_LONG
                ).show()
            }
    }

    Scaffold(

        topBar = {

            TopAppBar(
                title = {
                    Text("Tambah Laporan")
                }
            )
        }

    ) { paddingValues ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
                .verticalScroll(
                    rememberScrollState()
                )
        ) {

            OutlinedTextField(
                value = kodeBarang,
                onValueChange = {
                    kodeBarang = it
                },
                label = {
                    Text("Kode Barang")
                },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = noPolisi,
                onValueChange = {
                    noPolisi = it
                },
                label = {
                    Text("No Polisi")
                },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(12.dp))

            ExposedDropdownMenuBox(
                expanded = expandedPerolehan,
                onExpandedChange = {
                    expandedPerolehan = !expandedPerolehan
                }
            ) {

                OutlinedTextField(
                    value = perolehan,
                    onValueChange = {},
                    readOnly = true,
                    label = {
                        Text("Perolehan")
                    },
                    modifier = Modifier
                        .menuAnchor()
                        .fillMaxWidth()
                )

                ExposedDropdownMenu(
                    expanded = expandedPerolehan,
                    onDismissRequest = {
                        expandedPerolehan = false
                    }
                ) {

                    listPerolehan.forEach { item ->

                        DropdownMenuItem(
                            text = {
                                Text(item)
                            },
                            onClick = {

                                perolehan = item
                                expandedPerolehan = false
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            ExposedDropdownMenuBox(
                expanded = expandedJenis,
                onExpandedChange = {
                    expandedJenis = !expandedJenis
                }
            ) {

                OutlinedTextField(
                    value = jenisBarang,
                    onValueChange = {},
                    readOnly = true,
                    label = {
                        Text("Jenis Barang")
                    },
                    modifier = Modifier
                        .menuAnchor()
                        .fillMaxWidth()
                )

                ExposedDropdownMenu(
                    expanded = expandedJenis,
                    onDismissRequest = {
                        expandedJenis = false
                    }
                ) {

                    listJenis.forEach { item ->

                        DropdownMenuItem(
                            text = {
                                Text(item)
                            },
                            onClick = {

                                jenisBarang = item
                                expandedJenis = false
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = merk,
                onValueChange = {
                    merk = it
                },
                label = {
                    Text("Merk")
                },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = type,
                onValueChange = {
                    type = it
                },
                label = {
                    Text("Type")
                },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Foto Kendaraan"
            )

            Spacer(modifier = Modifier.height(12.dp))

            Button(
                onClick = {

                    launcher.launch("image/*")
                },
                modifier = Modifier.fillMaxWidth()
            ) {

                Text("Pilih Gambar")
            }

            Spacer(modifier = Modifier.height(12.dp))

            imageUri?.let {

                AsyncImage(
                    model = it,
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(220.dp)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {

                    uploadImageToFirebase()
                },
                modifier = Modifier.fillMaxWidth()
            ) {

                Text("Simpan Laporan")
            }
        }
    }
}