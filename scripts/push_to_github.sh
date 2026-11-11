#!/usr/bin/env bash
# ==============================================================================
# Samity Kitab 360 - Push & Release Script for GitHub & Vercel
# Pushes Android App (Kotlin), APK binaries (apk/*.apk), and Web Portal (web/)
# ==============================================================================

set -e

REPO_URL_DEFAULT="https://github.com/sajjadhossan/SamityKitab360.git"
GITHUB_USER_DEFAULT="sajjadhossan"
GITHUB_EMAIL_DEFAULT="hossansajjad1994@gmail.com"
VERSION="v1.0.0"

echo "=========================================================="
echo "🚀 [Samity Kitab 360] Preparing to Push Code, Web & APK"
echo "=========================================================="

# 1. Configure Git Safe Directory (Resolves Git CVE-2022-24765 dubious ownership error)
git config --global --add safe.directory "*" || true

# 2. Ensure Git repository is initialized
if [ ! -d ".git" ]; then
  echo "📦 Initializing Git repository on branch 'main'..."
  git init -b main
fi

# Ensure branch is main
CURRENT_BRANCH=$(git branch --show-current || echo "main")
if [ -z "$CURRENT_BRANCH" ]; then
  git checkout -b main || true
fi

# 3. Configure Git committer identity if not already set
if [ -z "$(git config user.name)" ]; then
  git config user.name "$GITHUB_USER_DEFAULT"
fi
if [ -z "$(git config user.email)" ]; then
  git config user.email "$GITHUB_EMAIL_DEFAULT"
fi

# 4. Copy latest compiled APK into apk/ directory for GitHub Releases
echo "📱 Packaging APK binaries..."
mkdir -p apk
if [ -f "app/build/outputs/apk/debug/app-debug.apk" ]; then
  cp -f app/build/outputs/apk/debug/app-debug.apk apk/samity-kitab-360-pro.apk
  cp -f app/build/outputs/apk/debug/app-debug.apk apk/app-debug.apk
  echo "✅ APK ready: apk/samity-kitab-360-pro.apk ($(ls -lh apk/samity-kitab-360-pro.apk | awk '{print $5}'))"
fi

# 5. Check Remote origin configuration
TOKEN="${1:-$GITHUB_TOKEN}"

if ! git remote | grep -q "origin"; then
  if [ -n "$TOKEN" ]; then
    AUTH_URL="https://${TOKEN}@github.com/sajjadhossan/SamityKitab360.git"
    git remote add origin "$AUTH_URL"
  else
    git remote add origin "$REPO_URL_DEFAULT"
  fi
  echo "🔗 Remote 'origin' added: $REPO_URL_DEFAULT"
else
  if [ -n "$TOKEN" ]; then
    AUTH_URL="https://${TOKEN}@github.com/sajjadhossan/SamityKitab360.git"
    git remote set-url origin "$AUTH_URL"
    echo "🔑 Updated remote with GitHub Token."
  fi
fi

# 6. Stage all project files (Android source, Web portal, APK, workflows)
echo "📁 Staging files (including web/ and apk/)..."
git add -A

# 7. Commit changes
echo "💾 Creating commit..."
git commit -m "Update: Samity Kitab 360 (Android App + Web Portal + APK Release)" || echo "ℹ️ Nothing new to commit."

# 8. Tag Release for GitHub Actions
echo "🏷️ Setting release tag: $VERSION"
git tag -fa "$VERSION" -m "Samity Kitab 360 Release $VERSION" || true

# 9. Push to GitHub
echo "⬆️ Pushing to GitHub (origin main)..."
if git push -u origin main --tags 2>/dev/null || git push -u origin HEAD --tags 2>/dev/null; then
  echo "=========================================================="
  echo "✅ SUCCESS! All Android, Web and APK files pushed to GitHub!"
  echo "🌐 Vercel will automatically build the web portal from GitHub."
  echo "📱 GitHub Actions will publish the APK Release automatically."
  echo "=========================================================="
else
  echo ""
  echo "⚠️ [Authentication Required / অ্যাক্সেস অনুমতি প্রয়োজন]"
  echo "GitHub আর সাধারণ পাসওয়ার্ড অনুমোদন করে না। পুশ করার জন্য একটি Personal Access Token (PAT) প্রয়োজন।"
  echo ""
  echo "সহজ সমাধানের নিয়ম:"
  echo "1. GitHub এ যান -> Settings -> Developer Settings -> Personal Access Tokens -> Tokens (classic)"
  echo "2. 'repo' পারমিশন দিয়ে একটি টোকেন তৈরি করুন।"
  echo "3. এরপর এই কমান্ডটি চালান:"
  echo "   bash scripts/push_to_github.sh <আপনার_টোকেন>"
  echo "   অথবা:"
  echo "   git push https://<আপনার_টোকেন>@github.com/sajjadhossan/SamityKitab360.git main --tags"
  echo "=========================================================="
fi
