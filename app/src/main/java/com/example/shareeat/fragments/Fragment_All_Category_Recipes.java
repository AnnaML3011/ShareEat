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


public class Fragment_All_Category_Recipes extends Fragment {
    private RecyclerView categories_LST_names;
    private FirebaseAuth mAuth;
    private List<Category> categories = new ArrayList<>();
    private List<Recipe> all_category_recipes = new ArrayList<>();
    private List<Recipe> all_category_recipes_wish_list = new ArrayList<>();
    private boolean isInWL;
    private FB_Manager fb_manager = new FB_Manager();
    private View view;


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
                                        showSpecificRecipe(all_category_recipes.get(position) ,categoryName);
                                    }

                                    @Override
                                    public void onAddToWishListClicked(View view, Recipe recipe, int position) {
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

    private void showSpecificRecipe(Recipe recipe , String categoryName) {
        Intent myIntent = new Intent(getActivity(), Activity_Specific_Recipe.class);
        myIntent.putExtra("Recipe",recipe);
        myIntent.putExtra("tag","Fragment_All_Category_Recipes");
        myIntent.putExtra("category" , categoryName);
        startActivity(myIntent);
        getActivity().finish();
    }


    public void refresh(String category_name) {
        getUsers(category_name);
    }
}


