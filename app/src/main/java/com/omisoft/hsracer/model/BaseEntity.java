package com.omisoft.hsracer.model;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.PrimaryKey;
import lombok.Getter;
import lombok.Setter;

/**
 * Base Entity for model
 * Created by dido on 26.04.17.
 */
@Getter
@Setter
public abstract class BaseEntity {

  @ColumnInfo(name = "_id")
  @PrimaryKey(autoGenerate = true)
  public Long id;
}
