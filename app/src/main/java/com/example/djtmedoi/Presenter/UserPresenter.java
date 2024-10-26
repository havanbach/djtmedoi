package com.example.djtmedoi.Presenter;

import com.example.djtmedoi.Models.User;
import com.example.djtmedoi.my_interface.IUser;
import com.example.djtmedoi.my_interface.UserView;

public class UserPresenter implements IUser {
    private User user;
    private UserView callback;

    public UserPresenter(UserView callback){
        user = new User(this);
        this.callback = callback;
    }

    public void HandleGetUsers(String iduser, String email){
        user.HandleGetUsers(iduser, email);
    }
    @Override
    public void OnSucess() {

    }

    @Override
    public void OnFail() {

    }

    @Override
    public void getDataUser(String id, String email, String name, String address, String phone, String date, String gender, String avatar) {
        callback.getDataUser(id, email, name, address, phone, date, gender, avatar);
    }
}
