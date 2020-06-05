package edu.tacoma.uw.dawggit.main;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.TextUtils;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import edu.tacoma.uw.dawggit.R;
import edu.tacoma.uw.dawggit.listings.ItemListing;
import edu.tacoma.uw.dawggit.listings.ItemListingDetail;

/**
 * Fragment Home page for the application
 * @version Sprint 2
 * @author Sean Smith
 * @author Kevin Bui
 */
public class HomeFragment extends Fragment {

    /** Recycler view of item listings.*/
    private RecyclerView mRecyclerView;

    /** List of items retrieved from the Firebase realtime database.*/
    private List<ItemListing> mItemList;

    /** Adapter for mRecyclerView*/
    private HomeListingsAdapter mAdapter;

    /** Used to access the current user's email.*/
    private SharedPreferences mSharedPreferences;

    private FirebaseStorage mStorage;

    /** References the users/listings folder on Firebase Realtime Database.*/
    private DatabaseReference mDatabaseRef;

    /** Listens to data changes to the Firebase database reference.*/
    private ValueEventListener mDBListener;

    /**
     *
     */
    public HomeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment HomeFragment.
     */
    static HomeFragment newInstance() {
        HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);

        return fragment;
    }

    /**
     * Creates a recycler view within the Home fragment showing a list of the current user's item listings.
     *
     * Users are able to delete their own items from the list by long clicking on each item.
     * @param inflater Required object to generate a view.
     * @param container Required object to generate a view.
     * @param savedInstanceState Used to save the state.
     * @return Generated view.
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.home_page_fragment, container, false);

        mItemList = new ArrayList<>();
        mAdapter = new HomeListingsAdapter(mItemList);
        mRecyclerView = v.findViewById(R.id.home_listings_recycler_view);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.addItemDecoration(new DividerItemDecoration(Objects.requireNonNull(getContext()), DividerItemDecoration.VERTICAL));
        mRecyclerView.setLayoutManager(new LinearLayoutManager(Objects.requireNonNull(getContext())));
        mRecyclerView.setAdapter(mAdapter);

        mSharedPreferences = Objects.requireNonNull(getActivity()).getSharedPreferences(getString(R.string.USER_EMAIL),
                Context.MODE_PRIVATE);

        mStorage = FirebaseStorage.getInstance();
        mDatabaseRef = FirebaseDatabase.getInstance().getReference("users/listings");
        mDBListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mItemList.clear();
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    ItemListing item = postSnapshot.getValue(ItemListing.class);
                    assert item != null;
                    item.setKey(postSnapshot.getKey());
                    if(TextUtils.equals(item.getEmail(), mSharedPreferences.getString(getString(R.string.USER_EMAIL), null))) {
                        mItemList.add(item);
                    }
                }
                Collections.reverse(mItemList);
                mAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getActivity(), databaseError.getMessage(), Toast.LENGTH_LONG).show();

            }
        };
        mDatabaseRef.addListenerForSingleValueEvent(mDBListener);
        return v;

    }


    /**
     * We have to remove the listener when the HomeActivity is destroyed.
     * The listener will call onCancelled when the user signs out of the app,
     * unless the listener is removed here.
     */
    @Override
    public void onDestroy() {
        super.onDestroy();
        mDatabaseRef.removeEventListener(mDBListener);
    }

    /**
     * Adapter class for the HomeFragment RecyclerView.
     */
    public class HomeListingsAdapter extends RecyclerView.Adapter<HomeListingsAdapter.ViewHolder> {

        private List<ItemListing> mValues;
        private final View.OnClickListener mOnClickListener = (view) -> { //Launches ItemListingDetail.java
            ItemListing item = (ItemListing) view.getTag();
            Log.i("Item Clicked", item.getTitle() + " " + item.getEmail() + " " + item.getTextBody());
            Intent intent = new Intent(view.getContext(), ItemListingDetail.class);
            intent.putExtra("ARG_ITEM_ID", item);
            view.getContext().startActivity(intent);
        };

        /**
         * Adapter constructor
         * @param itemList List<ItemListing>
         */
        HomeListingsAdapter(List<ItemListing> itemList) {
            this.mValues = itemList;
        }

        /**
         * Inflates fragment_home_listings_item.
         * @param parent ViewGroup
         * @param viewType int
         * @return ViewHolder
         */
        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.fragment_home_listings_item, parent, false);
            return new ViewHolder(view);
        }

        /**
         * Binds each item in the recycler list to the viewholder.
         * @param holder ViewHolder
         * @param position int
         */
        @Override
        public void onBindViewHolder(@NonNull HomeListingsAdapter.ViewHolder holder, int position) {
            ItemListing currentItem = mValues.get(position);
            String imageUrl = currentItem.getUrl();
            Picasso.get().load(imageUrl)
                    .placeholder(R.mipmap.ic_launcher)
                    .fit()
                    .centerCrop()
                    .into(holder.mImageView);
            holder.mTextTitle.setText(currentItem.getTitle());
            holder.mTextBody.setText(currentItem.getTextBody());
            holder.itemView.setTag(mValues.get(position));
            holder.itemView.setTag(mValues.get(position));
            holder.itemView.setOnClickListener(mOnClickListener);
        }

        /**
         * Returns size of list.
         * @return int
         */
        @Override
        public int getItemCount() {
            return mValues.size();
        }

        /**
         * Sets up the view for each recyclerview item.
         */
        class ViewHolder extends RecyclerView.ViewHolder implements View.OnCreateContextMenuListener, MenuItem.OnMenuItemClickListener {
            private ImageView mImageView;
            private TextView mTextTitle;
            private TextView mTextBody;

            /**
             * Viewholder
             * @param itemView View
             */
            ViewHolder(@NonNull View itemView) {
                super(itemView);
                mImageView = itemView.findViewById(R.id.home_listings_item_image);
                mTextTitle = itemView.findViewById(R.id.home_listings_item_title);
                mTextBody = itemView.findViewById(R.id.home_listings_item_body);
                itemView.setOnCreateContextMenuListener(this);
            }

            /**
             * Creates a context menu when long clicking on an item in the recycler view.
             * @param menu ContextMenu
             * @param v View
             * @param menuInfo ContextMenuInfo
             */
            @Override
            public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
                menu.setHeaderTitle("Actions");
                MenuItem deleteMenuItem = menu.add(Menu.NONE, 1, 1, "Delete");
                deleteMenuItem.setOnMenuItemClickListener(this);
            }

            /**
             * Handles context menu item click events.
             * @param item MenuItem
             * @return boolean
             */
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                int position = getAdapterPosition();
                if(position != RecyclerView.NO_POSITION) {
                    if(item.getItemId() == 1) {
                        deleteItemListing(position);
                        return true;
                    }
                }
                return false;
            }
        }
    }

    /**
     * Deletes a selected item from the list, and firebase database.
     * @param position int
     */
    private void deleteItemListing(int position) {
        ItemListing selectedItem = mItemList.get(position);
        final String selectedKey = selectedItem.getKey();
        StorageReference imageRef = mStorage.getReferenceFromUrl(selectedItem.getUrl());
        imageRef.delete().addOnSuccessListener(aVoid -> {
            mDatabaseRef.child(selectedKey).removeValue();
            Toast.makeText(getContext(), "Item Deleted", Toast.LENGTH_SHORT).show();
        });
    }

}
