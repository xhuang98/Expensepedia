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
    private List<Item> itemsList;

    public ItemAdapter(@NonNull Context context, ArrayList<Item> list) {
        super(context, 0 , list);
        mContext = context;
        itemsList = list;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View listItem = convertView;
        if(listItem == null)
            listItem = LayoutInflater.from(mContext).inflate(R.layout.list_item,parent,false);

        Item currentItem = itemsList.get(position);

        TextView itemName = (TextView)listItem.findViewById(R.id.itemName);
        itemName.setText(currentItem.name);

        TextView itemPrice = (TextView)listItem.findViewById(R.id.itemPrice);
        itemPrice.setText(currentItem.price.toString());

        ImageView icon = (ImageView)listItem.findViewById(R.id.imageView_poster);
        //icon.setImageResource();

        return listItem;
    }

}
