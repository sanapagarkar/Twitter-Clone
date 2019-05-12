package com.parse.starter;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckedTextView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseAnonymousUtils;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.List;

public class UserList extends AppCompatActivity {

    ArrayList<String> users= new ArrayList<>();
    ArrayAdapter arrayAdapter;
    ListView listView;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater menuInflater = new MenuInflater(this);
        menuInflater.inflate(R.menu.mainmenu, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if(item.getItemId()==R.id.tweet)
        {
            AlertDialog.Builder builder= new AlertDialog.Builder(this);
            builder.setTitle("Send a tweet");
            final EditText tweetText = new EditText(this);
            builder.setView(tweetText);
            builder.setPositiveButton("Send", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {

                    ParseObject tweet= new ParseObject("Tweet");
                    tweet.put("username", ParseUser.getCurrentUser().getUsername());
                    tweet.put("tweet", tweetText.getText().toString());
                    tweet.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {

                            if(e==null)
                            {
                                Toast.makeText(UserList.this, "Tweet done", Toast.LENGTH_SHORT).show();
                            }
                            else
                            {
                                Toast.makeText(UserList.this, "Failed", Toast.LENGTH_SHORT).show();
                            }

                        }
                    });

                }
            });
            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {

                    dialogInterface.cancel();

                }
            });
            builder.show();
        }
        else if(item.getItemId()==R.id.logout)
        {
            ParseUser.logOut();
            Intent intent= new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
        }
        else if(item.getItemId()==R.id.feed)
        {
            Intent intent = new Intent(getApplicationContext(), Feed.class);
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_list);

        setTitle("follow");

        if(ParseUser.getCurrentUser().get("isFollowing")==null)
        {
            List<String> emptyList = new ArrayList<>();
            ParseUser.getCurrentUser().put("isFollowing", emptyList);
        }
        listView= (ListView) findViewById(R.id.listView);
        listView.setChoiceMode(AbsListView.CHOICE_MODE_MULTIPLE);
        arrayAdapter= new ArrayAdapter(this, android.R.layout.simple_list_item_checked, users);
        listView.setAdapter(arrayAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                CheckedTextView checkedTextView= (CheckedTextView) view;
                if(checkedTextView.isChecked()==true)
                {
                    Log.i("msg", "checked");
                    ParseUser.getCurrentUser().add("isFollowing",users.get(i));
                    ParseUser.getCurrentUser().saveInBackground();
                }
                else if(checkedTextView.isChecked()!=true)
                {
                    Log.i("msg", "not checked");
                    List<Object> alist=ParseUser.getCurrentUser().getList("isFollowing");
                    alist.remove(users.get(i));
                    //int n=alist.indexOf(users.get(i));
                    //alist.remove(n);
                    ParseUser.getCurrentUser().put("isFollowing", alist);
                    ParseUser.getCurrentUser().saveInBackground();
                }

            }
        });

        users.clear();

        ParseQuery<ParseUser> query = ParseUser.getQuery();
        query.whereNotEqualTo("username", ParseUser.getCurrentUser().getUsername());
        query.findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> objects, ParseException e) {

                if(e==null)
                {
                    if(objects.size()>0)
                    {
                        for(ParseUser user : objects)
                        {
                            users.add(user.getUsername());
                        }
                        arrayAdapter.notifyDataSetChanged();
                        for(String username : users){
                            if(ParseUser.getCurrentUser().getList("isFollowing").contains(username))
                            {
                                listView.setItemChecked(users.indexOf(username), true);
                            }
                        }
                    }
                }

            }
        });


    }
}
