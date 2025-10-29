#!/bin/sh
# Universal Git hooks installer
# Works on Windows (Git Bash), Linux, macOS

set -e

echo "📦 Installing Git hooks..."

# Verify we're in a Git repository
if [ ! -d ".git" ]; then
  echo "❌ Error: Not a Git repository!"
  exit 1
fi

# Make hooks executable (no-op on Windows, required on Unix)
chmod +x .githooks/pre-commit .githooks/pre-push .githooks/commit-msg 2>/dev/null || true

# Configure Git to use .githooks directory
git config core.hooksPath .githooks

# Verify installation
if [ "$(git config core.hooksPath)" = ".githooks" ]; then
  echo "✅ Git hooks installed successfully!"
  echo ""
  echo "Hooks enabled:"
  echo "  • pre-commit  → Auto-format staged Java files"
  echo "  • pre-push    → Verify formatting before push"
  echo "  • commit-msg  → Enforce Conventional Commits"
  echo ""
  echo "💡 Skip hooks: git commit --no-verify"
else
  echo "❌ Failed to configure Git hooks"
  exit 1
fi

exit 0
