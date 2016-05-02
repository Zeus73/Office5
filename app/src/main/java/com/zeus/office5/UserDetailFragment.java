package com.zeus.office5;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.activeandroid.query.Select;
import com.activeandroid.util.SQLiteUtils;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by Zeus on 4/24/2016.
 */
public class UserDetailFragment extends android.support.v4.app.Fragment {
    public interface UserLogoutInterface{
        public void userLogoutClick(int searchTag);
    }
    SharedPreferences sp;
    int searchTag;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v= inflater.inflate(R.layout.user_detail_fragment_layout,container,false);
        Bundle b=getArguments();
        final String curUserUsername=(String) b.getString("curUserUsername");
//        Log.i("checkUsername",curUserUsername);
        final TextView tv=(TextView) v.findViewById(R.id.userDetailTextView1);
        tv.setText(curUserUsername);
        final LinearLayout pikachu=(LinearLayout) v.findViewById(R.id.pikachuLinearLayout);

        LinearLayout.LayoutParams layoutParams=new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        final TextView salTv=new TextView(getActivity());
        salTv.setTextSize(25);
        salTv.setGravity(Gravity.CENTER_HORIZONTAL);
        salTv.setText("Loading Salary");
        salTv.setLayoutParams(layoutParams);
        pikachu.addView(salTv);

        sp=getActivity().getSharedPreferences("login", Context.MODE_PRIVATE);
        int lock=sp.getInt("lock",0);

        Button logoutButton=new Button(getActivity());
        logoutButton.setText("Logout.");
        logoutButton.setTextSize(30);
        logoutButton.setGravity(Gravity.CENTER_HORIZONTAL);
        logoutButton.setLayoutParams(layoutParams);
        pikachu.addView(logoutButton);
        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pikachu.removeAllViews();
                UserLogoutInterface userLogoutInterfaceListener= (UserLogoutInterface) getActivity();
                userLogoutInterfaceListener.userLogoutClick(searchTag);
            }
        });

        searchTag=b.getInt("TabId");

        List<AllowedTags> allowedTags= new Select().from(AllowedTags.class).where("tag=?",searchTag).execute();
        if(allowedTags!=null&&allowedTags.size()>0){
            List<UserInfo> registeredUsers= SQLiteUtils.rawQuery(UserInfo.class,
                    "SELECT * from UserInfo where username= ?",new String[]{curUserUsername});
            UserInfo curUser=registeredUsers.get(0);
            salTv.setText("Salary: Rs "+curUser.salary);
            Log.i("found id",""+searchTag);
        }else if(lock>=0){
            lock=sp.getInt("lock",0);
            Log.i("lock",""+lock);
            lock++;
            SharedPreferences.Editor editor=sp.edit();
            editor.putInt("lock",lock).commit();
            Log.i("lock",""+lock);
            Log.i("Added tag",""+searchTag);
            AllowedTags at=new AllowedTags(searchTag);
            at.save();
            List<UserInfo> registeredUsers= SQLiteUtils.rawQuery(UserInfo.class,
                    "SELECT * from UserInfo where username= ?",new String[]{curUserUsername});
            UserInfo curUser=registeredUsers.get(0);
            salTv.setText("Salary: Rs "+curUser.salary);
        }else{
            String accessRestricted="One or more writers are currently Accessing database";
            salTv.setText(accessRestricted);
        }

        return v;
    }

}
