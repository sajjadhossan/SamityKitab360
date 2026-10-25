package com.example.ui.screens

import android.content.Context
import android.print.PrintAttributes
import android.print.PrintManager
import android.webkit.WebView
import android.webkit.WebViewClient
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.repository.SamityRepository
import com.example.ui.components.LetterheadHeader
import com.example.ui.components.LetterheadSignatureBar
import com.example.ui.components.QrCodeCanvas
import com.example.ui.language.LanguageManager
import com.example.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DocumentGeneratorScreen(
    repository: SamityRepository,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val isBn = LanguageManager.isBangla
    val samity = repository.activeSamity
    val members = repository.activeMembers

    var selectedDocType by remember { mutableStateOf("CERTIFICATE") } // CERTIFICATE, MEETING_NOTICE, CIRCULAR
    var selectedMember by remember { mutableStateOf(members.firstOrNull()) }
    var meetingAgenda by remember { mutableStateOf("অক্টোবর ২০২৬ মাসের হিসাব অনুমোদন ও বার্ষিক সাধারণ সভার প্রস্তুতি") }
    var meetingDate by remember { mutableStateOf("৩০ অক্টোবর ২০২৬, বিকাল ৪:০০ ঘটিকা") }

    val currentDateStr = SimpleDateFormat("dd MMMM yyyy", Locale.getDefault()).format(Date())

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(SlateBackground)
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Screen Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = if (isBn) "অফিসিয়াল নথি ও সনদ জেনারেটর" else "Official Document Generator",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = SamityNavyPrimary
                )
                Text(
                    text = if (isBn) "লেটারহেড প্যাডে সদস্যতা সনদপত্র, সভার নোটিশ ও বিজ্ঞপ্তি প্রস্তুত করুন" else "Generate certificates, meeting notices & circulars with letterhead",
                    fontSize = 11.5.sp,
                    color = Color(0xFF64748B)
                )
            }

            Button(
                onClick = {
                    printDocHtml(context, samity, selectedDocType, selectedMember?.nameBn ?: "", meetingAgenda, meetingDate)
                },
                colors = ButtonDefaults.buttonColors(containerColor = SamityNavyPrimary),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.testTag("print_doc_generated_btn")
            ) {
                Icon(Icons.Default.Print, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text(if (isBn) "প্রিন্ট নথি" else "Print Doc", color = Color.White, fontSize = 11.sp)
            }
        }

        // Document Type Tabs
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            FilterChip(
                selected = selectedDocType == "CERTIFICATE",
                onClick = { selectedDocType = "CERTIFICATE" },
                label = { Text(if (isBn) "সদস্যতা সনদপত্র" else "Membership Certificate", fontSize = 11.sp) }
            )
            FilterChip(
                selected = selectedDocType == "MEETING_NOTICE",
                onClick = { selectedDocType = "MEETING_NOTICE" },
                label = { Text(if (isBn) "সাধারণ সভা নোটিশ" else "Meeting Notice", fontSize = 11.sp) }
            )
            FilterChip(
                selected = selectedDocType == "CIRCULAR",
                onClick = { selectedDocType = "CIRCULAR" },
                label = { Text(if (isBn) "জরুরি বিজ্ঞপ্তি" else "Circular Notice", fontSize = 11.sp) }
            )
        }

        // Live Document Preview
        Card(
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            border = CardDefaults.outlinedCardBorder(),
            elevation = CardDefaults.cardElevation(2.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                LetterheadHeader(samity = samity)

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    when (selectedDocType) {
                        "CERTIFICATE" -> {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(SamityNavyPrimary.copy(alpha = 0.08f))
                                    .padding(8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "সদস্যতা প্রশংসাপত্র ও প্রত্যয়ন / MEMBERSHIP CERTIFICATE",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp,
                                    color = SamityNavyPrimary
                                )
                            }

                            Text(
                                text = "স্মারক নং: ${samity.regNo}/CERT/2026/01\nতারিখ: $currentDateStr",
                                fontSize = 11.sp,
                                color = Color(0xFF64748B)
                            )

                            Text(
                                text = "এই মর্মে প্রত্যয়ন করা যাচ্ছে যে, জনাব ${selectedMember?.nameBn ?: "রফিক আহমেদ"}, পিতা: ${selectedMember?.fatherName ?: "মরহুম আব্দুল গফুর"}, মাতা: মরহুমা রাবেয়া খাতুন, ঠিকানা: ${selectedMember?.address ?: "উত্তরা, ঢাকা"}— অত্র '${samity.nameBn}'-এর একজন বৈধ ও সম্মানিত ${selectedMember?.membershipType ?: "সাধারণ সদস্য"} (সদস্য আইডি: ${selectedMember?.memberCode ?: "SM-0001"})।\n\nতিনি প্রতিষ্ঠালগ্ন থেকে অত্র সমিতির সকল নিয়মাবলি ও মাসিক চাঁদা নিয়মিত প্রদান করে আসছেন। তাঁর চারিত্রিক ও সামাজিক সুনাম প্রশংসনীয়। আমরা তাঁর সার্বিক সাফল্য ও উজ্জ্বল ভবিষ্যৎ কামনা করি।",
                                fontSize = 12.sp,
                                lineHeight = 19.sp,
                                textAlign = TextAlign.Justify,
                                color = Color(0xFF1E293B)
                            )
                        }
                        "MEETING_NOTICE" -> {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(SamityGold.copy(alpha = 0.15f))
                                    .padding(8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "মাসিক কার্যনির্বাহী সভা সংক্রান্ত জরুরি নোটিশ",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp,
                                    color = SamityNavyDark
                                )
                            }

                            Text(
                                text = "স্মারক নং: ${samity.regNo}/MEET/2026/10\nতারিখ: $currentDateStr",
                                fontSize = 11.sp,
                                color = Color(0xFF64748B)
                            )

                            Text(
                                text = "সম্মানিত সদস্যবৃন্দ,\nআসসালামু আলাইকুম। অত্র সমিতির সকল সম্মানিত কার্যনির্বাহী ও সাধারণ সদস্যদের অবগতির জন্য জানানো যাচ্ছে যে, আগামী $meetingDate তারিখ সমিতির প্রধান কার্যালয়ে এক বিশেষ সভা অনুষ্ঠিত হবে।\n\nসভার আলোচ্য সূচি:\n১. বিগত সভার কার্যবিবরণী পাঠ ও অনুমোদন।\n২. $meetingAgenda\n৩. বকেয়া চাঁদা আদায় ও ব্যাংক হিসাব অগ্রগতি পর্যালোচনা।\n৪. বিবিধ।\n\nউক্ত সভায় যথাসময়ে উপস্থিত থাকার জন্য বিনীত অনুরোধ জানানো যাচ্ছে।",
                                fontSize = 12.sp,
                                lineHeight = 19.sp,
                                color = Color(0xFF1E293B)
                            )
                        }
                        else -> {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(StatusPaidBg)
                                    .padding(8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "জরুরি আর্থিক বিজ্ঞপ্তি ও নির্দেশনা",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp,
                                    color = StatusPaidGreen
                                )
                            }

                            Text(
                                text = "এতদ্বারা সমিতির সকল সদস্যদের জানানো যাচ্ছে যে, সমিতির সকল লেনদেন শতভাগ ডিজিটাল ও স্বচ্ছ রাখার লক্ষ্যে এখন থেকে ব্যাংক স্লিপ ও বিকাশ পেমেন্টের ডিজিটাল রিসিট অ্যাপস মারফত সরাসরি সংগ্রহ করার অনুরোধ করা হলো।",
                                fontSize = 12.sp,
                                lineHeight = 19.sp,
                                color = Color(0xFF1E293B)
                            )
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "ডিজিটাল ভেরিফিকেশন কোড: HKB-DOC-2026",
                            fontSize = 9.sp,
                            color = Color.Gray
                        )
                        QrCodeCanvas(content = "DOC:SAMITY:${samity.id}:$selectedDocType", size = 56.dp)
                    }
                }

                LetterheadSignatureBar(signatories = repository.signatories)
            }
        }
    }
}

private fun printDocHtml(
    context: Context,
    samity: com.example.data.model.Samity,
    docType: String,
    memberName: String,
    agenda: String,
    meetingDate: String
) {
    val title = if (docType == "CERTIFICATE") "সদস্যতা সনদপত্র" else "সাধারণ সভা নোটিশ"
    val html = """
        <!DOCTYPE html>
        <html>
        <head>
            <meta charset="utf-8">
            <title>${title}</title>
            <style>
                body { font-family: 'SolaimanLipi', Arial, sans-serif; margin: 30px; }
                .header { text-align: center; border-bottom: 2px solid #0f3b66; padding-bottom: 10px; }
                .title { font-size: 22px; font-weight: bold; color: #0f3b66; }
                .body-content { margin-top: 25px; line-height: 1.8; font-size: 14px; text-align: justify; }
                .signatures { display: flex; justify-content: space-between; margin-top: 50px; text-align: center; font-size: 13px; }
                .sig-box { width: 30%; border-top: 1px dashed #64748b; padding-top: 6px; }
            </style>
        </head>
        <body>
            <div class="header">
                <div class="title">${samity.nameBn}</div>
                <div>স্লোগান: ${samity.sloganBn} | রেজি: ${samity.regNo}</div>
                <div style="font-size: 11px; color: #64748b;">${samity.addressBn} | ফোন: ${samity.phone}</div>
            </div>
            
            <div style="text-align: center; font-size: 16px; font-weight: bold; margin-top: 20px; text-decoration: underline;">
                ${title}
            </div>
            
            <div class="body-content">
                ${
        if (docType == "CERTIFICATE")
            "এই মর্মে প্রত্যয়ন করা যাচ্ছে যে, জনাব ${memberName} অত্র সমিতির একজন সম্মানিত বৈধ সদস্য। তিনি নিয়মিত চাঁদা প্রদান করে সমিতির কার্যক্রমে সহায়তা প্রদান করছেন।"
        else
            "সম্মানিত সদস্যবৃন্দ, আগামী ${meetingDate} তারিখ সমিতির এক বিশেষ সাধারণ সভা অনুষ্ঠিত হবে। আলোচ্য বিষয়: ${agenda}। যথাসময়ে উপস্থিত থাকার জন্য অনুরোধ করা হলো।"
    }
            </div>
            
            <div class="signatures">
                <div class="sig-box"><b>সভাপতি</b><br>${samity.presidentName}</div>
                <div class="sig-box"><b>সাধারণ সম্পাদক</b><br>${samity.secretaryName}</div>
                <div class="sig-box"><b>কোষাধ্যক্ষ</b><br>${samity.treasurerName}</div>
            </div>
        </body>
        </html>
    """.trimIndent()

    val webView = WebView(context)
    webView.webViewClient = object : WebViewClient() {
        override fun onPageFinished(view: WebView?, url: String?) {
            val printManager = context.getSystemService(Context.PRINT_SERVICE) as? PrintManager
            val printAdapter = webView.createPrintDocumentAdapter("Official_Doc")
            printManager?.print("Official_Doc", printAdapter, PrintAttributes.Builder().build())
        }
    }
    webView.loadDataWithBaseURL(null, html, "text/html", "UTF-8", null)
}
