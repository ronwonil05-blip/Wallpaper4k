package kr.co.galaxywallpaper;

import android.app.Activity;
import android.app.WallpaperManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MainActivity extends Activity {
    private final String[] devices = {
        "Galaxy S26 Ultra", "Galaxy S26+", "Galaxy S26",
        "Galaxy S25 Ultra", "Galaxy S25+", "Galaxy S25",
        "Galaxy Z Fold7", "Galaxy Z Flip7",
        "Galaxy A56", "Galaxy A36", "직접 선택 / 기타 갤럭시"
    };
    private final String[] wallpapers = {
        "aurora_4k.jpg", "nebula_4k.jpg", "waves_4k.jpg"
    };
    private SharedPreferences prefs;
    private int selected = 0;
    private BroadcastReceiver screenOffReceiver;

    @Override public void onCreate(Bundle state) {
        super.onCreate(state);
        prefs = getSharedPreferences("settings", MODE_PRIVATE);
        selected = prefs.getInt("selected_wallpaper", 0);
        buildUi();
        registerScreenOffReceiver();
    }

    private void buildUi() {
        ScrollView scroll = new ScrollView(this);
        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(dp(20), dp(22), dp(20), dp(24));
        root.setBackgroundColor(Color.rgb(10, 13, 24));
        scroll.addView(root);

        TextView title = text("GALAXY WALLPAPER", 25, Color.WHITE, true);
        root.addView(title);
        TextView subtitle = text("기종에 맞춘 초고화질 배경화면", 14, Color.rgb(180, 190, 214), false);
        subtitle.setPadding(0, dp(5), 0, dp(20));
        root.addView(subtitle);

        root.addView(text("휴대폰 기종", 16, Color.WHITE, true));
        Spinner deviceSpinner = new Spinner(this);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
            android.R.layout.simple_spinner_dropdown_item, devices);
        deviceSpinner.setAdapter(adapter);
        deviceSpinner.setSelection(prefs.getInt("device", 0));
        deviceSpinner.setPadding(dp(8), dp(8), dp(8), dp(8));
        root.addView(deviceSpinner, new LinearLayout.LayoutParams(-1, dp(52)));

        TextView info = text("선택한 기종에 맞춰 미리보기 비율을 적용합니다. 원본 이미지는 세로형 4K(2160 × 3840)입니다.", 12,
            Color.rgb(180, 190, 214), false);
        info.setPadding(0, dp(7), 0, dp(16));
        root.addView(info);

        root.addView(text("배경화면 갤러리", 18, Color.WHITE, true));
        GridLayout grid = new GridLayout(this);
        grid.setColumnCount(2);
        String[] names = {"오로라", "네뷸라", "실버 웨이브"};
        for (int i = 0; i < wallpapers.length; i++) {
            final int index = i;
            LinearLayout tile = new LinearLayout(this);
            tile.setOrientation(LinearLayout.VERTICAL);
            tile.setPadding(dp(6), dp(6), dp(6), dp(12));
            TextView preview = new TextView(this);
            preview.setText(i == selected ? "✓ 선택됨" : "미리보기");
            preview.setTextColor(Color.WHITE);
            preview.setGravity(Gravity.CENTER);
            preview.setTextSize(14);
            int[] colors = i == 0
                ? new int[]{Color.rgb(18, 38, 80), Color.rgb(30, 180, 170), Color.rgb(10, 12, 30)}
                : i == 1
                ? new int[]{Color.rgb(9, 8, 35), Color.rgb(113, 42, 150), Color.rgb(4, 5, 18)}
                : new int[]{Color.rgb(10, 12, 20), Color.rgb(130, 143, 160), Color.rgb(20, 22, 32)};
            GradientDrawable bg = new GradientDrawable(GradientDrawable.Orientation.TL_BR, colors);
            bg.setCornerRadius(dp(18));
            preview.setBackground(bg);
            tile.addView(preview, new LinearLayout.LayoutParams(-1, dp(190)));
            TextView label = text(names[i] + "  ·  4K", 14, Color.WHITE, true);
            label.setPadding(dp(4), dp(9), 0, 0);
            tile.addView(label);
            tile.setOnClickListener(v -> {
                selected = index;
                prefs.edit().putInt("selected_wallpaper", selected).apply();
                Toast.makeText(this, names[index] + " 선택됨", Toast.LENGTH_SHORT).show();
                buildUi();
            });
            grid.addView(tile, new GridLayout.LayoutParams());
        }
        root.addView(grid);

        Button apply = new Button(this);
        apply.setText("선택한 배경화면 적용");
        apply.setAllCaps(false);
        root.addView(apply, new LinearLayout.LayoutParams(-1, dp(52)));
        apply.setOnClickListener(v -> {
            prefs.edit().putInt("device", deviceSpinner.getSelectedItemPosition()).apply();
            applyWallpaper(selected);
        });

        CheckBox auto = new CheckBox(this);
        auto.setText("화면이 꺼질 때 랜덤으로 배경화면 변경");
        auto.setTextColor(Color.WHITE);
        auto.setButtonTintList(android.content.res.ColorStateList.valueOf(Color.rgb(120, 160, 255)));
        auto.setChecked(prefs.getBoolean("auto", false));
        auto.setPadding(0, dp(14), 0, dp(4));
        root.addView(auto);
        auto.setOnCheckedChangeListener((button, checked) -> {
            prefs.edit().putBoolean("auto", checked).apply();
            Toast.makeText(this, checked
                ? "자동 변경 설정됨 (앱 프로세스가 실행 중일 때 화면 꺼짐을 감지합니다)"
                : "자동 변경 꺼짐", Toast.LENGTH_LONG).show();
        });

        TextView note = text("안내: Android/One UI의 백그라운드 제한 때문에 화면 꺼짐 순간의 자동 변경은 기기 설정에 따라 제한될 수 있습니다. 이 초기 버전은 앱 프로세스가 살아 있을 때 화면 꺼짐을 감지합니다.", 12,
            Color.rgb(180, 190, 214), false);
        note.setPadding(0, dp(8), 0, dp(12));
        root.addView(note);
        setContentView(scroll);
    }

    private void registerScreenOffReceiver() {
        screenOffReceiver = new BroadcastReceiver() {
            @Override public void onReceive(Context context, Intent intent) {
                if (Intent.ACTION_SCREEN_OFF.equals(intent.getAction())
                    && prefs.getBoolean("auto", false)) {
                    int next = new Random().nextInt(wallpapers.length);
                    selected = next;
                    prefs.edit().putInt("selected_wallpaper", next).apply();
                    applyWallpaper(next);
                }
            }
        };
        registerReceiver(screenOffReceiver, new IntentFilter(Intent.ACTION_SCREEN_OFF));
    }

    private void applyWallpaper(int index) {
        try (InputStream in = getAssets().open("wallpapers/" + wallpapers[index])) {
            Bitmap bitmap = BitmapFactory.decodeStream(in);
            WallpaperManager manager = WallpaperManager.getInstance(this);
            manager.setBitmap(bitmap);
            Toast.makeText(this, "배경화면을 적용했습니다.", Toast.LENGTH_SHORT).show();
            bitmap.recycle();
        } catch (IOException | RuntimeException e) {
            Toast.makeText(this, "적용할 수 없습니다. 기기 권한 또는 이미지 파일을 확인해 주세요.", Toast.LENGTH_LONG).show();
        }
    }

    private TextView text(String value, int size, int color, boolean bold) {
        TextView t = new TextView(this);
        t.setText(value);
        t.setTextSize(size);
        t.setTextColor(color);
        if (bold) t.setTypeface(null, android.graphics.Typeface.BOLD);
        return t;
    }
    private int dp(int value) { return (int)(value * getResources().getDisplayMetrics().density + 0.5f); }

    @Override protected void onDestroy() {
        super.onDestroy();
        if (screenOffReceiver != null) {
            try { unregisterReceiver(screenOffReceiver); } catch (Exception ignored) {}
        }
    }
}
