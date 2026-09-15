package com.master.radiomaroc;

import android.content.Context;
import android.util.AttributeSet;

/** Station grid with idle layout transitions disabled to prevent screen jitter. */
public final class StableStationGrid extends ReorderableStationGrid {
    public StableStationGrid(Context context) { super(context); stable(); }
    public StableStationGrid(Context context, AttributeSet attrs) { super(context, attrs); stable(); }
    public StableStationGrid(Context context, AttributeSet attrs, int defStyleAttr) { super(context, attrs, defStyleAttr); stable(); }
    private void stable() { setLayoutTransition(null); }
}
