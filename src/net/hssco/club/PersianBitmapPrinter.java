package net.hssco.club;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;

public class PersianBitmapPrinter {

    /**
     * تبدیل متن چندخطی RTL به Bitmap مناسب پرینتر 384px
     *
     * هر خط را با '\n' جدا کن
     */
    public static Bitmap textToBitmapRTL(Context ctx, String text, int fontSize) {

        if (text == null) {
            text = "";
        }

        // آماده‌سازی قلم
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setTextSize(fontSize);
        paint.setColor(Color.BLACK);

        try {
            Typeface tf = Typeface.createFromAsset(
                    ctx.getAssets(),
                    "fonts/IRANSansX-Regular.ttf"
            );
            paint.setTypeface(tf);
        } catch (Exception e) {
            // اگر فونت لود نشد، با فونت پیش‌فرض ادامه بده
        }

        // صفحه استاندارد پرینتر (Urovo / 58mm)
        int pageWidth = 384;
        int margin = 10;

        // متن را بر اساس خط جدا کن
        String[] lines = text.split("\n");
        if (lines.length == 0) {
            lines = new String[]{""};
        }

        // ارتفاع هر خط + فاصله بین خطوط
        int lineHeight = fontSize + 8;
        int paddingTop = 12;
        int paddingBottom = 12;

        int bmpWidth = pageWidth;
        int bmpHeight = paddingTop + paddingBottom + (lines.length * lineHeight);

        Bitmap bmp = Bitmap.createBitmap(bmpWidth, bmpHeight, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bmp);

        // پس‌زمینه سفید
        canvas.drawColor(Color.WHITE);

        // هر خط را جداگانه راست‌چین چاپ کن
        int i;
        for (i = 0; i < lines.length; i++) {
            String line = lines[i];
            if (line == null) line = "";

            float textWidth = paint.measureText(line);

            float startX;

            // اگر متن از عرض صفحه بزرگ‌تر بود، دیگه زور نزن، از سمت راست کمی فاصله بده
            if (textWidth + 2 * margin > pageWidth) {
                startX = margin;  // عملاً چپ‌چین نسبی، ولی تو پنل کوچک کار می‌کند
            } else {
                startX = pageWidth - textWidth - margin;  // راست‌چین
            }

            float y = paddingTop + (i + 1) * lineHeight;
            canvas.drawText(line, startX, y, paint);
        }

        return bmp;
    }
}
