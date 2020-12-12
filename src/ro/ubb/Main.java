package ro.ubb;

import mpi.MPI;

import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Main {

  final static int MAIN_PROCESS = 0;

  public static final int SIZE = 4;

  public static void main(String[] args) {
    MPI.Init(args);
    int size = MPI.COMM_WORLD.Size();
    int me = MPI.COMM_WORLD.Rank();

    List<Integer> initialBoard = new ArrayList<>();

    initialBoard.add(4 );initialBoard.add(8 );initialBoard.add(6 );initialBoard.add(10 );

    initialBoard.add(2 );initialBoard.add(9 );initialBoard.add(5 );initialBoard.add(14 );

    initialBoard.add(0 );initialBoard.add(1 );initialBoard.add(3 );initialBoard.add(11 );

    initialBoard.add(13 );initialBoard.add(15 );initialBoard.add(7 );initialBoard.add(12 );

    List<Integer> goalBoard = IntStream.range(1, Main.SIZE * Main.SIZE).boxed().collect(Collectors.toList());

    if(me == MAIN_PROCESS) {
      puzzleSolver(initialBoard,goalBoard,size);
    }
    else{
      puzzleSolverWorker();
    }

    MPI.Finalize();
  }

  private static void puzzleSolver(List<Integer> initialBoard, List<Integer> goalBoard, int totalNumberOfProcesses) {
    int numberOfWorkers = totalNumberOfProcesses - 1;
    PriorityQueue<List<Integer>> priorityQueue = new PriorityQueue<>();

  }

  private static void puzzleSolverWorker() {

  }
}
