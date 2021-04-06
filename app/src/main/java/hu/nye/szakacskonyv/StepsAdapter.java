package hu.nye.szakacskonyv;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class StepsAdapter extends RecyclerView.Adapter<StepsAdapter.myViewHolder> {
    Context mContext;
    List<Steps> mData;

    public static class myViewHolder extends RecyclerView.ViewHolder{

        TextView steps, description;

        public myViewHolder(@NonNull View itemView) {
            super(itemView);
            steps = itemView.findViewById(R.id.stepsNumber);
            description = itemView.findViewById(R.id.descriptionText);
        }
    }

    public StepsAdapter(Context mContext, List<Steps> mData) {
        this.mContext = mContext;
        this.mData = mData;
    }

    @NonNull
    @Override
    public myViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View v = inflater.inflate(R.layout.description_item,parent, false);
        return new myViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull myViewHolder holder, int position) {
        holder.steps.setText(mData.get(position).getStepNum());
        holder.description.setText(mData.get(position).getDescription());
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }
}
