package com.parse.starter;

import android.Manifest;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import org.w3c.dom.ls.LSException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Feed extends AppCompatActivity {

    ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed);

        listView= (ListView) findViewById(R.id.listView);

        ParseQuery<ParseObject> query = ParseQuery.getQuery("Tweet");
        query.whereContainedIn("username", ParseUser.getCurrentUser().getList("isFollowing"));
        query.orderByDescending("createdAt");
        query.setLimit(20);
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {

                if(e==null)
                {
                    if(objects.size()>0)
                    {
                        List<Map<String, String>> feedList= new ArrayList<Map<String, String>>();
                        for(ParseObject tweet : objects)
                        {
                                Map<String,String> tweetInfo = new HashMap<>();
                                tweetInfo.put("content", tweet.get("tweet").toString());
                                tweetInfo.put("username", tweet.get("username").toString());
                                feedList.add(tweetInfo);
                            }
                            SimpleAdapter simpleAdapter = new SimpleAdapter(Feed.this, feedList, android.R.layout.simple_list_item_2, new String[]{"content", "username"}, new int[]{android.R.id.text1, android.R.id.text2});
                            listView.setAdapter(simpleAdapter);
                        }
                    }
                }
        });
    }
}
