package com.example.shareeat.fragments;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.shareeat.utils.Adapter_Recipes;
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


public class Fragment_MyRecipes extends Fragment {
     private RecyclerView myRecipes_RECY_LAY;
     private FirebaseAuth mAuth;
    private List<Recipe> recipes = new ArrayList<>();
    private List<Recipe> recipes_WishList = new ArrayList<>();
    private List<Recipe> all_recipes_WishList = new ArrayList<>();
    private Adapter_Recipes adapter_recipe;
    private View view;
    private Recipe.RecipeCategory recipeCategory;
    private Recipe recipe;
    private ImageView save_to_WL_BTN_myRecipes;
    boolean isInWL = false;
    private FB_Manager fb_manager = new FB_Manager();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_my_recipes , container,false);
        mAuth = FirebaseAuth.getInstance();
        findViews(view);
        initViews();
        return view;
    }


    private void findViews(View view) {
        myRecipes_RECY_LAY = view.findViewById(R.id.myRecipes_RECY_LAY);
    }

    private void initViews() {
        getRecipesFromDB();


    }

    private void  getRecipesFromDB() {
        FirebaseFirestore.getInstance().collection("Users").document(Objects.requireNonNull(mAuth.getCurrentUser().getUid())).collection("userRecipes").get()
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
                                recipe = ds.toObject(Recipe.class);
                                recipes.add(recipe);
//                                Log.d("recipe:" , ""+recipe.getRecipeName() );
                                adapter_recipe = new Adapter_Recipes(getContext(), recipes);
                                myRecipes_RECY_LAY.setLayoutManager(new LinearLayoutManager(view.getContext()));
                                myRecipes_RECY_LAY.setAdapter(adapter_recipe);
                                adapter_recipe.setClickListener(new Adapter_Recipes.MyItemClickListener() {
                                    @Override
                                    public void onItemClick(View view, int position) {
                                        adapter_recipe.updateOneItem(position);
                                        Log.d("position:", "onCLICK: " + recipes.get(position).getRecipeName());
                                        Intent myIntent = new Intent(getActivity(), Activity_Specific_Recipe.class);
                                        myIntent.putExtra("Recipe",recipes.get(position));
                                        myIntent.putExtra("tag","Fragment_MyRecipes");
                                        startActivity(myIntent);
                                        getActivity().finish();
//                                        Bundle bundle = new Bundle();
//                                        bundle.putSerializable("Recipe", recipes.get(position));
//                                        bundle.putString("tag","Fragment_MyRecipes");
//                                        fragment_recipe.setArguments(bundle);
//                                        getActivity().getSupportFragmentManager().beginTransaction()
//                                                .replace(((ViewGroup)getView().getParent()).getId(), fragment_recipe, "findThisFragment")
//                                                .commit();
//                                        specific_recipe.setRecipeInfo(recipes.get(position));
                                    }
                                    @Override
                                    public void onAddToWishListClicked(View view, Recipe recipe, int position) {
//                                        adapter_recipe.updateOneItem(position);
                                        fb_manager.setOnAddToWishList(view, recipe, mAuth,getContext());
//                                        save_to_WL_BTN_myRecipes = view.findViewById(R.id.save_to_WL_BTN_myRecipes);
//                                        if(recipe.isInWishList() ==false){
//                                            isInWL = true;
//                                            recipe.setInWishList(isInWL);
//                                            Glide.with(view).load(R.drawable.ic_heart_filled_pink).apply(RequestOptions.circleCropTransform()).into(save_to_WL_BTN_myRecipes);
//                                            fb_manager.addSpecificRecipe(recipe.getRecipeName(), recipe.getRecipeIngredients(),recipe.getRecipeDirections(), recipe.getPreparationTime()
//                                                    ,recipe.getCategory().toString(), recipe.getRecipeImage(), isInWL, mAuth);
//                                            uploadRecipe(recipe.getRecipeName(), recipe, mAuth);
//                                        }else{
//                                            isInWL = false;
//                                            recipe.setInWishList(isInWL);
//                                            Glide.with(view).load(R.drawable.ic_heart_empty).apply(RequestOptions.circleCropTransform()).into(save_to_WL_BTN_myRecipes);
//                                            fb_manager.removeRecipeFromWishList(recipe.getRecipeName(),mAuth,getContext());
//                                            uploadRecipe(recipe.getRecipeName(), recipe, mAuth);
//
//                                        }

//                                        all_recipes_WishList.addAll(recipes_WishList);
//                                        fb_manager.uploadRecipe(recipe.getRecipeName(),recipe, mAuth);

//                                        addSpecificRecipe(recipe.getRecipeName(),recipe.getRecipeDescription(),recipe.getPreparationTime(),recipe.getCategory().toString(), recipe.getRecipeImage(), isInWL);
//                                        uploadRecipe(recipe.getRecipeName(),recipe);

//                                        for(Recipe recipe1 : all_recipes_WishList){
//                                            if(recipe.isInWishList() == true) {
//                                            }
////                                        }

//                                        if(isInWL == true){
//                                            save_to_WL_BTN_myRecipes = view.findViewById(R.id.save_to_WL_BTN_myRecipes);
//                                            save_to_WL_BTN_myRecipes.setImageResource(R.drawable.ic_heart_filled_pink);
//                                        }
                                        //TODO - fill the heart on click
//                                        save_to_WL_BTN_myRecipes = view.findViewById(R.id.save_to_WL_BTN_myRecipes);
//                                        save_to_WL_BTN_myRecipes.setImageResource(R.drawable.ic_heart_filled_black);
                                    }
                                });
                            }
                            Log.d("recipes:", "onSuccess: " + recipes);
                        }
                    }
                });


    }
}
