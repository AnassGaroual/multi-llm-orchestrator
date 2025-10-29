# 🎨 Code Formatting Guide

## Overview

This project enforces **Google Java Format** via Spotless for consistent code style.

## Local Development

### Automatic Formatting

Git hooks handle formatting automatically:

- **pre-commit**: Formats staged Java files
- **pre-push**: Verifies formatting before push
- **commit-msg**: Enforces Conventional Commits

### Manual Commands
```bash
# Apply formatting to all files
./gradlew spotlessApply

# Check formatting (CI-style)
./gradlew spotlessCheck

# Skip hooks (not recommended)
git commit --no-verify
git push --no-verify
```

## IDE Setup

### IntelliJ IDEA

1. Install **google-java-format** plugin
2. Settings → google-java-format Settings → **Enable**
3. `Ctrl+Alt+L` now matches Spotless formatting exactly

### VS Code

1. Install **Language Support for Java** extension
2. Install **google-java-format** formatter
3. Configure in `settings.json`:
```json
{
  "java.format.settings.url": "https://raw.githubusercontent.com/google/styleguide/gh-pages/intellij-java-google-style.xml",
  "editor.formatOnSave": true
}
```

## CI/CD Integration

### Workflows

1. **Code Quality** - Runs formatting check + security scans
2. **Auto-Fix** - Applies formatting automatically when labeled

### PR Process
```bash
# 1. Create feature branch
git checkout -b feat/new-feature

# 2. Develop (hooks auto-format on commit)
# ... code ...
git commit -m "feat: implement new feature"

# 3. Push (hooks verify before push)
git push origin feat/new-feature

# 4. CI validates formatting automatically
```

### Trigger Auto-Fix

**Option 1:** Add label `spotless-fix` to PR

**Option 2:** Include `[spotless-fix]` in commit message
```bash
git commit --amend -m "feat: implement feature [spotless-fix]"
git push --force-with-lease
```

## Troubleshooting

### Formatting Conflicts
```bash
./gradlew spotlessApply
git add .
git commit --amend --no-edit
```

### Hooks Not Working
```bash
./install-hooks.sh
```

### Windows Git Bash Issues

Ensure you're using Git Bash (not PowerShell/CMD) or WSL for hook execution.

## Best Practices

- **Never commit unformatted code** - Hooks enforce this
- **Use `spotlessApply` before large commits** - Prevents hook delays
- **Don't disable hooks** - Unless absolutely necessary
- **Keep IDE formatter in sync** - Install google-java-format plugin
