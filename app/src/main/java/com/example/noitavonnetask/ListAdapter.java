package com.example.noitavonnetask;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.json.JSONException;

import java.util.ArrayList;

public class ListAdapter extends ArrayAdapter<Item> {
    Context context;
    ArrayList<Item> itemList;

    public ListAdapter(@NonNull Context context, ArrayList<Item> itemList) {
        super(context, R.layout.list_item, itemList);
        this.context = context;
        this.itemList = itemList;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        Item item = getItem(position);

        if(convertView == null){
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item, parent, false);
        }

        TextView keyTextView = convertView.findViewById(R.id.list_item_key);
        keyTextView.setText(item.getKey());

        TextView valueTextView = convertView.findViewById(R.id.list_item_value);
        valueTextView.setText(item.getValue());

        EditText valueEditText = convertView.findViewById(R.id.list_item_value_editText);
        ImageView editImageView = convertView.findViewById(R.id.list_item_edit);
        ImageView doneImageView = convertView.findViewById(R.id.list_item_done);
        ImageView deleteImageView = convertView.findViewById(R.id.list_item_delete);
        LinearLayout mainLayout = convertView.findViewById(R.id.mainLayout);

        if(item.getKey().equals("Email")){
            editImageView.setVisibility(View.GONE);
            deleteImageView.setVisibility(View.GONE);
        }

        editImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                valueTextView.setVisibility(View.GONE);
                valueEditText.setVisibility(View.VISIBLE);
                valueEditText.setText(valueTextView.getText().toString());
                editImageView.setVisibility(View.GONE);
                doneImageView.setVisibility(View.VISIBLE);
            }
        });

        doneImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    item.setValue(valueEditText.getText().toString());
                    SharedPreference.editValue(context, item.getKey(), item.getValue());
                    valueTextView.setVisibility(View.VISIBLE);
                    valueTextView.setText(valueEditText.getText().toString());
                    valueEditText.setVisibility(View.GONE);
                    editImageView.setVisibility(View.VISIBLE);
                    doneImageView.setVisibility(View.GONE);
                    InputMethodManager imm = (InputMethodManager)context.getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(mainLayout.getWindowToken(), 0);
                    valueEditText.clearFocus();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        deleteImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    SharedPreference.deleteValue(context, item.getKey());
                    itemList.remove(position);
                    notifyDataSetChanged();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        return convertView;
    }

}
