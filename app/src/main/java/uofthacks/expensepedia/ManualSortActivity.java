package uofthacks.expensepedia;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import java.util.ArrayList;

public class ManualSortActivity extends AppCompatActivity {

    private ListView listView;
    private ItemAdapter mAdapter;

    Button groceriesBtn;
    Button entertainmentBtn;
    Button clothingBtn;
    Button otherBtn;

    public static ArrayList<Item> itemsList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.manual_sort);

        groceriesBtn = (Button) findViewById(R.id.groceriesBtn);
        entertainmentBtn = (Button) findViewById(R.id.entertainmentBtn);
        clothingBtn = (Button) findViewById(R.id.clothingBtn);
        otherBtn = (Button) findViewById(R.id.otherBtn);

        listView = (ListView) findViewById(R.id.itemsList);
        itemsList = new ArrayList<>();
        itemsList.add(new Item("Banana", 4.56, "Groceries"));
        itemsList.add(new Item("Shoes", 4.56, "Groceries"));
        itemsList.add(new Item("Cocaine", 4.56, "Groceries"));
        itemsList.add(new Item("Basketball", 4.56, "Groceries"));

        mAdapter = new ItemAdapter(this, itemsList);
        listView.setAdapter(mAdapter);

        setBtn(clothingBtn, "Clothing");
        setBtn(entertainmentBtn, "Entertainment");
        setBtn(groceriesBtn, "Groceries");
        setBtn(otherBtn, "Other");
    }

    public void setBtn(Button btn, String category) {
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayList<Item> checkedItems = new ArrayList<>();

                for (int i = 0; i < itemsList.size(); i++) {
                    if (itemsList.get(i).checked) {

                        checkedItems.add(itemsList.remove(i));
                        i -= 1;
                        mAdapter.notifyDataSetChanged();
                    }
                }


                //TODO: call firebase function on checked items
            }
        });
    }
}
