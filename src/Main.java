import java.util.*;

public class Main {
    private final static int CountOfXCellsOfMaze = 32;
    private final static int CountOfYCellsOfMaze = 32;
    private final static int CountOfCells = CountOfXCellsOfMaze * CountOfYCellsOfMaze;

    public static void main(String[] args) {
            short maze[] = new short[CountOfCells];
            maze = genMaze(maze);

            int[] startFinish = genStartFinish(maze);
            System.out.println("\nStart: " + startFinish[0] + "\nFinish: " + startFinish[1] + "\n");
            printMazeArray(maze);
    }

    private static int[] genStartFinish(short[] maze) {
        ArrayList<Integer> candidates = new ArrayList<>();

        for (int i=0; i<maze.length; i++) {
            char[] binary = Integer.toBinaryString(maze[i]).toCharArray();
            int count1 = 0;

            for (int j : binary) {
                if (j == '1') {
                    count1++;
                }
            }
            if (count1 == 1) {
                candidates.add(i);
            }
        }
        return checkDistance(candidates);
    }

    private static int[] checkDistance(ArrayList<Integer> candidates) {
        int maxDistance = 0;
        int startFinish[] = new int[2];
        Random random = new Random();

        for (int i : candidates) {
            for (int j : candidates) {
                System.out.println(i+"|"+j);
                if (i != j) {
                    int tmp = getDistance(indexToCoord(i)[0], indexToCoord(i)[1], indexToCoord(j)[0], indexToCoord(j)[1]);

                    if (tmp > maxDistance) {
                        int index = random.nextInt(2);

                        maxDistance = tmp;
                        startFinish[index] = i;
                        startFinish[1 - index] = j;
                    }
                }
            }
        }
        return startFinish;
    }

    private static int getDistance(int x1, int y1, int x2, int y2) {
        int [] currentCoordinates = {x1, y1};
        int counter = 0;

        while (currentCoordinates[0] != x2 | currentCoordinates[1] != y2) {
            double min = CountOfCells;
            Direction dir = null;
            HashSet<Direction> directions = validateDirection(coordToIndex(currentCoordinates));

            for (Direction d : directions) {
                int [] checkCoordinate = useDirection(indexToCoord(coordToIndex(currentCoordinates)), d);
                double distance = getDistanceDouble(checkCoordinate[0], checkCoordinate[1], x2, y2);

                if (distance < min) {
                    min = distance;
                    dir = d;
                }
            }
            if (dir == null) {
                System.err.println("Error: getDistance (code:10");
                System.exit(10);
            }
            currentCoordinates = useDirection(currentCoordinates, dir);
            counter++;
        }
        return counter;
    }

    private static double getDistanceDouble(int x1, int y1, int x2, int y2) {
        return Math.sqrt(Math.pow(x2 - x1, 2) + Math.pow(y2 - y1, 2));
    }

    private static void printMazeArray(short[] maze) {
        for (int i=0;i<maze.length; i++) {
            String tmp00 = Integer.toString(i);
            String tmp0 = Short.toString(maze[i]);
            String tmp = Integer.toBinaryString(maze[i]);

            while (tmp00.length() < Integer.toString(maze.length - 1).length()) {
                tmp00 = "0"+tmp00;
            }
            System.out.print(tmp00+": ");
            while (tmp0.length() < 2) {
                tmp0 = "0"+tmp0;
            }
            System.out.print(tmp0+" : ");
            while (tmp.length() < 6) {
                tmp = "0"+tmp;
            }
            System.out.println(tmp);
        }
    }

    private static short[] genMaze(short[] maze) {
        int start = getRandomNullCell(maze);
        int finish = getRandomNullCell(maze);

        while (start == finish) {
            finish = getRandomNullCell(maze);
        }
        maze = genMazeArray(maze, findWay(start, finish));
        while (hasNull(maze)) {
            maze = genMazeArray(maze, findWay(getRandomNullCell(maze), getNotNullCells(maze)));
        }
        return maze;
    }

    private static int getRandomNullCell(short[] maze) {
        HashSet<Integer> randList = new HashSet<>();

        for (int i=0; i<maze.length; i++) {
            if (maze[i] == 0) {
                randList.add(i);
            }
        }
        return randomInt(randList);
    }

    private static HashSet<Integer> getNotNullCells(short[] maze) {
        HashSet<Integer> list = new HashSet<>();

        for (int i=0; i<maze.length; i++) {
            if (maze[i] != 0) {
                list.add(i);
            }
        }
        System.out.println(CountOfCells - list.size());
        return list;
    }

    private static boolean hasNull(short[] maze) {
        boolean hasValue = false;

        for (short tmp : maze) {
            if (tmp == 0) {
                hasValue = true;
                break;
            }
        }
        return hasValue;
    }

    private static short[] genMazeArray(short[] maze, Stack<Integer> way) {
        while (way.size() > 1) {
            int currentIndex = way.pop();
            int nextIndex = way.peek();
            short tmp[] = valueMazeCell(indexToCoord(currentIndex), indexToCoord(nextIndex));

            maze[currentIndex] += degree(tmp[0]);
            maze[nextIndex] += degree(tmp[1]);
        }
        return maze;
    }

    private static short degree(short value) {
        return (short) Math.pow(2, value);
    }

    private static short[] valueMazeCell(int[] currentCoord, int[] nextCoord) {
        int x = currentCoord[0] - nextCoord[0];
        int y = currentCoord[1] - nextCoord[1];
        short mode[] = {0, 0};

        if (x == 1 & y == 0) {
            //D->U
            mode[0] = 0;
            mode[1] = 3;
        } else if (x == -1 & y == 0) {
            //U->D
            mode[0] = 3;
            mode[1] = 0;
        } else if (x == 1 & y == -1) {
            //RU->LD
            mode[0] = 1;
            mode[1] = 4;
        } else if (x == -1 & y == 1) {
            //LD->RU
            mode[0] = 4;
            mode[1] = 1;
        } else if (x == 0 & y == -1) {
            //RD->LU
            mode[0] = 2;
            mode[1] = 5;
        } else if (x == 0 & y == 1) {
            //LU->RD
            mode[0] = 5;
            mode[1] = 2;
        } else {
            System.err.println("Error in valueMazeCell (code:2)");
            System.exit(2);
        }
        return mode;
    }

    private static Stack<Integer> findWay(int startPosition, int finishPosition) {
        HashSet<Integer> finishPositions = new HashSet<>();

        finishPositions.add(finishPosition);
        return findWay(startPosition, finishPositions);
    }

    private static Stack<Integer> findWay(int startPosition, HashSet<Integer> finishPosition) {
        int debug = 0;
        Stack<Integer> way = new Stack<>();
        int position = startPosition;
        way.push(position);

        while (!finishPosition.contains(position)) {
            position = coordToIndex(useDirection(indexToCoord(position), randomDirection(validateDirection(position))));
            while (way.contains(position)) {
                way.pop();
            }
            way.push(position);
            debug++;
            if (debug == CountOfCells*10000) {
                System.err.println("Error: findWay (code:4)");
                System.exit(4);
            }
        }
        //System.out.println(way);
        return way;
    }

    private static Direction randomDirection(HashSet<Direction> directions) {
        ArrayList<Direction> mas = new ArrayList<>(directions);
        Random random = new Random();
        int rand = random.nextInt(mas.size());

        return mas.get(rand);
    }

    private static int randomInt(HashSet<Integer> integers) {
        ArrayList<Integer> mas = new ArrayList<>(integers);
        Random random = new Random();
        int rand = random.nextInt(mas.size());

        return mas.get(rand);
    }

    private static int[] useDirection(int coord[], Direction direction) {
        switch (direction) {
            case U:
                coord[0]--;
                break;
            case D:
                coord[0]++;
                break;
            case RU:
                coord[0]--;
                coord[1]++;
                break;
            case RD:
                coord[1]++;
                break;
            case LU:
                coord[1]--;
                break;
            case LD:
                coord[0]++;
                coord[1]--;
                break;
            default:
                System.err.println("Error in direction (code: 1)");
                System.exit(1);
        }
        return coord;
    }

    private static HashSet<Direction> validateDirection(int startPosition) {
        int coordinate[] = indexToCoord(startPosition);
        HashSet<Direction> directions = new HashSet<>();

        if (coordinate[0] > 0) {
            directions.add(Direction.U);
            if (coordinate[1] < CountOfYCellsOfMaze -1) {
                directions.add(Direction.RU);
            }
        }
        if (coordinate[0] < CountOfXCellsOfMaze - 1) {
            directions.add(Direction.D);
        }
        if (coordinate[1] > 0) {
            directions.add(Direction.LU);
            if (coordinate[0] < CountOfXCellsOfMaze - 1) {
                directions.add(Direction.LD);
            }
        }
        if (coordinate[1] < CountOfYCellsOfMaze - 1) {
            directions.add(Direction.RD);
        }
        if (directions.isEmpty()) {
            System.err.println("Error: validateDirection (code:11)");
            System.exit(11);
        }
        return directions;
    }

        private static int[] indexToCoord(int index) {
        int coord[] = new int[2];

        coord[0] = index/CountOfYCellsOfMaze;
        coord[1] = index%CountOfYCellsOfMaze;
        return coord;
    }

    private static int coordToIndex(int[] coord) {
        return coord[0]*CountOfYCellsOfMaze+coord[1];
    }
}
