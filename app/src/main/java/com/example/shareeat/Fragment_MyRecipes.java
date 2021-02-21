package com.example.shareeat;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class Fragment_MyRecipes extends Fragment {
     private RecyclerView myRecipes_RECY_LAY;
     private FirebaseAuth mAuth;
    List<Recipe> recipes = new ArrayList<>();


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_my_recipes , container,false);
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
                                Recipe recipe = ds.toObject(Recipe.class);
                                recipes.add(recipe);
//                                Log.d("recipe:" , ""+recipe.getRecipeName() );
                                Adapter_Recipe adapter_recipe = new Adapter_Recipe(getContext(), recipes);
                                myRecipes_RECY_LAY.setAdapter(adapter_recipe);
                                adapter_recipe.setClickListener(new Adapter_Recipe.MyItemClickListener() {
                                    @Override
                                    public void onItemClick(View view, int position) {
                                        Log.d("position:", "onCLICK: " + position);
                                        //TODO - implement that on click it will open a screen which have picture of the recipe, name, prep time, and all description with scroll oprion
                                    }

                                    @Override
                                    public void onReadMoreClicked(View view, Recipe recipe) {
                                        //TODO - implement that on click on "readmore" it will make option to scroll the text of the description down.
                                        Log.d("position:", "read more: " + recipe.getRecipeDescription());

                                    }
                                });
                            }
                            Log.d("recipes:", "onSuccess: " + recipes);
                        }
                    }
                });


    }
}
