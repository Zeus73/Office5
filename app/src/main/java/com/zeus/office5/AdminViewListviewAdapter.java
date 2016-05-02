package com.zeus.office5;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Zeus on 4/26/2016.
 */
public class AdminViewListviewAdapter extends ArrayAdapter<UserInfo> {

    Context context;
    List<UserInfo> userInfoList;
    List<UserInfo> modifiedList;
    public AdminViewListviewAdapter(Context context, List<UserInfo> userInfoList) {
        super(context, 0, userInfoList);
        this.context=context;
        modifiedList=new ArrayList<>();
        this.userInfoList=userInfoList;
    }
    public class UserInfoListViewHolder{
        TextView username;
        TextView currentSalary;
        EditText newSalary;
        Button saveButton;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView==null){
            convertView=View.inflate(context,R.layout.admin_listview_item,null);
            UserInfoListViewHolder vh=new UserInfoListViewHolder();
            vh.username=(TextView) convertView.findViewById(R.id.adminListItemUsername);
            vh.currentSalary=(TextView) convertView.findViewById(R.id.adminListItemSalary);
            vh.newSalary=(EditText) convertView.findViewById(R.id.adminListItemNewSalaryEditText);
            vh.saveButton=(Button) convertView.findViewById(R.id.adminListItemSaveNewSalaryButton);
            convertView.setTag(vh);
        }

        final UserInfo curUser=userInfoList.get(position);
        final UserInfoListViewHolder vh= (UserInfoListViewHolder) convertView.getTag();
        vh.username.setText(curUser.username);
        vh.currentSalary.setText("Rs "+curUser.salary);
        vh.saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String tempStr=vh.newSalary.getText().toString();
                if(!tempStr.equals("")){
                    Double newSal=Double.parseDouble(tempStr);
                    curUser.salary = newSal;
//                    curUser.save();
                    modifiedList.add(curUser);
                    Toast.makeText(context, "Changes added to be committed", Toast.LENGTH_SHORT).show();
//                    vh.newSalary.setText("");
//                    vh.currentSalary.setText(String.valueOf(newSal));
                }else{
                    Toast.makeText(context, "Please enter a valid Salary", Toast.LENGTH_SHORT).show();
                }
            }
        });
        return convertView;
    }
}
