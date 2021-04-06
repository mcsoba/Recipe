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


public class MainActivity extends AppCompatActivity {

    Button login_button;
    Button register_button;
    EditText username;
    EditText password;
    Boolean flag=false;
    String email;
    String name;
    int id;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        username = findViewById(R.id.username);
        password = findViewById(R.id.password);
        login_button = findViewById(R.id.loginButton);
        register_button = findViewById(R.id.registerButton);
        register_button.setPaintFlags(register_button.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);

        //Bejelentkezés gombra kattintás
        login_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Login().execute();
                try {
                    Thread.sleep((long) (0.75 * 1000));
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                }
                if(flag==true){
                    Toast.makeText(getApplicationContext(),"Sikeres bejelentkezés!", Toast.LENGTH_SHORT).show();
                    openCategoriesActivity();
                }else{
                    Toast.makeText(getApplicationContext(),"Hibás adatok!", Toast.LENGTH_SHORT).show();
                }


            }
        });
        //Regisztrálj gombra kattintás
        register_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openRegisterActivity();
            }
        });

    }

    private void openCategoriesActivity(){
        Intent intent = new Intent(this, CategoriesActivity.class);

        intent.putExtra("name",name);
        intent.putExtra("email",email);
        intent.putExtra("id",id);
        startActivity(intent);
    }

    private void openRegisterActivity(){
        Intent intent = new Intent(this, RegisterActivity.class);
        startActivity(intent);
    }
    public class Login extends AsyncTask<String,String,String> {


        @Override
        protected String doInBackground(String... strings) {



            HttpURLConnection kapcsolat = null;
            URL url;
            String eredmeny = "";

            try {

                url = new URL("https://studentlab.nye.hu/~szakacskonyv/login.php");
                kapcsolat = (HttpURLConnection)url.openConnection();
                kapcsolat.setDoInput(true);
                kapcsolat.setDoOutput(true);
                kapcsolat.setRequestMethod("POST");

                String adat  = URLEncoder.encode("name", "UTF-8")
                        + "=" + URLEncoder.encode(username.getText().toString(), "UTF-8");

                adat += "&" + URLEncoder.encode("password", "UTF-8")
                        + "=" + URLEncoder.encode(password.getText().toString(), "UTF-8");
                OutputStreamWriter ki = new OutputStreamWriter(kapcsolat.getOutputStream());
                ki.write(adat);

                ki.flush();
                ki.close();

                BufferedReader reader = new BufferedReader(new
                        InputStreamReader(kapcsolat.getInputStream()));

                StringBuilder sb = new StringBuilder();
                String line = null;

                // Read Server Response
                while((line = reader.readLine()) != null) {
                    sb.append(line);
                    break;
                }
                String[] szetvalaszt = sb.toString().split("\\s+");
                id = Integer.parseInt(szetvalaszt[0]);
                name = szetvalaszt[1];
                email = szetvalaszt[2];
                System.out.println("Az id: "+id+" Név: "+name+" Email: "+email);
                if(sb.toString().contains("Siker")){
                    flag=true;
                    System.out.println("Ez az sb ------------>:"+sb.toString());
                }else{
                    flag=false;
                    System.out.println("Ez az sb ------------>:"+sb.toString());
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