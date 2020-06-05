package edu.tacoma.uw.dawggit.course;

import android.text.Layout;
import android.util.Log;
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
import java.util.Collections;
import java.util.List;

import edu.tacoma.uw.dawggit.R;

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.ViewHolder> implements Filterable {

    private static final String TAG = "RecyclerAdapter";
    List<Course> courseList;
    List<Course> courseListAll;

    public RecyclerAdapter(List<Course> moviesList) {
        this.courseList = moviesList;
        courseListAll = new ArrayList<>();
        courseListAll.addAll(this.courseList);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.row_item, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        //holder.mIdView.setText(courseList.get(position).getCourse_code());
        //holder.mContentView.setText(courseList.get(position).getTitle());

        holder.textView.setText(courseList.get(position).getCourse_code());
        holder.itemView.setTag(courseList.get(position));
        holder.rowCountTextView.setText(courseList.get(position).getTitle());

        //holder.rowCountTextView.setText(String.valueOf(0));
       // holder.textView.setText((CharSequence) courseList.get(position));
    }

    @Override
    public int getItemCount() {
        return courseList.size();
    }

    @Override
    public Filter getFilter() {

        return myFilter;
    }

    Filter myFilter = new Filter() {

        //Automatic on background thread
        @Override
        protected FilterResults performFiltering(CharSequence charSequence) {

            List<Course> filteredList = new ArrayList<>();

            if (charSequence == null || charSequence.length() == 0) {
                filteredList.addAll(courseListAll);
            } else {
                for (Course movie: courseListAll) {
                    if (movie.getCourse_code().toLowerCase().contains(charSequence.toString().toLowerCase())) {
                        filteredList.add(movie);
                    }
                }
            }

            FilterResults filterResults = new FilterResults();
            filterResults.values = filteredList;
            return filterResults;
        }

        //Automatic on UI thread
        @Override
        protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
            courseList.clear();
            courseList.addAll((Collection<? extends Course>) filterResults.values);
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
            //Toast.makeText(view.getContext(), courseList.get(getAdapterPosition()), Toast.LENGTH_SHORT).show();
        }
    }
}

