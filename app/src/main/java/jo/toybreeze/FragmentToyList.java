package jo.toybreeze;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SearchView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

import jo.toybreeze.adaptor.MainToyAdaptor;
import jo.toybreeze.domain.Query;
import jo.toybreeze.domain.Toy;

public class FragmentToyList extends Fragment {
    private static final String TAG = FragmentToyList.class.getSimpleName();
    private ImageView back;
    private SearchView searchView;
    private RecyclerView recyclerView;
    private FirebaseFirestore db;
    private MainToyAdaptor mainToyAdaptor;
    private String keyword;
    private String age;
    private List<String> category;
    private List<String> tags;
    private boolean isFiltering;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_toy_list, container, false);
        back = view.findViewById(R.id.back);
        searchView = view.findViewById(R.id.searchView);
        recyclerView = view.findViewById(R.id.recyclerView);
        db = FirebaseFirestore.getInstance();

        Bundle args = getArguments();
        if (args != null) {
            Query query = (Query) args.getSerializable("query");
            keyword = query.getKeyword();
            age = query.getAge();
            category = query.getCategory();
            tags = query.getTags();
        }

        searchView.setQuery(keyword, false);
        setToys();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                if (isFiltering) {
                    db.collection("toys")
                            .whereArrayContainsAny("tags", tags)
                            .addSnapshotListener((value, error) -> {
                                List<Toy> toys = new ArrayList<>();
                                List<String> toyIds = new ArrayList<>();

                                for (QueryDocumentSnapshot document : value) {
                                    Toy toy = document.toObject(Toy.class);
                                    if (toy.getAge().contains(age) && category.contains(toy.getCategory())) {
                                        if (toy.getName().contains(s) || toy.getCompany().contains(s) || toy.getDescription().contains(s)) {
                                            toys.add(toy);
                                            toyIds.add(document.getId());
                                        }
                                    }
                                }
                                GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 2);
                                recyclerView.setLayoutManager(gridLayoutManager);
                                recyclerView.setNestedScrollingEnabled(false);
                                mainToyAdaptor = new MainToyAdaptor(toys, toyIds);
                                mainToyAdaptor.setOnItemClickListener((position, data) -> {
                                    Intent intent2 = new Intent(getContext(), ToyDetailActivity.class);
                                    intent2.putExtra("data", data);
                                    startActivity(intent2);
                                });
                                recyclerView.setAdapter(mainToyAdaptor);
                            });
                } else {
                    db.collection("toys")
                            .addSnapshotListener((value, error) -> {
                                List<Toy> toys = new ArrayList<>();
                                List<String> toyIds = new ArrayList<>();

                                for (QueryDocumentSnapshot document : value) {
                                    Toy toy = document.toObject(Toy.class);
                                    if (toy.getName().contains(s) || toy.getCompany().contains(s) || toy.getDescription().contains(s)) {
                                        toys.add(toy);
                                        toyIds.add(document.getId());
                                    }
                                }
                                GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 2);
                                recyclerView.setLayoutManager(gridLayoutManager);
                                recyclerView.setNestedScrollingEnabled(false);
                                mainToyAdaptor = new MainToyAdaptor(toys, toyIds);
                                mainToyAdaptor.setOnItemClickListener((position, data) -> {
                                    Intent intent2 = new Intent(getContext(), ToyDetailActivity.class);
                                    intent2.putExtra("data", data);
                                    startActivity(intent2);
                                });
                                recyclerView.setAdapter(mainToyAdaptor);
                            });
                }

                return true;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                return false;
            }
        });

        back.setOnClickListener(view1 -> {
            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
            if (fragmentManager.getBackStackEntryCount() > 0) {
                fragmentManager.popBackStack();
            }
        });

        return view;
    }

    private void setToys() {
        if (keyword != null) {
            isFiltering = false;
            db.collection("toys")
                    .addSnapshotListener((value, error) -> {
                        List<Toy> toys = new ArrayList<>();
                        List<String> toyIds = new ArrayList<>();

                        for (QueryDocumentSnapshot document : value) {
                            Toy toy = document.toObject(Toy.class);
                            if (toy.getName().contains(keyword) || toy.getCompany().contains(keyword) || toy.getDescription().contains(keyword)) {
                                toys.add(toy);
                                toyIds.add(document.getId());
                            }
                        }
                        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 2);
                        recyclerView.setLayoutManager(gridLayoutManager);
                        recyclerView.setNestedScrollingEnabled(false);
                        mainToyAdaptor = new MainToyAdaptor(toys, toyIds);
                        mainToyAdaptor.setOnItemClickListener((position, data) -> {
                            Intent intent2 = new Intent(getContext(), ToyDetailActivity.class);
                            intent2.putExtra("data", data);
                            startActivity(intent2);
                        });
                        recyclerView.setAdapter(mainToyAdaptor);
                    });
        } else if (age != null && category != null && tags != null) {
            isFiltering = true;
            db.collection("toys")
                    .whereArrayContainsAny("tags", tags)
                    .addSnapshotListener((value, error) -> {
                        List<Toy> toys = new ArrayList<>();
                        List<String> toyIds = new ArrayList<>();

                        for (QueryDocumentSnapshot document : value) {
                            Toy toy = document.toObject(Toy.class);
                            if (toy.getAge().contains(age) && category.contains(toy.getCategory())) {
                                toys.add(toy);
                                toyIds.add(document.getId());
                            }
                        }
                        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 2);
                        recyclerView.setLayoutManager(gridLayoutManager);
                        recyclerView.setNestedScrollingEnabled(false);
                        mainToyAdaptor = new MainToyAdaptor(toys, toyIds);
                        mainToyAdaptor.setOnItemClickListener((position, data) -> {
                            Intent intent2 = new Intent(getContext(), ToyDetailActivity.class);
                            intent2.putExtra("data", data);
                            startActivity(intent2);
                        });
                        recyclerView.setAdapter(mainToyAdaptor);
                    });
        }
    }
}
