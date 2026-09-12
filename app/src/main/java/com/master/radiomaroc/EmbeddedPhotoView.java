package com.master.radiomaroc;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.AttributeSet;
import android.util.Base64;
import android.widget.ImageView;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;

public class EmbeddedPhotoView extends ImageView {
    public EmbeddedPhotoView(Context context) { super(context); init(); }
    public EmbeddedPhotoView(Context context, AttributeSet attrs) { super(context, attrs); init(); }
    public EmbeddedPhotoView(Context context, AttributeSet attrs, int defStyleAttr) { super(context, attrs, defStyleAttr); init(); }

    private void init() {
        setScaleType(ScaleType.CENTER_CROP);
        try {
            StringBuilder encoded = new StringBuilder();
            int[] parts = new int[] {
                    R.raw.moroccan_photo_1,
                    R.raw.moroccan_photo_2,
                    R.raw.moroccan_photo_3,
                    R.raw.moroccan_photo_4
            };
            byte[] buffer = new byte[8192];
            for (int resId : parts) {
                InputStream in = getResources().openRawResource(resId);
                ByteArrayOutputStream out = new ByteArrayOutputStream();
                int count;
                while ((count = in.read(buffer)) != -1) out.write(buffer, 0, count);
                in.close();
                encoded.append(out.toString("US-ASCII").replaceAll("\\s", ""));
            }
            byte[] image = Base64.decode(encoded.toString(), Base64.DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(image, 0, image.length);
            if (bitmap == null) throw new IllegalStateException("Unable to decode Moroccan background");
            setImageBitmap(bitmap);
        } catch (Exception e) {
            setBackgroundColor(0xFF071C26);
        }
    }
}
