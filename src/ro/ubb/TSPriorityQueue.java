package ro.ubb;


import java.util.HashMap;
import java.util.List;
import java.util.concurrent.PriorityBlockingQueue;

public class TSPriorityQueue {

    PriorityBlockingQueue<List<Integer>> priorityBlockingQueues;
    HashMap<List<Integer>,List<Integer>> hashTables; //board and path

    public TSPriorityQueue(){

        priorityBlockingQueues = new PriorityBlockingQueue<>();
        hashTables = new HashMap<>();


    }
    public int computeHash(List<Integer> board) {

        int hashvalue = 0;
        long modulo = (1L<<56)-5;

        for(int i = 0; i < Main.SIZE*Main.SIZE; i++)
        {
            hashvalue = (int) ((hashvalue * Main.SIZE + board.get(i)%modulo));
        }

        hashvalue = hashvalue ^ (hashvalue >> 10);
        hashvalue = hashvalue ^ (hashvalue << 5);
        hashvalue = hashvalue ^ (hashvalue >> 4);
        hashvalue *= 432534532;
        hashvalue = hashvalue ^ (hashvalue << 20);
        hashvalue =hashvalue ^ (hashvalue >> 9);
        hashvalue = hashvalue ^ (hashvalue <<  5);


        return Math.abs(hashvalue);
    }
    public void addBoard(List<Integer> path,List<Integer> board){
        if(hashTables.containsKey(board)){
            List<Integer> foundPath = hashTables.get(board);

            if(foundPath.size() > path.size()){
                hashTables.put(board,path);
                priorityBlockingQueues.add(board);//maybe it will repeat itself ?
            }

        }
        else {
            hashTables.put(board,path);
            priorityBlockingQueues.add(board);
        }


    }

    public List<Integer> popBoard(int index){
        try {
            return priorityBlockingQueues.take();
        } catch (InterruptedException e) {
            return null;
        }

    }


}
