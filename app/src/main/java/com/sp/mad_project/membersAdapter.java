package com.sp.mad_project;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import java.util.List;

public class membersAdapter extends ArrayAdapter<String> {

    private List<String> selectedMembers;

    public membersAdapter(Context context, List<String> members, List<String> selectedMembers) {
        super(context, 0, members);
        this.selectedMembers = selectedMembers;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_member, parent, false);
        }

        String member = getItem(position);
        TextView memberName = convertView.findViewById(R.id.memberName);
        CheckBox checkBox = convertView.findViewById(R.id.memberCheckBox);

        memberName.setText(member);

        checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                selectedMembers.add(member);
            } else {
                selectedMembers.remove(member);
            }
        });

        return convertView;
    }
}
