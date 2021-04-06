package hu.nye.szakacskonyv;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
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

public class MyRecipesActivity extends AppCompatActivity implements NavActivitiesInterface, RecipeAdapter.OnItemListener{

    private  ActionBarDrawerToggle toggle;
    private DrawerLayout drawerLayout;
    private NavigationView navView;
    private Toolbar customToolbar;
    private ImageButton searchButton;
    private RecyclerView recyclerView;
    private RecipeAdapter recipeAdapter;
    private List<RecipeItem> mList;
    private ImageButton addButton;
    String név;
    String email;
    int id;
    ArrayList<String> lekértreceptek = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_recipes);

        recyclerView = findViewById(R.id.rv_List);
        searchButton = findViewById(R.id.searchButton);
        addButton = findViewById(R.id.addButton);

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openAddRecipeActivity();
            }
        });

        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openSearchActivity();
            }
        });

        mList = new ArrayList<>();

        Intent intent = getIntent();

        NavigationView navView = (NavigationView)findViewById(R.id.navView);
        View headerView = navView.getHeaderView(0);
        TextView username = (TextView)headerView.findViewById(R.id.username_tv);
        TextView email_tv = (TextView)headerView.findViewById(R.id.email_tv);
        username.setText(intent.getStringExtra("name"));
        email_tv.setText(intent.getStringExtra("email"));

        email = intent.getStringExtra("email");
        név = intent.getStringExtra("name");
        id = intent.getIntExtra("id",0);

        ReceptLekérdezés lekérdezés = new ReceptLekérdezés();
        lekérdezés.execute();

        System.out.println(mList.size());
        /*
        mList.add(new RecipeItem(R.drawable.category_reggeli, "Mákos izé"));
        mList.add(new RecipeItem(R.drawable.category_leves, "Saját halas szendvicsem"));
        mList.add(new RecipeItem(R.drawable.category_husetel, "Izébizé"));
        mList.add(new RecipeItem(R.drawable.category_haletel, "Nyekenyóka Tomi módra"));
        mList.add(new RecipeItem(R.drawable.category_teszta, "Elbaltázott tökfőzelék"));
        */

        try {
            Thread.sleep((long) (0.75 * 1000));
        } catch (InterruptedException ie) {
            Thread.currentThread().interrupt();
        }
        createCustomToolbar();
        createRecyclerView();
        createNavigationDrawer();



        System.out.println(intent.getStringExtra("name"));

    }

    private void createRecyclerView(){
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        recipeAdapter = new RecipeAdapter(this,mList,this);
        recyclerView.setAdapter(recipeAdapter);
    }

    public void createNavigationDrawer(){
        drawerLayout = findViewById(R.id.drawerLayout);
        toggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.open, R.string.close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        navView  = (NavigationView)findViewById(R.id.navView);
        navView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {

                switch (item.getItemId()){
                    case R.id.item_my_recipes:{
                        openMyRecipesActivity();
                        drawerLayout.closeDrawer(Gravity.LEFT, false);
                    }
                    break;
                    case R.id.item_categories:{
                        openCategoriesActivity();
                        drawerLayout.closeDrawer(Gravity.LEFT, false);
                    }
                    break;
                    case R.id.item_exit:{
                        logout();
                        drawerLayout.closeDrawer(Gravity.LEFT, false);
                    }
                    break;
                }
                return true;
            }
        });
    }

    public void createCustomToolbar(){
        customToolbar = findViewById(R.id.customToolbarId);
        customToolbar.setTitle("");
        setSupportActionBar(customToolbar);
    }

    public void openAddRecipeActivity() {
        Intent intent = new Intent(getApplicationContext(), AddRecipeActivity.class);
        intent.putExtra("name",név);
        intent.putExtra("email",email);
        intent.putExtra("id",id);
        startActivity(intent);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(toggle.onOptionsItemSelected(item)){
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void logout() {
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        finish();
        startActivity(intent);
    }

    @Override
    public void openSearchActivity() {
        Intent intent = new Intent(getApplicationContext(), SearchActivity.class);
        startActivity(intent);
    }

    @Override
    public void openCategoriesActivity() {
        Intent intent = new Intent(getApplicationContext(), CategoriesActivity.class);
        intent.putExtra("name",név);
        intent.putExtra("email",email);
        intent.putExtra("id",id);
        startActivity(intent);
    }

    @Override
    public void openMyRecipesActivity() {
        Intent intent = new Intent(getApplicationContext(), MyRecipesActivity.class);
        intent.putExtra("name",név);
        intent.putExtra("email",email);
        intent.putExtra("id",id);
        startActivity(intent);
    }

    @Override
    public void openFavoritesActivity() {
        //TODO
    }

    @Override
    public void onItemClick(int position) {
        Intent intent = new Intent(getApplicationContext(), RecipeActivity.class);
        intent.putExtra("recipe_id",mList.get(position).getRecipeId());
        intent.putExtra("title",mList.get(position).getTitle());
        intent.putExtra("name",név);
        intent.putExtra("email",email);
        intent.putExtra("id",id);
        startActivity(intent);
    }

    public class ReceptLekérdezés extends AsyncTask<String,String,String> {


        @Override
        protected String doInBackground(String... strings) {



            HttpURLConnection kapcsolat = null;
            URL url;

            try {

                url = new URL("https://studentlab.nye.hu/~szakacskonyv/getrecipe.php");
                kapcsolat = (HttpURLConnection)url.openConnection();
                kapcsolat.setDoInput(true);
                kapcsolat.setDoOutput(true);
                kapcsolat.setRequestMethod("POST");

                String adat  = URLEncoder.encode("user_id", "UTF-8")
                        + "=" + URLEncoder.encode(id+"", "UTF-8");


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
                    szetvalaszt = sb.toString().split("\\t");
                    break;
                }

                // Le kell kérdezni a receptek egyedi azonosítóját is és átadni az adott recept oldalának ahol pedig lekérjük az összes adatot ahol megegyezik az adott id így megkapjuk az étel összes leírását
                //A  RecipeItem osztályon belül kell még egy adattag ahol elároljuk az adatbázisból lekért azonosítót
                for (int i = 0; i < szetvalaszt.length; i+=2){


                    System.out.println("A lekért adatok: "+ szetvalaszt[i]);
                    lekértreceptek.add(szetvalaszt[i]);
                    mList.add(new RecipeItem(R.drawable.category_teszta, szetvalaszt[i+1],Integer.parseInt(szetvalaszt[i])));


                }

                for (int i = 0; i< lekértreceptek.size();i++){
                    System.out.println("Array list receptek: "+lekértreceptek.get(i));
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