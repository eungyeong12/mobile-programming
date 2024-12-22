package jo.toybreeze;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SearchView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

import jo.toybreeze.adaptor.BestToyAdaptor;
import jo.toybreeze.adaptor.MainToyAdaptor;
import jo.toybreeze.domain.Query;
import jo.toybreeze.domain.Toy;

public class FragmentHome  extends Fragment {
    private static final String TAG = FragmentHome.class.getSimpleName();
    private ImageView cart;
    private RecyclerView recyclerView1;
    private RecyclerView recyclerview2;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private BestToyAdaptor bestToyAdaptor;
    private MainToyAdaptor mainToyAdaptor;
    private SearchView searchView;
    private ImageView logout;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        cart = view.findViewById(R.id.cart);
        recyclerView1 = view.findViewById(R.id.recyclerView1);
        recyclerview2 = view.findViewById(R.id.recyclerView2);
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        searchView = view.findViewById(R.id.searchView);
        logout = view.findViewById(R.id.logout);

        db.collection("toys")
            .orderBy("sellQuantity", com.google.firebase.firestore.Query.Direction.DESCENDING)
                .addSnapshotListener((value, error) -> {
                    List<Toy> toys = new ArrayList<>();
                    List<String> toyIds = new ArrayList<>();

                    for (QueryDocumentSnapshot document : value) {
                        if (toys.size() == 10) break;
                        Toy toy = document.toObject(Toy.class);
                        toys.add(toy);
                        toyIds.add(document.getId());
                        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
                        recyclerView1.setLayoutManager(linearLayoutManager);
                        bestToyAdaptor = new BestToyAdaptor(toys, toyIds);
                        bestToyAdaptor.setOnItemClickListener((position, data) -> {
                            Intent intent = new Intent(getContext(), ToyDetailActivity.class);
                            intent.putExtra("data", data);
                            startActivity(intent);
                        });
                        recyclerView1.setAdapter(bestToyAdaptor);
                    }
                });

        db.collection("toys")
                .addSnapshotListener((value, error) -> {
                    List<Toy> toys = new ArrayList<>();
                    List<String> toyIds = new ArrayList<>();

                    for (QueryDocumentSnapshot document : value) {
                        Toy toy = document.toObject(Toy.class);
                        toys.add(toy);
                        toyIds.add(document.getId());
                    }
                    GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 2);
                    recyclerview2.setLayoutManager(gridLayoutManager);
                    recyclerview2.setNestedScrollingEnabled(false);
                    mainToyAdaptor = new MainToyAdaptor(toys, toyIds);
                    mainToyAdaptor.setOnItemClickListener((position, data) -> {
                        Intent intent = new Intent(getContext(), ToyDetailActivity.class);
                        intent.putExtra("data", data);
                        startActivity(intent);
                    });
                    recyclerview2.setAdapter(mainToyAdaptor);
                });

        cart.setOnClickListener(view1 -> {
            startActivity(new Intent(getContext(), CartActivity.class));
        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                Bundle bundle = new Bundle();
                Query query = new Query(s);
                bundle.putSerializable("query", query);
                FragmentToyList toyList = new FragmentToyList();
                toyList.setArguments(bundle);

                FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.main_container, toyList);
                transaction.addToBackStack(null);
                transaction.commit();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                return false;
            }
        });

        logout.setOnClickListener(view12 -> {
            mAuth.signOut();
            startActivity(new Intent(getContext(), LoginActivity.class));
        });

        return view;
    }
}
