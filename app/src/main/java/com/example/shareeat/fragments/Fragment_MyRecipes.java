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
    private Adapter_Recipes adapter_recipe;
    private View view;
    private Recipe recipe;
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
        FirebaseFirestore.getInstance().collection("Recipes").get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot documentSnapshots) {
                        if (documentSnapshots.isEmpty()) {
                            Log.d("empty", "onSuccess: LIST EMPTY");
                            return;
                        } else {
                            for(DocumentSnapshot ds : documentSnapshots.getDocuments())   {
                                recipe = ds.toObject(Recipe.class);
                                if(recipe.getUserUid().equals(mAuth.getCurrentUser().getUid())) {
                                    recipe.setInWishList(false);
                                    getRecipesFromMyWishList(recipe);
                                    recipes.add(recipe);
                                }
                                adapter_recipe = new Adapter_Recipes(getContext(), recipes);
                                myRecipes_RECY_LAY.setLayoutManager(new LinearLayoutManager(view.getContext()));
                                myRecipes_RECY_LAY.setAdapter(adapter_recipe);
                                adapter_recipe.setClickListener(new Adapter_Recipes.MyItemClickListener() {
                                    @Override
                                    public void onItemClick(View view, int position) {
                                        adapter_recipe.updateOneItem(position);
                                        Intent myIntent = new Intent(getActivity(), Activity_Specific_Recipe.class);
                                        myIntent.putExtra("Recipe",recipes.get(position));
                                        myIntent.putExtra("tag","Fragment_MyRecipes");
                                        startActivity(myIntent);
                                        getActivity().finish();
                                    }
                                    @Override
                                    public void onAddToWishListClicked(View view, Recipe recipe, int position) {
                                        fb_manager.setOnAddToWishList(view, recipe, mAuth,getContext());
                                    }
                                });
                            }
                        }
                    }
                });
    }


    private void getRecipesFromMyWishList(Recipe r){
        FirebaseFirestore.getInstance().collection("Users").document(Objects.requireNonNull(mAuth.getCurrentUser().getUid())).collection("userWishList").get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot documentSnapshots) {
                        if (documentSnapshots.isEmpty()) {
                            Log.d("empty", "onSuccess: LIST EMPTY");
                            return;
                        } else {
                            for(DocumentSnapshot ds : documentSnapshots.getDocuments())   {
                                recipe = ds.toObject(Recipe.class);
                                if(ds.getId().equals(r.getRecipeName()+"-"+r.getUserUid())){
                                    r.setInWishList(true);
                                }
                            }
                        }
                    }
                });
    }
}
