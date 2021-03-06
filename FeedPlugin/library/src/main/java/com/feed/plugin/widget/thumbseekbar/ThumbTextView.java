package com.feed.plugin.widget.thumbseekbar;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

public class ThumbTextView extends TextView{
    private LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
    private int width = 0;

    public ThumbTextView(Context context){
        super(context);
    }

    public ThumbTextView(Context context, AttributeSet attrs){
        super(context, attrs);
    }

    public void attachToSeekBar(SeekBar seekBar)
    {
        String content = getText().toString();
        if(TextUtils.isEmpty(content) || seekBar == null)
        {
            return;
        }

        float contentWidth = this.getPaint().measureText(content);
        int realWidth = width - seekBar.getPaddingLeft() - seekBar.getPaddingRight() + (int)(contentWidth/2);
        int maxLimit = (int) (width - contentWidth - seekBar.getPaddingRight()) + (int)(contentWidth/2);
        int minLimit = seekBar.getPaddingLeft() / 2;
        //int minLimit = 0;

        float percent = (float) (1.0 * seekBar.getProgress() / seekBar.getMax());

        int left = minLimit + (int) (realWidth * percent);
        //int left = minLimit + (int) (realWidth * percent - (contentWidth / 2.0));
        //int left = minLimit + (int) (realWidth * percent - contentWidth );
        left = left <= minLimit ? minLimit : left >= maxLimit ? maxLimit : left;
        lp.setMargins(left, 0, 0, 0);
        setLayoutParams(lp);

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec){
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if(width == 0)
            width = MeasureSpec.getSize(widthMeasureSpec);
    }
}
