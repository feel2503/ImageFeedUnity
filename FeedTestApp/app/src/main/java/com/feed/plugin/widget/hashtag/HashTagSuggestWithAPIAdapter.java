package com.feed.plugin.widget.hashtag;

import android.content.Context;
import android.support.v7.widget.AppCompatTextView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;

import com.feed.plugin.R;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public class HashTagSuggestWithAPIAdapter extends ArrayAdapter<HashTagSuggestWithAPIAdapter.HashTag>{
    private HashTagFilter filter;
    private List<HashTag> suggests = new ArrayList<>();

    private LayoutInflater inflater;
    private CursorPositionListener listener;

    private String baseUrl = "http://34.85.93.94/";

    public HashTagSuggestWithAPIAdapter(Context context, int resource, List<HashTag> objects) {
        super(context, resource, objects);
        inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public interface CursorPositionListener {
        int currentCursorPosition();
    }

    public void setCursorPositionListener(CursorPositionListener listener) {
        this.listener = listener;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.hashtag_suggest_cell, null);
        }

        try {
            HashTag hashTag = getItem(position);

            AppCompatTextView tagName = (AppCompatTextView) convertView.findViewById(R.id.hash_tag_name);
            AppCompatTextView tagCount = (AppCompatTextView) convertView.findViewById(R.id.hash_tag_count);

            tagName.setText(hashTag.tag);
            tagCount.setText(hashTag.count);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return convertView;
    }

    @Override
    public int getCount() {
        return suggests.size();
    }

    @Override
    public HashTag getItem(int position) {
        return suggests.get(position);
    }

    @Override
    public Filter getFilter() {

        if (filter == null) {
            filter = new HashTagFilter();
        }

        return filter;
    }

    public class HashTagFilter extends Filter {

        private final Pattern pattern = Pattern.compile("[#＃]([Ａ-Ｚａ-ｚA-Za-z一-\u9FC60-9０-９ぁ-ヶｦ-ﾟー])+");

        private SuggestService service;

        public int start;
        public int end;

        public HashTagFilter() {

            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(baseUrl)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

            service = retrofit.create(SuggestService.class);
        }

        @Override
        public CharSequence convertResultToString(Object resultValue) {
            return String.format("#%s ", ((HashTag) resultValue).tag);
        }

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {

            FilterResults filterResults = new FilterResults();

            if (constraint != null) {

                suggests.clear();

                int cursorPosition = listener.currentCursorPosition();

                Matcher m = pattern.matcher(constraint.toString());
                while (m.find()) {

                    if (m.start() < cursorPosition && cursorPosition <= m.end()) {

                        start = m.start();
                        end = m.end();

                        String keyword = constraint.subSequence(m.start() + 1, m.end()).toString();
                        RequestCommand reqCmd = new RequestCommand(keyword, 0, 20);
                        RequestBody reqBody = new RequestBody("9a9429f39e7b2e4e2797474d037f02c778347657", 12, reqCmd);

                        Call<SuggestResponse> call = service.listHashTags(reqBody);
                        try {
                            //suggests = call.execute().body().results;
                            //ArrayList<String> result = call.execute().body().command.tags;
                            ResultCommand cmd = call.execute().body().command;
                            int a = 0;
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }

            filterResults.values = suggests;
            filterResults.count = suggests.size();

            return filterResults;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            if (results != null && results.count > 0) {
                notifyDataSetChanged();
            } else {
                notifyDataSetInvalidated();
            }
        }
    }

    public interface SuggestService {
        @POST("game/people/search")
        Call<SuggestResponse> listHashTags(@Body RequestBody body);
    }

    public class SuggestResponse {
        @Expose
        @SerializedName("accessToken")
        public String accessToken;

        @Expose
        @SerializedName("accountId")
        public int accountId;

        @Expose
        @SerializedName("version")
        public int version;

        @Expose
        @SerializedName("serverTimestamp")
        public long serverTimestamp;

        @Expose
        @SerializedName("tokenRegistTime")
        public int tokenRegistTime;

        @Expose
        @SerializedName("tokenUpdateTime")
        public int tokenUpdateTime;

        @Expose
        @SerializedName("tokenExpireTime")
        public int tokenExpireTime;

        @Expose
        @SerializedName("status")
        public int status;

        @Expose
        @SerializedName("command")
        public ResultCommand command;

        public SuggestResponse() {
        }
    }

    class ResultCommand {
        @Expose
        @SerializedName("tags")
        public ArrayList<String> tags;
    }

    public class HashTag {
        private final String tag;
        private final String count;

        public HashTag(String tag, String count) {
            this.tag = tag;
            this.count = count;
        }
    }

    public class RequestBody
    {
        @Expose
        @SerializedName("accessToken")
        public String accessToken;
        @Expose
        @SerializedName("version")
        public int version;
        @Expose
        @SerializedName("command")
        public RequestCommand command;

        public RequestBody(String accessToken, int version, RequestCommand command){
            this.accessToken = accessToken;
            this.version = version;
            this.command = command;
        }
    }

    class RequestCommand {
        @Expose
        @SerializedName("tags")
        public String tags;

        @Expose
        @SerializedName("limit")
        public int limit;

        @Expose
        @SerializedName("offset")
        public int offset;

        public RequestCommand(String tags, int limit, int offset){
            this.tags = tags;
            this.limit = limit;
            this.offset = offset;
        }
    }
}
