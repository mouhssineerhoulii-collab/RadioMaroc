package com.master.radiomaroc;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

/** Spinner that keeps category names clean and renders the count as a separate visual badge. */
public final class CategoryCountSpinner extends Spinner {
    private static final String[] KEYS={"all","islamic","public","regional","news","sports","general","amazigh","music","foreign"};
    private SpinnerAdapter source;

    public CategoryCountSpinner(Context c){super(c);}
    public CategoryCountSpinner(Context c, AttributeSet a){super(c,a);}
    public CategoryCountSpinner(Context c, AttributeSet a,int s){super(c,a,s);}

    @Override public void setAdapter(SpinnerAdapter adapter){
        source=adapter;
        if(adapter==null){super.setAdapter(null);return;}
        super.setAdapter(new BaseAdapter() implements SpinnerAdapter {
            public int getCount(){return source.getCount();}
            public Object getItem(int p){return source.getItem(p);}
            public long getItemId(int p){return source.getItemId(p);}
            public View getView(int p,View v,ViewGroup parent){return row(p,false);}
            public View getDropDownView(int p,View v,ViewGroup parent){return row(p,true);}
        });
    }

    private View row(int position,boolean dropdown){
        Context c=getContext(); boolean rtl=getResources().getConfiguration().getLayoutDirection()==View.LAYOUT_DIRECTION_RTL;
        LinearLayout row=new LinearLayout(c);row.setOrientation(LinearLayout.HORIZONTAL);row.setGravity(Gravity.CENTER_VERTICAL);row.setPadding(dp(16),dropdown?dp(11):0,dp(16),dropdown?dp(11):0);row.setLayoutDirection(rtl?View.LAYOUT_DIRECTION_RTL:View.LAYOUT_DIRECTION_LTR);if(dropdown)row.setBackgroundColor(0xFF102735);
        TextView title=new TextView(c);title.setText(String.valueOf(source.getItem(position)));title.setTextColor(0xFFFFFFFF);title.setTextSize(16);title.setTypeface(Typeface.DEFAULT,Typeface.BOLD);title.setSingleLine(false);title.setGravity(rtl?Gravity.RIGHT:Gravity.LEFT);row.addView(title,new LinearLayout.LayoutParams(0,dropdown?dp(48):dp(52),1f));
        TextView badge=new TextView(c);String key=position<KEYS.length?KEYS[position]:"all";badge.setText(String.valueOf(CategoryCount.of(key)));badge.setTextColor(0xFFF8D990);badge.setTextSize(13);badge.setTypeface(Typeface.DEFAULT,Typeface.BOLD);badge.setGravity(Gravity.CENTER);badge.setMinWidth(dp(36));badge.setPadding(dp(9),dp(4),dp(9),dp(4));badge.setBackgroundResource(R.drawable.count_badge);LinearLayout.LayoutParams bp=new LinearLayout.LayoutParams(-2,dp(30));bp.setMarginStart(dp(10));row.addView(badge,bp);return row;
    }
    private int dp(int n){return(int)(n*getResources().getDisplayMetrics().density+.5f);}
}
