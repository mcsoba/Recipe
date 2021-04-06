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

public class CategoriesActivity extends AppCompatActivity implements NavActivitiesInterface, CategoryAdapter.OnItemListener {

    private ActionBarDrawerToggle toggle;
    private DrawerLayout drawerLayout;
    private NavigationView navView;
    private Toolbar customToolbar;
    private ImageButton searchButton;
    private RecyclerView recyclerView;
    private CategoryAdapter categoryAdapter;
    private List<CategoryItem> mList;
    ArrayList<String> kategóriák = new ArrayList<>();
    String név;
    String email;
    String kategória;
    int id;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_categories);
        recyclerView = findViewById(R.id.rv_List);
        searchButton = findViewById(R.id.searchButton);

        Intent intent = getIntent();

        NavigationView navView = (NavigationView)findViewById(R.id.navView);
        View headerView = navView.getHeaderView(0);
        TextView username = (TextView)headerView.findViewById(R.id.username_tv);
        TextView email_tv = (TextView)headerView.findViewById(R.id.email_tv);
        username.setText(intent.getStringExtra("name"));
        email_tv.setText(intent.getStringExtra("email"));
        id = intent.getIntExtra("id",0);
        System.out.println("Az átadott név: "+ intent.getStringExtra("name"));
        System.out.println("Az átadott email: "+ intent.getStringExtra("email"));
        System.out.println("Az átadott id: "+ intent.getIntExtra("id",0));
        név = intent.getStringExtra("name");
        email = intent.getStringExtra("email");
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openSearchActivity();
            }
        });

        mList = new ArrayList<>();
        mList.add(new CategoryItem(R.drawable.category_reggeli, "Reggeli" ));
        mList.add(new CategoryItem(R.drawable.category_leves, "Leves"));
        mList.add(new CategoryItem(R.drawable.category_husetel, "Húsétel"));
        mList.add(new CategoryItem(R.drawable.category_haletel, "Halétel"));
        mList.add(new CategoryItem(R.drawable.category_teszta, "Tésztaétel"));
        mList.add(new CategoryItem(R.drawable.category_egytaletel, "Egytálétel"));
        mList.add(new CategoryItem(R.drawable.category_husmentes, "Húsmentes étel"));
        mList.add(new CategoryItem(R.drawable.category_koret, "Köret"));
        mList.add(new CategoryItem(R.drawable.category_fozelek, "Főzelék"));
        mList.add(new CategoryItem(R.drawable.category_salata, "Saláta"));

        createCustomToolbar();
        createRecyclerView();
        createNavigationDrawer();
    }

    public void createRecyclerView(){
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        categoryAdapter = new CategoryAdapter(this,mList,this);
        recyclerView.setAdapter(categoryAdapter);
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
        intent.putExtra("name",név);
        intent.putExtra("email",email);
        intent.putExtra("id",id);
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
        switch (position){
            case 0: {
                Intent intent = new Intent(getApplicationContext(), CategoryRecipesActivity.class);
                intent.putExtra("title","Reggeli");
                intent.putExtra("name",név);
                intent.putExtra("email",email);
                intent.putExtra("id",id);
                startActivity(intent);
            }
            break;
            case 1: {
                Intent intent = new Intent(getApplicationContext(), CategoryRecipesActivity.class);
                intent.putExtra("title","Leves");
                intent.putExtra("name",név);
                intent.putExtra("email",email);
                intent.putExtra("id",id);
                startActivity(intent);
            }
            break;
            case 2: {
                Intent intent = new Intent(getApplicationContext(), CategoryRecipesActivity.class);
                intent.putExtra("title","Húsétel");
                intent.putExtra("name",név);
                intent.putExtra("email",email);
                intent.putExtra("id",id);
                startActivity(intent);
            }
            break;
            case 3: {
                Intent intent = new Intent(getApplicationContext(), CategoryRecipesActivity.class);
                intent.putExtra("title","Halétel");
                intent.putExtra("name",név);
                intent.putExtra("email",email);
                intent.putExtra("id",id);
                startActivity(intent);
            }
            break;
            case 4: {
                Intent intent = new Intent(getApplicationContext(), CategoryRecipesActivity.class);
                intent.putExtra("title","Tésztaétel");
                intent.putExtra("name",név);
                intent.putExtra("email",email);
                intent.putExtra("id",id);
                startActivity(intent);
            }
            break;
            case 5: {
                Intent intent = new Intent(getApplicationContext(), CategoryRecipesActivity.class);
                intent.putExtra("title","Egytálétel");
                intent.putExtra("name",név);
                intent.putExtra("email",email);
                intent.putExtra("id",id);
                startActivity(intent);
            }
            break;
            case 6: {
                Intent intent = new Intent(getApplicationContext(), CategoryRecipesActivity.class);
                intent.putExtra("title","Húsmentes étel");
                intent.putExtra("name",név);
                intent.putExtra("email",email);
                intent.putExtra("id",id);
                startActivity(intent);
            }
            break;
            case 7: {
                Intent intent = new Intent(getApplicationContext(), CategoryRecipesActivity.class);
                intent.putExtra("title","Köret");
                intent.putExtra("name",név);
                intent.putExtra("email",email);
                intent.putExtra("id",id);
                startActivity(intent);
            }
            break;
            case 8: {
                Intent intent = new Intent(getApplicationContext(), CategoryRecipesActivity.class);
                intent.putExtra("title","Fözelék");
                intent.putExtra("name",név);
                intent.putExtra("email",email);
                intent.putExtra("id",id);
                startActivity(intent);
            }
            break;
            case 9: {
                Intent intent = new Intent(getApplicationContext(), CategoryRecipesActivity.class);
                intent.putExtra("title","Saláta");
                intent.putExtra("name",név);
                intent.putExtra("email",email);
                intent.putExtra("id",id);
                startActivity(intent);
            }
            break;

        }
    }
}