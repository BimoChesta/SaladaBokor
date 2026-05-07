package com.bimoandroid.laporanmotor

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.*
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import androidx.lifecycle.lifecycleScope
import com.bimoandroid.laporanmotor.model.User
import com.bimoandroid.laporanmotor.ui.theme.LaporanmotorTheme
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.firebase.Firebase
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.launch
import androidx.navigation.compose.*
import com.bimoandroid.laporanmotor.navigation.Screen

class MainActivity : ComponentActivity() {

    private val auth = Firebase.auth
    private val db = Firebase.firestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {

            val navController = rememberNavController()

            LaporanmotorTheme {

                NavHost(
                    navController = navController,
                    startDestination =
                        if (auth.currentUser != null)
                            Screen.Dashboard.route
                        else
                            Screen.Login.route
                ) {

                    composable(Screen.Login.route) {

                        LoginScreen(

                            onGoogleLogin = {

                                signInGoogle(
                                    onLoginSuccess = {

                                        navController.navigate(
                                            Screen.Dashboard.route
                                        ) {

                                            popUpTo(Screen.Login.route) {
                                                inclusive = true
                                            }
                                        }
                                    }
                                )
                            }
                        )
                    }

                    composable(Screen.Dashboard.route) {

                        DashboardScreen(

                            userName =
                                auth.currentUser?.displayName ?: "",

                            onLogout = {

                                auth.signOut()

                                navController.navigate(
                                    Screen.Login.route
                                ) {

                                    popUpTo(0)
                                }
                            },

                            onAddReport = {

                                navController.navigate(
                                    Screen.AddReport.route
                                )
                            }
                        )
                    }
                    composable(Screen.AddReport.route) {

                        AddReportScreen()
                    }
                }
            }
        }
    }

//


    private fun signInGoogle(
        onLoginSuccess: () -> Unit
    ) {

        val credentialManager =
            CredentialManager.create(this)

        val googleIdOption =
            GetGoogleIdOption.Builder()
                .setFilterByAuthorizedAccounts(false)
                .setServerClientId(
                    getString(R.string.default_web_client_id)
                )
                .build()

        val request =
            GetCredentialRequest.Builder()
                .addCredentialOption(googleIdOption)
                .build()

        lifecycleScope.launch {

            try {

                val result =
                    credentialManager.getCredential(
                        request = request,
                        context = this@MainActivity
                    )

                val credential = result.credential

                val googleIdTokenCredential =
                    GoogleIdTokenCredential.createFrom(
                        credential.data
                    )

                val googleIdToken =
                    googleIdTokenCredential.idToken

                val firebaseCredential =
                    GoogleAuthProvider.getCredential(
                        googleIdToken,
                        null
                    )

                auth.signInWithCredential(firebaseCredential)
                    .addOnCompleteListener(this@MainActivity) { task ->

                        if (task.isSuccessful) {

                            val firebaseUser =
                                auth.currentUser

                            if (firebaseUser != null) {

                                val uid =
                                    firebaseUser.uid

                                val userRef =
                                    db.collection("users")
                                        .document(uid)

                                userRef.get()
                                    .addOnSuccessListener { document ->

                                        if (!document.exists()) {

                                            val user = User(
                                                uid = uid,
                                                name = firebaseUser.displayName ?: "",
                                                email = firebaseUser.email ?: "",
                                                role = "user"
                                            )

                                            userRef.set(user)
                                                .addOnSuccessListener {

                                                    Toast.makeText(
                                                        this@MainActivity,
                                                        "User berhasil disimpan",
                                                        Toast.LENGTH_LONG
                                                    ).show()
                                                }
                                                .addOnFailureListener {

                                                    Toast.makeText(
                                                        this@MainActivity,
                                                        "Gagal Firestore: ${it.message}",
                                                        Toast.LENGTH_LONG
                                                    ).show()
                                                }

                                        } else {

                                            Toast.makeText(
                                                this@MainActivity,
                                                "User sudah ada",
                                                Toast.LENGTH_LONG
                                            ).show()
                                        }
                                    }

                                onLoginSuccess()
                            }

                        } else {

                            Toast.makeText(
                                this@MainActivity,
                                "Login gagal",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    }

            } catch (e: Exception) {

                Toast.makeText(
                    this@MainActivity,
                    e.message,
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }
}