package com.zeus.office5;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

import java.io.Serializable;

/**
 * Created by Zeus on 4/28/2016.
 */
@Table(name="allowedTags")

public class AllowedTags extends Model implements Serializable {
    @Column(name = "tag",unique = true, onUniqueConflict = Column.ConflictAction.REPLACE)
    public int tag;
    public AllowedTags() {
        super();
    }

    public AllowedTags(int tag) {
        super();
        this.tag = tag;
    }
}
