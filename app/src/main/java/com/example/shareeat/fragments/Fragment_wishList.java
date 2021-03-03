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
import com.example.shareeat.utils.Adapter_MyWishList;
import com.example.shareeat.utils.Adapter_WishList;
import com.example.shareeat.R;
import com.example.shareeat.objects.Recipe;
import com.example.shareeat.activities.Activity_MyFeed;
import com.example.shareeat.activities.Activity_MyWishList;
import com.example.shareeat.activities.Activity_Specific_Recipe;
import com.example.shareeat.utils.FB_Manager;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


public class Fragment_wishList extends Fragment {
    private static final String RECIPE = "Recipe";
    private static final String TAG = "tag";
    private RecyclerView wishList_RECY_LAY;
    private RecyclerView myRecipes_RECY_LAY;
    List<Recipe> recipes = new ArrayList<>();
    private Recipe recipe = new Recipe();
    private List<Recipe> recipes_WishList ;
    private FirebaseAuth mAuth;
    private View view;
    private String which_Activity="";
    FB_Manager fb_manager;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if(getActivity() instanceof Activity_MyFeed){
            view = inflater.inflate(R.layout.fragment_wish_list , container,false);
            which_Activity = "Activity_MyFeed";
        }else if(getActivity() instanceof Activity_MyWishList){
            view = inflater.inflate(R.layout.fragment_my_recipes , container,false);
            which_Activity = "Activity_MyWishList";
        }
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
                            for(DocumentSnapshot ds : documentSnapshots.getDocuments())   {
                                Recipe recipe = ds.toObject(Recipe.class);
                                recipes_WishList.add(recipe);
                                if(which_Activity.equals("Activity_MyWishList")){
                                    setAdapterMyRecipesForWL(myRecipes_RECY_LAY);
                                }else if(which_Activity.equals("Activity_MyFeed")) {
                                    setAdapterWishList(wishList_RECY_LAY);
                                }
                            }
                        }
                    }
                });
    }

    private void setAdapterWishList(RecyclerView rv){
        Adapter_WishList adapter_recipe = new Adapter_WishList(getContext(), recipes_WishList);
        rv.setAdapter(adapter_recipe);
        adapter_recipe.setClickListener(new Adapter_WishList.MyItemClickListener() {

            @Override
            public void onItemClick(View view, int position) {
                Intent myIntent = new Intent(getActivity(), Activity_Specific_Recipe.class);
                myIntent.putExtra(RECIPE,recipes_WishList.get(position));
                myIntent.putExtra(TAG,"Fragment_wishList");
                startActivity(myIntent);
                getActivity().finish();
            }

            @Override
            public void onWishListClicked(View view, Recipe recipe) {
                recipe.setInWishList(false);
                fb_manager.removeRecipeFromWishList(recipe, mAuth, getContext());
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
                Intent myIntent = new Intent(getActivity(), Activity_Specific_Recipe.class);
                myIntent.putExtra(RECIPE,recipes_WishList.get(position));
                myIntent.putExtra(TAG,"Fragment_myWL");
                startActivity(myIntent);
                getActivity().finish();
            }
            @Override
            public void onWishListClicked(View view, Recipe recipe) {
                recipe.setInWishList(false);
                fb_manager.removeRecipeFromWishList(recipe ,mAuth, getContext());
                getActivity().finish();
                startActivity(getActivity().getIntent());

            }
        });
    }
}

