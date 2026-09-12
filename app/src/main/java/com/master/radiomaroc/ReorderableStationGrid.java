package com.master.radiomaroc;

import android.content.ClipData;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.AttributeSet;
import android.view.DragEvent;
import android.view.View;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/** GridLayout that lets users long-press a station card and drag it to a new position. */
public class ReorderableStationGrid extends GridLayout {
    private static final String PREFS = "radio_maroc_prefs";
    private static final String ORDER = "station_order";
    private static final String SEP = "\u001F";
    private boolean rebuilding;

    public ReorderableStationGrid(Context context) { super(context); }
    public ReorderableStationGrid(Context context, AttributeSet attrs) { super(context, attrs); }
    public ReorderableStationGrid(Context context, AttributeSet attrs, int defStyleAttr) { super(context, attrs, defStyleAttr); }

    @Override public void onViewAdded(View child) {
        super.onViewAdded(child);
        if (rebuilding) return;
        String stationName = stationName(child);
        if (stationName == null) return;
        child.setTag(R.id.stationsGrid, stationName);
        child.setOnLongClickListener(v -> {
            String name = (String) v.getTag(R.id.stationsGrid);
            if (name == null) return false;
            ClipData data = ClipData.newPlainText("station", name);
            v.performHapticFeedback(android.view.HapticFeedbackConstants.LONG_PRESS);
            v.startDragAndDrop(data, new View.DragShadowBuilder(v), v, 0);
            v.setAlpha(.45f);
            return true;
        });
        child.setOnDragListener((target, event) -> handleDrag(target, event));
        applySavedOrder();
    }

    private boolean handleDrag(View target, DragEvent event) {
        switch (event.getAction()) {
            case DragEvent.ACTION_DRAG_STARTED:
                return event.getClipDescription() != null && event.getClipDescription().hasMimeType("text/plain");
            case DragEvent.ACTION_DRAG_ENTERED:
                target.setScaleX(1.025f); target.setScaleY(1.025f); return true;
            case DragEvent.ACTION_DRAG_EXITED:
                target.setScaleX(1f); target.setScaleY(1f); return true;
            case DragEvent.ACTION_DROP:
                View dragged = (View) event.getLocalState();
                if (dragged == null || dragged == target || dragged.getParent() != this || target.getParent() != this) return false;
                int from = indexOfChild(dragged), to = indexOfChild(target);
                if (from < 0 || to < 0) return false;
                rebuilding = true;
                removeView(dragged);
                if (from < to) to--;
                addView(dragged, Math.max(0, Math.min(to, getChildCount())));
                rebuilding = false;
                saveOrder();
                requestLayout();
                return true;
            case DragEvent.ACTION_DRAG_ENDED:
                View source = (View) event.getLocalState();
                if (source != null) source.setAlpha(1f);
                target.setScaleX(1f); target.setScaleY(1f);
                return true;
            default: return true;
        }
    }

    private String stationName(View card) {
        if (!(card instanceof LinearLayout)) return null;
        LinearLayout row = (LinearLayout) card;
        for (int i=0;i<row.getChildCount();i++) {
            View v=row.getChildAt(i);
            if (v instanceof LinearLayout) {
                LinearLayout info=(LinearLayout)v;
                if (info.getChildCount()>0 && info.getChildAt(0) instanceof TextView) {
                    CharSequence text=((TextView)info.getChildAt(0)).getText();
                    if (text!=null && text.length()>0) return text.toString();
                }
            }
        }
        return null;
    }

    /** Save the visible reorder without destroying hidden stations when search/favorites filters are active. */
    private void saveOrder() {
        List<String> visible=new ArrayList<>();
        for(int i=0;i<getChildCount();i++) {
            Object tag=getChildAt(i).getTag(R.id.stationsGrid);
            if(tag instanceof String) visible.add((String)tag);
        }
        if (visible.isEmpty()) return;

        List<String> full=loadFullOrder();
        Set<String> visibleSet=new LinkedHashSet<>(visible);
        int next=0;
        for(int i=0;i<full.size() && next<visible.size();i++) {
            if(visibleSet.contains(full.get(i))) full.set(i, visible.get(next++));
        }
        while(next<visible.size()) full.add(visible.get(next++));
        saveFullOrder(full);
    }

    private List<String> loadFullOrder() {
        SharedPreferences p=getContext().getSharedPreferences(PREFS, Context.MODE_PRIVATE);
        String raw=p.getString(ORDER, "");
        List<String> result=new ArrayList<>();
        if(raw!=null && !raw.isEmpty()) {
            for(String n:raw.split(SEP, -1)) if(!n.isEmpty() && !result.contains(n)) result.add(n);
        }
        // Always retain newly added stations and establish a complete canonical order on first use.
        for(Station s:Stations.ALL) if(!result.contains(s.name)) result.add(s.name);
        return result;
    }

    private void saveFullOrder(List<String> names) {
        getContext().getSharedPreferences(PREFS, Context.MODE_PRIVATE).edit().putString(ORDER, join(names)).apply();
    }

    private void applySavedOrder() {
        List<String> order=loadFullOrder();
        if(getChildCount()<2) return;
        rebuilding=true;
        int wantedIndex=0;
        for(String wantedName:order) {
            int found=-1;
            for(int i=wantedIndex;i<getChildCount();i++) {
                Object tag=getChildAt(i).getTag(R.id.stationsGrid);
                if(wantedName.equals(tag)) { found=i; break; }
            }
            if(found>=0) {
                if(found!=wantedIndex) { View v=getChildAt(found); removeViewAt(found); addView(v,wantedIndex); }
                wantedIndex++;
                if(wantedIndex>=getChildCount()) break;
            }
        }
        rebuilding=false;
    }

    private String join(List<String> names) {
        StringBuilder b=new StringBuilder();
        for(String n:names){ if(b.length()>0)b.append(SEP); b.append(n); }
        return b.toString();
    }
}
