package controller;

import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelReader;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.GridPane;
import javafx.util.Pair;
import model.Direction;
import model.Piece;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static model.Direction.*;

public class Controller {

    //List of the puzzle pieces
    private List<Piece> pieces = new ArrayList<>();
    private List<Image> pics = new ArrayList<>();           //writable images
    private Image missingTextureImage = new Image("File:resources/missingTextureInviz.png");
    private Piece missingPiece;
    private ImageView imageToCompletePicture;
    private GridPane grid;
    private int size;

    public boolean solved() {
        return correctNumberOfPieces == size * size - 1;
    }

    private int correctNumberOfPieces;

    public Controller(){

    }

    /**
     * @param picture given Image
     * @param n       number of pieces for the image to be cut
     */
    public void cutImage(Image picture, int n){
        picture = scale(picture, 500, 500, true);
        PixelReader reader = picture.getPixelReader();
        size = n;

        int width = (int) picture.getWidth();
        int height = (int) picture.getHeight();
        int x, y = 0;
        for(int i = 1; i <= n; i++){
            x = 0;
            for(int j = 1; j<=n; j++){
                //cuts an image from the picture as:  from position (x, y) with width and height given
                WritableImage newImage = new WritableImage(reader, x, y, width/n, height/n);
                pics.add(newImage);
                x += width/n;
            }
            y += height/n;
        }

    }

    /**
     * @param source            the source image to be scaled
     * @param targetWidth       the target width for the image to be scaled
     * @param targetHeight      the target height for the image to be scaled
     * @param preserveRatio     preserve the height/width ratio of the initial image
     * @return                  the scaled image
     */
    private Image scale(Image source, int targetWidth, int targetHeight, boolean preserveRatio) {
        ImageView imageView = new ImageView(source);
        imageView.setPreserveRatio(preserveRatio);
        imageView.setFitWidth(targetWidth);
        imageView.setFitHeight(targetHeight);
        return imageView.snapshot(null, null);
    }

    public void initPieces(){
        int x = 0, y = 0, k = 0;       //indexes in the grid
        int n = (int) Math.sqrt(pics.size());     //nr of images per width or height

        for(Image img : pics){
            k++;
            Pair<Integer, Integer> pear = new Pair<>(x, y);
            if(k == pics.size()){
                //last piece is empty pic
                Image lastPieceImg = scale(missingTextureImage, (int)img.getWidth(), (int)img.getHeight(), false);
                ImageView lastPieceImgView = new ImageView(lastPieceImg);
                Piece p = new Piece(lastPieceImgView, pear);
                missingPiece = p;       //set missing piece to the piece with missing texture image
                pieces.add(p);
                imageToCompletePicture = new ImageView(img);        //the image that is replaced by the missingTexture
            }
            else{
                Piece p = new Piece(new ImageView(img), pear);
                pieces.add(p);
            }

            y++;
            if(y == n){
                //when it gets to the end of the row start from the beginning of the next row
                y = 0;
                x++;
            }
        }

        //all pieces are currently in the right position => all are on the correct index
        correctNumberOfPieces = size*size - 1;
    }

    /**
     * @return returns a grid with the images in order
     */
    public GridPane initGrid(){
        grid = new GridPane();

        for(Piece pic : pieces){
            int x = pic.getIndex().getKey();
            int y = pic.getIndex().getValue();
            //setConstraints(Node, column, row)
            GridPane.setConstraints(pic.getImg(), y, x);
            grid.getChildren().add(pic.getImg());
        }
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(4);
        grid.setVgap(4);
        shufflePieces();
        return grid;
    }

    /**
     * @param gridPane the grid pane from which to take the node
     * @param col      the column of the node to be extracted
     * @param row      the row of the node to be extracted
     * @return         returns the node if found, null otherwise
     */
    private Node getNodeFromGridPane(GridPane gridPane, int col, int row) {
        for (Node node : gridPane.getChildren()) {
            if (GridPane.getColumnIndex(node) == col && GridPane.getRowIndex(node) == row) {
                return node;
            }
        }
        return null;
    }

    //returns the piece with the given pair of index values
    private Piece getPieceWithIndex(int key, int value){
        for(Piece found: pieces){
            if(found.getIndex().getKey() == key && found.getIndex().getValue() == value){
                return found;
            }
        }
        return null;
    }

    /**
     * @param direction the direction in which to move the empty
     *                  piece by adding its values to the index values of the empty piece
     */
    public void moveEmptyPiece(Pair<Integer, Integer> direction){
        // TODO: correct index counting pieces
        int missingPieceRow = missingPiece.getIndex().getKey();
        int missingPieceCol = missingPiece.getIndex().getValue();

        if(missingPieceRow + direction.getKey() == size || missingPieceCol + direction.getValue() == size || missingPieceRow + direction.getKey() < 0 || missingPieceCol + direction.getValue() < 0){
            //moving out of bounds
            return;
        }
        else{
            int swapPieceRow = missingPieceRow + direction.getKey();
            int swapPieceCol = missingPieceCol + direction.getValue();

            //the piece with which we swap places
            Piece swapPiece = getPieceWithIndex(swapPieceRow, swapPieceCol);

            //check if we're moving the piece from its rightful place
            if(swapPieceRow == swapPiece.getCorrectIndex().getKey() && swapPieceCol == swapPiece.getCorrectIndex().getValue()){
                correctNumberOfPieces --;
            }

            //check if moving on correct spot
            if(missingPieceRow == swapPiece.getCorrectIndex().getKey() && missingPieceCol == swapPiece.getCorrectIndex().getValue()){
                correctNumberOfPieces ++;
            }

            //the node containing the image
            Node swapPieceNode = getNodeFromGridPane(grid, swapPieceCol, swapPieceRow);
            //node of the empty piece
            Node emptyPieceNode = getNodeFromGridPane(grid, missingPieceCol, missingPieceRow);

            //swap the Constraints and add to grid
            GridPane.setConstraints(emptyPieceNode, swapPieceCol, swapPieceRow);
            GridPane.setConstraints(swapPieceNode, missingPieceCol, missingPieceRow);

            //modify Piece indexes
            missingPiece.setIndex(new Pair<Integer, Integer>(missingPieceRow + direction.getKey(), missingPieceCol + direction.getValue()));
            swapPiece.setIndex(new Pair<>(swapPieceRow - direction.getKey(), swapPieceCol - direction.getValue()));

        }
    }

    /**
     * Shuffles the pieces in the grid by moving the empty piece a number of time
     * in a random direction. This way the puzzle is 100% solvable (theoretically)
     */
    public void shufflePieces(){
        int times = 3000; //how many times to move the empty piece

        int pick;
        //for(int i = 0; i < times; i++){
        while(correctNumberOfPieces != 0){
            pick = new Random().nextInt(Direction.values().length);
            if(Direction.values()[pick] == UP){
                moveEmptyPiece(new Pair<>(1, 0));
            }
            if(Direction.values()[pick] == DOWN){
                moveEmptyPiece(new Pair<>(-1, 0));
            }
            if(Direction.values()[pick] == LEFT){
                moveEmptyPiece(new Pair<>(0, 1));
            }
            if(Direction.values()[pick] == RIGHT){
                moveEmptyPiece(new Pair<>(0, -1));
            }
        }
    }
}
