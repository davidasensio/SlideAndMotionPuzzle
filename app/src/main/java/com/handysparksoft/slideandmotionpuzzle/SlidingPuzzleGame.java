package com.handysparksoft.slideandmotionpuzzle;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by davasens on 6/12/2015.
 */
public class SlidingPuzzleGame {
    private final static String LOG_TAG = SlidingPuzzleGame.class.getSimpleName();
    private final static int HOLE = 0;

    private int cols = 3;
    private int rows = 3;

    private int puzzle[][];
    private int puzzleSolution[][];

    private boolean finished = false;
    private List<SlidingPuzzleListener> listeners = new ArrayList<SlidingPuzzleListener>();

    //Default constructor 3 X 3
    public SlidingPuzzleGame() {
        this(3, 3);
    }

    //Custom constructor
    public SlidingPuzzleGame(int cols, int rows) {
        this.cols = cols;
        this.rows = rows;

        puzzle = new int[cols][rows];
        puzzleSolution = new int[cols][rows];

        int counter = 1;

        for (int j = 0; j < rows; j++) {
            for (int i = 0; i < cols; i++) {
                puzzle[i][j] = counter;
                puzzleSolution[i][j] = counter;
                counter++;
            }
        }

        //Hole
        puzzle[cols - 1][rows - 1] = 0;
        puzzleSolution[cols - 1][rows - 1] = 0;
    }



    public void start() {
        shuffle2D(puzzle);
        finished = false;
    }

    public int getCell(int i, int j) {
        int result = -1;
        if (i >= 0 && j >= 0) {
            if (i < cols && j < rows) {
                result = puzzle[i][j];
            }
        }
        return result;
    }

    private boolean canMove(int i, int j) {
        boolean result = false;

        if (getCell(i + 1, j) == HOLE || getCell(i, j + 1) == HOLE || getCell(i - 1, j) == HOLE || getCell(i, j - 1) == HOLE) {
            result = true;
        }
        return result;
    }

    private boolean canMove(int idStick) {
        boolean result = false;
        int[] coordsById = getCoordsById(idStick);

        if (coordsById != null) {
            result = canMove(coordsById[0], coordsById[1]);
        }
        return result;
    }

    private void move(int i, int j) {
        int cellValue = getCell(i, j);

        if (getCell(i + 1, j) == HOLE) {
            puzzle[i + 1][j] = cellValue;
        } else if (getCell(i, j + 1) == HOLE) {
            puzzle[i][j + 1] = cellValue;
        } else if (getCell(i - 1, j) == HOLE) {
            puzzle[i - 1][j] = cellValue;
        } else if (getCell(i, j - 1) == HOLE) {
            puzzle[i][j - 1] = cellValue;
        }

        puzzle[i][j] = HOLE;
    }

    ;

    public void play(int xMove, int yMove) {
        if (!finished) {
            int i = xMove;
            int j = yMove;

            int cellValue = this.getCell(i, j);
            if (cellValue != -1 && cellValue != 0) {
                Log.d(LOG_TAG, "is a stick");

                boolean canMove = canMove(i, j);
                if (canMove) {
                    Log.d(LOG_TAG, "can be moved");
                    move(i, j);

                    //Trigger event Play
					firePlayEvent();
                } else {
                    Log.d(LOG_TAG, "can NOT be moved");
                }
            } else {
                Log.d(LOG_TAG, "is NOT a Stick");
            }

            if (isSolved()) {
                finished = true;
            }
        }
    }

    public void play(int idStick) {
        int[] coordsById = getCoordsById(idStick);
        if (coordsById != null) {
            int i = coordsById[0];
            int j = coordsById[1];
            play(i, j);
        }
    }

    public int[] getCoordsById(int idStick) {
        int[] result = null;
        boolean finded = false;

        for (int i = 0; i < this.cols; i++) {
            for (int j = 0; j < this.rows; j++) {
                if (!finded && puzzle[i][j] == idStick) {
                    result = new int[2];
                    result[0] = i;
                    result[1] = j;
                    finded = true;
                    break;
                }
            }
        }
        return result;
    }

    public int[] getHoleCoords() {
        int[] result = null;
        boolean finded = false;

        for (int i = 0; i < this.cols; i++) {
            for (int j = 0; j < this.rows; j++) {
                if (!finded && puzzle[i][j] == HOLE) {
                    result = new int[2];
                    result[0] = i;
                    result[1] = j;
                    finded = true;
                    break;
                }
            }
        }
        return result;
    }


    public boolean isSolved() {
        boolean result = false; //FIXME poner a true

        for (int i = 0; i < this.cols; i++) {
            for (int j = 0; j < this.rows; j++) {
                if (puzzleSolution[i][j] != puzzle[i][j]) {
                    return false;
                }
            }
        }

        return result;
    }


    public void solve() {
        boolean solved = false;
        int counter = 0;

        while (!solved && counter < 50) {
            //this.doRandomPlay();
            solved = this.isSolved();
            counter++;
        }

    }

    public boolean canMoveInXAxis(int idStick) {
        boolean result = false;
        int[] coordsById = getCoordsById(idStick);
        if (coordsById != null) {
            int i = coordsById[0];
            int j = coordsById[1];
            if (getCell(i + 1, j) == HOLE || getCell(i - 1, j) == HOLE) {
                result = true;
            }
        }

        return result;

    }

    public boolean canMoveInYAxis(int idStick) {
        boolean result = false;
        int[] coordsById = getCoordsById(idStick);
        if (coordsById != null) {
            int i = coordsById[0];
            int j = coordsById[1];
            if (getCell(i, j + 1) == HOLE || getCell(i, j - 1) == HOLE) {
                result = true;
            }
        }

        return result;

    }

    private int[] shuffle(int[] array) {
        int currentIndex = array.length;
        int randomIndex;
        int tmpValue;

        while (currentIndex != 0) {
            randomIndex = Double.valueOf(Math.floor(Math.random() * currentIndex)).intValue();
            currentIndex--;

            tmpValue = array[currentIndex];
            array[currentIndex] = array[randomIndex];
            array[randomIndex] = tmpValue;
        }

        return array;
    }

    private int[][] shuffle2D(int[][] array) {
        int currentIndex = array.length;
        int randomIndex;
        int subRandomIndex;
        int tmpValue;

        while (currentIndex != 0) {
            int subCurrentIndex = array[currentIndex - 1].length;
            while (subCurrentIndex != 0) {
                randomIndex = Double.valueOf(Math.floor(Math.random() * currentIndex)).intValue();
                subRandomIndex = Double.valueOf(Math.floor(Math.random() * subCurrentIndex)).intValue();

                tmpValue = array[currentIndex - 1][subCurrentIndex - 1];
                array[currentIndex - 1][subCurrentIndex - 1] = array[randomIndex][subRandomIndex];
                array[randomIndex][subRandomIndex] = tmpValue;
                subCurrentIndex--;
            }
            currentIndex--;
        }

        return array;
    }

    public void registerListener(SlidingPuzzleListener listener) {
        listeners.add(listener);
    }

    public void unregisterListener(SlidingPuzzleListener listener) {
        listeners.remove(listener);
    }

    public void firePlayEvent() {
        for (SlidingPuzzleListener listener: listeners) {
            listener.onPlayListener();
        }
        Log.d(LOG_TAG, "Event Play triggered");

    }

    public void fireRepaintEvent() {
        for (SlidingPuzzleListener listener: listeners) {
            listener.onRepaintListener();
        }
        Log.d(LOG_TAG, "Event Repaint triggered");
    }

    //Getters & Setters
    public int getCols() {
        return cols;
    }

    public int getRows() {
        return rows;
    }


}
