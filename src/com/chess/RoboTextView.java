package com.chess;


import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

import java.io.Serializable;

public class RoboTextView extends TextView implements Serializable {

	private static final long serialVersionUID = -2417945858405913303L;
	public static final String MAIN_FONT = "fonts/trebuc-";

	private String ttfName = "Regular";

	public RoboTextView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
        setupFont(attrs);
	}

	public RoboTextView(Context context) {
		super(context);
	}

	public RoboTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
        setupFont(attrs);
    }

    private void setupFont(AttributeSet attrs) {
        TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.RobotoTextView);

        final int N = a.getIndexCount();
        for (int i = 0; i < N; i++) {
            int attr = a.getIndex(i);
            switch (attr) {
                case R.styleable.RobotoTextView_ttf: {
                    ttfName = a.getString(i);
                }
                break;
            }
        }
        init();
    }

    private void init() {
        Typeface font = Typeface.createFromAsset(getContext().getAssets(), MAIN_FONT + ttfName + ".ttf");
        setTypeface(font);
    }

	@Override
	public void setTypeface(Typeface tf) {
		super.setTypeface(tf);
	}

}
