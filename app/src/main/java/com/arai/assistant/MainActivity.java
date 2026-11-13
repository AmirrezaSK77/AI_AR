package com.arai.assistant;

import android.app.Activity;
import android.os.Bundle;
import android.graphics.Color;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

public class MainActivity extends Activity {
    private int dp(float value) {
        return (int) (value * getResources().getDisplayMetrics().density + 0.5f);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(dp(22), dp(28), dp(22), dp(20));
        root.setBackgroundColor(Color.rgb(8, 12, 24));

        TextView title = new TextView(this);
        title.setText("AR AI");
        title.setTextColor(Color.rgb(80, 170, 255));
        title.setTextSize(32);
        title.setGravity(Gravity.CENTER);
        title.setPadding(0, dp(24), 0, dp(8));
        root.addView(title, new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        TextView subtitle = new TextView(this);
        subtitle.setText("Your AI assistant is getting ready.");
        subtitle.setTextColor(Color.LTGRAY);
        subtitle.setTextSize(15);
        subtitle.setGravity(Gravity.CENTER);
        subtitle.setPadding(0, 0, 0, dp(28));
        root.addView(subtitle);

        TextView status = new TextView(this);
        status.setText("مرحله اول آماده است؛ اتصال هوش مصنوعی در مرحله بعد اضافه می‌شود.");
        status.setTextColor(Color.WHITE);
        status.setTextSize(16);
        status.setGravity(Gravity.CENTER);
        status.setPadding(dp(16), dp(18), dp(16), dp(18));
        status.setBackgroundColor(Color.rgb(20, 30, 50));
        root.addView(status);

        EditText message = new EditText(this);
        message.setHint("پیامت را اینجا بنویس...");
        message.setHintTextColor(Color.GRAY);
        message.setTextColor(Color.WHITE);
        message.setSingleLine(false);
        message.setMinLines(2);
        message.setBackgroundTintList(android.content.res.ColorStateList.valueOf(
                Color.rgb(70, 140, 220)));
        LinearLayout.LayoutParams inputParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        inputParams.topMargin = dp(24);
        root.addView(message, inputParams);

        Button send = new Button(this);
        send.setText("ارسال");
        send.setOnClickListener(v -> {
            String text = message.getText().toString().trim();
            if (!text.isEmpty()) {
                status.setText("پیام دریافت شد: " + text
                        + "\n\nدر مرحله بعد، پاسخ واقعی مدل هوش مصنوعی را وصل می‌کنیم.");
                message.setText("");
            }
        });
        root.addView(send);

        setContentView(root);
    }
}
