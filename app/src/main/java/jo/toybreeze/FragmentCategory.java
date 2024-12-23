package jo.toybreeze;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.ramotion.fluidslider.FluidSlider;

import java.util.ArrayList;
import java.util.List;

import jo.toybreeze.domain.Query;
import kotlin.Unit;

public class FragmentCategory extends Fragment {
    private static final String TAG = FragmentCategory.class.getSimpleName();
    private FluidSlider slider;
    private ImageView category1;
    private ImageView category2;
    private ImageView category3;
    private ImageView category4;
    private ImageView category5;
    private ImageView category6;
    private TextView category1_text;
    private TextView category2_text;
    private TextView category3_text;
    private TextView category4_text;
    private TextView category5_text;
    private TextView category6_text;
    private boolean checkCategory1;
    private boolean checkCategory2;
    private boolean checkCategory3;
    private boolean checkCategory4;
    private boolean checkCategory5;
    private boolean checkCategory6;

    private CheckBox tag_age1;
    private CheckBox tag_age2;
    private CheckBox tag_age3;
    private CheckBox tag_age4;
    private CheckBox tag_merit1;
    private CheckBox tag_merit2;
    private CheckBox tag_merit3;
    private CheckBox tag_feature1;
    private CheckBox tag_feature2;
    private CheckBox tag_feature3;
    private CheckBox tag_use1;
    private CheckBox tag_use2;
    private CheckBox tag_use3;
    private CheckBox tag_use4;

    private Button submit;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_category, container, false);

        slider = view.findViewById(R.id.fluidSlider);
        category1 = view.findViewById(R.id.category1);
        category2 = view.findViewById(R.id.category2);
        category3 = view.findViewById(R.id.category3);
        category4 = view.findViewById(R.id.category4);
        category5 = view.findViewById(R.id.category5);
        category6 = view.findViewById(R.id.category6);
        category1_text = view.findViewById(R.id.category1_reverse);
        category2_text = view.findViewById(R.id.category2_reverse);
        category3_text = view.findViewById(R.id.category3_reverse);
        category4_text = view.findViewById(R.id.category4_reverse);
        category5_text = view.findViewById(R.id.category5_reverse);
        category6_text = view.findViewById(R.id.category6_reverse);
        checkCategory1 = false;
        checkCategory2 = false;
        checkCategory3 = false;
        checkCategory4 = false;
        checkCategory5 = false;
        checkCategory6 = false;

        tag_age1 = view.findViewById(R.id.tag_age1);
        tag_age2 = view.findViewById(R.id.tag_age2);
        tag_age3 = view.findViewById(R.id.tag_age3);
        tag_age4 = view.findViewById(R.id.tag_age4);
        tag_merit1 = view.findViewById(R.id.tag_merit1);
        tag_merit2 = view.findViewById(R.id.tag_merit2);
        tag_merit3 = view.findViewById(R.id.tag_merit3);
        tag_feature1 = view.findViewById(R.id.tag_feature1);
        tag_feature2 = view.findViewById(R.id.tag_feature2);
        tag_feature3 = view.findViewById(R.id.tag_feature3);
        tag_use1 = view.findViewById(R.id.tag_use1);
        tag_use2 = view.findViewById(R.id.tag_use2);
        tag_use3 = view.findViewById(R.id.tag_use3);
        tag_use4 = view.findViewById(R.id.tag_use4);

        submit = view.findViewById(R.id.submit);

        category1.setOnClickListener(view1 -> {
            category1.setVisibility(View.INVISIBLE);
            category1_text.setVisibility(View.VISIBLE);
            checkCategory1 = true;
        });

        category1_text.setOnClickListener(view12 -> {
            category1.setVisibility(View.VISIBLE);
            category1_text.setVisibility(View.INVISIBLE);
            checkCategory1 = false;
        });

        category2.setOnClickListener(view13 -> {
            category2.setVisibility(View.INVISIBLE);
            category2_text.setVisibility(View.VISIBLE);
            checkCategory2 = true;
        });

        category2_text.setOnClickListener(view14 -> {
            category2.setVisibility(View.VISIBLE);
            category2_text.setVisibility(View.INVISIBLE);
            checkCategory2 = false;
        });

        category3.setOnClickListener(view15 -> {
            category3.setVisibility(View.INVISIBLE);
            category3_text.setVisibility(View.VISIBLE);
            checkCategory3 = true;
        });

        category3_text.setOnClickListener(view16 -> {
            category3.setVisibility(View.VISIBLE);
            category3_text.setVisibility(View.INVISIBLE);
            checkCategory3 = false;
        });

        category4.setOnClickListener(view17 -> {
            category4.setVisibility(View.INVISIBLE);
            category4_text.setVisibility(View.VISIBLE);
            checkCategory4 = true;
        });

        category4_text.setOnClickListener(view18 -> {
            category4.setVisibility(View.VISIBLE);
            category4_text.setVisibility(View.INVISIBLE);
            checkCategory4 = false;
        });

        category5.setOnClickListener(view19 -> {
            category5.setVisibility(View.INVISIBLE);
            category5_text.setVisibility(View.VISIBLE);
            checkCategory5 = true;
        });

        category5_text.setOnClickListener(view110 -> {
            category5.setVisibility(View.VISIBLE);
            category5_text.setVisibility(View.INVISIBLE);
            checkCategory5 = false;
        });

        category6.setOnClickListener(view111 -> {
            category6.setVisibility(View.INVISIBLE);
            category6_text.setVisibility(View.VISIBLE);
            checkCategory6 = true;
        });

        category6_text.setOnClickListener(view112 -> {
            category6.setVisibility(View.VISIBLE);
            category6_text.setVisibility(View.INVISIBLE);
            checkCategory6 = false;
        });

        setSlider();

        submit.setOnClickListener(view113 -> {
            Bundle bundle = new Bundle();
            Query query = getQuery();
            bundle.putSerializable("query", query);
            FragmentToyList toyList = new FragmentToyList();
            toyList.setArguments(bundle);

            FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.main_container, toyList);
            transaction.addToBackStack(null);
            transaction.commit();
        });

        return view;
    }

    private Query getQuery() {
        int age = (int) (0 + 9 * slider.getPosition());
        List<String> category = getCategory();
        List<String> tags = getTags();
        return new Query(String.valueOf(age), category, tags);
    }

    private List<String> getCategory() {
        List<String> category = new ArrayList<>();
        if (checkCategory1) {
            category.add(category1_text.getText().toString());
        }
        if (checkCategory2) {
            category.add(category2_text.getText().toString());
        }
        if (checkCategory3) {
            category.add(category3_text.getText().toString());
        }
        if (checkCategory4) {
            category.add(category4_text.getText().toString());
        }
        if (checkCategory5) {
            category.add(category5_text.getText().toString());
        }
        if (checkCategory6) {
            category.add(category6_text.getText().toString());
        }
        return category;
    }

    private List<String> getTags() {
        List<String> tags = new ArrayList<>();
        if (tag_age1.isChecked()) {
            tags.add(tag_age1.getText().toString());
        }
        if (tag_age2.isChecked()) {
            tags.add(tag_age2.getText().toString());
        }
        if (tag_age3.isChecked()) {
            tags.add(tag_age3.getText().toString());
        }
        if (tag_age4.isChecked()) {
            tags.add(tag_age4.getText().toString());
        }
        if (tag_merit1.isChecked()) {
            tags.add(tag_merit1.getText().toString());
        }
        if (tag_merit2.isChecked()) {
            tags.add(tag_merit2.getText().toString());
        }
        if (tag_merit3.isChecked()) {
            tags.add(tag_merit3.getText().toString());
        }
        if (tag_feature1.isChecked()) {
            tags.add(tag_feature1.getText().toString());
        }
        if (tag_feature2.isChecked()) {
            tags.add(tag_feature2.getText().toString());
        }
        if (tag_feature3.isChecked()) {
            tags.add(tag_feature3.getText().toString());
        }
        if (tag_use1.isChecked()) {
            tags.add(tag_use1.getText().toString());
        }
        if (tag_use2.isChecked()) {
            tags.add(tag_use2.getText().toString());
        }
        if (tag_use3.isChecked()) {
            tags.add(tag_use3.getText().toString());
        }
        if (tag_use4.isChecked()) {
            tags.add(tag_use4.getText().toString());
        }
        return tags;
    }

    private void setSlider() {
        final int max = 9;
        final int min = 0;
        final int total = max - min;

        slider.setBeginTrackingListener(() -> Unit.INSTANCE);

        slider.setEndTrackingListener(() -> Unit.INSTANCE);

        slider.setPositionListener(pos -> {
            final String value = String.valueOf( (int)(min + total * pos) );
            slider.setBubbleText(value);
            return Unit.INSTANCE;
        });

        slider.setPosition(0.0f);
        slider.setStartText(String.valueOf(min));
        slider.setEndText(String.valueOf(max));
    }
}
