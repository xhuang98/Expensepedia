package uofthacks.expensepedia;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class ItemAdapter extends ArrayAdapter<Item> {

    private Context mContext;

    public ItemAdapter(@NonNull Context context, ArrayList<Item> list) {
        super(context, 0 , list);
        mContext = context;
    }

    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View listItem = convertView;
        if(listItem == null)
            listItem = LayoutInflater.from(mContext).inflate(R.layout.list_item,parent,false);

        Item currentItem = ManualSortActivity.itemsList.get(position);
        currentItem.v = listItem;

        TextView itemName = (TextView)listItem.findViewById(R.id.itemName);
        itemName.setText(currentItem.name);

        TextView itemPrice = (TextView)listItem.findViewById(R.id.itemPrice);
        itemPrice.setText(currentItem.price.toString());

        ImageView icon = (ImageView)listItem.findViewById(R.id.imageView_poster);
        //icon.setImageResource();

        View listItem2 = listItem;
        listItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("LOGGING");
                Item item = ManualSortActivity.itemsList.get(position);
                if (item.checked) {
                    System.out.println("running");
                    item.checked = false;
                    listItem2.setBackgroundColor(listItem2.getResources().getColor(R.color.colorAccent));
                } else {
                    System.out.println("running 2");
                    item.checked = true;
                    listItem2.setBackgroundColor(listItem2.getResources().getColor(R.color.colorPrimary));
                }
            }
        });

        return listItem;
    }
}
