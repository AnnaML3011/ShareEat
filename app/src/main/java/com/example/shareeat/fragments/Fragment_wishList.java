package com.example.shareeat.fragments;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.shareeat.utils.Adapter_MyWishList;
import com.example.shareeat.utils.Adapter_WishList;
import com.example.shareeat.R;
import com.example.shareeat.objects.Recipe;
import com.example.shareeat.activities.Activity_MyFeed;
import com.example.shareeat.activities.Activity_MyWishList;
import com.example.shareeat.activities.Activity_Specific_Recipe;
import com.example.shareeat.utils.FB_Manager;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Fragment_wishList extends Fragment {
    private RecyclerView wishList_RECY_LAY;
    private RecyclerView myRecipes_RECY_LAY;
    List<Recipe> recipes = new ArrayList<>();
    private ImageView recipe_image_WL_IMG;
    private TextView recipe_title_WL_LBL;
    private Recipe recipe = new Recipe();
    private String fragment_tag;
    private List<Recipe> recipes_WishList ;
    private FirebaseAuth mAuth;
    private View view;
//    private Fragment_Recipe fragment_recipe;
    private String which_Activity="";
    FB_Manager fb_manager;



    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if(getActivity() instanceof Activity_MyFeed){
            view = inflater.inflate(R.layout.fragment_wish_list , container,false);
            which_Activity = "Activity_MyFeed";
            Log.d("Activity_MyFeed","yessssss");
        }else if(getActivity() instanceof Activity_MyWishList){
            view = inflater.inflate(R.layout.fragment_my_recipes , container,false);
            which_Activity = "Activity_MyWishList";
            Log.d("Activity_MyWishList","yessssss");
        }
//        view = inflater.inflate(R.layout.fragment_wish_list , container,false);
        mAuth = FirebaseAuth.getInstance();
        fb_manager = new FB_Manager();
        recipes_WishList = new ArrayList<>();
        findViews(view);
        initViews();
        return view;
    }

    private void findViews(View view) {
        wishList_RECY_LAY = view.findViewById(R.id.wishList_RECY_LAY);
        myRecipes_RECY_LAY = view.findViewById(R.id.myRecipes_RECY_LAY);

    }

    private void initViews() {
        getAllWishListRecipesFromDB();
    }


    private void getAllWishListRecipesFromDB(){
        FirebaseFirestore.getInstance().collection("Users").document(Objects.requireNonNull(mAuth.getCurrentUser().getUid())).collection("userWishList").get()
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
                                recipes_WishList.add(recipe);
                                if(which_Activity.equals("Activity_MyWishList")){
                                    setAdapterMyRecipesForWL(myRecipes_RECY_LAY);
                                }else if(which_Activity.equals("Activity_MyFeed")) {
                                    setAdapterWishList(wishList_RECY_LAY);
                                }
                            }
                            Log.d("recipes:", "onSuccess: " + recipes);
                        }
                    }
                });
    }

    private void setAdapterWishList(RecyclerView rv){
        Adapter_WishList adapter_recipe = new Adapter_WishList(getContext(), recipes_WishList);
//                                wishList_RECY_LAY.setLayoutManager(new LinearLayoutManager(view.getContext()));
        rv.setAdapter(adapter_recipe);
        adapter_recipe.setClickListener(new Adapter_WishList.MyItemClickListener() {

            @Override
            public void onItemClick(View view, int position) {
                Log.d("position:", "onCLICK: " + recipes_WishList.get(position).getRecipeName());
                Intent myIntent = new Intent(getActivity(), Activity_Specific_Recipe.class);
                myIntent.putExtra("Recipe",recipes_WishList.get(position));
                myIntent.putExtra("tag","Fragment_wishList");
                startActivity(myIntent);
                getActivity().finish();
            }

            @Override
            public void onWishListClicked(View view, Recipe recipe) {
                fb_manager.removeRecipeFromWishList(recipe.getRecipeName(), mAuth, getContext());
                recipe.setInWishList(false);
                fb_manager.uploadRecipeToUserRecipes(recipe.getRecipeName(), recipe, mAuth);
                getActivity().finish();
                startActivity(getActivity().getIntent());
            }
        });
    }

    private void setAdapterMyRecipesForWL(RecyclerView rv){
        Adapter_MyWishList adapter_mWL = new Adapter_MyWishList(getContext(),recipes_WishList);
        rv.setLayoutManager(new LinearLayoutManager(view.getContext()));
        rv.setAdapter(adapter_mWL);
        adapter_mWL.setClickListener(new Adapter_MyWishList.MyItemClickListener() {

            @Override
            public void onItemClick(View view, int position) {
                Log.d("position:", "onCLICK: " + recipes_WishList.get(position).getRecipeName());
                Intent myIntent = new Intent(getActivity(), Activity_Specific_Recipe.class);
                myIntent.putExtra("Recipe",recipes_WishList.get(position));
                myIntent.putExtra("tag","Fragment_myWL");
                startActivity(myIntent);
                getActivity().finish();
            }
            @Override
            public void onWishListClicked(View view, Recipe recipe) {
                fb_manager.removeRecipeFromWishList(recipe.getRecipeName() ,mAuth, getContext());
                recipe.setInWishList(false);
                fb_manager.uploadRecipeToUserRecipes(recipe.getRecipeName(), recipe, mAuth);
                getActivity().finish();
                startActivity(getActivity().getIntent());

            }
        });
    }
}

