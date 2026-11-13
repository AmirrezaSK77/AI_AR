package com.arai.assistant;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends Activity {
    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private LinearLayout messages;
    private ScrollView scroll;
    private EditText input;
    private Button send;
    private TextView status;
    private final StringBuilder conversation = new StringBuilder();

    private int dp(float value) {
        return (int) (value * getResources().getDisplayMetrics().density + 0.5f);
    }

    private GradientDrawable rounded(int color, int radius) {
        GradientDrawable shape = new GradientDrawable();
        shape.setColor(color);
        shape.setCornerRadius(dp(radius));
        return shape;
    }

    private TextView makeText(String value, int size, int color) {
        TextView view = new TextView(this);
        view.setText(value);
        view.setTextSize(size);
        view.setTextColor(color);
        view.setTextDirection(View.TEXT_DIRECTION_RTL);
        view.setGravity(Gravity.RIGHT);
        return view;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setStatusBarColor(Color.rgb(7, 12, 27));
        getWindow().setNavigationBarColor(Color.rgb(7, 12, 27));
        buildInterface();
        addMessage("سلام! من AR AI هستم. برای شروع، مطمئن شو ServLlama باز است و سرورش روشن است.", false);
        checkServer();
    }

    private void buildInterface() {
        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setBackgroundColor(Color.rgb(7, 12, 27));

        LinearLayout header = new LinearLayout(this);
        header.setOrientation(LinearLayout.VERTICAL);
        header.setPadding(dp(18), dp(16), dp(18), dp(12));
        header.setBackgroundColor(Color.rgb(12, 22, 46));

        TextView title = makeText("AR AI", 24, Color.WHITE);
        title.setTypeface(Typeface.DEFAULT_BOLD);
        title.setGravity(Gravity.CENTER);
        header.addView(title, new LinearLayout.LayoutParams(-1, -2));

        status = makeText("در حال بررسی اتصال...", 12, Color.LTGRAY);
        status.setGravity(Gravity.CENTER);
        LinearLayout.LayoutParams statusParams = new LinearLayout.LayoutParams(-1, -2);
        statusParams.topMargin = dp(5);
        header.addView(status, statusParams);
        root.addView(header);

        scroll = new ScrollView(this);
        scroll.setFillViewport(true);
        messages = new LinearLayout(this);
        messages.setOrientation(LinearLayout.VERTICAL);
        messages.setPadding(dp(14), dp(14), dp(14), dp(14));
        scroll.addView(messages);
        root.addView(scroll, new LinearLayout.LayoutParams(-1, 0, 1));

        LinearLayout composer = new LinearLayout(this);
        composer.setGravity(Gravity.CENTER_VERTICAL);
        composer.setPadding(dp(10), dp(10), dp(10), dp(10));
        composer.setBackgroundColor(Color.rgb(12, 22, 46));

        input = new EditText(this);
        input.setSingleLine(false);
        input.setMinLines(1);
        input.setMaxLines(4);
        input.setTextSize(16);
        input.setTextColor(Color.WHITE);
        input.setHintTextColor(Color.rgb(160, 170, 190));
        input.setHint("پیامت را بنویس...");
        input.setTextDirection(View.TEXT_DIRECTION_RTL);
        input.setGravity(Gravity.RIGHT | Gravity.CENTER_VERTICAL);
        input.setPadding(dp(14), dp(10), dp(14), dp(10));
        input.setBackground(rounded(Color.rgb(25, 38, 67), 18));
        composer.addView(input, new LinearLayout.LayoutParams(0, -2, 1));

        send = new Button(this);
        send.setText("ارسال");
        send.setTextColor(Color.WHITE);
        send.setAllCaps(false);
        send.setBackground(rounded(Color.rgb(35, 105, 220), 16));
        LinearLayout.LayoutParams sendParams = new LinearLayout.LayoutParams(-2, dp(48));
        sendParams.leftMargin = dp(8);
        composer.addView(send, sendParams);
        send.setOnClickListener(v -> sendMessage());
        root.addView(composer);

        setContentView(root);
    }

    private void addMessage(String value, boolean user) {
        TextView bubble = makeText(value, 16, Color.WHITE);
        bubble.setPadding(dp(14), dp(11), dp(14), dp(11));
        bubble.setBackground(rounded(user ? Color.rgb(31, 86, 170) : Color.rgb(25, 37, 62), 18));
        int width = (int) (getResources().getDisplayMetrics().widthPixels * (user ? 0.82f : 0.90f));
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(width, -2);
        params.gravity = user ? Gravity.RIGHT : Gravity.LEFT;
        params.bottomMargin = dp(10);
        messages.addView(bubble, params);
        scroll.post(() -> scroll.fullScroll(View.FOCUS_DOWN));
    }

    private void checkServer() {
        executor.execute(() -> {
            HttpURLConnection connection = null;
            try {
                connection = (HttpURLConnection) new URL("http://127.0.0.1:8080/v1/models").openConnection();
                connection.setConnectTimeout(5000);
                connection.setReadTimeout(5000);
                int code = connection.getResponseCode();
                runOnUiThread(() -> status.setText(
                        code == 200 ? "مغز LFM متصل است" : "پاسخ سرور: " + code));
            } catch (Exception e) {
                runOnUiThread(() -> status.setText("ServLlama را باز کن و سرور را روشن کن"));
            } finally {
                if (connection != null) connection.disconnect();
            }
        });
    }

    private void sendMessage() {
        String message = input.getText().toString().trim();
        if (message.isEmpty()) return;

        input.setText("");
        addMessage(message, true);
        send.setEnabled(false);
        send.setText("...");

        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) imm.hideSoftInputFromWindow(input.getWindowToken(), 0);

        executor.execute(() -> {
            HttpURLConnection connection = null;
            try {
                conversation.append("User: ").append(message).append("\nAssistant:");

                JSONObject body = new JSONObject();
                body.put("model", "LFM2.5-2.6B-QAD-Q4_0");
                body.put("prompt", conversation.toString());
                body.put("max_tokens", 220);
                body.put("temperature", 0.35);
                body.put("stream", false);

                connection = (HttpURLConnection) new URL("http://127.0.0.1:8080/v1/completions").openConnection();
                connection.setRequestMethod("POST");
                connection.setConnectTimeout(15000);
                connection.setReadTimeout(180000);
                connection.setDoOutput(true);
                connection.setRequestProperty("Content-Type", "application/json; charset=utf-8");

                byte[] payload = body.toString().getBytes(StandardCharsets.UTF_8);
                try (OutputStream output = connection.getOutputStream()) {
                    output.write(payload);
                }

                int code = connection.getResponseCode();
                InputStream stream = code >= 200 && code < 300
                        ? connection.getInputStream() : connection.getErrorStream();

                StringBuilder responseText = new StringBuilder();
                try (BufferedReader reader = new BufferedReader(
                        new InputStreamReader(stream, StandardCharsets.UTF_8))) {
                    String line;
                    while ((line = reader.readLine()) != null) responseText.append(line);
                }

                if (code < 200 || code >= 300) {
                    throw new Exception("HTTP " + code + ": " + responseText);
                }

                JSONObject result = new JSONObject(responseText.toString());
                JSONArray choices = result.getJSONArray("choices");
                String answer = choices.getJSONObject(0).optString("text", "").trim();
                if (answer.isEmpty()) answer = "پاسخی دریافت نشد.";
                conversation.append(" ").append(answer).append("\n");

                String finalAnswer = answer;
                runOnUiThread(() -> addMessage(finalAnswer, false));
            } catch (Exception e) {
                String error = "خطا در ارتباط با مغز AR AI: " + e.getMessage()
                        + "\nبررسی کن ServLlama باز و سرورش روشن باشد.";
                runOnUiThread(() -> addMessage(error, false));
            } finally {
                if (connection != null) connection.disconnect();
                runOnUiThread(() -> {
                    send.setEnabled(true);
                    send.setText("ارسال");
                });
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        executor.shutdownNow();
    }
}
