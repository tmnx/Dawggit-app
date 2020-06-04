package edu.tacoma.uw.dawggit.main;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
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
import com.google.firebase.storage.FirebaseStorage;
import com.squareup.picasso.Picasso;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import edu.tacoma.uw.dawggit.R;
import edu.tacoma.uw.dawggit.listings.ItemListing;
import edu.tacoma.uw.dawggit.listings.ItemListingDetail;

/**
 * Fragment Home page for the application
 * @version Sprint 2
 * @author Sean Smith, Kevin Bui
 */
public class HomeFragment extends Fragment {

    private RecyclerView mRecyclerView;
    private List<ItemListing> mItemList;
    private HomeListingsAdapter mAdapter;
    private SharedPreferences mSharedPreferences;
    private Button mAddButton;

    private FirebaseStorage mStorage;
    private DatabaseReference mDatabaseRef;
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
     *
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

        mSharedPreferences = Objects.requireNonNull(getActivity()).getSharedPreferences(getString(R.string.FIREBASE_UID),
                Context.MODE_PRIVATE);
        String uid = mSharedPreferences.getString(getString(R.string.FIREBASE_UID), null);
        if(uid == null) {
            Log.e("ListingsFragment", "Firebase UID is null");
        }
        mStorage = FirebaseStorage.getInstance();
        mDatabaseRef = FirebaseDatabase.getInstance().getReference("users/" + uid + "/listings_uploads");

        mDBListener = mDatabaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mItemList.clear();
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    ItemListing item = postSnapshot.getValue(ItemListing.class);
                    assert item != null;
                    item.setKey(postSnapshot.getKey());
                    mItemList.add(item);
                }
                mAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getActivity(), databaseError.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
        return v;

    }

    public class HomeListingsAdapter extends RecyclerView.Adapter<HomeListingsAdapter.ViewHolder> {

        private List<ItemListing> mValues;
        private final View.OnClickListener mOnClickListener = (view) -> {
            ItemListing item = (ItemListing) view.getTag();
            Toast.makeText(view.getContext(),
                    "Item " + item.getTitle() + " Email: " + item.getEmail(),
                    Toast.LENGTH_SHORT).show();
            Log.i("Item Clicked", item.getTitle() + " " + item.getEmail() + " " + item.getTextBody());

            Intent intent = new Intent(view.getContext(), ItemListingDetail.class);
            intent.putExtra("ARG_ITEM_ID", item);
            view.getContext().startActivity(intent);
        };

        HomeListingsAdapter(List<ItemListing> itemList) {
            this.mValues = itemList;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.fragment_home_listings_item, parent, false);
            return new ViewHolder(view);
        }

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
            //holder.itemView.setOnClickListener(mOnClickListener);
            //holder.itemView.setOnCreateContextMenuListener(m);

            holder.itemView.setTag(mValues.get(position));
            holder.itemView.setOnClickListener(mOnClickListener);
        }

        @Override
        public int getItemCount() {
            return mValues.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            private ImageView mImageView;
            private TextView mTextTitle;
            private TextView mTextBody;

            ViewHolder(@NonNull View itemView) {
                super(itemView);
                mImageView = itemView.findViewById(R.id.home_listings_item_image);
                mTextTitle = itemView.findViewById(R.id.home_listings_item_title);
                mTextBody = itemView.findViewById(R.id.home_listings_item_body);
            }

//            private final View.OnCreateContextMenuListener mContextListener = (menu, view, menuInfo) -> {
//                menu.setHeaderTitle("Actions");
//                MenuItem deleteMenuItem = menu.add(Menu.NONE, 1, 1, "Delete");
//                deleteMenuItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
//                    @Override
//                    public boolean onMenuItemClick(MenuItem item) {
//                        int position = getAdapterPosition();
//                        if(position != RecyclerView.NO_POSITION) {
//                            if(position == 1) {
//                                deleteItemListing(position);
//                                return true;
//                            }
//                        }
//                        return false;
//                    }
//                });
//            };
        }
    }

}
