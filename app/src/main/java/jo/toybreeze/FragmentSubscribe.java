package jo.toybreeze;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import jo.toybreeze.domain.Company;
import jo.toybreeze.domain.Image;

public class FragmentSubscribe extends Fragment {
    private static final String TAG = FragmentSubscribe.class.getSimpleName();
    private RecyclerView recyclerView;
    private FirebaseFirestore db;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_subscribe, container, false);

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
                    CompanyAdaptor companyAdaptor = new CompanyAdaptor(companies);
                    recyclerView.setAdapter(companyAdaptor);
                });
        return view;
    }
}
