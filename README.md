# AR AI

A Persian Android chat app written in Java. It connects to the locally running ServLlama API and uses the downloaded LFM GGUF model.

## Requirements
- Android phone
- ServLlama installed
- The LFM model downloaded and selected in ServLlama
- ServLlama server started at `http://127.0.0.1:8080`

## Build the APK
The GitHub Actions workflow is at `.github/workflows/build-apk.yml`.

1. Open the repository's **Actions** tab.
2. Select **Build AR AI APK**.
3. If no run has started, use **Run workflow** on the `main` branch.
4. Open the successful run and find **Artifacts**.
5. Download **AR-AI-debug-apk**, extract it, and install `app-debug.apk`.

## How it works
The APK sends requests to `http://127.0.0.1:8080/v1/completions` on the same Android phone. Start the ServLlama server before chatting.

The APK does not contain the 1 GB model. ServLlama hosts the model locally, so the app requires ServLlama to remain installed and its server to be running.

This is a debug APK for testing, not a Play Store release.
