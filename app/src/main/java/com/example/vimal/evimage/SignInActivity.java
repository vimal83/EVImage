package com.example.vimal.evimage;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.vimal.evimage.CommonUtils.Globals;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Iterator;

import javax.net.ssl.HttpsURLConnection;

public class SignInActivity extends AppCompatActivity {

    EditText emailBox, passwordBox;
    Button loginButton;
//    TextView registerLink;
    String Email,Password;

    String URLPath = "http://amazons3api-dev.us-west-2.elasticbeanstalk.com/api/user/Login";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        emailBox = (EditText)findViewById(R.id.emailBox);
        Email=emailBox.getText().toString();
        passwordBox = (EditText)findViewById(R.id.passwordBox);
        Password=passwordBox.getText().toString();
        loginButton = (Button)findViewById(R.id.loginButton);
        //registerLink = (TextView)findViewById(R.id.registerLink);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (emailBox.getText().toString().trim().isEmpty()) {
                    emailBox.setError("Enter EmailID");
                    emailBox.requestFocus();
                    return;
                }
                if (passwordBox.getText().toString().trim().isEmpty()) {
                    passwordBox.setError("Enter Password");
                    passwordBox.requestFocus();
                    return;
                }
                new SendPostRequest().execute();


            }
        });

        /*registerLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SignInActivity.this, Register.class));
            }
        });*/
    }

    public class SendPostRequest extends AsyncTask<String, Void, String> {

        protected void onPreExecute(){}

        protected String doInBackground(String... arg0) {

            try {

                URL url = new URL(URLPath); // here is your URL path

                JSONObject postDataParams = new JSONObject();
                postDataParams.put("EmailID", emailBox.getText().toString());
                postDataParams.put("password", passwordBox.getText());
                Log.e("params",postDataParams.toString());

                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(15000);
                conn.setConnectTimeout(15000);
                conn.setRequestMethod("POST");
                conn.setDoInput(true);
                conn.setDoOutput(true);

                OutputStream os = conn.getOutputStream();
                BufferedWriter writer = new BufferedWriter(
                        new OutputStreamWriter(os, "UTF-8"));
                writer.write(getPostDataString(postDataParams));

                writer.flush();
                writer.close();
                os.close();

                int responseCode=conn.getResponseCode();

                if (responseCode == HttpsURLConnection.HTTP_OK) {

                    BufferedReader in=new BufferedReader(new
                            InputStreamReader(
                            conn.getInputStream()));

                    StringBuffer sb = new StringBuffer("");
                    String line="";

                    while((line = in.readLine()) != null) {

                        sb.append(line);
                        break;
                    }

                    in.close();
                    Log.i("VOLLEY return", sb.toString());
                    Globals AuthKeyVal = Globals.getInstance();
                    AuthKeyVal.setData(sb.toString());

                    //Change authkey store sharepreference to intent calling method
                    /*Intent myIntent = new Intent(this, NewActivityClassName.class);
                    myIntent.putExtra("firstKeyName","FirstKeyValue");
                    myIntent.putExtra("secondKeyName","SecondKeyValue");
                    startActivity(myIntent);

                    */

                    SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", 0);
                    SharedPreferences.Editor editor = pref.edit();
                    editor.putString("AuthKey", sb.toString());
                    editor.putString("View", "List");
                    editor.apply();


                    Log.i("shared preferences", pref.getString("AuthKey","No Data"));
                    return sb.toString();

                }
                else {
                    Log.i("VOLLEY status",new String("false : "+responseCode) );
                    return new String("false : "+responseCode);
                }
            }
            catch(Exception e){
                Log.i("VOLLEY status",new String("Exception: " + e.getMessage()));
                return new String("Exception: " + e.getMessage());
            }

        }

        @Override
        protected void onPostExecute(String result) {
            Toast.makeText(getApplicationContext(), result,
                    Toast.LENGTH_LONG).show();
            startActivity(new Intent(SignInActivity.this,Home.class));
        }
    }

    public String getPostDataString(JSONObject params) throws Exception {

        StringBuilder result = new StringBuilder();
        boolean first = true;

        Iterator<String> itr = params.keys();

        while(itr.hasNext()){

            String key= itr.next();
            Object value = params.get(key);

            if (first)
                first = false;
            else
                result.append("&");

            result.append(URLEncoder.encode(key, "UTF-8"));
            result.append("=");
            result.append(URLEncoder.encode(value.toString(), "UTF-8"));

        }
        return result.toString();
    }
}
