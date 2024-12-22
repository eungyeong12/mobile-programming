package jo.toybreeze.adaptor;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

import jo.toybreeze.R;

public class ReviewImageAdaptor extends RecyclerView.Adapter<ReviewImageAdaptor.ViewHolder> {
    private List<String> imageUrls;

    public ReviewImageAdaptor(List<String> imageUrls) {
        this.imageUrls = imageUrls;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_review_image, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String imageUrl = imageUrls.get(position);
        Glide.with(holder.itemView.getContext()).load(imageUrl).into(holder.reviewImage);
    }

    @Override
    public int getItemCount() {
        return imageUrls.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView reviewImage;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            reviewImage = itemView.findViewById(R.id.review_image);
        }
    }
}
