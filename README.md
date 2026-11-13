# AR AI

A Persian Android chat app and responsive web chat interface.

## Web version

The web interface is in `index.html`. It is a static front end and does **not** run the AI model by itself.

To publish it for free with GitHub Pages:

1. Open **Settings → Pages** in this repository.
2. Under **Build and deployment**, set **Source** to **Deploy from a branch**.
3. Choose branch **main** and folder **/(root)**, then press **Save**.
4. Wait for GitHub Pages to publish the site. The URL will be `https://amirrezask77.github.io/AI_AR/`.

GitHub Pages sites are public. Do not put passwords, API keys, or private information in the repository or web page.

## Connecting the model

The web page can send requests to an OpenAI-compatible `/v1/completions` endpoint configured in **تنظیم اتصال**. For a public HTTPS website, the model server must also be reachable through HTTPS and allow browser CORS requests.

A phone-local address such as `127.0.0.1` is only reachable from that same phone. A Tailscale address is only reachable by devices signed into the same private tailnet; it is not a public endpoint. Do not expose an unauthenticated model API to the public internet.

## Android APK

The Android app is written in Java and currently connects to ServLlama at `http://127.0.0.1:8080` on the same phone.

Build workflow: `.github/workflows/build-apk.yml`.

1. Open the repository's **Actions** tab.
2. Select **Build AR AI APK**.
3. Run the workflow on `main` if needed.
4. Download the `AR-AI-debug-apk` artifact from a successful run.

The APK does not include the model file. Start ServLlama before chatting.
