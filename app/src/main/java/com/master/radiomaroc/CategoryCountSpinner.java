package com.master.radiomaroc;

import android.content.Context;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

/** Category selector with a compact visual count badge. */
public final class CategoryCountSpinner extends Spinner {
    private static final String[] KEYS={"all","islamic","public","regional","news","sports","general","amazigh","music","foreign"};
    private SpinnerAdapter source;
    public CategoryCountSpinner(Context c){super(c);} public CategoryCountSpinner(Context c,AttributeSet a){super(c,a);} public CategoryCountSpinner(Context c,AttributeSet a,int s){super(c,a,s);}
    @Override public void setAdapter(SpinnerAdapter adapter){source=adapter;if(adapter==null){super.setAdapter(null);return;}super.setAdapter(new BaseAdapter(){public int getCount(){return source.getCount();}public Object getItem(int p){return source.getItem(p);}public long getItemId(int p){return source.getItemId(p);}public View getView(int p,View v,ViewGroup parent){return row(p,false);}public View getDropDownView(int p,View v,ViewGroup parent){return row(p,true);}});}
    private View row(int p,boolean drop){Context c=getContext();boolean rtl=getResources().getConfiguration().getLayoutDirection()==View.LAYOUT_DIRECTION_RTL;LinearLayout r=new LinearLayout(c);r.setOrientation(LinearLayout.HORIZONTAL);r.setGravity(Gravity.CENTER_VERTICAL);r.setLayoutDirection(rtl?View.LAYOUT_DIRECTION_RTL:View.LAYOUT_DIRECTION_LTR);r.setPadding(dp(16),drop?dp(10):0,dp(16),drop?dp(10):0);if(drop)r.setBackgroundColor(0xFF102735);TextView title=new TextView(c);title.setText(String.valueOf(source.getItem(p)));title.setTextColor(0xFFFFFFFF);title.setTextSize(16);title.setTypeface(Typeface.DEFAULT,Typeface.BOLD);title.setGravity((rtl?Gravity.RIGHT:Gravity.LEFT)|Gravity.CENTER_VERTICAL);r.addView(title,new LinearLayout.LayoutParams(0,dp(48),1f));TextView b=new TextView(c);b.setText(String.valueOf(CategoryCount.of(p<KEYS.length?KEYS[p]:"all")));b.setTextColor(0xFF0A392B);b.setTextSize(13);b.setTypeface(Typeface.DEFAULT,Typeface.BOLD);b.setGravity(Gravity.CENTER);b.setPadding(dp(9),dp(3),dp(9),dp(3));GradientDrawable bg=new GradientDrawable();bg.setColor(0xFFF8D990);bg.setCornerRadius(dp(15));bg.setStroke(dp(1),0xFF0A392B);b.setBackground(bg);LinearLayout.LayoutParams bp=new LinearLayout.LayoutParams(-2,dp(30));bp.setMarginStart(dp(10));r.addView(b,bp);return r;}
    private int dp(int n){return(int)(n*getResources().getDisplayMetrics().density+.5f);}
}
