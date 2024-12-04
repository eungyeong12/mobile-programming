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
import jo.toybreeze.domain.Image;

public class ImageAdaptor extends RecyclerView.Adapter<ImageAdaptor.ViewHolder> {
    private List<Image> images;

    public ImageAdaptor(List<Image> images) {
        this.images = images;
    }

    @NonNull
    @Override
    public ImageAdaptor.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_review_image, parent, false);
        ImageAdaptor.ViewHolder viewHolder = new ImageAdaptor.ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Image img = images.get(position);

        Glide.with(holder.image.getContext())
                .load(img.getUrl())
                .thumbnail()
                .into(holder.image);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView image;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.review_image);
        }
    }

    @Override
    public int getItemCount() {
        return images.size();
    }
}
