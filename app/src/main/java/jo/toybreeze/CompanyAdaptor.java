package jo.toybreeze;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.tabs.TabLayout;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import jo.toybreeze.domain.Company;

public class CompanyAdaptor extends RecyclerView.Adapter<CompanyAdaptor.ViewHolder> {
    private List<Company> companies;
    private Bitmap bitmap;

    public CompanyAdaptor(List<Company> companies) {
        this.companies = new ArrayList<>(companies);
    }

    @NonNull
    @Override
    public CompanyAdaptor.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_company, parent, false);
        CompanyAdaptor.ViewHolder viewHolder = new CompanyAdaptor.ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull CompanyAdaptor.ViewHolder holder, int position) {
        Company company = companies.get(position);

        Glide.with(holder.logo.getContext())
                .load(company.getLogo().getUrl())
                .dontAnimate()
                .thumbnail()
                .into(holder.logo);

        holder.name.setText(company.getName());
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private de.hdodenhof.circleimageview.CircleImageView logo;
        private TextView name;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            logo = itemView.findViewById(R.id.item_logo);
            name = itemView.findViewById(R.id.item_company_name);
        }
    }

    @Override
    public int getItemCount() {
        return companies.size();
    }
}
