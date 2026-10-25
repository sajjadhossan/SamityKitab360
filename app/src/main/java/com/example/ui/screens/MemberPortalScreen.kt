package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Deposit
import com.example.data.repository.SamityRepository
import com.example.ui.components.QrCodeCanvas
import com.example.ui.language.LanguageManager
import com.example.ui.theme.*
import com.example.util.NumberToWords

@Composable
fun MemberPortalScreen(
    repository: SamityRepository,
    onViewReceipt: (Deposit) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val isBn = LanguageManager.isBangla
    val samity = repository.activeSamity
    val members = repository.activeMembers

    // For member role, view Kamal Hossain (SM-0002) or Rafiq Ahmed (SM-0001)
    val member = members.firstOrNull { it.memberCode == "SM-0002" } ?: members.first()
    val memberDeposits = repository.activeDeposits.filter { it.memberId == member.id }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(SlateBackground)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Welcome Header
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = if (isBn) "সদস্য পোর্টাল ও আত্মপরিচয়" else "Member Portal & Profile",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = SamityNavyPrimary
                    )
                    Text(
                        text = "${if (isBn) "সমিতি: " else "Samity: "}${samity.nameBn}",
                        fontSize = 11.5.sp,
                        color = Color(0xFF64748B)
                    )
                }

                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = SamityGold
                ) {
                    Text(
                        text = if (isBn) "সাধারণ সদস্য" else "General Member",
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp,
                        color = SamityNavyDark,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }
            }
        }

        // Digital Membership Card
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = SamityNavyPrimary),
                elevation = CardDefaults.cardElevation(3.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = samity.nameBn,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = SamityGold
                        ) {
                            Text(
                                text = member.memberCode,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = SamityNavyDark,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(50.dp)
                                .clip(CircleShape)
                                .background(Color.White.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = member.nameBn.take(1),
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = member.nameBn,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Text(
                                text = member.nameEn,
                                fontSize = 11.sp,
                                color = Color(0xFFD0E4FF)
                            )
                            Text(
                                text = "মোবাইল: ${member.phone} • রক্তের গ্রুপ: ${member.bloodGroup}",
                                fontSize = 10.5.sp,
                                color = Color.White.copy(alpha = 0.8f)
                            )
                        }

                        QrCodeCanvas(content = "MEMBER:${member.memberCode}:${member.phone}", size = 64.dp)
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                    Divider(color = Color.White.copy(alpha = 0.2f))
                    Spacer(modifier = Modifier.height(6.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "যোগদান: ${member.joinDate}",
                            fontSize = 10.sp,
                            color = Color(0xFFD0E4FF)
                        )
                        Text(
                            text = "পদবি: ${member.membershipType}",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = SamityGold
                        )
                    }
                }
            }
        }

        // My Subscription & Due Status
        item {
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = CardDefaults.outlinedCardBorder()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = if (isBn) "আমার চাঁদা ও আর্থিক স্থিতি (My Financial Status)" else "My Financial Status",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.5.sp,
                        color = SamityNavyPrimary
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(if (isBn) "মাসিক ধার্য চাঁদা" else "Monthly Fee", fontSize = 11.sp, color = Color(0xFF64748B))
                            Text(NumberToWords.formatCurrency(member.monthlyFee, isBn), fontSize = 14.sp, fontWeight = FontWeight.Bold, color = SamityNavyPrimary)
                        }
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(if (isBn) "অক্টোবর মাসে জমা" else "Paid Oct 2026", fontSize = 11.sp, color = Color(0xFF64748B))
                            Text(NumberToWords.formatCurrency(member.currentMonthPaid, isBn), fontSize = 14.sp, fontWeight = FontWeight.Bold, color = StatusPaidGreen)
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            Text(if (isBn) "মোট বকেয়া" else "Total Due", fontSize = 11.sp, color = Color(0xFF64748B))
                            Text(NumberToWords.formatCurrency(member.totalDue, isBn), fontSize = 14.sp, fontWeight = FontWeight.Bold, color = if (member.totalDue > 0) StatusUnpaidRed else Color.Gray)
                        }
                    }

                    if (member.totalDue > 0) {
                        Spacer(modifier = Modifier.height(10.dp))
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = StatusUnpaidBg,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.HourglassTop, contentDescription = null, tint = StatusUnpaidRed, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = if (isBn) "আপনার ৳ ${member.totalDue.toInt()} টাকা বকেয়া রয়েছে। অনুগ্রহপূর্বক ১০ অক্টোবরের মধ্যে জমা দিন।" else "You have ৳ ${member.totalDue.toInt()} due. Please deposit before 10th October.",
                                    fontSize = 11.sp,
                                    color = StatusUnpaidRed,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }
        }

        // How to Pay Instructions Card
        item {
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = CardDefaults.outlinedCardBorder()
            ) {
                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = if (isBn) "চাঁদা পরিশোধের তথ্য (Payment Details)" else "Payment Details",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = SamityNavyPrimary
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("ব্যাংক একাউন্ট নম্বর:", fontSize = 11.sp, color = Color.Gray)
                            Text("${samity.bankName} - ${samity.bankAccountNo}", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }
                        IconButton(
                            onClick = {
                                val cb = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                cb.setPrimaryClip(ClipData.newPlainText("Bank Account", samity.bankAccountNo))
                                Toast.makeText(context, "Account copied", Toast.LENGTH_SHORT).show()
                            }
                        ) {
                            Icon(Icons.Default.ContentCopy, contentDescription = "Copy", tint = SamityNavyPrimary)
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("বিকাশ মার্চেন্ট নম্বর:", fontSize = 11.sp, color = Color.Gray)
                            Text(samity.bkashNumber, fontWeight = FontWeight.Bold, fontSize = 12.sp, color = Color(0xFFD12053))
                        }
                        IconButton(
                            onClick = {
                                val cb = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                cb.setPrimaryClip(ClipData.newPlainText("bKash Number", samity.bkashNumber))
                                Toast.makeText(context, "bKash copied", Toast.LENGTH_SHORT).show()
                            }
                        ) {
                            Icon(Icons.Default.ContentCopy, contentDescription = "Copy", tint = SamityNavyPrimary)
                        }
                    }
                }
            }
        }

        // My Deposit Receipts
        item {
            Text(
                text = if (isBn) "আমার জমা ও রিসিট হিস্টোরি" else "My Deposit & Receipt History",
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                color = SamityNavyPrimary
            )
        }

        if (memberDeposits.isEmpty()) {
            item {
                Text(
                    text = if (isBn) "চলতি মাসে আপনার কোন জমা এখনও পাওয়া যায়নি।" else "No deposits recorded yet for this member.",
                    fontSize = 12.sp,
                    color = Color.Gray
                )
            }
        } else {
            items(memberDeposits) { dep ->
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
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(dep.voucherNo, fontWeight = FontWeight.Bold, fontSize = 12.5.sp, color = SamityNavyPrimary)
                            Text("${dep.depositDate} • ${dep.depositMethod}", fontSize = 10.5.sp, color = Color.Gray)
                        }

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(NumberToWords.formatCurrency(dep.amount, isBn), fontWeight = FontWeight.Bold, fontSize = 13.sp, color = StatusPaidGreen)
                            Spacer(modifier = Modifier.width(8.dp))
                            OutlinedButton(
                                onClick = { onViewReceipt(dep) },
                                shape = RoundedCornerShape(6.dp),
                                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                                modifier = Modifier.height(28.dp)
                            ) {
                                Text(if (isBn) "রিসিট" else "Receipt", fontSize = 10.sp)
                            }
                        }
                    }
                }
            }
        }
    }
}
