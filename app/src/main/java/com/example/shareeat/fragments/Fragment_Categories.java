package com.example.shareeat.fragments;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import com.example.shareeat.activities.Activity_All_Category_Recipes;
import com.example.shareeat.utils.Adapter_Categories;
import com.example.shareeat.objects.Category;
import com.example.shareeat.utils.FB_Manager;
import com.example.shareeat.R;
import com.example.shareeat.objects.Recipe;
import com.google.firebase.auth.FirebaseAuth;
import java.util.ArrayList;
import java.util.List;


public class Fragment_Categories extends Fragment {
    private static final String CATEGORY = "category";
    private static final String TAG = "tag";
    private RecyclerView categories_LST_names;
    private FirebaseAuth mAuth;
    private List<Category> categories = new ArrayList<>();
    private List<Recipe> all_category_recipes = new ArrayList<>();
    private List<Recipe> all_category_recipes_wish_list = new ArrayList<>();
    private boolean isInWL;
    private Fragment_All_Category_Recipes fragment_all_category_recipes;
    private FB_Manager fb_manager = new FB_Manager();



    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_categories_list , container,false);
        mAuth = FirebaseAuth.getInstance();
        fragment_all_category_recipes = new Fragment_All_Category_Recipes();
        findViews(view);
        initViews();
        return view;
    }

    private void findViews(View view) {
        categories_LST_names = view.findViewById(R.id.categories_LST_names);
    }

    private void initViews() {
        categories= generateCategories();
        Adapter_Categories adapter_categories = new Adapter_Categories(getContext(), categories);
        categories_LST_names.setAdapter(adapter_categories);
        adapter_categories.setClickListener(new Adapter_Categories.MyItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Intent myIntent = new Intent(getActivity(), Activity_All_Category_Recipes.class);
                myIntent.putExtra(CATEGORY, categories.get(position).getCategory_Name());
                myIntent.putExtra(TAG,"Fragment_Categories");
                startActivity(myIntent);
                getActivity().finish();
            }
        });

    }

    public static ArrayList<Category> generateCategories() {
        ArrayList<Category> categories = new ArrayList<>();
        categories.add(new Category("Desserts",
                "https://images.immediate.co.uk/production/volatile/sites/30/2020/08/dessert-main-image-molten-cake-0fbd4f2.jpg?quality=90&resize=768,574"));
        categories.add(new Category("Breakfasts",
                "https://cdn.cnn.com/cnnnext/dam/assets/190515173104-03-breakfast-around-the-world-avacado-toast-exlarge-169.jpg"));
        categories.add(new Category("Salads",
                "https://www.onceuponachef.com/images/2010/03/Big-Italian-Salad-4.jpg"));
        categories.add(new Category("Soups",
                "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcRkOvA33nHTeO4E-w2uGYtflV7Un5wZ2TyL4w&usqp=CAU"));
        categories.add(new Category("SideDishes",
                "https://www.acouplecooks.com/wp-content/uploads/2019/11/Steamed-Green-Beans-015-800x1000.jpg"));
        categories.add(new Category("Italic",
                "https://blue.kumparan.com/image/upload/fl_progressive,fl_lossy,c_fill,q_auto:good,w_640,ar_16:9/v1599755069/jo3okzjelukaehcnhlgl.jpg"));
        categories.add(new Category("Meat",
                "https://s3-media0.fl.yelpcdn.com/wphoto/l77clbqAQ5aUXxNJyVL4kQ/h.jpg"));
        categories.add(new Category("Vegetarian",
                "https://cdn.livekindly.co/wp-content/uploads/2018/01/vegan-cancer-1-e1516719430710.jpg"));
        categories.add(new Category("Asian",
                "https://www.practi-food.com/wp-content/uploads/2019/08/bao-bun-10.jpg"));
        return categories;
    }
}


