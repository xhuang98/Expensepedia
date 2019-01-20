package uofthacks.expensepedia;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ListView;

import java.util.ArrayList;

public class ManualSortActivity extends AppCompatActivity {

    private ListView listView;
    private ItemAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.manual_sort);

        listView = (ListView) findViewById(R.id.itemsList);
        ArrayList<Item> itemsList = new ArrayList<>();
        itemsList.add(new Item("Banana", 4.56, "Groceries"));
        itemsList.add(new Item("Shoes", 4.56, "Groceries"));
        itemsList.add(new Item("Cocaine", 4.56, "Groceries"));
        itemsList.add(new Item("Basketball", 4.56, "Groceries"));

        mAdapter = new ItemAdapter(this, itemsList);
        listView.setAdapter(mAdapter);
    }
}
