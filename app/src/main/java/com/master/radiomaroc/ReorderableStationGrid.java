package com.master.radiomaroc;

import android.content.ClipData;
import android.content.Context;
import android.content.SharedPreferences;
import android.animation.LayoutTransition;
import android.os.Handler;
import android.os.Looper;
import android.graphics.Canvas;
import android.graphics.Point;
import android.util.AttributeSet;
import android.view.DragEvent;
import android.view.View;
import android.view.ViewParent;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.HashMap;
import java.util.Map;

/** GridLayout with live long-press vertical station reordering. */
public class ReorderableStationGrid extends GridLayout {
    private static final String PREFS = "radio_maroc_prefs";
    private static final String ORDER = "station_order";
    private static final String SEP = "\u001F";
    private boolean rebuilding;
    private View dragging;
    private View hoverTarget;
    private final Handler dragHandler = new Handler(Looper.getMainLooper());
    private float lastDragScreenY;
    private int scrollVelocity;
    private boolean autoScrollRunning;
    private final Runnable autoScrollFrame = new Runnable() {
        @Override public void run() {
            if (!autoScrollRunning || dragging == null) return;
            ScrollView scroll = parentScrollView();
            if (scroll != null && scrollVelocity != 0) {
                scroll.scrollBy(0, scrollVelocity);
                int[] loc = new int[2]; getLocationOnScreen(loc);
                liveMove(dragging, lastDragScreenY - loc[1]);
            }
            dragHandler.postDelayed(this, 16);
        }
    };

    public ReorderableStationGrid(Context context) { super(context); init(); }
    public ReorderableStationGrid(Context context, AttributeSet attrs) { super(context, attrs); init(); }
    public ReorderableStationGrid(Context context, AttributeSet attrs, int defStyleAttr) { super(context, attrs, defStyleAttr); init(); }

    private void init() {
        LayoutTransition transition = new LayoutTransition();
        transition.setDuration(LayoutTransition.CHANGE_APPEARING, 90);
        transition.setDuration(LayoutTransition.CHANGE_DISAPPEARING, 90);
        transition.setDuration(LayoutTransition.CHANGING, 90);
        transition.enableTransitionType(LayoutTransition.CHANGING);
        setLayoutTransition(transition);
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
            dragging = v;
            ClipData data = ClipData.newPlainText("station", name);
            v.performHapticFeedback(android.view.HapticFeedbackConstants.LONG_PRESS);
            boolean started = v.startDragAndDrop(data, new InvisibleDragShadowBuilder(v), v, 0);
            if (started) v.animate().alpha(.88f).scaleX(1.045f).scaleY(1.045f).setDuration(70).setInterpolator(new android.view.animation.OvershootInterpolator(.7f)).start(); else dragging = null;
            return started;
        });
        applySavedOrder();
    }

    private boolean accepts(DragEvent event) {
        return event.getClipDescription() != null && event.getClipDescription().hasMimeType("text/plain");
    }

    private boolean handleGridDrag(DragEvent event) {
        switch (event.getAction()) {
            case DragEvent.ACTION_DRAG_STARTED:
                dragging = event.getLocalState() instanceof View ? (View) event.getLocalState() : dragging;
                autoScrollRunning=true;dragHandler.removeCallbacks(autoScrollFrame);dragHandler.post(autoScrollFrame);
                return accepts(event) && dragging != null;
            case DragEvent.ACTION_DRAG_LOCATION:
                updateAutoScroll(event.getY());
                liveMove(dragging, event.getY());
                return true;
            case DragEvent.ACTION_DROP:
                liveMove(dragging, event.getY());
                saveOrder();
                return true;
            case DragEvent.ACTION_DRAG_ENDED:
                finishDrag();
                return true;
            default:
                return true;
        }
    }

    /** Move the dragged card as soon as its centre crosses another card centre. */
    private void liveMove(View dragged, float gridY) {
        if (dragged == null || dragged.getParent() != this || getChildCount() < 2) return;
        int from = indexOfChild(dragged);
        int desired = from;
        View nearest = null; float nearestDistance = Float.MAX_VALUE;
        for (int i = 0; i < getChildCount(); i++) {
            View child = getChildAt(i);
            if (child == dragged) continue;
            float center = child.getTop() + child.getHeight() / 2f;
            float distance = Math.abs(gridY-center); if(distance<nearestDistance){nearestDistance=distance;nearest=child;}
            if (gridY > center) desired = Math.max(desired, i);
            else if (gridY < center) { desired = Math.min(desired, i); break; }
        }
        pulseTarget(nearest);
        desired = Math.max(0, Math.min(desired, getChildCount() - 1));
        if (desired == from) return;

        Map<View,Integer> oldTops=new HashMap<>();for(int i=0;i<getChildCount();i++){View v=getChildAt(i);oldTops.put(v,v.getTop());}
        rebuilding = true;
        LayoutTransition transition=getLayoutTransition();setLayoutTransition(null);
        removeViewAt(from);
        desired = Math.max(0, Math.min(desired, getChildCount()));
        addView(dragged, desired);
        setLayoutTransition(transition);
        rebuilding = false;
        dragged.setAlpha(.72f);
        requestLayout();
        post(()->animateDomino(oldTops,dragged));
        saveOrder();
    }

    private void finishDrag() {
        autoScrollRunning=false;scrollVelocity=0;dragHandler.removeCallbacks(autoScrollFrame);
        if (dragging != null) dragging.animate().alpha(1f).scaleX(1f).scaleY(1f).setDuration(130).start();
        pulseTarget(null);
        dragging = null;
        saveOrder();
        requestLayout();
    }

    /** Waterfall motion: each displaced card follows the previous one with a very short delay. */
    private void animateDomino(Map<View,Integer> oldTops,View dragged){
        int wave=0;
        for(int i=0;i<getChildCount();i++){
            View v=getChildAt(i);Integer old=oldTops.get(v);if(old==null)continue;float delta=old-v.getTop();
            if(Math.abs(delta)<1f)continue;v.animate().cancel();v.setTranslationY(delta);v.setScaleX(.985f);v.setScaleY(.985f);
            v.animate().translationY(0f).scaleX(1f).scaleY(1f).setStartDelay(Math.min(72,wave++*12L)).setDuration(v==dragged?95:135).setInterpolator(new android.view.animation.OvershootInterpolator(.55f)).start();
        }
    }

    private void pulseTarget(View target) {
        if (hoverTarget == target) return;
        if (hoverTarget != null && hoverTarget != dragging) hoverTarget.animate().scaleX(1f).scaleY(1f).alpha(1f).setDuration(80).start();
        hoverTarget = target;
        if (hoverTarget != null && hoverTarget != dragging) hoverTarget.animate().scaleX(.975f).scaleY(.975f).alpha(.82f).setDuration(70).start();
    }

    /** Fast edge scrolling keeps long lists fluid while a card is being reordered. */
    private void updateAutoScroll(float gridY) {
        ScrollView scroll = parentScrollView();
        if (scroll == null) return;
        int[] gridLocation = new int[2], scrollLocation = new int[2];
        getLocationOnScreen(gridLocation); scroll.getLocationOnScreen(scrollLocation);
        float screenY = gridLocation[1] + gridY;lastDragScreenY=screenY;
        float top = scrollLocation[1], bottom = top + scroll.getHeight();
        float edge = Math.max(72f, scroll.getHeight() * .16f);
        int dy = 0;
        if (screenY < top + edge) dy = -(int) (9 + 30 * Math.min(1f,(top + edge - screenY) / edge));
        else if (screenY > bottom - edge) dy = (int) (9 + 30 * Math.min(1f,(screenY - bottom + edge) / edge));
        scrollVelocity=dy;
    }

    private ScrollView parentScrollView() {
        ViewParent p = getParent();
        while (p != null) { if (p instanceof ScrollView) return (ScrollView) p; p = p.getParent(); }
        return null;
    }

    /** Deliberately invisible: Android must not show a red arrow, bar or floating preview. */
    private static final class InvisibleDragShadowBuilder extends View.DragShadowBuilder {
        InvisibleDragShadowBuilder(View view) { super(view); }
        @Override public void onProvideShadowMetrics(Point size, Point touch) {
            size.set(1, 1);
            touch.set(0, 0);
        }
        @Override public void onDrawShadow(Canvas canvas) { }
    }

    private String stationName(View card) {
        if (!(card instanceof LinearLayout)) return null;
        LinearLayout row = (LinearLayout) card;
        for (int i = 0; i < row.getChildCount(); i++) {
            View v = row.getChildAt(i);
            if (v instanceof LinearLayout) {
                LinearLayout info = (LinearLayout) v;
                if (info.getChildCount() > 0 && info.getChildAt(0) instanceof TextView) {
                    CharSequence text = ((TextView) info.getChildAt(0)).getText();
                    if (text != null && text.length() > 0) return text.toString();
                }
            }
        }
        return null;
    }

    private void saveOrder() {
        List<String> visible = new ArrayList<>();
        for (int i = 0; i < getChildCount(); i++) {
            Object tag = getChildAt(i).getTag(R.id.stationsGrid);
            if (tag instanceof String) visible.add((String) tag);
        }
        if (visible.isEmpty()) return;
        List<String> full = loadFullOrder();
        Set<String> visibleSet = new LinkedHashSet<>(visible);
        int next = 0;
        for (int i = 0; i < full.size() && next < visible.size(); i++) {
            if (visibleSet.contains(full.get(i))) full.set(i, visible.get(next++));
        }
        while (next < visible.size()) full.add(visible.get(next++));
        saveFullOrder(full);
    }

    private List<String> loadFullOrder() {
        SharedPreferences p = getContext().getSharedPreferences(PREFS, Context.MODE_PRIVATE);
        String raw = p.getString(ORDER, "");
        List<String> result = new ArrayList<>();
        if (raw != null && !raw.isEmpty()) {
            for (String n : raw.split(SEP, -1)) if (!n.isEmpty() && !result.contains(n)) result.add(n);
        }
        for (Station s : Stations.ALL) if (!result.contains(s.name)) result.add(s.name);
        return result;
    }

    private void saveFullOrder(List<String> names) {
        getContext().getSharedPreferences(PREFS, Context.MODE_PRIVATE).edit().putString(ORDER, join(names)).apply();
    }

    private void applySavedOrder() {
        List<String> order = loadFullOrder();
        if (getChildCount() < 2) return;
        rebuilding = true;
        int wantedIndex = 0;
        for (String wantedName : order) {
            int found = -1;
            for (int i = wantedIndex; i < getChildCount(); i++) {
                Object tag = getChildAt(i).getTag(R.id.stationsGrid);
                if (wantedName.equals(tag)) { found = i; break; }
            }
            if (found >= 0) {
                if (found != wantedIndex) {
                    View v = getChildAt(found);
                    removeViewAt(found);
                    addView(v, wantedIndex);
                }
                wantedIndex++;
                if (wantedIndex >= getChildCount()) break;
            }
        }
        rebuilding = false;
    }

    private String join(List<String> names) {
        StringBuilder b = new StringBuilder();
        for (String n : names) { if (b.length() > 0) b.append(SEP); b.append(n); }
        return b.toString();
    }
}
