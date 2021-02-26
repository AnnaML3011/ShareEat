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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.shareeat.utils.Adapter_Categories;
import com.example.shareeat.utils.Adapter_Recipes;
import com.example.shareeat.objects.Category;
import com.example.shareeat.utils.FB_Manager;
import com.example.shareeat.R;
import com.example.shareeat.objects.Recipe;
import com.example.shareeat.activities.Activity_Specific_Recipe;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import java.util.ArrayList;
import java.util.List;


public class Fragment_All_Category_Recipes extends Fragment {
    private RecyclerView categories_LST_names;
    private FirebaseAuth mAuth;
    private List<Category> categories = new ArrayList<>();
    private List<Recipe> all_category_recipes = new ArrayList<>();
    private List<Recipe> all_category_recipes_wish_list = new ArrayList<>();
    private boolean isInWL;
    //    private Fragment_Recipe fragment_recipe;
    private FB_Manager fb_manager = new FB_Manager();
    private View view;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_categories_list , container,false);
        mAuth = FirebaseAuth.getInstance();
//        fragment_recipe = new Fragment_Recipe();
        findViews(view);
        Bundle bundle =new Bundle();
        String category = bundle.getString("Category");
        getUsers(category);
        return view;
    }



    private void findViews(View view) {
        categories_LST_names = view.findViewById(R.id.categories_LST_names);
    }

    private void getUsers(String categoryName){
        FirebaseFirestore.getInstance().collection("Users").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot documentSnapshots) {
                if(documentSnapshots.isEmpty()){
                    Log.d("empty", "onSuccess: USERS LIST EMPTY");
                }else{
                    for(DocumentSnapshot ds : documentSnapshots.getDocuments())   {
                        String uID = ds.getId();
                        getRecipesByCategoryFromDB(categoryName, uID);

                    }
                }
            }
        });
    }

    private void  getRecipesByCategoryFromDB(String categoryName, String useruID ) {
        FirebaseFirestore.getInstance().collection("Users").document(useruID).
                collection("userRecipes").whereEqualTo("category",categoryName).get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot documentSnapshots) {
                        if (documentSnapshots.isEmpty()) {
                            Log.d("empty", "onSuccess: LIST EMPTY");
                            return;
                        } else {
                            // Convert the whole Query Snapshot to a list
                            // of objects directly! No need to fetch each
                            // document.
                            for(DocumentSnapshot ds : documentSnapshots.getDocuments())   {
                                Recipe recipe = ds.toObject(Recipe.class);
                                all_category_recipes.add(recipe);
//                                Log.d("recipe:" , ""+recipe.getRecipeName() );
                                Adapter_Recipes adapter_recipe = new Adapter_Recipes(getContext(), all_category_recipes);
                                categories_LST_names.setLayoutManager(new LinearLayoutManager(view.getContext()));
                                categories_LST_names.setAdapter(adapter_recipe);
                                adapter_recipe.setClickListener(new Adapter_Recipes.MyItemClickListener() {
                                    @Override
                                    public void onItemClick(View view, int position) {
                                        Log.d("position:", "onCLICK: " + position);
                                        showSpecificRecipe(all_category_recipes.get(position));
                                    }

                                    @Override
                                    public void onAddToWishListClicked(View view, Recipe recipe) {
                                        isInWL = true;
                                        fb_manager.addSpecificRecipe(recipe.getRecipeName(),recipe.getRecipeIngredients(), recipe.getRecipeDirections(), recipe.getPreparationTime(),recipe.getCategory().toString(), recipe.getRecipeImage(), isInWL,mAuth);
//                                        fb_manager.uploadRecipe(recipe.getRecipeName(),recipe, mAuth);
                                    }
                                });
                            }
                            Log.d("recipes:", "onSuccess: " + all_category_recipes);
                        }
                    }
                });
    }

    private void showSpecificRecipe(Recipe recipe) {
        Intent myIntent = new Intent(getActivity(), Activity_Specific_Recipe.class);
        myIntent.putExtra("Recipe",recipe);
        myIntent.putExtra("tag","Fragment_Categories");
        startActivity(myIntent);
        getActivity().finish();
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


