package hu.nye.szakacskonyv;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
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

public class RecipeActivity extends AppCompatActivity implements NavActivitiesInterface{

    ListAdapter ing_adapter;
    StepsAdapter desc_adapter;
    RecyclerView ing_recyclerView;
    RecyclerView desc_recyclerView;
    TextView food_title;
    TextView description;

    ActionBarDrawerToggle toggle;
    DrawerLayout drawerLayout;
    NavigationView navView;
    Toolbar customToolbar;
    ImageButton searchButton;
    String data;
    Intent intent;
    String név;
    String email;
    int id;
    int recipe_id;
    List<String> ingredients;
    List<Steps> descriptions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe);

        description = findViewById(R.id.descriptionList);
        ing_recyclerView = findViewById(R.id.ingredientList);
        //desc_recyclerView = findViewById(R.id.descriptionList);
        searchButton = findViewById(R.id.searchButton);
        food_title = findViewById(R.id.card_title);

        intent = getIntent();
        Bundle extras = intent.getExtras();
        if(extras != null)
        {
            data = extras.getString("title");
        }

        NavigationView navView = (NavigationView)findViewById(R.id.navView);
        View headerView = navView.getHeaderView(0);
        TextView username = (TextView)headerView.findViewById(R.id.username_tv);
        TextView email_tv = (TextView)headerView.findViewById(R.id.email_tv);
        username.setText(intent.getStringExtra("name"));
        email_tv.setText(intent.getStringExtra("email"));
        id= intent.getIntExtra("id",0);
        recipe_id= intent.getIntExtra("recipe_id",0);
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openSearchActivity();
            }
        });
        System.out.println("Az átadott recipe_id: "+ recipe_id);
        ingredients = new ArrayList<>();
        descriptions = new ArrayList<>();

        //Étel nevének beállitása
        food_title.setText(data);

        //Hozzávalók lista feltöltése

        /*ingredients.add("1/2 kg marhahús");
        ingredients.add("2 gerezd fokhagyma");
        ingredients.add("2 téáskanál só");
        ingredients.add("1/2 tesákanál bors");
        ingredients.add("1 csomag tészta");

         */

        //Lépések lista feltöltése
        /*descriptions.add(new Steps("1","Forraljuk fel a vizet egy fazékban."));
        descriptions.add(new Steps("2","Tegyük bele a tésztát és főzzük meg."));
        descriptions.add(new Steps("3","Öntsük le a vizet a tésztáról."));
        descriptions.add(new Steps("4","Tegyük bele a főtt marhahúst, valamint a fűszereket."));
        descriptions.add(new Steps("5","Forraljuk fel a vizet egy fazékban."));
        descriptions.add(new Steps("6","Tegyük bele a tésztát és főzzük meg."));
        descriptions.add(new Steps("7","Öntsük le a vizet a tésztáról."));
        descriptions.add(new Steps("8","Tegyük bele a főtt marhahúst, valamint a fűszereket."));
        descriptions.add(new Steps("9","Forraljuk fel a vizet egy fazékban."));
        descriptions.add(new Steps("10","Tegyük bele a tésztát és főzzük meg."));
        descriptions.add(new Steps("11","Nagyon hosszú szöveg a tesztelés céljábol. Lorem Ipsum nada nada gdhkgksahg a ahsh shsfhfh hdezwz  hdfhdh dhdjdj."));
        descriptions.add(new Steps("12","Egyél!"));*/

        //Leírás text hozzáadása
        /*description.setText("Egy serpenyőben felhevítjük az olajat, amire aztán rádobjuk a hagymát, amit üvegesre párolunk. Erre jöhet majd a gomba is, amit ugyancsak szépen átpirítunk.\n" +
                "Ennél a pontnál lehet sózni, borsozni ízlésünknek megfelelően. Aztán a gombához adjuk hozzá a fokhagymát, pár percig pirítsuk így együtt az egészet. Majd szórjuk rá a lisztet, és keverjük jól el.\n" +
                "A következő pontnál szórjuk rá az apróra vágott petrezselymet, plusz öntsük rá a tejfölt is. Addig hagyjuk főzni, míg szép egynemű masszát kapunk.\n" +
                "Utolsó lépésként szórjuk rá a fűszerpaprikát, és keverjük jól el. Ennél a pontnál még kóstoljuk meg, és ha kell sózzuk-borsozzuk. Lehet is tálalni, és szerintem a legjobb, ha nokepdlivel esszük, úgy az igazi!");
         */
        description.setVisibility(View.GONE);
        ReceptLekérdezés lekérdezés = new ReceptLekérdezés();
        lekérdezés.execute();

        try {
            Thread.sleep((long) (0.75 * 1000));
        } catch (InterruptedException ie) {
            Thread.currentThread().interrupt();
        }
        createCustomToolbar();
        createNavigationDrawer();
        createIngredientRecyclerView();
        //createDescriptionRecyclerView();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(toggle.onOptionsItemSelected(item)){
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void onRadioButtonClicked(View view) {

        boolean checked = ((RadioButton) view).isChecked();

        switch(view.getId()) {
            case R.id.ingredients:
                if (checked)
                {
                    ing_recyclerView.setVisibility(View.VISIBLE);
                    //desc_recyclerView.setVisibility(View.GONE);
                    description.setVisibility(View.GONE);
                }
                break;
            case R.id.description:
                if (checked) {
                    ing_recyclerView.setVisibility(View.GONE);
                    //desc_recyclerView.setVisibility(View.VISIBLE);
                    description.setVisibility(View.VISIBLE);
                }
                    break;
        }
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

    public void createIngredientRecyclerView(){
        ing_recyclerView.setLayoutManager(new LinearLayoutManager(this));
        ing_adapter = new ListAdapter(this, ingredients);
        ing_recyclerView.setAdapter(ing_adapter);
    }

    public void createDescriptionRecyclerView(){
        desc_recyclerView.setLayoutManager(new LinearLayoutManager(this));
        desc_adapter = new StepsAdapter(this, descriptions);
        desc_recyclerView.setAdapter(desc_adapter);
        desc_recyclerView.setVisibility(View.GONE);
    }

    public void createCustomToolbar(){
        customToolbar = findViewById(R.id.customToolbarId);
        customToolbar.setTitle("");
        setSupportActionBar(customToolbar);
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
    public class ReceptLekérdezés extends AsyncTask<String,String,String> {


        @Override
        protected String doInBackground(String... strings) {
            HttpURLConnection kapcsolat = null;
            URL url;

            try {

                url = new URL("https://studentlab.nye.hu/~szakacskonyv/getrecipeid.php");
                kapcsolat = (HttpURLConnection)url.openConnection();
                kapcsolat.setDoInput(true);
                kapcsolat.setDoOutput(true);
                kapcsolat.setRequestMethod("POST");

                String adat  = URLEncoder.encode("recipe_id", "UTF-8")
                        + "=" + URLEncoder.encode(recipe_id+"", "UTF-8");


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
                    ingredients.add(szetvalaszt[i]);
                    description.setText(szetvalaszt[i+1]);
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