package com.zeus.office5;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.activeandroid.ActiveAndroid;
import com.activeandroid.query.Select;
import com.activeandroid.util.SQLiteUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Zeus on 4/26/2016.
 */
public class AdminDetailFragment extends android.support.v4.app.Fragment {

    public interface AdminLogoutInterface{
        public void adminLogoutClick(int searchTag);
    }
    SharedPreferences sp;
    AdminViewListviewAdapter adapter;
    List<UserInfo> userInfoList;
    int searchTag;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View v=inflater.inflate(R.layout.admin_detail_fragment_layout,container,false);
        Bundle b=getArguments();
        sp=getActivity().getSharedPreferences("login", Context.MODE_PRIVATE);
        searchTag=b.getInt("TabId");
        final String curAdminUsername=(String) b.getString("curAdminUsername");
        final LinearLayout raichu=(LinearLayout) v.findViewById(R.id.raichu);
        LinearLayout.LayoutParams layoutParams=new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        TextView welcomeTv=new TextView(getActivity());
        welcomeTv.setTextSize(25);
        welcomeTv.setGravity(Gravity.CENTER_HORIZONTAL);
        String orig="Welcome Admin: "+curAdminUsername;
        welcomeTv.setText("Welcome Admin: "+curAdminUsername);
        welcomeTv.setLayoutParams(layoutParams);
        raichu.addView(welcomeTv);


        sp=getActivity().getSharedPreferences("login", Context.MODE_PRIVATE);
        int lock=sp.getInt("lock",0);
        Button logoutButton=new Button(getActivity());
        logoutButton.setText("Abort & Logout.");
        logoutButton.setTextSize(19);
        logoutButton.setGravity(Gravity.CENTER_HORIZONTAL);
        logoutButton.setLayoutParams(layoutParams);

        Button commitButton=new Button(getActivity());
        commitButton.setText("Commit & Logout.");
        commitButton.setTextSize(19);
        commitButton.setGravity(Gravity.CENTER_HORIZONTAL);
        commitButton.setLayoutParams(layoutParams);
        commitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayList<UserInfo> tempChanges= (ArrayList<UserInfo>) adapter.modifiedList;
                for(UserInfo ui:tempChanges){
                    ui.save();
//                    Log.i("checkYou",""+ui.salary);
                }
                ActiveAndroid.setTransactionSuccessful();
                ActiveAndroid.endTransaction();
                adapter.modifiedList.clear();
                raichu.removeAllViews();
                AdminLogoutInterface userLogoutInterfaceListener= (AdminLogoutInterface) getActivity();
                userLogoutInterfaceListener.adminLogoutClick(searchTag);
            }
        });


        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ActiveAndroid.endTransaction();
                raichu.removeAllViews();
                AdminLogoutInterface userLogoutInterfaceListener= (AdminLogoutInterface) getActivity();
                userLogoutInterfaceListener.adminLogoutClick(searchTag);
            }
        });

        Button newUserRegister=new Button(getActivity());
        newUserRegister.setText("Register new Employee");
        newUserRegister.setTextSize(17);
        newUserRegister.setGravity(Gravity.LEFT);
        newUserRegister.setLayoutParams(layoutParams);
        newUserRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder bd=new AlertDialog.Builder(getActivity());
                bd.setTitle("New Employee Registration");
                bd.setMessage("Enter details-");
                View dialogView=getLayoutInflater(null).inflate(R.layout.new_employee_register_dialog,null);
                final EditText newUsername=(EditText) dialogView.findViewById(R.id.NewEmployeeRegisterDialogUsername);
                final EditText newPassword=(EditText) dialogView.findViewById(R.id.NewEmployeeRegisterDialogPassword);
                final EditText newSalary=(EditText) dialogView.findViewById(R.id.NewEmployeeRegisterDialogSalary);
                bd.setView(dialogView);

                bd.setNeutralButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                bd.setPositiveButton("Register", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String str1=newUsername.getText().toString();
                        String str2=newPassword.getText().toString();
                        String str3=newSalary.getText().toString();
                        if(str1!=null&&str2!=null&&str3!=null){
                            UserInfo newUserCreate=new UserInfo(str1,str2,Double.parseDouble(str3));
                            Toast.makeText(getActivity(),newUsername+" registered",Toast.LENGTH_LONG);
                            newUserCreate.save();
                            userInfoList.add(newUserCreate);
                            adapter.notifyDataSetChanged();
                        }else{
                            Toast.makeText(getActivity(), "Invalid credentials", Toast.LENGTH_SHORT).show();
                        }

                    }
                });
                bd.create().show();

            }
        });

        ListView listView=new ListView(getActivity());
        LinearLayout.LayoutParams listviewParams=new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        listView.setLayoutParams(listviewParams);
        userInfoList= SQLiteUtils.rawQuery(UserInfo.class,
                "SELECT * from UserInfo",new String[]{});

        adapter=new AdminViewListviewAdapter(getActivity(),userInfoList);
        listView.setAdapter(adapter);

        List<AllowedTags> allowedTags= new Select().from(AllowedTags.class).where("tag=?",searchTag).execute();
        if(allowedTags!=null&&allowedTags.size()>0){
            welcomeTv.setText("Hello "+curAdminUsername);
            userInfoList= SQLiteUtils.rawQuery(UserInfo.class,
                    "SELECT * from UserInfo",new String[]{});
            raichu.addView(listView);
            raichu.addView(newUserRegister);
            raichu.addView(commitButton);
            Log.i("found id",""+searchTag);
        }else if(lock==0){
            Log.i("lock",""+lock);
            SharedPreferences.Editor editor=sp.edit();
            lock=-1;
            editor.putInt("lock",lock).commit();
            Log.i("lock",""+lock);
            Log.i("Added tag",""+searchTag);
            AllowedTags at=new AllowedTags(searchTag);
            at.save();
            ActiveAndroid.beginTransaction();
            welcomeTv.setText("Hello "+curAdminUsername);
            raichu.addView(listView);
            raichu.addView(newUserRegister);
            raichu.addView(commitButton);

        }else{
            welcomeTv.setText(orig+"\nAnother reading/writing session already in progress");
        }

        raichu.addView(logoutButton);
        return v;
    }
}
