/*
 * TCSS 450 - Spring 2020
 * Dawggit
 * Team 6: Codie Bryan, Kevin Bui, Minh Nguyen, Sean Smith
 */
package edu.tacoma.uw.dawggit.main;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import edu.tacoma.uw.dawggit.R;
import edu.tacoma.uw.dawggit.listings.ItemListing;
import edu.tacoma.uw.dawggit.listings.ItemListingAddActivity;
import edu.tacoma.uw.dawggit.listings.ItemListingDetail;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ListingsFragment#newInstance} factory method to
 * create an instance of this fragment.
 * @author Kevin Bui
 * @version Sprint 2
 */
public class ListingsFragment extends Fragment {
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    /** Recyclerv view.*/
    private RecyclerView mRecyclerView;

    /** List of recycler view item listings.*/
    private List<ItemListing> mItemList;

    /** Recyclerview adapter.*/
    private GridItemRecyclerViewAdapter mAdapter;

    /** Button to add new Item Listings.*/
    private Button mAddButton;

    /** Reference to the firebase database.*/
    private DatabaseReference mDatabaseRef;

    /** Listens to when data changes in the firebase database.*/
    private ValueEventListener mDBListener;


    private String mParam1;
    private String mParam2;

    public ListingsFragment() {
        // Required empty public constructor
    }


    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment TabGridFragment.
     */
    static ListingsFragment newInstance() {
//        Bundle args = new Bundle();
//        args.putString(ARG_PARAM1, param1);
//        args.putString(ARG_PARAM2, param2);
//        fragment.setArguments(args);
        return new ListingsFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

    }

    /**
     * Creates the view for the listings tab.
     *
     * Shows a list of all listings from all users.
     *
     * Users are able to add new listings, or click on different items to see details.
     * @param inflater inflater
     * @param container container
     * @param savedInstanceState null
     * @return View
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_listings, container, false);


        mItemList = new ArrayList<>();
        mAdapter = new GridItemRecyclerViewAdapter(mItemList);
        mRecyclerView = view.findViewById(R.id.listings_list);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.addItemDecoration(new DividerItemDecoration(Objects.requireNonNull(getContext()), DividerItemDecoration.VERTICAL));
        int numberOfColumns = 2;
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), numberOfColumns);
        gridLayoutManager.setOrientation(GridLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(gridLayoutManager);
        mRecyclerView.setAdapter(mAdapter);

        mDatabaseRef = FirebaseDatabase.getInstance().getReference("users/listings");
         mDBListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mItemList.clear();
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    ItemListing item = postSnapshot.getValue(ItemListing.class);
                    assert item != null;
                    item.setKey(postSnapshot.getKey());
                    mItemList.add(item);
                }
                Collections.reverse(mItemList);
                mAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getActivity(), databaseError.getMessage(), Toast.LENGTH_SHORT).show();

            }
        };

        mDatabaseRef.addListenerForSingleValueEvent(mDBListener);

        mAddButton = view.findViewById(R.id.button_fragment_listings_add);
        mAddButton.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), ItemListingAddActivity.class);
            startActivity(intent);
        });
        return view;
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
     * Adapter for the recycler view.
     */
    public class GridItemRecyclerViewAdapter
            extends RecyclerView.Adapter<GridItemRecyclerViewAdapter.ViewHolder> {

        private final List<ItemListing> mValues;
        private final View.OnClickListener mOnClickListener = (view) -> { //Launches ItemListingDetail.java
            ItemListing item = (ItemListing) view.getTag();
            Log.i("Item Clicked", item.getTitle() + " " + item.getEmail() + " " + item.getTextBody());
            Intent intent = new Intent(view.getContext(), ItemListingDetail.class);
            intent.putExtra("ARG_ITEM_ID", item);
            view.getContext().startActivity(intent);
        };

        /**
         * Constructor
         * @param items a list of item listings.
         */
        GridItemRecyclerViewAdapter(List<ItemListing> items) {
            mValues = items;
        }

        /**
         * Inflates the fragment_listing_item
         * @param parent parent
         * @param viewType viewType
         * @return ViewHolder
         */
        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.fragment_listing_item, parent, false);
            return new ViewHolder(view);
        }

        /**
         * Binds each item in the recyler list to the view holder.
         * @param holder ViewHolder
         * @param position int
         */
        @Override
        public void onBindViewHolder(final ViewHolder holder, int position) {
            ItemListing currentItem = mValues.get(position);
            holder.mIdView.setText(mValues.get(position).getTitle());
            double price = mValues.get(position).getPrice();
            DecimalFormat decForm = new DecimalFormat("0.00");
            String priceFormat = "$" + decForm.format(price);
            holder.mContentView.setText(priceFormat);

            String imageUrl = currentItem.getUrl();
            Picasso.get().load(imageUrl)
                    .placeholder(R.mipmap.ic_launcher)
                    .fit()
                    .centerCrop()
                    .into(holder.mImageView);


            holder.itemView.setTag(mValues.get(position));
            holder.itemView.setOnClickListener(mOnClickListener);
        }

        /**
         * Returns the size of the list.
         * @return int
         */
        @Override
        public int getItemCount() {
            return mValues.size();
        }


        /**
         * ViewHolder
         */
        class ViewHolder extends RecyclerView.ViewHolder {
            final TextView mIdView;
            final TextView mContentView;
            final ImageView mImageView;

            /**
             * View Holder constructor.
             * @param view View
             */
            ViewHolder(View view) {
                super(view);
                mIdView = view.findViewById(R.id.tv_fragment_listing_item_title);
                mContentView = view.findViewById(R.id.tv_fragment_listing_item_price);
                mImageView = view.findViewById(R.id.imageView_fragment_listing_item_image);
            }
        }
    }
}
