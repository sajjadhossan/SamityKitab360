package com.example.ui.screens

import android.content.Context
import android.print.PrintAttributes
import android.print.PrintManager
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.repository.SamityRepository
import com.example.ui.language.LanguageManager
import com.example.ui.theme.*
import com.example.util.NumberToWords

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportsScreen(
    repository: SamityRepository,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val isBn = LanguageManager.isBangla
    val samity = repository.activeSamity
    val members = repository.activeMembers
    val deposits = repository.activeDeposits
    val expenses = repository.activeExpenses

    var selectedReportType by remember { mutableStateOf("MEMBER_COLLECTION") }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(SlateBackground)
            .padding(16.dp)
    ) {
        // Screen Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = if (isBn) "সমিতি আর্থিক ও হিসাব রিপোর্ট" else "Financial & Audit Reports",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = SamityNavyPrimary
                )
                Text(
                    text = if (isBn) "মাসিক কালেকশন, বকেয়া এবং ব্যাংক বিবরণী" else "Monthly collection, due & bank reports",
                    fontSize = 11.5.sp,
                    color = Color(0xFF64748B)
                )
            }

            Button(
                onClick = {
                    printReportHtml(context, samity, selectedReportType, repository)
                },
                colors = ButtonDefaults.buttonColors(containerColor = SamityNavyPrimary),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.testTag("print_full_report_btn")
            ) {
                Icon(Icons.Default.Print, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text(if (isBn) "প্রিন্ট রিপোর্ট" else "Print Report", color = Color.White, fontSize = 11.sp)
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Report Type Selector Chips
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            FilterChip(
                selected = selectedReportType == "MEMBER_COLLECTION",
                onClick = { selectedReportType = "MEMBER_COLLECTION" },
                label = { Text(if (isBn) "আদায় রিপোর্ট" else "Collection", fontSize = 10.5.sp) }
            )
            FilterChip(
                selected = selectedReportType == "DUE_REPORT",
                onClick = { selectedReportType = "DUE_REPORT" },
                label = { Text(if (isBn) "বকেয়া রিপোর্ট" else "Due Report", fontSize = 10.5.sp) }
            )
            FilterChip(
                selected = selectedReportType == "BANK_REPORT",
                onClick = { selectedReportType = "BANK_REPORT" },
                label = { Text(if (isBn) "ব্যাংক স্টেটমেন্ট" else "Bank Report", fontSize = 10.5.sp) }
            )
            FilterChip(
                selected = selectedReportType == "EXPENSE_REPORT",
                onClick = { selectedReportType = "EXPENSE_REPORT" },
                label = { Text(if (isBn) "ব্যয় রিপোর্ট" else "Expenses", fontSize = 10.5.sp) }
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Table Header
        Card(
            shape = RoundedCornerShape(8.dp),
            colors = CardDefaults.cardColors(containerColor = SamityNavyPrimary),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(if (isBn) "বিবরণ / সদস্য" else "Details / Member", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1.5f))
                Text(if (isBn) "মাধ্যম / রেফারেন্স" else "Method / Ref", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
                Text(if (isBn) "পরিমাণ" else "Amount", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f), textAlign = androidx.compose.ui.text.style.TextAlign.End)
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        // Report Content Rows
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            when (selectedReportType) {
                "MEMBER_COLLECTION" -> {
                    items(deposits) { dep ->
                        ReportRowCard(
                            col1 = "${dep.memberName} (${dep.memberCode})",
                            col2 = "${dep.voucherNo} • ${dep.depositDate}",
                            col3 = NumberToWords.formatCurrency(dep.amount, isBn),
                            isPositive = true
                        )
                    }
                }
                "DUE_REPORT" -> {
                    val dueMembers = members.filter { it.totalDue > 0 }
                    items(dueMembers) { mem ->
                        ReportRowCard(
                            col1 = "${mem.nameBn} (${mem.memberCode})",
                            col2 = "ফোন: ${mem.phone}",
                            col3 = NumberToWords.formatCurrency(mem.totalDue, isBn),
                            isPositive = false
                        )
                    }
                }
                "BANK_REPORT" -> {
                    items(deposits.filter { it.depositMethod.contains("Bank") || it.depositMethod.contains("Online") }) { dep ->
                        ReportRowCard(
                            col1 = "${dep.bankName} (${dep.bankReceiptNo})",
                            col2 = "${dep.memberName} (${dep.depositDate})",
                            col3 = NumberToWords.formatCurrency(dep.amount, isBn),
                            isPositive = true
                        )
                    }
                }
                "EXPENSE_REPORT" -> {
                    items(expenses) { exp ->
                        ReportRowCard(
                            col1 = exp.titleBn,
                            col2 = "${exp.voucherNo} • ${exp.category}",
                            col3 = NumberToWords.formatCurrency(exp.amount, isBn),
                            isPositive = false
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ReportRowCard(
    col1: String,
    col2: String,
    col3: String,
    isPositive: Boolean
) {
    Card(
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = CardDefaults.outlinedCardBorder(),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1.5f)) {
                Text(col1, fontWeight = FontWeight.Bold, fontSize = 12.sp, color = SlateTextPrimary)
                Text(col2, fontSize = 10.sp, color = Color(0xFF64748B))
            }
            Text(
                text = col3,
                fontSize = 12.5.sp,
                fontWeight = FontWeight.Bold,
                color = if (isPositive) StatusPaidGreen else StatusUnpaidRed,
                textAlign = androidx.compose.ui.text.style.TextAlign.End,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

private fun printReportHtml(
    context: Context,
    samity: com.example.data.model.Samity,
    reportType: String,
    repository: SamityRepository
) {
    val title = when (reportType) {
        "MEMBER_COLLECTION" -> "মাসিক চাঁদা আদায় প্রতিবেদন (Collection Report)"
        "DUE_REPORT" -> "সদস্য বকেয়া তালিকা ও রিপোর্ট (Due Report)"
        "BANK_REPORT" -> "ব্যাংক হিসাব বিবরণী (Bank Statement)"
        else -> "ব্যয় ও খরচ ভাউচার প্রতিবেদন (Expense Report)"
    }

    val html = """
        <!DOCTYPE html>
        <html>
        <head>
            <meta charset="utf-8">
            <title>${title}</title>
            <style>
                body { font-family: 'SolaimanLipi', Arial, sans-serif; margin: 30px; }
                .header { text-align: center; border-bottom: 2px solid #0f3b66; padding-bottom: 10px; }
                .title { font-size: 20px; font-weight: bold; color: #0f3b66; }
                table { width: 100%; border-collapse: collapse; margin-top: 15px; }
                th, td { border: 1px solid #cbd5e1; padding: 6px; font-size: 11px; text-align: left; }
                th { background-color: #0f3b66; color: white; }
                .summary { margin-top: 20px; font-size: 13px; font-weight: bold; text-align: right; }
            </style>
        </head>
        <body>
            <div class="header">
                <div class="title">${samity.nameBn}</div>
                <div>স্লোগান: ${samity.sloganBn} | রেজি: ${samity.regNo}</div>
                <div style="font-size: 12px; margin-top: 5px;">${title} - অক্টোবর ২০২৬</div>
            </div>
            
            <table>
                <tr>
                    <th>ক্রমিক</th>
                    <th>সদস্য/খাত</th>
                    <th>আইডি/ভাউচার</th>
                    <th>তারিখ/বিবরণ</th>
                    <th>টাকার পরিমাণ</th>
                </tr>
                ${
        repository.activeDeposits.mapIndexed { idx, dep ->
            "<tr><td>${idx + 1}</td><td>${dep.memberName}</td><td>${dep.memberCode}</td><td>${dep.depositDate} (${dep.depositMethod})</td><td>৳ ${dep.amount.toInt()}</td></tr>"
        }.joinToString("\n")
    }
            </table>
            
            <div class="summary">
                মোট আদায়: ৳ ${repository.totalApprovedDeposits.toInt()} | মোট ব্যয়: ৳ ${repository.totalApprovedExpenses.toInt()} | নিট তহবিল: ৳ ${repository.netAvailableFund.toInt()}
            </div>
            
            <div style="margin-top: 40px; display: flex; justify-content: space-between;">
                <div><b>কোষাধ্যক্ষ</b><br>${samity.treasurerName}</div>
                <div><b>সাধারণ সম্পাদক</b><br>${samity.secretaryName}</div>
                <div><b>সভাপতি</b><br>${samity.presidentName}</div>
            </div>
        </body>
        </html>
    """.trimIndent()

    val webView = WebView(context)
    webView.webViewClient = object : WebViewClient() {
        override fun onPageFinished(view: WebView?, url: String?) {
            val printManager = context.getSystemService(Context.PRINT_SERVICE) as? PrintManager
            val printAdapter = webView.createPrintDocumentAdapter("Report_${reportType}")
            printManager?.print("Report_${reportType}", printAdapter, PrintAttributes.Builder().build())
        }
    }
    webView.loadDataWithBaseURL(null, html, "text/html", "UTF-8", null)
}
