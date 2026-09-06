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
            InputStream in = getResources().openRawResource(R.raw.moroccan_photo_b64);
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            byte[] buffer = new byte[8192];
            int count;
            while ((count = in.read(buffer)) != -1) out.write(buffer, 0, count);
            in.close();
            String encoded = out.toString("US-ASCII").replaceAll("\\s", "");
            byte[] image = Base64.decode(encoded, Base64.DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(image, 0, image.length);
            if (bitmap == null) throw new IllegalStateException("Unable to decode embedded Moroccan photo");
            setImageBitmap(bitmap);
        } catch (Exception e) {
            setBackgroundColor(0xFF071C26);
        }
    }
}
