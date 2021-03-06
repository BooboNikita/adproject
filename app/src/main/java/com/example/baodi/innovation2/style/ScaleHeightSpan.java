package com.example.baodi.innovation2.style;

import android.graphics.Paint;
import android.text.style.LineHeightSpan;

/**
 * ScaleHeightSpan
 */
public class ScaleHeightSpan implements LineHeightSpan {

    private float scale;

    public ScaleHeightSpan(float scale) {
        this.scale = scale;
    }

    @Override
    public void chooseHeight(CharSequence text, int start, int end, int spanstartv, int v, Paint.FontMetricsInt fm) {
        fm.ascent *= scale;
        fm.top *= scale;
        fm.descent *= scale;
        fm.bottom *= scale;
    }

}
