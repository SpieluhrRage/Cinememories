package com.example.platonov.data.entity;

import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(
        tableName = "users",
        indices = {@Index(value = {"email"}, unique = true)}
)
public class UserEntity {
    @PrimaryKey(autoGenerate = true)
    public long id;

    public String email;
    public String password;

    public UserEntity() { }

    public UserEntity(String email, String password) {
        this.email = email;
        this.password = password;
    }
}
