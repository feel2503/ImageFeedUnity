package com.feed.plugin.widget.hashtag;

import android.content.Context;
import android.text.Editable;
import android.util.AttributeSet;
import android.widget.AutoCompleteTextView;

public class HashTagTextView extends AutoCompleteTextView  {

    public HashTagTextView(Context context) {
        super(context, null);
    }

    public HashTagTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public HashTagTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void replaceText(CharSequence text) {
        clearComposingText();

        HashTagSuggestWithAPIAdapter adapter = (HashTagSuggestWithAPIAdapter)getAdapter();
        HashTagSuggestWithAPIAdapter.HashTagFilter filter = (HashTagSuggestWithAPIAdapter.HashTagFilter) adapter.getFilter();

//        HashTagSuggestAdapter adapter = (HashTagSuggestAdapter) getAdapter();
//        HashTagSuggestAdapter.HashTagFilter filter = (HashTagSuggestAdapter.HashTagFilter) adapter.getFilter();

        Editable span = getText();
        span.replace(filter.start, filter.end, text);
    }
}
