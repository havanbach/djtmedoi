package com.example.djtmedoi.Presenter;

import com.example.djtmedoi.Models.Favorite;
import com.example.djtmedoi.my_interface.FavoriteView;
import com.example.djtmedoi.my_interface.IFavorite;

public class FavoritePresenter implements IFavorite {
    private Favorite favorite;
    private FavoriteView callback;

    public FavoritePresenter(FavoriteView callback){
        this.callback = callback;
        favorite = new Favorite(this);
    }

    public void HandleGetFavorite(String iduser){
        favorite.HandleGetFavorite(iduser);
    }


    @Override
    public void getDataFavorite(String idlove, String idproduct, String iduser) {
        callback.getDataFavorite(idlove, idproduct, iduser);
    }
}
