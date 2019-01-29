package com.feed.plugin.widget;

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

        HashTagSuggestAdapter adapter = (HashTagSuggestAdapter) getAdapter();
        HashTagSuggestAdapter.HashTagFilter filter = (HashTagSuggestAdapter.HashTagFilter) adapter.getFilter();

        Editable span = getText();
        span.replace(filter.start, filter.end, text);
    }
}
