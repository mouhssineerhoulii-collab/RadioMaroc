package com.master.radiomaroc;

import android.content.ClipData;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Point;
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

/** GridLayout that lets users long-press a station card and reorder it vertically. */
public class ReorderableStationGrid extends GridLayout {
    private static final String PREFS = "radio_maroc_prefs";
    private static final String ORDER = "station_order";
    private static final String SEP = "\u001F";
    private boolean rebuilding;
    private View activeTarget;

    public ReorderableStationGrid(Context context) { super(context); init(); }
    public ReorderableStationGrid(Context context, AttributeSet attrs) { super(context, attrs); init(); }
    public ReorderableStationGrid(Context context, AttributeSet attrs, int defStyleAttr) { super(context, attrs, defStyleAttr); init(); }

    private void init() {
        // The grid itself must receive drag events while the finger travels through the gaps
        // between cards. This makes true up/down reordering reliable instead of target-only drops.
        setOnDragListener((v, event) -> handleGridDrag(event));
    }

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
            // Invisible shadow: no Android/red arrow/marker follows the finger.
            v.startDragAndDrop(data, new InvisibleDragShadowBuilder(v), v, 0);
            v.setAlpha(.55f);
            return true;
        });
        child.setOnDragListener((target, event) -> handleCardDrag(target, event));
        applySavedOrder();
    }

    private boolean accepts(DragEvent event) {
        return event.getClipDescription() != null && event.getClipDescription().hasMimeType("text/plain");
    }

    private boolean handleGridDrag(DragEvent event) {
        switch (event.getAction()) {
            case DragEvent.ACTION_DRAG_STARTED:
                return accepts(event);
            case DragEvent.ACTION_DRAG_LOCATION:
                View nearest = nearestCard(event.getY());
                if (nearest != null) showInsertionTarget(nearest, event.getY() - nearest.getTop());
                return true;
            case DragEvent.ACTION_DROP:
                View target = nearestCard(event.getY());
                if (target == null) return false;
                return performDrop((View) event.getLocalState(), target, event.getY() - target.getTop());
            case DragEvent.ACTION_DRAG_ENDED:
                finishDrag((View) event.getLocalState());
                return true;
            default:
                return true;
        }
    }

    private boolean handleCardDrag(View target, DragEvent event) {
        switch (event.getAction()) {
            case DragEvent.ACTION_DRAG_STARTED:
                return accepts(event);
            case DragEvent.ACTION_DRAG_ENTERED:
            case DragEvent.ACTION_DRAG_LOCATION:
                showInsertionTarget(target, event.getY());
                return true;
            case DragEvent.ACTION_DRAG_EXITED:
                return true;
            case DragEvent.ACTION_DROP:
                return performDrop((View) event.getLocalState(), target, event.getY());
            case DragEvent.ACTION_DRAG_ENDED:
                finishDrag((View) event.getLocalState());
                return true;
            default:
                return true;
        }
    }

    private boolean performDrop(View dragged, View target, float localY) {
        clearAllInsertionTargets();
        if (dragged == null || target == null || dragged == target || dragged.getParent() != this || target.getParent() != this) return false;
        int from = indexOfChild(dragged), targetIndex = indexOfChild(target);
        if (from < 0 || targetIndex < 0) return false;
        boolean after = localY >= target.getHeight() / 2f;
        int insertion = targetIndex + (after ? 1 : 0);
        rebuilding = true;
        removeView(dragged);
        if (from < insertion) insertion--;
        insertion = Math.max(0, Math.min(insertion, getChildCount()));
        addView(dragged, insertion);
        rebuilding = false;
        saveOrder();
        requestLayout();
        return true;
    }

    private View nearestCard(float gridY) {
        if (getChildCount() == 0) return null;
        View best = null;
        float bestDistance = Float.MAX_VALUE;
        for (int i = 0; i < getChildCount(); i++) {
            View child = getChildAt(i);
            float center = child.getTop() + child.getHeight() / 2f;
            float distance = Math.abs(gridY - center);
            if (distance < bestDistance) { bestDistance = distance; best = child; }
        }
        return best;
    }

    private void finishDrag(View source) {
        if (source != null) source.setAlpha(1f);
        clearAllInsertionTargets();
    }

    /** Subtle vertical spacing only; no arrow, red bar, or floating card. */
    private void showInsertionTarget(View target, float localY) {
        if (activeTarget != null && activeTarget != target) clearInsertionTarget(activeTarget);
        activeTarget = target;
        boolean below = localY >= target.getHeight() / 2f;
        target.setTranslationX(0f);
        target.setTranslationY(below ? -dp(7) : dp(7));
        target.setScaleX(1f);
        target.setScaleY(1f);
    }

    private void clearInsertionTarget(View target) {
        if (target == null) return;
        target.animate().translationX(0f).translationY(0f).scaleX(1f).scaleY(1f).setDuration(70).start();
        if (activeTarget == target) activeTarget = null;
    }

    private void clearAllInsertionTargets() {
        for (int i = 0; i < getChildCount(); i++) clearInsertionTarget(getChildAt(i));
        activeTarget = null;
    }

    private float dp(float value) { return value * getResources().getDisplayMetrics().density; }

    private static final class InvisibleDragShadowBuilder extends View.DragShadowBuilder {
        InvisibleDragShadowBuilder(View view) { super(view); }
        @Override public void onProvideShadowMetrics(Point size, Point touch) {
            size.set(1, 1);
            touch.set(0, 0);
        }
        @Override public void onDrawShadow(Canvas canvas) { /* intentionally invisible */ }
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
