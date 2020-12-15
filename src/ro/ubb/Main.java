package ro.ubb;

import mpi.MPI;
import ro.ubb.models.Board;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.PriorityQueue;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Main {

  static final int MAIN_PROCESS = 0;
  public static final int SIZE = 4;

  static final List<Integer> directions = new ArrayList<>();

  static {
    directions.add(-1);
    directions.add(1);
    directions.add(-SIZE);
    directions.add(SIZE);
  }

  public static final List<Integer> goalBoard;

  static {
    goalBoard = IntStream.range(1, Main.SIZE * Main.SIZE).boxed().collect(Collectors.toList());
    goalBoard.add(0);
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

//    System.out.println("Size " + size + " me " + me);

    if (me == MAIN_PROCESS) {
      puzzleSolver(initialBoard, size);
    } else {
      puzzleSolverWorker();
    }

    MPI.Finalize();
  }

  private static void puzzleSolver(List<Integer> initialBoard, int totalNumberOfProcesses) {
    int numberOfWorkers = totalNumberOfProcesses - 1;
    List<Integer> initialPath = new ArrayList<>();
    PriorityQueue<Board> priorityQueueOfBoards = new PriorityQueue<>();
    priorityQueueOfBoards.add(new Board(initialBoard, initialPath, 0));

//    System.out.println(priorityQueueOfBoards.peek());
    int[] sendBuffer;

    List<Integer> currentPath = new ArrayList<>();

    Board theGoalBoard = new Board(goalBoard,new ArrayList<>(),0);

    while (true) {
      int itemsSent = 0;
      while (itemsSent < numberOfWorkers && priorityQueueOfBoards.size() > 0) {
        Board current = priorityQueueOfBoards.poll();
        itemsSent += 1;

        sendBuffer = current.flattenBoard();
        int[] sizeToSend = {sendBuffer.length};

//        System.out.println("Sent to " + itemsSent + ": " + Arrays.toString(sendBuffer));

        MPI.COMM_WORLD.Send(sizeToSend, 0, 1, MPI.INT, itemsSent, 2);
        MPI.COMM_WORLD.Send(sendBuffer, 0, sizeToSend[0], MPI.INT, itemsSent, 1);
      }

      for (int i = 1; i <= itemsSent; i++) {
        int[] sizeToRecieve = {0};
        int[] numberOfItemsToRead = {0};

        MPI.COMM_WORLD.Recv(numberOfItemsToRead, 0, 1, MPI.INT, itemsSent, 3);
        for (int j = 0; j < numberOfItemsToRead[0]; j++) {
          MPI.COMM_WORLD.Recv(sizeToRecieve, 0, 1, MPI.INT, itemsSent, 2);
          int[] result = new int[sizeToRecieve[0]];
          MPI.COMM_WORLD.Recv(result, 0, sizeToRecieve[0], MPI.INT, itemsSent, 1);
          Board newBoard = Board.fromArray(result);

          priorityQueueOfBoards.add(newBoard);
          if (newBoard.equals(theGoalBoard)) {
            System.out.println("Hurray for soltuion");
          }
        }
      }
    }
  }

  private static void puzzleSolverWorker() {

    while (true) {
      int[] sizeToRecieve = {0};
      int[] sizeToSend = {0};

      MPI.COMM_WORLD.Recv(sizeToRecieve, 0, 1, MPI.INT, 0, 2);

      int[] recievingBuffer = new int[sizeToRecieve[0]];

      MPI.COMM_WORLD.Recv(recievingBuffer, 0, sizeToRecieve[0], MPI.INT, 0, 1);

//      System.out.println("Sending result to master: " + Arrays.toString(recievingBuffer));

      sizeToSend[0] = recievingBuffer.length;
      int[] numberOfItemsToSend = {1};
      MPI.COMM_WORLD.Send(numberOfItemsToSend, 0, 1, MPI.INT, 0, 3);
      MPI.COMM_WORLD.Send(sizeToSend, 0, 1, MPI.INT, 0, 2);
      MPI.COMM_WORLD.Send(recievingBuffer, 0, sizeToSend[0], MPI.INT, 0, 1);
    }
  }
}
