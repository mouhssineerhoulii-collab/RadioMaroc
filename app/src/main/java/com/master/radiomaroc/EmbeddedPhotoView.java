package com.master.radiomaroc;

import android.content.Context;
import android.graphics.*;
import android.graphics.drawable.BitmapDrawable;
import android.util.AttributeSet;
import android.util.Base64;
import android.widget.ImageView;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;

/** Full-screen Moroccan background with a warm Marrakech/Atlas treatment. */
public class EmbeddedPhotoView extends ImageView {
    public EmbeddedPhotoView(Context c) { super(c); init(); }
    public EmbeddedPhotoView(Context c, AttributeSet a) { super(c, a); init(); }
    public EmbeddedPhotoView(Context c, AttributeSet a, int s) { super(c, a, s); init(); }

    private void init() {
        setScaleType(ScaleType.CENTER_CROP);
        setAdjustViewBounds(false);
        try {
            StringBuilder encoded = new StringBuilder();
            int[] parts = {R.raw.moroccan_photo_1,R.raw.moroccan_photo_2,R.raw.moroccan_photo_3,R.raw.moroccan_photo_4};
            byte[] buffer = new byte[8192];
            for (int id : parts) {
                InputStream in = getResources().openRawResource(id);
                ByteArrayOutputStream out = new ByteArrayOutputStream();
                int n; while ((n=in.read(buffer))!=-1) out.write(buffer,0,n);
                in.close();
                encoded.append(out.toString("US-ASCII").replaceAll("\\s", ""));
            }
            byte[] data=Base64.decode(encoded.toString(),Base64.DEFAULT);
            Bitmap src=BitmapFactory.decodeByteArray(data,0,data.length);
            if(src==null) throw new IllegalStateException("background decode failed");

            Bitmap out=Bitmap.createBitmap(src.getWidth(),src.getHeight(),Bitmap.Config.ARGB_8888);
            Canvas canvas=new Canvas(out);
            Paint p=new Paint(Paint.ANTI_ALIAS_FLAG|Paint.FILTER_BITMAP_FLAG|Paint.DITHER_FLAG);
            ColorMatrix cm=new ColorMatrix();
            cm.setSaturation(1.16f);
            ColorMatrix warm=new ColorMatrix(new float[]{
                    1.07f,0,0,0,5,
                    0,1.02f,0,0,1,
                    0,0,.94f,0,-2,
                    0,0,0,1,0});
            cm.postConcat(warm);
            p.setColorFilter(new ColorMatrixColorFilter(cm));
            canvas.drawBitmap(src,0,0,p);
            setImageDrawable(new BitmapDrawable(getResources(),out));
        } catch(Exception e) {
            setImageResource(R.drawable.moroccan_background);
        }
    }
}
