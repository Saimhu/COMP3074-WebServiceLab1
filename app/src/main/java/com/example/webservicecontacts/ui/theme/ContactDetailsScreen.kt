
package com.example.webservicecontacts.ui.theme

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.webservicecontacts.model.User
import com.example.webservicecontacts.network.RetrofitInstance

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ContactDetailsScreen(
    userId: Int?,
    onBack: () -> Unit
) {
    var user by remember { mutableStateOf<User?>(null) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var loading by remember { mutableStateOf(true) }

    LaunchedEffect(userId) {
        loading = true
        user = null
        errorMessage = null

        if (userId == null) {
            errorMessage = "Invalid user ID."
            loading = false
            return@LaunchedEffect
        }

        try {
            user = RetrofitInstance.api.getUser(userId)
        } catch (e: Exception) {
            errorMessage = e.message ?: "Unable to load contact."
        } finally {
            loading = false
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Contact Details") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        when {
            loading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }

            errorMessage != null -> {
                Text(
                    text = "Error: $errorMessage",
                    modifier = Modifier
                        .padding(paddingValues)
                        .padding(24.dp)
                )
            }

            user != null -> {
                ContactDetails(
                    user = user!!,
                    modifier = Modifier.padding(paddingValues)
                )
            }
        }
    }
}

@Composable
fun ContactDetails(
    user: User,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Text(
            text = user.name,
            style = MaterialTheme.typography.headlineMedium
        )

        DetailRow("ID", user.id.toString())
        DetailRow("Username", user.username)
        DetailRow("Email", user.email)

        Text(
            text = "Phone",
            fontWeight = FontWeight.Bold
        )

        Text(
            text = user.phone,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.clickable {
                dialPhoneNumber(context, user.phone)
            }
        )

        DetailRow("Website", user.website)

        HorizontalDivider()

        Text(
            text = "Address",
            style = MaterialTheme.typography.titleMedium
        )

        DetailRow("Street", user.address.street)
        DetailRow("Suite", user.address.suite)
        DetailRow("City", user.address.city)
        DetailRow("Zip code", user.address.zipcode)
        DetailRow("Latitude", user.address.geo.lat)
        DetailRow("Longitude", user.address.geo.lng)

        HorizontalDivider()

        Text(
            text = "Company",
            style = MaterialTheme.typography.titleMedium
        )

        DetailRow("Name", user.company.name)
        DetailRow("Catch phrase", user.company.catchPhrase)
        DetailRow("Business", user.company.bs)
    }
}

@Composable
fun DetailRow(
    label: String,
    value: String
) {
    Column {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Bold
        )
        Text(text = value)
    }
}

fun dialPhoneNumber(
    context: Context,
    phoneNumber: String
) {
    val phoneUri = Uri.parse("tel:$phoneNumber")

    val intent = Intent(
        Intent.ACTION_DIAL,
        phoneUri
    )

    if (intent.resolveActivity(context.packageManager) != null) {
        context.startActivity(intent)
    }
}
