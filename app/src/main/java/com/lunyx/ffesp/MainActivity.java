package com.lunyx.ffesp;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class MainActivity extends Activity {
    private static final int REQUEST_OVERLAY = 410;

    @Override protected void onCreate(Bundle state) {
        super.onCreate(state);
        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(28, 32, 28, 24);

        TextView title = new TextView(this);
        title.setText("FF ESP Overlay");
        title.setTextSize(24);
        root.addView(title);

        TextView info = new TextView(this);
        info.setText("Android 6.0+  •  External overlay framework\n\nOverlay permission is required. Live player coordinates are not supplied by this app.");
        info.setTextSize(15);
        info.setPadding(0, 18, 0, 24);
        root.addView(info);

        Button start = new Button(this);
        start.setText("Cấp quyền và mở menu nổi");
        root.addView(start);
        start.setOnClickListener(v -> startOverlay());

        Button stop = new Button(this);
        stop.setText("Tắt menu nổi");
        root.addView(stop);
        stop.setOnClickListener(v -> stopService(new Intent(this, OverlayService.class)));

        setContentView(root);
    }

    private void startOverlay() {
        if (Build.VERSION.SDK_INT >= 23 && !Settings.canDrawOverlays(this)) {
            Intent permission = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    Uri.parse("package:" + getPackageName()));
            startActivityForResult(permission, REQUEST_OVERLAY);
            return;
        }
        if (Build.VERSION.SDK_INT >= 26) {
            startForegroundService(new Intent(this, OverlayService.class));
        } else {
            startService(new Intent(this, OverlayService.class));
        }
        finish();
    }

    @Override protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_OVERLAY && (Build.VERSION.SDK_INT < 23 || Settings.canDrawOverlays(this))) {
            startOverlay();
        }
    }
}
