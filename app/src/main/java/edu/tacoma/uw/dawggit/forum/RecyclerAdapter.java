package edu.tacoma.uw.dawggit.forum;

/**
 * TCSS 450 Mobile Application
 * Team 6
 **/


import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import edu.tacoma.uw.dawggit.R;
import edu.tacoma.uw.dawggit.comment.CommentsContent;
import edu.tacoma.uw.dawggit.course.Course;
import edu.tacoma.uw.dawggit.course.CourseDisplayActivity;

/**
 * Displays the course reviews and allows user to filter through them.
 */
public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.ViewHolder> implements Filterable {

    private static final String TAG = "RecyclerAdapter";
    List<Forum> forumList;
    List<Forum> forumListAll;

    public RecyclerAdapter(List<Forum> moviesList) {
        this.forumList = moviesList;
        forumListAll = new ArrayList<>();
        forumListAll.addAll(this.forumList);
    }

    /**
     * Sets the layout of the search bar results.
     * @param parent
     * @param viewType
     * @return ViewHolder
     */
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.row_item, parent, false);
        RecyclerAdapter.ViewHolder viewHolder = new RecyclerAdapter.ViewHolder(view);
        return viewHolder;
    }

    /**
     * Positions each course in the search bar results.
     * @param holder
     * @param position
     */
    @Override
    public void onBindViewHolder(@NonNull RecyclerAdapter.ViewHolder holder, int position) {
        //holder.mIdView.setText(courseList.get(position).getCourse_code());
        //holder.mContentView.setText(courseList.get(position).getTitle());

        holder.textView.setText(forumList.get(position).getTitle());
        holder.itemView.setTag(forumList.get(position));
        holder.rowCountTextView.setText(forumList.get(position).getDate());

        //holder.rowCountTextView.setText(String.valueOf(0));
        // holder.textView.setText((CharSequence) courseList.get(position));
    }

    /**
     * Returns the number of elements in courselist
     * @return the current count of items.
     */
    @Override
    public int getItemCount() {
        return forumList.size();
    }

    /**
     * Returns the filter object.
     * @return the filter.
     */
    @Override
    public Filter getFilter() {

        return myFilter;
    }

    Filter myFilter = new Filter() {

        //Automatic on background thread

        /**
         * Filters out the course list to what the user types in search bar.
         * @param charSequence
         * @return filter results of courses.
         */
        @Override
        protected FilterResults performFiltering(CharSequence charSequence) {

            List<Forum> filteredList = new ArrayList<>();

            if (charSequence == null || charSequence.length() == 0) {
                filteredList.addAll(forumListAll);
            } else {
                for (Forum movie: forumListAll) {
                    if (movie.getTitle().toLowerCase().contains(charSequence.toString().toLowerCase())) {
                        filteredList.add(movie);
                    }
                }
            }

            FilterResults filterResults = new FilterResults();
            filterResults.values = filteredList;
            return filterResults;
        }

        /**
         * Automatically updates the course review list to the filter results.
         * @param charSequence
         * @param filterResults
         */
        @Override
        protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
            forumList.clear();
            forumList.addAll((Collection<? extends Forum>) filterResults.values);
            notifyDataSetChanged();
        }
    };






    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        ImageView imageView;
        TextView textView, rowCountTextView;
        TextView mIdView;
        TextView mContentView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            //imageView = itemView.findViewById(R.id.imageView);
            textView = itemView.findViewById(R.id.textView);
            mIdView = (TextView) itemView.findViewById(R.id.id_text);
            mContentView =  itemView.findViewById(R.id.content);
            rowCountTextView = itemView.findViewById(R.id.rowCountTextView);

            itemView.setOnClickListener(this);

        }






        @Override
        public void onClick(View view) {
            Forum item = (Forum) view.getTag();

            Context context = view.getContext();
//                    Toast.makeText(context, "Course review clicked " + context.toString(), Toast.LENGTH_LONG).show();
            Intent intent = new Intent(context, ForumDisplayActivity.class);
            intent.putExtra(ForumDisplayActivity.ARG_ITEM_ID, item);
            context.startActivity(intent);
        }


        }
    }

