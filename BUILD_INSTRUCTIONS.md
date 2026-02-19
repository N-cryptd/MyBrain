# Building MyBrain APK with GLM Integration

## Fork Information

This is a fork of [mhss1/MyBrain](https://github.com/mhss1/MyBrain) maintained by [N-cryptd](https://github.com/N-cryptd). For detailed information about this fork, see [FORK_NOTES.md](FORK_NOTES.md).

## Overview

The MyBrain app has been successfully updated with GLM Coding Plan integration. However, due to architecture limitations (we're running on ARM64), the Android SDK's aapt2 binary (which is x86_64 only) cannot run natively on this system.

## Solution: GitHub Actions CI/CD

I've created a GitHub Actions workflow (`.github/workflows/build-apk.yml`) that will automatically build the APK on GitHub's x86_64 runners.

### How to Build the APK

#### Option 1: Push to GitHub (Recommended)

1. **Commit your changes:**
   ```bash
   git add .
   git commit -m "Add GLM Coding Plan integration"
   ```

2. **Push to GitHub:**
   ```bash
   git push origin master
   ```

3. **GitHub Actions will automatically:**
   - Build the Debug APK
   - Build the Release APK (unsigned)
   - Upload both APKs as artifacts

4. **Download the APK:**
   - Go to your GitHub repository (https://github.com/N-cryptd/MyBrain)
   - Click on "Actions" tab
   - Select the latest workflow run
   - Download the APK artifacts

#### Option 2: Build Locally on x86_64 Machine

If you have access to an x86_64 (Intel/AMD) machine:

1. **Install dependencies:**
   ```bash
   # Ubuntu/Debian
   sudo apt-get update
   sudo apt-get install -y openjdk-17-jdk
   
   # macOS
   brew install openjdk@17
   ```

2. **Setup Android SDK:**
   ```bash
   # Download command line tools
   mkdir -p ~/android-sdk/cmdline-tools
   cd ~/android-sdk/cmdline-tools
   wget https://dl.google.com/android/repository/commandlinetools-linux-11076708_latest.zip
   unzip commandlinetools-linux-11076708_latest.zip
   mv cmdline-tools latest
   
   # Install SDK components
   export ANDROID_HOME=~/android-sdk
   export PATH=$PATH:$ANDROID_HOME/cmdline-tools/latest/bin
   yes | sdkmanager --licenses
   sdkmanager "platform-tools" "platforms;android-34" "build-tools;34.0.0"
   ```

3. **Build the APK:**
   ```bash
   cd /path/to/MyBrain
   ./gradlew :app:assembleDebug
   ```

4. **Find the APK:**
   - Debug APK: `app/build/outputs/apk/debug/app-debug.apk`
   - Release APK: `app/build/outputs/apk/release/app-release-unsigned.apk`

## Changes Made for GLM Integration

### 1. Core Preferences (PrefsConstants.kt)
- Added GLM preference keys:
  - `GLM_MODEL_KEY`
  - `GLM_KEY`
  - `GLM_URL_KEY`
  - `GLM_USE_URL_KEY`

### 2. AI Provider Model (AiProvider.kt)
- Added new `GLM` provider with ID 7
- Default model: `glm-4.7`
- API endpoint: `https://api.z.ai/api/coding/paas/v4`
- Supports custom URL configuration

### 3. AI Repository (AiRepositoryImpl.kt)
- Added GLM client initialization using OpenAI-compatible API
- Uses `OpenAILLMClient` with GLM's base URL

### 4. LLM Utilities (LLMUtil.kt)
- Mapped GLM provider to `LLMProvider.OpenAI` for capability detection

### 5. UI Components
- **strings.xml**: Added `glm` string resource
- **AiProviderSection.kt**: Added GLM to provider selection dropdown
- **ic_glm.xml**: Created GLM vector icon

## Using the GLM Provider

1. Open the MyBrain app
2. Go to Settings → Integrations → AI
3. Select "GLM" from the provider dropdown
4. Enter your GLM API key (get it from https://z.ai/apikeys)
5. (Optional) Configure custom URL if needed
6. Select model (default: glm-4.7, also supports glm-4.5, glm-4.6)
7. Enable "AI tools" if you want the AI to interact with the app

## API Configuration

The GLM provider uses the following default configuration:
- **Base URL**: `https://api.z.ai/api/coding/paas/v4`
- **Model**: `glm-4.7` (configurable)
- **API Key**: Required (get from Z.ai)
- **Custom URL Support**: Yes (for alternative endpoints)

## Troubleshooting

### Build Issues
- If you encounter "aapt2: cannot execute binary file", you're on an ARM64 machine and need to use GitHub Actions or an x86_64 machine
- Make sure you have Java 17 installed
- Ensure ANDROID_HOME environment variable is set correctly

### Runtime Issues
- If the AI doesn't respond, check your API key
- Verify you have an active GLM Coding Plan subscription
- Check network connectivity to `api.z.ai`
- Enable "AI tools" in settings for full functionality

## Files Modified

1. `core/preferences/src/main/java/com/mhss/app/preferences/PrefsConstants.kt`
2. `core/preferences/src/main/java/com/mhss/app/preferences/domain/model/AiProvider.kt`
3. `ai/data/src/main/java/com/mhss/app/data/repository/AiRepositoryImpl.kt`
4. `ai/data/src/main/java/com/mhss/app/data/LLMUtil.kt`
5. `core/ui/src/main/res/values/strings.xml`
6. `settings/presentation/src/main/java/com/mhss/app/presentation/integrations/AiProviderSection.kt`
7. `core/ui/src/main/res/drawable/ic_glm.xml` (new file)
8. `.github/workflows/build-apk.yml` (new file)

## Fork-Specific Notes

### Current State
- Based on last successfully compiled version (commit 56b4251)
- All repository links updated to point to N-cryptd/MyBrain
- Development roadmap available at [GitHub Project](https://github.com/users/N-cryptd/projects/2)

### Known Issues
- Habits module temporarily removed due to compilation errors (planned for restoration in Tier 1)
- VoiceInputUtil removed due to compilation issues

### Development Branches
- `master`: Stable production builds
- `dev`: Development and feature work

## License

This project is licensed under GPL-3.0. The GLM integration maintains compatibility with the existing license.
