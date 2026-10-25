package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AuditLog
import com.example.data.repository.SamityRepository
import com.example.ui.language.LanguageManager
import com.example.ui.theme.*

@Composable
fun AuditTrailScreen(
    repository: SamityRepository,
    modifier: Modifier = Modifier
) {
    val isBn = LanguageManager.isBangla
    val auditLogs = repository.activeAuditLogs
    var searchQuery by remember { mutableStateOf("") }

    val filteredLogs = auditLogs.filter {
        it.actionBn.contains(searchQuery, ignoreCase = true) ||
                it.actionEn.contains(searchQuery, ignoreCase = true) ||
                it.details.contains(searchQuery, ignoreCase = true) ||
                it.performedBy.contains(searchQuery, ignoreCase = true)
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(SlateBackground)
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = if (isBn) "সিস্টেম অডিট ট্রেইল ও নিরাপত্তা লগ" else "System Audit Trail & Security Logs",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = SamityNavyPrimary
                )
                Text(
                    text = if (isBn) "কে কখন কি লেনদেন বা ডাটা পরিবর্তন করেছেন তার পূর্ণাঙ্গ রেকর্ড" else "Complete trace of all deposits, updates, and reminders",
                    fontSize = 11.5.sp,
                    color = Color(0xFF64748B)
                )
            }

            Surface(
                shape = RoundedCornerShape(8.dp),
                color = SamityNavyPrimary.copy(alpha = 0.1f)
            ) {
                Text(
                    text = "${auditLogs.size} Logs",
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    color = SamityNavyPrimary,
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            placeholder = { Text(if (isBn) "অডিট লগ বা কার্যক্রম খুঁজুন..." else "Search audit records...") },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = SamityNavyPrimary) },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(10.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White
            )
        )

        Spacer(modifier = Modifier.height(12.dp))

        if (filteredLogs.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(if (isBn) "কোন অডিট লগ পাওয়া যায়নি" else "No audit logs found", color = Color.Gray)
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(filteredLogs) { log ->
                    AuditItemCard(log = log, isBn = isBn)
                }
            }
        }
    }
}

@Composable
private fun AuditItemCard(log: AuditLog, isBn: Boolean) {
    Card(
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = CardDefaults.outlinedCardBorder(),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(SamityNavyPrimary.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.Security, contentDescription = null, tint = SamityNavyPrimary, modifier = Modifier.size(18.dp))
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = if (isBn) log.actionBn else log.actionEn,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = SlateTextPrimary
                    )
                    Text(
                        text = log.timestamp,
                        fontSize = 10.sp,
                        color = Color(0xFF64748B)
                    )
                }
                Text(
                    text = "সম্পাদনকারী: ${log.performedBy} (${log.userRole})",
                    fontSize = 10.5.sp,
                    color = SamityNavyPrimary,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = log.details,
                    fontSize = 11.sp,
                    color = Color(0xFF334155),
                    lineHeight = 15.sp
                )
            }
        }
    }
}
