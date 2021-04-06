package hu.nye.szakacskonyv;

import android.content.Intent;
import android.graphics.Paint;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;



public class RegisterActivity extends AppCompatActivity {


    Boolean flag=false;

    private Button registerButton;
    private Button loginButton;
    private EditText email;
    private EditText name;
    private EditText password;
    private EditText password_again;
    String uzenet = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        email = findViewById(R.id.email);
        name =findViewById(R.id.username);
        password = findViewById(R.id.password);
        password_again = findViewById(R.id.password_again);

        registerButton = findViewById(R.id.registerButton);
        loginButton = findViewById(R.id.toLoginButton);
        loginButton.setPaintFlags(loginButton.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                new Register().execute();
                try {
                    Thread.sleep((long) (0.75 * 1000));
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                }
                if(flag==true){
                    Toast.makeText(getApplicationContext(),"Sikeres regisztráció!", Toast.LENGTH_SHORT).show();
                    openLoginActivty();
                }else{
                    Toast.makeText(getApplicationContext(),uzenet, Toast.LENGTH_SHORT).show();
                }


            }
        });
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openLoginActivty();
            }
        });
    }

    private void openLoginActivty(){
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    public class Register extends AsyncTask<String,String,String>{


        @Override
        protected String doInBackground(String... strings) {



            HttpURLConnection kapcsolat = null;
            URL url;
            String eredmeny = "";

            try {

                url = new URL("https://studentlab.nye.hu/~szakacskonyv/create_user.php");
                kapcsolat = (HttpURLConnection)url.openConnection();
                kapcsolat.setDoInput(true);
                kapcsolat.setDoOutput(true);
                kapcsolat.setRequestMethod("POST");


                if(password.getText().toString().equals(password_again.getText().toString()) ){
                    if(password.getText().toString().length() > 7 && password.getText().toString().length() < 17) {
                        String adat = URLEncoder.encode("name", "UTF-8")
                                + "=" + URLEncoder.encode(name.getText().toString(), "UTF-8");

                        adat += "&" + URLEncoder.encode("password", "UTF-8")
                                + "=" + URLEncoder.encode(password.getText().toString(), "UTF-8");

                        adat += "&" + URLEncoder.encode("email", "UTF-8")
                                + "=" + URLEncoder.encode(email.getText().toString(), "UTF-8");

                        OutputStreamWriter ki = new OutputStreamWriter(kapcsolat.getOutputStream());
                        ki.write(adat);
                        System.out.println(name.getText().toString());
                        System.out.println(password.getText().toString());

                        ki.flush();
                        ki.close();

                        BufferedReader reader = new BufferedReader(new
                                InputStreamReader(kapcsolat.getInputStream()));

                        StringBuilder sb = new StringBuilder();
                        String line = null;

                        // Read Server Response
                        while ((line = reader.readLine()) != null) {
                            sb.append(line);
                            break;
                        }
                        if (sb.toString().equals("Már létező felhasználó")) {
                            flag = false;
                            uzenet = "Már létező felhasználó";
                            //Toast.makeText(getApplicationContext(),"Már létező felhasználó", Toast.LENGTH_SHORT).show();
                        } else {
                            flag = true;
                        }
                    }else{
                        uzenet = "Jelszó hosszúsága nem megfelelő (8-16)";
                    }
                }else{
                    uzenet = "A megadott jelszavak nem egyeznek meg!";
                    //Toast.makeText(getApplicationContext(),"A megadott jelszavak nem egyeznek meg!", Toast.LENGTH_SHORT).show();

                }


                if(kapcsolat.getResponseCode()!= HttpURLConnection.HTTP_OK){
                    return "Hiba a szerver elérésnél";
                }
            }catch (Exception ex){
                System.out.println(ex);
            }
            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
        }

        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);
        }
    }
}