package com.master.radiomaroc;

import android.content.ClipData;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Point;
import android.graphics.drawable.ColorDrawable;
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
            // A narrow custom shadow removes the impression that the whole card can float freely
            // in every direction. The actual insertion position is determined only by vertical Y.
            v.startDragAndDrop(data, new VerticalDragShadowBuilder(v), v, 0);
            v.setAlpha(.35f);
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
            case DragEvent.ACTION_DRAG_LOCATION:
                showInsertionTarget(target, event.getY());
                return true;
            case DragEvent.ACTION_DRAG_EXITED:
                clearInsertionTarget(target);
                return true;
            case DragEvent.ACTION_DROP:
                View dragged = (View) event.getLocalState();
                clearInsertionTarget(target);
                if (dragged == null || dragged == target || dragged.getParent() != this || target.getParent() != this) return false;
                int from = indexOfChild(dragged), targetIndex = indexOfChild(target);
                if (from < 0 || targetIndex < 0) return false;

                // Top half means insert before the target; bottom half means insert after it.
                boolean after = event.getY() >= target.getHeight() / 2f;
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
            case DragEvent.ACTION_DRAG_ENDED:
                View source = (View) event.getLocalState();
                if (source != null) source.setAlpha(1f);
                clearAllInsertionTargets();
                return true;
            default: return true;
        }
    }

    /** Visually marks whether the dragged station will be inserted above or below this card. */
    private void showInsertionTarget(View target, float localY) {
        if (activeTarget != null && activeTarget != target) clearInsertionTarget(activeTarget);
        activeTarget = target;
        boolean below = localY >= target.getHeight() / 2f;
        target.setTranslationX(0f); // never visually follow horizontal finger movement
        target.setTranslationY(below ? -dp(5) : dp(5));
        target.setScaleX(1f);
        target.setScaleY(.985f);
        target.setElevation(dp(2));
    }

    private void clearInsertionTarget(View target) {
        if (target == null) return;
        target.animate().translationX(0f).translationY(0f).scaleX(1f).scaleY(1f).setDuration(90).start();
        target.setElevation(0f);
        if (activeTarget == target) activeTarget = null;
    }

    private void clearAllInsertionTargets() {
        for (int i = 0; i < getChildCount(); i++) clearInsertionTarget(getChildAt(i));
        activeTarget = null;
    }

    private float dp(float value) {
        return value * getResources().getDisplayMetrics().density;
    }

    /** Compact vertical-only drag preview; insertion feedback is shown on the destination card. */
    private static final class VerticalDragShadowBuilder extends View.DragShadowBuilder {
        VerticalDragShadowBuilder(View view) { super(view); }

        @Override public void onProvideShadowMetrics(Point size, Point touch) {
            View v = getView();
            int width = Math.max(8, Math.min(v.getWidth() / 10, 28));
            int height = Math.max(32, v.getHeight());
            size.set(width, height);
            touch.set(width / 2, height / 2);
        }

        @Override public void onDrawShadow(Canvas canvas) {
            ColorDrawable marker = new ColorDrawable(0xCCB71C1C);
            marker.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
            marker.draw(canvas);
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
