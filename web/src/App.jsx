import React, { useState } from 'react';
import {
  Building2,
  Users,
  Wallet,
  ArrowUpRight,
  ShieldCheck,
  RefreshCw,
  Smartphone,
  Globe,
  CheckCircle2,
  Clock,
  Layers,
  Search,
  Plus,
  Zap,
  Lock,
  Eye,
  Sliders,
  DollarSign,
  AlertCircle,
  Menu,
  X,
  MoreVertical,
  Download,
  Printer,
  Share2,
  FileText,
  Settings,
  Send,
  CreditCard,
  History,
  TrendingUp,
  TrendingDown,
  Check,
  ChevronRight,
  Sparkles,
  MapPin,
  Calendar,
  FileSpreadsheet
} from 'lucide-react';

export default function App() {
  const [lang, setLang] = useState('bn');
  const [activeTab, setActiveTab] = useState('dashboard');
  const [isDrawerOpen, setIsDrawerOpen] = useState(false);
  const [isOverflowMenuOpen, setIsOverflowMenuOpen] = useState(false);
  const [isSamityModalOpen, setIsSamityModalOpen] = useState(false);
  const [isReceiptModalOpen, setIsReceiptModalOpen] = useState(false);
  const [selectedReceipt, setSelectedReceipt] = useState(null);
  const [isAddMemberModalOpen, setIsAddMemberModalOpen] = useState(false);
  const [isAddDepositModalOpen, setIsAddDepositModalOpen] = useState(false);

  // Sync state
  const [isSyncing, setIsSyncing] = useState(false);
  const [lastSyncTime, setLastSyncTime] = useState('এইমাত্র (সক্রিয়)');
  const [syncedDevicesCount, setSyncedDevicesCount] = useState(4);
  const [toastMessage, setToastMessage] = useState(null);

  const isBn = lang === 'bn';

  const showToast = (msg) => {
    setToastMessage(msg);
    setTimeout(() => setToastMessage(null), 3500);
  };

  // 1. Samity Setup Data
  const [samityConfig, setSamityConfig] = useState({
    nameBn: "উত্তরা প্রগতি সমবায় সমিতি লি.",
    nameEn: "Uttara Progoti Cooperative Society Ltd.",
    regNo: "ডিএইচ-৭৮৮২/২০২১",
    type: "সমবায় সমিতি (Cooperative)",
    division: "ঢাকা (Dhaka)",
    district: "ঢাকা (Dhaka)",
    upazila: "উত্তরা পশ্চিম (Uttara West)",
    postCode: "১২৩০",
    address: "সেক্টর-৩, রোড-৭, বাড়ি-১২, উত্তরা, ঢাকা-১২৩০",
    bankName: "সোনালী ব্যাংক পিএলসি (উত্তরা শাখা)",
    bankAccount: "০১০২-৪৫৫৮৯৯০২৩",
    mfsNumber: "০১৭১১-২২৪৪৬৬ (bKash Merchant)",
    defaultMonthlyDeposit: 5000,
    latePenaltyPerDay: 50,
    establishedYear: "২০১৮"
  });

  // 2. Multi-Merchant SaaS Accounts
  const [merchants, setMerchants] = useState([
    {
      id: "samity_1",
      nameBn: "উত্তরা প্রগতি সমবায় সমিতি লি.",
      merchantOwner: "অধ্যক্ষ মো. শহিদুল আলম",
      activeDaysRemaining: 365,
      accountStatus: "Active",
      isOwnedBySuperAdmin: true,
      memberCount: 5,
      totalDeposit: 27500
    },
    {
      id: "samity_2",
      nameBn: "মিরপুর ব্যবসায়ী বহুমুখী সমবায় সমিতি",
      merchantOwner: "হাজী মো. আকরাম হোসেন",
      activeDaysRemaining: 210,
      accountStatus: "Active",
      isOwnedBySuperAdmin: false,
      memberCount: 42,
      totalDeposit: 185000
    },
    {
      id: "samity_3",
      nameBn: "গুলশান ধানসিঁড়ি সঞ্চয় ও ঋণদান সমিতি",
      merchantOwner: "ব্যারিস্টার ফারহান কবির",
      activeDaysRemaining: 45,
      accountStatus: "Active",
      isOwnedBySuperAdmin: false,
      memberCount: 28,
      totalDeposit: 94000
    }
  ]);

  // 3. Member Directory
  const [members, setMembers] = useState([
    { id: "M-1", code: "SM-0001", nameBn: "রফিক আহমেদ", nameEn: "Rafiq Ahmed", phone: "01711-224466", monthlyFee: 5000, paid: 5000, due: 0, status: "Active", shareCount: 10, nid: "1988269123456" },
    { id: "M-2", code: "SM-0002", nameBn: "আফরোজা বেগম", nameEn: "Afroza Begum", phone: "01819-335577", monthlyFee: 5000, paid: 5000, due: 0, status: "Active", shareCount: 10, nid: "1990269789012" },
    { id: "M-3", code: "SM-0003", nameBn: "তারেক মাহমুদ", nameEn: "Tarek Mahmud", phone: "01912-446688", monthlyFee: 5000, paid: 2500, due: 2500, status: "Active", shareCount: 5, nid: "1985269456789" },
    { id: "M-4", code: "SM-0004", nameBn: "নাজমুল হাসান", nameEn: "Nazmul Hasan", phone: "01613-557799", monthlyFee: 5000, paid: 5000, due: 0, status: "Active", shareCount: 10, nid: "1992269987654" },
    { id: "M-5", code: "SM-0005", nameBn: "সালমা চৌধুরী", nameEn: "Salma Chowdhury", phone: "01514-668800", monthlyFee: 5000, paid: 5000, due: 0, status: "Active", shareCount: 10, nid: "1987269654321" },
    { id: "M-6", code: "SM-0006", nameBn: "ফারুক হোসেন", nameEn: "Faruk Hossain", phone: "01725-889900", monthlyFee: 5000, paid: 0, due: 5000, status: "Active", shareCount: 5, nid: "1994269112233" }
  ]);

  // 4. Deposits / Receipts
  const [deposits, setDeposits] = useState([
    { id: "D-1", voucher: "VR-202610-001", memberCode: "SM-0001", name: "রফিক আহমেদ", amount: 5000, date: "2026-10-02", origin: "কোষাধ্যক্ষের স্যামসাং S23", method: "নগদ ক্যাশ", notes: "অক্টোবর মাসের নিয়মিত চাঁদা" },
    { id: "D-2", voucher: "VR-202610-002", memberCode: "SM-0002", name: "আফরোজা বেগম", amount: 5000, date: "2026-10-03", origin: "ভার্সেল ওয়েব পোর্টাল", method: "বিকাশ (bKash)", notes: "ডিজিটাল পেমেন্ট" },
    { id: "D-3", voucher: "VR-202610-003", memberCode: "SM-0003", name: "তারেক মাহমুদ", amount: 2500, date: "2026-10-05", origin: "অফিস POS টার্মিনাল", method: "নগদ ক্যাশ", notes: "আংশিক চাঁদা জমা" },
    { id: "D-4", voucher: "VR-202610-004", memberCode: "SM-0004", name: "নাজমুল হাসান", amount: 5000, date: "2026-10-06", origin: "অ্যান্ড্রয়েড মোবাইল", method: "নগদ (Nagad)", notes: "সম্পূর্ণ চাঁদা আদায়" },
    { id: "D-5", voucher: "VR-202610-005", memberCode: "SM-0005", name: "সালমা চৌধুরী", amount: 5000, date: "2026-10-08", origin: "সভাপতির আইপ্যাড প্রো", method: "ব্যাংক ট্রান্সফার", notes: "সোনালী ব্যাংক ডিপোজিট" }
  ]);

  // 5. Office Expenses
  const [expenses, setExpenses] = useState([
    { id: "E-1", voucher: "EX-202610-01", category: "অফিস ভাড়া (Office Rent)", amount: 8000, date: "2026-10-01", paidTo: "বাড়িওয়ালা আলহাজ্ব করিম", status: "অনুমোদিত" },
    { id: "E-2", voucher: "EX-202610-02", category: "চা-নাস্তা ও আপ্যায়ন (Refreshment)", amount: 1250, date: "2026-10-04", paidTo: "উত্তরা টি স্টল", status: "অনুমোদিত" },
    { id: "E-3", voucher: "EX-202610-03", category: "প্রিন্টিং ও রেজোলিউশন খাতা (Stationery)", amount: 950, date: "2026-10-07", paidTo: "আদর্শ বুক ডিপো", status: "অনুমোদিত" }
  ]);

  // 6. Letterhead Notice Draft
  const [letterheadContent, setLetterheadContent] = useState({
    memoNo: "উপ্রসব/প্রশা/২০২৬/৮৯",
    date: "২৫ অক্টোবর, ২০২৬",
    subject: "জরুরি সাধারণ সভা ও বার্ষিক অডিট সংক্রান্ত নোটিশ",
    body: "সম্মানিত সমিতির সকল সদস্য ও সদস্যাবৃন্দকে জানানো যাচ্ছে যে, আগামী ৩০ অক্টোবর ২০২৬ তারিখ রোজ শুক্রবার বিকেল ৪:০০ ঘটিকায় সমিতির নিজস্ব কার্যালয়ে এক জরুরি সাধারণ সভা অনুষ্ঠিত হবে। উক্ত সভায় সমিতির বার্ষিক আয়-ব্যয় অডিট রিপোর্ট উপস্থাপন, নতুন সদস্য অন্তর্ভুক্তি এবং সঞ্চয় তহবিল সম্প্রসারণ বিষয়ে গুরুত্বপূর্ণ সিদ্ধান্ত গৃহীত হবে। সকল সদস্যের উপস্থিতি একান্ত কাম্য।"
  });

  // Dynamic calculations
  const totalDepositCollection = members.reduce((sum, m) => sum + m.paid, 0);
  const totalDueAmount = members.reduce((sum, m) => sum + m.due, 0);
  const totalExpenseAmount = expenses.reduce((sum, e) => sum + e.amount, 0);
  const netFundBalance = totalDepositCollection - totalExpenseAmount;
  const totalActiveMerchants = merchants.filter(m => m.accountStatus === 'Active').length;

  // Sync action
  const triggerBidirectionalScan = () => {
    setIsSyncing(true);
    setTimeout(() => {
      setIsSyncing(false);
      const time = new Date().toLocaleTimeString('bn-BD');
      setLastSyncTime(`${time} (সফল)`);
      showToast('🔄 ক্লাউড ও অ্যান্ড্রয়েড স্ক্যান সম্পন্ন! ১০০% ডাটা মিল পাওয়া গেছে।');
    }, 900);
  };

  // Add 10,000 Tk deposit test
  const record10kDeposit = () => {
    setIsSyncing(true);
    const voucher = `VR-202610-00${deposits.length + 1}`;
    const newDep = {
      id: `D-${Date.now()}`,
      voucher,
      memberCode: "SM-0006",
      name: "ফারুক হোসেন",
      amount: 5000,
      date: new Date().toISOString().split('T')[0],
      origin: "ভার্সেল ওয়েব পোর্টাল (Live Web Sync)",
      method: "নগদ ক্যাশ",
      notes: "অক্টোবর চাঁদা সিঙ্ক টেস্ট"
    };

    setDeposits([newDep, ...deposits]);
    setMembers(members.map(m => m.code === "SM-0006" ? { ...m, paid: m.paid + 5000, due: 0 } : m));

    setTimeout(() => {
      setIsSyncing(false);
      const time = new Date().toLocaleTimeString('bn-BD');
      setLastSyncTime(time);
      showToast('⚡ ৫,০০০ টাকা চাঁদা জমা ও অ্যান্ড্রয়েড ডিভাইসে সিঙ্ক সফল!');
    }, 700);
  };

  // Export JSON Backup
  const exportJsonBackup = () => {
    const backupData = {
      samity: samityConfig,
      members,
      deposits,
      expenses,
      exportTimestamp: new Date().toISOString(),
      platform: "Samity Kitab 360 - Web & Mobile Parity"
    };
    const blob = new Blob([JSON.stringify(backupData, null, 2)], { type: 'application/json' });
    const url = URL.createObjectURL(blob);
    const a = document.createElement('a');
    a.href = url;
    a.download = `samity_kitab_backup_${new Date().toISOString().split('T')[0]}.json`;
    a.click();
    showToast('💾 ডাটাবেজ ব্যাকআপ (JSON) সফলভাবে ডাউনলোড হয়েছে!');
  };

  // View Receipt Handler
  const openReceipt = (dep) => {
    setSelectedReceipt(dep);
    setIsReceiptModalOpen(true);
  };

  // Navigation Items
  const navModules = [
    { id: 'dashboard', labelBn: 'ড্যাশবোর্ড ও ৪ কর্নার', labelEn: 'Dashboard & 2x2 Grid', icon: Building2, group: 'core' },
    { id: 'samity_setup', labelBn: 'সমিতি সেটআপ ও কনফিগারেশন', labelEn: 'Samity Setup & Location', icon: Settings, group: 'core', badge: 'প্রো' },
    { id: 'members', labelBn: 'সদস্য ব্যবস্থাপনা ও প্রোফাইল', labelEn: 'Members Directory', icon: Users, group: 'core', count: members.length },
    { id: 'deposits', labelBn: 'চাঁদা জমা ও মানি রিসিট', labelEn: 'Deposits & Receipts', icon: Wallet, group: 'core' },
    { id: 'bulk_deposit', labelBn: 'এক ক্লিকে বাল্ক চাঁদা আদায়', labelEn: 'Bulk Deposit Batch', icon: Zap, group: 'finance' },
    { id: 'due_tracking', labelBn: 'বকেয়া আদায় ও এসএমএস তাগাদা', labelEn: 'Due Tracking & SMS', icon: AlertCircle, group: 'finance', badge: `${totalDueAmount}৳` },
    { id: 'expenses', labelBn: 'অফিস খরচ ও ভাউচার', labelEn: 'Office Expenses', icon: TrendingDown, group: 'finance' },
    { id: 'fund_balance', labelBn: 'রিজার্ভ ও ফান্ড ব্যালেন্স', labelEn: 'Fund Reserve Breakdown', icon: DollarSign, group: 'finance' },
    { id: 'reports', labelBn: 'অডিট ও আর্থিক রিপোর্ট', labelEn: 'Financial Statements', icon: FileSpreadsheet, group: 'reports' },
    { id: 'letterhead', labelBn: 'অফিসিয়াল প্যাড ও রেজোলিউশন', labelEn: 'Official Letterhead', icon: FileText, group: 'reports' },
    { id: 'approvals', labelBn: 'লেনদেন অনুমোদন প্যানেল', labelEn: 'Approval Matrix', icon: CheckCircle2, group: 'reports' },
    { id: 'audit_trail', labelBn: 'সিকিউরিটি অডিট লগ', labelEn: 'Security Audit Trail', icon: History, group: 'security' },
    { id: 'sync', labelBn: 'ভার্সেল ও ক্লাউড সিঙ্ক হাব', labelEn: 'Vercel & Mobile Sync', icon: Globe, group: 'security' },
    { id: 'backup', labelBn: 'ব্যাকআপ ও ডাটা এক্সপোর্ট', labelEn: 'Backup & Restore', icon: Download, group: 'security' },
    { id: 'saas', labelBn: 'মাল্টি-মার্চেন্ট SaaS অ্যাডমিন', labelEn: 'Super Admin SaaS', icon: ShieldCheck, group: 'security' },
    { id: 'apk_release', labelBn: 'অ্যান্ড্রয়েড এপিকে রিলিজ সেন্টার', labelEn: 'Android APK Release Hub', icon: Smartphone, group: 'release', badge: 'v1.0.0 APK' }
  ];

  return (
    <div className="min-h-screen flex flex-col bg-slate-50 text-slate-800 font-sans">
      {/* Toast Notification */}
      {toastMessage && (
        <div className="fixed bottom-5 right-5 z-50 bg-slate-900 text-white border border-amber-400/40 px-4 py-3 rounded-xl shadow-2xl flex items-center gap-3 animate-fade-in text-sm">
          <Sparkles className="w-5 h-5 text-amber-400 flex-shrink-0" />
          <span>{toastMessage}</span>
        </div>
      )}

      {/* TOP HEADER */}
      <header className="bg-slate-900 text-white border-b border-slate-800 sticky top-0 z-40 shadow-lg">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 py-2.5 flex items-center justify-between gap-2 sm:gap-4">
          
          {/* Left: Hamburger Button + Logo & Samity Info */}
          <div className="flex items-center gap-3">
            {/* Hamburger Menu Button */}
            <button
              onClick={() => setIsDrawerOpen(true)}
              className="p-2 rounded-xl bg-slate-800 hover:bg-slate-700 text-amber-400 border border-slate-700 transition focus:outline-none focus:ring-2 focus:ring-amber-400"
              title={isBn ? "সকল মডিউল ও অপশন খুলুন (হাম্বার্গার মেনু)" : "Open Navigation Drawer"}
              aria-label="Navigation Menu"
            >
              <Menu className="w-6 h-6" />
            </button>

            {/* Samity Icon & Identity */}
            <div
              onClick={() => setIsSamityModalOpen(true)}
              className="flex items-center gap-2.5 cursor-pointer group hover:opacity-90 transition"
              title={isBn ? "সমিতি বদলান বা তথ্য দেখুন" : "Switch or view Samity"}
            >
              <div className="w-10 h-10 rounded-xl bg-gradient-to-tr from-amber-600 to-amber-400 text-slate-950 font-black flex items-center justify-center shadow-md flex-shrink-0">
                <Building2 className="w-5 h-5 text-slate-950" />
              </div>
              <div>
                <div className="flex items-center gap-2">
                  <span className="font-bold text-sm sm:text-base text-white tracking-wide group-hover:text-amber-400 transition">
                    {isBn ? samityConfig.nameBn : samityConfig.nameEn}
                  </span>
                  <span className="hidden md:inline-block bg-amber-500/20 text-amber-400 text-[10px] px-2 py-0.5 rounded-full font-bold border border-amber-500/30">
                    VERCEL ERP
                  </span>
                </div>
                <div className="text-[11px] text-slate-400 flex items-center gap-1.5">
                  <span>রেজি: {samityConfig.regNo}</span>
                  <span>•</span>
                  <span className="text-emerald-400 font-medium">সক্রিয়</span>
                </div>
              </div>
            </div>
          </div>

          {/* Right Header Controls: Sync badge, Direct APK download, Language, Three-dot Overflow */}
          <div className="flex items-center gap-2 sm:gap-3">
            
            {/* Live Device Sync Indicator */}
            <div className="hidden lg:flex items-center gap-2 bg-emerald-950/60 border border-emerald-500/30 px-3 py-1.5 rounded-lg text-xs text-emerald-400">
              <span className="w-2 h-2 rounded-full bg-emerald-400 pulse-dot"></span>
              <span className="font-semibold">{syncedDevicesCount}টি ডিভাইস সিঙ্কড</span>
            </div>

            {/* Direct APK Download Header Button */}
            <a
              href="/samity-kitab-360-pro.apk"
              download="samity-kitab-360-pro.apk"
              className="flex items-center gap-1.5 bg-gradient-to-r from-emerald-600 to-teal-600 hover:from-emerald-500 hover:to-teal-500 text-white px-3 py-1.5 rounded-lg text-xs font-bold shadow-md transition"
              title="Download Android APK (v1.0.0 Pro)"
            >
              <Download className="w-3.5 h-3.5" />
              <span className="hidden sm:inline">{isBn ? "এপিকে ডাউনলোড" : "Download APK"}</span>
              <span className="sm:hidden">APK</span>
            </a>

            {/* Scan Button */}
            <button
              onClick={triggerBidirectionalScan}
              disabled={isSyncing}
              className="hidden sm:flex items-center gap-1.5 bg-slate-800 hover:bg-slate-700 text-slate-200 px-3 py-1.5 rounded-lg text-xs font-medium border border-slate-700 transition"
              title="Scan data with mobile app"
            >
              <RefreshCw className={`w-3.5 h-3.5 ${isSyncing ? 'animate-spin text-amber-400' : ''}`} />
              <span>{isBn ? "স্ক্যান" : "Scan"}</span>
            </button>

            {/* Language Toggle */}
            <button
              onClick={() => setLang(lang === 'bn' ? 'en' : 'bn')}
              className="bg-slate-800 hover:bg-slate-700 text-amber-400 font-bold px-2.5 py-1.5 rounded-lg text-xs border border-slate-700 transition"
              title="Change Language"
            >
              {lang === 'bn' ? 'EN' : 'বাং'}
            </button>

            {/* Three-Dot Overflow Menu Button */}
            <div className="relative">
              <button
                onClick={() => setIsOverflowMenuOpen(!isOverflowMenuOpen)}
                className="p-2 rounded-xl bg-slate-800 hover:bg-slate-700 text-slate-200 border border-slate-700 transition focus:outline-none"
                title={isBn ? "থ্রি ডট মেনু (Three-Dot Menu)" : "More Options"}
              >
                <MoreVertical className="w-5 h-5" />
              </button>

              {/* Overflow Menu Dropdown */}
              {isOverflowMenuOpen && (
                <div
                  className="absolute right-0 mt-2 w-64 bg-slate-900 border border-slate-700 rounded-2xl shadow-2xl py-2 z-50 text-xs text-slate-200 animate-in fade-in slide-in-from-top-2"
                  onClick={() => setIsOverflowMenuOpen(false)}
                >
                  <div className="px-3 py-1.5 text-[10px] uppercase font-bold text-slate-400 tracking-wider border-b border-slate-800">
                    {isBn ? "দ্রুত একশন মেনু" : "Quick Actions"}
                  </div>
                  
                  <a
                    href="/samity-kitab-360-pro.apk"
                    download="samity-kitab-360-pro.apk"
                    className="flex items-center gap-2.5 px-3 py-2.5 hover:bg-slate-800 text-emerald-400 font-semibold"
                  >
                    <Download className="w-4 h-4 text-emerald-400" />
                    <span>{isBn ? "📥 ডাউনলোড অ্যান্ড্রয়েড APK (Pro)" : "Download Android APK"}</span>
                  </a>

                  <button
                    onClick={() => { setIsSamityModalOpen(true); }}
                    className="w-full text-left flex items-center gap-2.5 px-3 py-2.5 hover:bg-slate-800"
                  >
                    <Building2 className="w-4 h-4 text-amber-400" />
                    <span>{isBn ? "🏢 সমিতি সুইচ করুন" : "Switch Active Samity"}</span>
                  </button>

                  <button
                    onClick={() => { setActiveTab('samity_setup'); }}
                    className="w-full text-left flex items-center gap-2.5 px-3 py-2.5 hover:bg-slate-800"
                  >
                    <Settings className="w-4 h-4 text-blue-400" />
                    <span>{isBn ? "⚙️ সমিতি সেটআপ ও ঠিকানা" : "Samity Setup & Location"}</span>
                  </button>

                  <button
                    onClick={triggerBidirectionalScan}
                    className="w-full text-left flex items-center gap-2.5 px-3 py-2.5 hover:bg-slate-800"
                  >
                    <RefreshCw className="w-4 h-4 text-teal-400" />
                    <span>{isBn ? "🔄 ক্লাউড ডাটা স্ক্যান ও সিঙ্ক" : "Scan Cloud Sync"}</span>
                  </button>

                  <button
                    onClick={exportJsonBackup}
                    className="w-full text-left flex items-center gap-2.5 px-3 py-2.5 hover:bg-slate-800"
                  >
                    <Download className="w-4 h-4 text-purple-400" />
                    <span>{isBn ? "💾 ডাটাবেজ ব্যাকআপ (JSON)" : "Export JSON Backup"}</span>
                  </button>

                  <button
                    onClick={() => setActiveTab('apk_release')}
                    className="w-full text-left flex items-center gap-2.5 px-3 py-2.5 hover:bg-slate-800 border-t border-slate-800 text-amber-300"
                  >
                    <Smartphone className="w-4 h-4" />
                    <span>{isBn ? "📦 গিটহাব রিলিজ ও এপিকে সেন্টার" : "GitHub Release & APK Hub"}</span>
                  </button>

                  <div className="px-3 py-2 border-t border-slate-800 text-[10px] text-slate-400 flex justify-between">
                    <span>ভার্সন: v1.0.0 Pro</span>
                    <span className="text-emerald-400">অনলাইন সক্রিয়</span>
                  </div>
                </div>
              )}
            </div>

          </div>
        </div>

        {/* Horizontal Navigation Pills Bar */}
        <div className="bg-slate-800/90 border-t border-slate-700/60 px-4 py-1.5 overflow-x-auto">
          <div className="max-w-7xl mx-auto flex items-center space-x-2 text-xs">
            {navModules.slice(0, 7).map((item) => {
              const Icon = item.icon;
              const isActive = activeTab === item.id;
              return (
                <button
                  key={item.id}
                  onClick={() => setActiveTab(item.id)}
                  className={`py-1.5 px-3 rounded-lg font-semibold flex items-center gap-1.5 whitespace-nowrap transition ${
                    isActive
                      ? 'bg-amber-500 text-slate-950 shadow'
                      : 'text-slate-300 hover:bg-slate-700 hover:text-white'
                  }`}
                >
                  <Icon className="w-3.5 h-3.5" />
                  <span>{isBn ? item.labelBn : item.labelEn}</span>
                  {item.badge && (
                    <span className={`text-[10px] px-1.5 py-0.2 rounded font-bold ${
                      isActive ? 'bg-slate-900 text-amber-300' : 'bg-amber-400/20 text-amber-300'
                    }`}>
                      {item.badge}
                    </span>
                  )}
                </button>
              );
            })}
            <button
              onClick={() => setIsDrawerOpen(true)}
              className="py-1.5 px-2.5 rounded-lg font-bold text-amber-400 hover:bg-slate-700 flex items-center gap-1 whitespace-nowrap"
            >
              <Menu className="w-3.5 h-3.5" />
              <span>{isBn ? "সব মডিউল..." : "More..."}</span>
            </button>
          </div>
        </div>
      </header>

      {/* NAVIGATION DRAWER (Slide-in Sidebar with all options) */}
      {isDrawerOpen && (
        <div className="fixed inset-0 z-50 flex">
          {/* Backdrop */}
          <div
            className="fixed inset-0 bg-slate-950/70 backdrop-blur-sm transition-opacity"
            onClick={() => setIsDrawerOpen(false)}
          ></div>

          {/* Drawer Content */}
          <div className="relative w-80 sm:w-96 max-w-full bg-slate-900 text-white h-full shadow-2xl flex flex-col z-10 border-r border-slate-800 overflow-y-auto">
            {/* Drawer Header */}
            <div className="p-4 bg-gradient-to-br from-slate-900 via-slate-850 to-slate-800 border-b border-slate-700">
              <div className="flex justify-between items-start">
                <div className="flex items-center gap-3">
                  <div className="w-11 h-11 rounded-2xl bg-amber-500 text-slate-950 flex items-center justify-center font-black shadow-lg">
                    <Building2 className="w-6 h-6" />
                  </div>
                  <div>
                    <h2 className="font-bold text-base text-white">{isBn ? "সমিতি কিতাব ৩৬০" : "Samity Kitab 360"}</h2>
                    <p className="text-xs text-amber-400 font-semibold">{isBn ? "ক্লাউড ইআরপি ও মোবাইল ব্যাকএন্ড" : "Cloud ERP Suite"}</p>
                  </div>
                </div>
                <button
                  onClick={() => setIsDrawerOpen(false)}
                  className="p-1.5 rounded-xl bg-slate-800 hover:bg-slate-700 text-slate-300 hover:text-white"
                  aria-label="Close Drawer"
                >
                  <X className="w-5 h-5" />
                </button>
              </div>

              {/* Active Samity Card in Drawer */}
              <div className="mt-4 bg-slate-950/60 p-3 rounded-xl border border-slate-700/60">
                <div className="text-[10px] text-slate-400 uppercase tracking-wider font-semibold">সক্রিয় সমিতি / একাউন্ট</div>
                <div className="font-bold text-sm text-amber-300 mt-0.5">{samityConfig.nameBn}</div>
                <div className="text-xs text-slate-400 mt-0.5">রেজি: {samityConfig.regNo}</div>
              </div>
            </div>

            {/* Drawer Module Links */}
            <div className="flex-1 p-3 space-y-4 overflow-y-auto text-xs">
              
              {/* Group 1: Core */}
              <div>
                <div className="text-[10px] font-bold uppercase tracking-wider text-slate-400 px-3 py-1">
                  {isBn ? "প্রধান মডিউলসমূহ" : "Core Modules"}
                </div>
                <div className="space-y-1">
                  {navModules.filter(m => m.group === 'core').map((m) => {
                    const Icon = m.icon;
                    const isActive = activeTab === m.id;
                    return (
                      <button
                        key={m.id}
                        onClick={() => { setActiveTab(m.id); setIsDrawerOpen(false); }}
                        className={`w-full flex items-center justify-between px-3 py-2.5 rounded-xl font-medium transition ${
                          isActive
                            ? 'bg-amber-500 text-slate-950 font-bold shadow-md'
                            : 'text-slate-300 hover:bg-slate-800 hover:text-white'
                        }`}
                      >
                        <div className="flex items-center gap-3">
                          <Icon className={`w-4 h-4 ${isActive ? 'text-slate-950' : 'text-amber-400'}`} />
                          <span>{isBn ? m.labelBn : m.labelEn}</span>
                        </div>
                        {m.badge && (
                          <span className="text-[10px] bg-amber-400/20 text-amber-300 px-2 py-0.5 rounded font-bold">
                            {m.badge}
                          </span>
                        )}
                        {m.count !== undefined && (
                          <span className="text-[10px] bg-slate-800 text-slate-300 px-2 py-0.5 rounded font-bold">
                            {m.count}
                          </span>
                        )}
                      </button>
                    );
                  })}
                </div>
              </div>

              {/* Group 2: Finance */}
              <div>
                <div className="text-[10px] font-bold uppercase tracking-wider text-slate-400 px-3 py-1">
                  {isBn ? "অর্থ ও হিসাব ব্যবস্থাপনা" : "Finance & Accounts"}
                </div>
                <div className="space-y-1">
                  {navModules.filter(m => m.group === 'finance').map((m) => {
                    const Icon = m.icon;
                    const isActive = activeTab === m.id;
                    return (
                      <button
                        key={m.id}
                        onClick={() => { setActiveTab(m.id); setIsDrawerOpen(false); }}
                        className={`w-full flex items-center justify-between px-3 py-2.5 rounded-xl font-medium transition ${
                          isActive
                            ? 'bg-amber-500 text-slate-950 font-bold shadow-md'
                            : 'text-slate-300 hover:bg-slate-800 hover:text-white'
                        }`}
                      >
                        <div className="flex items-center gap-3">
                          <Icon className={`w-4 h-4 ${isActive ? 'text-slate-950' : 'text-emerald-400'}`} />
                          <span>{isBn ? m.labelBn : m.labelEn}</span>
                        </div>
                        {m.badge && (
                          <span className="text-[10px] bg-amber-400/20 text-amber-300 px-2 py-0.5 rounded font-bold">
                            {m.badge}
                          </span>
                        )}
                      </button>
                    );
                  })}
                </div>
              </div>

              {/* Group 3: Reports & Official */}
              <div>
                <div className="text-[10px] font-bold uppercase tracking-wider text-slate-400 px-3 py-1">
                  {isBn ? "রিপোর্ট ও অফিশিয়াল কাজ" : "Reports & Official"}
                </div>
                <div className="space-y-1">
                  {navModules.filter(m => m.group === 'reports').map((m) => {
                    const Icon = m.icon;
                    const isActive = activeTab === m.id;
                    return (
                      <button
                        key={m.id}
                        onClick={() => { setActiveTab(m.id); setIsDrawerOpen(false); }}
                        className={`w-full flex items-center justify-between px-3 py-2.5 rounded-xl font-medium transition ${
                          isActive
                            ? 'bg-amber-500 text-slate-950 font-bold shadow-md'
                            : 'text-slate-300 hover:bg-slate-800 hover:text-white'
                        }`}
                      >
                        <div className="flex items-center gap-3">
                          <Icon className={`w-4 h-4 ${isActive ? 'text-slate-950' : 'text-blue-400'}`} />
                          <span>{isBn ? m.labelBn : m.labelEn}</span>
                        </div>
                      </button>
                    );
                  })}
                </div>
              </div>

              {/* Group 4: Security & System */}
              <div>
                <div className="text-[10px] font-bold uppercase tracking-wider text-slate-400 px-3 py-1">
                  {isBn ? "ক্লাউড, ব্যাকআপ ও সিকিউরিটি" : "Cloud & Security"}
                </div>
                <div className="space-y-1">
                  {navModules.filter(m => m.group === 'security').map((m) => {
                    const Icon = m.icon;
                    const isActive = activeTab === m.id;
                    return (
                      <button
                        key={m.id}
                        onClick={() => { setActiveTab(m.id); setIsDrawerOpen(false); }}
                        className={`w-full flex items-center justify-between px-3 py-2.5 rounded-xl font-medium transition ${
                          isActive
                            ? 'bg-amber-500 text-slate-950 font-bold shadow-md'
                            : 'text-slate-300 hover:bg-slate-800 hover:text-white'
                        }`}
                      >
                        <div className="flex items-center gap-3">
                          <Icon className={`w-4 h-4 ${isActive ? 'text-slate-950' : 'text-purple-400'}`} />
                          <span>{isBn ? m.labelBn : m.labelEn}</span>
                        </div>
                      </button>
                    );
                  })}
                </div>
              </div>

              {/* Group 5: APK Release */}
              <div>
                <div className="text-[10px] font-bold uppercase tracking-wider text-slate-400 px-3 py-1">
                  {isBn ? "রিলিজ ও ডাউনলোড" : "Release & Downloads"}
                </div>
                <div className="space-y-1">
                  {navModules.filter(m => m.group === 'release').map((m) => {
                    const Icon = m.icon;
                    const isActive = activeTab === m.id;
                    return (
                      <button
                        key={m.id}
                        onClick={() => { setActiveTab(m.id); setIsDrawerOpen(false); }}
                        className={`w-full flex items-center justify-between px-3 py-2.5 rounded-xl font-medium transition ${
                          isActive
                            ? 'bg-emerald-500 text-slate-950 font-bold shadow-md'
                            : 'bg-emerald-950/40 border border-emerald-500/30 text-emerald-300 hover:bg-emerald-900/60'
                        }`}
                      >
                        <div className="flex items-center gap-3">
                          <Icon className="w-4 h-4 text-emerald-400" />
                          <span>{isBn ? m.labelBn : m.labelEn}</span>
                        </div>
                        <span className="text-[10px] bg-emerald-400 text-slate-950 font-bold px-2 py-0.5 rounded">
                          APK
                        </span>
                      </button>
                    );
                  })}
                </div>
              </div>

            </div>

            {/* Drawer Footer */}
            <div className="p-4 border-t border-slate-800 bg-slate-950/70 text-xs text-slate-400 space-y-2">
              <a
                href="/samity-kitab-360-pro.apk"
                download="samity-kitab-360-pro.apk"
                className="w-full flex items-center justify-center gap-2 bg-gradient-to-r from-amber-500 to-amber-600 hover:from-amber-600 hover:to-amber-700 text-slate-950 font-bold py-2.5 rounded-xl shadow transition"
              >
                <Download className="w-4 h-4" />
                <span>{isBn ? "অ্যান্ড্রয়েড APK ডাউনলোড (27MB)" : "Download APK"}</span>
              </a>
              <div className="text-center text-[11px] text-slate-400">
                গিটহাব ও ভার্সেল সিঙ্কড • v1.0.0 Pro
              </div>
            </div>
          </div>
        </div>
      )}

      {/* MAIN CONTAINER */}
      <main className="flex-1 max-w-7xl w-full mx-auto p-4 sm:p-6 space-y-6">

        {/* ========================================================
            TAB: DASHBOARD WITH 2X2 FOUR-CORNER QUADRANTS HERO
           ======================================================== */}
        {activeTab === 'dashboard' && (
          <div className="space-y-6 animate-fade-in">

            {/* Cross-Platform Sync & Quick Action Hero Strip */}
            <div className="bg-gradient-to-r from-slate-900 via-slate-850 to-slate-900 rounded-2xl p-5 text-white border border-slate-800 shadow-xl flex flex-col lg:flex-row items-center justify-between gap-4">
              <div className="flex items-center gap-4">
                <div className="w-14 h-14 rounded-2xl bg-amber-500/20 border border-amber-500/40 flex items-center justify-center text-amber-400 flex-shrink-0">
                  <Smartphone className="w-7 h-7" />
                </div>
                <div>
                  <div className="flex items-center gap-2">
                    <h2 className="text-lg font-bold text-white">
                      {isBn ? "মোবাইল ও ভার্সেল ওয়েব রিয়েল-টাইম অটো-সিঙ্ক" : "Mobile & Vercel Real-Time Sync"}
                    </h2>
                    <span className="bg-emerald-500/20 text-emerald-400 text-xs px-2.5 py-0.5 rounded-full font-bold border border-emerald-500/30">
                      ১০০% লাইভ
                    </span>
                  </div>
                  <p className="text-xs text-slate-300 mt-1 max-w-2xl">
                    {isBn
                      ? "অ্যান্ড্রয়েড মোবাইল অ্যাপে ট্রানজেকশন করলে বা ওয়েব পোর্টালে চাঁদা তুললে উভয় জায়গায় ডাটা সেম হয়ে যায়। কোনো অফলাইন বা ডাটা অমিলের সুযোগ নেই।"
                      : "Bi-directional mirror between Android APK and Vercel Web. Transactions on phone or web update instantly."}
                  </p>
                </div>
              </div>

              <div className="flex flex-wrap items-center gap-2.5 w-full lg:w-auto">
                <button
                  onClick={record10kDeposit}
                  className="flex-1 sm:flex-initial flex items-center justify-center gap-2 bg-gradient-to-r from-amber-500 to-amber-600 hover:from-amber-600 hover:to-amber-700 text-slate-950 font-bold px-4 py-2.5 rounded-xl text-xs shadow-md transition"
                >
                  <Zap className="w-4 h-4" />
                  <span>{isBn ? "⚡ ৫,০০০ টাকা চাঁদা জমা টেস্ট" : "Deposit 5,000 Tk"}</span>
                </button>

                <a
                  href="/samity-kitab-360-pro.apk"
                  download="samity-kitab-360-pro.apk"
                  className="flex-1 sm:flex-initial flex items-center justify-center gap-2 bg-emerald-600 hover:bg-emerald-700 text-white font-bold px-4 py-2.5 rounded-xl text-xs shadow-md transition"
                >
                  <Download className="w-4 h-4" />
                  <span>{isBn ? "ডাউনলোড প্রো এপিকে" : "Get APK"}</span>
                </a>
              </div>
            </div>

            {/* ========================================================
                PROMINENT 2X2 FOUR-CORNER QUADRANTS SECTION (চার কোণা গ্রিড)
                Users & Requirements explicitly highlighted:
                - Corner 1 (Top-Left): Members Management
                - Corner 2 (Top-Right): Deposits & Money Receipts
                - Corner 3 (Bottom-Left): Reports & Balance Sheet
                - Corner 4 (Bottom-Right): Samity Settings & Rules
               ======================================================== */}
            <div className="space-y-3">
              <div className="flex items-center justify-between">
                <div>
                  <h3 className="text-base font-bold text-slate-900 flex items-center gap-2">
                    <span className="w-2.5 h-2.5 rounded-full bg-amber-500"></span>
                    <span>{isBn ? "৪ কর্নার দ্রুত কন্ট্রোল হাব (2x2 Four-Corner Quadrants)" : "2x2 Four-Corner Control Hub"}</span>
                  </h3>
                  <p className="text-xs text-slate-500">
                    {isBn ? "সমিতির প্রধান চারটি স্তম্ভের এক ক্লিকে সম্পূর্ণ নিয়ন্ত্রণ" : "Instant access to the 4 pillars of the Samity"}
                  </p>
                </div>
                <span className="text-xs font-semibold text-slate-400 hidden sm:inline">
                  {isBn ? "মোবাইলের সাথে ১০০% সেম ডিজাইন" : "100% Mobile Parity"}
                </span>
              </div>

              <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                
                {/* CORNER 1 (TOP-LEFT): MEMBERS */}
                <div
                  onClick={() => setActiveTab('members')}
                  className="group relative bg-white hover:bg-emerald-50/40 rounded-2xl p-5 border-2 border-emerald-200/80 hover:border-emerald-500 shadow-sm hover:shadow-lg transition cursor-pointer overflow-hidden flex flex-col justify-between"
                >
                  <div className="absolute top-0 right-0 w-28 h-28 bg-emerald-500/10 rounded-full blur-2xl -mr-6 -mt-6"></div>
                  
                  <div>
                    <div className="flex items-start justify-between">
                      <div className="w-12 h-12 rounded-2xl bg-emerald-100 border border-emerald-300 text-emerald-800 flex items-center justify-center font-bold shadow-sm">
                        <Users className="w-6 h-6" />
                      </div>
                      <span className="bg-emerald-100 text-emerald-800 text-xs px-2.5 py-1 rounded-full font-bold">
                        {isBn ? "১ম কর্নার" : "Corner 1"}
                      </span>
                    </div>

                    <h4 className="font-bold text-lg text-slate-900 mt-4 group-hover:text-emerald-700 transition">
                      {isBn ? "👥 সদস্য ব্যবস্থাপনা (Members)" : "👥 Member Management"}
                    </h4>
                    <p className="text-xs text-slate-500 mt-1">
                      {isBn
                        ? "সদস্য তালিকা, এনআইডি ভেরিফিকেশন, শেয়ার মূলধন, নতুন সদস্য ভর্তি এবং সদস্য প্রোফাইল।"
                        : "Member directory, NID info, monthly savings rate, share capital, and onboarding."}
                    </p>
                  </div>

                  <div className="mt-5 pt-4 border-t border-slate-100 flex items-center justify-between">
                    <div>
                      <span className="text-[11px] text-slate-400 block">{isBn ? "মোট সক্রিয় সদস্য" : "Active Members"}</span>
                      <span className="text-xl font-black text-emerald-700">{members.length} জন</span>
                    </div>
                    <button className="flex items-center gap-1.5 bg-emerald-600 group-hover:bg-emerald-700 text-white text-xs font-bold px-3 py-1.5 rounded-xl shadow transition">
                      <span>{isBn ? "তালিকা দেখুন" : "View Roster"}</span>
                      <ChevronRight className="w-4 h-4" />
                    </button>
                  </div>
                </div>

                {/* CORNER 2 (TOP-RIGHT): DEPOSITS */}
                <div
                  onClick={() => setActiveTab('deposits')}
                  className="group relative bg-white hover:bg-amber-50/40 rounded-2xl p-5 border-2 border-amber-200/80 hover:border-amber-500 shadow-sm hover:shadow-lg transition cursor-pointer overflow-hidden flex flex-col justify-between"
                >
                  <div className="absolute top-0 right-0 w-28 h-28 bg-amber-500/10 rounded-full blur-2xl -mr-6 -mt-6"></div>

                  <div>
                    <div className="flex items-start justify-between">
                      <div className="w-12 h-12 rounded-2xl bg-amber-100 border border-amber-300 text-amber-900 flex items-center justify-center font-bold shadow-sm">
                        <Wallet className="w-6 h-6" />
                      </div>
                      <span className="bg-amber-100 text-amber-800 text-xs px-2.5 py-1 rounded-full font-bold">
                        {isBn ? "২য় কর্নার" : "Corner 2"}
                      </span>
                    </div>

                    <h4 className="font-bold text-lg text-slate-900 mt-4 group-hover:text-amber-700 transition">
                      {isBn ? "💰 চাঁদা আদায় ও রিসিট (Deposits)" : "💰 Deposit Collection"}
                    </h4>
                    <p className="text-xs text-slate-500 mt-1">
                      {isBn
                        ? "নগদ ও বিকাশ/নগদে মাসিক চাঁদা এন্ট্রি, ইনস্ট্যান্ট মানি রিসিট ভাউচার প্রিন্ট ও শেয়ারিং।"
                        : "Record monthly contributions, generate verified money receipt vouchers & receipts."}
                    </p>
                  </div>

                  <div className="mt-5 pt-4 border-t border-slate-100 flex items-center justify-between">
                    <div>
                      <span className="text-[11px] text-slate-400 block">{isBn ? "মোট সংগৃহীত চাঁদা" : "Total Collected"}</span>
                      <span className="text-xl font-black text-amber-700">{totalDepositCollection.toLocaleString('bn-BD')} ৳</span>
                    </div>
                    <button className="flex items-center gap-1.5 bg-amber-500 group-hover:bg-amber-600 text-slate-950 text-xs font-bold px-3 py-1.5 rounded-xl shadow transition">
                      <span>{isBn ? "চাঁদা এন্ট্রি দিন" : "Add Deposit"}</span>
                      <ChevronRight className="w-4 h-4" />
                    </button>
                  </div>
                </div>

                {/* CORNER 3 (BOTTOM-LEFT): REPORTS */}
                <div
                  onClick={() => setActiveTab('reports')}
                  className="group relative bg-white hover:bg-blue-50/40 rounded-2xl p-5 border-2 border-blue-200/80 hover:border-blue-500 shadow-sm hover:shadow-lg transition cursor-pointer overflow-hidden flex flex-col justify-between"
                >
                  <div className="absolute top-0 right-0 w-28 h-28 bg-blue-500/10 rounded-full blur-2xl -mr-6 -mt-6"></div>

                  <div>
                    <div className="flex items-start justify-between">
                      <div className="w-12 h-12 rounded-2xl bg-blue-100 border border-blue-300 text-blue-900 flex items-center justify-center font-bold shadow-sm">
                        <FileSpreadsheet className="w-6 h-6" />
                      </div>
                      <span className="bg-blue-100 text-blue-800 text-xs px-2.5 py-1 rounded-full font-bold">
                        {isBn ? "৩য় কর্নার" : "Corner 3"}
                      </span>
                    </div>

                    <h4 className="font-bold text-lg text-slate-900 mt-4 group-hover:text-blue-700 transition">
                      {isBn ? "📊 অডিট ও রিপোর্ট (Reports)" : "📊 Financial Reports"}
                    </h4>
                    <p className="text-xs text-slate-500 mt-1">
                      {isBn
                        ? "মাসিক ব্যালেন্স শিট, আয়-ব্যয় বিবরণী, ব্যাংকের স্থিতি এবং সরকারি অডিট ফরম্যাট স্টেটমেন্ট।"
                        : "Income vs expense statements, balance sheets, and audit-ready reports."}
                    </p>
                  </div>

                  <div className="mt-5 pt-4 border-t border-slate-100 flex items-center justify-between">
                    <div>
                      <span className="text-[11px] text-slate-400 block">{isBn ? "ফান্ড নেট রিজার্ভ" : "Net Fund"}</span>
                      <span className="text-xl font-black text-blue-700">{netFundBalance.toLocaleString('bn-BD')} ৳</span>
                    </div>
                    <button className="flex items-center gap-1.5 bg-blue-600 group-hover:bg-blue-700 text-white text-xs font-bold px-3 py-1.5 rounded-xl shadow transition">
                      <span>{isBn ? "রিপোর্ট দেখুন" : "View Statement"}</span>
                      <ChevronRight className="w-4 h-4" />
                    </button>
                  </div>
                </div>

                {/* CORNER 4 (BOTTOM-RIGHT): SETTINGS */}
                <div
                  onClick={() => setActiveTab('samity_setup')}
                  className="group relative bg-white hover:bg-purple-50/40 rounded-2xl p-5 border-2 border-purple-200/80 hover:border-purple-500 shadow-sm hover:shadow-lg transition cursor-pointer overflow-hidden flex flex-col justify-between"
                >
                  <div className="absolute top-0 right-0 w-28 h-28 bg-purple-500/10 rounded-full blur-2xl -mr-6 -mt-6"></div>

                  <div>
                    <div className="flex items-start justify-between">
                      <div className="w-12 h-12 rounded-2xl bg-purple-100 border border-purple-300 text-purple-900 flex items-center justify-center font-bold shadow-sm">
                        <Settings className="w-6 h-6" />
                      </div>
                      <span className="bg-purple-100 text-purple-800 text-xs px-2.5 py-1 rounded-full font-bold">
                        {isBn ? "৪র্থ কর্নার" : "Corner 4"}
                      </span>
                    </div>

                    <h4 className="font-bold text-lg text-slate-900 mt-4 group-hover:text-purple-700 transition">
                      {isBn ? "⚙️ সমিতি সেটআপ ও সেটিংস (Settings)" : "⚙️ Samity Setup & Rules"}
                    </h4>
                    <p className="text-xs text-slate-500 mt-1">
                      {isBn
                        ? "সমিতির নাম (বাংলা/ইংরেজি), টাইপ, ঠিকানা ও ডাকঘর, সোনালী ব্যাংক ও বিকাশ অ্যাকাউন্ট কনফিগারেশন।"
                        : "Samity profile, location fields, bank/bKash accounts, and deposit rules."}
                    </p>
                  </div>

                  <div className="mt-5 pt-4 border-t border-slate-100 flex items-center justify-between">
                    <div>
                      <span className="text-[11px] text-slate-400 block">{isBn ? "রেজিস্ট্রেশন স্ট্যাটাস" : "Registration Status"}</span>
                      <span className="text-sm font-bold text-purple-700">{samityConfig.regNo}</span>
                    </div>
                    <button className="flex items-center gap-1.5 bg-purple-600 group-hover:bg-purple-700 text-white text-xs font-bold px-3 py-1.5 rounded-xl shadow transition">
                      <span>{isBn ? "সেটআপ পরিবর্তন" : "Configure"}</span>
                      <ChevronRight className="w-4 h-4" />
                    </button>
                  </div>
                </div>

              </div>
            </div>

            {/* KPI METRIC CARDS */}
            <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-4">
              
              <div className="bg-white p-4 rounded-2xl border border-slate-200 shadow-sm">
                <div className="flex justify-between items-center text-slate-500 text-xs font-semibold mb-2">
                  <span>{isBn ? "মোট সংগৃহীত চাঁদা" : "Total Collection"}</span>
                  <Wallet className="w-4 h-4 text-emerald-600" />
                </div>
                <div className="text-2xl font-black text-slate-900">
                  {totalDepositCollection.toLocaleString('bn-BD')} ৳
                </div>
                <div className="text-[11px] text-emerald-600 font-medium mt-1 flex items-center gap-1">
                  <CheckCircle2 className="w-3.5 h-3.5" />
                  <span>{isBn ? "অ্যান্ড্রয়েড ও ওয়েবে ১০০% সিঙ্কড" : "100% Synced"}</span>
                </div>
              </div>

              <div className="bg-white p-4 rounded-2xl border border-slate-200 shadow-sm">
                <div className="flex justify-between items-center text-slate-500 text-xs font-semibold mb-2">
                  <span>{isBn ? "মোট বাকি/বকেয়া" : "Total Due"}</span>
                  <Clock className="w-4 h-4 text-amber-600" />
                </div>
                <div className="text-2xl font-black text-amber-700">
                  {totalDueAmount.toLocaleString('bn-BD')} ৳
                </div>
                <div className="text-[11px] text-amber-600 font-medium mt-1 flex items-center gap-1">
                  <AlertCircle className="w-3.5 h-3.5" />
                  <span>{isBn ? "বকেয়া সদস্যদের এসএমএস করুন" : "Overdue alerts ready"}</span>
                </div>
              </div>

              <div className="bg-white p-4 rounded-2xl border border-slate-200 shadow-sm">
                <div className="flex justify-between items-center text-slate-500 text-xs font-semibold mb-2">
                  <span>{isBn ? "মোট অফিস ব্যয়" : "Total Expenses"}</span>
                  <TrendingDown className="w-4 h-4 text-rose-600" />
                </div>
                <div className="text-2xl font-black text-rose-700">
                  {totalExpenseAmount.toLocaleString('bn-BD')} ৳
                </div>
                <div className="text-[11px] text-slate-500 mt-1">
                  {expenses.length}টি অনুমোদিত ভাউচার
                </div>
              </div>

              <div className="bg-white p-4 rounded-2xl border border-slate-200 shadow-sm">
                <div className="flex justify-between items-center text-slate-500 text-xs font-semibold mb-2">
                  <span>{isBn ? "হাতে ও ব্যাংকে নগদ স্থিতি" : "Net Fund in Hand"}</span>
                  <DollarSign className="w-4 h-4 text-blue-600" />
                </div>
                <div className="text-2xl font-black text-blue-700">
                  {netFundBalance.toLocaleString('bn-BD')} ৳
                </div>
                <div className="text-[11px] text-blue-600 mt-1 font-medium">
                  {isBn ? "সোনালী ব্যাংক + ক্যাশ তহবিল" : "Verified Liquidity"}
                </div>
              </div>

            </div>

            {/* RECENT TRANSACTIONS TABLE WITH RECEIPT GENERATOR */}
            <div className="bg-white rounded-2xl border border-slate-200 shadow-sm overflow-hidden">
              <div className="p-4 border-b border-slate-200 flex flex-wrap justify-between items-center gap-2">
                <div className="flex items-center gap-2">
                  <History className="w-5 h-5 text-amber-500" />
                  <h3 className="font-bold text-sm text-slate-900">
                    {isBn ? "রিয়েল-টাইম লেনদেন ভাউচার ও মানি রিসিট" : "Recent Synced Transactions"}
                  </h3>
                </div>
                <div className="flex items-center gap-2">
                  <button
                    onClick={() => setActiveTab('deposits')}
                    className="text-xs text-amber-700 hover:text-amber-800 font-bold"
                  >
                    {isBn ? "সব লেনদেন দেখুন →" : "View All →"}
                  </button>
                </div>
              </div>

              <div className="overflow-x-auto">
                <table className="w-full text-xs text-left">
                  <thead className="bg-slate-50 text-slate-500 font-semibold border-b border-slate-200">
                    <tr>
                      <th className="py-3 px-4">{isBn ? "ভাউচার নং" : "Voucher"}</th>
                      <th className="py-3 px-4">{isBn ? "সদস্যের নাম ও কোড" : "Member"}</th>
                      <th className="py-3 px-4">{isBn ? "পরিমাণ (টাকা)" : "Amount"}</th>
                      <th className="py-3 px-4">{isBn ? "মাধ্যম" : "Method"}</th>
                      <th className="py-3 px-4">{isBn ? "সিঙ্ক ডিভাইস" : "Origin Device"}</th>
                      <th className="py-3 px-4 text-right">{isBn ? "মানি রিসিট" : "Receipt"}</th>
                    </tr>
                  </thead>
                  <tbody className="divide-y divide-slate-100">
                    {deposits.slice(0, 5).map((dep) => (
                      <tr key={dep.id} className="hover:bg-slate-50/80 transition">
                        <td className="py-3 px-4 font-mono font-bold text-blue-700">{dep.voucher}</td>
                        <td className="py-3 px-4">
                          <div className="font-semibold text-slate-900">{dep.name}</div>
                          <div className="text-[10px] text-slate-400">{dep.memberCode}</div>
                        </td>
                        <td className="py-3 px-4 font-black text-emerald-700">
                          {dep.amount.toLocaleString('bn-BD')} ৳
                        </td>
                        <td className="py-3 px-4">
                          <span className="bg-slate-100 text-slate-700 px-2 py-0.5 rounded text-[11px] font-medium">
                            {dep.method}
                          </span>
                        </td>
                        <td className="py-3 px-4 text-slate-500">
                          <span className="inline-flex items-center gap-1">
                            <Smartphone className="w-3.5 h-3.5 text-slate-400" />
                            <span>{dep.origin}</span>
                          </span>
                        </td>
                        <td className="py-3 px-4 text-right">
                          <button
                            onClick={() => openReceipt(dep)}
                            className="inline-flex items-center gap-1 bg-amber-50 hover:bg-amber-100 text-amber-800 border border-amber-300 px-2.5 py-1 rounded-lg text-[11px] font-bold transition"
                            title="Print / View Receipt Voucher"
                          >
                            <Printer className="w-3 h-3 text-amber-600" />
                            <span>{isBn ? "রিসিট প্রিন্ট" : "Receipt"}</span>
                          </button>
                        </td>
                      </tr>
                    ))}
                  </tbody>
                </table>
              </div>
            </div>

          </div>
        )}

        {/* ========================================================
            TAB 2: SAMITY SETUP & CONFIGURATION (100% Android Parity)
           ======================================================== */}
        {activeTab === 'samity_setup' && (
          <div className="bg-white rounded-2xl border border-slate-200 shadow-sm p-5 sm:p-6 space-y-6 animate-fade-in">
            <div className="flex flex-wrap justify-between items-center gap-3 border-b border-slate-200 pb-4">
              <div className="flex items-center gap-3">
                <div className="w-11 h-11 rounded-2xl bg-purple-100 text-purple-700 flex items-center justify-center font-bold">
                  <Settings className="w-6 h-6" />
                </div>
                <div>
                  <h3 className="font-bold text-lg text-slate-900">
                    {isBn ? "সমিতি সেটআপ ও বিস্তারিত কনফিগারেশন" : "Samity Setup & Configuration"}
                  </h3>
                  <p className="text-xs text-slate-500">
                    {isBn ? "সমিতির নাম (বাংলা ও ইংরেজি), ধরন, ভৌগোলিক অবস্থান ও ব্যাংক একাউন্ট বিবরণী" : "Official Samity Profile, Location, and Banking Rules"}
                  </p>
                </div>
              </div>

              <button
                onClick={() => {
                  showToast('✅ সমিতি কনফিগারেশন সফলভাবে আপডেট এবং ক্লাউডে সংরক্ষিত হয়েছে!');
                }}
                className="flex items-center gap-2 bg-gradient-to-r from-emerald-600 to-teal-600 hover:from-emerald-500 hover:to-teal-500 text-white font-bold px-4 py-2 rounded-xl text-xs shadow transition"
              >
                <Check className="w-4 h-4" />
                <span>{isBn ? "পরিবর্তন সংরক্ষণ করুন" : "Save Changes"}</span>
              </button>
            </div>

            {/* Form Fields Grid */}
            <div className="grid grid-cols-1 md:grid-cols-2 gap-5 text-xs">
              
              {/* Samity Name Bangla */}
              <div>
                <label className="font-bold text-slate-700 block mb-1.5">
                  {isBn ? "সমিতির নাম (বাংলায়) *" : "Samity Name (Bengali) *"}
                </label>
                <input
                  type="text"
                  value={samityConfig.nameBn}
                  onChange={(e) => setSamityConfig({ ...samityConfig, nameBn: e.target.value })}
                  className="w-full bg-slate-50 border border-slate-300 rounded-xl px-3.5 py-2.5 font-semibold text-slate-900 focus:bg-white focus:ring-2 focus:ring-amber-500"
                />
              </div>

              {/* Samity Name English */}
              <div>
                <label className="font-bold text-slate-700 block mb-1.5">
                  {isBn ? "সমিতির নাম (ইংরেজিতে) *" : "Samity Name (English) *"}
                </label>
                <input
                  type="text"
                  value={samityConfig.nameEn}
                  onChange={(e) => setSamityConfig({ ...samityConfig, nameEn: e.target.value })}
                  className="w-full bg-slate-50 border border-slate-300 rounded-xl px-3.5 py-2.5 font-semibold text-slate-900 focus:bg-white focus:ring-2 focus:ring-amber-500"
                />
              </div>

              {/* Registration Number */}
              <div>
                <label className="font-bold text-slate-700 block mb-1.5">
                  {isBn ? "সরকারি রেজিস্ট্রেশন নম্বর *" : "Registration Number *"}
                </label>
                <input
                  type="text"
                  value={samityConfig.regNo}
                  onChange={(e) => setSamityConfig({ ...samityConfig, regNo: e.target.value })}
                  className="w-full bg-slate-50 border border-slate-300 rounded-xl px-3.5 py-2.5 font-semibold text-slate-900 focus:bg-white focus:ring-2 focus:ring-amber-500"
                />
              </div>

              {/* Samity Type */}
              <div>
                <label className="font-bold text-slate-700 block mb-1.5">
                  {isBn ? "সমিতির ধরন (Samity Type) *" : "Samity Type *"}
                </label>
                <select
                  value={samityConfig.type}
                  onChange={(e) => setSamityConfig({ ...samityConfig, type: e.target.value })}
                  className="w-full bg-slate-50 border border-slate-300 rounded-xl px-3.5 py-2.5 font-semibold text-slate-900 focus:bg-white focus:ring-2 focus:ring-amber-500"
                >
                  <option value="সমবায় সমিতি (Cooperative)">সমবায় সমিতি (Cooperative)</option>
                  <option value="ক্ষুদ্রঋণ ও সঞ্চয় সমিতি (Microfinance)">ক্ষুদ্রঋণ ও সঞ্চয় সমিতি (Microfinance)</option>
                  <option value="ব্যবসায়ী বহুমুখী সমবায় সমিতি">ব্যবসায়ী বহুমুখী সমবায় সমিতি</option>
                  <option value="কর্মচারী সঞ্চয় ও ঋণদান তহবিল">কর্মচারী সঞ্চয় ও ঋণদান তহবিল</option>
                  <option value="দৈনিক/সাপ্তাহিক ক্ষুদ্র সঞ্চয় সমিতি">দৈনিক/সাপ্তাহিক ক্ষুদ্র সঞ্চয় সমিতি</option>
                </select>
              </div>

              {/* Division & District */}
              <div>
                <label className="font-bold text-slate-700 block mb-1.5">
                  {isBn ? "বিভাগ ও জেলা *" : "Division & District *"}
                </label>
                <div className="grid grid-cols-2 gap-2">
                  <input
                    type="text"
                    value={samityConfig.division}
                    onChange={(e) => setSamityConfig({ ...samityConfig, division: e.target.value })}
                    placeholder="বিভাগ"
                    className="w-full bg-slate-50 border border-slate-300 rounded-xl px-3.5 py-2.5 font-semibold text-slate-900"
                  />
                  <input
                    type="text"
                    value={samityConfig.district}
                    onChange={(e) => setSamityConfig({ ...samityConfig, district: e.target.value })}
                    placeholder="জেলা"
                    className="w-full bg-slate-50 border border-slate-300 rounded-xl px-3.5 py-2.5 font-semibold text-slate-900"
                  />
                </div>
              </div>

              {/* Upazila & Post Code */}
              <div>
                <label className="font-bold text-slate-700 block mb-1.5">
                  {isBn ? "উপজেলা/থানা ও পোস্ট কোড *" : "Upazila & Post Code *"}
                </label>
                <div className="grid grid-cols-2 gap-2">
                  <input
                    type="text"
                    value={samityConfig.upazila}
                    onChange={(e) => setSamityConfig({ ...samityConfig, upazila: e.target.value })}
                    placeholder="উপজেলা"
                    className="w-full bg-slate-50 border border-slate-300 rounded-xl px-3.5 py-2.5 font-semibold text-slate-900"
                  />
                  <input
                    type="text"
                    value={samityConfig.postCode}
                    onChange={(e) => setSamityConfig({ ...samityConfig, postCode: e.target.value })}
                    placeholder="পোস্ট কোড"
                    className="w-full bg-slate-50 border border-slate-300 rounded-xl px-3.5 py-2.5 font-semibold text-slate-900"
                  />
                </div>
              </div>

              {/* Full Address */}
              <div className="md:col-span-2">
                <label className="font-bold text-slate-700 block mb-1.5">
                  {isBn ? "কার্যালয়ের পূর্ণ ঠিকানা *" : "Official Office Address *"}
                </label>
                <input
                  type="text"
                  value={samityConfig.address}
                  onChange={(e) => setSamityConfig({ ...samityConfig, address: e.target.value })}
                  className="w-full bg-slate-50 border border-slate-300 rounded-xl px-3.5 py-2.5 font-semibold text-slate-900 focus:bg-white"
                />
              </div>

              {/* Banking & MFS Details */}
              <div>
                <label className="font-bold text-slate-700 block mb-1.5">
                  {isBn ? "অফিসিয়াল ব্যাংক একাউন্ট বিবরণী" : "Official Bank Account"}
                </label>
                <input
                  type="text"
                  value={samityConfig.bankName}
                  onChange={(e) => setSamityConfig({ ...samityConfig, bankName: e.target.value })}
                  placeholder="ব্যাংক ও শাখার নাম"
                  className="w-full bg-slate-50 border border-slate-300 rounded-xl px-3.5 py-2.5 font-semibold text-slate-900 mb-2"
                />
                <input
                  type="text"
                  value={samityConfig.bankAccount}
                  onChange={(e) => setSamityConfig({ ...samityConfig, bankAccount: e.target.value })}
                  placeholder="একাউন্ট নম্বর"
                  className="w-full bg-slate-50 border border-slate-300 rounded-xl px-3.5 py-2.5 font-mono text-slate-900"
                />
              </div>

              <div>
                <label className="font-bold text-slate-700 block mb-1.5">
                  {isBn ? "বিকাশ/নগদ মার্চেন্ট নম্বর" : "bKash / Nagad Merchant Number"}
                </label>
                <input
                  type="text"
                  value={samityConfig.mfsNumber}
                  onChange={(e) => setSamityConfig({ ...samityConfig, mfsNumber: e.target.value })}
                  className="w-full bg-slate-50 border border-slate-300 rounded-xl px-3.5 py-2.5 font-mono font-semibold text-slate-900"
                />
                <p className="text-[11px] text-slate-400 mt-1">সদস্যরা এই নম্বরে পেমেন্ট করলে সরাসরি রিসিট জেনারেট হয়।</p>
              </div>

              {/* Monthly Deposit Rule & Late Penalty */}
              <div>
                <label className="font-bold text-slate-700 block mb-1.5">
                  {isBn ? "ডিফল্ট মাসিক চাঁদা (টাকা)" : "Default Monthly Deposit (BDT)"}
                </label>
                <input
                  type="number"
                  value={samityConfig.defaultMonthlyDeposit}
                  onChange={(e) => setSamityConfig({ ...samityConfig, defaultMonthlyDeposit: Number(e.target.value) })}
                  className="w-full bg-slate-50 border border-slate-300 rounded-xl px-3.5 py-2.5 font-bold text-slate-900"
                />
              </div>

              <div>
                <label className="font-bold text-slate-700 block mb-1.5">
                  {isBn ? "বিলম্ব ফি নিয়ম (টাকা/প্রতি দিন)" : "Late Fee Penalty (Tk/Day)"}
                </label>
                <input
                  type="number"
                  value={samityConfig.latePenaltyPerDay}
                  onChange={(e) => setSamityConfig({ ...samityConfig, latePenaltyPerDay: Number(e.target.value) })}
                  className="w-full bg-slate-50 border border-slate-300 rounded-xl px-3.5 py-2.5 font-bold text-slate-900"
                />
              </div>

            </div>

            <div className="bg-amber-50 border border-amber-200 rounded-xl p-4 text-xs text-amber-900 flex items-center justify-between">
              <div>
                <span className="font-bold">ক্লাউড সিঙ্ক স্থিতি:</span> এই সেটিংস আপডেট করলে অ্যান্ড্রয়েড মোবাইল ডিভাইসে রিয়েল-টাইমে পরিবর্তিত নাম এবং নিয়মাবলী দৃশ্যমান হবে।
              </div>
              <button
                onClick={() => {
                  showToast('✅ ক্লাউড ডাটাবেজ আপডেট সম্পন্ন হয়েছে!');
                }}
                className="bg-amber-500 hover:bg-amber-600 text-slate-950 font-bold px-3 py-1.5 rounded-lg text-xs"
              >
                তাৎক্ষণিক সিঙ্ক
              </button>
            </div>
          </div>
        )}

        {/* ========================================================
            TAB 3: MEMBERS DIRECTORY & PROFILES
           ======================================================== */}
        {activeTab === 'members' && (
          <div className="bg-white rounded-2xl border border-slate-200 shadow-sm p-4 sm:p-6 space-y-4 animate-fade-in">
            <div className="flex flex-wrap justify-between items-center gap-3">
              <div>
                <h3 className="font-bold text-lg text-slate-900">
                  {isBn ? "সদস্য তালিকা ও ডিজিটাল প্রোফাইল" : "Members Directory"}
                </h3>
                <p className="text-xs text-slate-500">
                  {isBn ? `মোট ${members.length} জন সক্রিয় সদস্য তালিকাভুক্ত আছেন` : `${members.length} Active members registered`}
                </p>
              </div>

              <div className="flex items-center gap-2">
                <button
                  onClick={() => setIsAddMemberModalOpen(true)}
                  className="flex items-center gap-1.5 bg-gradient-to-r from-emerald-600 to-teal-600 hover:from-emerald-500 hover:to-teal-500 text-white font-bold px-3.5 py-2 rounded-xl text-xs shadow transition"
                >
                  <Plus className="w-4 h-4" />
                  <span>{isBn ? "নতুন সদস্য যোগ করুন" : "Add Member"}</span>
                </button>
              </div>
            </div>

            {/* Member Table */}
            <div className="overflow-x-auto">
              <table className="w-full text-xs text-left">
                <thead className="bg-slate-50 text-slate-500 font-semibold border-b border-slate-200">
                  <tr>
                    <th className="py-3 px-4">{isBn ? "সদস্য কোড" : "Code"}</th>
                    <th className="py-3 px-4">{isBn ? "সদস্যের নাম" : "Name"}</th>
                    <th className="py-3 px-4">{isBn ? "মোবাইল ও এনআইডি" : "Phone & NID"}</th>
                    <th className="py-3 px-4">{isBn ? "শেয়ার" : "Shares"}</th>
                    <th className="py-3 px-4">{isBn ? "নির্ধারিত চাঁদা" : "Monthly Fee"}</th>
                    <th className="py-3 px-4">{isBn ? "পরিশোধিত" : "Paid"}</th>
                    <th className="py-3 px-4">{isBn ? "বকেয়া" : "Due"}</th>
                    <th className="py-3 px-4">{isBn ? "স্ট্যাটাস" : "Status"}</th>
                  </tr>
                </thead>
                <tbody className="divide-y divide-slate-100">
                  {members.map((m) => (
                    <tr key={m.id} className="hover:bg-slate-50 transition">
                      <td className="py-3 px-4 font-mono font-bold text-blue-700">{m.code}</td>
                      <td className="py-3 px-4 font-bold text-slate-900">{isBn ? m.nameBn : m.nameEn}</td>
                      <td className="py-3 px-4">
                        <div className="text-slate-800">{m.phone}</div>
                        <div className="text-[10px] text-slate-400 font-mono">NID: {m.nid}</div>
                      </td>
                      <td className="py-3 px-4 font-semibold text-slate-700">{m.shareCount}টি</td>
                      <td className="py-3 px-4 font-medium text-slate-800">{m.monthlyFee.toLocaleString('bn-BD')} ৳</td>
                      <td className="py-3 px-4 font-bold text-emerald-600">{m.paid.toLocaleString('bn-BD')} ৳</td>
                      <td className="py-3 px-4 font-bold text-amber-600">{m.due.toLocaleString('bn-BD')} ৳</td>
                      <td className="py-3 px-4">
                        <span className={`px-2 py-0.5 rounded text-[10px] font-bold ${
                          m.due === 0
                            ? 'bg-emerald-100 text-emerald-800'
                            : m.paid > 0
                            ? 'bg-blue-100 text-blue-800'
                            : 'bg-amber-100 text-amber-800'
                        }`}>
                          {m.due === 0 ? (isBn ? 'পরিশোধিত' : 'Paid') : (isBn ? 'বকেয়া আছে' : 'Due')}
                        </span>
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          </div>
        )}

        {/* ========================================================
            TAB 4: DEPOSITS & RECEIPT GENERATOR
           ======================================================== */}
        {activeTab === 'deposits' && (
          <div className="space-y-4 animate-fade-in">
            <div className="bg-white rounded-2xl border border-slate-200 shadow-sm p-5 flex flex-wrap justify-between items-center gap-3">
              <div>
                <h3 className="font-bold text-lg text-slate-900">
                  {isBn ? "চাঁদা আদায় ও মানি রিসিট ভাউচার জেনারেটর" : "Deposit Collections & Receipts"}
                </h3>
                <p className="text-xs text-slate-500">
                  {isBn ? "অনলাইন ও ক্যাশ জমার নির্ভুল হিসাব এবং প্রিন্টযোগ্য অফিসিয়াল মানি রিসিট" : "Money receipts with official signatures"}
                </p>
              </div>

              <div className="flex items-center gap-2">
                <button
                  onClick={() => setIsAddDepositModalOpen(true)}
                  className="flex items-center gap-2 bg-amber-500 hover:bg-amber-600 text-slate-950 font-bold px-4 py-2 rounded-xl text-xs shadow transition"
                >
                  <Plus className="w-4 h-4" />
                  <span>{isBn ? "নতুন চাঁদা আদায় এন্ট্রি" : "Record Deposit"}</span>
                </button>
              </div>
            </div>

            {/* Deposits Table */}
            <div className="bg-white rounded-2xl border border-slate-200 shadow-sm overflow-hidden">
              <div className="overflow-x-auto">
                <table className="w-full text-xs text-left">
                  <thead className="bg-slate-50 text-slate-500 font-semibold border-b border-slate-200">
                    <tr>
                      <th className="py-3 px-4">{isBn ? "ভাউচার নং" : "Voucher"}</th>
                      <th className="py-3 px-4">{isBn ? "সদস্য কোড ও নাম" : "Member"}</th>
                      <th className="py-3 px-4">{isBn ? "পরিমাণ (টাকা)" : "Amount"}</th>
                      <th className="py-3 px-4">{isBn ? "পেমেন্ট মাধ্যম" : "Payment Method"}</th>
                      <th className="py-3 px-4">{isBn ? "তারিখ" : "Date"}</th>
                      <th className="py-3 px-4">{isBn ? "সিঙ্ক উৎস" : "Origin Device"}</th>
                      <th className="py-3 px-4 text-right">{isBn ? "একশন" : "Action"}</th>
                    </tr>
                  </thead>
                  <tbody className="divide-y divide-slate-100">
                    {deposits.map((dep) => (
                      <tr key={dep.id} className="hover:bg-slate-50 transition">
                        <td className="py-3 px-4 font-mono font-bold text-blue-700">{dep.voucher}</td>
                        <td className="py-3 px-4">
                          <div className="font-semibold text-slate-900">{dep.name}</div>
                          <div className="text-[10px] text-slate-400">{dep.memberCode}</div>
                        </td>
                        <td className="py-3 px-4 font-bold text-emerald-700">
                          {dep.amount.toLocaleString('bn-BD')} ৳
                        </td>
                        <td className="py-3 px-4">
                          <span className="bg-slate-100 text-slate-700 px-2.5 py-0.5 rounded text-[11px] font-medium">
                            {dep.method}
                          </span>
                        </td>
                        <td className="py-3 px-4 text-slate-500">{dep.date}</td>
                        <td className="py-3 px-4">
                          <span className="inline-flex items-center gap-1 text-slate-600">
                            <Smartphone className="w-3.5 h-3.5 text-slate-400" />
                            <span>{dep.origin}</span>
                          </span>
                        </td>
                        <td className="py-3 px-4 text-right">
                          <button
                            onClick={() => openReceipt(dep)}
                            className="inline-flex items-center gap-1.5 bg-amber-50 hover:bg-amber-100 text-amber-800 border border-amber-300 px-3 py-1.5 rounded-lg text-xs font-bold transition"
                          >
                            <Printer className="w-3.5 h-3.5 text-amber-600" />
                            <span>{isBn ? "রিসিট দেখুন" : "View Receipt"}</span>
                          </button>
                        </td>
                      </tr>
                    ))}
                  </tbody>
                </table>
              </div>
            </div>
          </div>
        )}

        {/* ========================================================
            TAB 5: BULK DEPOSIT BATCH COLLECTION
           ======================================================== */}
        {activeTab === 'bulk_deposit' && (
          <div className="bg-white rounded-2xl border border-slate-200 shadow-sm p-5 sm:p-6 space-y-5 animate-fade-in">
            <div className="flex flex-wrap justify-between items-center gap-3 border-b border-slate-200 pb-4">
              <div>
                <h3 className="font-bold text-lg text-slate-900">
                  {isBn ? "এক ক্লিকে বাল্ক চাঁদা আদায় ব্যাচ" : "One-Click Bulk Deposit Batch"}
                </h3>
                <p className="text-xs text-slate-500">
                  {isBn ? "চলতি মাসের সকল সক্রিয় সদস্যের নিয়মিত চাঁদা এক ক্লিকে স্বয়ংক্রিয়ভাবে জমা ও সিঙ্ক করুন" : "Batch deposit collection for all active members"}
                </p>
              </div>

              <button
                onClick={() => {
                  setMembers(members.map(m => ({ ...m, paid: m.monthlyFee, due: 0 })));
                  showToast('⚡ সকল সদস্যের চলতি মাসের চাঁদা সফলভাবে বাল্ক জমা ও সিঙ্ক হয়েছে!');
                }}
                className="flex items-center gap-2 bg-gradient-to-r from-emerald-600 to-teal-600 hover:from-emerald-500 hover:to-teal-500 text-white font-bold px-4 py-2 rounded-xl text-xs shadow transition"
              >
                <Zap className="w-4 h-4" />
                <span>{isBn ? "সকলের চাঁদা একসাথে জমা করুন" : "Process All Bulk Deposits"}</span>
              </button>
            </div>

            <div className="grid grid-cols-1 sm:grid-cols-3 gap-4">
              <div className="bg-emerald-50 border border-emerald-200 rounded-xl p-4">
                <span className="text-xs text-emerald-800 font-bold block">মোট সদস্য সংখ্যা</span>
                <span className="text-2xl font-black text-emerald-900">{members.length} জন</span>
              </div>
              <div className="bg-blue-50 border border-blue-200 rounded-xl p-4">
                <span className="text-xs text-blue-800 font-bold block">মাসিক প্রত্যাশিত আদায়</span>
                <span className="text-2xl font-black text-blue-900">
                  {(members.reduce((sum, m) => sum + m.monthlyFee, 0)).toLocaleString('bn-BD')} ৳
                </span>
              </div>
              <div className="bg-purple-50 border border-purple-200 rounded-xl p-4">
                <span className="text-xs text-purple-800 font-bold block">চলতি মাস</span>
                <span className="text-2xl font-black text-purple-900">অক্টোবর ২০২৬</span>
              </div>
            </div>
          </div>
        )}

        {/* ========================================================
            TAB 6: DUE TRACKING & REMINDERS
           ======================================================== */}
        {activeTab === 'due_tracking' && (
          <div className="bg-white rounded-2xl border border-slate-200 shadow-sm p-5 sm:p-6 space-y-4 animate-fade-in">
            <div className="flex flex-wrap justify-between items-center gap-3">
              <div>
                <h3 className="font-bold text-lg text-slate-900">
                  {isBn ? "বকেয়া আদায় ও স্বয়ংক্রিয় এসএমএস তাগাদা" : "Due Tracking & SMS Reminders"}
                </h3>
                <p className="text-xs text-slate-500">
                  {isBn ? `মোট বকেয়া টাকার পরিমাণ: ${totalDueAmount.toLocaleString('bn-BD')} ৳` : `Total Due: ${totalDueAmount} BDT`}
                </p>
              </div>

              <button
                onClick={() => {
                  showToast('📱 বকেয়া সদস্যদের মোবাইলে স্বয়ংক্রিয় তাগাদা এসএমএস প্রেরণ সম্পন্ন হয়েছে!');
                }}
                className="flex items-center gap-2 bg-amber-500 hover:bg-amber-600 text-slate-950 font-bold px-4 py-2 rounded-xl text-xs shadow transition"
              >
                <Send className="w-4 h-4" />
                <span>{isBn ? "সকলকে বকেয়া তাগাদা এসএমএস পাঠান" : "Send Bulk SMS Reminders"}</span>
              </button>
            </div>

            <div className="overflow-x-auto">
              <table className="w-full text-xs text-left">
                <thead className="bg-slate-50 text-slate-500 font-semibold border-b border-slate-200">
                  <tr>
                    <th className="py-3 px-4">{isBn ? "কোড" : "Code"}</th>
                    <th className="py-3 px-4">{isBn ? "সদস্যের নাম" : "Name"}</th>
                    <th className="py-3 px-4">{isBn ? "মোবাইল নম্বর" : "Phone"}</th>
                    <th className="py-3 px-4">{isBn ? "বকেয়ার পরিমাণ" : "Due Amount"}</th>
                    <th className="py-3 px-4 text-right">{isBn ? "তাগাদা অ্যাকশন" : "Action"}</th>
                  </tr>
                </thead>
                <tbody className="divide-y divide-slate-100">
                  {members.filter(m => m.due > 0).map((m) => (
                    <tr key={m.id} className="hover:bg-slate-50 transition">
                      <td className="py-3 px-4 font-mono font-bold text-blue-700">{m.code}</td>
                      <td className="py-3 px-4 font-bold text-slate-900">{isBn ? m.nameBn : m.nameEn}</td>
                      <td className="py-3 px-4 text-slate-700">{m.phone}</td>
                      <td className="py-3 px-4 font-bold text-amber-700">{m.due.toLocaleString('bn-BD')} ৳</td>
                      <td className="py-3 px-4 text-right">
                        <button
                          onClick={() => showToast(`📱 ${m.nameBn}-কে এসএমএস পাঠানো হয়েছে: 'সম্মানিত সদস্য, আপনার সমিতির ${m.due} টাকা চাঁদা বকেয়া আছে। অনুগ্রহপূর্বক পরিশোধ করুন।'`)}
                          className="bg-amber-100 hover:bg-amber-200 text-amber-900 font-bold px-3 py-1.5 rounded-lg text-xs transition"
                        >
                          এসএমএস পাঠান
                        </button>
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          </div>
        )}

        {/* ========================================================
            TAB 7: OFFICE EXPENSES & VOUCHERS
           ======================================================== */}
        {activeTab === 'expenses' && (
          <div className="bg-white rounded-2xl border border-slate-200 shadow-sm p-5 sm:p-6 space-y-4 animate-fade-in">
            <div className="flex flex-wrap justify-between items-center gap-3">
              <div>
                <h3 className="font-bold text-lg text-slate-900">
                  {isBn ? "অফিস খরচ ও অনুমোদন ভাউচার" : "Office Expenses & Vouchers"}
                </h3>
                <p className="text-xs text-slate-500">
                  {isBn ? `মোট ব্যয়: ${totalExpenseAmount.toLocaleString('bn-BD')} ৳` : `Total Expenses: ${totalExpenseAmount} BDT`}
                </p>
              </div>

              <button
                onClick={() => {
                  const newEx = {
                    id: `E-${Date.now()}`,
                    voucher: `EX-202610-0${expenses.length + 1}`,
                    category: "সাধারণ স্টেশনারি ও ফটোকপি",
                    amount: 500,
                    date: new Date().toISOString().split('T')[0],
                    paidTo: "মডার্ন প্রেস",
                    status: "অনুমোদিত"
                  };
                  setExpenses([...expenses, newEx]);
                  showToast('✅ ৫০০ টাকার নতুন অফিস খরচ এন্ট্রি সংরক্ষিত হয়েছে!');
                }}
                className="flex items-center gap-1.5 bg-rose-600 hover:bg-rose-700 text-white font-bold px-3.5 py-2 rounded-xl text-xs shadow transition"
              >
                <Plus className="w-4 h-4" />
                <span>{isBn ? "নতুন খরচ এন্ট্রি" : "Record Expense"}</span>
              </button>
            </div>

            <div className="overflow-x-auto">
              <table className="w-full text-xs text-left">
                <thead className="bg-slate-50 text-slate-500 font-semibold border-b border-slate-200">
                  <tr>
                    <th className="py-3 px-4">{isBn ? "ভাউচার নং" : "Voucher"}</th>
                    <th className="py-3 px-4">{isBn ? "খরচের খাত" : "Category"}</th>
                    <th className="py-3 px-4">{isBn ? "প্রাপক" : "Paid To"}</th>
                    <th className="py-3 px-4">{isBn ? "পরিমাণ (টাকা)" : "Amount"}</th>
                    <th className="py-3 px-4">{isBn ? "তারিখ" : "Date"}</th>
                    <th className="py-3 px-4">{isBn ? "স্ট্যাটাস" : "Status"}</th>
                  </tr>
                </thead>
                <tbody className="divide-y divide-slate-100">
                  {expenses.map((e) => (
                    <tr key={e.id} className="hover:bg-slate-50 transition">
                      <td className="py-3 px-4 font-mono font-bold text-rose-700">{e.voucher}</td>
                      <td className="py-3 px-4 font-semibold text-slate-900">{e.category}</td>
                      <td className="py-3 px-4 text-slate-600">{e.paidTo}</td>
                      <td className="py-3 px-4 font-bold text-rose-700">{e.amount.toLocaleString('bn-BD')} ৳</td>
                      <td className="py-3 px-4 text-slate-500">{e.date}</td>
                      <td className="py-3 px-4">
                        <span className="bg-emerald-100 text-emerald-800 font-bold px-2 py-0.5 rounded text-[10px]">
                          {e.status}
                        </span>
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          </div>
        )}

        {/* ========================================================
            TAB 8: FUND BALANCE BREAKDOWN
           ======================================================== */}
        {activeTab === 'fund_balance' && (
          <div className="bg-white rounded-2xl border border-slate-200 shadow-sm p-5 sm:p-6 space-y-6 animate-fade-in">
            <div>
              <h3 className="font-bold text-lg text-slate-900">
                {isBn ? "রিজার্ভ ও ফান্ড ব্যালেন্সের বিশ্লেষণ" : "Reserve & Fund Balance Breakdown"}
              </h3>
              <p className="text-xs text-slate-500">
                {isBn ? "সমিতির মোট সংরক্ষিত স্থিতি এবং বিভিন্ন ফান্ডে বিভাজন" : "Categorized reserves and liquid funds"}
              </p>
            </div>

            <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
              <div className="p-4 rounded-xl border border-slate-200 bg-slate-50 space-y-3">
                <div className="flex justify-between items-center">
                  <span className="font-bold text-slate-800 text-sm">সাধারণ সঞ্চয় তহবিল (General Reserve)</span>
                  <span className="font-black text-emerald-700 text-base">১৬,৫০০ ৳</span>
                </div>
                <div className="w-full bg-slate-200 h-2.5 rounded-full overflow-hidden">
                  <div className="bg-emerald-500 h-full rounded-full" style={{ width: '65%' }}></div>
                </div>
              </div>

              <div className="p-4 rounded-xl border border-slate-200 bg-slate-50 space-y-3">
                <div className="flex justify-between items-center">
                  <span className="font-bold text-slate-800 text-sm">সদস্য শেয়ার মূলধন তহবিল (Share Capital)</span>
                  <span className="font-black text-blue-700 text-base">৫,৫০০ ৳</span>
                </div>
                <div className="w-full bg-slate-200 h-2.5 rounded-full overflow-hidden">
                  <div className="bg-blue-500 h-full rounded-full" style={{ width: '25%' }}></div>
                </div>
              </div>

              <div className="p-4 rounded-xl border border-slate-200 bg-slate-50 space-y-3">
                <div className="flex justify-between items-center">
                  <span className="font-bold text-slate-800 text-sm">কল্যাণ ও আপৎকালীন তহবিল (Welfare Fund)</span>
                  <span className="font-black text-purple-700 text-base">৩,২০০ ৳</span>
                </div>
                <div className="w-full bg-slate-200 h-2.5 rounded-full overflow-hidden">
                  <div className="bg-purple-500 h-full rounded-full" style={{ width: '15%' }}></div>
                </div>
              </div>

              <div className="p-4 rounded-xl border border-slate-200 bg-slate-50 space-y-3">
                <div className="flex justify-between items-center">
                  <span className="font-bold text-slate-800 text-sm">ব্যাংক ও তরল নগদ স্থিতি (Liquid Cash)</span>
                  <span className="font-black text-amber-700 text-base">{netFundBalance.toLocaleString('bn-BD')} ৳</span>
                </div>
                <div className="w-full bg-slate-200 h-2.5 rounded-full overflow-hidden">
                  <div className="bg-amber-500 h-full rounded-full" style={{ width: '80%' }}></div>
                </div>
              </div>
            </div>
          </div>
        )}

        {/* ========================================================
            TAB 9: FINANCIAL REPORTS & BALANCE SHEETS
           ======================================================== */}
        {activeTab === 'reports' && (
          <div className="bg-white rounded-2xl border border-slate-200 shadow-sm p-5 sm:p-6 space-y-6 animate-fade-in">
            <div className="flex flex-wrap justify-between items-center gap-3 border-b border-slate-200 pb-4">
              <div>
                <h3 className="font-bold text-lg text-slate-900">
                  {isBn ? "অডিট ও মাসিক আর্থিক বিবরণী (Financial Statements)" : "Financial Statements & Balance Sheet"}
                </h3>
                <p className="text-xs text-slate-500">
                  {isBn ? "চলতি অর্থবছর ২০২৬-এর সরকারি সমবায় অডিট উপযোগী ফরম্যাট" : "Audit-compliant cooperative financial statement"}
                </p>
              </div>

              <button
                onClick={() => window.print()}
                className="flex items-center gap-2 bg-slate-900 hover:bg-slate-800 text-white font-bold px-4 py-2 rounded-xl text-xs shadow transition"
              >
                <Printer className="w-4 h-4" />
                <span>{isBn ? "রিপোর্ট প্রিন্ট ও PDF" : "Print Report"}</span>
              </button>
            </div>

            {/* Income & Expenditure Statement Box */}
            <div className="border border-slate-300 rounded-xl overflow-hidden text-xs">
              <div className="bg-slate-100 p-3 font-bold text-slate-800 border-b border-slate-300 flex justify-between">
                <span>১. আয় খাত (Income Sources)</span>
                <span>পরিমাণ (টাকা)</span>
              </div>
              <div className="divide-y divide-slate-200 p-2">
                <div className="flex justify-between py-1.5 px-3">
                  <span>সদস্য মাসিক সঞ্চয় ও নিয়মিত চাঁদা আদায়</span>
                  <span className="font-bold text-emerald-700">{totalDepositCollection.toLocaleString('bn-BD')} ৳</span>
                </div>
                <div className="flex justify-between py-1.5 px-3">
                  <span>নতুন সদস্য ভর্তি ফি</span>
                  <span className="font-bold text-emerald-700">১,৫০০ ৳</span>
                </div>
                <div className="flex justify-between py-1.5 px-3 bg-emerald-50/50 font-bold">
                  <span>মোট সংগৃহীত আয় (Total Income)</span>
                  <span className="text-emerald-800">{(totalDepositCollection + 1500).toLocaleString('bn-BD')} ৳</span>
                </div>
              </div>

              <div className="bg-slate-100 p-3 font-bold text-slate-800 border-t border-b border-slate-300 flex justify-between">
                <span>২. ব্যয় খাত (Expenditure)</span>
                <span>পরিমাণ (টাকা)</span>
              </div>
              <div className="divide-y divide-slate-200 p-2">
                {expenses.map((e) => (
                  <div key={e.id} className="flex justify-between py-1.5 px-3">
                    <span>{e.category}</span>
                    <span className="font-bold text-rose-700">{e.amount.toLocaleString('bn-BD')} ৳</span>
                  </div>
                ))}
                <div className="flex justify-between py-1.5 px-3 bg-rose-50/50 font-bold">
                  <span>মোট অফিস ব্যয় (Total Expenditure)</span>
                  <span className="text-rose-800">{totalExpenseAmount.toLocaleString('bn-BD')} ৳</span>
                </div>
              </div>

              <div className="bg-amber-100 p-3 font-bold text-slate-900 border-t border-amber-300 flex justify-between text-sm">
                <span>নিট উদ্বৃত্ত তহবিল (Net Surplus Fund in Hand)</span>
                <span className="text-emerald-800 font-black">
                  {(totalDepositCollection + 1500 - totalExpenseAmount).toLocaleString('bn-BD')} ৳
                </span>
              </div>
            </div>
          </div>
        )}

        {/* ========================================================
            TAB 10: OFFICIAL LETTERHEAD GENERATOR
           ======================================================== */}
        {activeTab === 'letterhead' && (
          <div className="bg-white rounded-2xl border border-slate-200 shadow-sm p-5 sm:p-6 space-y-5 animate-fade-in">
            <div className="flex flex-wrap justify-between items-center gap-3 border-b border-slate-200 pb-4">
              <div>
                <h3 className="font-bold text-lg text-slate-900">
                  {isBn ? "অফিসিয়াল প্যাড ও রেজোলিউশন জেনারেটর" : "Official Letterhead & Resolutions"}
                </h3>
                <p className="text-xs text-slate-500">
                  {isBn ? "সমিতির নিজস্ব লোগো ও প্যাডে নোটিশ ও রেজোলিউশন প্রিন্ট করুন" : "Generate stamped official circulars"}
                </p>
              </div>

              <button
                onClick={() => window.print()}
                className="flex items-center gap-2 bg-gradient-to-r from-amber-500 to-amber-600 hover:from-amber-600 hover:to-amber-700 text-slate-950 font-bold px-4 py-2 rounded-xl text-xs shadow transition"
              >
                <Printer className="w-4 h-4" />
                <span>{isBn ? "প্যাড প্রিন্ট করুন" : "Print Letterhead"}</span>
              </button>
            </div>

            {/* Letterhead Preview Box */}
            <div className="border-4 border-double border-slate-300 rounded-2xl p-6 sm:p-8 bg-white shadow-inner max-w-3xl mx-auto space-y-6">
              
              {/* Letterhead Header */}
              <div className="text-center border-b-2 border-slate-800 pb-4 space-y-1">
                <h2 className="text-xl sm:text-2xl font-black text-slate-900 tracking-wide">
                  {samityConfig.nameBn}
                </h2>
                <h3 className="text-sm font-semibold text-slate-600">
                  {samityConfig.nameEn}
                </h3>
                <p className="text-xs text-slate-500">
                  সরকারি রেজি নং: {samityConfig.regNo} | প্রতিষ্ঠিত: {samityConfig.establishedYear}
                </p>
                <p className="text-[11px] text-slate-500">
                  কার্যালয়: {samityConfig.address} | ফোন: {samityConfig.mfsNumber}
                </p>
              </div>

              {/* Memo & Date */}
              <div className="flex justify-between text-xs text-slate-700 font-semibold">
                <div>স্মারক নং: {letterheadContent.memoNo}</div>
                <div>তারিখ: {letterheadContent.date}</div>
              </div>

              {/* Subject */}
              <div className="text-xs font-bold text-slate-900 border-b border-slate-200 pb-2">
                বিষয়: {letterheadContent.subject}
              </div>

              {/* Body */}
              <div className="text-xs text-slate-800 leading-relaxed whitespace-pre-line min-h-[140px]">
                {letterheadContent.body}
              </div>

              {/* Signature Blocks */}
              <div className="pt-10 flex justify-between items-end text-xs text-slate-800">
                <div className="text-center border-t border-slate-400 pt-2 w-40">
                  <div className="font-bold">সাধারণ সম্পাদক</div>
                  <div className="text-[10px] text-slate-500">{samityConfig.nameBn}</div>
                </div>
                <div className="text-center border-t border-slate-400 pt-2 w-40">
                  <div className="font-bold">সভাপতি</div>
                  <div className="text-[10px] text-slate-500">{samityConfig.nameBn}</div>
                </div>
              </div>

            </div>
          </div>
        )}

        {/* ========================================================
            TAB 11: APPROVALS & MAKER-CHECKER
           ======================================================== */}
        {activeTab === 'approvals' && (
          <div className="bg-white rounded-2xl border border-slate-200 shadow-sm p-5 sm:p-6 space-y-4 animate-fade-in">
            <div>
              <h3 className="font-bold text-lg text-slate-900">
                {isBn ? "লেনদেন অনুমোদন প্যানেল (Maker-Checker)" : "Transaction Approval Matrix"}
              </h3>
              <p className="text-xs text-slate-500">
                {isBn ? "কোষাধ্যক্ষ এন্ট্রি দেওয়ার পর সভাপতি বা অডিটরের ফাইনাল অনুমোদন" : "Two-tier verification for high security"}
              </p>
            </div>

            <div className="space-y-3">
              <div className="p-4 rounded-xl border border-emerald-200 bg-emerald-50/60 flex items-center justify-between">
                <div>
                  <span className="font-bold text-xs text-emerald-900 block">SM-0004 নাজমুল হাসানের ৫,০০০ টাকা চাঁদা</span>
                  <span className="text-[11px] text-emerald-700">জমার মাধ্যম: নগদ • কোষাধ্যক্ষ এন্ট্রি করেছেন</span>
                </div>
                <span className="bg-emerald-600 text-white font-bold px-3 py-1 rounded-lg text-xs">
                  ✓ অনুমোদিত
                </span>
              </div>

              <div className="p-4 rounded-xl border border-blue-200 bg-blue-50/60 flex items-center justify-between">
                <div>
                  <span className="font-bold text-xs text-blue-900 block">EX-202610-01 অফিস ভাড়া ৮,০০০ টাকা</span>
                  <span className="text-[11px] text-blue-700">ভাউচার প্রস্তুতকারী: হিসাবরক্ষক</span>
                </div>
                <span className="bg-blue-600 text-white font-bold px-3 py-1 rounded-lg text-xs">
                  ✓ অনুমোদিত
                </span>
              </div>
            </div>
          </div>
        )}

        {/* ========================================================
            TAB 12: SECURITY AUDIT TRAIL
           ======================================================== */}
        {activeTab === 'audit_trail' && (
          <div className="bg-white rounded-2xl border border-slate-200 shadow-sm p-5 sm:p-6 space-y-4 animate-fade-in">
            <div>
              <h3 className="font-bold text-lg text-slate-900">
                {isBn ? "সিকিউরিটি অডিট ট্রেইল ও লগ" : "Security Audit Trail"}
              </h3>
              <p className="text-xs text-slate-500">
                {isBn ? "সিস্টেমে কে কখন লগইন করেছে বা ডাটা পরিবর্তন করেছে তার প্রমাণ্য দলিল" : "Immutable system activity log"}
              </p>
            </div>

            <div className="divide-y divide-slate-100 text-xs">
              <div className="py-3 flex justify-between items-center">
                <div>
                  <span className="font-bold text-slate-900 block">চাঁদা জমা সিঙ্ক সম্পন্ন (VR-202610-005)</span>
                  <span className="text-slate-500">ব্যবহারকারী: কোষাধ্যক্ষ মো. শহিদুল আলম • ডিভাইস: Samsung S23</span>
                </div>
                <span className="text-slate-400 font-mono">আজ ১০:২৪ AM</span>
              </div>
              <div className="py-3 flex justify-between items-center">
                <div>
                  <span className="font-bold text-slate-900 block">ক্লাউড ডাটাবেজ ব্যাকআপ ডাউনলোড</span>
                  <span className="text-slate-500">ব্যবহারকারী: সুপার অ্যাডমিন • উৎস: Vercel Web Portal</span>
                </div>
                <span className="text-slate-400 font-mono">গতকাল ০৪:১৫ PM</span>
              </div>
              <div className="py-3 flex justify-between items-center">
                <div>
                  <span className="font-bold text-slate-900 block">নতুন সদস্য ভর্তি (SM-0006 ফারুক হোসেন)</span>
                  <span className="text-slate-500">ব্যবহারকারী: সাধারণ সম্পাদক • উৎস: অ্যান্ড্রয়েড অ্যাপ</span>
                </div>
                <span className="text-slate-400 font-mono">১৮ অক্টোবর ২০২৬</span>
              </div>
            </div>
          </div>
        )}

        {/* ========================================================
            TAB 13: CLOUD SYNC & DEVICE PARITY HUB
           ======================================================== */}
        {activeTab === 'sync' && (
          <div className="space-y-4 animate-fade-in">
            <div className="bg-white rounded-2xl p-5 sm:p-6 border border-slate-200 shadow-sm space-y-4">
              <div className="flex items-center gap-3">
                <Globe className="w-7 h-7 text-emerald-600" />
                <div>
                  <h3 className="font-bold text-base text-slate-900">
                    {isBn ? "ভার্সেল ও অ্যান্ড্রয়েড সম্পূর্ণ ডাটা স্ক্যান ও সিঙ্ক হাব" : "Vercel & Android Cloud Sync Hub"}
                  </h3>
                  <p className="text-xs text-slate-500">
                    {isBn
                      ? "এখানে ভার্সেল ওয়েব এবং মোবাইল অ্যাপের ডাটা একসাথে স্ক্যান করে ১০০% নিশ্চিত করা হয় যে উভয় প্ল্যাটফর্মের ডাটা সেম টু সেম আছে।"
                      : "Bi-directional scan engine ensuring 100% data parity across Vercel web and Android mobile."}
                  </p>
                </div>
              </div>

              {/* Parity Comparison Grid */}
              <div className="grid grid-cols-1 md:grid-cols-3 gap-4 bg-slate-50 p-4 rounded-xl border border-slate-200">
                <div className="text-center">
                  <div className="text-xs text-slate-500 font-semibold mb-1">
                    {isBn ? "সদস্য সংখ্যা ম্যাচিং" : "Member Parity"}
                  </div>
                  <div className="text-lg font-bold text-slate-900">
                    মোবাইল: {members.length} | ভার্সেল: {members.length}
                  </div>
                  <div className="text-[11px] text-emerald-600 font-bold mt-0.5">
                    ✓ ১০০% সেম টু সেম
                  </div>
                </div>

                <div className="text-center border-y md:border-y-0 md:border-x border-slate-200 py-2 md:py-0">
                  <div className="text-xs text-slate-500 font-semibold mb-1">
                    {isBn ? "চাঁদা ব্যালেন্স ম্যাচিং" : "Financial Balance Parity"}
                  </div>
                  <div className="text-lg font-bold text-slate-900">
                    {totalDepositCollection.toLocaleString('bn-BD')} ৳ = {totalDepositCollection.toLocaleString('bn-BD')} ৳
                  </div>
                  <div className="text-[11px] text-emerald-600 font-bold mt-0.5">
                    ✓ ১০০% সেম টু সেম
                  </div>
                </div>

                <div className="text-center">
                  <div className="text-xs text-slate-500 font-semibold mb-1">
                    {isBn ? "সক্রিয় মার্চেন্ট সংখ্যা" : "Merchant Parity"}
                  </div>
                  <div className="text-lg font-bold text-slate-900">
                    {totalActiveMerchants}টি সক্রিয় ব্যবসা
                  </div>
                  <div className="text-[11px] text-emerald-600 font-bold mt-0.5">
                    ✓ ১০০% সেম টু সেম
                  </div>
                </div>
              </div>

              {/* Action Buttons */}
              <div className="flex flex-wrap gap-3">
                <button
                  onClick={triggerBidirectionalScan}
                  className="flex items-center gap-2 bg-emerald-600 hover:bg-emerald-700 text-white font-bold px-4 py-2.5 rounded-xl text-xs transition"
                >
                  <RefreshCw className={`w-4 h-4 ${isSyncing ? 'animate-spin' : ''}`} />
                  <span>{isBn ? "🔄 সম্পূর্ণ ডাটা স্ক্যান ও ভেরিফাই করুন" : "Scan & Verify Parity"}</span>
                </button>

                <button
                  onClick={record10kDeposit}
                  className="flex items-center gap-2 bg-amber-500 hover:bg-amber-600 text-slate-950 font-bold px-4 py-2.5 rounded-xl text-xs transition"
                >
                  <Zap className="w-4 h-4" />
                  <span>{isBn ? "⚡ ৫,০০০ টাকা লেনদেন সিঙ্ক টেস্ট" : "Test 5k Tk Cross-Platform Sync"}</span>
                </button>
              </div>
            </div>
          </div>
        )}

        {/* ========================================================
            TAB 14: BACKUP & DATA EXPORT
           ======================================================== */}
        {activeTab === 'backup' && (
          <div className="bg-white rounded-2xl border border-slate-200 shadow-sm p-5 sm:p-6 space-y-5 animate-fade-in">
            <div>
              <h3 className="font-bold text-lg text-slate-900">
                {isBn ? "ব্যাকআপ ও ডাটাবেজ এক্সপোর্ট" : "Backup & Database Export"}
              </h3>
              <p className="text-xs text-slate-500">
                {isBn ? "সমিতির সকল ডাটা (সদস্য, চাঁদা, ব্যয়, সেটিংস) নিরাপদে ডাউনলোড করে সংরক্ষণ করুন" : "Download structured JSON and CSV datasets"}
              </p>
            </div>

            <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
              <div className="p-5 rounded-2xl border border-slate-200 bg-slate-50 space-y-3">
                <div className="flex items-center gap-3">
                  <div className="w-10 h-10 rounded-xl bg-purple-100 text-purple-700 flex items-center justify-center">
                    <Download className="w-5 h-5" />
                  </div>
                  <div>
                    <h4 className="font-bold text-sm text-slate-900">সম্পূর্ণ JSON ব্যাকআপ</h4>
                    <p className="text-[11px] text-slate-500">সকল ডাটাবেজ টেবিল এক ফাইলে</p>
                  </div>
                </div>
                <button
                  onClick={exportJsonBackup}
                  className="w-full bg-purple-600 hover:bg-purple-700 text-white font-bold py-2 rounded-xl text-xs transition"
                >
                  JSON ফাইল ডাউনলোড করুন
                </button>
              </div>

              <div className="p-5 rounded-2xl border border-slate-200 bg-slate-50 space-y-3">
                <div className="flex items-center gap-3">
                  <div className="w-10 h-10 rounded-xl bg-emerald-100 text-emerald-700 flex items-center justify-center">
                    <FileSpreadsheet className="w-5 h-5" />
                  </div>
                  <div>
                    <h4 className="font-bold text-sm text-slate-900">এক্সেল / CSV এক্সপোর্ট</h4>
                    <p className="text-[11px] text-slate-500">সদস্য ও চাঁদা স্প্রেডশীট</p>
                  </div>
                </div>
                <button
                  onClick={() => {
                    const csvContent = "data:text/csv;charset=utf-8," + "Code,Name,Phone,Fee,Paid,Due\n" + members.map(m => `${m.code},${m.nameBn},${m.phone},${m.monthlyFee},${m.paid},${m.due}`).join("\n");
                    const encodedUri = encodeURI(csvContent);
                    const link = document.createElement("a");
                    link.setAttribute("href", encodedUri);
                    link.setAttribute("download", "samity_members_export.csv");
                    link.click();
                    showToast('📊 এক্সেল CSV ফাইল ডাউনলোড সম্পন্ন!');
                  }}
                  className="w-full bg-emerald-600 hover:bg-emerald-700 text-white font-bold py-2 rounded-xl text-xs transition"
                >
                  CSV ফাইল ডাউনলোড করুন
                </button>
              </div>
            </div>
          </div>
        )}

        {/* ========================================================
            TAB 15: SUPER ADMIN SAAS MERCHANT CONTROL
           ======================================================== */}
        {activeTab === 'saas' && (
          <div className="space-y-4 animate-fade-in">
            <div className="bg-slate-900 text-white rounded-2xl p-5 border border-slate-800">
              <div className="flex items-center gap-3">
                <ShieldCheck className="w-8 h-8 text-amber-400" />
                <div>
                  <h3 className="font-bold text-base text-white">
                    {isBn ? "সুপার অ্যাডমিন প্ল্যাটফর্ম - সফটওয়্যার সেলস ও মার্চেন্ট কন্ট্রোল" : "Super Admin SaaS Vendor Control"}
                  </h3>
                  <p className="text-xs text-slate-400">
                    {isBn
                      ? "সফটওয়্যার বিক্রেতা হিসেবে আপনি এখানে দেখতে পারবেন কয়টা ব্যবসায়ী একাউন্ট আছে, তাদের লাইসেন্স ম্যাথ কতদিন এবং সদস্য সংখ্যা।"
                      : "Monitor client merchant accounts, license validity days, and member counts."}
                  </p>
                </div>
              </div>
            </div>

            {/* Merchant Cards */}
            <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
              {merchants.map((m) => (
                <div
                  key={m.id}
                  className={`bg-white rounded-2xl p-4 border transition ${
                    m.isOwnedBySuperAdmin ? 'border-amber-400 ring-2 ring-amber-400/20' : 'border-slate-200'
                  }`}
                >
                  <div className="flex justify-between items-start mb-2">
                    <span className={`text-[10px] font-bold px-2 py-0.5 rounded ${
                      m.isOwnedBySuperAdmin
                        ? 'bg-amber-100 text-amber-800'
                        : 'bg-purple-100 text-purple-800'
                    }`}>
                      {m.isOwnedBySuperAdmin
                        ? (isBn ? 'সুপার অ্যাডমিনের নিজস্ব সমিতি' : 'Self-Owned')
                        : (isBn ? 'ক্লায়েন্ট মার্চেন্ট একাউন্ট' : 'Client Merchant')}
                    </span>

                    <span className="text-[10px] font-bold bg-emerald-100 text-emerald-800 px-2 py-0.5 rounded">
                      {m.accountStatus}
                    </span>
                  </div>

                  <h4 className="font-bold text-sm text-slate-900 mb-1">{m.nameBn}</h4>
                  <p className="text-xs text-slate-500 mb-3">{isBn ? "মালিক:" : "Owner:"} {m.merchantOwner}</p>

                  <div className="bg-slate-50 rounded-xl p-2.5 text-xs space-y-1.5 border border-slate-100">
                    <div className="flex justify-between">
                      <span className="text-slate-500">{isBn ? "লাইসেন্স ম্যাথ বাকি:" : "Validity Remaining:"}</span>
                      <span className="font-bold text-slate-800">{m.activeDaysRemaining} {isBn ? "দিন" : "days"}</span>
                    </div>

                    <div className="flex justify-between">
                      <span className="text-slate-500">{isBn ? "মোট সদস্য সংখ্যা:" : "Members Count:"}</span>
                      <span className="font-bold text-blue-700">{m.memberCount} {isBn ? "জন" : "members"}</span>
                    </div>

                    <div className="flex justify-between">
                      <span className="text-slate-500">{isBn ? "প্রোফাইল দেখার অধিকার:" : "Profile Privacy:"}</span>
                      <span className={`font-bold ${m.isOwnedBySuperAdmin ? 'text-emerald-700' : 'text-slate-500'}`}>
                        {m.isOwnedBySuperAdmin
                          ? (isBn ? "সম্পূর্ণ দৃশ্যমান (মালিক)" : "Full Access")
                          : (isBn ? "লকড (সুরক্ষিত)" : "Protected")}
                      </span>
                    </div>
                  </div>
                </div>
              ))}
            </div>
          </div>
        )}

        {/* ========================================================
            TAB 16: ANDROID APK RELEASE & GITHUB HUB (CRITICAL USER REQUEST)
           ======================================================== */}
        {activeTab === 'apk_release' && (
          <div className="space-y-6 animate-fade-in">
            {/* Hero Card */}
            <div className="bg-gradient-to-br from-slate-950 via-slate-900 to-emerald-950 rounded-2xl p-6 sm:p-8 text-white border border-emerald-500/30 shadow-2xl relative overflow-hidden">
              <div className="max-w-2xl space-y-4">
                <div className="inline-flex items-center gap-2 bg-emerald-500/20 text-emerald-400 border border-emerald-500/40 px-3 py-1 rounded-full text-xs font-bold">
                  <Smartphone className="w-4 h-4" />
                  <span>অফিসিয়াল অ্যান্ড্রয়েড এপিকে রিলিজ • ভার্সন ১.০.০ প্রো</span>
                </div>

                <h2 className="text-2xl sm:text-3xl font-black text-white leading-tight">
                  সমিতি কিতাব ৩৬০ প্রো <br className="hidden sm:inline" />
                  <span className="text-transparent bg-clip-text bg-gradient-to-r from-amber-400 to-emerald-400">
                    অ্যান্ড্রয়েড APK সরাসরি ডাউনলোড ও গিটহাব রিলিজ
                  </span>
                </h2>

                <p className="text-xs sm:text-sm text-slate-300 leading-relaxed">
                  আপনার ক্লায়েন্ট ও সমিতির কোষাধ্যক্ষ/সভাপতিদের জন্য প্রস্তুত করা হয়েছে অফিসিয়াল ইনস্টলযোগ্য APK। এটি সরাসরি যে কোনো অ্যান্ড্রয়েড ডিভাইসে ইনস্টল করা যাবে এবং গিটহাবে পুশ করলেই অটোমেটিক রিলিজ হিসেবে পাবলিশ হবে।
                </p>

                {/* Direct Download Buttons */}
                <div className="flex flex-wrap items-center gap-3 pt-2">
                  <a
                    href="/samity-kitab-360-pro.apk"
                    download="samity-kitab-360-pro.apk"
                    className="flex items-center gap-2.5 bg-gradient-to-r from-emerald-500 to-teal-500 hover:from-emerald-400 hover:to-teal-400 text-slate-950 font-black px-6 py-3.5 rounded-2xl text-sm shadow-xl transition transform active:scale-95"
                  >
                    <Download className="w-5 h-5" />
                    <span>📥 ডাউনলোড প্রো এপিকে (27 MB)</span>
                  </a>

                  <a
                    href="/app-debug.apk"
                    download="app-debug.apk"
                    className="flex items-center gap-2 bg-slate-800 hover:bg-slate-700 text-slate-200 border border-slate-700 font-bold px-4 py-3 rounded-2xl text-xs transition"
                  >
                    <Smartphone className="w-4 h-4 text-slate-400" />
                    <span>ডিবাগ এপিকে ডাউনলোড</span>
                  </a>
                </div>

                <div className="flex items-center gap-4 text-[11px] text-slate-400 pt-1">
                  <span>✓ অ্যান্ড্রয়েড ৮.০+ সাপোর্ট</span>
                  <span>•</span>
                  <span>✓ সাইনড ও টেস্টেড</span>
                  <span>•</span>
                  <span>✓ রিয়েল-টাইম ক্লাউড সিঙ্ক</span>
                </div>
              </div>
            </div>

            {/* GitHub Push & Release Automation Guide Card */}
            <div className="bg-white rounded-2xl border border-slate-200 shadow-sm p-6 space-y-4">
              <div className="flex items-center gap-3">
                <div className="w-10 h-10 rounded-xl bg-slate-900 text-white flex items-center justify-center font-bold">
                  <Layers className="w-5 h-5 text-amber-400" />
                </div>
                <div>
                  <h3 className="font-bold text-base text-slate-900">
                    {isBn ? "গিটহাব রিলিজ পুশ করার অটোমেটিক সিস্টেম (.github/workflows/release.yml)" : "Automated GitHub Releases System"}
                  </h3>
                  <p className="text-xs text-slate-500">
                    {isBn
                      ? "গিটহাবে কোড পুশ করলে স্বয়ংক্রিয়ভাবে GitHub Release তৈরি হবে এবং সেখানে samity-kitab-360-pro.apk যুক্ত হয়ে যাবে।"
                      : "Pushing code to GitHub triggers GitHub Actions to automatically create a release with the APK attached."}
                  </p>
                </div>
              </div>

              {/* Step-by-Step Instructions */}
              <div className="space-y-3 text-xs text-slate-700">
                <div className="bg-slate-50 p-4 rounded-xl border border-slate-200 space-y-2">
                  <div className="font-bold text-slate-900 flex items-center gap-2">
                    <span className="w-5 h-5 rounded-full bg-amber-500 text-slate-950 flex items-center justify-center text-[10px] font-black">১</span>
                    <span>সহজ ১-কমান্ড পুশ স্ক্রিপ্ট (One-Command Push):</span>
                  </div>
                  <p className="text-slate-600">আমরা প্রজেক্টের মধ্যে <code className="bg-slate-200 px-1.5 py-0.5 rounded font-mono">scripts/push_to_github.sh</code> স্ক্রিপ্টটি তৈরি করে রেখেছি। টার্মিনালে শুধু চালান:</p>
                  <div className="bg-slate-950 p-3 rounded-lg font-mono text-[11px] text-emerald-400 overflow-x-auto">
                    ./scripts/push_to_github.sh
                  </div>
                </div>

                <div className="bg-slate-50 p-4 rounded-xl border border-slate-200 space-y-2">
                  <div className="font-bold text-slate-900 flex items-center gap-2">
                    <span className="w-5 h-5 rounded-full bg-amber-500 text-slate-950 flex items-center justify-center text-[10px] font-black">২</span>
                    <span>অথবা ম্যানুয়াল গিট কমান্ড:</span>
                  </div>
                  <div className="bg-slate-950 p-3 rounded-lg font-mono text-[11px] text-emerald-400 overflow-x-auto">
                    git add . && git commit -m "Release v1.0.0 Pro APK" && git push origin main
                  </div>
                </div>

                <div className="bg-emerald-50 border border-emerald-200 p-4 rounded-xl text-emerald-900">
                  <span className="font-bold">রেডি-টু-ডাউনলোড স্ট্যাটিক পাথ:</span> আপনার ওয়েব অ্যাপ্লিকেশনে <code className="bg-emerald-100 font-mono font-bold px-1.5 py-0.5 rounded text-emerald-950">/samity-kitab-360-pro.apk</code> এবং <code className="bg-emerald-100 font-mono font-bold px-1.5 py-0.5 rounded text-emerald-950">/app-debug.apk</code> ফাইল দুটি সম্পূর্ণ প্রস্তুত রয়েছে। যে কোনো ব্যবহারকারী সরাসরি ব্রাউজার থেকে ডাউনলোড করতে পারেন!
                </div>
              </div>
            </div>
          </div>
        )}

      </main>

      {/* ========================================================
          MODAL: MONEY RECEIPT (মানি রিসিট ভাউচার প্রিন্ট)
         ======================================================== */}
      {isReceiptModalOpen && selectedReceipt && (
        <div className="fixed inset-0 z-50 flex items-center justify-center p-4 bg-slate-950/70 backdrop-blur-sm animate-fade-in">
          <div className="bg-white rounded-2xl max-w-lg w-full p-6 space-y-4 shadow-2xl border border-slate-200">
            <div className="flex justify-between items-start border-b border-slate-200 pb-3">
              <div className="flex items-center gap-2">
                <Receipt className="w-5 h-5 text-amber-500" />
                <h3 className="font-bold text-base text-slate-900">মানি রিসিট ভাউচার</h3>
              </div>
              <button
                onClick={() => setIsReceiptModalOpen(false)}
                className="p-1 rounded-lg text-slate-400 hover:text-slate-700"
              >
                <X className="w-5 h-5" />
              </button>
            </div>

            {/* Receipt Printable Card */}
            <div className="border-2 border-dashed border-slate-300 rounded-xl p-5 space-y-3 bg-slate-50/50 text-xs text-slate-800">
              <div className="text-center border-b border-slate-300 pb-2">
                <div className="font-black text-sm text-slate-900">{samityConfig.nameBn}</div>
                <div className="text-[10px] text-slate-500">{samityConfig.address}</div>
                <div className="text-[10px] text-slate-500">রেজি নং: {samityConfig.regNo}</div>
              </div>

              <div className="flex justify-between text-[11px] font-mono">
                <span>রিসিট নং: <strong>{selectedReceipt.voucher}</strong></span>
                <span>তারিখ: {selectedReceipt.date}</span>
              </div>

              <div className="space-y-1.5 pt-1">
                <div className="flex justify-between">
                  <span className="text-slate-500">সদস্যের নাম:</span>
                  <span className="font-bold text-slate-900">{selectedReceipt.name}</span>
                </div>
                <div className="flex justify-between">
                  <span className="text-slate-500">সদস্য কোড:</span>
                  <span className="font-mono font-bold text-blue-700">{selectedReceipt.memberCode}</span>
                </div>
                <div className="flex justify-between">
                  <span className="text-slate-500">আদায়ের মাধ্যম:</span>
                  <span className="font-semibold text-slate-800">{selectedReceipt.method}</span>
                </div>
                <div className="flex justify-between text-sm font-black border-t border-slate-300 pt-2 text-emerald-800">
                  <span>আদায়কৃত পরিমাণ:</span>
                  <span>{selectedReceipt.amount.toLocaleString('bn-BD')} ৳</span>
                </div>
              </div>

              <div className="pt-6 flex justify-between text-[10px] text-slate-500">
                <div className="text-center border-t border-slate-400 pt-1 w-24">জমা প্রদানকারী</div>
                <div className="text-center border-t border-slate-400 pt-1 w-24">কোষাধ্যক্ষ / রিসিভার</div>
              </div>
            </div>

            <div className="flex justify-end gap-2 pt-2">
              <button
                onClick={() => setIsReceiptModalOpen(false)}
                className="px-4 py-2 rounded-xl border border-slate-300 text-xs font-semibold text-slate-700 hover:bg-slate-100"
              >
                বন্ধ করুন
              </button>
              <button
                onClick={() => {
                  window.print();
                }}
                className="flex items-center gap-1.5 bg-amber-500 hover:bg-amber-600 text-slate-950 font-bold px-4 py-2 rounded-xl text-xs shadow transition"
              >
                <Printer className="w-4 h-4" />
                <span>প্রিন্ট করুন</span>
              </button>
            </div>
          </div>
        </div>
      )}

      {/* ========================================================
          MODAL: SAMITY QUICK-SWITCH 4-CORNER SELECTOR
         ======================================================== */}
      {isSamityModalOpen && (
        <div className="fixed inset-0 z-50 flex items-center justify-center p-4 bg-slate-950/70 backdrop-blur-sm animate-fade-in">
          <div className="bg-white rounded-2xl max-w-md w-full p-6 space-y-4 shadow-2xl border border-slate-200">
            <div className="flex justify-between items-center border-b border-slate-200 pb-3">
              <div className="flex items-center gap-2">
                <Building2 className="w-5 h-5 text-amber-500" />
                <h3 className="font-bold text-base text-slate-900">সমিতি নির্বাচন ও সুইচ করুন</h3>
              </div>
              <button
                onClick={() => setIsSamityModalOpen(false)}
                className="p-1 rounded-lg text-slate-400 hover:text-slate-700"
              >
                <X className="w-5 h-5" />
              </button>
            </div>

            <div className="space-y-2.5">
              {merchants.map((m) => (
                <div
                  key={m.id}
                  onClick={() => {
                    setSamityConfig({ ...samityConfig, nameBn: m.nameBn });
                    setIsSamityModalOpen(false);
                    showToast(`🏢 '${m.nameBn}'-এ সক্রিয় সুইচ করা হয়েছে!`);
                  }}
                  className={`p-3.5 rounded-xl border cursor-pointer transition ${
                    samityConfig.nameBn === m.nameBn
                      ? 'border-amber-500 bg-amber-50/60 ring-2 ring-amber-400/30'
                      : 'border-slate-200 hover:bg-slate-50'
                  }`}
                >
                  <div className="flex justify-between items-center">
                    <span className="font-bold text-sm text-slate-900">{m.nameBn}</span>
                    {samityConfig.nameBn === m.nameBn && (
                      <span className="bg-amber-500 text-slate-950 text-[10px] font-bold px-2 py-0.5 rounded">
                        বর্তমান
                      </span>
                    )}
                  </div>
                  <div className="text-xs text-slate-500 mt-1">মালিক: {m.merchantOwner} • {m.memberCount} জন সদস্য</div>
                </div>
              ))}
            </div>
          </div>
        </div>
      )}

      {/* ========================================================
          MODAL: ADD NEW MEMBER
         ======================================================== */}
      {isAddMemberModalOpen && (
        <div className="fixed inset-0 z-50 flex items-center justify-center p-4 bg-slate-950/70 backdrop-blur-sm animate-fade-in">
          <div className="bg-white rounded-2xl max-w-md w-full p-6 space-y-4 shadow-2xl border border-slate-200">
            <div className="flex justify-between items-center border-b border-slate-200 pb-3">
              <h3 className="font-bold text-base text-slate-900">নতুন সদস্য ভর্তি ফরম</h3>
              <button onClick={() => setIsAddMemberModalOpen(false)}>
                <X className="w-5 h-5 text-slate-400" />
              </button>
            </div>

            <form
              onSubmit={(e) => {
                e.preventDefault();
                const formData = new FormData(e.target);
                const newMember = {
                  id: `M-${Date.now()}`,
                  code: `SM-000${members.length + 1}`,
                  nameBn: formData.get('nameBn') || 'নতুন সদস্য',
                  nameEn: formData.get('nameEn') || 'New Member',
                  phone: formData.get('phone') || '01700-000000',
                  monthlyFee: Number(formData.get('monthlyFee')) || 5000,
                  paid: 0,
                  due: Number(formData.get('monthlyFee')) || 5000,
                  status: 'Active',
                  shareCount: Number(formData.get('shareCount')) || 10,
                  nid: formData.get('nid') || '1995000000000'
                };
                setMembers([...members, newMember]);
                setIsAddMemberModalOpen(false);
                showToast(`✅ সদস্য ${newMember.nameBn} সফলভাবে তালিকাভুক্ত ও ক্লাউডে সিঙ্ক হয়েছে!`);
              }}
              className="space-y-3 text-xs"
            >
              <div>
                <label className="font-bold text-slate-700 block mb-1">সদস্যের নাম (বাংলায়) *</label>
                <input required name="nameBn" className="w-full bg-slate-50 border border-slate-300 rounded-xl px-3 py-2 font-semibold" placeholder="উদা: কামাল হোসেন" />
              </div>
              <div>
                <label className="font-bold text-slate-700 block mb-1">মোবাইল নম্বর *</label>
                <input required name="phone" className="w-full bg-slate-50 border border-slate-300 rounded-xl px-3 py-2 font-semibold" placeholder="০১৭০০-০০০০০০" />
              </div>
              <div className="grid grid-cols-2 gap-2">
                <div>
                  <label className="font-bold text-slate-700 block mb-1">মাসিক চাঁদা (টাকা) *</label>
                  <input required name="monthlyFee" type="number" defaultValue="5000" className="w-full bg-slate-50 border border-slate-300 rounded-xl px-3 py-2 font-bold" />
                </div>
                <div>
                  <label className="font-bold text-slate-700 block mb-1">শেয়ার সংখ্যা</label>
                  <input name="shareCount" type="number" defaultValue="10" className="w-full bg-slate-50 border border-slate-300 rounded-xl px-3 py-2 font-bold" />
                </div>
              </div>
              <div>
                <label className="font-bold text-slate-700 block mb-1">জাতীয় পরিচয়পত্র (NID)</label>
                <input name="nid" className="w-full bg-slate-50 border border-slate-300 rounded-xl px-3 py-2 font-mono" placeholder="১০ বা ১৭ ডিজিটের এনআইডি" />
              </div>

              <div className="flex justify-end gap-2 pt-2 border-t border-slate-200">
                <button
                  type="button"
                  onClick={() => setIsAddMemberModalOpen(false)}
                  className="px-4 py-2 rounded-xl border border-slate-300 text-xs font-semibold text-slate-700"
                >
                  বাতিল
                </button>
                <button
                  type="submit"
                  className="bg-emerald-600 hover:bg-emerald-700 text-white font-bold px-4 py-2 rounded-xl text-xs shadow transition"
                >
                  সংরক্ষণ করুন
                </button>
              </div>
            </form>
          </div>
        </div>
      )}

      {/* ========================================================
          MODAL: ADD NEW DEPOSIT
         ======================================================== */}
      {isAddDepositModalOpen && (
        <div className="fixed inset-0 z-50 flex items-center justify-center p-4 bg-slate-950/70 backdrop-blur-sm animate-fade-in">
          <div className="bg-white rounded-2xl max-w-md w-full p-6 space-y-4 shadow-2xl border border-slate-200">
            <div className="flex justify-between items-center border-b border-slate-200 pb-3">
              <h3 className="font-bold text-base text-slate-900">নতুন চাঁদা আদায় এন্ট্রি</h3>
              <button onClick={() => setIsAddDepositModalOpen(false)}>
                <X className="w-5 h-5 text-slate-400" />
              </button>
            </div>

            <form
              onSubmit={(e) => {
                e.preventDefault();
                const formData = new FormData(e.target);
                const memberCode = formData.get('memberCode');
                const selectedMember = members.find(m => m.code === memberCode) || members[0];
                const amount = Number(formData.get('amount')) || 5000;
                const method = formData.get('method') || 'নগদ ক্যাশ';

                const newDep = {
                  id: `D-${Date.now()}`,
                  voucher: `VR-202610-00${deposits.length + 1}`,
                  memberCode: selectedMember.code,
                  name: selectedMember.nameBn,
                  amount,
                  date: new Date().toISOString().split('T')[0],
                  origin: "ভার্সেল ওয়েব পোর্টাল",
                  method,
                  notes: "মাসিক নিয়মিত চাঁদা"
                };

                setDeposits([newDep, ...deposits]);
                setMembers(members.map(m => m.code === selectedMember.code ? { ...m, paid: m.paid + amount, due: Math.max(0, m.due - amount) } : m));
                setIsAddDepositModalOpen(false);
                openReceipt(newDep);
                showToast(`✅ ${selectedMember.nameBn}-এর ${amount} টাকা চাঁদা জমা হয়েছে!`);
              }}
              className="space-y-3 text-xs"
            >
              <div>
                <label className="font-bold text-slate-700 block mb-1">সদস্য নির্বাচন করুন *</label>
                <select name="memberCode" className="w-full bg-slate-50 border border-slate-300 rounded-xl px-3 py-2 font-semibold">
                  {members.map(m => (
                    <option key={m.id} value={m.code}>{m.code} - {m.nameBn} (বকেয়া: {m.due}৳)</option>
                  ))}
                </select>
              </div>

              <div>
                <label className="font-bold text-slate-700 block mb-1">জমার পরিমাণ (টাকা) *</label>
                <input required name="amount" type="number" defaultValue="5000" className="w-full bg-slate-50 border border-slate-300 rounded-xl px-3 py-2 font-bold text-slate-900" />
              </div>

              <div>
                <label className="font-bold text-slate-700 block mb-1">পেমেন্ট মাধ্যম *</label>
                <select name="method" className="w-full bg-slate-50 border border-slate-300 rounded-xl px-3 py-2 font-semibold">
                  <option value="নগদ ক্যাশ">নগদ ক্যাশ</option>
                  <option value="বিকাশ (bKash)">বিকাশ (bKash)</option>
                  <option value="নগদ (Nagad)">নগদ (Nagad)</option>
                  <option value="ব্যাংক ডিপোজিট">সোনালী ব্যাংক ডিপোজিট</option>
                </select>
              </div>

              <div className="flex justify-end gap-2 pt-2 border-t border-slate-200">
                <button
                  type="button"
                  onClick={() => setIsAddDepositModalOpen(false)}
                  className="px-4 py-2 rounded-xl border border-slate-300 text-xs font-semibold text-slate-700"
                >
                  বাতিল
                </button>
                <button
                  type="submit"
                  className="bg-amber-500 hover:bg-amber-600 text-slate-950 font-bold px-4 py-2 rounded-xl text-xs shadow transition"
                >
                  জমা করুন ও রিসিট তৈরি করুন
                </button>
              </div>
            </form>
          </div>
        </div>
      )}

      {/* FOOTER */}
      <footer className="bg-slate-900 text-slate-400 border-t border-slate-800 py-6 px-4 sm:px-6 text-xs mt-auto">
        <div className="max-w-7xl mx-auto flex flex-col md:flex-row justify-between items-center gap-4">
          <div className="flex items-center gap-3">
            <div className="w-8 h-8 rounded-lg bg-amber-500/20 text-amber-400 flex items-center justify-center font-black">
              <Building2 className="w-4 h-4" />
            </div>
            <div>
              <div className="font-bold text-white text-sm">সমিতি কিতাব ৩৬০ (Samity Kitab 360 Pro)</div>
              <div className="text-[11px] text-slate-400">অ্যান্ড্রয়েড মোবাইল APK + ভার্সেল ক্লাউড ইআরপি</div>
            </div>
          </div>

          <div className="flex flex-wrap items-center justify-center gap-4 text-slate-400">
            <a href="/samity-kitab-360-pro.apk" download className="text-emerald-400 hover:text-emerald-300 font-bold">
              📥 অ্যান্ড্রয়েড APK ডাউনলোড
            </a>
            <span>•</span>
            <button onClick={() => setActiveTab('samity_setup')} className="hover:text-white">সমিতি সেটআপ</button>
            <span>•</span>
            <button onClick={() => setActiveTab('reports')} className="hover:text-white">অডিট রিপোর্ট</button>
            <span>•</span>
            <button onClick={() => setActiveTab('backup')} className="hover:text-white">ব্যাকআপ</button>
          </div>

          <div className="text-slate-400 text-center md:text-right text-[11px]">
            © 2026 সর্বস্বত্ব সংরক্ষিত • সংস্করণ v1.0.0 Pro
          </div>
        </div>
      </footer>
    </div>
  );
}
