package hu.nye.szakacskonyv;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.myViewHolder> {

    Context mContext;
    List<CategoryItem> mData;
    OnItemListener mOnItemListener;

    public static class myViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        ImageView background_img;
        TextView tv_title, tv_numbers;
        OnItemListener onItemListener;

        public myViewHolder(@NonNull View itemView, OnItemListener onItemListener) {
            super(itemView);
            background_img = itemView.findViewById(R.id.card_background);
            tv_title = itemView.findViewById(R.id.card_title);
            tv_numbers = itemView.findViewById(R.id.card_size);
            this.onItemListener = onItemListener;

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            onItemListener.onItemClick(getAdapterPosition());
        }
    }

    public CategoryAdapter(Context mContext, List<CategoryItem> mData, OnItemListener mOnItemListener) {
        this.mContext = mContext;
        this.mData = mData;
        this.mOnItemListener = mOnItemListener;
    }

    @NonNull
    @Override
    public myViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View v = inflater.inflate(R.layout.category_item,parent, false);
        return new myViewHolder(v,mOnItemListener);
    }

    @Override
    public void onBindViewHolder(@NonNull myViewHolder holder, int position) {
        holder.background_img.setImageResource(mData.get(position).getBackground());
        holder.tv_title.setText(mData.get(position).getCategoryName());
        holder.tv_numbers.setText(mData.get(position).getRecipes());
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public interface OnItemListener{
        void onItemClick(int position);
    }
}
