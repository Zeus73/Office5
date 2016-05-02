package com.zeus.office5;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.activeandroid.util.SQLiteUtils;
import com.zeus.office5.R;

import java.util.List;

/**
 *
 */
public class Tab1Fragment extends Fragment {

    public interface Fragment1ListenerInterface{
        public void onFragment1Click(String curUserUsername);
    }
    public interface Fragment1AdminListenerInterface{
        public void onFragment1AdminClick(String curAdminUsername);
    }

    Button customerLogin, adminLogin,newuserRegister;
    SharedPreferences sp;

    /* (non-Javadoc)
     * @see android.support.v4.app.Fragment#onCreateView(android.view.LayoutInflater, android.view.ViewGroup, android.os.Bundle)
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (container == null) {
            return null;
        }
        sp=getActivity().getSharedPreferences("login", Context.MODE_PRIVATE);
        View v= (LinearLayout)inflater.inflate(R.layout.tab_frag1_layout, container, false);
        final EditText usernameEditText=(EditText)v.findViewById(R.id.usernameEditText);
        final EditText passwordEditText=(EditText)v.findViewById(R.id.passwordEditText);
        customerLogin=(Button) v.findViewById(R.id.employeeLogin);
        adminLogin =(Button) v.findViewById(R.id.adminLogin);
//        newuserRegister=(Button) v.findViewById(R.id.newEmployeeRegisterButton);
//        newuserRegister.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//
//            }
//        });

        adminLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                String usernameEntered=usernameEditText.getText().toString();
                String passwordEntered=passwordEditText.getText().toString();
                List<AdminInfo> registeredAdmins= SQLiteUtils.rawQuery(AdminInfo.class,
                        "SELECT * from AdminInfo where username= ?",new String[]{usernameEntered});


                if(registeredAdmins==null||registeredAdmins.size()==0){
                    Toast.makeText(getActivity(), "Incorrect Admin Username", Toast.LENGTH_SHORT).show();
                }else if(registeredAdmins.get(0).password.equals(passwordEntered)){
                    AdminInfo curAdmin=registeredAdmins.get(0);
                    Toast.makeText(getActivity(), "Welcome Admin: "+curAdmin.username, Toast.LENGTH_SHORT).show();

                    Fragment1AdminListenerInterface listener=(Fragment1AdminListenerInterface) getActivity();
                    usernameEditText.setText("");
                    passwordEditText.setText("");
                    listener.onFragment1AdminClick(curAdmin.username);

                }else{
                    Toast.makeText(getActivity(), "incorrect password, try again", Toast.LENGTH_SHORT).show();
                }



            }
        });

        customerLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String usernameEntered=usernameEditText.getText().toString();
                String passwordEntered=passwordEditText.getText().toString();
                List<UserInfo> registeredUsers= SQLiteUtils.rawQuery(UserInfo.class,
                        "SELECT * from UserInfo where username= ?",new String[]{usernameEntered});
                if(registeredUsers==null||registeredUsers.size()==0){
                    Toast.makeText(getActivity(), "Incorrect Employee Username", Toast.LENGTH_SHORT).show();
                }else if(registeredUsers.get(0).password.equals(passwordEntered)){
                    UserInfo curUser=registeredUsers.get(0);
                    Toast.makeText(getActivity(), "Welcome Employee: "+curUser.username, Toast.LENGTH_SHORT).show();

                    Fragment1ListenerInterface listener=(Fragment1ListenerInterface) getActivity();
                    usernameEditText.setText("");
                    passwordEditText.setText("");
                    Log.i("checkUsername",curUser.username);

                    listener.onFragment1Click(curUser.username);

                }else{
                    Toast.makeText(getActivity(), "incorrect password, try again", Toast.LENGTH_SHORT).show();
                }

            }
        });


        return v;
    }

}
