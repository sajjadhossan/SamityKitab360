package com.example.ui.screens

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.print.PrintAttributes
import android.print.PrintManager
import android.webkit.WebView
import android.webkit.WebViewClient
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
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Member
import com.example.data.repository.SamityRepository
import com.example.ui.components.LetterheadHeader
import com.example.ui.components.LetterheadSignatureBar
import com.example.ui.components.QrCodeCanvas
import com.example.ui.language.LanguageManager
import com.example.ui.theme.*
import com.example.util.NumberToWords
import java.net.URLEncoder

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DueNoticeScreen(
    repository: SamityRepository,
    member: Member,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val isBn = LanguageManager.isBangla
    val samity = repository.activeSamity
    val dueAmount = if (member.totalDue > 0) member.totalDue else member.monthlyFee

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = if (isBn) "বকেয়া তাগিদ নোটিশ" else "Due Subscription Notice",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                },
                actions = {
                    IconButton(
                        onClick = {
                            val msg = """
                                প্রিয় ${member.nameBn},
                                
                                ${samity.nameBn}-এর মাসিক চাঁদা বাবদ আপনার মোট ৳ ${dueAmount.toInt()} টাকা বকেয়া রয়েছে।
                                
                                জমার শেষ তারিখ: ${samity.dueDayOfMonth} অক্টোবর ২০২৬
                                ব্যাংক: ${samity.bankName} (হিসাব: ${samity.bankAccountNo})
                                বিকাশ মার্চেন্ট: ${samity.bkashNumber}
                                
                                বিশেষ অনুরোধ: নির্ধারিত সময়ের মধ্যে চাঁদা পরিশোধ করে সমিতির কার্যক্রমে সহযোগিতা করুন।
                                
                                ধন্যবাদান্তে,
                                ${samity.treasurerName} (কোষাধ্যক্ষ)
                                ${samity.phone}
                            """.trimIndent()
                            val cleanNumber = member.whatsappNumber.replace("+", "").replace("-", "").replace(" ", "")
                            val url = "https://wa.me/$cleanNumber?text=${URLEncoder.encode(msg, "UTF-8")}"
                            try {
                                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                                context.startActivity(intent)
                                repository.logReminder(member, "WhatsApp", "অক্টোবর ২০২৬", dueAmount, "Treasurer")
                            } catch (e: Exception) {
                                Toast.makeText(context, "WhatsApp not available", Toast.LENGTH_SHORT).show()
                            }
                        }
                    ) {
                        Icon(Icons.Default.Share, contentDescription = "Share", tint = Color.White)
                    }

                    IconButton(
                        onClick = {
                            printNoticeHtml(context, samity, member, dueAmount)
                        },
                        modifier = Modifier.testTag("print_due_notice_action")
                    ) {
                        Icon(Icons.Default.Print, contentDescription = "Print", tint = Color.White)
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
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = CardDefaults.outlinedCardBorder(),
                elevation = CardDefaults.cardElevation(2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    // 1. Samity Letterhead
                    LetterheadHeader(samity = samity)

                    // 2. Notice Title
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(StatusUnpaidRed.copy(alpha = 0.08f))
                            .padding(vertical = 10.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "মাসিক চাঁদা পরিশোধের বকেয়া নোটিশ / DUE NOTICE",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.5.sp,
                            color = StatusUnpaidRed
                        )
                    }

                    // 3. Body
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Text(
                            text = "স্মারক নং: ${samity.regNo}/DUE/2026/OCT",
                            fontSize = 11.sp,
                            color = Color(0xFF64748B)
                        )
                        Text(
                            text = "তারিখ: ২০ অক্টোবর ২০২৬",
                            fontSize = 11.sp,
                            color = Color(0xFF64748B)
                        )

                        Text(
                            text = "বরাবর,\n${member.nameBn} (${member.memberCode})\n${member.address}\nমোবাইল: ${member.phone}",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            lineHeight = 18.sp,
                            color = SlateTextPrimary
                        )

                        Text(
                            text = "বিষয়: অক্টোবর ২০২৬ মাসের বকেয়া চাঁদা পরিশোধ প্রসঙ্গে।",
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.5.sp,
                            color = SamityNavyPrimary
                        )

                        Text(
                            text = "জনাব,\nশুভেচ্ছা গ্রহণ করবেন। সমিতির হিসাব শাখা অনুযায়ী, আপনার নামে বর্তমান মাস (অক্টোবর ২০২৬) ও পূর্ববর্তী বকেয়া বাবদ মোট ${NumberToWords.formatCurrency(dueAmount, isBn)} (${NumberToWords.convertBangla(dueAmount)}) অদ্যাবধি অপরিশোধিত রয়েছে।\n\nসমিতির আর্থিক স্বচ্ছতা ও কল্যাণমূলক কার্যক্রম সচল রাখার স্বার্থে আগামী ${samity.dueDayOfMonth} অক্টোবর ২০২৬ তারিখের মধ্যে উল্লেখিত অর্থ নিম্নের ব্যাংক একাউন্ট অথবা সমিতির মোবাইল ব্যাংকিং নম্বরে পরিশোধ করার জন্য বিনীত অনুরোধ জানানো যাচ্ছে।",
                            fontSize = 12.sp,
                            lineHeight = 19.sp,
                            color = Color(0xFF334155),
                            textAlign = TextAlign.Justify
                        )

                        // Bank Account Box
                        Card(
                            colors = CardDefaults.cardColors(containerColor = Color(0xFFF1F5F9)),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                Text(
                                    text = "টাকা জমা দেয়ার ব্যাংক ও মোবাইল ব্যাংকিং বিবরণ:",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 11.5.sp,
                                    color = SamityNavyPrimary
                                )
                                Text("• ব্যাংক: ${samity.bankName} (${samity.bankBranch})", fontSize = 11.sp)
                                Text("• একাউন্ট নং: ${samity.bankAccountNo}", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                Text("• বিকাশ / নগদ (মার্চেন্ট): ${samity.bkashNumber}", fontSize = 11.sp)
                                Text("• কোষাধ্যক্ষের ফোন: ${samity.treasurerPhone}", fontSize = 11.sp)
                            }
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "টাকা জমা দেওয়ার পর স্লিপ বা ট্রানজেকশন আইডির স্ক্রিনশট কোষাধ্যক্ষকে পাঠাবেন।",
                                fontSize = 10.sp,
                                color = Color(0xFF64748B),
                                modifier = Modifier.weight(1f)
                            )
                            QrCodeCanvas(content = "SAMITY:DUE:${member.memberCode}:$dueAmount", size = 60.dp)
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Signatures
                    LetterheadSignatureBar(signatories = repository.signatories)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Button(
                    onClick = {
                        val msg = "প্রিয় ${member.nameBn}, ${samity.nameBn}-এর বকেয়া চাঁদা ${dueAmount.toInt()} টাকা পরিশোধ করার অনুরোধ। শেষ তারিখ: ${samity.dueDayOfMonth} অক্টোবর।"
                        val cleanNumber = member.phone.replace("+", "").replace("-", "")
                        val uri = Uri.parse("smsto:$cleanNumber")
                        val smsIntent = Intent(Intent.ACTION_SENDTO, uri).apply {
                            putExtra("sms_body", msg)
                        }
                        try {
                            context.startActivity(smsIntent)
                            repository.logReminder(member, "SMS", "অক্টোবর ২০২৬", dueAmount, "Treasurer")
                        } catch (e: Exception) {
                            Toast.makeText(context, "SMS app not found", Toast.LENGTH_SHORT).show()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = SamityGold),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(Icons.Default.Sms, contentDescription = null, tint = SamityNavyDark, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("SMS নোটিশ", color = SamityNavyDark, fontWeight = FontWeight.Bold)
                }

                Button(
                    onClick = {
                        printNoticeHtml(context, samity, member, dueAmount)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = SamityNavyPrimary),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(Icons.Default.Print, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(if (isBn) "প্রিন্ট নোটিশ" else "Print Notice", color = Color.White)
                }
            }
        }
    }
}

private fun printNoticeHtml(
    context: Context,
    samity: com.example.data.model.Samity,
    member: Member,
    amount: Double
) {
    val html = """
        <!DOCTYPE html>
        <html>
        <head>
            <meta charset="utf-8">
            <title>Due Notice ${member.memberCode}</title>
            <style>
                body { font-family: 'SolaimanLipi', Arial, sans-serif; margin: 30px; color: #1e293b; }
                .header { text-align: center; border-bottom: 2px solid #0f3b66; padding-bottom: 10px; }
                .title { font-size: 22px; font-weight: bold; color: #0f3b66; }
                .notice-badge { background: #fee2e2; color: #dc2626; padding: 6px; text-align: center; font-weight: bold; margin-top: 15px; font-size: 14px; }
                .content { margin-top: 20px; line-height: 1.8; font-size: 14px; text-align: justify; }
                .bank-box { background: #f8fafc; border: 1px solid #cbd5e1; padding: 12px; margin: 20px 0; border-radius: 6px; }
                .signatures { display: flex; justify-content: space-between; margin-top: 50px; text-align: center; font-size: 13px; }
                .sig-box { width: 30%; border-top: 1px dashed #64748b; padding-top: 6px; }
            </style>
        </head>
        <body>
            <div class="header">
                <div class="title">${samity.nameBn}</div>
                <div>স্লোগান: ${samity.sloganBn}</div>
                <div style="font-size: 11px; color: #64748b;">${samity.addressBn} | ফোন: ${samity.phone}</div>
            </div>
            
            <div class="notice-badge">মাসিক চাঁদা পরিশোধের বকেয়া তাগিদপত্র / DUE NOTICE</div>
            
            <div class="content">
                <div><b>তারিখ:</b> ২০ অক্টোবর ২০২৬</div>
                <div style="margin-top: 10px;">
                    <b>বরাবর,</b><br>
                    ${member.nameBn} (${member.memberCode})<br>
                    মোবাইল: ${member.phone}
                </div>
                
                <p><b>বিষয়: অক্টোবর ২০২৬ মাসের বকেয়া চাঁদা পরিশোধ প্রসঙ্গে।</b></p>
                <p>জনাব, শুভেচ্ছা গ্রহণ করবেন। সমিতির হিসাব অনুযায়ী আপনার মোট বকেয়া চাঁদার পরিমাণ <b>৳ ${amount.toInt()} (${NumberToWords.convertBangla(amount)})</b> অদ্যাবধি অপরিশোধিত রয়েছে। নির্ধারিত সময়ের মধ্যে নিম্নের ব্যাংক একাউন্টে জমা দেওয়ার জন্য অনুরোধ করা হলো।</p>
                
                <div class="bank-box">
                    <b>টাকা জমা দেওয়ার তথ্য:</b><br>
                    ব্যাংক: ${samity.bankName} (${samity.bankBranch})<br>
                    একাউন্ট নম্বর: ${samity.bankAccountNo}<br>
                    বিকাশ/নগদ মার্চেন্ট: ${samity.bkashNumber}<br>
                    যোগাযোগ: ${samity.treasurerName} (${samity.treasurerPhone})
                </div>
            </div>
            
            <div class="signatures">
                <div class="sig-box">
                    <div><b>সভাপতি</b></div>
                    <div>${samity.presidentName}</div>
                </div>
                <div class="sig-box">
                    <div><b>সাধারণ সম্পাদক</b></div>
                    <div>${samity.secretaryName}</div>
                </div>
                <div class="sig-box">
                    <div><b>কোষাধ্যক্ষ</b></div>
                    <div>${samity.treasurerName}</div>
                </div>
            </div>
        </body>
        </html>
    """.trimIndent()

    val webView = WebView(context)
    webView.webViewClient = object : WebViewClient() {
        override fun onPageFinished(view: WebView?, url: String?) {
            val printManager = context.getSystemService(Context.PRINT_SERVICE) as? PrintManager
            val printAdapter = webView.createPrintDocumentAdapter("Due_Notice_${member.memberCode}")
            printManager?.print("Due_Notice_${member.memberCode}", printAdapter, PrintAttributes.Builder().build())
        }
    }
    webView.loadDataWithBaseURL(null, html, "text/html", "UTF-8", null)
}
