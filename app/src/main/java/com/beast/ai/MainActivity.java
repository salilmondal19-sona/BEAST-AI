package com.beast.ai;

import android.app.Activity;
import android.os.Bundle;
import android.content.Intent;
import android.provider.Settings;
import android.net.Uri;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.graphics.drawable.GradientDrawable;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.LinearLayout;
import android.widget.EditText;

public class MainActivity extends Activity {

    WindowManager wm;
    TextView bubble;
    LinearLayout chat;
    WindowManager.LayoutParams bubbleParams;

    float touchX, touchY;
    int startX, startY;
    boolean dragged;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        wm = (WindowManager) getSystemService(WINDOW_SERVICE);
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (Settings.canDrawOverlays(this)) {
            showBubble();
        } else {
            Intent i = new Intent(
                Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                Uri.parse("package:" + getPackageName())
            );
            startActivity(i);
        }
    }

    private int dp(int value) {
        return (int) (value * getResources()
            .getDisplayMetrics().density);
    }

    private void showBubble() {

        if (bubble != null) return;

        bubble = new TextView(this);
        bubble.setText("AI");
        bubble.setTextSize(20);
        bubble.setTextColor(Color.WHITE);
        bubble.setGravity(Gravity.CENTER);

        GradientDrawable bg = new GradientDrawable();
        bg.setColor(Color.rgb(20, 20, 20));
        bg.setCornerRadius(dp(50));
        bubble.setBackground(bg);

        bubbleParams = new WindowManager.LayoutParams(
            dp(60), dp(60),
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
            PixelFormat.TRANSLUCENT
        );

        bubbleParams.gravity = Gravity.TOP | Gravity.RIGHT;
        bubbleParams.x = dp(20);
        bubbleParams.y = dp(250);

        bubble.setOnTouchListener((v, event) -> {
            switch (event.getAction()) {

                case MotionEvent.ACTION_DOWN:
                    touchX = event.getRawX();
                    touchY = event.getRawY();
                    startX = bubbleParams.x;
                    startY = bubbleParams.y;
                    dragged = false;
                    return true;

                case MotionEvent.ACTION_MOVE:
                    float dx = event.getRawX() - touchX;
                    float dy = event.getRawY() - touchY;

                    if (Math.abs(dx) > dp(5) ||
                        Math.abs(dy) > dp(5)) {
                        dragged = true;
                    }

                    if (dragged) {
                        bubbleParams.x = startX - (int) dx;
                        bubbleParams.y = startY + (int) dy;
                        wm.updateViewLayout(bubble, bubbleParams);
                    }
                    return true;

                case MotionEvent.ACTION_UP:
                    if (!dragged) {
                        showChat();
                    }
                    return true;
            }
            return false;
        });

        wm.addView(bubble, bubbleParams);
    }

    private void showChat() {

        if (chat != null) return;

        chat = new LinearLayout(this);
        chat.setOrientation(LinearLayout.VERTICAL);
        chat.setPadding(dp(16), dp(16), dp(16), dp(16));
        chat.setBackgroundColor(Color.rgb(25, 25, 25));

        TextView title = new TextView(this);
        title.setText("🐉 BEAST AI");
        title.setTextColor(Color.WHITE);
        title.setTextSize(22);

        EditText input = new EditText(this);
        input.setHint("Ask BEAST AI...");
        input.setTextColor(Color.WHITE);
        input.setHintTextColor(Color.GRAY);

        TextView close = new TextView(this);
        close.setText("CLOSE ✕");
        close.setTextColor(Color.RED);
        close.setTextSize(16);
        close.setPadding(0, dp(15), 0, dp(10));

        close.setOnClickListener(v -> {
            if (chat != null) {
                wm.removeView(chat);
                chat = null;
            }
        });

        chat.addView(title);
        chat.addView(input);
        chat.addView(close);

        int width = getResources()
            .getDisplayMetrics().widthPixels - dp(40);

        WindowManager.LayoutParams p =
            new WindowManager.LayoutParams(
                width,
                dp(300),
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
                WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL,
                PixelFormat.TRANSLUCENT
            );

        p.gravity = Gravity.CENTER;
        wm.addView(chat, p);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
