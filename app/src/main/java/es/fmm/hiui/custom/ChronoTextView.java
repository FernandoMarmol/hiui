package es.fmm.hiui.custom;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

public class ChronoTextView extends TextView {

	public ChronoTextView(Context context, AttributeSet attrs, int defStyle) {
	    super(context, attrs, defStyle);
	    setTypeface(Typeface.createFromAsset(context.getAssets(), "fonts/watchTypeFace.ttf"));
    }
	
	public ChronoTextView(Context context, AttributeSet attrs) {
	    super(context, attrs);
	    setTypeface(Typeface.createFromAsset(context.getAssets(), "fonts/watchTypeFace.ttf"));
    }

	public ChronoTextView(Context context) {
	    super(context);
	    setTypeface(Typeface.createFromAsset(context.getAssets(), "fonts/watchTypeFace.ttf"));
    }
	
	protected void onDraw (Canvas canvas) {
        super.onDraw(canvas);
    }
	
}
