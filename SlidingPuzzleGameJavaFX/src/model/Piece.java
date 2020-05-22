package model;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.util.Pair;

public class Piece {
    private ImageView img;   //image corresponding to the puzzle piece
    private Pair<Integer, Integer> index, correctIndex;           //position of the piece in the grid at any given time and the correct position

    public Piece(ImageView img, Pair<Integer, Integer> index){
        this.img = img;
        this.index = index;
        correctIndex = index;
    }

    //--  Getters  --//

    public ImageView getImg() {
        return img;
    }

    public Pair<Integer, Integer> getIndex() {
        return index;
    }

    public Pair<Integer, Integer> getCorrectIndex() {
        return correctIndex;
    }

    //-- Setters  --//

    public void setIndex(Pair<Integer, Integer> index) {
        this.index = index;
    }

    public void setImg(ImageView img) {
        this.img = img;
    }
}
