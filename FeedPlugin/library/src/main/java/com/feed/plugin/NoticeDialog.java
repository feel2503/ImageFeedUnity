package com.feed.plugin;

import android.app.Activity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

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

        findViewById(R.id.text_yes).setOnClickListener(mOnClickListener);
        findViewById(R.id.text_no).setOnClickListener(mOnClickListener);
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
