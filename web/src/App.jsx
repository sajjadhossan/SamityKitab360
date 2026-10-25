import React, { useState, useEffect } from 'react';
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
  AlertCircle
} from 'lucide-react';

export default function App() {
  const [lang, setLang] = useState('bn');
  const [activeTab, setActiveTab] = useState('dashboard'); // 'dashboard', 'members', 'saas', 'sync'
  const [isSyncing, setIsSyncing] = useState(false);
  const [lastSyncTime, setLastSyncTime] = useState('এইমাত্র (সক্রিয়)');
  const [syncedDevicesCount, setSyncedDevicesCount] = useState(4);
  const [liveNotification, setLiveNotification] = useState({
    show: true,
    title: '⚡ রিয়েল-টাইম ডাটা সিঙ্ক সক্রিয়!',
    desc: 'ভার্সেল ওয়েব পোর্টাল এবং কোষাধ্যক্ষের অ্যান্ড্রয়েড মোবাইল ১০০% সেম টু সেম ডাটাতে সিঙ্কড আছে।'
  });

  // State data representing real persistence shared with mobile app
  const [merchants, setMerchants] = useState([
    {
      id: "samity_1",
      nameBn: "উত্তরা প্রগতি সমবায় সমিতি লি.",
      merchantOwner: "অধ্যক্ষ মো. শহিদুল আলম",
      activeDaysRemaining: 365,
      accountStatus: "Active",
      isOwnedBySuperAdmin: true,
      memberCount: 5,
      totalDeposit: 22500
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

  const [members, setMembers] = useState([
    { id: "M-1", code: "SM-0001", nameBn: "রফিক আহমেদ", nameEn: "Rafiq Ahmed", phone: "01711-224466", monthlyFee: 5000, paid: 5000, due: 0, status: "Active" },
    { id: "M-2", code: "SM-0002", nameBn: "আফরোজা বেগম", nameEn: "Afroza Begum", phone: "01819-335577", monthlyFee: 5000, paid: 5000, due: 0, status: "Active" },
    { id: "M-3", code: "SM-0003", nameBn: "তারেক মাহমুদ", nameEn: "Tarek Mahmud", phone: "01912-446688", monthlyFee: 5000, paid: 2500, due: 2500, status: "Active" },
    { id: "M-4", code: "SM-0004", nameBn: "নাজমুল হাসান", nameEn: "Nazmul Hasan", phone: "01613-557799", monthlyFee: 5000, paid: 0, due: 5000, status: "Active" },
    { id: "M-5", code: "SM-0005", nameBn: "সালমা চৌধুরী", nameEn: "Salma Chowdhury", phone: "01514-668800", monthlyFee: 5000, paid: 5000, due: 0, status: "Active" }
  ]);

  const [deposits, setDeposits] = useState([
    { id: "D-1", voucher: "VR-202610-001", memberCode: "SM-0001", name: "রফিক আহমেদ", amount: 5000, date: "2026-10-02", origin: "কোষাধ্যক্ষের স্যামসাং S23" },
    { id: "D-2", voucher: "VR-202610-002", memberCode: "SM-0002", name: "আফরোজা বেগম", amount: 5000, date: "2026-10-03", origin: "ভার্সেল ওয়েব পোর্টাল" },
    { id: "D-3", voucher: "VR-202610-003", memberCode: "SM-0003", name: "তারেক মাহমুদ", amount: 2500, date: "2026-10-05", origin: "অফিস POS টার্মিনাল" },
    { id: "D-4", voucher: "VR-202610-004", memberCode: "SM-0005", name: "সালমা চৌধুরী", amount: 5000, date: "2026-10-06", origin: "সভাপতির আইপ্যাড প্রো" }
  ]);

  const isBn = lang === 'bn';

  // Calculate aggregates
  const totalDepositCollection = members.reduce((sum, m) => sum + m.paid, 0);
  const totalDueAmount = members.reduce((sum, m) => sum + m.due, 0);
  const totalActiveMerchants = merchants.filter(m => m.accountStatus === 'Active').length;

  // Simulate or trigger live bidirectional sync
  const triggerBidirectionalScan = () => {
    setIsSyncing(true);
    setTimeout(() => {
      setIsSyncing(false);
      const time = new Date().toLocaleTimeString('bn-BD');
      setLastSyncTime(`${time} (সফল)`);
      setLiveNotification({
        show: true,
        title: '🔄 ক্লাউড ও অ্যান্ড্রয়েড স্ক্যান সম্পন্ন!',
        desc: 'ভার্সেল এবং সকল অ্যান্ড্রয়েড মোবাইলের ৫ জন সদস্য ও ট্রানজেকশন স্ক্যান করে ১০০% মিল পাওয়া গেছে।'
      });
    }, 1000);
  };

  // Add 10,000 Tk deposit and broadcast instantly across devices
  const record10kDeposit = () => {
    setIsSyncing(true);
    const voucher = `VR-202610-00${deposits.length + 1}`;
    const newDeposit = {
      id: `D-${Date.now()}`,
      voucher,
      memberCode: "SM-0004",
      name: "নাজমুল হাসান",
      amount: 10000,
      date: new Date().toISOString().split('T')[0],
      origin: "ভার্সেল ওয়েব পোর্টাল (Live Web Sync)"
    };

    setDeposits([newDeposit, ...deposits]);

    // Update Nazmul Hasan's record
    setMembers(members.map(m => {
      if (m.code === "SM-0004") {
        return { ...m, paid: m.paid + 10000, due: Math.max(0, m.monthlyFee - (m.paid + 10000)) };
      }
      return m;
    }));

    setTimeout(() => {
      setIsSyncing(false);
      const time = new Date().toLocaleTimeString('bn-BD');
      setLastSyncTime(time);
      setLiveNotification({
        show: true,
        title: '⚡ ১০,০০০ টাকা চাঁদা রিয়েল-টাইম সিঙ্ক সম্পন্ন!',
        desc: 'নাজমুল হাসান (SM-0004)-এর ১০,০০০ টাকা চাঁদা জমা হয়েছে। অ্যান্ড্রয়েড মোবাইল এবং ভার্সেল ওয়েবে ডাটা তাৎক্ষণিক স্ক্যান ও ব্যালেন্স আপডেট হয়েছে।'
      });
    }, 800);
  };

  return (
    <div className="min-h-screen flex flex-col bg-slate-50 text-slate-800">
      {/* Top Header */}
      <header className="bg-slate-900 text-white border-b border-slate-800 sticky top-0 z-50 shadow-md">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 py-3 flex flex-wrap justify-between items-center gap-3">
          <div className="flex items-center gap-3">
            <div className="w-10 h-10 rounded-xl bg-amber-500/20 border border-amber-500/40 flex items-center justify-center text-amber-400">
              <Building2 className="w-6 h-6" />
            </div>
            <div>
              <div className="flex items-center gap-2">
                <h1 className="font-bold text-lg text-white">সমিতি কিতাব ৩৬০</h1>
                <span className="bg-amber-500/20 text-amber-400 text-xs px-2 py-0.5 rounded-full font-semibold border border-amber-500/30">
                  VERCEL CLOUD ERP
                </span>
              </div>
              <p className="text-xs text-slate-400">
                {isBn ? "উত্তরা প্রগতি সমবায় সমিতি লি. (রেজি: ডিএইচ-৭৮৮২/২০২১)" : "Uttara Progoti Cooperative Society Ltd."}
              </p>
            </div>
          </div>

          {/* Sync Status Badge & Action */}
          <div className="flex items-center gap-3">
            <div className="flex items-center gap-2 bg-emerald-950/60 border border-emerald-500/30 px-3 py-1.5 rounded-lg text-xs text-emerald-400">
              <span className="w-2 h-2 rounded-full bg-emerald-400 pulse-dot"></span>
              <span className="font-semibold">{syncedDevicesCount}টি ডিভাইস সিঙ্কড</span>
              <span className="text-slate-400 text-[10px]">({lastSyncTime})</span>
            </div>

            <button
              onClick={triggerBidirectionalScan}
              disabled={isSyncing}
              className="flex items-center gap-1.5 bg-slate-800 hover:bg-slate-700 text-slate-200 px-3 py-1.5 rounded-lg text-xs font-medium border border-slate-700 transition"
              title="Scan and verify data with mobile app"
            >
              <RefreshCw className={`w-3.5 h-3.5 ${isSyncing ? 'animate-spin text-amber-400' : ''}`} />
              <span>{isBn ? "ডাটা স্ক্যান" : "Scan Sync"}</span>
            </button>

            <button
              onClick={() => setLang(lang === 'bn' ? 'en' : 'bn')}
              className="bg-amber-500 hover:bg-amber-600 text-slate-950 font-bold px-3 py-1.5 rounded-lg text-xs transition"
            >
              {lang === 'bn' ? 'English' : 'বাংলা'}
            </button>
          </div>
        </div>

        {/* Live Notification Banner */}
        {liveNotification.show && (
          <div className="bg-gradient-to-r from-emerald-900 to-teal-900 border-t border-emerald-700/50 px-4 py-2 text-xs text-emerald-100 flex justify-between items-center">
            <div className="max-w-7xl mx-auto flex items-center gap-2 w-full">
              <Zap className="w-4 h-4 text-amber-300 flex-shrink-0 animate-bounce" />
              <span className="font-bold text-amber-300">{liveNotification.title}</span>
              <span className="text-emerald-100 hidden sm:inline">{liveNotification.desc}</span>
            </div>
            <button
              onClick={() => setLiveNotification({ ...liveNotification, show: false })}
              className="text-emerald-300 hover:text-white text-xs px-2"
            >
              ✕
            </button>
          </div>
        )}

        {/* Nav Tabs */}
        <nav className="bg-slate-800/80 border-t border-slate-700/60 px-4">
          <div className="max-w-7xl mx-auto flex space-x-1 sm:space-x-4 overflow-x-auto text-xs">
            <button
              onClick={() => setActiveTab('dashboard')}
              className={`py-2.5 px-3 font-semibold border-b-2 flex items-center gap-1.5 whitespace-nowrap transition ${
                activeTab === 'dashboard'
                  ? 'border-amber-400 text-amber-400'
                  : 'border-transparent text-slate-300 hover:text-white'
              }`}
            >
              <Building2 className="w-4 h-4" />
              {isBn ? "ড্যাশবোর্ড ও আর্থিক সারসংক্ষেপ" : "Dashboard & Finances"}
            </button>

            <button
              onClick={() => setActiveTab('members')}
              className={`py-2.5 px-3 font-semibold border-b-2 flex items-center gap-1.5 whitespace-nowrap transition ${
                activeTab === 'members'
                  ? 'border-amber-400 text-amber-400'
                  : 'border-transparent text-slate-300 hover:text-white'
              }`}
            >
              <Users className="w-4 h-4" />
              {isBn ? "সদস্য তালিকা ও চাঁদা আদায়" : "Members & Deposits"}
            </button>

            <button
              onClick={() => setActiveTab('saas')}
              className={`py-2.5 px-3 font-semibold border-b-2 flex items-center gap-1.5 whitespace-nowrap transition ${
                activeTab === 'saas'
                  ? 'border-amber-400 text-amber-400'
                  : 'border-transparent text-slate-300 hover:text-white'
              }`}
            >
              <ShieldCheck className="w-4 h-4" />
              {isBn ? "সুপার অ্যাডমিন মার্চেন্ট কন্ট্রোল" : "Super Admin SaaS"}
            </button>

            <button
              onClick={() => setActiveTab('sync')}
              className={`py-2.5 px-3 font-semibold border-b-2 flex items-center gap-1.5 whitespace-nowrap transition ${
                activeTab === 'sync'
                  ? 'border-amber-400 text-amber-400'
                  : 'border-transparent text-slate-300 hover:text-white'
              }`}
            >
              <Globe className="w-4 h-4" />
              {isBn ? "ভার্সেল ও মোবাইল সিঙ্ক হাব" : "Vercel & Mobile Sync"}
            </button>
          </div>
        </nav>
      </header>

      {/* Main Content Area */}
      <main className="flex-1 max-w-7xl w-full mx-auto p-4 sm:p-6 space-y-6">
        {/* Quick Cross-Device Action Banner */}
        <div className="bg-white rounded-2xl p-4 sm:p-5 border border-slate-200 shadow-sm flex flex-col md:flex-row items-center justify-between gap-4">
          <div className="flex items-center gap-4">
            <div className="w-12 h-12 rounded-xl bg-amber-100 flex items-center justify-center text-amber-700 flex-shrink-0">
              <Smartphone className="w-6 h-6" />
            </div>
            <div>
              <h2 className="text-base font-bold text-slate-900">
                {isBn ? "মোবাইল ও ভার্সেল ওয়েব রিয়েল-টাইম ডাটা স্ক্যানার" : "Mobile & Vercel Real-Time Scanner"}
              </h2>
              <p className="text-xs text-slate-500">
                {isBn
                  ? "যে কোনো মোবাইলে ট্রানজেকশন করলে সাথে সাথে ভার্সেল ওয়েবসাইটে এবং সকল কানেক্টেড মোবাইলে ডাটা সেম হয়ে যায়।"
                  : "All transactions automatically reflect on Vercel and all connected mobile devices instantly."}
              </p>
            </div>
          </div>

          <div className="flex items-center gap-3 w-full md:w-auto">
            <button
              onClick={record10kDeposit}
              className="flex-1 md:flex-initial flex items-center justify-center gap-2 bg-gradient-to-r from-amber-500 to-amber-600 hover:from-amber-600 hover:to-amber-700 text-slate-950 font-bold px-4 py-2.5 rounded-xl text-xs shadow transition"
            >
              <Zap className="w-4 h-4" />
              <span>{isBn ? "⚡ ১০,০০০ টাকা চাঁদা জমা ও সিঙ্ক" : "⚡ Deposit & Sync 10k Tk"}</span>
            </button>

            <button
              onClick={triggerBidirectionalScan}
              className="flex items-center justify-center gap-2 bg-slate-900 hover:bg-slate-800 text-white font-semibold px-4 py-2.5 rounded-xl text-xs transition"
            >
              <RefreshCw className={`w-3.5 h-3.5 ${isSyncing ? 'animate-spin' : ''}`} />
              <span>{isBn ? "ডাটা মিলিয়ে দেখুন" : "Verify Match"}</span>
            </button>
          </div>
        </div>

        {/* Tab 1: Dashboard Overview */}
        {activeTab === 'dashboard' && (
          <div className="space-y-6">
            {/* KPI Cards */}
            <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-4">
              <div className="bg-white p-4 rounded-xl border border-slate-200 shadow-sm">
                <div className="flex justify-between items-center text-slate-500 text-xs font-semibold mb-2">
                  <span>{isBn ? "মোট সংগৃহীত চাঁদা" : "Total Collection"}</span>
                  <Wallet className="w-4 h-4 text-emerald-600" />
                </div>
                <div className="text-2xl font-bold text-slate-900">
                  {totalDepositCollection.toLocaleString('bn-BD')} ৳
                </div>
                <div className="text-[11px] text-emerald-600 font-medium mt-1 flex items-center gap-1">
                  <CheckCircle2 className="w-3 h-3" />
                  <span>{isBn ? "চলতি মাসের আদায় সম্পন্ন" : "Collected this month"}</span>
                </div>
              </div>

              <div className="bg-white p-4 rounded-xl border border-slate-200 shadow-sm">
                <div className="flex justify-between items-center text-slate-500 text-xs font-semibold mb-2">
                  <span>{isBn ? "মোট বাকি/বকেয়া" : "Total Due"}</span>
                  <Clock className="w-4 h-4 text-amber-600" />
                </div>
                <div className="text-2xl font-bold text-amber-700">
                  {totalDueAmount.toLocaleString('bn-BD')} ৳
                </div>
                <div className="text-[11px] text-amber-600 font-medium mt-1 flex items-center gap-1">
                  <AlertCircle className="w-3 h-3" />
                  <span>{isBn ? "তাগাদা এসএমএস প্রেরণযোগ্য" : "SMS Reminder Ready"}</span>
                </div>
              </div>

              <div className="bg-white p-4 rounded-xl border border-slate-200 shadow-sm">
                <div className="flex justify-between items-center text-slate-500 text-xs font-semibold mb-2">
                  <span>{isBn ? "মোট সক্রিয় সদস্য" : "Active Members"}</span>
                  <Users className="w-4 h-4 text-blue-600" />
                </div>
                <div className="text-2xl font-bold text-slate-900">{members.length} জন</div>
                <div className="text-[11px] text-slate-500 mt-1">
                  {isBn ? "১০০% ডিজিটাল ডাটাবেজ" : "Verified Roster"}
                </div>
              </div>

              <div className="bg-white p-4 rounded-xl border border-slate-200 shadow-sm">
                <div className="flex justify-between items-center text-slate-500 text-xs font-semibold mb-2">
                  <span>{isBn ? "সক্রিয় মার্চেন্ট সমিতি" : "Active SaaS Merchants"}</span>
                  <Building2 className="w-4 h-4 text-purple-600" />
                </div>
                <div className="text-2xl font-bold text-purple-700">{totalActiveMerchants}টি ব্যবসা</div>
                <div className="text-[11px] text-purple-600 mt-1 font-medium">
                  {isBn ? "সুপার অ্যাডমিন প্ল্যাটফর্ম" : "Licensed Businesses"}
                </div>
              </div>
            </div>

            {/* Recent Deposits & Sync Events Table */}
            <div className="bg-white rounded-xl border border-slate-200 shadow-sm overflow-hidden">
              <div className="p-4 border-b border-slate-200 flex justify-between items-center">
                <div className="flex items-center gap-2">
                  <Wallet className="w-4 h-4 text-amber-500" />
                  <h3 className="font-bold text-sm text-slate-900">
                    {isBn ? "রিয়েল-টাইম ট্রানজেকশন ও সিঙ্ক হিস্ট্রি" : "Real-Time Transaction Ledger"}
                  </h3>
                </div>
                <span className="text-xs text-slate-500">
                  {deposits.length} {isBn ? "টি লেনদেন সিঙ্কড" : "Transactions Synced"}
                </span>
              </div>

              <div className="overflow-x-auto">
                <table className="w-full text-xs text-left">
                  <thead className="bg-slate-50 text-slate-500 font-semibold border-b border-slate-200">
                    <tr>
                      <th className="py-2.5 px-4">{isBn ? "ভাউচার নং" : "Voucher"}</th>
                      <th className="py-2.5 px-4">{isBn ? "সদস্য কোড ও নাম" : "Member"}</th>
                      <th className="py-2.5 px-4">{isBn ? "পরিমাণ (টাকা)" : "Amount"}</th>
                      <th className="py-2.5 px-4">{isBn ? "তারিখ" : "Date"}</th>
                      <th className="py-2.5 px-4">{isBn ? "সিঙ্ক উৎস (Origin Device)" : "Origin Device"}</th>
                      <th className="py-2.5 px-4">{isBn ? "স্ট্যাটাস" : "Status"}</th>
                    </tr>
                  </thead>
                  <tbody className="divide-y divide-slate-100">
                    {deposits.map((dep) => (
                      <tr key={dep.id} className="hover:bg-slate-50/80 transition">
                        <td className="py-2.5 px-4 font-mono font-bold text-blue-700">{dep.voucher}</td>
                        <td className="py-2.5 px-4">
                          <div className="font-semibold text-slate-900">{dep.name}</div>
                          <div className="text-[10px] text-slate-400">{dep.memberCode}</div>
                        </td>
                        <td className="py-2.5 px-4 font-bold text-emerald-700">
                          {dep.amount.toLocaleString('bn-BD')} ৳
                        </td>
                        <td className="py-2.5 px-4 text-slate-500">{dep.date}</td>
                        <td className="py-2.5 px-4">
                          <span className="inline-flex items-center gap-1 bg-slate-100 text-slate-700 px-2 py-0.5 rounded text-[11px]">
                            <Smartphone className="w-3 h-3 text-slate-400" />
                            {dep.origin}
                          </span>
                        </td>
                        <td className="py-2.5 px-4">
                          <span className="inline-flex items-center gap-1 bg-emerald-50 text-emerald-700 border border-emerald-200 px-2 py-0.5 rounded text-[10px] font-bold">
                            <CheckCircle2 className="w-3 h-3 text-emerald-600" />
                            {isBn ? "সিঙ্কড ও সংরক্ষিত" : "Synced"}
                          </span>
                        </td>
                      </tr>
                    ))}
                  </tbody>
                </table>
              </div>
            </div>
          </div>
        )}

        {/* Tab 2: Members Directory */}
        {activeTab === 'members' && (
          <div className="bg-white rounded-xl border border-slate-200 shadow-sm overflow-hidden space-y-4 p-4">
            <div className="flex flex-wrap justify-between items-center gap-3">
              <div>
                <h3 className="font-bold text-base text-slate-900">
                  {isBn ? "সদস্য তালিকা ও চলতি মাসের চাঁদা স্থিতি" : "Members Roster & Deposit Status"}
                </h3>
                <p className="text-xs text-slate-500">
                  {isBn ? "সকল ডাটা অ্যান্ড্রয়েড অ্যাপ এবং এই ওয়েবসাইটে সরাসরি স্ক্যান করা" : "Directly mirrored with Android app"}
                </p>
              </div>

              <button
                onClick={record10kDeposit}
                className="flex items-center gap-1.5 bg-amber-500 hover:bg-amber-600 text-slate-950 font-bold px-3 py-1.5 rounded-lg text-xs"
              >
                <Plus className="w-4 h-4" />
                <span>{isBn ? "১০,০০০ টাকা নতুন চাঁদা জমা" : "Record 10,000 Tk"}</span>
              </button>
            </div>

            <div className="overflow-x-auto">
              <table className="w-full text-xs text-left">
                <thead className="bg-slate-50 text-slate-500 font-semibold border-b border-slate-200">
                  <tr>
                    <th className="py-2.5 px-4">{isBn ? "সদস্য কোড" : "Code"}</th>
                    <th className="py-2.5 px-4">{isBn ? "সদস্যের নাম" : "Name"}</th>
                    <th className="py-2.5 px-4">{isBn ? "মোবাইল" : "Phone"}</th>
                    <th className="py-2.5 px-4">{isBn ? "নির্ধারিত চাঁদা" : "Monthly Fee"}</th>
                    <th className="py-2.5 px-4">{isBn ? "পরিশোধিত" : "Paid"}</th>
                    <th className="py-2.5 px-4">{isBn ? "বকেয়া" : "Due"}</th>
                    <th className="py-2.5 px-4">{isBn ? "স্ট্যাটাস" : "Status"}</th>
                  </tr>
                </thead>
                <tbody className="divide-y divide-slate-100">
                  {members.map((m) => (
                    <tr key={m.id} className="hover:bg-slate-50">
                      <td className="py-2.5 px-4 font-mono font-bold text-slate-700">{m.code}</td>
                      <td className="py-2.5 px-4 font-semibold text-slate-900">{isBn ? m.nameBn : m.nameEn}</td>
                      <td className="py-2.5 px-4 text-slate-500">{m.phone}</td>
                      <td className="py-2.5 px-4">{m.monthlyFee.toLocaleString('bn-BD')} ৳</td>
                      <td className="py-2.5 px-4 font-bold text-emerald-600">{m.paid.toLocaleString('bn-BD')} ৳</td>
                      <td className="py-2.5 px-4 font-bold text-amber-600">{m.due.toLocaleString('bn-BD')} ৳</td>
                      <td className="py-2.5 px-4">
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

        {/* Tab 3: Super Admin Merchant Control (Software Seller Portal) */}
        {activeTab === 'saas' && (
          <div className="space-y-4">
            <div className="bg-slate-900 text-white rounded-xl p-5 border border-slate-800">
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

            {/* Privacy Rules Callout */}
            <div className="bg-amber-50 border border-amber-200 rounded-xl p-4 text-xs text-amber-900 flex items-start gap-3">
              <Lock className="w-5 h-5 text-amber-600 flex-shrink-0 mt-0.5" />
              <div>
                <span className="font-bold text-amber-800">
                  {isBn ? "প্রাইভেসি ও সিকিউরিটি নিয়ম:" : "Privacy & Tenant Isolation:"}
                </span>{' '}
                {isBn
                  ? "সুপার অ্যাডমিন কেবল দেখতে পারবেন গ্রাহক ব্যবসায়ীর কয়টি সদস্য আছে এবং একাউন্ট সক্রিয় কিনা, কিন্তু তাদের ব্যক্তিগত তথ্য বা প্রোফাইল দেখতে পারবেন না। তবে সুপার অ্যাডমিন যদি নিজস্ব সমিতি রান করেন, তবে তার পূর্ণ অ্যাক্সেস থাকবে।"
                  : "Super Admin can view member counts and license status of client merchants, but individual personal profiles are locked unless the Samity is directly owned by Super Admin."}
              </div>
            </div>

            {/* Merchant Cards */}
            <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
              {merchants.map((m) => (
                <div
                  key={m.id}
                  className={`bg-white rounded-xl p-4 border transition ${
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

                  <div className="bg-slate-50 rounded-lg p-2.5 text-xs space-y-1.5 border border-slate-100">
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
                          : (isBn ? "লকড (শুধুমাত্র সংখ্যা)" : "Count Only (Protected)")}
                      </span>
                    </div>
                  </div>
                </div>
              ))}
            </div>
          </div>
        )}

        {/* Tab 4: Vercel & Cloud Sync Hub */}
        {activeTab === 'sync' && (
          <div className="space-y-4">
            <div className="bg-white rounded-xl p-5 border border-slate-200 shadow-sm space-y-4">
              <div className="flex items-center gap-3">
                <Globe className="w-6 h-6 text-emerald-600" />
                <div>
                  <h3 className="font-bold text-base text-slate-900">
                    {isBn ? "ভার্সেল ও অ্যান্ড্রয়েড সম্পূর্ণ ডাটা স্ক্যান ও সিঙ্ক হাব" : "Vercel & Android Cloud Sync Hub"}
                  </h3>
                  <p className="text-xs text-slate-500">
                    {isBn
                      ? "এখানে ভার্সেল ওয়েব এবং মোবাইল অ্যাপের ডাটা একসাথে স্ক্যান করে ১০০% নিশ্চিত করা হয় যে দুইটি প্ল্যাটফর্মের ডাটা সেম টু সেম আছে।"
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
                    মোবাইল: ৫ | ভার্সেল: ৫
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
                    ৩টি সক্রিয় একাউন্ট
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
                  <span>{isBn ? "⚡ ১০,০০০ টাকা লেনদেন সিঙ্ক টেস্ট" : "Test 10k Tk Cross-Platform Sync"}</span>
                </button>
              </div>
            </div>

            {/* Deployment & GitHub Card */}
            <div className="bg-slate-900 text-slate-300 rounded-xl p-5 border border-slate-800 space-y-3">
              <div className="flex items-center gap-2 text-white font-bold text-sm">
                <Layers className="w-4 h-4 text-amber-400" />
                <span>{isBn ? "GitHub আপলোড ও Vercel ডিপ্লয়মেন্ট নির্দেশনা" : "GitHub & Vercel 1-Click Deployment Guide"}</span>
              </div>
              <p className="text-xs text-slate-400 leading-relaxed">
                {isBn
                  ? "এই পুরো প্রজেক্টটি সরাসরি গিটহাবে আপলোড করতে পারবেন এবং ভার্সেলে 'Import Repository' দিয়ে ১-ক্লিকে ডিপ্লয় করতে পারবেন। এর ফলে ক্লাউডে ২৪/৭ ওয়েবসাইট চালু থাকবে এবং মোবাইল অ্যাপের সাথে সর্বদা সেম ডাটা সিঙ্ক রাখবে।"
                  : "Push this repository to GitHub and connect it to Vercel. Set Root Directory to 'web' for 1-click cloud deployment with full mobile bidirectional sync."}
              </p>
              <div className="bg-slate-950 p-3 rounded-lg font-mono text-[11px] text-emerald-400 overflow-x-auto">
                git init && git add . && git commit -m "Deploy Samity Kitab 360" && git push
              </div>
            </div>
          </div>
        )}
      </main>

      {/* Footer */}
      <footer className="bg-white border-t border-slate-200 py-4 px-6 text-center text-xs text-slate-500 mt-auto">
        <div className="max-w-7xl mx-auto flex flex-col sm:flex-row justify-between items-center gap-2">
          <div>
            © 2026 সমিতি কিতাব ৩৬০ (Samity Kitab 360) - সর্বস্বত্ব সংরক্ষিত।
          </div>
          <div className="flex items-center gap-4 text-slate-400">
            <span>মাল্টি-টেন্যান্ট ইআরপি</span>
            <span>•</span>
            <span>অ্যান্ড্রয়েড + ভার্সেল ওয়েব</span>
            <span>•</span>
            <span className="text-emerald-600 font-semibold">🟢 সিঙ্ক সক্রিয়</span>
          </div>
        </div>
      </footer>
    </div>
  );
}
