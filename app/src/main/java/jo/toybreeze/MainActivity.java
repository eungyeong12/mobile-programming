package jo.toybreeze;

import android.os.Bundle;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.firebase.firestore.FirebaseFirestore;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import jo.toybreeze.domain.Image;
import jo.toybreeze.domain.Toy;
import jxl.Sheet;
import jxl.Workbook;
import jxl.read.biff.BiffException;

public class MainActivity extends AppCompatActivity {
    private FragmentManager fragmentManager = getSupportFragmentManager();
    private FragmentCategory fragmentCategory = new FragmentCategory();
    private FragmentHome fragmentHome = new FragmentHome();
    private ImageView category;
    private ImageView home;
    private FirebaseFirestore db;

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

        db = FirebaseFirestore.getInstance();

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

    public void readExcel() {
        try {
            InputStream is = getBaseContext().getResources().getAssets().open("toy.xls");
            Workbook wb = Workbook.getWorkbook(is);
            if(wb != null){

                Sheet sheet = wb.getSheet(0);

                if(sheet != null){
                    int colTotal = sheet.getColumns();
                    int rowIndexStart = 1;
                    int rowTotal = sheet.getColumn(colTotal-1).length;

                    for(int row = rowIndexStart; row < rowTotal; row++) {
                        String company = sheet.getCell(0, row).getContents();
                        String name = sheet.getCell(1, row).getContents();
                        String image = sheet.getCell(2, row).getContents();
                        String age = sheet.getCell(3, row).getContents();
                        String category = sheet.getCell(4, row).getContents();
                        String tag = sheet.getCell(5, row).getContents();
                        List<String> tags = Arrays.stream(tag.split(", ")).collect(Collectors.toList());
                        int monthPrice = Integer.parseInt(sheet.getCell(6, row).getContents());
                        int threeMonthPrice = Integer.parseInt(sheet.getCell(7, row).getContents());
                        int quantity = Integer.parseInt(sheet.getCell(8, row).getContents());
                        int sellQuantity = Integer.parseInt(sheet.getCell(9, row).getContents());
                        String description = sheet.getCell(10, row).getContents();
                        String policy = sheet.getCell(11, row).getContents();

                        String documentId = db.collection("toys").document().getId();
                        Toy toy = new Toy(company, name, age, category, tags, monthPrice, threeMonthPrice, quantity, sellQuantity, description, policy);
                        Image img = new Image(image);

                        db.collection("toys")
                                .document(documentId)
                                .set(toy)
                                .addOnSuccessListener(aVoid -> {
                                    db.collection("companies").document(company).collection("toys").document(documentId).set(toy);
                                    db.collection("images").document(documentId).set(img);
                                });
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (BiffException e) {
            throw new RuntimeException(e);
        }
    }
}