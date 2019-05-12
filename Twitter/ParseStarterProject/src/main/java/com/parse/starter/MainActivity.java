/*
 * Copyright (c) 2015-present, Parse, LLC.
 * All rights reserved.
 *
 * This source code is licensed under the BSD-style license found in the
 * LICENSE file in the root directory of this source tree. An additional grant
 * of patent rights can be found in the PATENTS file in the same directory.
 */
package com.parse.starter;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

import com.parse.LogInCallback;
import com.parse.Parse;
import com.parse.ParseAnalytics;
import com.parse.ParseAnonymousUtils;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.parse.SignUpCallback;


public class MainActivity extends AppCompatActivity {

  EditText username;
  EditText password;
  Button button;

  public void redirect()
  {
      if (ParseUser.getCurrentUser()!=null) {
          Intent intent= new Intent(getApplicationContext(), UserList.class);
          startActivity(intent);
      }
  }

  public void onClick(View view){
      if(username.getText().toString().matches("") || password.getText().toString().matches("")){
          Toast.makeText(this, "Username and password required", Toast.LENGTH_SHORT).show();
      }
      else
      {
          ParseUser.logInInBackground(username.getText().toString(), password.getText().toString(), new LogInCallback() {
              @Override
              public void done(ParseUser user, ParseException e) {

                  if(user != null && e== null)
                  {
                      Toast.makeText(MainActivity.this, "Logged in", Toast.LENGTH_SHORT).show();
                      Log.i("msg", "user logged in");
                      redirect();
                  }else
                  {
                      ParseUser parseUser= new ParseUser();
                      parseUser.setUsername(username.getText().toString());
                      parseUser.setPassword(password.getText().toString());
                      parseUser.signUpInBackground(new SignUpCallback() {
                          @Override
                          public void done(ParseException e) {

                              if(e==null)
                              {
                                  Toast.makeText(MainActivity.this, "Signed in", Toast.LENGTH_SHORT).show();
                                  Log.i("msg", "sign up");
                                  redirect();
                              }
                              else{
                                  Toast.makeText(MainActivity.this, e.getMessage().substring(e.getMessage().indexOf(" ")), Toast.LENGTH_SHORT).show();
                              }

                          }
                      });
                  }
              }
          });

  }}

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    setTitle("Twitter:Login");
    redirect();
    username=(EditText) findViewById(R.id.editText1);
    password=(EditText) findViewById(R.id.editText2);
    button=(Button) findViewById(R.id.button);

    ParseAnalytics.trackAppOpenedInBackground(getIntent());
  }

}