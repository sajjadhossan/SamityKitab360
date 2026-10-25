package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.repository.SamityRepository
import com.example.ui.language.LanguageManager
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BackupExportScreen(
    repository: SamityRepository,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val isBn = LanguageManager.isBangla
    val samity = repository.activeSamity
    val members = repository.activeMembers
    val deposits = repository.activeDeposits
    val expenses = repository.activeExpenses

    var generatedData by remember { mutableStateOf<String?>(null) }
    var exportType by remember { mutableStateOf("JSON") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = if (isBn) "ব্যাকআপ, এক্সপোর্ট ও ইমপোর্ট" else "Backup, Export & Import",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            text = if (isBn) "সমিতির সকল ডাটাবেজ ব্যাকআপ ও সিএসভি রূপান্তর" else "Complete Database Backup & CSV Export",
                            fontSize = 11.sp,
                            color = SamityGold
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = SamityNavyPrimary)
            )
        }
    ) { paddingValues ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .background(SlateBackground)
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Stats Card
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = CardDefaults.outlinedCardBorder()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = if (isBn) "বর্তমান ডাটাবেজের সারসংক্ষেপ" else "Current Database Statistics",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = SamityNavyDark
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        StatPill(label = if (isBn) "সদস্য" else "Members", count = members.size)
                        StatPill(label = if (isBn) "জমা" else "Deposits", count = deposits.size)
                        StatPill(label = if (isBn) "ব্যয়" else "Expenses", count = expenses.size)
                        StatPill(label = if (isBn) "সমিতি" else "Samities", count = repository.samities.size)
                    }
                }
            }

            // Export Actions Card
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = CardDefaults.outlinedCardBorder()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = if (isBn) "এক্সপোর্ট অপশন নির্বাচন করুন" else "Select Export Option",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = SamityNavyDark
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    Button(
                        onClick = {
                            exportType = "JSON"
                            generatedData = repository.exportBackupJson()
                            Toast.makeText(context, if (isBn) "সম্পূর্ণ JSON ব্যাকআপ প্রস্তুত!" else "Full JSON Backup Ready!", Toast.LENGTH_SHORT).show()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = SamityNavyPrimary),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth().testTag("export_json_backup_btn")
                    ) {
                        Icon(Icons.Default.DataObject, contentDescription = null, tint = Color.White)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(if (isBn) "সম্পূর্ণ ডাটাবেজ ব্যাকআপ (Full JSON)" else "Full Database Backup (JSON)")
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedButton(
                            onClick = {
                                exportType = "CSV (Members)"
                                generatedData = repository.exportMembersCsv()
                                Toast.makeText(context, if (isBn) "সদস্য CSV প্রস্তুত!" else "Members CSV Ready!", Toast.LENGTH_SHORT).show()
                            },
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(Icons.Default.TableChart, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(if (isBn) "সদস্য CSV" else "Members CSV", fontSize = 11.sp)
                        }

                        OutlinedButton(
                            onClick = {
                                exportType = "CSV (Deposits)"
                                generatedData = repository.exportDepositsCsv()
                                Toast.makeText(context, if (isBn) "জমা ভাউচার CSV প্রস্তুত!" else "Deposits CSV Ready!", Toast.LENGTH_SHORT).show()
                            },
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(Icons.Default.Receipt, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(if (isBn) "জমা CSV" else "Deposits CSV", fontSize = 11.sp)
                        }
                    }
                }
            }

            // Preview & Copy Card (if data generated)
            generatedData?.let { data ->
                Card(
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = CardDefaults.outlinedCardBorder()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "${if (isBn) "ব্যাকআপ ফাইল: " else "Backup File: "} $exportType (${data.length} bytes)",
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                color = SamityNavyPrimary
                            )
                            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                IconButton(
                                    onClick = {
                                        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                        val clip = ClipData.newPlainText("SamityBackup", data)
                                        clipboard.setPrimaryClip(clip)
                                        Toast.makeText(context, if (isBn) "ক্লিপবোর্ডে কপি সম্পন্ন!" else "Copied to clipboard!", Toast.LENGTH_SHORT).show()
                                    }
                                ) {
                                    Icon(Icons.Default.ContentCopy, contentDescription = "Copy", tint = SamityNavyPrimary)
                                }
                                IconButton(
                                    onClick = {
                                        val sendIntent: Intent = Intent().apply {
                                            action = Intent.ACTION_SEND
                                            putExtra(Intent.EXTRA_TEXT, data)
                                            type = "text/plain"
                                        }
                                        context.startActivity(Intent.createChooser(sendIntent, if (isBn) "ব্যাকআপ শেয়ার করুন" else "Share Backup"))
                                    }
                                ) {
                                    Icon(Icons.Default.Share, contentDescription = "Share", tint = SamityGold)
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = Color(0xFFF8FAFC),
                            border = CardDefaults.outlinedCardBorder(),
                            modifier = Modifier.fillMaxWidth().heightIn(max = 240.dp)
                        ) {
                            Text(
                                text = data.take(1500) + if (data.length > 1500) "\n... [সম্পূর্ণ ডাটা কপি করতে কপি বাটনে চাপুন]" else "",
                                fontFamily = FontFamily.Monospace,
                                fontSize = 10.sp,
                                color = Color(0xFF334155),
                                modifier = Modifier.padding(10.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun StatPill(label: String, count: Int) {
    Surface(
        shape = RoundedCornerShape(8.dp),
        color = SamityNavyPrimary.copy(alpha = 0.08f)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
        ) {
            Text(label, fontSize = 10.sp, color = Color.Gray)
            Text("$count", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = SamityNavyPrimary)
        }
    }
}
