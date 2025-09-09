package tw.nekomimi.nekogram.helpers;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.os.Build;

import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;

import java.util.List;

public class TypefaceHelper {

    private static final String TEST_TEXT;
    private static final int CANVAS_SIZE = 40;
    private static final Paint PAINT = new Paint() {{
        setTextSize(20);
        setAntiAlias(false);
        setSubpixelText(false);
        setFakeBoldText(false);
    }};

    private static Boolean mediumWeightSupported = null;

    static {
        var lang = LocaleController.getInstance().getCurrentLocale().getLanguage();
        if (List.of("zh", "ja", "ko").contains(lang)) {
            TEST_TEXT = "你好";
        } else if (List.of("ar", "fa").contains(lang)) {
            TEST_TEXT = "مرحبا";
        } else if ("iw".equals(lang)) {
            TEST_TEXT = "שלום";
        } else if ("th".equals(lang)) {
            TEST_TEXT = "สวัสดี";
        } else if ("hi".equals(lang)) {
            TEST_TEXT = "नमस्ते";
        } else if (List.of("ru", "uk", "ky", "be", "sr").contains(lang)) {
            TEST_TEXT = "Привет";
        } else {
            TEST_TEXT = "R";
        }
    }

    public static Typeface createTypeface(String assetPath) {
        return switch (assetPath) {
            case AndroidUtilities.TYPEFACE_ROBOTO_MEDIUM ->
                    isMediumWeightSupported() ? Typeface.create("sans-serif-medium", Typeface.NORMAL) : Typeface.create("sans-serif", Typeface.BOLD);
            case AndroidUtilities.TYPEFACE_ROBOTO_MEDIUM_ITALIC ->
                    isMediumWeightSupported() ? Typeface.create("sans-serif-medium", Typeface.ITALIC) : Typeface.create("sans-serif", Typeface.BOLD_ITALIC);
            case AndroidUtilities.TYPEFACE_RCONDENSED_BOLD ->
                    Typeface.create("sans-serif-condensed", Typeface.BOLD);
            case AndroidUtilities.TYPEFACE_RITALIC ->
                    Typeface.create("sans-serif", Typeface.ITALIC);
            case AndroidUtilities.TYPEFACE_ROBOTO_MONO ->
                    Typeface.MONOSPACE;
            default -> createTypefaceFromAsset(assetPath);
        };
    }

    public static Typeface createTypefaceFromAsset(String assetPath) {
        if (Build.VERSION.SDK_INT >= 26) {
            Typeface.Builder builder = new Typeface.Builder(ApplicationLoader.applicationContext.getAssets(), assetPath);
            if (assetPath.contains("medium")) {
                builder.setWeight(700);
            }
            if (assetPath.contains("italic")) {
                builder.setItalic(true);
            }
            return builder.build();
        } else {
            return Typeface.createFromAsset(ApplicationLoader.applicationContext.getAssets(), assetPath);
        }
    }

    public static boolean isMediumWeightSupported() {
        if (mediumWeightSupported == null) {
            mediumWeightSupported = testTypeface(Typeface.create("sans-serif-medium", Typeface.NORMAL));
            FileLog.d("mediumWeightSupported = " + mediumWeightSupported);
        }
        return mediumWeightSupported;
    }

    private static boolean testTypeface(Typeface typeface) {
        Canvas canvas = new Canvas();

        Bitmap bitmap1 = Bitmap.createBitmap(CANVAS_SIZE * 2, CANVAS_SIZE, Bitmap.Config.ARGB_8888);
        canvas.setBitmap(bitmap1);
        PAINT.setTypeface(null);
        canvas.drawText(TEST_TEXT, 0, CANVAS_SIZE, PAINT);

        Bitmap bitmap2 = Bitmap.createBitmap(CANVAS_SIZE * 2, CANVAS_SIZE, Bitmap.Config.ARGB_8888);
        canvas.setBitmap(bitmap2);
        PAINT.setTypeface(typeface);
        canvas.drawText(TEST_TEXT, 0, CANVAS_SIZE, PAINT);

        boolean supported = !bitmap1.sameAs(bitmap2);
        AndroidUtilities.recycleBitmaps(List.of(bitmap1, bitmap2));
        return supported;
    }
}
