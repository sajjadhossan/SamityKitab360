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
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Deposit
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
fun PaidReceiptScreen(
    repository: SamityRepository,
    deposit: Deposit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val isBn = LanguageManager.isBangla
    val samity = repository.activeSamity
    val member = repository.activeMembers.firstOrNull { it.id == deposit.memberId }

    val amountInBnWords = NumberToWords.convertBangla(deposit.amount)
    val amountInEnWords = NumberToWords.convertEnglish(deposit.amount)

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = if (isBn) "পরিশোধ রিসিট প্রিভিউ ও প্রিন্ট" else "Payment Receipt Preview & Print",
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
                    // WhatsApp Share
                    IconButton(
                        onClick = {
                            val msg = """
                                *${samity.nameBn}*
                                *পরিশোধ রিসিট / PAYMENT RECEIPT*
                                রিসিট নং: ${deposit.voucherNo}
                                তারিখ: ${deposit.depositDate}
                                সদস্য: ${deposit.memberName} (${deposit.memberCode})
                                জমার বিবরণ: ${deposit.depositType} (${deposit.paymentMonth})
                                পরিমাণ: ৳ ${deposit.amount.toInt()} (${amountInBnWords})
                                মাধ্যম: ${deposit.depositMethod}
                                ব্যাংক রিসিট: ${deposit.bankReceiptNo}
                                
                                আপনার পেমেন্ট সফলভাবে গৃহীত হয়েছে। ধন্যবাদ!
                            """.trimIndent()
                            val phone = member?.whatsappNumber?.ifBlank { member.phone } ?: samity.whatsappNumber
                            val cleanNumber = phone.replace("+", "").replace("-", "").replace(" ", "")
                            val url = "https://wa.me/$cleanNumber?text=${URLEncoder.encode(msg, "UTF-8")}"
                            try {
                                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                                context.startActivity(intent)
                            } catch (e: Exception) {
                                Toast.makeText(context, "WhatsApp not available", Toast.LENGTH_SHORT).show()
                            }
                        }
                    ) {
                        Icon(Icons.Default.Share, contentDescription = "Share", tint = Color.White)
                    }

                    // Print Button
                    IconButton(
                        onClick = {
                            printReceiptHtml(context, samity, deposit, amountInBnWords, amountInEnWords)
                        },
                        modifier = Modifier.testTag("print_receipt_action")
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
            // High-fidelity Printable Receipt Card
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = CardDefaults.outlinedCardBorder(),
                elevation = CardDefaults.cardElevation(3.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("printable_receipt_container")
            ) {
                Box(modifier = Modifier.fillMaxWidth()) {
                    // Watermark: PAID / পরিশোধিত diagonally
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(top = 160.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "PAID\nপরিশোধিত",
                            fontSize = 46.sp,
                            fontWeight = FontWeight.Black,
                            color = Color(0xFF16A34A).copy(alpha = 0.08f),
                            textAlign = TextAlign.Center,
                            modifier = Modifier.rotate(-25f)
                        )
                    }

                    Column(modifier = Modifier.fillMaxWidth()) {
                        // 1. Official Samity Letterhead Header
                        LetterheadHeader(samity = samity)

                        // 2. Receipt Title
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(SamityNavyPrimary.copy(alpha = 0.06f))
                                .padding(vertical = 8.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "পরিশোধ রিসিট / PAYMENT RECEIPT",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = SamityNavyPrimary
                            )
                            Text(
                                text = "মূল কপি / Original Office & Member Copy",
                                fontSize = 10.sp,
                                color = Color(0xFF64748B)
                            )
                        }

                        // 3. Receipt No & Date
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "রিসিট নং: ${deposit.voucherNo}",
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.5.sp,
                                color = SamityNavyPrimary
                            )
                            Text(
                                text = "তারিখ: ${deposit.depositDate}",
                                fontSize = 11.5.sp,
                                color = Color(0xFF334155)
                            )
                        }

                        Divider(modifier = Modifier.padding(horizontal = 16.dp))

                        // 4. Member Details
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            ReceiptFieldRow("সদস্য আইডি / Member ID", deposit.memberCode, isBold = true)
                            ReceiptFieldRow("সদস্যের নাম / Member Name", deposit.memberName, isBold = true)
                            ReceiptFieldRow("ফোন নম্বর / Mobile", member?.phone ?: "-")
                            ReceiptFieldRow("হোয়াটসঅ্যাপ / WhatsApp", member?.whatsappNumber ?: "-")

                            Divider(modifier = Modifier.padding(vertical = 4.dp))

                            // 5. Deposit Details
                            ReceiptFieldRow("জমার ধরন / Deposit Type", deposit.depositType)
                            ReceiptFieldRow("জমার মাস / Payment Month", deposit.paymentMonth)
                            ReceiptFieldRow("জমার পরিমাণ / Amount", NumberToWords.formatCurrency(deposit.amount, isBn), isBold = true, valueColor = StatusPaidGreen)
                            ReceiptFieldRow("কথায় (বাংলায়)", amountInBnWords, isItalic = true)
                            ReceiptFieldRow("In Words (English)", amountInEnWords, isItalic = true)

                            Divider(modifier = Modifier.padding(vertical = 4.dp))

                            // 6. Bank & Payment Gateway
                            ReceiptFieldRow("ব্যাংকের নাম / Bank Name", deposit.bankName)
                            ReceiptFieldRow("ব্রাঞ্চ / Branch", deposit.bankBranch)
                            ReceiptFieldRow("অ্যাকাউন্ট নং / Account No", deposit.bankAccountNo)
                            ReceiptFieldRow("ব্যাংক রিসিট নং / Bank Slip", deposit.bankReceiptNo)
                            ReceiptFieldRow("জমার মাধ্যম / Payment Method", deposit.depositMethod)
                            ReceiptFieldRow("গ্রহণকারী / Received By", deposit.entryBy)
                            if (deposit.remarks.isNotBlank()) {
                                ReceiptFieldRow("মন্তব্য / Remarks", deposit.remarks)
                            }
                        }

                        // 7. QR Code & Verification Banner
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Surface(
                                    shape = RoundedCornerShape(4.dp),
                                    color = StatusPaidBg
                                ) {
                                    Text(
                                        text = "পরিশোধিত / PAID",
                                        color = StatusPaidGreen,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 11.sp,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                    )
                                }
                                Text(
                                    text = "অনলাইন ভেরিফিকেশন কোড: ${deposit.voucherNo.takeLast(6)}",
                                    fontSize = 9.sp,
                                    color = Color.Gray,
                                    modifier = Modifier.padding(top = 4.dp)
                                )
                            }

                            QrCodeCanvas(
                                content = "VERIFY:SAMITY:${samity.id}:RCPT:${deposit.voucherNo}:${deposit.amount}",
                                size = 68.dp
                            )
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // 8. Triple Authorized Signatures (President, General Secretary, Treasurer)
                        LetterheadSignatureBar(signatories = repository.signatories)

                        // 9. Letterhead Footer
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(SamityNavyPrimary)
                                .padding(vertical = 8.dp, horizontal = 12.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = if (isBn) repository.letterheadConfig.footerTextBn else repository.letterheadConfig.footerTextEn,
                                fontSize = 9.5.sp,
                                color = Color.White,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedButton(
                    onClick = {
                        val shareIntent = Intent(Intent.ACTION_SEND).apply {
                            type = "text/plain"
                            putExtra(Intent.EXTRA_SUBJECT, "পরিশোধ রিসিট - ${deposit.voucherNo}")
                            putExtra(
                                Intent.EXTRA_TEXT,
                                "স্বপ্নছোঁয়া সমবায় সমিতি রিসিট নং: ${deposit.voucherNo}, সদস্য: ${deposit.memberName}, পরিমাণ: ৳ ${deposit.amount.toInt()}"
                            )
                        }
                        context.startActivity(Intent.createChooser(shareIntent, "Share Receipt"))
                    },
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(Icons.Default.Share, contentDescription = null, tint = SamityNavyPrimary, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(if (isBn) "শেয়ার করুন" else "Share", color = SamityNavyPrimary)
                }

                Button(
                    onClick = {
                        printReceiptHtml(context, samity, deposit, amountInBnWords, amountInEnWords)
                    },
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = SamityNavyPrimary),
                    modifier = Modifier.weight(1f).testTag("print_button_bottom")
                ) {
                    Icon(Icons.Default.Print, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(if (isBn) "প্রিন্ট রিসিট" else "Print Receipt", color = Color.White)
                }
            }
        }
    }
}

@Composable
private fun ReceiptFieldRow(
    label: String,
    value: String,
    isBold: Boolean = false,
    isItalic: Boolean = false,
    valueColor: Color = SlateTextPrimary
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            fontSize = 11.5.sp,
            color = Color(0xFF64748B),
            modifier = Modifier.weight(1f)
        )
        Text(
            text = value,
            fontSize = 12.sp,
            fontWeight = if (isBold) FontWeight.Bold else FontWeight.Normal,
            fontStyle = if (isItalic) androidx.compose.ui.text.font.FontStyle.Italic else androidx.compose.ui.text.font.FontStyle.Normal,
            color = valueColor,
            textAlign = TextAlign.End,
            modifier = Modifier.weight(1.4f)
        )
    }
}

private fun printReceiptHtml(
    context: Context,
    samity: com.example.data.model.Samity,
    deposit: Deposit,
    bnWords: String,
    enWords: String
) {
    val html = """
        <!DOCTYPE html>
        <html>
        <head>
            <meta charset="utf-8">
            <title>Receipt ${deposit.voucherNo}</title>
            <style>
                body { font-family: 'SolaimanLipi', Arial, sans-serif; margin: 20px; color: #1e293b; }
                .header { text-align: center; border-bottom: 2px solid #0f3b66; padding-bottom: 8px; }
                .title { font-size: 20px; font-weight: bold; color: #0f3b66; }
                .slogan { font-size: 13px; color: #475569; font-style: italic; }
                .receipt-title { background: #f1f5f9; padding: 6px; font-weight: bold; margin-top: 10px; font-size: 15px; }
                .meta { display: flex; justify-content: space-between; margin: 12px 0; font-weight: bold; font-size: 13px; }
                table { width: 100%; border-collapse: collapse; margin-top: 10px; }
                td { padding: 6px; border-bottom: 1px solid #e2e8f0; font-size: 13px; }
                .label { color: #64748b; width: 45%; }
                .value { font-weight: bold; text-align: right; }
                .watermark { text-align: center; font-size: 40px; font-weight: bold; color: #16a34a; opacity: 0.2; margin: 20px 0; }
                .signatures { display: flex; justify-content: space-between; margin-top: 40px; text-align: center; font-size: 12px; }
                .sig-box { width: 30%; border-top: 1px dashed #64748b; padding-top: 6px; }
            </style>
        </head>
        <body>
            <div class="header">
                <div class="title">${samity.nameBn}</div>
                <div class="slogan">স্লোগান: ${samity.sloganBn}</div>
                <div style="font-size: 11px; color: #64748b;">${samity.addressBn} | ফোন: ${samity.phone} | রেজি: ${samity.regNo}</div>
            </div>
            
            <div class="receipt-title" style="text-align: center;">পরিশোধ রিসিট / PAYMENT RECEIPT</div>
            
            <div class="meta">
                <span>রিসিট নং: ${deposit.voucherNo}</span>
                <span>তারিখ: ${deposit.depositDate}</span>
            </div>
            
            <table>
                <tr><td class="label">সদস্য আইডি</td><td class="value">${deposit.memberCode}</td></tr>
                <tr><td class="label">সদস্যের নাম</td><td class="value">${deposit.memberName}</td></tr>
                <tr><td class="label">জমার ধরন</td><td class="value">${deposit.depositType}</td></tr>
                <tr><td class="label">জমার মাস</td><td class="value">${deposit.paymentMonth}</td></tr>
                <tr><td class="label">জমার পরিমাণ</td><td class="value" style="color: #16a34a; font-size: 16px;">৳ ${deposit.amount.toInt()}</td></tr>
                <tr><td class="label">কথায় (বাংলায়)</td><td class="value" style="font-weight: normal; font-style: italic;">${bnWords}</td></tr>
                <tr><td class="label">In Words (English)</td><td class="value" style="font-weight: normal; font-style: italic;">${enWords}</td></tr>
                <tr><td class="label">ব্যাংকের নাম</td><td class="value">${deposit.bankName}</td></tr>
                <tr><td class="label">ব্রাঞ্চ ও একাউন্ট</td><td class="value">${deposit.bankBranch} (${deposit.bankAccountNo})</td></tr>
                <tr><td class="label">ব্যাংক স্লিপ নং</td><td class="value">${deposit.bankReceiptNo}</td></tr>
                <tr><td class="label">জমার মাধ্যম</td><td class="value">${deposit.depositMethod}</td></tr>
                <tr><td class="label">গ্রহণকারী</td><td class="value">${deposit.entryBy}</td></tr>
            </table>
            
            <div class="watermark">PAID / পরিশোধিত</div>
            
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
            
            <div style="margin-top: 30px; text-align: center; font-size: 10px; color: #64748b; border-top: 1px solid #cbd5e1; padding-top: 6px;">
                হিসাব কিতাব ৩৬০ ডিগ্রি সমবায় সফটওয়্যার দ্বারা সংক্রিয়ভাবে প্রস্তুতকৃত।
            </div>
        </body>
        </html>
    """.trimIndent()

    val webView = WebView(context)
    webView.webViewClient = object : WebViewClient() {
        override fun onPageFinished(view: WebView?, url: String?) {
            val printManager = context.getSystemService(Context.PRINT_SERVICE) as? PrintManager
            val printAdapter = webView.createPrintDocumentAdapter("Receipt_${deposit.voucherNo}")
            val printAttributes = PrintAttributes.Builder()
                .setMediaSize(PrintAttributes.MediaSize.ISO_A4)
                .setResolution(PrintAttributes.Resolution("id", "res", 300, 300))
                .setMinMargins(PrintAttributes.Margins.NO_MARGINS)
                .build()
            printManager?.print("Receipt_${deposit.voucherNo}", printAdapter, printAttributes)
        }
    }
    webView.loadDataWithBaseURL(null, html, "text/html", "UTF-8", null)
}
