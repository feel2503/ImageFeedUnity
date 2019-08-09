package com.feed.plugin.widget.hashtag;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v7.widget.AppCompatTextView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.TextView;

import com.feed.plugin.BridgeCls;
import com.feed.plugin.R;
import com.google.gson.Gson;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
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

public class HashTagSuggestWithAPIAdapter extends ArrayAdapter<String>{
    private HashTagFilter filter;
    private List<String> suggests = new ArrayList<>();
    private LayoutInflater inflater;
    private CursorPositionListener listener;
    private Gson mGson;

    private String tagUrl = "http://34.85.93.94:8081/game/tag/search";
    private String peopleUrl = "http://34.85.93.94:8081/game/people/search";

    private String tokenValue = "9a9429f39e7b2e4e2797474d037f02c778347657";
    private int versionValue = 12;
    private int mResource;

    private Typeface mTypeFace;

    public HashTagSuggestWithAPIAdapter(Context context, int resource) {
        super(context, resource);
        mResource = resource;
        mGson = new Gson();
        inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if(BridgeCls.mTagUrl != null && BridgeCls.mTagUrl.length() > 0)
            tagUrl = BridgeCls.mTagUrl;

        if(BridgeCls.mPeopleUrl != null && BridgeCls.mPeopleUrl.length() > 0)
            peopleUrl = BridgeCls.mPeopleUrl;

        if(BridgeCls.mTokenValue != null && BridgeCls.mTokenValue.length() > 0)
            tokenValue = BridgeCls.mTokenValue;

        mTypeFace = Typeface.createFromAsset(context.getAssets(), "RingsideWide-Semibold.otf");
    }

    public interface CursorPositionListener {
        int currentCursorPosition();
    }

    public void setCursorPositionListener(CursorPositionListener listener) {
        this.listener = listener;
    }

    @Override
    public int getCount() {
        return suggests.size();
    }

    @Override
    public String getItem(int position) {
        return suggests.get(position);
    }

    @Override
    public Filter getFilter() {

        if (filter == null) {
            filter = new HashTagFilter();
        }

        return filter;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            convertView = inflater.inflate(mResource, null);
        }

        try {
            String hashTag = getItem(position);

            TextView tagName = (TextView) convertView.findViewById(R.id.text_tag);
            tagName.setTypeface(mTypeFace);
            tagName.setText(hashTag);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return convertView;
    }



    public class HashTagFilter extends Filter {

        private final Pattern pattern = Pattern.compile("[#＃@]([Ａ-Ｚａ-ｚA-Za-z一-\u9FC60-9０-９ぁ-ヶｦ-ﾟー])+");

        public int start;
        public int end;
        public String tagValue = "";

        public HashTagFilter() {
        }

        @Override
        public CharSequence convertResultToString(Object resultValue) {
            //return String.format("#%s ", resultValue);
            String prefix = "#";
            if(tagValue.equalsIgnoreCase("#"))
                prefix = "#";
            else if(tagValue.equalsIgnoreCase("@"))
                prefix = "@";
            return prefix + resultValue.toString() + " ";
        }

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {

            FilterResults filterResults = new FilterResults();

            // limit 10 ch
            if (constraint != null && constraint.length() < 12)
            {
                suggests.clear();

                int cursorPosition = listener.currentCursorPosition();

                Matcher m = pattern.matcher(constraint.toString());
                while (m.find()) {
                    if (m.start() < cursorPosition && cursorPosition <= m.end()) {
                        String url = tagUrl;
                        start = m.start();
                        end = m.end();

                        tagValue = constraint.subSequence(m.start() , m.start()+1).toString();
                        if(tagValue.equalsIgnoreCase("#"))
                            url = tagUrl;
                        else if(tagValue.equalsIgnoreCase("@"))
                            url = peopleUrl;

                        String keyword = constraint.subSequence(m.start() + 1, m.end()).toString();
                        RequestCommand reqCmd = new RequestCommand(keyword, 0, 20);
                        RequestBody reqBody = new RequestBody(tokenValue, versionValue, reqCmd);
                        String reqPost = mGson.toJson(reqBody);

                        String resultStr = requestPost(url, reqPost);
                        SuggestResponse respons = mGson.fromJson(resultStr, SuggestResponse.class);
                        suggests = respons.command.tags;
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

    public String requestPost(String addr, String param)
    {
        String result = "";
        try{
            URL url = new URL(addr);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            if(conn!=null)
            {
                conn.setConnectTimeout(10000);
                conn.setRequestMethod("POST");
                conn.setUseCaches(false);
                conn.setDoInput(true);
                conn.setDoOutput(true);
                conn.setRequestProperty("Content-Type","application/json");

                byte[] outputInBytes = param.getBytes("UTF-8");
                OutputStream os = conn.getOutputStream();
                os.write( outputInBytes );
                os.close();

                if(conn.getResponseCode() == HttpURLConnection.HTTP_OK)
                {
                    BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(),"utf-8"));
                    String line;
                    StringBuffer response = new StringBuffer();
                    while((line = br.readLine()) != null) {
                        response.append(line);
                    }
                    br.close();

                    result = response.toString();
                }
                conn.disconnect();
            }
        }catch(Exception ex)
        {
            ex.printStackTrace();
        }
        return result;

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
        @SerializedName("tag")
        public String tag;

        @Expose
        @SerializedName("limit")
        public int limit;

        @Expose
        @SerializedName("offset")
        public int offset;

        public RequestCommand(String tag, int limit, int offset){
            this.tag = tag;
            this.limit = limit;
            this.offset = offset;
        }
    }
}
