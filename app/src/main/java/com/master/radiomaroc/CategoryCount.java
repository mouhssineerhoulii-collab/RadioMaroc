package com.master.radiomaroc;

/** Single source of truth for category counts shown by the UI. */
public final class CategoryCount {
    private CategoryCount() {}

    public static int of(String category) {
        if ("all".equals(category)) return Stations.ALL.size();
        int count = 0;
        for (Station station : Stations.ALL) {
            if (category != null && category.equals(station.category)) count++;
        }
        return count;
    }

    public static String badge(String category) {
        return "  [" + of(category) + "]";
    }
}
