package com.feed.plugin.widget.thumbseekbar;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.SeekBar;

import com.feed.plugin.R;

public class ThumbTextSeekBar extends LinearLayout{
    public ThumbTextView tvThumb;
    public SeekBar seekBar;
    private SeekBar.OnSeekBarChangeListener onSeekBarChangeListener;

    private boolean mIsCenterValue = false;

    public ThumbTextSeekBar(Context context) {
        super(context);
        init();
    }

    public ThumbTextSeekBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }


    private void init() {
        LayoutInflater.from(getContext()).inflate(R.layout.view_thumb_text_seekbar, this);
        setOrientation(LinearLayout.VERTICAL);
        tvThumb = (ThumbTextView) findViewById(R.id.tvThumb);
        seekBar = (SeekBar) findViewById(R.id.sbProgress);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                if (onSeekBarChangeListener != null)
                    onSeekBarChangeListener.onStopTrackingTouch(seekBar);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                if (onSeekBarChangeListener != null)
                    onSeekBarChangeListener.onStartTrackingTouch(seekBar);
            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (onSeekBarChangeListener != null)
                    onSeekBarChangeListener.onProgressChanged(seekBar, progress, fromUser);

                tvThumb.attachToSeekBar(seekBar);
            }
        });

    }

    public void setOnSeekBarChangeListener(SeekBar.OnSeekBarChangeListener l) {
        this.onSeekBarChangeListener = l;
    }

    public void initProgressValue(boolean isCenter)
    {
        mIsCenterValue = isCenter;
        if(isCenter)
        {
            setProgress(50);
        }
        else
        {
            setProgress(100);
        }

        tvThumb.post( new Runnable() {
            @Override
            public void run() {
                // your layout is now drawn completely , use it here.
                tvThumb.attachToSeekBar(seekBar);
            }
        });
    }

    public void setThumbText(String text) {
        if(mIsCenterValue)
        {
            try{
                int textValue = Integer.valueOf(text);
                int value = textValue - 50;
                String setValue = ""+value;
                tvThumb.setText(setValue);
            }catch(Exception e)
            {
                e.printStackTrace();
            }

        }
        else
        {
            tvThumb.setText(text);
        }

    }


    public void setProgress(int progress) {
        if (progress == seekBar.getProgress() && progress == 0) {
            seekBar.setProgress(1);
            seekBar.setProgress(0);
        } else {
            seekBar.setProgress(progress);
        }
    }
}
