
package com.solaredge.utils;

import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

public class VUtils {

    public static final int ALPHA_OPAQUE = 255;
    public static final int ALPHA_TRANSLUCECNT = 64;

    public static void disableView(View view) {
        disableView(view, true);
    }

    public static void disableView(View view, boolean transparent) {
        if (view != null) {
            view.setEnabled(false);

            if (transparent) {
                if (view.getBackground() != null) {
                    view.getBackground().mutate().setAlpha(ALPHA_TRANSLUCECNT);
                }

                if (view instanceof ImageView) {
                    if (((ImageView) view).getDrawable() != null) {
                        ((ImageView) view).getDrawable().mutate().setAlpha(ALPHA_TRANSLUCECNT);
                    }
                } else if (view instanceof TextView) {
                    ((TextView) view).setTextColor(((TextView) view).getTextColors().withAlpha(
                            ALPHA_TRANSLUCECNT));
                }
            }

            view.setEnabled(false);
        }
    }

    public static void disableViewGroup(ViewGroup group) {
        disableViewGroup(group, true);
    }

    public static void disableViewGroup(ViewGroup group, boolean transparent) {
        if (group == null) {
            return;
        }

        int childCount = group.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View view = group.getChildAt(i);
            if (view instanceof ViewGroup) {
                disableViewGroup((ViewGroup) view);
            } else {
                disableView(view);
            }

        }

        group.setEnabled(false);
    }

    public static void enableViewGroup(ViewGroup group) {
        if (group == null) {
            return;
        }

        int childCount = group.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View view = group.getChildAt(i);
            if (view instanceof ViewGroup) {
                enableViewGroup((ViewGroup) view);
            } else {
                enableView(view);
            }
        }

        group.setEnabled(true);
    }

    public static void enableView(View view) {
        if (view != null) {
            view.setEnabled(true);
            if (view.getBackground() != null) {
                view.getBackground().mutate().setAlpha(ALPHA_OPAQUE);
            }

            if (view instanceof ImageView) {
                if (((ImageView) view).getDrawable() != null) {
                    ((ImageView) view).getDrawable().mutate().setAlpha(ALPHA_OPAQUE);
                }
            } else if (view instanceof TextView) {
                ((TextView) view).setTextColor(((TextView) view).getTextColors().withAlpha(
                        ALPHA_OPAQUE));
            }
        }
    }
}
