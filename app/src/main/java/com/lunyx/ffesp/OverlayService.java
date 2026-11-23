package com.lunyx.ffesp;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.IBinder;
import android.provider.Settings;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

public class OverlayService extends Service {
    private WindowManager windowManager;
    private View panel;
    private WindowManager.LayoutParams params;
    private static final String CHANNEL = "ffesp_overlay";

    @Override public void onCreate() {
        super.onCreate();
        createNotification();
        if (Build.VERSION.SDK_INT >= 23 && !Settings.canDrawOverlays(this)) {
            stopSelf();
            return;
        }
        showPanel();
    }

    private void createNotification() {
        if (Build.VERSION.SDK_INT >= 26) {
            NotificationManager nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            nm.createNotificationChannel(new NotificationChannel(CHANNEL, "FF ESP Overlay", NotificationManager.IMPORTANCE_LOW));
            Notification n = new Notification.Builder(this, CHANNEL)
                    .setContentTitle("FF ESP Overlay")
                    .setContentText("Menu nổi đang hoạt động")
                    .setSmallIcon(android.R.drawable.ic_menu_view)
                    .build();
            startForeground(1, n);
        }
    }

    private void showPanel() {
        windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        LinearLayout box = new LinearLayout(this);
        box.setOrientation(LinearLayout.VERTICAL);
        box.setPadding(16, 12, 16, 12);
        box.setBackgroundColor(Color.argb(235, 24, 27, 34));

        TextView header = label("FF ESP  •  MENU", 17, Color.WHITE);
        header.setPadding(4, 4, 4, 12);
        box.addView(header);

        addToggle(box, "Player ESP");
        addToggle(box, "Box ESP");
        addToggle(box, "Skeleton ESP");
        addToggle(box, "Health ESP");
        addToggle(box, "Distance ESP");

        TextView note = label("Chưa có nguồn tọa độ mục tiêu", 11, Color.LTGRAY);
        note.setPadding(4, 12, 4, 2);
        box.addView(note);

        TextView close = label("ĐÓNG MENU", 13, Color.WHITE);
        close.setPadding(4, 14, 4, 4);
        box.addView(close);
        close.setOnClickListener(v -> stopSelf());

        params = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                Build.VERSION.SDK_INT >= 26 ? WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
                        : WindowManager.LayoutParams.TYPE_PHONE,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                        | WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN,
                PixelFormat.TRANSLUCENT);
        params.gravity = Gravity.TOP | Gravity.LEFT;
        params.x = 24; params.y = 120;

        header.setOnTouchListener(new View.OnTouchListener() {
            int startX, startY; float downX, downY;
            @Override public boolean onTouch(View v, MotionEvent e) {
                if (e.getAction() == MotionEvent.ACTION_DOWN) {
                    startX = params.x; startY = params.y;
                    downX = e.getRawX(); downY = e.getRawY(); return true;
                }
                if (e.getAction() == MotionEvent.ACTION_MOVE) {
                    params.x = startX + (int)(e.getRawX() - downX);
                    params.y = startY + (int)(e.getRawY() - downY);
                    windowManager.updateViewLayout(panel, params); return true;
                }
                return e.getAction() == MotionEvent.ACTION_UP;
            }
        });
        panel = box;
        windowManager.addView(panel, params);
    }

    private void addToggle(LinearLayout parent, String name) {
        TextView row = label("○  " + name + "   [OFF]", 14, Color.WHITE);
        row.setPadding(4, 9, 4, 9);
        row.setOnClickListener(v -> {
            Object state = row.getTag();
            boolean enabled = !(state instanceof Boolean && (Boolean) state);
            row.setTag(enabled);
            row.setText((enabled ? "●  " : "○  ") + name + "   [" + (enabled ? "ON" : "OFF") + "]");
            row.setTextColor(enabled ? Color.rgb(100, 220, 150) : Color.WHITE);
        });
        parent.addView(row);
    }

    private TextView label(String text, int size, int color) {
        TextView view = new TextView(this);
        view.setText(text); view.setTextSize(size); view.setTextColor(color);
        return view;
    }

    @Override public void onDestroy() {
        if (windowManager != null && panel != null) {
            try { windowManager.removeView(panel); } catch (Exception ignored) {}
        }
        super.onDestroy();
    }
    @Override public IBinder onBind(Intent intent) { return null; }
}
