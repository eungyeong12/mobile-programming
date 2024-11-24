package jo.toybreeze;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

import jo.toybreeze.domain.Company;
import jo.toybreeze.domain.Image;

public class FragmentSubscribe extends Fragment {
    private static final String TAG = FragmentSubscribe.class.getSimpleName();
    private SearchView searchView;
    private RecyclerView recyclerView;
    private FirebaseFirestore db;
    private CompanyAdaptor companyAdaptor;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_subscribe, container, false);

        searchView = view.findViewById(R.id.searchView);
        recyclerView = view.findViewById(R.id.recyclerView);
        db = FirebaseFirestore.getInstance();

        db.collection("logo")
                .addSnapshotListener((value, error) -> {
                    List<Company> companies = new ArrayList<>();

                    for (QueryDocumentSnapshot document : value) {
                        Company company = new Company(document.getId(), new Image(document.getData().get("url").toString()));
                        companies.add(company);
                    }

                    LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
                    recyclerView.setLayoutManager(linearLayoutManager);
                    companyAdaptor = new CompanyAdaptor(companies);
                    recyclerView.setAdapter(companyAdaptor);
                });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                companyAdaptor.filter(newText);
                return false;
            }
        });
        return view;
    }
}
