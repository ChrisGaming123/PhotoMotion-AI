package com.example

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.util.Base64
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.data.model.Project
import com.example.ui.MainViewModel
import com.example.ui.theme.MyApplicationTheme
import com.example.util.ImageUtils
import java.io.ByteArrayOutputStream

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val mainViewModel: MainViewModel = viewModel(
                factory = MainViewModel.Factory(application)
            )

            val activePrefs by mainViewModel.preferences.collectAsState()
            val systemDark = isSystemInDarkTheme()

            // Derive theme from active preferences
            val useDarkTheme = when (activePrefs?.theme) {
                "light" -> false
                "dark" -> true
                else -> systemDark
            }

            MyApplicationTheme(darkTheme = useDarkTheme) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AppSkeleton(viewModel = mainViewModel)
                }
            }
        }
    }
}

@Composable
fun AppSkeleton(viewModel: MainViewModel) {
    val email by viewModel.userEmail.collectAsState()

    if (email == null) {
        AuthScreen(viewModel = viewModel)
    } else {
        MainDashboardScreen(viewModel = viewModel, userEmail = email!!)
    }
}

@Composable
fun AuthScreen(viewModel: MainViewModel) {
    var emailInput by remember { mutableStateOf("") }
    var passwordInput by remember { mutableStateOf("") }
    val isAuthLoading by viewModel.isAuthLoading.collectAsState()
    val authError by viewModel.authError.collectAsState()

    val gradientBrush = Brush.linearGradient(
        colors = listOf(Color(0xFF8E2DE2), Color(0xFF4A00E0))
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0D0B18)) // Dark Cosmic Midnight
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Glowing App Icon Holder
        Box(
            modifier = Modifier
                .size(90.dp)
                .clip(RoundedCornerShape(24.dp))
                .background(gradientBrush),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.PlayArrow,
                contentDescription = "App Icon",
                tint = Color.White,
                modifier = Modifier.size(50.dp)
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = "AI PHOTO MOTION",
            fontSize = 30.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White,
            letterSpacing = 2.sp,
            fontFamily = FontFamily.SansSerif
        )

        Text(
            text = "Animate static photos with generative fluid motion",
            fontSize = 13.sp,
            color = Color.Gray,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 24.dp, vertical = 6.dp)
        )

        Spacer(modifier = Modifier.height(30.dp))

        // Card container for Form
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .widthIn(max = 480.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color(0xFF161326),
                contentColor = Color.White
            ),
            shape = RoundedCornerShape(20.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Sign In securely below",
                    fontWeight = FontWeight.Medium,
                    fontSize = 15.sp,
                    color = Color.LightGray,
                    modifier = Modifier.align(Alignment.Start)
                )

                Spacer(modifier = Modifier.height(14.dp))

                OutlinedTextField(
                    value = emailInput,
                    onValueChange = { emailInput = it },
                    label = { Text("Email Address") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("email_input"),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.LightGray,
                        focusedBorderColor = Color(0xFF8E2DE2),
                        unfocusedBorderColor = Color(0xFF332D4B)
                    )
                )

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = passwordInput,
                    onValueChange = { passwordInput = it },
                    label = { Text("Password") },
                    singleLine = true,
                    visualTransformation = PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("password_input"),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.LightGray,
                        focusedBorderColor = Color(0xFF8E2DE2),
                        unfocusedBorderColor = Color(0xFF332D4B)
                    )
                )

                Spacer(modifier = Modifier.height(8.dp))

                if (authError != null) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Warning,
                            contentDescription = "Error icon",
                            tint = Color.Red,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = authError!!,
                            color = Color.Red,
                            fontSize = 12.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                Button(
                    onClick = { viewModel.loginWithEmail(emailInput, passwordInput) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .testTag("login_button"),
                    enabled = !isAuthLoading,
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF8E2DE2),
                        contentColor = Color.White
                    )
                ) {
                    if (isAuthLoading) {
                        CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                    } else {
                        Text("Connect securely with Email")
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Modern mock Google Auth trigger button
                Button(
                    onClick = { viewModel.loginWithGoogle() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .testTag("google_login_button"),
                    enabled = !isAuthLoading,
                    border = ButtonDefaults.outlinedButtonBorder,
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.White,
                        contentColor = Color.Black
                    )
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "G ",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color(0xFFEA4335)
                        )
                        Text("Sign In with Google Cloud Account", fontWeight = FontWeight.SemiBold)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(bottom = 12.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Lock,
                contentDescription = "Secure Lock Icon",
                tint = Color.LightGray,
                modifier = Modifier.size(14.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = "Preferences & archives securely synced with AI Cloud Vault",
                fontSize = 11.sp,
                color = Color.LightGray
            )
        }
    }
}

@Composable
fun MainDashboardScreen(viewModel: MainViewModel, userEmail: String) {
    val activeTab by viewModel.currentTab.collectAsState()
    val isSyncing by viewModel.isSyncing.collectAsState()

    Scaffold(
        topBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF0D0B18))
            ) {
                // Header Bar containing Home Button and sync indicators
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 24.dp)
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    // LEFT & HOME BUTTON: clicking "Home" instantly brings user back to Main Menu / generate tab
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .clickable {
                                viewModel.navigateTo("home")
                            }
                            .testTag("home_button")
                            .background(
                                if (activeTab == "home") Color(0xFF1C1930) else Color.Transparent
                            )
                            .padding(horizontal = 8.dp, vertical = 6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Home,
                            contentDescription = "Home Button Click",
                            tint = if (activeTab == "home") Color(0xFF20E2BA) else Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "HOME",
                            color = if (activeTab == "home") Color(0xFF20E2BA) else Color.White,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    // CENTER: App identity Title
                    Text(
                        text = "PhotoMotion AI",
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.White,
                        fontSize = 15.sp,
                        letterSpacing = 1.sp
                    )

                    // RIGHT: User accounts & backups status
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (isSyncing) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(16.dp),
                                strokeWidth = 2.dp,
                                color = Color(0xFF20E2BA)
                            )
                        } else {
                            Icon(
                                imageVector = Icons.Default.Lock,
                                contentDescription = "Secure Sync State",
                                tint = Color(0xFF20E2BA),
                                modifier = Modifier.size(18.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(8.dp))

                        // Safe Sign-out
                        IconButton(
                            onClick = { viewModel.signOut() },
                            modifier = Modifier.size(32.dp).testTag("logout_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.ExitToApp,
                                contentDescription = "Sign Out User",
                                tint = Color.LightGray
                            )
                        }
                    }
                }

                // Layout Navigation Switch Tab panel
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 4.dp),
                    horizontalArrangement = Arrangement.Center
                ) {
                    val tabs = listOf("home" to "Generator", "projects" to "My Projects", "preferences" to "Secure Config")
                    tabs.forEach { (route, label) ->
                        val selected = activeTab == route
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .padding(horizontal = 4.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(
                                    if (selected) Color(0xFF272145) else Color(0xFF131122)
                                )
                                .clickable { viewModel.navigateTo(route) }
                                .padding(vertical = 10.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = label,
                                fontSize = 12.sp,
                                fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
                                color = if (selected) Color(0xFF20E2BA) else Color.LightGray
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(Color(0xFF090810))
        ) {
            when (activeTab) {
                "home" -> GeneratorTab(viewModel = viewModel)
                "projects" -> ProjectsTab(viewModel = viewModel)
                "preferences" -> PreferencesTab(viewModel = viewModel)
            }

            // Running Processing Overlay
            val isGenerating by viewModel.isGenerating.collectAsState()
            val stepDescription by viewModel.generationStep.collectAsState()
            if (isGenerating) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color(0xE60D0B18)),
                    contentAlignment = Alignment.Center
                ) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth(0.85f)
                            .padding(24.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF1C1930)),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            CircularProgressIndicator(color = Color(0xFF20E2BA), modifier = Modifier.size(48.dp))
                            Spacer(modifier = Modifier.height(20.dp))
                            Text(
                                text = "Animating Live Photo",
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                fontSize = 18.sp
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = stepDescription,
                                color = Color.LightGray,
                                fontSize = 13.sp,
                                textAlign = TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            LinearProgressIndicator(
                                modifier = Modifier.fillMaxWidth().height(4.dp),
                                color = Color(0xFF20E2BA),
                                trackColor = Color(0xFF0F2027)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Integrating visual frames safely to database folder",
                                fontSize = 11.sp,
                                color = Color.Gray
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun GeneratorTab(viewModel: MainViewModel) {
    val context = LocalContext.current
    val imageBase64 by viewModel.selectedImageBase64.collectAsState()
    val prompt by viewModel.promptInput.collectAsState()
    val selectedDuration by viewModel.selectedDuration.collectAsState()
    val isCustomDurationActive by viewModel.isCustomDurationActive.collectAsState()
    val customDurationValue by viewModel.customDurationValue.collectAsState()
    val generationError by viewModel.generationError.collectAsState()

    // Setup device media picking launcher
    val pickMediaLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null) {
            val base64 = uriToBase64(context, uri)
            if (base64 != null) {
                viewModel.setImageBytes(base64)
            }
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .widthIn(max = 600.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF131122)),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "1. Frame Canvas Input",
                        color = Color.White,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.align(Alignment.Start)
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    if (imageBase64 == null) {
                        // Empty Image Placeholder Box
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(180.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color(0xFF090810)),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(
                                    imageVector = Icons.Default.Add,
                                    contentDescription = "Image Needed",
                                    tint = Color.Gray,
                                    modifier = Modifier.size(32.dp)
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "No Photo Canvas Active",
                                    color = Color.Gray,
                                    fontSize = 12.sp
                                )
                            }
                        }
                    } else {
                        // Active base64 image renderer
                        val bitmap = remember(imageBase64) {
                            ImageUtils.base64ToBitmap(imageBase64!!)
                        }
                        if (bitmap != null) {
                            Image(
                                bitmap = bitmap.asImageBitmap(),
                                contentDescription = "Active Canvas Image",
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(200.dp)
                                    .clip(RoundedCornerShape(12.dp)),
                                contentScale = ContentScale.Crop
                            )
                        } else {
                            Text("Error loading canvas", color = Color.Red)
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Buttons to Select or Load presets
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Button(
                            onClick = {
                                pickMediaLauncher.launch(
                                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                )
                            },
                            modifier = Modifier
                                .weight(1f)
                                .height(46.dp)
                                .testTag("gallery_picker_button"),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF272145),
                                contentColor = Color.White
                            ),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Icon(Icons.Default.Add, contentDescription = "Gallery key", modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Your Photo", fontSize = 12.sp)
                        }

                        Spacer(modifier = Modifier.width(10.dp))

                        // Trigger clearing active selection
                        if (imageBase64 != null) {
                            OutlinedButton(
                                onClick = { viewModel.clearGeneratorFields() },
                                modifier = Modifier
                                    .height(46.dp),
                                colors = ButtonDefaults.outlinedButtonColors(
                                    contentColor = Color.Red
                                ),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Text("Clear", fontSize = 12.sp)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Stunning fast presets for device testing
                    Text(
                        text = "Or immediately load a preset scene:",
                        color = Color.LightGray,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.align(Alignment.Start)
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf(
                            Triple("sunset", "🌅 Sunset", "custom_preset_sunset"),
                            Triple("cosmic", "🌌 Cosmic", "custom_preset_cosmic"),
                            Triple("ocean", "🌊 Ocean", "custom_preset_ocean")
                        ).forEach { (type, label, testTag) ->
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(Color(0xFF1E1B33))
                                    .clickable {
                                        val generatedBase64 = ImageUtils.generateGradientPlaceholder(type)
                                        viewModel.setImageBytes(generatedBase64)
                                    }
                                    .testTag(testTag)
                                    .padding(vertical = 10.dp, horizontal = 4.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(label, fontSize = 11.sp, color = Color.White, fontWeight = FontWeight.SemiBold)
                            }
                        }
                    }
                }
            }
        }

        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .widthIn(max = 600.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF131122)),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "2. Prompt & Animation Duration",
                        color = Color.White,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = prompt,
                        onValueChange = { viewModel.updatePrompt(it) },
                        label = { Text("What should the AI animate?") },
                        placeholder = { Text("e.g. Make clouds pan slowly and stream sunlight") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("prompt_text_field")
                            .heightIn(min = 90.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.LightGray,
                            focusedBorderColor = Color(0xFF8E2DE2),
                            unfocusedBorderColor = Color(0xFF332D4B)
                        ),
                        maxLines = 4
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Duration preset selector
                    Text(
                        text = "Duration seconds presets:",
                        color = Color.LightGray,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth().testTag("duration_selector"),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf(5, 8, 20).forEach { sec ->
                            val active = !isCustomDurationActive && selectedDuration == sec
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (active) Color(0xFF8E2DE2) else Color(0xFF1E1B33))
                                    .clickable { viewModel.selectPresetDuration(sec) }
                                    .testTag("custom_preset_$sec")
                                    .padding(vertical = 12.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "$sec sec",
                                    color = Color.White,
                                    fontSize = 12.sp,
                                    fontWeight = if (active) FontWeight.Bold else FontWeight.Medium
                                )
                            }
                        }

                        // Custom Duration tab selector
                        val customActive = isCustomDurationActive
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (customActive) Color(0xFF8E2DE2) else Color(0xFF1E1B33))
                                .clickable { viewModel.activateCustomDuration(customDurationValue) }
                                .testTag("custom_preset_custom")
                                .padding(vertical = 12.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Custom",
                                color = Color.White,
                                fontSize = 12.sp,
                                fontWeight = if (customActive) FontWeight.Bold else FontWeight.Medium
                            )
                        }
                    }

                    // Sliding custom values if active (up to 60s max)
                    AnimatedVisibility(visible = isCustomDurationActive) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 16.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Custom Animation Speed Limit",
                                    fontSize = 11.sp,
                                    color = Color.Gray
                                )
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(Color(0xFF272145))
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        text = "$customDurationValue seconds",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF20E2BA)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Slider(
                                value = customDurationValue.toFloat(),
                                onValueChange = { viewModel.updateCustomDurationValue(it.toInt()) },
                                valueRange = 1f..60f,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("custom_slider"),
                                colors = SliderDefaults.colors(
                                    thumbColor = Color(0xFF20E2BA),
                                    activeTrackColor = Color(0xFF8E2DE2),
                                    inactiveTrackColor = Color(0xFF272145)
                                )
                            )
                        }
                    }
                }
            }
        }

        item {
            if (generationError != null) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .widthIn(max = 600.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0x33FF0000))
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Warning, contentDescription = "Alert", tint = Color.Red)
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = generationError!!,
                            color = Color.White,
                            fontSize = 13.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            // Main Action Submit button Processing request
            val activeDuration = if (isCustomDurationActive) customDurationValue else selectedDuration
            Button(
                onClick = { viewModel.submitMotionRequest() },
                modifier = Modifier
                    .fillMaxWidth()
                    .widthIn(max = 600.dp)
                    .height(54.dp)
                    .testTag("submit_button"),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF8E2DE2),
                    contentColor = Color.White
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(Icons.Default.PlayArrow, contentDescription = "Cloud Icon", tint = Color.White)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Animate Now ($activeDuration Seconds Video)",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
fun ProjectsTab(viewModel: MainViewModel) {
    val projectsList by viewModel.projects.collectAsState()

    if (projectsList.isEmpty()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "No creations",
                tint = Color.DarkGray,
                modifier = Modifier.size(60.dp)
            )
            Spacer(modifier = Modifier.height(14.dp))
            Text(
                text = "Archived project list is empty",
                fontWeight = FontWeight.Bold,
                color = Color.LightGray,
                fontSize = 15.sp
            )
            Text(
                text = "Proceed back to the Generator tab to upload a Canvas image and let Gemini build animations.",
                color = Color.Gray,
                fontSize = 11.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 6.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = { viewModel.navigateTo("home") },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF272145))
            ) {
                Text("Synthesize Fresh Workspace")
            }
        }
    } else {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Text(
                    text = "${projectsList.size} AI Generated Motion Projects",
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    fontSize = 16.sp
                )
            }
            items(projectsList, key = { it.id }) { project ->
                ProjectItemCard(project = project, onDelete = { viewModel.deleteProject(project.id) })
            }
        }
    }
}

@Composable
fun ProjectItemCard(project: Project, onDelete: () -> Unit) {
    var expanded by remember { mutableStateOf(false) }

    // Dynamic animation slideshow simulator modeling the requested video duration!
    var currentPlaybackStage by remember { mutableIntStateOf(1) }
    var isPlayingSequence by remember { mutableStateOf(false) }

    LaunchedEffect(isPlayingSequence, currentPlaybackStage) {
        if (isPlayingSequence) {
            kotlinx.coroutines.delay(1800) // Transition frames speed
            currentPlaybackStage = if (currentPlaybackStage >= 3) 1 else currentPlaybackStage + 1
        }
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("project_item_${project.id}"),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF131122)),
        shape = RoundedCornerShape(14.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            // Main picture and info Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Top
            ) {
                val bitmap = remember(project.photoBase64) {
                    ImageUtils.base64ToBitmap(project.photoBase64)
                }

                if (bitmap != null) {
                    Box(
                        modifier = Modifier
                            .size(100.dp)
                            .clip(RoundedCornerShape(10.dp))
                    ) {
                        Image(
                            bitmap = bitmap.asImageBitmap(),
                            contentDescription = "Project Canvas",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )

                        // Play indicator badge
                        Box(
                            modifier = Modifier
                                .align(Alignment.BottomEnd)
                                .background(Color(0xE60D0B18))
                                .padding(horizontal = 4.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = "${project.durationSeconds}s",
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF20E2BA)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = project.promptText,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        maxLines = 2
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Synthesized: ${android.text.format.DateUtils.getRelativeTimeSpanString(project.timestamp)}",
                        fontSize = 11.sp,
                        color = Color.Gray
                    )
                    Spacer(modifier = Modifier.height(6.dp))

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(Color(0xFF272145))
                            .clickable {
                                isPlayingSequence = !isPlayingSequence
                            }
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.PlayArrow,
                            contentDescription = "Simulate",
                            tint = if (isPlayingSequence) Color(0xFF20E2BA) else Color.White,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = if (isPlayingSequence) "Playing Stage $currentPlaybackStage..." else "Simulate Motion",
                            fontSize = 10.sp,
                            color = if (isPlayingSequence) Color(0xFF20E2BA) else Color.White,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }

                IconButton(onClick = onDelete) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Clear Project Record",
                        tint = Color.DarkGray
                    )
                }
            }

            // Expanded simulation detailing transition steps
            Spacer(modifier = Modifier.height(8.dp))

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xFF090810))
                    .padding(10.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Visual Animation Stages",
                        fontSize = 11.sp,
                        color = Color(0xFF20E2BA),
                        fontWeight = FontWeight.Bold
                    )
                    IconButton(
                        onClick = { expanded = !expanded },
                        modifier = Modifier.size(24.dp)
                    ) {
                        Text(
                            text = if (expanded) "▲" else "▼",
                            color = Color.Gray,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                // If expanded or running simulate show details
                if (expanded || isPlayingSequence) {
                    Spacer(modifier = Modifier.height(6.dp))

                    // Stages view progress row
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        for (i in 1..3) {
                            val active = currentPlaybackStage == i || (!isPlayingSequence && expanded)
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(3.dp)
                                    .clip(RoundedCornerShape(2.dp))
                                    .background(
                                        if (active) Color(0xFF8E2DE2) else Color(0xFF1E1B33)
                                    )
                            )
                        }
                    }

                    val currentStageText = when (currentPlaybackStage) {
                        1 -> "Stage 1 (Beginning): ${project.stage1Description}"
                        2 -> "Stage 2 (Middle Motion): ${project.stage2Description}"
                        else -> "Stage 3 (Peak Finality): ${project.stage3Description}"
                    }

                    Text(
                        text = currentStageText,
                        fontSize = 11.sp,
                        color = Color.LightGray
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "AI Overview:",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Gray
                    )
                    Text(
                        text = project.aiSummary,
                        fontSize = 11.sp,
                        color = Color.White
                    )
                } else {
                    Text(
                        text = "Click 'Simulate Motion' or tap toggle arrow key to detail the 3-stage generative transition timeline.",
                        fontSize = 10.sp,
                        color = Color.Gray
                    )
                }
            }
        }
    }
}

@Composable
fun PreferencesTab(viewModel: MainViewModel) {
    val activePrefs by viewModel.preferences.collectAsState()
    val isSyncing by viewModel.isSyncing.collectAsState()
    val syncLogMessage by viewModel.lastSyncMessage.collectAsState()

    var activeTheme by remember { mutableStateOf("dark") }
    var defaultDuration by remember { mutableIntStateOf(8) }
    var backupOn by remember { mutableStateOf(true) }

    // Synchronize local compose state with database loaded flows
    LaunchedEffect(activePrefs) {
        if (activePrefs != null) {
            activeTheme = activePrefs!!.theme
            defaultDuration = activePrefs!!.defaultDurationSeconds
            backupOn = activePrefs!!.enableCloudBackup
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .widthIn(max = 600.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF131122)),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Theme Preferences",
                        color = Color.White,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf("light" to "Light Modern", "dark" to "Space Slate", "system" to "System").forEach { (type, label) ->
                            val selected = activeTheme == type
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (selected) Color(0xFF8E2DE2) else Color(0xFF1E1B33))
                                    .clickable {
                                        activeTheme = type
                                        viewModel.updatePreferences(
                                            theme = type,
                                            defaultDurationSeconds = defaultDuration,
                                            motionIntensity = activePrefs?.motionIntensity ?: "medium",
                                            enableCloudBackup = backupOn
                                        )
                                    }
                                    .padding(vertical = 12.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    label,
                                    fontSize = 11.sp,
                                    color = Color.White,
                                    fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium
                                )
                            }
                        }
                    }
                }
            }
        }

        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .widthIn(max = 600.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF131122)),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Preferred Motion Duration presets",
                        color = Color.White,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Automatically configure selected preset seconds on boot.",
                        fontSize = 11.sp,
                        color = Color.Gray
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf(5, 8, 20).forEach { sec ->
                            val selected = defaultDuration == sec
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (selected) Color(0xFF8E2DE2) else Color(0xFF1E1B33))
                                    .clickable {
                                        defaultDuration = sec
                                        viewModel.updatePreferences(
                                            theme = activeTheme,
                                            defaultDurationSeconds = sec,
                                            motionIntensity = activePrefs?.motionIntensity ?: "medium",
                                            enableCloudBackup = backupOn
                                        )
                                    }
                                    .padding(vertical = 12.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    "$sec sec",
                                    fontSize = 11.sp,
                                    color = Color.White,
                                    fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium
                                )
                            }
                        }
                    }
                }
            }
        }

        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .widthIn(max = 600.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF131122)),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Secure Cloud Synchronization",
                                color = Color.White,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "Sync configuration and outputs securely across Google and Email credentials.",
                                fontSize = 11.sp,
                                color = Color.Gray
                            )
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (backupOn) Color(0xFF20E2BA) else Color(0xFF1E1B33))
                                .clickable {
                                    val nextBackupState = !backupOn
                                    backupOn = nextBackupState
                                    viewModel.updatePreferences(
                                        theme = activeTheme,
                                        defaultDurationSeconds = defaultDuration,
                                        motionIntensity = activePrefs?.motionIntensity ?: "medium",
                                        enableCloudBackup = nextBackupState
                                    )
                                }
                                .padding(horizontal = 12.dp, vertical = 8.dp)
                        ) {
                            Text(
                                text = if (backupOn) "ENABLED" else "DISABLED",
                                color = if (backupOn) Color.Black else Color.Gray,
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "Cloud Sync Activity Logs",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.height(4.dp))

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFF090810))
                            .padding(10.dp)
                    ) {
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Info,
                                    contentDescription = "Sync details",
                                    tint = Color(0xFF20E2BA),
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = syncLogMessage,
                                    fontSize = 11.sp,
                                    color = Color.LightGray
                                )
                            }
                            if (activePrefs != null) {
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = "Last synced: ${android.text.format.DateUtils.getRelativeTimeSpanString(activePrefs!!.lastSyncTimestamp)}",
                                    fontSize = 10.sp,
                                    color = Color.Gray
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = { viewModel.forceCloudSync() },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF272145),
                            contentColor = Color.White
                        )
                    ) {
                        Icon(imageVector = Icons.Default.Refresh, contentDescription = "Sync", modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Force Cloud Archive Mirror Now", fontSize = 12.sp)
                    }
                }
            }
        }
    }
}

/**
 * Scale and convert Android visual photo URIs safely into web JPEG-Base64 bytes.
 */
private fun uriToBase64(context: Context, uri: Uri): String? {
    return try {
        val inputStream = context.contentResolver.openInputStream(uri)
        val originalBitmap = BitmapFactory.decodeStream(inputStream) ?: return null

        // Downscale to max 600 width/height keeping proportion perfectly
        val maxDimension = 600
        val ratio = originalBitmap.width.toFloat() / originalBitmap.height.toFloat()
        val width = if (ratio > 1) maxDimension else (maxDimension * ratio).toInt()
        val height = if (ratio > 1) (maxDimension / ratio).toInt() else maxDimension

        val scaledBitmap = Bitmap.createScaledBitmap(originalBitmap, width, height, true)
        val outputStream = ByteArrayOutputStream()
        scaledBitmap.compress(Bitmap.CompressFormat.JPEG, 80, outputStream)
        Base64.encodeToString(outputStream.toByteArray(), Base64.NO_WRAP)
    } catch (e: Exception) {
        Log.e("ImagePicker", "Failed to compress selected image uri to Base64 payload", e)
        null
    }
}
