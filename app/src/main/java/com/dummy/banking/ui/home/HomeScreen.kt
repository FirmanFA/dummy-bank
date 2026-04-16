package com.dummy.banking.ui.home

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.dummy.banking.R
import com.dummy.banking.model.Transaction
import com.dummy.banking.utils.CurrencyFormatter
import com.dummy.banking.viewmodel.HomeUiState
import com.dummy.banking.viewmodel.HomeViewModel

@Composable
fun HomeScreen(
    onNavigateToTransfer: () -> Unit,
    onNavigateToHistory: () -> Unit,
    onLogout: () -> Unit,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val primaryColor = MaterialTheme.colorScheme.primary
    var showComingSoonDialog by remember { mutableStateOf(false) }

    val scrollState = rememberLazyListState()

    val density = LocalDensity.current
    val thresholdPx = with(density) { 80.dp.toPx() }

    val headerBgColor by remember {
        derivedStateOf {
            val alpha = if (scrollState.firstVisibleItemIndex > 0) {
                1f
            } else {
                (scrollState.firstVisibleItemScrollOffset / thresholdPx).coerceIn(0f, 1f)
            }
            primaryColor.copy(alpha = alpha)
        }
    }

    Box(modifier = Modifier
        .fillMaxSize()
        .background(MaterialTheme.colorScheme.background)) {

        LazyColumn(
            state = scrollState,
            modifier = Modifier.fillMaxSize()
        ) {
            item {
                when (val state = uiState) {
                    is HomeUiState.Success -> HeaderSection(state.user?.name ?: "FIRMANSYAH FIRDAUS ANHAR", primaryColor)
                    else -> HeaderSection("...", primaryColor)
                }
            }

            item {
                when (val state = uiState) {
                    is HomeUiState.Success -> BalanceCard(state.user?.balance ?: 0L, onNavigateToHistory, primaryColor)
                    else -> BalanceCard(0L, onNavigateToHistory, primaryColor)
                }
            }

//            item {
//                BannerSection(primaryColor)
//            }

            item {
                MenuGrid(onNavigateToTransfer, {
                    showComingSoonDialog = true
                }, primaryColor)
            }

            item {
                Text(
                    "Riwayat Transaksi",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier
                        .padding(horizontal = 24.dp, vertical = 16.dp)
                        .offset(y = (-30).dp),
                    fontWeight = FontWeight.Bold
                )
            }

            when (val state = uiState) {
                is HomeUiState.Loading -> {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator()
                        }
                    }
                }

                is HomeUiState.Success -> {
                    items(state.transactions) { transaction ->
                        Box(
                            modifier = Modifier.offset(y = (-30).dp)
                        ) {
                            TransactionItem(
                                transaction = transaction,
                            )
                        }
                    }
                }

                is HomeUiState.Error -> {
                    item {
                        Text(
                            state.message,
                            color = MaterialTheme.colorScheme.error,
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                }
            }

            item { Spacer(modifier = Modifier.height(100.dp)) }
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(headerBgColor)
        ) {
            Box(modifier = Modifier.statusBarsPadding()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_logo_white),
                        contentDescription = "Logo",
                        modifier = Modifier.height(32.dp),
                        contentScale = ContentScale.Fit
                    )
                    Row {
                        IconButton(onClick = onLogout) {
                            Icon(
                                Icons.AutoMirrored.Filled.Logout,
                                null,
                                tint = Color.White
                            )
                        }
                    }
                }
            }
        }

        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .navigationBarsPadding()
                .padding(bottom = 16.dp)
                .height(64.dp)
                .clip(RoundedCornerShape(32.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.95f))
                .clickable {
                    showComingSoonDialog = true
                },
            contentAlignment = Alignment.Center
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier
                    .padding(horizontal = 24.dp)
            ) {
                Icon(Icons.Default.QrCodeScanner, contentDescription = null, tint = primaryColor)
                Spacer(modifier = Modifier.width(12.dp))
                Text("QRIS", fontWeight = FontWeight.Bold, color = primaryColor)
            }
        }

        if (showComingSoonDialog) {
            ComingSoonDialog(onDismiss = { showComingSoonDialog = false })
        }
    }
}

@Composable
fun ComingSoonDialog(onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            Button(
                onClick = onDismiss,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("SAYA MENGERTI", fontWeight = FontWeight.Bold)
            }
        },
        icon = {
            Box(
                modifier = Modifier
                    .size(72.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primaryContainer),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Default.RocketLaunch,
                    contentDescription = null,
                    modifier = Modifier.size(36.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        },
        title = {
            Text(
                text = "Segera Hadir!",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        },
        text = {
            Text(
                text = "Kami sedang menyiapkan sesuatu yang luar biasa untuk Anda. Fitur ini akan segera tersedia di pembaruan mendatang untuk meningkatkan pengalaman perbankan Anda.",
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        },
        shape = RoundedCornerShape(28.dp),
        containerColor = MaterialTheme.colorScheme.surface,
    )
}

@Composable
fun HeaderSection(userName: String, primary: Color) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(240.dp)
            .background(
                brush = Brush.verticalGradient(listOf(primary, primary.copy(alpha = 0.8f)))
            )
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawCircle(
                color = Color.White.copy(alpha = 0.05f),
                radius = 400f,
                center = center.copy(x = size.width, y = 0f)
            )
            drawCircle(
                color = Color.White.copy(alpha = 0.05f),
                radius = 200f,
                center = center.copy(x = 0f, y = size.height * 0.6f)
            )
        }

        Column(
            modifier = Modifier
                .statusBarsPadding()
                .padding(horizontal = 20.dp, vertical = 16.dp)
        ) {

            Spacer(modifier = Modifier.height(64.dp))

            Spacer(modifier = Modifier.height(24.dp))
            Text(
                "HALO, ${userName.uppercase()}",
                color = Color.White,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.5.sp
            )
        }
    }
}

@Composable
fun BalanceCard(balance: Long, onHistory: () -> Unit, primary: Color) {
    var isBalanceVisible by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .offset(y = (-70).dp),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Rekening: 018 - 412 - 8299", fontSize = 13.sp, color = Color.Gray)
                Spacer(modifier = Modifier.width(10.dp))
                IconButton(onClick = {}, modifier = Modifier.size(20.dp)) {
                    Icon(Icons.Default.ContentCopy, null, tint = Color.LightGray)
                }
            }

            Spacer(modifier = Modifier.height(20.dp))
            Text("Saldo Aktif", fontSize = 13.sp, color = Color.Gray)

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (isBalanceVisible) CurrencyFormatter.formatToRupiah(balance) else "IDR ••••••••",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF333333)
                )
                IconButton(onClick = { isBalanceVisible = !isBalanceVisible }) {
                    Icon(
                        if (isBalanceVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                        null,
                        tint = primary,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }

            HorizontalDivider(
                modifier = Modifier.padding(vertical = 16.dp),
                thickness = 0.5.dp,
                color = Color.LightGray
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onHistory() },
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Default.History,
                    null,
                    modifier = Modifier.size(20.dp),
                    tint = primary
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    "Mutasi Rekening",
                    fontSize = 15.sp,
                    color = primary,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
fun BannerSection(primary: Color) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .offset(y = (-50).dp)
            .height(70.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = primary.copy(alpha = 0.08f))
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    Icons.Default.CardGiftcard,
                    null,
                    tint = primary,
                    modifier = Modifier.size(28.dp)
                )
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text("Gebyar Hadiah Dummy", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    Text("Menangkan di Sini", fontSize = 12.sp, color = primary)
                }
            }
            Icon(Icons.Default.ChevronRight, null, tint = primary)
        }
    }
}

@Composable
fun MenuGrid(onTransfer: () -> Unit, onDefault: () -> Unit, primary: Color) {
    val menus = listOf(
        Triple("Transfer", Icons.AutoMirrored.Filled.Send, onTransfer),
        Triple("Bayar & Isi", Icons.Default.Receipt, onDefault),
        Triple("Lifestyle", Icons.Default.ShoppingBag, onDefault),
        Triple("Semua", Icons.Default.Apps, onDefault)
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .offset(y = (-50).dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                "Menu Utama",
                fontWeight = FontWeight.ExtraBold,
                style = MaterialTheme.typography.titleMedium
            )
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.clickable {
                onDefault.invoke()
            }) {
                Icon(Icons.Default.Tune, null, tint = primary, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Atur", color = primary, fontSize = 14.sp, fontWeight = FontWeight.Bold)
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            for (j in 0 until 4) {
                val menu = menus[j]
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(12.dp))
                        .clickable { menu.third() },
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(54.dp)
                            .clip(CircleShape)
                            .background(primary.copy(alpha = 0.12f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            menu.second,
                            null,
                            tint = primary,
                            modifier = Modifier.size(26.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        menu.first,
                        fontSize = 11.sp,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                        fontWeight = FontWeight.Medium,
                        lineHeight = 14.sp
                    )
                }
            }
        }
    }
}

@Composable
fun TransactionItem(transaction: Transaction) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 6.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(1.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Column {
                    Text(transaction.recipient, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    Text(transaction.date, fontSize = 12.sp, color = Color.Gray)
                }
            }
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    CurrencyFormatter.formatToRupiah(transaction.amount),
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 14.sp,
                    color = if (transaction.status == "Success") Color(0xFF2E7D32) else Color(
                        0xFFD32F2F
                    )
                )
                Text(
                    transaction.status,
                    fontSize = 11.sp,
                    color = if (transaction.status == "Success") Color(0xFF2E7D32) else Color(
                        0xFFD32F2F
                    )
                )
            }
        }
    }
}
