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

        Board theGoalBoard = new Board(goalBoard, new ArrayList<>(), 0);

        while (true) {
            int itemsSent = 0;
            int idToSendTo = 1;
            while (itemsSent < numberOfWorkers && priorityQueueOfBoards.size() > 0) {
                Board current = priorityQueueOfBoards.poll();

                sendBuffer = current.flattenBoard();
                int[] sizeToSend = {sendBuffer.length};

                MPI.COMM_WORLD.Send(sizeToSend, 0, 1, MPI.INT, idToSendTo, 2);
                MPI.COMM_WORLD.Send(sendBuffer, 0, sizeToSend[0], MPI.INT, idToSendTo, 1);

                itemsSent += 1;
                idToSendTo += 1;
            }

            for (int i = 1; i <= itemsSent; i++) {
                int[] numberOfItemsToRead = {0};
                int[] sizeToRecieve = {0};
                MPI.COMM_WORLD.Recv(numberOfItemsToRead, 0, 1, MPI.INT, i, 3);

                for (int j = 0; j < numberOfItemsToRead[0]; j++) {

                    MPI.COMM_WORLD.Recv(sizeToRecieve, 0, 1, MPI.INT, i, 2);

                    int[] result = new int[sizeToRecieve[0]];

                    MPI.COMM_WORLD.Recv(result, 0, sizeToRecieve[0], MPI.INT, i, 1);

                    Board newBoard = Board.fromArray(result);

                    priorityQueueOfBoards.add(newBoard);
                }
            }

            if (priorityQueueOfBoards.peek() != null
                    && priorityQueueOfBoards.peek().equals(theGoalBoard)) {
                System.out.println("Result " + priorityQueueOfBoards.peek());
                int[] endMessage = {-1};
                for (int i = 1; i <= totalNumberOfProcesses; i++) {
                    MPI.COMM_WORLD.Send(endMessage, 0, 1, MPI.INT, itemsSent, 2);
                }
                return;
            }
        }
    }

    private static void puzzleSolverWorker() {
        int length[] = new int[1];
        while (true) {
            MPI.COMM_WORLD.Recv(length, 0, 1, MPI.INT, MAIN_PROCESS, 2);
            if (length[0] == -1) {
                return;
            }
            int data[] = new int[length[0] + 1];
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
            int[] count = {0};

            for (int c = 0; c < 4; c++) {
                int newPos = gap + directions.get(c);
                if (c < 2 && gap / SIZE == newPos / SIZE) {
                    if (newPos >= 0 && newPos < 16) {
                        count[0] += 1;
                    }
                }else if (c >= 2) {
                        if (newPos >= 0 && newPos < 16) {
                            count[0] += 1;
                        }
                    }
            }

            MPI.COMM_WORLD.Send(count, 0, 1, MPI.INT, MAIN_PROCESS, 3);
            length[0] += 1;
            for (int c = 0; c < 4; c++) {
                int newPos = gap + directions.get(c);
                if (c < 2 && gap / SIZE == newPos / SIZE) {
                if (newPos >= 0 && newPos < 16) {
                    int swap = data[newPos];
                    data[newPos] = data[gap];
                    data[gap] = swap;
                    data[length[0] - 1] = c;
                    MPI.COMM_WORLD.Send(length, 0, 1, MPI.INT, MAIN_PROCESS, 2);
                    MPI.COMM_WORLD.Send(data, 0, length[0], MPI.INT, MAIN_PROCESS, 1);
                    swap = data[newPos];
                    data[newPos] = data[gap];
                    data[gap] = swap;
//          System.out.println("===");
//          System.out.println(newPos);
//          System.out.println(Arrays.toString(data));
//          System.out.println(length[0]);
//          System.out.println("===");
//          if (checkSolution(data)) {
//            int realLength = length[0];
//            length[0] = -1;
//            MPI.COMM_WORLD.Send(length, 0, 1, MPI.INT, MAIN_PROCESS, 2);
//            length[0] = realLength;
                }
                }
                else if( c >= 2){
                    if (newPos >= 0 && newPos < 16) {
                        int swap = data[newPos];
                        data[newPos] = data[gap];
                        data[gap] = swap;
                        data[length[0] - 1] = c;
                        MPI.COMM_WORLD.Send(length, 0, 1, MPI.INT, MAIN_PROCESS, 2);
                        MPI.COMM_WORLD.Send(data, 0, length[0], MPI.INT, MAIN_PROCESS, 1);
                        swap = data[newPos];
                        data[newPos] = data[gap];
                        data[gap] = swap;
                }

                }
            }
        }
    }

    private static boolean checkSolution(int[] data) {
        if (data[0] == 0) {
            return false;
        }
        for (int c = 1; c < 16; c++) {
            if (data[c - 1] > data[c]) {
                return false;
            }
        }
        return true;
    }
}
