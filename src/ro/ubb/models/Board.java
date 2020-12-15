package ro.ubb.models;

import ro.ubb.Main;

import java.util.ArrayList;
import java.util.List;

public class Board implements Comparable<Board> {
    private int H;
    List<Integer> boardTiles;
    List<Integer> path;
    int stepsTaken;
    private int myHash;

    public static Board fromArray(int[] theArray) {

        List<Integer> tiles = new ArrayList<>();
        List<Integer> path = new ArrayList<>();
        for(int i = 0; i < Main.SIZE*Main.SIZE;i++)
        {
            tiles.add(theArray[i]);
        }
        for(int i = Main.SIZE*Main.SIZE; i< theArray.length; i++)
        {
            path.add(theArray[i]);
        }
        return new Board(tiles,path,path.size());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Board board = (Board) o;
        return H == board.H &&
                stepsTaken == board.stepsTaken &&
                myHash == board.myHash &&
                boardTiles.equals(board.boardTiles) &&
                path.equals(board.path);
    }

    @Override
    public String toString() {
        return "Board{" +
                "H=" + H +
                ", boardTiles=" + boardTiles +
                ", path=" + path +
                ", stepsTaken=" + stepsTaken +
                ", myHash=" + myHash +
                '}';
    }

    public List<Integer> getBoardTiles() {
        return boardTiles;
    }

    public void setBoardTiles(List<Integer> boardTiles) {
        this.boardTiles = boardTiles;
    }

    public int getStepsTaken() {
        return stepsTaken;
    }

    public void setStepsTaken(int stepsTaken) {
        this.stepsTaken = stepsTaken;
    }

    public Board(List<Integer> boardTiles, List<Integer> path, int stepsTaken) {
        this.boardTiles = boardTiles;
        this.stepsTaken = stepsTaken;
        this.path = path;
        recalculateHash();
        computeH();
    }


    public int[] flattenBoard()
    {
        List<Integer> dummy = new ArrayList<>();
        dummy.addAll(boardTiles);
        dummy.addAll(path);
        int[] resultArray = new int[boardTiles.size()+path.size()];
        for(int i = 0; i < resultArray.length; i++)
        {
            resultArray[i] = dummy.get(i);
        }
        return resultArray;
    }

    public void computeH(){

        int total = 0;
        for(int i = 0; i < Main.SIZE; i++){
            for(int j = 0; j < Main.SIZE; j++){
                if(this.boardTiles.get(i*Main.SIZE+j) != 0) {
                    int element = boardTiles.get(i*Main.SIZE + j);
                    int row = (element-1)/ Main.SIZE;
                    int col = (element-1)% Main.SIZE;
                    int dist = Math.abs(row-i)+Math.abs(col-j);
                    total+= dist;
                }
                else {
                    total += Math.abs(i-(Main.SIZE-1)) + Math.abs(j-(Main.SIZE-1));
                }
            }
        }
        this.H = total;
    }

    public void recalculateHash() {

        int hashvalue = 0;
        long modulo = (1L<<56)-5;

        for(int i = 0; i < Main.SIZE*Main.SIZE; i++)
        {
                hashvalue = (int) ((hashvalue * Main.SIZE + this.boardTiles.get(i))%modulo);
        }

        hashvalue = hashvalue ^ (hashvalue >> 10);
        hashvalue = hashvalue ^ (hashvalue << 5);
        hashvalue = hashvalue ^ (hashvalue >> 4);
        hashvalue *= 432534532;
        hashvalue = hashvalue ^ (hashvalue << 20);
        hashvalue =hashvalue ^ (hashvalue >> 9);
        hashvalue = hashvalue ^ (hashvalue <<  5);


        myHash = Math.abs(hashvalue);
    }

    @Override
    public int hashCode() {
        return myHash;
    }

    public int getH() {
        return H;
    }

    public int getF(){
        return  H + stepsTaken;
    }

    @Override
    public int compareTo(Board o) {
        if(this.getF() < o.getF()){
            return -1;
        }
        else if(this.getF()==o.getF())
        {
            if(this.stepsTaken < o.stepsTaken)
                return -1;
            else if(this.stepsTaken > o.stepsTaken)
                return 1;
            return 0;
        }
        return 1;
    }
}
