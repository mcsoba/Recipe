package hu.nye.szakacskonyv;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.icu.util.Freezable;
import android.media.Image;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.navigation.NavigationView;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

public class AddRecipeActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener, NavActivitiesInterface {

    ActionBarDrawerToggle toggle;
    DrawerLayout drawerLayout;
    NavigationView navView;
    Toolbar customToolbar;
    ImageButton searchButton;
    EditText add_title;
    EditText description;
    Button image_change;
    Button image_change_done;
    Button save_button;
    ImageButton add_ingredient;
    ImageView avatar;
    NewIngredientAdapter adapter;
    RecyclerView recycler_view;
    List<NewIngredientItem> new_ingredients;
    Spinner categories_spinner;
    ArrayAdapter<CharSequence> spinnerAdapter;
    String név;
    String email;
    int id;
    int max_ingredients = 5;
    private static final int IMAGE_PICK_CODE = 1000;
    private static final int PERMISSION_CODE = 1001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_recipe);

        add_ingredient = findViewById(R.id.addIngredient);
        recycler_view = findViewById(R.id.rv_ingredients);
        save_button = findViewById(R.id.saveButton);

        categories_spinner = findViewById(R.id.spinnerCategories);
        add_title = findViewById(R.id.recipeTitle);
        image_change = findViewById(R.id.addImage);
        image_change_done = findViewById(R.id.imagechangedone);
        avatar = (ImageView) findViewById(R.id.recipeImage);
        searchButton = findViewById(R.id.searchButton);
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openSearchActivity();
            }
        });
        description = findViewById(R.id.recipeDescription);
        image_change.setPaintFlags(image_change.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        image_change.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                image_change.setVisibility(View.GONE);
                image_change_done.setVisibility(View.VISIBLE);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                            == PackageManager.PERMISSION_DENIED) {
                        String[] permissions = {Manifest.permission.READ_EXTERNAL_STORAGE};
                        requestPermissions(permissions, PERMISSION_CODE);
                    } else {
                        pickImageFromGallery();
                    }
                } else {
                    pickImageFromGallery();
                }
                
            }
        });
        image_change_done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ImageView avatardone = (ImageView) findViewById(R.id.recipeImage);
                byte[] kep = imageViewToByte(avatardone);
                System.out.println(kep);
                //sql metódus kép cserére
                //db.updateAvatar(kep,email);
                image_change.setVisibility(View.VISIBLE);
                image_change_done.setVisibility(View.GONE);

            }
        });

        add_ingredient.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(new_ingredients.size() < max_ingredients)
                    addItemToList();
            }
        });

        save_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Mentés gomb felveszi a választott kategóriát, nevet, leírást, hozzávalókat(lista) majd új receptként feltölti adatbázisba
                System.out.println("A kiválasztott kategória: "+categories_spinner.getSelectedItem().toString());
                AddRecipe recept= new AddRecipe();

                recept.execute();
                //TODO
            }
        });
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
        new_ingredients = new ArrayList<>();
        new_ingredients.add(new NewIngredientItem());

        createSpinner();
        createCustomToolbar();
        createRecyclerView();
        createNavigationDrawer();
        //recycler_view.setNestedScrollingEnabled(false);
    }

    public void addItemToList(){

        new_ingredients.add(new NewIngredientItem());
        adapter.notifyItemInserted(new_ingredients.size() - 1);
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

    public void createSpinner(){
        spinnerAdapter = ArrayAdapter.createFromResource(this, R.array.categories, R.layout.spinner_item);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categories_spinner.setAdapter(spinnerAdapter);
        categories_spinner.setOnItemSelectedListener(this);

    }

    public void createRecyclerView(){
        recycler_view.setLayoutManager(new LinearLayoutManager(this));
        adapter = new NewIngredientAdapter(this, new_ingredients);
        recycler_view.setAdapter(adapter);
        adapter.setOnItemClickListener(new NewIngredientAdapter.OnItemClickListener() {
            @Override
            public void onDeleteClick(int position) {
                removeItem(position);
            }
        });
    }

    public void removeItem(int postition){
        if(new_ingredients.size() > 1){
            new_ingredients.remove(postition);
            adapter.notifyItemRemoved(postition);
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
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
    public void pickImageFromGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, IMAGE_PICK_CODE);
        ImageView avatarnew = (ImageView) findViewById(R.id.recipeImage);
        byte[] kep = imageViewToByte(avatarnew);

        //SQL metódus képfrissitésre
        //updateAvatar(kep,email);
    }

    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == IMAGE_PICK_CODE) {
            avatar.setImageURI(data.getData());
        }
    }
    private byte[] imageViewToByte(ImageView image) {
        Bitmap bm = ((BitmapDrawable) image.getDrawable()).getBitmap();
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] byteArray = stream.toByteArray();
        return byteArray;
    }

    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_CODE: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Intent i = new Intent();
                    String email = i.getStringExtra("email");
                    pickImageFromGallery();
                } else {
                    Toast.makeText(this, "Hozáférés megtagadva", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }


    @Override
    public void openFavoritesActivity() {
        //TODO
    }

    public class AddRecipe extends AsyncTask<String,String,String> {


        @Override
        protected String doInBackground(String... strings) {



            HttpURLConnection kapcsolat = null;
            URL url;
            String eredmeny = "";

            try {

                url = new URL("https://studentlab.nye.hu/~szakacskonyv/my_recipe.php");
                kapcsolat = (HttpURLConnection)url.openConnection();
                kapcsolat.setDoInput(true);
                kapcsolat.setDoOutput(true);
                kapcsolat.setRequestMethod("POST");

                String hozzávaló ="";
                for (int i = 0; i< new_ingredients.size();i++){


                    hozzávaló += " "+((TextView) recycler_view.findViewHolderForAdapterPosition(i).itemView.findViewById(R.id.ingredientName)).getText().toString();
                    System.out.println(hozzávaló);
                }
                String adat  = URLEncoder.encode("name", "UTF-8")
                        + "=" + URLEncoder.encode(add_title.getText().toString(), "UTF-8");
                adat += "&" + URLEncoder.encode("category", "UTF-8")
                        + "=" + URLEncoder.encode(categories_spinner.getSelectedItem().toString(), "UTF-8");
                adat += "&" + URLEncoder.encode("ingredients", "UTF-8")
                        + "=" + URLEncoder.encode(hozzávaló, "UTF-8");
                adat += "&" + URLEncoder.encode("description", "UTF-8")
                        + "=" + URLEncoder.encode(description.getText().toString(), "UTF-8");
                adat += "&" + URLEncoder.encode("user_id", "UTF-8")
                        + "=" + URLEncoder.encode(id+"", "UTF-8");


                System.out.println("Ez az adat "+ adat);
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
