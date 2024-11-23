package jo.toybreeze;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {
    private FragmentManager fragmentManager = getSupportFragmentManager();
    private FragmentCategory fragmentCategory = new FragmentCategory();
    private FragmentHome fragmentHome = new FragmentHome();
    private ImageView category;
    private ImageView home;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.main_container, fragmentHome).commitAllowingStateLoss();

        category = findViewById(R.id.category);
        home = findViewById(R.id.home);

        category.setOnClickListener(view -> {
            FragmentTransaction transaction1 = fragmentManager.beginTransaction();
            transaction1.replace(R.id.main_container, fragmentCategory).commitAllowingStateLoss();
        });

        home.setOnClickListener(view -> {
            FragmentTransaction transaction1 = fragmentManager.beginTransaction();
            transaction1.replace(R.id.main_container, fragmentHome).commitAllowingStateLoss();
        });
    }
}