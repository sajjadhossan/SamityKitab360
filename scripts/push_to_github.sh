#!/usr/bin/env bash
# Samity Kitab 360 - Push & Release Helper Script
# This script commits all changes, tags a release, and pushes to GitHub to trigger automatic APK Release publication.

set -e

echo "🚀 [Samity Kitab 360] Preparing to push and publish APK Release to GitHub..."

# Ensure git remote exists
if ! git remote | grep -q "origin"; then
  echo "⚠️ Remote 'origin' is not configured yet. Please configure your GitHub repository:"
  echo "   git remote add origin https://github.com/<your-username>/samity-kitab-360.git"
fi

VERSION="v1.0.0"
echo "📦 Version: $VERSION"

git add -A
git commit -m "Release: Samity Kitab 360 Pro $VERSION with Full Web Parity and 2x2 Layout" || true

echo "🏷️ Creating git release tag: $VERSION"
git tag -fa "$VERSION" -m "Samity Kitab 360 Pro $VERSION" || true

echo "⬆️ Pushing code and tags to GitHub..."
git push origin HEAD --tags || true

echo "✅ Done! GitHub Actions will automatically create a release and attach 'samity-kitab-360-pro.apk'!"
