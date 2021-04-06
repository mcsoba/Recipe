package hu.nye.szakacskonyv;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
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
import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import static android.webkit.URLUtil.decode;

public class CategoryRecipesActivity extends AppCompatActivity implements NavActivitiesInterface, RecipeAdapter.OnItemListener{

    ActionBarDrawerToggle toggle;
    DrawerLayout drawerLayout;
    NavigationView navView;
    Toolbar customToolbar;
    ImageButton searchButton;
    RecyclerView recyclerView;
    RecipeAdapter recipeAdapter;
    List<RecipeItem> mList;
    TextView title;
    Intent intent;
    Button gomb;
    String név;
    String email;
    int id;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category_recipes);
        recyclerView = findViewById(R.id.rv_List);
        searchButton = findViewById(R.id.searchButton);
        title = findViewById(R.id.category_title);
        mList = new ArrayList<>();
        mList.clear();
        intent = getIntent();
        String data;
        Bundle extras = intent.getExtras();

        if(extras != null)
        {
             data = extras.getString("title");
             title.setText(data);
        }


        NavigationView navView = (NavigationView)findViewById(R.id.navView);
        View headerView = navView.getHeaderView(0);
        TextView username = (TextView)headerView.findViewById(R.id.username_tv);
        TextView email_tv = (TextView)headerView.findViewById(R.id.email_tv);
        username.setText(intent.getStringExtra("name"));
        email_tv.setText(intent.getStringExtra("email"));

        email = intent.getStringExtra("email");
        név = intent.getStringExtra("name");
        id =intent.getIntExtra("id",0);
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openSearchActivity();
            }
        });


        System.out.println(title.getText().toString());




        /*mList.add(new RecipeItem(R.drawable.category_reggeli, "Mákos izé"));
        mList.add(new RecipeItem(R.drawable.category_leves, "Halas szendvics"));
        mList.add(new RecipeItem(R.drawable.category_husetel, "Izébizé"));
        mList.add(new RecipeItem(R.drawable.category_haletel, "Nyekenyóka Tomi módra"));
        mList.add(new RecipeItem(R.drawable.category_teszta, "Rántott pityerke"));
        mList.add(new RecipeItem(R.drawable.category_reggeli, "Mákos izé"));
        mList.add(new RecipeItem(R.drawable.category_leves, "Halas szendvics"));
        mList.add(new RecipeItem(R.drawable.category_husetel, "Izébizé"));
        mList.add(new RecipeItem(R.drawable.category_haletel, "Nyekenyóka Tomi módra"));
        mList.add(new RecipeItem(R.drawable.category_teszta, "Rántott pityerke"));
        mList.add(new RecipeItem(R.drawable.category_reggeli, "Mákos izé"));
        mList.add(new RecipeItem(R.drawable.category_leves, "Halas szendvics"));
        mList.add(new RecipeItem(R.drawable.category_husetel, "Izébizé"));
        mList.add(new RecipeItem(R.drawable.category_haletel, "Nyekenyóka Tomi módra"));
        mList.add(new RecipeItem(R.drawable.category_teszta, "Rántott pityerke"));
        */
        ReceptLekérdezés lekérdezés = new ReceptLekérdezés();
        lekérdezés.execute();
        try {
            Thread.sleep((long) (0.75 * 1000));
        } catch (InterruptedException ie) {
            Thread.currentThread().interrupt();
        }
        createCustomToolbar();
        createRecycleView();
        createNavigationDrawer();

    }

    private void createRecycleView(){
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

                url = new URL("https://studentlab.nye.hu/~szakacskonyv/getcategory.php");
                kapcsolat = (HttpURLConnection)url.openConnection();
                kapcsolat.setDoInput(true);
                kapcsolat.setDoOutput(true);
                kapcsolat.setRequestMethod("POST");

                String adat  = URLEncoder.encode("category", "UTF-8")
                        + "=" + URLEncoder.encode(title.getText().toString(), "UTF-8");

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
                    szetvalaszt = sb.toString().split("\\t");
                    break;
                }


                // Le kell kérdezni a receptek egyedi azonosítóját is és átadni az adott recept oldalának ahol pedig lekérjük az összes adatot ahol megegyezik az adott id így megkapjuk az étel összes leírását
                //A  RecipeItem osztályon belül kell még egy adattag ahol elároljuk az adatbázisból lekért azonosítót
                for (int i = 0; i < szetvalaszt.length/2; i+=2){
                    mList.add(new RecipeItem(R.drawable.category_teszta, szetvalaszt[i+1],Integer.parseInt(szetvalaszt[i])));
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


    /*public Bitmap getImage() {
    //Adatbázisból megfelelő elem kiválasztása
        Intent i = getIntent();
        String email = i.getStringExtra("email");
        Cursor c = db.getAvatar(email);
        c.moveToFirst();
//byte array bitmappá alakítása
        ByteArrayInputStream inputStream = new ByteArrayInputStream(c.getBlob(0));
        Bitmap b = BitmapFactory.decodeStream(inputStream);
        return b;
    }

//átalakított bitmapot rárakjuk egy változóra, a változóból rárakjuk az imageviewre a bitmapot
 Bitmap image = getImage();
   ImageView    avatar = (ImageView) findViewById(R.id.profileavatar);
        avatar.setImageBitmap(image);
     */