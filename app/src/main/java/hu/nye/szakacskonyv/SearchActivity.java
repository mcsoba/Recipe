package hu.nye.szakacskonyv;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.navigation.NavigationView;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

public class SearchActivity extends AppCompatActivity implements RecipeAdapter.OnItemListener{

    Toolbar customToolbar;
    ImageButton backButton;
    SearchView searchView;
    RecyclerView recyclerView;
    RecipeAdapter recipeAdapter;
    List<RecipeItem> mList;
    String név;
    String email;
    int id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        Intent intent = getIntent();

        id = intent.getIntExtra("id",0);
        név = intent.getStringExtra("name");
        email = intent.getStringExtra("email");

        recyclerView = findViewById(R.id.rv_List);
        searchView = findViewById(R.id.search);
        backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openLastActivity();
            }
        });
        mList = new ArrayList<>();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                mList.clear();
                //Csak enter lenyomása után keres az adatbázisban
                //Adatbázisból kinyert receptekkel feltölti a listát
                Toast.makeText(getApplicationContext(), query, Toast.LENGTH_SHORT).show();
                Keresés keresés = new Keresés();
                keresés.execute(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                //VAGY Minden egyes uj karakter megadásánál keres az adatbázisban
                //Adatbázisból kinyert receptekkel feltölti a listát
                //Toast.makeText(getApplicationContext(), newText, Toast.LENGTH_SHORT).show();
                return true;
            }
        });

        //ebbe a listába kell majd feltölteni a recepteket
        //mList.add(new RecipeItem(kép, Recept neve)); módon


        createRecycleView();
        createCustomToolbar();
    }

    private void createRecycleView(){
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        recipeAdapter = new RecipeAdapter(this,mList,this);
        recyclerView.setAdapter(recipeAdapter);
    }

    public void createCustomToolbar(){
        customToolbar = findViewById(R.id.customToolbarId);
        customToolbar.setTitle("");
        setSupportActionBar(customToolbar);
    }

    public void openLastActivity(){
        finish();
    }

    @Override
    public void onItemClick(int position) {
        Intent intent = new Intent(getApplicationContext(), RecipeActivity.class);
        intent.putExtra("title",mList.get(position).getTitle());
        intent.putExtra("recipe_id",mList.get(position).getRecipeId());
        intent.putExtra("name",név);
        intent.putExtra("email",email);
        intent.putExtra("id",id);
        startActivity(intent);
    }

    public class Keresés extends AsyncTask<String,String,String> {

        @Override
        protected String doInBackground(String... strings) {
            HttpURLConnection kapcsolat = null;
            URL url;
            String keresendo = strings[0];
            try {

                url = new URL("https://studentlab.nye.hu/~szakacskonyv/search.php");
                kapcsolat = (HttpURLConnection)url.openConnection();
                kapcsolat.setDoInput(true);
                kapcsolat.setDoOutput(true);
                kapcsolat.setRequestMethod("POST");

                String adat  = URLEncoder.encode("recipe_name", "UTF-8")
                        + "=" + URLEncoder.encode(keresendo, "UTF-8");

                System.out.println(adat);
                OutputStreamWriter ki = new OutputStreamWriter(kapcsolat.getOutputStream());
                ki.write(adat);

                ki.flush();
                ki.close();

                BufferedReader reader = new BufferedReader(new
                        InputStreamReader(kapcsolat.getInputStream()));

                StringBuilder sb = new StringBuilder();
                String line = null;
                String[] szetvalaszt = null;
                // Read Server Response
                while((line = reader.readLine()) != null) {

                    sb.append(line);
                    System.out.println("Adat: "+sb.append(line));
                    szetvalaszt = sb.toString().split("\\s+");
                    break;
                }


                // Le kell kérdezni a receptek egyedi azonosítóját is és átadni az adott recept oldalának ahol pedig lekérjük az összes adatot ahol megegyezik az adott id így megkapjuk az étel összes leírását
                //A  RecipeItem osztályon belül kell még egy adattag ahol elároljuk az adatbázisból lekért azonosítót
                for (int i = 0; i < szetvalaszt.length/2; i++){
                    mList.add(new RecipeItem(R.drawable.category_teszta, szetvalaszt[i],Integer.parseInt(szetvalaszt[i])));
                    System.out.println("A recept hozzáadva");
                    System.out.println("Lista mérete: "+ mList.size());

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
