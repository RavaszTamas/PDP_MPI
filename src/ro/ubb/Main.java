package ro.ubb;

import mpi.MPI;
import mpi.MPIException;

import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Main {

    final static int MAIN_PROCESS = 0;
    public static final int SIZE = 4;


    final static List<Integer> directions = new ArrayList<>();

    static {
        directions.add(-1);
        directions.add(1);
        directions.add(-SIZE);
        directions.add(SIZE);
    }


    public static void main(String[] args) {
        MPI.Init(args);
        int size = MPI.COMM_WORLD.Size();
        int me = MPI.COMM_WORLD.Rank();

        List<Integer> initialBoard = new ArrayList<>();

        initialBoard.add(4);
        initialBoard.add(8);
        initialBoard.add(6);
        initialBoard.add(10);

        initialBoard.add(2);
        initialBoard.add(9);
        initialBoard.add(5);
        initialBoard.add(14);

        initialBoard.add(0);
        initialBoard.add(1);
        initialBoard.add(3);
        initialBoard.add(11);

        initialBoard.add(13);
        initialBoard.add(15);
        initialBoard.add(7);
        initialBoard.add(12);

        List<Integer> goalBoard = IntStream.range(1, Main.SIZE * Main.SIZE).boxed().collect(Collectors.toList());

        if (me == MAIN_PROCESS) {
            puzzleSolver(initialBoard, size);
        } else {
            puzzleSolverWorker();
        }

        MPI.Finalize();
    }

    private static void puzzleSolver(List<Integer> initialBoard, int totalNumberOfProcesses) {
        int numberOfWorkers = totalNumberOfProcesses - 1;
        int expanded = 0;
        PriorityQueue<List<Integer>> priorityQueue = new PriorityQueue<>();
        while (true) {
            if (expanded % 10000 == 0) {
                if (handleTick()) {
                    return;
                }
            }
            expanded++;
            boolean notFinished = true;
            while (notFinished) {
                //TODO MPI RECEIVE
                if (notFinished == true) {
                    notFinished = false;
                }
                priorityQueue.add()
            }

        }

    }

    private static boolean handleTick() {
        return false;
    }

    private static void puzzleSolverWorker() {
        int length[] = new int[1];
        while (true) {
            MPI.COMM_WORLD.Recv(length, 0, 1, MPI.INT, MAIN_PROCESS, 2);
            if(length[0]==-1){
                return;
            }
            int data[] = new int[length[0]+1];
            MPI.COMM_WORLD.Recv(data, 0, length[0], MPI.INT, MAIN_PROCESS, 1);
            boolean found = false;
            int gap = 0;
            while (!found) {
                if (data[gap] == 0) {
                    found = true;
                } else {
                    gap++;
                }
            }
            for (int c = 0; c <= 4; c++) {
                length[0]+=1;
                int newPos = gap + directions.get(c);
                if (newPos>=0 && newPos<16) {
                    int swap = data[newPos];
                    data[newPos]=data[gap];
                    data[gap]=swap;
                    data[length[0]]=c;
                    if(checkSolution(data)){
                        int realLength=length[0];
                        length[0]=-1;
                        MPI.COMM_WORLD.Send(length,0,1, MPI.INT,MAIN_PROCESS,2);
                        length[0]=realLength;
                    }
                    MPI.COMM_WORLD.Send(length,0,1, MPI.INT,MAIN_PROCESS,2);
                    MPI.COMM_WORLD.Send(data,0,length[0],MPI.INT,MAIN_PROCESS,1);
                    swap = data[newPos];
                    data[newPos]=data[gap];
                    data[gap]=swap;
                }

            }
        }

    }

    private static boolean checkSolution(int[] data) {
        if(data[0]==0){
            return false;
        }
        for(int c=1;c<16;c++){
            if(data[c-1]>data[c]){
                return false;
            }
        }
        return true;
    }
}
