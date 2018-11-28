package com.example.android.app_hci;

//Creating story as an object
public class Story {

    //private variables
    int _id;
    String _title;
    String _description;
    int Image;

    // Empty constructor
    public Story(){

    }
    // constructor
    public Story(int id, String title, String description, int image){
        this._id = id;
        this._title = title;
        this._description = description;
        this.Image = image;
    }

//     getting ID
    public int getID(){
        return this._id;
    }

    // setting ID
    public void setID(int id){
        this._id = id;
    }

    // getting title
    public String getTitle(){
        return this._title;
    }

    // setting title
    public void setTitle(String title){
        this._title = title;
    }

    // getting title
    public String getDescription(){
        return this._description;
    }

    // setting title
    public void setDescription(String description){
        this._description = description;
    }

    public int getImage()
    {
        return Image;
    }

    public void setImage(int image)
    {
        Image = image;

    }

}
