package com.zeus.office5;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

import java.io.Serializable;

/**
 * Created by Zeus on 4/23/2016.
 */
@Table(name="AdminInfo")
public class AdminInfo extends Model implements Serializable  {

    @Column(name = "username",unique = true, onUniqueConflict = Column.ConflictAction.REPLACE)
    public String username;
    @Column(name = "password")
    public String password;


    public AdminInfo(){
        super();
    }
    public AdminInfo( String username, String password){
        super();
        this.username=username;
        this.password=password;
    }
}
