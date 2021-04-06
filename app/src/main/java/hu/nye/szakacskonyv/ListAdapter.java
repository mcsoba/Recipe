package hu.nye.szakacskonyv;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ListAdapter extends RecyclerView.Adapter<ListAdapter.MyViewHolder> {

    Context mContext;
    List<String> mDataset;
    private LayoutInflater mInflater;

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView title;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.ingredientTitle);
        }
    }

    public ListAdapter(Context mContext, List<String> myDataset) {
        this.mInflater = LayoutInflater.from(mContext);
        this.mDataset = myDataset;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.ingredient_item, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        String animal = mDataset.get(position);
        holder.title.setText(animal);

    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }
}