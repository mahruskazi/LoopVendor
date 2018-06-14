package com.team.socero.loopvendor;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by Mahrus Kazi on 2017-09-03.
 */

public class HeaderView extends View {

    //circle and text colors
    private int headerCol, labelCol;
    private int textSize;
    //label text
    private String headerText;
    //paint for drawing custom view
    private Paint headerPaint;

    private Path path = new Path();

    private Rect rect = new Rect();

    public HeaderView(Context context, AttributeSet attrs) {
        super(context, attrs);

        headerPaint = new Paint();
        TypedArray a = context.getTheme().obtainStyledAttributes(attrs,
                R.styleable.HeaderView, 0, 0);

        try {
            //get the text and colors specified using the names in attrs.xml
            headerText = a.getString(R.styleable.HeaderView_headerLabel);
            headerCol = a.getInteger(R.styleable.HeaderView_headerColor, 0);//0 is default
            labelCol = a.getInteger(R.styleable.HeaderView_labelColor, 0);
            String s = a.getString(R.styleable.HeaderView_textSize);

            if(s != null && s.substring(s.length()-2).contentEquals("sp")) {
                s = s.substring(0, s.length() - 2);
                textSize = (int) Double.parseDouble(s);
            }
            else
                textSize = 15;
        } finally {
            a.recycle();
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.getClipBounds(rect);
        int height = rect.height();
        int width = rect.width();

        headerPaint.setStyle(Paint.Style.FILL);
        headerPaint.setAntiAlias(true);

        headerPaint.setColor(headerCol);
        path.moveTo(0, 0);
        path.lineTo(width*3/4, 0);
        path.lineTo(width*2/3, height);
        path.lineTo(0, height);
        path.close();
        canvas.drawPath(path, headerPaint);

        float px = textSize * getResources().getDisplayMetrics().scaledDensity;

        headerPaint.setColor(labelCol);
        headerPaint.setTextAlign(Paint.Align.LEFT);
        headerPaint.setTextSize(px);

        headerPaint.getTextBounds(headerText, 0, headerText.length(), rect);
        float y = height/2f + rect.height()/2f - rect.bottom;
        canvas.drawText(headerText, width/20, y, headerPaint);

        super.onDraw(canvas);
    }
}
