package net.hssco.club;

import android.content.ClipData;
import android.content.ClipDescription;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.os.Bundle;
import android.view.DragEvent;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.MarginLayoutParams;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

public class TemplateBuilderActivity extends AppCompatActivity {

    private LinearLayout templateButtonsContainer;
    private LinearLayout templatePreviewHost;
    private LinearLayout currentTemplateLayout;
    private TextView txtSelectedTemplate;
    private TextView txtTemplateDescription;
    private TextView txtDropStatus;

    private final List<TemplateDefinition> templates = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_template_builder);

        templateButtonsContainer = findViewById(R.id.layoutTemplateButtons);
        templatePreviewHost = findViewById(R.id.templatePreviewHost);
        txtSelectedTemplate = findViewById(R.id.txtSelectedTemplate);
        txtTemplateDescription = findViewById(R.id.txtTemplateDescription);
        txtDropStatus = findViewById(R.id.txtDropStatus);

        View imgBack = findViewById(R.id.imgBack);
        if (imgBack != null) {
            imgBack.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });
        }

        seedTemplates();
        buildTemplateButtons();
        bindPaletteItems();

        if (!templates.isEmpty()) {
            renderTemplate(templates.get(0));
        }
    }

    private void seedTemplates() {
        templates.clear();
        templates.add(new TemplateDefinition(
                "classic",
                "قالب کلاسیک",
                "چیدمان ستونی برای رسیدها یا معرفی محصولات.",
                LinearLayout.VERTICAL,
                Color.parseColor("#F5F5F5"),
                new String[]{"عنوان اصلی", "زیرعنوان", "جای تصویر", "متن توضیحات"}
        ));
        templates.add(new TemplateDefinition(
                "split",
                "دو ستونه",
                "چیدمان افقی برای نمایش دو بخش کنار هم.",
                LinearLayout.HORIZONTAL,
                Color.parseColor("#E3F2FD"),
                new String[]{"ستون چپ", "ستون راست"}
        ));
        templates.add(new TemplateDefinition(
                "invoice",
                "صورت‌حساب",
                "برای نمایش آیتم‌ها و مبلغ در قالب رسید.",
                LinearLayout.VERTICAL,
                Color.parseColor("#FFF9C4"),
                new String[]{"نام مشتری", "شماره سفارش", "شرح اقلام", "جمع کل"}
        ));
        templates.add(new TemplateDefinition(
                "hero",
                "کاور تصویری",
                "برای صفحات معرفی با تصویر بزرگ و متن اکشن.",
                LinearLayout.VERTICAL,
                Color.parseColor("#E8EAF6"),
                new String[]{"هدر تصویر", "تیتر اصلی", "توضیح مختصر", "دکمه تماس"}
        ));
        templates.add(new TemplateDefinition(
                "highlight",
                "کارت شاخص",
                "قالب کارت برای نمایش یک آیتم خاص.",
                LinearLayout.VERTICAL,
                Color.parseColor("#F1F8E9"),
                new String[]{"عنوان کارت", "ویژگی اول", "ویژگی دوم"}
        ));
        templates.add(new TemplateDefinition(
                "gallery",
                "شبکه ساده",
                "سه خانه افقی برای عکس یا اطلاعات سریع.",
                LinearLayout.HORIZONTAL,
                Color.parseColor("#FFE0B2"),
                new String[]{"خانه ۱", "خانه ۲", "خانه ۳"}
        ));
        templates.add(new TemplateDefinition(
                "profile",
                "پروفایل",
                "برای نمایش اطلاعات کاربر یا مشتری.",
                LinearLayout.VERTICAL,
                Color.parseColor("#FFF3E0"),
                new String[]{"نام", "نقش", "شماره تماس", "توضیح"}
        ));
        templates.add(new TemplateDefinition(
                "message",
                "پیام و اعلان",
                "بلاک هشدار یا پیام سریع برای اطلاع‌رسانی.",
                LinearLayout.VERTICAL,
                Color.parseColor("#E0F7FA"),
                new String[]{"عنوان پیام", "شرح کوتاه"}
        ));
    }

    private void buildTemplateButtons() {
        templateButtonsContainer.removeAllViews();
        int padding = (int) (getResources().getDisplayMetrics().density * 8);

        for (final TemplateDefinition definition : templates) {
            Button btn = new Button(this);
            btn.setText(definition.title);
            btn.setAllCaps(false);
            btn.setBackgroundResource(R.drawable.bg_btn_white);
            btn.setTextColor(Color.parseColor("#111111"));
            btn.setPadding(padding * 2, padding, padding * 2, padding);
            MarginLayoutParams params = new MarginLayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
            );
            params.setMargins(padding, padding, padding, padding);
            btn.setLayoutParams(params);
            btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    renderTemplate(definition);
                }
            });
            templateButtonsContainer.addView(btn);
        }
    }

    private void bindPaletteItems() {
        int[] paletteIds = new int[]{
                R.id.paletteTitle,
                R.id.paletteSubtitle,
                R.id.palettePrice,
                R.id.paletteAction,
                R.id.paletteNote
        };

        View.OnLongClickListener longClickListener = new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                String label = String.valueOf(v.getTag());
                ClipData dragData = ClipData.newPlainText("FIELD", label);
                View.DragShadowBuilder shadowBuilder = new View.DragShadowBuilder(v);

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    v.startDragAndDrop(dragData, shadowBuilder, null, 0);
                } else {
                    v.startDrag(dragData, shadowBuilder, null, 0);
                }
                txtDropStatus.setText("آیتم \"" + label + "\" آماده رها کردن است");
                return true;
            }
        };

        for (int id : paletteIds) {
            View view = findViewById(id);
            if (view != null) {
                view.setOnLongClickListener(longClickListener);
            }
        }
    }

    private void renderTemplate(TemplateDefinition definition) {
        txtSelectedTemplate.setText("قالب فعال: " + definition.title);
        txtTemplateDescription.setText(definition.description);

        if (txtDropStatus.getParent() instanceof ViewGroup) {
            ((ViewGroup) txtDropStatus.getParent()).removeView(txtDropStatus);
        }
        templatePreviewHost.removeAllViews();

        currentTemplateLayout = new LinearLayout(this);
        currentTemplateLayout.setOrientation(definition.orientation);
        currentTemplateLayout.setBackground(createRoundedBackground(definition.backgroundColor));
        int padding = (int) (getResources().getDisplayMetrics().density * 12);
        currentTemplateLayout.setPadding(padding, padding, padding, padding);
        currentTemplateLayout.setGravity(Gravity.CENTER);
        currentTemplateLayout.setClipToPadding(false);
        currentTemplateLayout.setDividerPadding(padding);
        currentTemplateLayout.setOnDragListener(new View.OnDragListener() {
            @Override
            public boolean onDrag(View v, DragEvent event) {
                return handleDrop(event);
            }
        });

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            currentTemplateLayout.setShowDividers(LinearLayout.SHOW_DIVIDER_MIDDLE);
        }

        for (String slot : definition.placeholderSlots) {
            currentTemplateLayout.addView(createSlotView(slot, true));
        }

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        templatePreviewHost.addView(currentTemplateLayout, params);
        templatePreviewHost.addView(txtDropStatus);
        txtDropStatus.setText("قالب فعال آماده پذیرش آیتم‌های کشیده شده است.");
    }

    private boolean handleDrop(DragEvent event) {
        ClipDescription description = event.getClipDescription();
        switch (event.getAction()) {
            case DragEvent.ACTION_DRAG_STARTED:
                return description != null && description.hasMimeType(ClipDescription.MIMETYPE_TEXT_PLAIN);
            case DragEvent.ACTION_DROP:
                if (currentTemplateLayout == null || event.getClipData() == null) {
                    return false;
                }
                ClipData.Item item = event.getClipData().getItemAt(0);
                String label = item.getText().toString();
                currentTemplateLayout.addView(createSlotView(label, false));
                txtDropStatus.setText("آیتم \"" + label + "\" به قالب اضافه شد.");
                Toast.makeText(this, "افزوده شد: " + label, Toast.LENGTH_SHORT).show();
                return true;
            case DragEvent.ACTION_DRAG_ENDED:
                if (!event.getResult()) {
                    txtDropStatus.setText("رهاسازی انجام نشد. یک قالب را انتخاب کنید و دوباره تلاش کنید.");
                }
                return true;
            default:
                return true;
        }
    }

    private TextView createSlotView(String label, boolean placeholder) {
        TextView tv = new TextView(this);
        tv.setText(label);
        tv.setTextSize(16f);
        tv.setTextColor(placeholder ? Color.parseColor("#444444") : Color.parseColor("#111111"));
        tv.setBackground(createRoundedBackground(placeholder ? Color.parseColor("#E0E0E0") : Color.WHITE));
        tv.setPadding(18, 12, 18, 12);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                placeholder ? LinearLayout.LayoutParams.MATCH_PARENT : LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        params.setMargins(12, 8, 12, 8);
        tv.setLayoutParams(params);
        tv.setGravity(Gravity.CENTER_HORIZONTAL);
        return tv;
    }

    private GradientDrawable createRoundedBackground(int color) {
        GradientDrawable drawable = new GradientDrawable();
        drawable.setColor(color);
        float radius = getResources().getDisplayMetrics().density * 8;
        drawable.setCornerRadius(radius);
        drawable.setStroke(2, Color.parseColor("#33000000"));
        return drawable;
    }

    private static class TemplateDefinition {
        final String key;
        final String title;
        final String description;
        final int orientation;
        final int backgroundColor;
        final String[] placeholderSlots;

        TemplateDefinition(String key, String title, String description,
                           int orientation, int backgroundColor, String[] placeholderSlots) {
            this.key = key;
            this.title = title;
            this.description = description;
            this.orientation = orientation;
            this.backgroundColor = backgroundColor;
            this.placeholderSlots = placeholderSlots != null
                    ? placeholderSlots
                    : new String[]{};
        }
    }
}
