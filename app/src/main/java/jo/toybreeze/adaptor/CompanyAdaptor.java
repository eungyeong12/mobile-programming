package jo.toybreeze.adaptor;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import jo.toybreeze.R;
import jo.toybreeze.domain.Company;

public class CompanyAdaptor extends RecyclerView.Adapter<CompanyAdaptor.ViewHolder> {
    private List<Company> companies;
    private List<Company> allCompanies;

    public CompanyAdaptor(List<Company> companies) {
        this.companies = companies;
        this.allCompanies = new ArrayList<>(companies);
    }

    public interface OnItemClickListener {
        void onItemClicked(int position, String company);
    }

    private CompanyAdaptor.OnItemClickListener itemClickListener;

    public void setOnItemClickListener(CompanyAdaptor.OnItemClickListener listener) {
        itemClickListener = listener;
    }

    @NonNull
    @Override
    public CompanyAdaptor.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_company, parent, false);
        CompanyAdaptor.ViewHolder viewHolder = new CompanyAdaptor.ViewHolder(view);

        view.setOnClickListener(view1 -> {
            int position = viewHolder.getBindingAdapterPosition();
            String name = "";
            if (position != RecyclerView.NO_POSITION) {
                Company company = companies.get(position);
                name = company.getName();
            }
            itemClickListener.onItemClicked(position, name);
        });
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

    public void filter(String text) {
        companies.clear();
        if (text.isEmpty()) {
            companies.addAll(allCompanies);
        } else {
            text = text.toLowerCase();
            for (Company company : allCompanies) {
                if (company.getName().toLowerCase().contains(text)) {
                    companies.add(company);
                }
            }
        }
        notifyDataSetChanged();
    }
}
