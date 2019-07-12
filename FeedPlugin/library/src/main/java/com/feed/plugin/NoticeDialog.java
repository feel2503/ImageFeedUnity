package com.feed.plugin;

import android.app.Activity;
import android.graphics.Typeface;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

public class NoticeDialog extends Activity{

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        WindowManager.LayoutParams lpWindow = new WindowManager.LayoutParams();
        lpWindow.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        lpWindow.dimAmount = 0.75f;
        getWindow().setAttributes(lpWindow);

        requestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.activity_notice_dialog);

        Typeface typeface = Typeface.createFromAsset(getAssets(), "RingsideWide-Semibold.otf");

        TextView textTitle = findViewById(R.id.notice_title);
        textTitle.setTypeface(typeface);

        TextView textContent = findViewById(R.id.notice_content);
        textContent.setTypeface(typeface);

        TextView textYes = findViewById(R.id.text_yes);
        textYes.setTypeface(typeface);
        textYes.setOnClickListener(mOnClickListener);

        TextView textNo = findViewById(R.id.text_no);
        textNo.setTypeface(typeface);
        textNo.setOnClickListener(mOnClickListener);
    }


    @Override
    public void setRequestedOrientation(int requestedOrientation){
        //
        if (Build.VERSION.SDK_INT != Build.VERSION_CODES.O) {
            super.setRequestedOrientation(requestedOrientation);
        }

    }

    private View.OnClickListener mOnClickListener = new View.OnClickListener(){
        @Override
        public void onClick(View v){
            if(v.getId() == R.id.text_yes)
            {
                setResult(Activity.RESULT_OK);
                finish();
            }
            else if(v.getId() == R.id.text_no)
            {
                setResult(Activity.RESULT_CANCELED);
                finish();
            }

        }
    };
}
