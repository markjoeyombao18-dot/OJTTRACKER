package com.example.ojttrackerbyteambalagbag

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.util.Base64
import androidx.activity.compose.BackHandler
import androidx.activity.ComponentActivity
import androidx.activity.enableEdgeToEdge
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Divider
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.ojttrackerbyteambalagbag.ui.theme.OJTTrackerByTeamBalagbagTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.OutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.KeyboardType
import kotlinx.coroutines.delay
import androidx.compose.foundation.layout.wrapContentHeight
import java.io.File
import java.io.FileOutputStream
import java.util.UUID

private val BackgroundColor = Color(0xFF0D1117)
private val CardColor = Color(0xFF161B22)
private val PrimaryBlue = Color(0xFF1976D2)
private val LightBlue = Color(0xFF64B5F6)
private val MainText = Color(0xFFF5F7FA)
private val SecondaryText = Color(0xFFB0BEC5)
private val ErrorRed = Color(0xFFFF6B6B)
private val GreenColor = Color(0xFF4CAF50)

class MainActivity : ComponentActivity() {

    private lateinit var userPreferences: UserPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        userPreferences = UserPreferences(this)

        setContent {

            var showSplash by remember {
                mutableStateOf(true)
            }

            OJTTrackerByTeamBalagbagTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = BackgroundColor
                ) {

                    if (showSplash) {

                        LaunchedEffect(Unit) {
                            delay(2000)
                            showSplash = false
                        }

                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(BackgroundColor),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = "WELCOME TO",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = SecondaryText
                                )

                                Spacer(modifier = Modifier.height(8.dp))

                                Text(
                                    text = "OJT Tracker",
                                    fontSize = 36.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = LightBlue
                                )

                                Spacer(modifier = Modifier.height(4.dp))

                                Text(
                                    text = "by Team Balagbag",
                                    fontSize = 14.sp,
                                    color = SecondaryText
                                )
                            }
                        }

                    } else {

                        OJTTrackerApp(userPreferences)

                    }
                }
            }
        } }
}

@Composable
fun OJTTrackerApp(
    userPreferences: UserPreferences
) {
    var currentScreen by remember {
        mutableStateOf(
            if (userPreferences.isLoggedIn()) {
                "dashboard"
            } else {
                "login"
            }
        )
    }

    when (currentScreen) {

        "login" -> {
            LoginScreen(
                userPreferences = userPreferences,
                onLoginSuccess = {
                    currentScreen = "dashboard"
                },
                onCreateAccount = {
                    currentScreen = "terms"
                }
            )
        }

        "terms" -> {
            TermsScreen(
                onBack = {
                    currentScreen = "login"
                },
                onAgree = {
                    currentScreen = "register"
                }
            )
        }

        "register" -> {
            RegisterScreen(
                userPreferences = userPreferences,
                onBack = {
                    currentScreen = "terms"
                },
                onAccountCreated = {
                    currentScreen = "login"
                }
            )
        }

        "dashboard" -> {
            DashboardScreen(
                userPreferences = userPreferences,
                onLogout = {
                    userPreferences.logout()
                    currentScreen = "login"
                },
                onJournal = {
                    currentScreen = "journals"
                },
                onProfile = {
                    currentScreen = "profile"
                },
                onSummary = {
                    currentScreen = "summary"
                }
            )
        }

        "summary" -> {
            SummaryScreen(
                userPreferences = userPreferences,
                onBack = {
                    currentScreen = "dashboard"
                }
            )
        }
        "journals" -> {
            JournalListScreen(
                onBack = {
                    currentScreen = "dashboard"
                }
            )
        }

        "profile" -> {
            ProfileScreen(
                userPreferences = userPreferences,
                onBack = {
                    currentScreen = "dashboard"
                }
            )
        }
    }
}


// ============================================================
// LOGIN
// ============================================================

@Composable
fun LoginScreen(
    userPreferences: UserPreferences,
    onLoginSuccess: () -> Unit,
    onCreateAccount: () -> Unit
) {
    val context = LocalContext.current

    var showExitDialog by remember {
        mutableStateOf(false)
    }

    var email by remember {
        mutableStateOf("")
    }

    var password by remember {
        mutableStateOf("")
    }

    var passwordVisible by remember {
        mutableStateOf(false)
    }

    var errorMessage by remember {
        mutableStateOf("")
    }

    /*
     * true  = Saved Accounts screen
     * false = Login form
     */
    var showSavedAccounts by remember {
        mutableStateOf(true)
    }

    var accountToRemove by remember {
        mutableStateOf<String?>(null)
    }

    var savedAccountEmails by remember {
        mutableStateOf(
            getSavedAccounts(context)
        )
    }

    /*
     * BACK BUTTON
     *
     * If login form is open because of
     * "Use another account", go back to
     * Saved Accounts first.
     *
     * If already on Saved Accounts,
     * show the Exit warning.
     *
     * If there are no saved accounts,
     * the login form is the starting screen,
     * so Back shows Exit warning.
     */
    BackHandler {

        if (
            showSavedAccounts &&
            savedAccountEmails.isNotEmpty()
        ) {
            showExitDialog = true
        } else if (
            !showSavedAccounts &&
            savedAccountEmails.isNotEmpty()
        ) {
            showSavedAccounts = true
            email = ""
            password = ""
            errorMessage = ""
        } else {
            showExitDialog = true
        }
    }

    /*
     * EXIT DIALOG
     */
    if (showExitDialog) {
        AlertDialog(
            onDismissRequest = {
                showExitDialog = false
            },
            title = {
                Text(
                    text = "Exit OJT Tracker?",
                    color = MainText,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Text(
                    text = "Are you sure you want to exit the application?",
                    color = SecondaryText
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        if (context is Activity) {
                            context.finish()
                        }
                    }
                ) {
                    Text(
                        text = "EXIT",
                        color = ErrorRed,
                        fontWeight = FontWeight.Bold
                    )
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showExitDialog = false
                    }
                ) {
                    Text(
                        text = "CANCEL",
                        color = LightBlue,
                        fontWeight = FontWeight.Bold
                    )
                }
            },
            containerColor = CardColor
        )
    }

    /*
     * GET SAVED ACCOUNTS
     */
    val savedAccounts = remember(savedAccountEmails) {

        context.getSharedPreferences(
            ACCOUNT_PREFS,
            Context.MODE_PRIVATE
        )
            .getStringSet(
                ACCOUNT_LIST,
                emptySet()
            )
            ?.mapNotNull { record ->

                val parts = record.split("|||")

                if (parts.size >= 9) {
                    parts.map {
                        Uri.decode(it)
                    }
                } else {
                    null
                }
            }
            ?.filter { account ->

                savedAccountEmails.contains(
                    account[0]
                        .trim()
                        .lowercase()
                )
            }
            ?: emptyList()
    }

    /*
     * REMOVE SAVED ACCOUNT DIALOG
     */
    if (accountToRemove != null) {

        AlertDialog(
            onDismissRequest = {
                accountToRemove = null
            },
            title = {
                Text(
                    text = "Remove Saved Account?",
                    color = MainText,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Text(
                    text = "This will only remove the account from your Saved Accounts list. Your account data and journals will not be deleted.",
                    color = SecondaryText
                )
            },
            confirmButton = {

                TextButton(
                    onClick = {

                        removeAccountFromSavedList(
                            context = context,
                            email = accountToRemove!!
                        )

                        savedAccountEmails =
                            getSavedAccounts(context)

                        accountToRemove = null

                        /*
                         * If the last saved account
                         * was removed, automatically
                         * show the login form.
                         */
                        if (
                            savedAccountEmails.isEmpty()
                        ) {
                            showSavedAccounts = true
                        }
                    }
                ) {
                    Text(
                        text = "REMOVE",
                        color = ErrorRed,
                        fontWeight = FontWeight.Bold
                    )
                }
            },
            dismissButton = {

                TextButton(
                    onClick = {
                        accountToRemove = null
                    }
                ) {
                    Text(
                        text = "CANCEL",
                        color = LightBlue,
                        fontWeight = FontWeight.Bold
                    )
                }
            },
            containerColor = CardColor
        )
    }

    /*
     * MAIN SCREEN
     */
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundColor)
    ) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(
                    rememberScrollState()
                )
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

            Spacer(
                modifier = Modifier.height(30.dp)
            )

            /*
             * APP TITLE
             */
            Text(
                text = "OJT",
                fontSize = 46.sp,
                fontWeight = FontWeight.ExtraBold,
                color = LightBlue
            )

            Text(
                text = "OJT Tracker",
                fontSize = 30.sp,
                fontWeight = FontWeight.Bold,
                color = MainText
            )

            Text(
                text = "by Team Balagbag",
                fontSize = 14.sp,
                color = SecondaryText
            )

            Spacer(
                modifier = Modifier.height(10.dp)
            )

            Text(
                text = "Document. Track. Reflect.",
                color = SecondaryText
            )

            Spacer(
                modifier = Modifier.height(35.dp)
            )

            /*
             * SAVED ACCOUNTS SCREEN
             *
             * Only shown when:
             * - there are saved accounts
             * - showSavedAccounts is true
             */
            if (
                showSavedAccounts &&
                savedAccounts.isNotEmpty()
            ) {

                Text(
                    text = "Saved Accounts",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = MainText,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp)
                )

                savedAccounts.forEach { account ->

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                            .clickable {

                                email = account[0]
                                password = ""
                                errorMessage = ""

                                showSavedAccounts = false
                            },
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = CardColor
                        )
                    ) {

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp),
                            verticalAlignment =
                                Alignment.CenterVertically
                        ) {

                            /*
                             * PROFILE PICTURE
                             */
                            val profileBitmap =
                                remember(account[8]) {

                                    try {

                                        context.contentResolver
                                            .openInputStream(
                                                Uri.parse(account[8])
                                            )
                                            ?.use { inputStream ->

                                                BitmapFactory
                                                    .decodeStream(
                                                        inputStream
                                                    )
                                            }

                                    } catch (
                                        e: Exception
                                    ) {
                                        null
                                    }
                                }

                            if (profileBitmap != null) {

                                Image(
                                    bitmap =
                                        profileBitmap
                                            .asImageBitmap(),
                                    contentDescription =
                                        "Profile Picture",
                                    modifier = Modifier
                                        .size(50.dp)
                                        .clip(CircleShape),
                                    contentScale =
                                        ContentScale.Crop
                                )

                            } else {

                                Box(
                                    modifier = Modifier
                                        .size(50.dp)
                                        .clip(CircleShape)
                                        .background(CardColor),
                                    contentAlignment =
                                        Alignment.Center
                                ) {

                                    Text(
                                        text = account[1]
                                            .firstOrNull()
                                            ?.uppercase()
                                            ?: "?",
                                        fontWeight =
                                            FontWeight.Bold,
                                        color = LightBlue
                                    )
                                }
                            }

                            Spacer(
                                modifier =
                                    Modifier.width(12.dp)
                            )

                            /*
                             * ACCOUNT INFORMATION
                             */
                            Column(
                                modifier =
                                    Modifier.weight(1f)
                            ) {

                                Text(
                                    text = account[1],
                                    fontWeight =
                                        FontWeight.Bold,
                                    color = MainText
                                )

                                Text(
                                    text = account[0],
                                    fontSize = 13.sp,
                                    color = SecondaryText
                                )

                                TextButton(
                                    onClick = {
                                        accountToRemove =
                                            account[0]
                                    },
                                    modifier =
                                        Modifier.fillMaxWidth()
                                ) {

                                    Text(
                                        text = "Remove",
                                        color = ErrorRed
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(
                    modifier =
                        Modifier.height(15.dp)
                )

                /*
                 * USE ANOTHER ACCOUNT
                 */
                TextButton(
                    onClick = {

                        showSavedAccounts = false
                        email = ""
                        password = ""
                        errorMessage = ""
                    },
                    modifier =
                        Modifier.fillMaxWidth()
                ) {

                    Text(
                        text = "+ Use another account",
                        color = LightBlue
                    )
                }

                Spacer(
                    modifier =
                        Modifier.height(10.dp)
                )
            }

            /*
             * LOGIN FORM
             *
             * Shown when:
             * 1. There are NO saved accounts
             * OR
             * 2. User selected "Use another account"
             */
            if (
                !showSavedAccounts ||
                savedAccounts.isEmpty()
            ) {

                /*
                 * EMAIL
                 */
                OutlinedTextField(
                    value = email,
                    onValueChange = {

                        email = it
                        errorMessage = ""
                    },
                    modifier =
                        Modifier.fillMaxWidth(),
                    label = {
                        Text("Email")
                    },
                    singleLine = true,
                    shape =
                        RoundedCornerShape(14.dp)
                )

                Spacer(
                    modifier =
                        Modifier.height(15.dp)
                )

                /*
                 * PASSWORD
                 */
                OutlinedTextField(
                    value = password,
                    onValueChange = {

                        password = it
                        errorMessage = ""
                    },
                    modifier =
                        Modifier.fillMaxWidth(),
                    label = {
                        Text("Password")
                    },
                    trailingIcon = {

                        TextButton(
                            onClick = {
                                passwordVisible =
                                    !passwordVisible
                            }
                        ) {

                            Text(
                                text =
                                    if (passwordVisible)
                                        "Hide"
                                    else
                                        "Show",
                                color = LightBlue
                            )
                        }
                    },
                    visualTransformation =
                        if (passwordVisible) {
                            VisualTransformation.None
                        } else {
                            PasswordVisualTransformation()
                        },
                    singleLine = true,
                    shape =
                        RoundedCornerShape(14.dp)
                )

                /*
                 * ERROR MESSAGE
                 */
                if (errorMessage.isNotEmpty()) {

                    Spacer(
                        modifier =
                            Modifier.height(10.dp)
                    )

                    Text(
                        text = errorMessage,
                        color = ErrorRed,
                        textAlign =
                            TextAlign.Center
                    )
                }

                Spacer(
                    modifier =
                        Modifier.height(25.dp)
                )

                /*
                 * LOGIN BUTTON
                 */
                Button(
                    onClick = {

                        when {

                            email.isBlank() ||
                                    password.isBlank() -> {

                                errorMessage =
                                    "Please enter your email and password."
                            }

                            else -> {

                                val account =
                                    findAccount(
                                        context = context,
                                        email = email.trim(),
                                        password = password
                                    )

                                if (account != null) {

                                    /*
                                     * SAVE ACCOUNT DATA
                                     */
                                    userPreferences.saveAccount(
                                        fullName = account[1],
                                        email = account[0],
                                        password = account[2],
                                        school = account[3],
                                        course = account[4],
                                        yearSection = account[5],
                                        company = account[6],
                                        requiredHours = account[7],
                                        profileImage = account[8]
                                    )

                                    userPreferences.setLoggedIn(
                                        true
                                    )

                                    /*
                                     * SET ACTIVE ACCOUNT
                                     */
                                    setActiveAccount(
                                        context = context,
                                        email = account[0]
                                    )

                                    /*
                                     * SAVE ACCOUNT TO
                                     * SAVED ACCOUNTS
                                     */
                                    saveAccountToSavedList(
                                        context = context,
                                        email = account[0]
                                    )

                                    onLoginSuccess()

                                } else {

                                    errorMessage =
                                        "Incorrect email or password."
                                }
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(55.dp),
                    shape =
                        RoundedCornerShape(14.dp),
                    colors =
                        ButtonDefaults.buttonColors(
                            containerColor =
                                PrimaryBlue
                        )
                ) {

                    Text(
                        text = "LOGIN",
                        fontWeight =
                            FontWeight.Bold
                    )
                }

                /*
                 * CREATE ACCOUNT
                 */
                Spacer(
                    modifier =
                        Modifier.height(18.dp)
                )

                TextButton(
                    onClick = onCreateAccount,
                    modifier =
                        Modifier.fillMaxWidth()
                ) {

                    Text(
                        text =
                            "Don't have an account?  Create Account",
                        color = LightBlue
                    )
                }

                Spacer(
                    modifier =
                        Modifier.height(20.dp)
                )
            }
        }
    }
}

// ============================================================
// TERMS
// ============================================================

@Composable
fun TermsScreen(
    onBack: () -> Unit,
    onAgree: () -> Unit
) {
    var agreed by remember {
        mutableStateOf(false)
    }

    BackHandler {
        onBack()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundColor)
            .padding(24.dp)
    ) {

        TextButton(onClick = onBack) {
            Text(
                text = "← Back",
                color = LightBlue
            )
        }

        Text(
            text = "Terms & Conditions",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = MainText
        )

        Spacer(modifier = Modifier.height(20.dp))

        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .background(
                    CardColor,
                    RoundedCornerShape(16.dp)
                )
                .padding(20.dp)
                .verticalScroll(rememberScrollState())
        ) {

            Text(
                text = "1. Account Information",
                fontWeight = FontWeight.Bold,
                fontSize = 17.sp,
                color = MainText
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Users are responsible for providing accurate information when creating an account.",
                color = SecondaryText
            )

            Spacer(modifier = Modifier.height(18.dp))

            Text(
                text = "2. Journal Content",
                fontWeight = FontWeight.Bold,
                fontSize = 17.sp,
                color = MainText
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Users are responsible for the journal entries, images, and other content they add.",
                color = SecondaryText
            )

            Spacer(modifier = Modifier.height(18.dp))

            Text(
                text = "3. Privacy",
                fontWeight = FontWeight.Bold,
                fontSize = 17.sp,
                color = MainText
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Personal information and journal data should be treated as private user information.",
                color = SecondaryText
            )

            Spacer(modifier = Modifier.height(18.dp))

            Text(
                text = "4. OJT Information",
                fontWeight = FontWeight.Bold,
                fontSize = 17.sp,
                color = MainText
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "The OJT hours and progress displayed are based on information entered by the user.",
                color = SecondaryText
            )
        }

        Spacer(modifier = Modifier.height(15.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {

            Checkbox(
                checked = agreed,
                onCheckedChange = {
                    agreed = it
                }
            )

            Text(
                text = "I agree to the Terms & Conditions",
                color = MainText,
                modifier = Modifier.clickable {
                    agreed = !agreed
                }
            )
        }

        Button(
            onClick = onAgree,
            enabled = agreed,
            modifier = Modifier
                .fillMaxWidth()
                .height(55.dp),
            shape = RoundedCornerShape(14.dp)
        ) {
            Text(
                text = "CONTINUE",
                fontWeight = FontWeight.Bold
            )
        }
    }
}


// ============================================================
// REGISTER
// ============================================================

@Composable
fun RegisterScreen(
    userPreferences: UserPreferences,
    onBack: () -> Unit,
    onAccountCreated: () -> Unit
) {
    val context = LocalContext.current
    BackHandler {
        onBack()
    }

    var profileImageUri by remember {
        mutableStateOf<Uri?>(null)
    }

    val imagePicker =
        rememberLauncherForActivityResult(
            ActivityResultContracts.GetContent()
        ) { uri ->
            profileImageUri = uri
        }

    var fullName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var school by remember { mutableStateOf("") }
    var course by remember { mutableStateOf("") }
    var yearSection by remember { mutableStateOf("") }
    var company by remember { mutableStateOf("") }
    var requiredHours by remember { mutableStateOf("") }

    var passwordVisible by remember { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) }

    var errorMessage by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundColor)
            .padding(24.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        TextButton(
            onClick = onBack,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "← Back",
                color = LightBlue,
                modifier = Modifier.fillMaxWidth()
            )
        }

        Text(
            text = "Create Account",
            fontSize = 30.sp,
            fontWeight = FontWeight.Bold,
            color = MainText
        )

        Text(
            text = "Start documenting your OJT journey.",
            color = SecondaryText
        )

        Spacer(modifier = Modifier.height(25.dp))

        ProfileImage(
            uri = profileImageUri
        )

        Spacer(modifier = Modifier.height(10.dp))

        TextButton(
            onClick = {
                imagePicker.launch("image/*")
            }
        ) {
            Text(
                text =
                    if (profileImageUri == null)
                        "Add Profile Picture"
                    else
                        "Change Profile Picture",
                color = LightBlue
            )
        }

        Spacer(modifier = Modifier.height(15.dp))

        RegisterField(
            value = fullName,
            label = "Full Name",
            onValueChange = { fullName = it }
        )

        Spacer(modifier = Modifier.height(15.dp))

        RegisterField(
            value = email,
            label = "Email",
            onValueChange = { email = it }
        )

        Spacer(modifier = Modifier.height(15.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Password") },
            visualTransformation =
                if (passwordVisible)
                    VisualTransformation.None
                else
                    PasswordVisualTransformation(),
            trailingIcon = {
                TextButton(
                    onClick = {
                        passwordVisible = !passwordVisible
                    }
                ) {
                    Text(
                        text =
                            if (passwordVisible) "Hide"
                            else "Show",
                        color = LightBlue
                    )
                }
            },
            singleLine = true,
            shape = RoundedCornerShape(14.dp)
        )

        Spacer(modifier = Modifier.height(15.dp))

        OutlinedTextField(
            value = confirmPassword,
            onValueChange = { confirmPassword = it },
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Confirm Password") },
            visualTransformation =
                if (confirmPasswordVisible)
                    VisualTransformation.None
                else
                    PasswordVisualTransformation(),
            trailingIcon = {
                TextButton(
                    onClick = {
                        confirmPasswordVisible =
                            !confirmPasswordVisible
                    }
                ) {
                    Text(
                        text =
                            if (confirmPasswordVisible) "Hide"
                            else "Show",
                        color = LightBlue
                    )
                }
            },
            singleLine = true,
            shape = RoundedCornerShape(14.dp)
        )

        Spacer(modifier = Modifier.height(25.dp))

        Text(
            text = "Student Information",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = MainText,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(15.dp))

        RegisterField(
            value = school,
            label = "School",
            onValueChange = { school = it }
        )

        Spacer(modifier = Modifier.height(15.dp))

        RegisterField(
            value = course,
            label = "Course / Program",
            onValueChange = { course = it }
        )

        Spacer(modifier = Modifier.height(15.dp))

        RegisterField(
            value = yearSection,
            label = "Year & Section",
            onValueChange = { yearSection = it }
        )

        Spacer(modifier = Modifier.height(25.dp))

        Text(
            text = "OJT Information",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = MainText,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(15.dp))

        RegisterField(
            value = company,
            label = "OJT Company",
            onValueChange = { company = it }
        )

        Spacer(modifier = Modifier.height(15.dp))

        RegisterField(
            value = requiredHours,
            label = "Required OJT Hours",
            onValueChange = { requiredHours = it }
        )

        if (errorMessage.isNotEmpty()) {

            Spacer(modifier = Modifier.height(15.dp))

            Text(
                text = errorMessage,
                color = ErrorRed,
                textAlign = TextAlign.Center
            )
        }

        Spacer(modifier = Modifier.height(25.dp))

        Button(
            onClick = {

                when {

                    fullName.isBlank() ||
                            email.isBlank() ||
                            password.isBlank() ||
                            confirmPassword.isBlank() ||
                            school.isBlank() ||
                            course.isBlank() ||
                            yearSection.isBlank() ||
                            company.isBlank() ||
                            requiredHours.isBlank() -> {

                        errorMessage =
                            "Please complete all fields."
                    }

                    !email.contains("@") -> {

                        errorMessage =
                            "Please enter a valid email."
                    }

                    password.length < 6 -> {

                        errorMessage =
                            "Password must be at least 6 characters."
                    }

                    password != confirmPassword -> {

                        errorMessage =
                            "Passwords do not match."
                    }

                    requiredHours.toDoubleOrNull() == null ||
                            requiredHours.toDouble() <= 0 -> {

                        errorMessage =
                            "Enter a valid OJT hour requirement."
                    }

                    else -> {
                        val savedProfileImage = profileImageUri?.let {
                            copyProfileImageToInternalStorage(context, it)
                        } ?: ""

                        userPreferences.saveAccount(
                            fullName = fullName.trim(),
                            email = email.trim(),
                            password = password,
                            school = school.trim(),
                            course = course.trim(),
                            yearSection = yearSection.trim(),
                            company = company.trim(),
                            requiredHours = requiredHours.trim(),
                            profileImage = profileImageUri?.toString() ?: ""
                        )

                        saveAccountToList(
                            context = context,
                            userPreferences = userPreferences
                        )

                        setActiveAccount(
                            context = context,
                            email = email.trim()
                        )

                        onAccountCreated()
                    }
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(55.dp),
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = PrimaryBlue
            )
        ) {

            Text(
                text = "CREATE ACCOUNT",
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(30.dp))
    }
}

@Composable
fun RegisterField(
    value: String,
    label: String,
    onValueChange: (String) -> Unit
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = Modifier.fillMaxWidth(),
        label = {
            Text(label)
        },
        singleLine = true,
        shape = RoundedCornerShape(14.dp)
    )
}


// ============================================================
// DASHBOARD
// ============================================================

@Composable
fun DashboardScreen(
    userPreferences: UserPreferences,
    onLogout: () -> Unit,
    onJournal: () -> Unit,
    onProfile: () -> Unit,
    onSummary: () -> Unit
) {
    var showExitDialog by remember { mutableStateOf(false) }
    val context = LocalContext.current

    BackHandler {
        showExitDialog = true
    }

    if (showExitDialog) {
        AlertDialog(
            onDismissRequest = {
                showExitDialog = false
            },
            title = {
                Text(
                    "Exit OJT Tracker?",
                    color = MainText,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Text(
                    "Are you sure you want to exit the application?",
                    color = SecondaryText
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        if (context is Activity) {
                            context.finish()
                        }
                    }
                ) {
                    Text(
                        "EXIT",
                        color = ErrorRed,
                        fontWeight = FontWeight.Bold
                    )
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showExitDialog = false
                    }
                ) {
                    Text(
                        "CANCEL",
                        color = LightBlue,
                        fontWeight = FontWeight.Bold
                    )
                }
            },
            containerColor = CardColor
        )
    }

    val fullName = userPreferences.getFullName()
    val company = userPreferences.getCompany()
    var requiredHours by remember {
        mutableStateOf(userPreferences.getRequiredHours())
    }
    val profileImage = userPreferences.getProfileImage()
    var showEditGoalDialog by remember {
        mutableStateOf(false)
    }

    var editedGoal by remember {
        mutableStateOf(requiredHours)
    }

    var goalMessage by remember {
        mutableStateOf("")
    }

    val journals = remember {
        loadJournalEntries(context)
    }

    val completedHours =
        journals.sumOf {
            it.hours
        }

    val required =
        requiredHours.toDoubleOrNull() ?: 0.0

    val remaining =
        (required - completedHours).coerceAtLeast(0.0)

    // ========================================================
    // NEW PROGRESS CALCULATION
    // ========================================================

    val progress =
        if (required > 0) {
            (completedHours / required).coerceIn(0.0, 1.0)
        } else {
            0.0
        }

    val progressPercentage =
        progress * 100.0

    val progressStatus =
        when {
            progress >= 1.0 ->
                "OJT COMPLETED! 🎉"

            progress >= 0.75 ->
                "Almost there! 💪"

            progress >= 0.50 ->
                "Halfway there! 🔥"

            progress > 0.0 ->
                "OJT in progress..."

            else ->
                "No hours recorded yet."
        }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundColor)
            .padding(24.dp)
            .verticalScroll(rememberScrollState())
    ) {

        Spacer(modifier = Modifier.height(20.dp))

        // ====================================================
        // STUDENT HEADER
        // ====================================================

        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {

            ProfileImage(
                uri =
                    if (profileImage.isNotEmpty())
                        Uri.parse(profileImage)
                    else
                        null,
                size = 75
            )

            Spacer(modifier = Modifier.width(15.dp))

            Column {

                Text(
                    text = "Good day!",
                    color = SecondaryText
                )

                Text(
                    text = fullName.ifEmpty {
                        "OJT Student"
                    },
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = MainText
                )

                Text(
                    text = company,
                    color = SecondaryText
                )
            }
        }

        Spacer(modifier = Modifier.height(25.dp))

        // ====================================================
        // NEW OJT PROGRESS CARD
        // ====================================================

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = CardColor
            ),
            shape = RoundedCornerShape(20.dp)
        ) {

            Column(
                modifier = Modifier.padding(22.dp)
            ) {

                Text(
                    text = "OJT PROGRESS",
                    color = LightBlue,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp
                )

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.Bottom
                ) {

                    Text(
                        text = formatHours(completedHours),
                        fontSize = 30.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = MainText
                    )

                    Text(
                        text = " / ${formatHours(required)} hours",
                        fontSize = 16.sp,
                        color = SecondaryText,
                        modifier = Modifier.padding(bottom = 4.dp)
                    )
                }

                Spacer(modifier = Modifier.height(15.dp))

                LinearProgressIndicator(
                    progress = {
                        progress.toFloat()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(10.dp)
                        .clip(RoundedCornerShape(10.dp)),
                    color = LightBlue,
                    trackColor = Color(0xFF252B33)
                )

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {

                    Text(
                        text = "${
                            String.format(
                                Locale.getDefault(),
                                "%.1f",
                                progressPercentage
                            )
                        }% completed",
                        color = LightBlue,
                        fontWeight = FontWeight.Bold
                    )

                    Text(
                        text = "${formatHours(remaining)} hrs left",
                        color = SecondaryText
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = progressStatus,
                    color =
                        if (progress >= 1.0)
                            GreenColor
                        else
                            SecondaryText,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(15.dp))

                OutlinedButton(
                    onClick = {
                        editedGoal = requiredHours
                        goalMessage = ""
                        showEditGoalDialog = true
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = "EDIT OJT GOAL",
                        color = LightBlue,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        if (showEditGoalDialog) {

            AlertDialog(
                onDismissRequest = {
                    showEditGoalDialog = false
                },

                title = {
                    Text(
                        text = "Edit OJT Goal",
                        color = MainText,
                        fontWeight = FontWeight.Bold
                    )
                },

                text = {

                    Column {

                        Text(
                            text = "Set your required OJT hours.",
                            color = SecondaryText
                        )

                        Spacer(
                            modifier = Modifier.height(15.dp)
                        )

                        OutlinedTextField(
                            value = editedGoal,
                            onValueChange = {
                                editedGoal = it
                                goalMessage = ""
                            },
                            label = {
                                Text("Required OJT Hours")
                            },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Decimal
                            )
                        )

                        if (goalMessage.isNotEmpty()) {

                            Spacer(
                                modifier = Modifier.height(8.dp)
                            )

                            Text(
                                text = goalMessage,
                                color = Color.Red
                            )
                        }
                    }
                },

                confirmButton = {

                    TextButton(
                        onClick = {

                            val validGoal =
                                editedGoal.toDoubleOrNull()

                            if (
                                validGoal == null ||
                                validGoal <= 0
                            ) {

                                goalMessage =
                                    "Enter a valid OJT hour goal."

                            } else {

                                userPreferences.saveAccount(
                                    fullName =
                                        userPreferences.getFullName(),

                                    email =
                                        userPreferences.getEmail(),

                                    password =
                                        getSavedPassword(context),

                                    school =
                                        userPreferences.getSchool(),

                                    course =
                                        userPreferences.getCourse(),

                                    yearSection =
                                        userPreferences.getYearSection(),

                                    company =
                                        userPreferences.getCompany(),

                                    requiredHours =
                                        editedGoal.trim(),

                                    profileImage =
                                        userPreferences.getProfileImage()
                                )
                                // Update the saved account record
                                saveAccountToList(
                                    context = context,
                                    userPreferences = userPreferences
                                )


                                requiredHours = editedGoal.trim()

                                showEditGoalDialog = false
                            }
                        }
                    ) {
                        Text(
                            text = "SAVE",
                            color = GreenColor,
                            fontWeight = FontWeight.Bold
                        )
                    }
                },

                dismissButton = {

                    TextButton(
                        onClick = {
                            showEditGoalDialog = false
                        }
                    ) {
                        Text(
                            text = "CANCEL",
                            color = SecondaryText
                        )
                    }
                }
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        // ====================================================
        // ADD JOURNAL
        // ====================================================

        Button(
            onClick = onJournal,
            modifier = Modifier
                .fillMaxWidth()
                .height(55.dp),
            shape = RoundedCornerShape(14.dp)
        ) {

            Text(
                text = "+ ADD JOURNAL",
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // ====================================================
        // VIEW JOURNALS
        // ====================================================

        OutlinedButton(
            onClick = onJournal,
            modifier = Modifier
                .fillMaxWidth()
                .height(55.dp),
            shape = RoundedCornerShape(14.dp)
        ) {

            Text(
                text = "VIEW JOURNALS",
                color = LightBlue
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // ====================================================
        // EDIT PROFILE
        // ====================================================

        OutlinedButton(
            onClick = onProfile,
            modifier = Modifier
                .fillMaxWidth()
                .height(55.dp),
            shape = RoundedCornerShape(14.dp)
        ) {

            Text(
                text = "EDIT PROFILE",
                color = LightBlue
            )
        }

        Spacer(modifier = Modifier.height(25.dp))

// SUMMARY BUTTON


        Spacer(modifier = Modifier.height(12.dp))

        OutlinedButton(
            onClick = onSummary,
            modifier = Modifier
                .fillMaxWidth()
                .height(55.dp),
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.outlinedButtonColors(
                containerColor = Color(0xFF90EE90)
            )
        ) {
            Text(
                text = "📊 VIEW SUMMARY",
                color = Color(0xFF0D1117),
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(25.dp))

        // ====================================================
        // LOGOUT
        // ====================================================

        var showLogoutDialog by remember { mutableStateOf(false) }

        Button(
            onClick = {
                showLogoutDialog = true
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF2A3038)
            ),
            shape = RoundedCornerShape(14.dp)
        ) {

            Text(
                text = "LOGOUT",
                color = MainText,
                fontWeight = FontWeight.Bold
            )
        }

        if (showLogoutDialog) {

            AlertDialog(
                onDismissRequest = {
                    showLogoutDialog = false
                },

                title = {
                    Text(
                        text = "Logout?",
                        color = MainText,
                        fontWeight = FontWeight.Bold
                    )
                },

                text = {
                    Text(
                        text = "Are you sure you want to logout?",
                        color = SecondaryText
                    )
                },

                confirmButton = {

                    TextButton(
                        onClick = {
                            showLogoutDialog = false
                            onLogout()
                        }
                    ) {

                        Text(
                            text = "LOGOUT",
                            color = ErrorRed,
                            fontWeight = FontWeight.Bold
                        )
                    }
                },

                dismissButton = {

                    TextButton(
                        onClick = {
                            showLogoutDialog = false
                        }
                    ) {

                        Text(
                            text = "CANCEL",
                            color = LightBlue,
                            fontWeight = FontWeight.Bold
                        )
                    }
                },

                containerColor = CardColor
            )
        }
    }
}

// ============================================================
// OJT SUMMARY
// ============================================================

@Composable
fun SummaryScreen(
    userPreferences: UserPreferences,
    onBack: () -> Unit
) {
    val context = LocalContext.current

    BackHandler {
        onBack()
    }

    val journals = remember {
        loadJournalEntries(context)
    }

    val completedHours = journals.sumOf {
        it.hours
    }

    val requiredHours =
        userPreferences.getRequiredHours()
            .toDoubleOrNull() ?: 0.0

    val remainingHours =
        (requiredHours - completedHours)
            .coerceAtLeast(0.0)

    val progress =
        if (requiredHours > 0) {
            (completedHours / requiredHours)
                .coerceIn(0.0, 1.0)
        } else {
            0.0
        }

    val percentage = progress * 100

    val status = when {
        progress >= 1.0 ->
            "OJT COMPLETED! 🎉"

        progress >= 0.75 ->
            "Almost there! 💪"

        progress >= 0.50 ->
            "Halfway there! 🔥"

        progress > 0 ->
            "OJT in progress..."

        else ->
            "No hours recorded yet."
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundColor)
            .padding(24.dp)
            .verticalScroll(
                rememberScrollState()
            )
    ) {

        TextButton(
            onClick = onBack
        ) {
            Text(
                text = "← Back",
                color = LightBlue
            )
        }

        Text(
            text = "OJT Summary",
            fontSize = 30.sp,
            fontWeight = FontWeight.Bold,
            color = MainText
        )

        Text(
            text = "Your overall OJT progress",
            color = SecondaryText
        )

        Spacer(
            modifier = Modifier.height(25.dp)
        )

        // STUDENT INFO
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = CardColor
            ),
            shape = RoundedCornerShape(18.dp)
        ) {

            Column(
                modifier = Modifier.padding(20.dp)
            ) {

                Text(
                    text = "STUDENT INFORMATION",
                    color = LightBlue,
                    fontWeight = FontWeight.Bold
                )

                Spacer(
                    modifier = Modifier.height(12.dp)
                )

                SummaryRow(
                    "Name",
                    userPreferences.getFullName()
                )

                SummaryRow(
                    "School",
                    userPreferences.getSchool()
                )

                SummaryRow(
                    "Course",
                    userPreferences.getCourse()
                )

                SummaryRow(
                    "Year & Section",
                    userPreferences.getYearSection()
                )

                SummaryRow(
                    "OJT Company",
                    userPreferences.getCompany()
                )
            }
        }

        Spacer(
            modifier = Modifier.height(15.dp)
        )

        // HOURS SUMMARY
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = CardColor
            ),
            shape = RoundedCornerShape(18.dp)
        ) {

            Column(
                modifier = Modifier.padding(20.dp)
            ) {

                Text(
                    text = "HOURS SUMMARY",
                    color = LightBlue,
                    fontWeight = FontWeight.Bold
                )

                Spacer(
                    modifier = Modifier.height(15.dp)
                )

                SummaryRow(
                    "Completed Hours",
                    "${formatHours(completedHours)} hrs"
                )

                SummaryRow(
                    "Required Hours",
                    "${formatHours(requiredHours)} hrs"
                )

                SummaryRow(
                    "Remaining Hours",
                    "${formatHours(remainingHours)} hrs"
                )

                SummaryRow(
                    "Journal Entries",
                    journals.size.toString()
                )
            }
        }

        Spacer(
            modifier = Modifier.height(15.dp)
        )

        // PROGRESS
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = CardColor
            ),
            shape = RoundedCornerShape(18.dp)
        ) {

            Column(
                modifier = Modifier.padding(20.dp)
            ) {

                Text(
                    text = "OVERALL PROGRESS",
                    color = LightBlue,
                    fontWeight = FontWeight.Bold
                )

                Spacer(
                    modifier = Modifier.height(15.dp)
                )

                LinearProgressIndicator(
                    progress = {
                        progress.toFloat()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(12.dp)
                        .clip(
                            RoundedCornerShape(10.dp)
                        ),
                    color = LightBlue,
                    trackColor = Color(0xFF252B33)
                )

                Spacer(
                    modifier = Modifier.height(12.dp)
                )

                Text(
                    text = String.format(
                        Locale.getDefault(),
                        "%.1f%% completed",
                        percentage
                    ),
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = MainText
                )

                Spacer(
                    modifier = Modifier.height(8.dp)
                )

                Text(
                    text = status,
                    color =
                        if (progress >= 1.0)
                            GreenColor
                        else
                            SecondaryText,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Spacer(
            modifier = Modifier.height(20.dp)
        )

        Text(
            text = "Keep going and continue documenting your OJT journey! 💙",
            color = SecondaryText,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(
            modifier = Modifier.height(20.dp)
        )
    }
}

@Composable
fun SummaryRow(
    label: String,
    value: String
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
    ) {

        Text(
            text = label,
            color = SecondaryText,
            fontSize = 13.sp
        )

        Text(
            text = value.ifEmpty {
                "Not provided"
            },
            color = MainText,
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium
        )
    }
}

// ============================================================
// JOURNAL LIST
// ============================================================

@Composable
fun JournalListScreen(
    onBack: () -> Unit
) {
    val context = LocalContext.current

    var showAddJournal by remember { mutableStateOf(false) }
    var selectedJournal by remember { mutableStateOf<JournalEntry?>(null) }
    var journalToEdit by remember { mutableStateOf<JournalEntry?>(null) }

    // ========================================================
    // SEARCH
    // ========================================================

    var searchQuery by remember {
        mutableStateOf("")
    }

    BackHandler {
        when {
            showAddJournal || journalToEdit != null -> {
                showAddJournal = false
                journalToEdit = null
            }

            selectedJournal != null -> {
                selectedJournal = null
            }

            else -> onBack()
        }
    }

    var journals by remember {
        mutableStateOf(
            loadJournalEntries(context)
        )
    }

    // ========================================================
    // ADD / EDIT JOURNAL
    // ========================================================

    if (showAddJournal || journalToEdit != null) {

        JournalEditorScreen(
            existingJournal = journalToEdit,

            onBack = {
                showAddJournal = false
                journalToEdit = null
            },

            onSaved = {
                journals = loadJournalEntries(context)

                showAddJournal = false
                journalToEdit = null
            }
        )

        return
    }

    // ========================================================
    // FILTERED JOURNALS
    // ========================================================

    val filteredJournals =
        journals
            .sortedByDescending {
                it.id
            }
            .filter { journal ->

                if (searchQuery.isBlank()) {
                    true
                } else {

                    journal.title.contains(
                        searchQuery,
                        ignoreCase = true
                    ) ||

                            journal.content.contains(
                                searchQuery,
                                ignoreCase = true
                            ) ||

                            journal.date.contains(
                                searchQuery,
                                ignoreCase = true
                            ) ||

                            journal.time.contains(
                                searchQuery,
                                ignoreCase = true
                            )
                }
            }

    // ========================================================
    // JOURNAL LIST
    // ========================================================

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundColor)
            .padding(20.dp)
    ) {

        // ====================================================
        // HEADER
        // ====================================================

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {

            TextButton(
                onClick = onBack
            ) {

                Text(
                    text = "← Back",
                    color = LightBlue
                )
            }

            Text(
                text = "My Journals",
                fontSize = 25.sp,
                fontWeight = FontWeight.Bold,
                color = MainText,
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(
            modifier = Modifier.height(10.dp)
        )

        // ====================================================
        // SEARCH BAR
        // ====================================================

        OutlinedTextField(
            value = searchQuery,

            onValueChange = {
                searchQuery = it
            },

            modifier = Modifier.fillMaxWidth(),

            label = {
                Text("Search Journals")
            },

            placeholder = {
                Text(
                    "Search title, content, date..."
                )
            },

            singleLine = true,

            shape = RoundedCornerShape(14.dp),

            trailingIcon = {

                if (searchQuery.isNotEmpty()) {

                    TextButton(
                        onClick = {
                            searchQuery = ""
                        }
                    ) {

                        Text(
                            text = "✕",
                            color = LightBlue,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        )

        Spacer(
            modifier = Modifier.height(12.dp)
        )

        // ====================================================
        // NEW JOURNAL BUTTON
        // ====================================================

        Button(
            onClick = {

                journalToEdit = null
                showAddJournal = true

            },

            modifier = Modifier.fillMaxWidth(),

            shape = RoundedCornerShape(14.dp)
        ) {

            Text(
                text = "+ NEW JOURNAL",
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(
            modifier = Modifier.height(15.dp)
        )

        // ====================================================
        // SEARCH RESULT INFO
        // ====================================================

        if (searchQuery.isNotBlank()) {

            Text(
                text =
                    "${filteredJournals.size} journal(s) found",

                color = SecondaryText,

                fontSize = 13.sp
            )

            Spacer(
                modifier = Modifier.height(10.dp)
            )
        }

        // ====================================================
        // NO JOURNALS
        // ====================================================

        if (journals.isEmpty()) {

            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {

                Column(
                    horizontalAlignment =
                        Alignment.CenterHorizontally
                ) {

                    Text(
                        text = "📝",
                        fontSize = 55.sp
                    )

                    Text(
                        text = "No journals yet",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = MainText
                    )

                    Text(
                        text =
                            "Start documenting your OJT experience.",
                        color = SecondaryText
                    )
                }
            }

            // ====================================================
            // NO SEARCH RESULTS
            // ====================================================

        } else if (filteredJournals.isEmpty()) {

            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {

                Column(
                    horizontalAlignment =
                        Alignment.CenterHorizontally
                ) {

                    Text(
                        text = "🔍",
                        fontSize = 50.sp
                    )

                    Spacer(
                        modifier = Modifier.height(10.dp)
                    )

                    Text(
                        text = "No journals found",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = MainText
                    )

                    Text(
                        text =
                            "Try searching with another keyword.",
                        color = SecondaryText,
                        textAlign = TextAlign.Center
                    )

                    Spacer(
                        modifier = Modifier.height(12.dp)
                    )

                    TextButton(
                        onClick = {
                            searchQuery = ""
                        }
                    ) {

                        Text(
                            text = "CLEAR SEARCH",
                            color = LightBlue,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            // ====================================================
            // JOURNAL RESULTS
            // ====================================================

        } else {

            LazyColumn(
                modifier = Modifier.fillMaxSize(),

                verticalArrangement =
                    Arrangement.spacedBy(12.dp)
            ) {

                items(
                    filteredJournals
                ) { journal ->

                    JournalCard(
                        journal = journal,

                        onClick = {
                            selectedJournal = journal
                        }
                    )
                }
            }
        }
    }

    // ========================================================
    // JOURNAL DETAIL
    // ========================================================

    if (selectedJournal != null) {

        JournalDetailDialog(

            journal = selectedJournal!!,

            onDismiss = {
                selectedJournal = null
            },

            onEdit = { journal ->

                selectedJournal = null
                journalToEdit = journal
            },

            onDelete = {

                deleteJournal(
                    context,
                    selectedJournal!!.id
                )

                journals =
                    loadJournalEntries(context)

                selectedJournal = null
            }
        )
    }
}


// ============================================================
// JOURNAL EDITOR
// ============================================================

@Composable
fun JournalEditorScreen(
    existingJournal: JournalEntry?,
    onBack: () -> Unit,
    onSaved: () -> Unit
) {
    val context = LocalContext.current
    val isEditing = existingJournal != null
    val draftKey =
        if (isEditing)
            "edit_${existingJournal!!.id}"
        else
            "new"

    val draft = remember {
        loadJournalDraft(context, draftKey)
    }

    var title by remember {
        mutableStateOf(
            draft?.first
                ?: existingJournal?.title
                ?: ""
        )
    }

    var content by remember {
        mutableStateOf(
            draft?.second
                ?: existingJournal?.content
                ?: ""
        )
    }

    var time by remember {
        mutableStateOf(
            draft?.third
                ?: existingJournal?.time
                ?: SimpleDateFormat(
                    "hh:mm a",
                    Locale.getDefault()
                ).format(Date())
        )
    }

    var hours by remember {
        mutableStateOf(
            draft?.fourth
                ?: existingJournal?.hours?.toString()
                ?: ""
        )
    }

    var selectedImages by remember {
        mutableStateOf(
            if (!draft?.fifth.isNullOrBlank()) {
                getImageUris(draft!!.fifth)
            } else {
                getImageUris(
                    existingJournal?.imageUri ?: ""
                )
            }
        )
    }

    var errorMessage by remember {
        mutableStateOf("")
    }

    val imagePicker =
        rememberLauncherForActivityResult(
            ActivityResultContracts.OpenMultipleDocuments()
        ) { uris ->

            uris.forEach { uri ->

                try {
                    context.contentResolver
                        .takePersistableUriPermission(
                            uri,
                            Intent.FLAG_GRANT_READ_URI_PERMISSION
                        )
                } catch (_: Exception) {
                }
            }

            selectedImages =
                (selectedImages + uris).distinct()

            saveJournalDraft(
                context,
                draftKey,
                title,
                content,
                time,
                hours,
                selectedImages
            )
        }

    LaunchedEffect(
        title,
        content,
        time,
        hours,
        selectedImages
    ) {

        saveJournalDraft(
            context,
            draftKey,
            title,
            content,
            time,
            hours,
            selectedImages
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundColor)
            .padding(20.dp)
            .verticalScroll(rememberScrollState())
    ) {

        TextButton(onClick = onBack) {
            Text(
                "← Back",
                color = LightBlue
            )
        }

        Text(
            text =
                if (isEditing)
                    "Edit Journal"
                else
                    "Daily Journal",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = MainText
        )

        Text(
            text =
                if (isEditing)
                    "Update your OJT journal entry."
                else
                    "Record what happened during your OJT today.",
            color = SecondaryText
        )

        Spacer(modifier = Modifier.height(20.dp))

        JournalField(
            title,
            "Journal Title"
        ) {
            title = it
        }

        Spacer(modifier = Modifier.height(15.dp))

        JournalField(
            time,
            "Time"
        ) {
            time = it
        }

        Spacer(modifier = Modifier.height(15.dp))

        JournalField(
            hours,
            "OJT Hours"
        ) {
            hours = it
        }

        Spacer(modifier = Modifier.height(15.dp))

        OutlinedTextField(
            value = content,
            onValueChange = {
                content = it
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp),
            label = {
                Text("What happened today?")
            },
            shape = RoundedCornerShape(14.dp)
        )

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = "Journal Images",
            fontSize = 19.sp,
            fontWeight = FontWeight.Bold,
            color = MainText
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "${selectedImages.size} image(s) selected",
            color = SecondaryText
        )

        Spacer(modifier = Modifier.height(10.dp))

        OutlinedButton(
            onClick = {
                imagePicker.launch(
                    arrayOf("image/*")
                )
            },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp)
        ) {
            Text(
                "+ ADD IMAGES",
                color = LightBlue
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        selectedImages.forEachIndexed { index, uri ->

            SelectedImageRow(
                uri = uri,
                index = index,
                onRemove = {

                    selectedImages =
                        selectedImages.filterIndexed {
                                i, _ ->
                            i != index
                        }
                }
            )

            Spacer(
                modifier = Modifier.height(8.dp)
            )
        }

        if (errorMessage.isNotEmpty()) {

            Text(
                text = errorMessage,
                color = ErrorRed,
                modifier = Modifier.padding(top = 10.dp)
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        Button(
            onClick = {

                val parsedHours =
                    hours.toDoubleOrNull()

                when {

                    title.isBlank() -> {
                        errorMessage =
                            "Please enter a journal title."
                    }

                    content.isBlank() -> {
                        errorMessage =
                            "Please write something in your journal."
                    }

                    parsedHours == null ||
                            parsedHours <= 0 -> {
                        errorMessage =
                            "Enter valid OJT hours."
                    }

                    else -> {

                        val imageString =
                            selectedImages.joinToString("||") {
                                Uri.encode(it.toString())
                            }

                        val journal =
                            JournalEntry(
                                id =
                                    existingJournal?.id
                                        ?: System.currentTimeMillis(),

                                date =
                                    existingJournal?.date
                                        ?: SimpleDateFormat(
                                            "MMMM dd, yyyy",
                                            Locale.getDefault()
                                        ).format(Date()),

                                time = time.trim(),

                                title = title.trim(),

                                content = content.trim(),

                                hours = parsedHours,

                                imageUri = imageString
                            )

                        if (isEditing) {
                            updateJournal(
                                context,
                                journal
                            )
                        } else {
                            saveJournal(
                                context,
                                journal
                            )
                        }

                        clearJournalDraft(
                            context,
                            draftKey
                        )

                        onSaved()
                    }
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(55.dp),
            shape = RoundedCornerShape(14.dp)
        ) {

            Text(
                text =
                    if (isEditing)
                        "SAVE CHANGES"
                    else
                        "SAVE JOURNAL",
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(30.dp))
    }
}

@Composable
fun JournalField(
    value: String,
    label: String,
    onValueChange: (String) -> Unit
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = Modifier.fillMaxWidth(),
        label = {
            Text(label)
        },
        shape = RoundedCornerShape(14.dp)
    )
}


// ============================================================
// JOURNAL CARD
// ============================================================

@Composable
fun JournalCard(
    journal: JournalEntry,
    onClick: () -> Unit
) {
    val imageList =
        getImageUris(journal.imageUri)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                onClick()
            },
        colors = CardDefaults.cardColors(
            containerColor = CardColor
        ),
        shape = RoundedCornerShape(18.dp)
    ) {

        Column(
            modifier = Modifier.padding(18.dp)
        ) {

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {

                Column(
                    modifier = Modifier.weight(1f)
                ) {

                    Text(
                        text = journal.title,
                        fontSize = 19.sp,
                        fontWeight = FontWeight.Bold,
                        color = MainText
                    )

                    Spacer(
                        modifier = Modifier.height(4.dp)
                    )

                    Text(
                        text = journal.date,
                        color = SecondaryText
                    )
                }

                Text(
                    text = "${journal.hours} hrs",
                    color = GreenColor,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = journal.time,
                color = LightBlue,
                fontSize = 13.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = journal.content,
                color = SecondaryText,
                maxLines = 3
            )

            if (imageList.isNotEmpty()) {

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "📷 ${imageList.size} image(s)",
                    color = LightBlue,
                    fontSize = 13.sp
                )
            }
        }
    }
}


// ============================================================
// EXPORT WORD
// ============================================================

private fun escapeHtml(
    value: String
): String {
    return value
        .replace("&", "&amp;")
        .replace("<", "&lt;")
        .replace(">", "&gt;")
        .replace("\"", "&quot;")
        .replace("'", "&#39;")
        .replace("\n", "<br>")
}

private fun bitmapToDataUri(
    context: Context,
    uri: Uri
): String? {

    return try {

        context.contentResolver
            .openInputStream(uri)
            ?.use { input ->

                val bitmap =
                    BitmapFactory.decodeStream(input)
                        ?: return null

                val output =
                    java.io.ByteArrayOutputStream()

                bitmap.compress(
                    Bitmap.CompressFormat.JPEG,
                    88,
                    output
                )

                bitmap.recycle()

                "data:image/jpeg;base64," +
                        Base64.encodeToString(
                            output.toByteArray(),
                            Base64.NO_WRAP
                        )
            }

    } catch (_: Exception) {
        null
    }
}

private fun exportJournalAsWord(
    context: Context,
    uri: Uri,
    journal: JournalEntry
) {

    try {

        val prefs =
            context.getSharedPreferences(
                "OJT_TRACKER_PREFS",
                Context.MODE_PRIVATE
            )

        val fullName =
            prefs.getString(
                "full_name",
                ""
            ) ?: ""

        val school =
            prefs.getString(
                "school",
                ""
            ) ?: ""

        val course =
            prefs.getString(
                "course",
                ""
            ) ?: ""

        val yearSection =
            prefs.getString(
                "year_section",
                ""
            ) ?: ""

        val company =
            prefs.getString(
                "company",
                ""
            ) ?: ""

        val html =
            StringBuilder()

        html.append(
            "<html><head>" +
                    "<meta charset='UTF-8'>" +
                    "<style>"
        )

        html.append(
            "body{" +
                    "font-family:Arial,sans-serif;" +
                    "margin:40px;" +
                    "color:#222;" +
                    "}" +
                    "h1{text-align:center;}" +
                    "h2{color:#1976D2;}" +
                    "table{" +
                    "width:100%;" +
                    "border-collapse:collapse;" +
                    "}" +
                    "td{" +
                    "padding:7px;" +
                    "border-bottom:1px solid #ddd;" +
                    "}" +
                    "img{" +
                    "max-width:100%;" +
                    "height:auto;" +
                    "margin:10px 0;" +
                    "}"
        )

        html.append(
            "</style></head><body>"
        )

        html.append(
            "<h1>OJT JOURNAL</h1>"
        )

        html.append(
            "<h2>Student Information</h2>" +
                    "<table>"
        )

        html.append(
            "<tr><td><b>Full Name</b></td>" +
                    "<td>${escapeHtml(fullName)}</td></tr>"
        )

        html.append(
            "<tr><td><b>School</b></td>" +
                    "<td>${escapeHtml(school)}</td></tr>"
        )

        html.append(
            "<tr><td><b>Course / Program</b></td>" +
                    "<td>${escapeHtml(course)}</td></tr>"
        )

        html.append(
            "<tr><td><b>Year & Section</b></td>" +
                    "<td>${escapeHtml(yearSection)}</td></tr>"
        )

        html.append(
            "<tr><td><b>OJT Company</b></td>" +
                    "<td>${escapeHtml(company)}</td></tr>"
        )

        html.append("</table>")

        html.append(
            "<h2>Journal Entry</h2><table>"
        )

        html.append(
            "<tr><td><b>Date</b></td>" +
                    "<td>${escapeHtml(journal.date)}</td></tr>"
        )

        html.append(
            "<tr><td><b>Time</b></td>" +
                    "<td>${escapeHtml(journal.time)}</td></tr>"
        )

        html.append(
            "<tr><td><b>OJT Hours</b></td>" +
                    "<td>${journal.hours}</td></tr>"
        )

        html.append(
            "<tr><td><b>Title</b></td>" +
                    "<td>${escapeHtml(journal.title)}</td></tr>"
        )

        html.append("</table>")

        html.append(
            "<h2>Journal Content</h2>" +
                    "<p>${escapeHtml(journal.content)}</p>"
        )

        val images =
            getImageUris(journal.imageUri)

        if (images.isNotEmpty()) {

            html.append(
                "<h2>Attached Images</h2>"
            )

            images.forEach { imageUri ->

                val dataUri =
                    bitmapToDataUri(
                        context,
                        imageUri
                    )

                if (dataUri != null) {

                    html.append(
                        "<img src='$dataUri'>"
                    )
                }
            }
        }

        html.append(
            "<hr>" +
                    "<p><i>" +
                    "Generated by OJT Tracker by Team Balagbag" +
                    "</i></p>"
        )

        html.append(
            "</body></html>"
        )

        context.contentResolver
            .openOutputStream(uri)
            ?.use { output: OutputStream ->

                output.write(
                    html.toString()
                        .toByteArray(Charsets.UTF_8)
                )
            }

    } catch (_: Exception) {
    }
}


// ============================================================
// JOURNAL DETAIL
// ============================================================

@Composable
fun JournalDetailDialog(
    journal: JournalEntry,
    onDismiss: () -> Unit,
    onEdit: (JournalEntry) -> Unit,
    onDelete: () -> Unit
) {

    val context = LocalContext.current

    val imageList =
        getImageUris(journal.imageUri)

    // DELETE WARNING
    var showDeleteWarning by remember {
        mutableStateOf(false)
    }

    // FULL SCREEN IMAGE
    var fullScreenImageIndex by remember {
        mutableStateOf<Int?>(null)
    }

    // EXPORT WORD
    val exportLauncher =
        rememberLauncherForActivityResult(
            contract =
                ActivityResultContracts
                    .CreateDocument(
                        "application/msword"
                    )
        ) { uri ->

            if (uri != null) {

                exportJournalAsWord(
                    context,
                    uri,
                    journal
                )
            }
        }

    // ========================================================
    // JOURNAL DETAIL DIALOG
    // ========================================================

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = CardColor,

        title = {

            Text(
                text = journal.title,
                color = MainText,
                fontWeight = FontWeight.Bold
            )
        },

        text = {

            Column(
                modifier = Modifier.verticalScroll(
                    rememberScrollState()
                )
            ) {

                Text(
                    text = journal.date,
                    color = SecondaryText
                )

                Text(
                    text = journal.time,
                    color = LightBlue
                )

                Spacer(
                    modifier = Modifier.height(10.dp)
                )

                Text(
                    text = "${journal.hours} OJT hours",
                    color = GreenColor,
                    fontWeight = FontWeight.Bold
                )

                Spacer(
                    modifier = Modifier.height(15.dp)
                )

                Text(
                    text = journal.content,
                    color = MainText
                )

                // ====================================================
                // IMAGES
                // ====================================================

                if (imageList.isNotEmpty()) {

                    Spacer(
                        modifier = Modifier.height(15.dp)
                    )

                    Text(
                        text = "Images",
                        color = MainText,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(
                        modifier = Modifier.height(10.dp)
                    )

                    imageList.forEachIndexed {
                            index,
                            uri ->

                        JournalImage(
                            uri = uri,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {

                                    fullScreenImageIndex =
                                        index
                                }
                        )

                        Spacer(
                            modifier = Modifier.height(10.dp)
                        )
                    }
                }
            }
        },

        // ========================================================
        // RIGHT SIDE BUTTONS
        // ========================================================

        confirmButton = {

            Row {

                // EXPORT
                TextButton(
                    onClick = {

                        exportLauncher.launch(
                            "OJT_Journal_${journal.date.replace(
                                "/",
                                "-"
                            )}.doc"
                        )
                    }
                ) {

                    Text(
                        text = "EXPORT",
                        color = GreenColor,
                        fontWeight = FontWeight.Bold
                    )
                }

                // EDIT
                TextButton(
                    onClick = {
                        onEdit(journal)
                    }
                ) {

                    Text(
                        text = "EDIT",
                        color = LightBlue,
                        fontWeight = FontWeight.Bold
                    )
                }

                // CLOSE
                TextButton(
                    onClick = onDismiss
                ) {

                    Text(
                        text = "CLOSE",
                        color = LightBlue
                    )
                }
            }
        },

        // ========================================================
        // DELETE BUTTON
        // ========================================================

        dismissButton = {

            TextButton(
                onClick = {
                    showDeleteWarning = true
                }
            ) {

                Text(
                    text = "DELETE",
                    color = ErrorRed
                )
            }
        }
    )

    // ============================================================
    // DELETE WARNING DIALOG
    // ============================================================

    if (showDeleteWarning) {

        AlertDialog(
            onDismissRequest = {
                showDeleteWarning = false
            },

            containerColor = CardColor,

            title = {

                Text(
                    text = "Delete Journal?",
                    color = MainText,
                    fontWeight = FontWeight.Bold
                )
            },

            text = {

                Text(
                    text = "Are you sure you want to delete this journal? This action cannot be undone.",
                    color = SecondaryText
                )
            },

            confirmButton = {

                TextButton(
                    onClick = {

                        showDeleteWarning = false

                        // ACTUALLY DELETE
                        onDelete()
                    }
                ) {

                    Text(
                        text = "DELETE",
                        color = ErrorRed,
                        fontWeight = FontWeight.Bold
                    )
                }
            },

            dismissButton = {

                TextButton(
                    onClick = {
                        showDeleteWarning = false
                    }
                ) {

                    Text(
                        text = "CANCEL",
                        color = LightBlue,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        )
    }

    // ============================================================
    // FULL SCREEN IMAGE VIEWER
    // ============================================================

    fullScreenImageIndex?.let {
            selectedIndex ->

        FullScreenImageViewer(
            images = imageList,
            initialIndex = selectedIndex,
            onDismiss = {
                fullScreenImageIndex = null
            }
        )
    }
}


// ============================================================
// FULL SCREEN IMAGE VIEWER
// ============================================================

@Composable
fun FullScreenImageViewer(
    images: List<Uri>,
    initialIndex: Int,
    onDismiss: () -> Unit
) {

    var currentIndex by remember(initialIndex) {
        mutableStateOf(
            initialIndex.coerceIn(
                0,
                (images.size - 1)
                    .coerceAtLeast(0)
            )
        )
    }

    if (images.isEmpty()) {
        return
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            usePlatformDefaultWidth = false,
            dismissOnBackPress = true,
            dismissOnClickOutside = false
        )
    ) {

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black)
        ) {

            JournalImage(
                uri = images[currentIndex],
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.Center)
            )

            TextButton(
                onClick = onDismiss,
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(12.dp)
            ) {

                Text(
                    text = "✕ CLOSE",
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            }

            if (images.size > 1) {

                Text(
                    text =
                        "${currentIndex + 1} / ${images.size}",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .padding(top = 22.dp)
                        .background(
                            Color(0x99000000),
                            RoundedCornerShape(20.dp)
                        )
                        .padding(
                            horizontal = 14.dp,
                            vertical = 7.dp
                        )
                )

                if (currentIndex > 0) {

                    TextButton(
                        onClick = {
                            currentIndex--
                        },
                        modifier = Modifier
                            .align(
                                Alignment.CenterStart
                            )
                            .padding(start = 8.dp)
                    ) {

                        Text(
                            text = "‹",
                            color = Color.White,
                            fontSize = 50.sp
                        )
                    }
                }

                if (currentIndex < images.lastIndex) {

                    TextButton(
                        onClick = {
                            currentIndex++
                        },
                        modifier = Modifier
                            .align(
                                Alignment.CenterEnd
                            )
                            .padding(end = 8.dp)
                    ) {

                        Text(
                            text = "›",
                            color = Color.White,
                            fontSize = 50.sp
                        )
                    }
                }
            }
        }
    }
}


// ============================================================
// IMAGE PREVIEW
// ============================================================

@Composable
fun SelectedImageRow(
    uri: Uri,
    index: Int,
    onRemove: () -> Unit
) {

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                CardColor,
                RoundedCornerShape(14.dp)
            )
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {

        JournalImage(
            uri = uri,
            modifier = Modifier.size(70.dp)
        )

        Spacer(
            modifier = Modifier.width(10.dp)
        )

        Text(
            text = "Image ${index + 1}",
            color = MainText,
            modifier = Modifier.weight(1f)
        )

        TextButton(
            onClick = onRemove
        ) {

            Text(
                text = "Remove",
                color = ErrorRed
            )
        }
    }
}

@Composable
fun JournalImage(
    uri: Uri,
    modifier: Modifier = Modifier
) {

    val context = LocalContext.current

    var bitmap by remember(uri) {
        mutableStateOf<Bitmap?>(null)
    }

    LaunchedEffect(uri) {

        bitmap =
            withContext(Dispatchers.IO) {

                try {

                    context.contentResolver
                        .openInputStream(uri)
                        ?.use {
                            BitmapFactory
                                .decodeStream(it)
                        }

                } catch (_: Exception) {

                    null
                }
            }
    }

    if (bitmap != null) {

        Image(
            bitmap = bitmap!!.asImageBitmap(),
            contentDescription = "Journal Image",
            modifier = modifier
                .fillMaxWidth()
                .clip(
                    RoundedCornerShape(12.dp)
                ),
            contentScale = ContentScale.Fit
        )

    } else {

        Box(
            modifier = modifier
                .fillMaxWidth()
                .height(150.dp)
                .background(
                    Color(0xFF252B33),
                    RoundedCornerShape(12.dp)
                ),
            contentAlignment = Alignment.Center
        ) {

            Text(
                text = "Unable to load image",
                color = SecondaryText
            )
        }
    }
}


// ============================================================
// PROFILE
// ============================================================

@Composable
fun ProfileScreen(
    userPreferences: UserPreferences,
    onBack: () -> Unit
) {

    var editing by remember {
        mutableStateOf(false)
    }

    var showChangePassword by remember {
        mutableStateOf(false)
    }

    var fullName by remember {
        mutableStateOf(
            userPreferences.getFullName()
        )
    }

    var email by remember {
        mutableStateOf(
            userPreferences.getEmail()
        )
    }

    var school by remember {
        mutableStateOf(
            userPreferences.getSchool()
        )
    }

    var course by remember {
        mutableStateOf(
            userPreferences.getCourse()
        )
    }

    var yearSection by remember {
        mutableStateOf(
            userPreferences.getYearSection()
        )
    }

    var company by remember {
        mutableStateOf(
            userPreferences.getCompany()
        )
    }

    var requiredHours by remember {
        mutableStateOf(
            userPreferences.getRequiredHours()
        )
    }

    var profileUri by remember {
        mutableStateOf(
            userPreferences
                .getProfileImage()
                .takeIf {
                    it.isNotEmpty()
                }
                ?.let {
                    Uri.parse(it)
                }
        )
    }

    var message by remember {
        mutableStateOf("")
    }

    val context = LocalContext.current

    val imagePicker =
        rememberLauncherForActivityResult(
            ActivityResultContracts.OpenDocument()
        ) { uri ->

            if (uri != null) {

                try {

                    context.contentResolver
                        .takePersistableUriPermission(
                            uri,
                            Intent.FLAG_GRANT_READ_URI_PERMISSION
                        )

                } catch (_: Exception) {
                }

                profileUri = uri
            }
        }

    BackHandler {

        when {

            showChangePassword -> {
                showChangePassword = false
            }

            editing -> {
                editing = false
            }

            else -> {
                onBack()
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundColor)
            .padding(24.dp)
            .verticalScroll(
                rememberScrollState()
            )
    ) {

        TextButton(
            onClick = {

                when {

                    showChangePassword -> {
                        showChangePassword = false
                    }

                    editing -> {
                        editing = false
                    }

                    else -> {
                        onBack()
                    }
                }
            }
        ) {

            Text(
                "← Back",
                color = LightBlue
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Text(
                text = "My Profile",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = MainText,
                modifier = Modifier.weight(1f)
            )

            if (!editing && !showChangePassword) {

                TextButton(
                    onClick = {
                        message = ""
                        editing = true
                    }
                ) {

                    Text(
                        "EDIT",
                        color = LightBlue,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        Spacer(
            modifier = Modifier.height(20.dp)
        )

        // =====================================================
        // PROFILE PICTURE
        // =====================================================

        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {

            Column(
                horizontalAlignment =
                    Alignment.CenterHorizontally
            ) {

                ProfileImage(
                    uri = profileUri,
                    size = 120
                )

                if (editing) {

                    Spacer(
                        modifier = Modifier.height(8.dp)
                    )

                    TextButton(
                        onClick = {
                            imagePicker.launch(
                                arrayOf("image/*")
                            )
                        }
                    ) {

                        Text(
                            "CHANGE PROFILE PICTURE",
                            color = LightBlue
                        )
                    }
                }
            }
        }

        Spacer(
            modifier = Modifier.height(20.dp)
        )

        // =====================================================
        // CHANGE PASSWORD SCREEN
        // =====================================================

        if (showChangePassword) {

            ChangePasswordContent(
                context = context,
                onSuccess = {
                    showChangePassword = false
                    editing = false
                    message =
                        "Password changed successfully."
                },
                onCancel = {
                    showChangePassword = false
                }
            )

        } else if (editing) {

            // =================================================
            // EDIT PROFILE
            // =================================================

            ProfileEditField(
                "Full Name",
                fullName
            ) {
                fullName = it
            }

            ProfileEditField(
                "Email",
                email
            ) {
                email = it
            }

            ProfileEditField(
                "School",
                school
            ) {
                school = it
            }

            ProfileEditField(
                "Course / Program",
                course
            ) {
                course = it
            }

            ProfileEditField(
                "Year & Section",
                yearSection
            ) {
                yearSection = it
            }

            ProfileEditField(
                "OJT Company",
                company
            ) {
                company = it
            }

            ProfileEditField(
                "Required OJT Hours",
                requiredHours
            ) {
                requiredHours = it
            }

            Spacer(
                modifier = Modifier.height(10.dp)
            )

            // =================================================
            // CHANGE PASSWORD BUTTON
            // =================================================

            OutlinedButton(
                onClick = {
                    message = ""
                    showChangePassword = true
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(14.dp)
            ) {

                Text(
                    "CHANGE PASSWORD",
                    color = LightBlue,
                    fontWeight = FontWeight.Bold
                )
            }

            if (message.isNotEmpty()) {

                Spacer(
                    modifier = Modifier.height(10.dp)
                )

                Text(
                    message,
                    color = GreenColor
                )
            }

            Spacer(
                modifier = Modifier.height(15.dp)
            )

            // =================================================
            // SAVE PROFILE
            // =================================================

            Button(
                onClick = {

                    val validHours =
                        requiredHours.toDoubleOrNull()

                    when {

                        fullName.isBlank() ||
                                email.isBlank() ||
                                school.isBlank() ||
                                course.isBlank() ||
                                yearSection.isBlank() ||
                                company.isBlank() -> {

                            message =
                                "Please complete all profile fields."
                        }

                        !email.contains("@") -> {

                            message =
                                "Please enter a valid email."
                        }

                        validHours == null ||
                                validHours <= 0 -> {

                            message =
                                "Enter a valid OJT hour requirement."
                        }

                        else -> {

                            userPreferences.saveAccount(

                                fullName =
                                    fullName.trim(),

                                email =
                                    email.trim(),

                                password =
                                    getSavedPassword(
                                        context
                                    ),

                                school =
                                    school.trim(),

                                course =
                                    course.trim(),

                                yearSection =
                                    yearSection.trim(),

                                company =
                                    company.trim(),

                                requiredHours =
                                    requiredHours.trim(),

                                profileImage =
                                    profileUri
                                        ?.toString()
                                        ?: ""
                            )

                            message =
                                "Profile updated successfully."

                            editing = false
                        }
                    }
                },

                modifier = Modifier
                    .fillMaxWidth()
                    .height(55.dp),

                shape =
                    RoundedCornerShape(14.dp)
            ) {

                Text(
                    "SAVE CHANGES",
                    fontWeight = FontWeight.Bold
                )
            }

        } else {

            // =================================================
            // PROFILE INFORMATION
            // =================================================

            ProfileInformation(
                "Full Name",
                fullName
            )

            ProfileInformation(
                "Email",
                email
            )

            ProfileInformation(
                "School",
                school
            )

            ProfileInformation(
                "Course / Program",
                course
            )

            ProfileInformation(
                "Year & Section",
                yearSection
            )

            ProfileInformation(
                "OJT Company",
                company
            )

            ProfileInformation(
                "Required OJT Hours",
                requiredHours
            )

            if (message.isNotEmpty()) {

                Spacer(
                    modifier = Modifier.height(10.dp)
                )

                Text(
                    message,
                    color = GreenColor,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
fun ChangePasswordContent(
    context: Context,
    onSuccess: () -> Unit,
    onCancel: () -> Unit
) {

    var currentPassword by remember {
        mutableStateOf("")
    }

    var newPassword by remember {
        mutableStateOf("")
    }

    var confirmPassword by remember {
        mutableStateOf("")
    }

    var showCurrent by remember {
        mutableStateOf(false)
    }

    var showNew by remember {
        mutableStateOf(false)
    }

    var showConfirm by remember {
        mutableStateOf(false)
    }

    var errorMessage by remember {
        mutableStateOf("")
    }

    val savedPassword =
        getSavedPassword(context)

    Column(
        modifier = Modifier.fillMaxWidth()
    ) {

        Text(
            text = "Change Password",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = MainText
        )

        Spacer(
            modifier = Modifier.height(6.dp)
        )

        Text(
            text = "Update your account password.",
            color = SecondaryText
        )

        Spacer(
            modifier = Modifier.height(20.dp)
        )

        OutlinedTextField(
            value = currentPassword,
            onValueChange = {
                currentPassword = it
                errorMessage = ""
            },
            modifier = Modifier.fillMaxWidth(),
            label = {
                Text("Current Password")
            },
            trailingIcon = {

                TextButton(
                    onClick = {
                        showCurrent = !showCurrent
                    }
                ) {

                    Text(
                        if (showCurrent)
                            "Hide"
                        else
                            "Show",
                        color = LightBlue
                    )
                }
            },
            visualTransformation =
                if (showCurrent)
                    VisualTransformation.None
                else
                    PasswordVisualTransformation(),
            singleLine = true,
            shape = RoundedCornerShape(14.dp)
        )

        Spacer(
            modifier = Modifier.height(15.dp)
        )

        OutlinedTextField(
            value = newPassword,
            onValueChange = {
                newPassword = it
                errorMessage = ""
            },
            modifier = Modifier.fillMaxWidth(),
            label = {
                Text("New Password")
            },
            trailingIcon = {

                TextButton(
                    onClick = {
                        showNew = !showNew
                    }
                ) {

                    Text(
                        if (showNew)
                            "Hide"
                        else
                            "Show",
                        color = LightBlue
                    )
                }
            },
            visualTransformation =
                if (showNew)
                    VisualTransformation.None
                else
                    PasswordVisualTransformation(),
            singleLine = true,
            shape = RoundedCornerShape(14.dp)
        )

        Spacer(
            modifier = Modifier.height(15.dp)
        )

        OutlinedTextField(
            value = confirmPassword,
            onValueChange = {
                confirmPassword = it
                errorMessage = ""
            },
            modifier = Modifier.fillMaxWidth(),
            label = {
                Text("Confirm New Password")
            },
            trailingIcon = {

                TextButton(
                    onClick = {
                        showConfirm = !showConfirm
                    }
                ) {

                    Text(
                        if (showConfirm)
                            "Hide"
                        else
                            "Show",
                        color = LightBlue
                    )
                }
            },
            visualTransformation =
                if (showConfirm)
                    VisualTransformation.None
                else
                    PasswordVisualTransformation(),
            singleLine = true,
            shape = RoundedCornerShape(14.dp)
        )

        if (errorMessage.isNotEmpty()) {

            Spacer(
                modifier = Modifier.height(10.dp)
            )

            Text(
                text = errorMessage,
                color = ErrorRed
            )
        }

        Spacer(
            modifier = Modifier.height(20.dp)
        )

        Button(
            onClick = {

                when {

                    currentPassword.isBlank() ||
                            newPassword.isBlank() ||
                            confirmPassword.isBlank() -> {

                        errorMessage =
                            "Please complete all password fields."
                    }

                    currentPassword != savedPassword -> {

                        errorMessage =
                            "Current password is incorrect."
                    }

                    newPassword.length < 6 -> {

                        errorMessage =
                            "New password must be at least 6 characters."
                    }

                    newPassword == currentPassword -> {

                        errorMessage =
                            "New password must be different from your current password."
                    }

                    newPassword != confirmPassword -> {

                        errorMessage =
                            "New passwords do not match."
                    }

                    else -> {

                        context
                            .getSharedPreferences(
                                "OJT_TRACKER_PREFS",
                                Context.MODE_PRIVATE
                            )
                            .edit()
                            .putString(
                                "password",
                                newPassword
                            )
                            .apply()

                        onSuccess()
                    }
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(55.dp),
            shape = RoundedCornerShape(14.dp)
        ) {

            Text(
                "UPDATE PASSWORD",
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(
            modifier = Modifier.height(10.dp)
        )

        OutlinedButton(
            onClick = onCancel,
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            shape = RoundedCornerShape(14.dp)
        ) {

            Text(
                "CANCEL",
                color = LightBlue
            )
        }
    }
}

@Composable
fun ProfileEditField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit
) {

    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        label = {
            Text(label)
        },
        singleLine = true,
        shape = RoundedCornerShape(14.dp)
    )
}

private fun getSavedPassword(
    context: Context
): String {

    return context
        .getSharedPreferences(
            "OJT_TRACKER_PREFS",
            Context.MODE_PRIVATE
        )
        .getString(
            "password",
            ""
        ) ?: ""
}

@Composable
fun ProfileInformation(
    label: String,
    value: String
) {

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {

        Text(
            text = label,
            color = LightBlue,
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold
        )

        Text(
            text = value.ifEmpty {
                "Not provided"
            },
            color = MainText,
            fontSize = 16.sp
        )

        Divider(
            modifier = Modifier.padding(
                top = 8.dp
            )
        )
    }
}


// ============================================================
// PROFILE IMAGE
// ============================================================

@Composable
fun ProfileImage(
    uri: Uri?,
    size: Int = 120
) {

    val context = LocalContext.current

    var bitmap by remember(uri) {
        mutableStateOf<Bitmap?>(null)
    }

    LaunchedEffect(uri) {

        if (uri != null) {

            bitmap =
                withContext(Dispatchers.IO) {

                    try {

                        context.contentResolver
                            .openInputStream(uri)
                            ?.use {
                                BitmapFactory
                                    .decodeStream(it)
                            }

                    } catch (_: Exception) {

                        null
                    }
                }

        } else {

            bitmap = null
        }
    }

    Box(
        modifier = Modifier
            .size(size.dp)
            .clip(CircleShape)
            .background(CardColor)
            .border(
                3.dp,
                LightBlue,
                CircleShape
            ),
        contentAlignment = Alignment.Center
    ) {

        if (bitmap != null) {

            Image(
                bitmap = bitmap!!.asImageBitmap(),
                contentDescription =
                    "Profile Picture",
                modifier = Modifier
                    .fillMaxSize()
                    .clip(CircleShape),
                contentScale = ContentScale.Crop
            )

        } else {

            Text(
                text = "👤",
                fontSize = 45.sp
            )
        }
    }
}


// ============================================================
// JOURNAL STORAGE
// ============================================================

// ============================================================
// JOURNAL STORAGE
// ============================================================

private const val JOURNAL_PREFS =
    "OJT_JOURNALS"

private const val ACCOUNT_PREFS =
    "OJT_ACCOUNTS"

private const val ACCOUNT_LIST =
    "account_list"

private const val SAVED_ACCOUNT_LIST = "saved_account_list"

private const val ACTIVE_ACCOUNT =
    "active_account"


// ------------------------------------------------------------
// GET JOURNAL STORAGE KEY FOR CURRENT ACCOUNT
// ------------------------------------------------------------

private fun getJournalEntriesKey(
    context: Context
): String {

    val activeAccount =
        getActiveAccount(context)
            .trim()
            .lowercase()

    return if (activeAccount.isEmpty()) {

        // Fallback for safety
        "entries"

    } else {

        "entries_${Uri.encode(activeAccount)}"
    }
}


// ------------------------------------------------------------
// MIGRATE OLD GLOBAL JOURNALS
// ------------------------------------------------------------

private fun migrateOldJournals(
    context: Context
) {

    val activeAccount =
        getActiveAccount(context)
            .trim()

    if (activeAccount.isEmpty()) {
        return
    }

    val preferences =
        context.getSharedPreferences(
            JOURNAL_PREFS,
            Context.MODE_PRIVATE
        )

    val oldData =
        preferences.getString(
            "entries",
            null
        )

    val accountKey =
        getJournalEntriesKey(context)

    val accountData =
        preferences.getString(
            accountKey,
            null
        )

    // Only migrate if:
    // 1. old global journals exist
    // 2. current account has no journals yet
    if (!oldData.isNullOrEmpty() &&
        accountData == null
    ) {

        preferences.edit()
            .putString(
                accountKey,
                oldData
            )
            .remove("entries")
            .apply()
    }
}


// ------------------------------------------------------------
// SAVE JOURNAL
// ------------------------------------------------------------

private fun saveJournal(
    context: Context,
    journal: JournalEntry
) {

    // Make sure old journals are migrated first
    migrateOldJournals(context)

    val preferences =
        context.getSharedPreferences(
            JOURNAL_PREFS,
            Context.MODE_PRIVATE
        )

    val storageKey =
        getJournalEntriesKey(context)

    val oldData =
        preferences.getString(
            storageKey,
            ""
        ) ?: ""

    val encodedTitle =
        Uri.encode(journal.title)

    val encodedContent =
        Uri.encode(journal.content)

    val encodedDate =
        Uri.encode(journal.date)

    val encodedTime =
        Uri.encode(journal.time)

    val record =
        listOf(
            journal.id.toString(),
            encodedDate,
            encodedTime,
            encodedTitle,
            encodedContent,
            journal.hours.toString(),
            journal.imageUri
        ).joinToString("|||")

    val newData =
        if (oldData.isEmpty()) {

            record

        } else {

            "$oldData###$record"
        }

    preferences.edit()
        .putString(
            storageKey,
            newData
        )
        .apply()
}


// ------------------------------------------------------------
// LOAD JOURNALS
// ------------------------------------------------------------

private fun loadJournalEntries(
    context: Context
): List<JournalEntry> {

    // Make sure old journals are migrated first
    migrateOldJournals(context)

    val preferences =
        context.getSharedPreferences(
            JOURNAL_PREFS,
            Context.MODE_PRIVATE
        )

    val storageKey =
        getJournalEntriesKey(context)

    val data =
        preferences.getString(
            storageKey,
            ""
        ) ?: ""

    if (data.isEmpty()) {
        return emptyList()
    }

    return data
        .split("###")
        .mapNotNull { record ->

            try {

                val parts =
                    record.split("|||")

                if (parts.size < 7) {
                    return@mapNotNull null
                }

                JournalEntry(

                    id =
                        parts[0].toLong(),

                    date =
                        Uri.decode(parts[1]),

                    time =
                        Uri.decode(parts[2]),

                    title =
                        Uri.decode(parts[3]),

                    content =
                        Uri.decode(parts[4]),

                    hours =
                        parts[5].toDouble(),

                    imageUri =
                        parts
                            .drop(6)
                            .joinToString("|||"),

                    accountEmail =
                        getActiveAccount(context)
                )

            } catch (_: Exception) {

                null
            }
        }
}


// ------------------------------------------------------------
// DELETE JOURNAL
// ------------------------------------------------------------

private fun deleteJournal(
    context: Context,
    id: Long
) {

    val journals =
        loadJournalEntries(context)
            .filter {
                it.id != id
            }

    val preferences =
        context.getSharedPreferences(
            JOURNAL_PREFS,
            Context.MODE_PRIVATE
        )

    val storageKey =
        getJournalEntriesKey(context)

    // Remove only the current account's journals
    preferences.edit()
        .remove(storageKey)
        .apply()

    // Save the remaining journals
    journals.forEach {
        saveJournal(
            context,
            it
        )
    }
}


// ============================================================
// JOURNAL DRAFT + UPDATE
// ============================================================

private const val DRAFT_PREFS =
    "OJT_JOURNAL_DRAFTS"

private fun saveJournalDraft(
    context: Context,
    key: String,
    title: String,
    content: String,
    time: String,
    hours: String,
    images: List<Uri>
) {

    context
        .getSharedPreferences(
            DRAFT_PREFS,
            Context.MODE_PRIVATE
        )
        .edit()

        .putString(
            "${key}_title",
            title
        )

        .putString(
            "${key}_content",
            content
        )

        .putString(
            "${key}_time",
            time
        )

        .putString(
            "${key}_hours",
            hours
        )

        .putString(
            "${key}_images",
            images.joinToString("||") {
                Uri.encode(it.toString())
            }
        )

        .apply()
}

private fun loadJournalDraft(
    context: Context,
    key: String
): DraftData? {

    val prefs =
        context.getSharedPreferences(
            DRAFT_PREFS,
            Context.MODE_PRIVATE
        )

    val exists =
        prefs.contains("${key}_title") ||
                prefs.contains("${key}_content") ||
                prefs.contains("${key}_images")

    if (!exists) {
        return null
    }

    return DraftData(

        prefs.getString(
            "${key}_title",
            ""
        ) ?: "",

        prefs.getString(
            "${key}_content",
            ""
        ) ?: "",

        prefs.getString(
            "${key}_time",
            ""
        ) ?: "",

        prefs.getString(
            "${key}_hours",
            ""
        ) ?: "",

        prefs.getString(
            "${key}_images",
            ""
        ) ?: ""
    )
}

private data class DraftData(
    val first: String,
    val second: String,
    val third: String,
    val fourth: String,
    val fifth: String
)

private fun clearJournalDraft(
    context: Context,
    key: String
) {

    context
        .getSharedPreferences(
            DRAFT_PREFS,
            Context.MODE_PRIVATE
        )
        .edit()

        .remove("${key}_title")
        .remove("${key}_content")
        .remove("${key}_time")
        .remove("${key}_hours")
        .remove("${key}_images")
        .apply()
}

private fun updateJournal(
    context: Context,
    updated: JournalEntry
) {

    val journals =
        loadJournalEntries(context)
            .map {

                if (it.id == updated.id)
                    updated
                else
                    it
            }

    val preferences =
        context.getSharedPreferences(
            JOURNAL_PREFS,
            Context.MODE_PRIVATE
        )

    preferences.edit()
        .remove("entries")
        .apply()

    journals.forEach {
        saveJournal(
            context,
            it
        )
    }
}


// ============================================================
// IMAGE URI HELPERS
// ============================================================

private fun getImageUris(
    imageString: String
): List<Uri> {

    if (imageString.isBlank()) {
        return emptyList()
    }

    return imageString
        .split("||")
        .filter {
            it.isNotBlank()
        }
        .mapNotNull {

            try {

                Uri.parse(
                    Uri.decode(it)
                )

            } catch (_: Exception) {

                null
            }
        }
}
// SAVE ACCOUNT
private fun saveAccountToList(
    context: Context,
    userPreferences: UserPreferences
) {
    val prefs = context.getSharedPreferences(
        ACCOUNT_PREFS,
        Context.MODE_PRIVATE
    )

    val email = userPreferences.getEmail().trim()

    if (email.isEmpty()) return

    val accountData = listOf(
        email,
        userPreferences.getFullName(),
        getSavedPassword(context),
        userPreferences.getSchool(),
        userPreferences.getCourse(),
        userPreferences.getYearSection(),
        userPreferences.getCompany(),
        userPreferences.getRequiredHours(),
        userPreferences.getProfileImage()
    ).joinToString("|||") { Uri.encode(it) }

    val accounts = prefs.getStringSet(
        ACCOUNT_LIST,
        emptySet()
    )?.toMutableSet() ?: mutableSetOf()

    // Remove old record of the same email
    accounts.removeAll { record ->
        val parts = record.split("|||")
        parts.firstOrNull()?.let {
            Uri.decode(it).equals(email, ignoreCase = true)
        } ?: false
    }

    accounts.add(accountData)

    prefs.edit()
        .putStringSet(ACCOUNT_LIST, accounts)
        .apply()
}
// ============================================================
// SAVED ACCOUNT HELPERS
// ============================================================

private fun getSavedAccounts(
    context: Context
): Set<String> {

    val prefs = context.getSharedPreferences(
        ACCOUNT_PREFS,
        Context.MODE_PRIVATE
    )

    return prefs.getStringSet(
        SAVED_ACCOUNT_LIST,
        emptySet()
    ) ?: emptySet()
}


private fun saveAccountToSavedList(
    context: Context,
    email: String
) {

    val prefs = context.getSharedPreferences(
        ACCOUNT_PREFS,
        Context.MODE_PRIVATE
    )

    val savedAccounts =
        getSavedAccounts(context).toMutableSet()

    savedAccounts.add(
        email.trim().lowercase()
    )

    prefs.edit()
        .putStringSet(
            SAVED_ACCOUNT_LIST,
            savedAccounts
        )
        .apply()
}


private fun removeAccountFromSavedList(
    context: Context,
    email: String
) {

    val prefs = context.getSharedPreferences(
        ACCOUNT_PREFS,
        Context.MODE_PRIVATE
    )

    val savedAccounts =
        getSavedAccounts(context).toMutableSet()

    savedAccounts.remove(
        email.trim().lowercase()
    )

    prefs.edit()
        .putStringSet(
            SAVED_ACCOUNT_LIST,
            savedAccounts
        )
        .apply()
}

//FIND ACCOUNT
private fun findAccount(
    context: Context,
    email: String,
    password: String
): List<String>? {

    val prefs = context.getSharedPreferences(
        ACCOUNT_PREFS,
        Context.MODE_PRIVATE
    )

    val accounts = prefs.getStringSet(
        ACCOUNT_LIST,
        emptySet()
    ) ?: emptySet()

    return accounts
        .map { record ->
            record.split("|||").map { Uri.decode(it) }
        }
        .firstOrNull { parts ->
            parts.size >= 9 &&
                    parts[0].equals(email.trim(), ignoreCase = true) &&
                    parts[2] == password
        }
}

//SET ACTIVE ACCOUNT
private fun setActiveAccount(
    context: Context,
    email: String
) {
    context.getSharedPreferences(
        ACCOUNT_PREFS,
        Context.MODE_PRIVATE
    )
        .edit()
        .putString(ACTIVE_ACCOUNT, email.trim())
        .apply()
}

private fun getActiveAccount(
    context: Context
): String {
    return context.getSharedPreferences(
        ACCOUNT_PREFS,
        Context.MODE_PRIVATE
    )
        .getString(ACTIVE_ACCOUNT, "") ?: ""
}


// ============================================================
// UTILITY
// ============================================================

private fun formatHours(
    hours: Double
): String {

    return if (hours % 1.0 == 0.0) {

        hours.toInt().toString()

    } else {

        String.format(
            Locale.getDefault(),
            "%.1f",
            hours
        )
    }
}

private fun copyProfileImageToInternalStorage(
    context: Context,
    sourceUri: Uri
): String {
    return try {
        val imageFolder = File(
            context.filesDir,
            "profile_images"
        )

        if (!imageFolder.exists()) {
            imageFolder.mkdirs()
        }

        val imageFile = File(
            imageFolder,
            "profile_${UUID.randomUUID()}.jpg"
        )

        val inputStream =
            context.contentResolver.openInputStream(sourceUri)
                ?: return ""

        inputStream.use { input ->
            FileOutputStream(imageFile).use { output ->
                input.copyTo(output)
            }
        }

        Uri.fromFile(imageFile).toString()
    } catch (e: Exception) {
        e.printStackTrace()
        ""
    }
}
