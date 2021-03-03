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
import java.util.Objects;


public class Fragment_All_Category_Recipes extends Fragment {
    private static final String CATEGORY = "category";
    private static final String RECIPE = "Recipe";
    private static final String TAG = "tag";
    private RecyclerView categories_LST_names;
    private FirebaseAuth mAuth;
    private List<Recipe> all_category_recipes = new ArrayList<>();
    private boolean isInWL;
    private FB_Manager fb_manager = new FB_Manager();
    private View view;
    private Recipe recipe;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_categories_list , container,false);
        mAuth = FirebaseAuth.getInstance();
        findViews(view);
        return view;
    }



    private void findViews(View view) {
        categories_LST_names = view.findViewById(R.id.categories_LST_names);
    }


    private void  getRecipesByCategoryFromDB(String categoryName) {
        FirebaseFirestore.getInstance().collection("Recipes")
                    .whereEqualTo("category",categoryName).get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot documentSnapshots) {
                        if (documentSnapshots.isEmpty()) {
                            Log.d("empty", "onSuccess: LIST EMPTY");
                            return;
                        } else {
                            for(DocumentSnapshot ds : documentSnapshots.getDocuments())   {
                                recipe = ds.toObject(Recipe.class);
                                recipe.setInWishList(false);
                                getRecipesFromMyWishListAndCheck(recipe, categoryName);

                            }
                        }
                    }
                });
    }

    private void getRecipesFromMyWishListAndCheck(Recipe r, String categoryName){
        FirebaseFirestore.getInstance().collection("Users").document(Objects.requireNonNull(mAuth.getCurrentUser().getUid())).collection("userWishList").get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot documentSnapshots) {
                        if (documentSnapshots.isEmpty()) {
                            Log.d("empty", "onSuccess: LIST EMPTY-HERE -getRecipesFromMyWishList");
                            addRecipesToListAndSetAdapter(r, categoryName);
                            return;
                        } else {
                            for(DocumentSnapshot ds : documentSnapshots.getDocuments())   {
                                recipe = ds.toObject(Recipe.class);
                                if(ds.getId().equals(r.getRecipeName()+"-"+r.getUserUid())){
                                    r.setInWishList(true);
                                }
                            }
                            addRecipesToListAndSetAdapter(r, categoryName);
                        }
                    }
                });
    }

    private void addRecipesToListAndSetAdapter(Recipe r, String categoryName){
        all_category_recipes.add(r);
        Adapter_Recipes adapter_recipe = new Adapter_Recipes(getContext(), all_category_recipes);
        categories_LST_names.setLayoutManager(new LinearLayoutManager(view.getContext()));
        categories_LST_names.setAdapter(adapter_recipe);
        adapter_recipe.setClickListener(new Adapter_Recipes.MyItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                adapter_recipe.updateOneItem(position);
                showSpecificRecipe(all_category_recipes.get(position) ,categoryName);
            }
            @Override
            public void onAddToWishListClicked(View view, Recipe recipe, int position) {
                isInWL = true;
                fb_manager.setOnAddToWishList(view, recipe, mAuth,getContext());
            }
        });
    }

    private void showSpecificRecipe(Recipe recipe , String categoryName) {
        Intent myIntent = new Intent(getActivity(), Activity_Specific_Recipe.class);
        myIntent.putExtra(RECIPE,recipe);
        myIntent.putExtra(TAG,"Fragment_All_Category_Recipes");
        myIntent.putExtra(CATEGORY , categoryName);
        startActivity(myIntent);
        getActivity().finish();
    }


    public void refresh(String category_name) {
        getRecipesByCategoryFromDB(category_name);
    }
}


