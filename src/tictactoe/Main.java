package tictactoe;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.Scanner;
import java.util.function.Function;

public class Main {

    public enum Way {
        ROW, COLUMN, FIRST_DIAGONAL, SECOND_DIAGONAL
    }

    static class GameResult {

        private char result;

        private boolean finished;
        private String message;

        public GameResult(char result, boolean finished, String message) {
            this.result = result;
            this.finished = finished;
            this.message = message;
        }

        public char getResult() {
            return result;
        }

        public void setResult(char result) {
            this.result = result;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public boolean isFinished() {
            return finished;
        }

        public void setFinished(boolean finished) {
            this.finished = finished;
        }
    }
    static class AI {

        int difficulty;
        String message;

        char symbol;

        Map<Character, Integer> scores;

        public AI(int difficulty, String message) {
            this.difficulty = difficulty;
            this.message = message;
            scores = new HashMap<>();
        }

        public Point getTurn(char[][] cells, char symbol) {
            scores.put('_',0);
            scores.put(symbol == 'X'?'X':'O', 1);
            scores.put(symbol == 'X'?'O':'X', -1);
            this.symbol = symbol;
            if (this.difficulty == 1) {
                return getEasyTurn(cells);
            } else if (this.difficulty == 2) {
                return getMediumTurn(cells);
            } else {
                return getHardTurn(cells);
            }
        }

        private Point getEasyTurn(char[][] cells) {
            int x;
            int y;
            Random random = new Random();
            do {
                random = new Random(random.nextLong());
                x = random.nextInt(3);
                y = random.nextInt(3);
            } while (cells[x][y] != '_');
            return new Point(x,y);
        }

        private Point getMediumTurn(char[][] cells) {
            char enemySymbol = symbol == 'X'?'O':'X';
            Point turn = processMediumTurn(cells, symbol);
            if (turn == null) {
                turn = processMediumTurn(cells, enemySymbol);
                if (turn != null)
                    return turn;
            } else {
                return turn;
            }
            return getEasyTurn(cells);
        }

        private Point getHardTurn(char[][] cells) {
            int bestScore = Integer.MIN_VALUE;
            Point bestTurn = null;
            for(int i = 0; i < 3; i++) {
                for(int j = 0; j < 3; j++) {
                    if(cells[i][j] == '_') {
                        cells[i][j] = symbol;
                        int score = miniMax(cells,false);
                        cells[i][j] = '_';
                        if(bestScore < score) {
                            bestScore = score;
                            bestTurn = new Point(i,j);
                        }
                    }
                }
            }
            return bestTurn;
        }

        private int miniMax(char[][] cells, boolean isMaximizing) {
            GameResult gameResult = scanCells(cells);
            if(gameResult.isFinished()) {
                return scores.get(gameResult.getResult());
            }
            int score;
            int bestScore = isMaximizing?Integer.MIN_VALUE:Integer.MAX_VALUE;
            for(int i = 0; i < 3; i++) {
                for(int j = 0; j < 3; j++) {
                    if(cells[i][j] == '_') {
                        cells[i][j] = isMaximizing?symbol:symbol == 'X'?'O':'X';
                        score = miniMax(cells, !isMaximizing);
                        cells[i][j] = '_';
                        if(isMaximizing) {
                            bestScore = Math.max(score, bestScore);
                        } else {
                            bestScore = Math.min(score, bestScore);
                        }
                    }
                }
            }
            return bestScore;
        }


        private Point processMediumTurn(char[][] cells, char symbol) {
            Point turn = null;
            Function<Character, Integer> addToCount = c -> c == symbol?1:0;
            int rowsCount = 0;
            int columnsCount = 0;
            int firstDiagonalCount = 0;
            int secondDiagonalCount = 0;
            for(int i = 0; i < 3; i++) {
                for(int j = 0; j < 3; j++) {
                    rowsCount += addToCount.apply(cells[i][j]);
                    columnsCount += addToCount.apply(cells[j][i]);
                }
                firstDiagonalCount += addToCount.apply(cells[i][i]);
                secondDiagonalCount += addToCount.apply(cells[i][2 - i]);


                if(rowsCount == 2) {
                    turn = checkWayAndGetPoint(cells, i, Way.ROW);
                }
                if(columnsCount == 2) {
                    turn = checkWayAndGetPoint(cells, i, Way.COLUMN);
                }
                if(firstDiagonalCount == 2) {
                    turn = checkWayAndGetPoint(cells, 0, Way.FIRST_DIAGONAL);
                }
                if(secondDiagonalCount == 2) {
                    turn = checkWayAndGetPoint(cells, 0, Way.SECOND_DIAGONAL);
                }
                if (turn != null) {
                    break;
                }
                rowsCount = 0;
                columnsCount = 0;
            }
            return turn;
        }

        public Point checkWayAndGetPoint(char[][] cells, int i, Way way) {
            for (int j = 0; j < 3; j++) {
                switch (way) {
                    case ROW -> {
                        if (cells[i][j] == '_') return new Point(i, j);
                    }
                    case COLUMN -> {
                        if (cells[j][i] == '_') return new Point(j, i);
                    }
                    case FIRST_DIAGONAL -> {
                        if (cells[j][j] == '_') return new Point(j, j);
                    }
                    case SECOND_DIAGONAL -> {
                        if (cells[j][2 - j] == '_') return new Point(j, 2 - j);
                    }
                }
            }
            return null;
        }

        public int getDifficulty() {
            return difficulty;
        }

        public void setDifficulty(int difficulty, boolean whatTurn) {
            this.difficulty = difficulty;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }
    }

    static class Point {
        public int x;
        public int y;

        public Point() {
        }

        public Point(int x, int y) {
            this.x = x;
            this.y = y;
        }

    }
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        String command;
        do {
            System.out.print("Input command: ");
            command = scanner.nextLine();
            if (!command.equals("exit")) {
                startGame(command, scanner);
            }
        }
        while (!command.equals("exit"));
    }

    public static void startGame(String command, Scanner scanner) {
        char[][] cells = new char[3][3];
        for(int i = 0; i < 3; i++)
            for(int j = 0; j < 3; j++)
                cells[i][j] = '_';
        String[] parameters = command.split(" ");
        if (parameters.length != 3) {
            System.out.println("Bad parameters!");
            return;
        }

        AI AIOne = recognizePlayer(parameters[1]);
        AI AITwo = recognizePlayer(parameters[2]);
        showCells(cells);
        GameResult gameResult;
        while(true) {
            processCell(cells, 'X', AIOne, scanner);
            showCells(cells);
            if ((gameResult = scanCells(cells)).isFinished())
                break;
            processCell(cells, 'O', AITwo, scanner);
            showCells(cells);
            if ((gameResult = scanCells(cells)).isFinished())
                break;
        }
        System.out.println(gameResult.getMessage());
    }

//    public static boolean recognizePlayer(String player) {
//        return player.equals("user");
//    }

    public static AI recognizePlayer(String player) {
        return switch (player) {
            case "easy" -> new AI(1, "Making move level \"easy\"");
            case "medium" -> new AI(2, "Making move level \"medium\"");
            case "hard" -> new AI(3, "Making move level \"hard\"");
            default -> null;
        };
    }

    public static void showCells(char[][] cells){
        System.out.println("---------");
        for(int i = 0; i < 3; i++){
            System.out.print("|");
            for(int j = 0; j < 3; j++){
                System.out.print(" ");
                switch (cells[i][j]) {
                    case 'X' -> System.out.print("X");
                    case 'O' -> System.out.print("O");
                    default -> System.out.print(" ");
                }
            }
            System.out.println(" |");
        }
        System.out.println("---------");
    }

    public static void processCell(char[][] cells, char symbol, AI ai, Scanner scanner) {
        Point turn = new Point();
        if (ai == null) {
            System.out.print("Enter the coordinates: ");

            String s = scanner.nextLine();
            try {
                if (s.equals(""))
                    s = scanner.nextLine();
                turn.x = Integer.parseInt(s.split(" ")[0]);
                turn.y = Integer.parseInt(s.split(" ")[1]);
            } catch (NumberFormatException e) {
                System.out.println("You should enter numbers!");
                return;
            }

            if ((turn.x > 3 || turn.x < 1) || (turn.y > 3 || turn.y < 1)) {
                System.out.println("Coordinates should be from 1 to 3!");
                return;
            } else if (cells[turn.x - 1][turn.y - 1] != '_') {
                System.out.println("This cell is occupied! Choose another one!");
                return;
            }

            cells[turn.x-1][turn.y-1] = symbol;

        } else {
            System.out.println(ai.getMessage());
            turn = ai.getTurn(cells, symbol);
            cells[turn.x][turn.y] = symbol;
        }
    }

    public static GameResult scanCells(char[][] cells) {
        GameResult gameResult = checkResultForSymbol(cells, 'X');
        if(gameResult != null) {
            return gameResult;
        }
        gameResult = checkResultForSymbol(cells, 'O');
        if(gameResult != null) {
            return gameResult;
        }
        for(int i = 0; i < 3; i++)
            for(int j = 0; j < 3; j++)
                if(cells[i][j] == '_')
                    return new GameResult('_', false, "");
        return new GameResult('_', true, "Draw\n");
    }

    public static GameResult checkResultForSymbol(char[][] cells, char symbol) {
        Function<Character, Integer> addToCount = c -> c == symbol?1:0;
        int rowsCount = 0;
        int columnsCount = 0;
        int firstDiagonalCount = 0;
        int secondDiagonalCount = 0;
        for(int i = 0; i < 3; i++) {
            for(int j = 0; j < 3; j++) {
                rowsCount += addToCount.apply(cells[i][j]);
                columnsCount += addToCount.apply(cells[j][i]);
            }
            firstDiagonalCount += addToCount.apply(cells[i][i]);
            secondDiagonalCount += addToCount.apply(cells[i][2 - i]);
            // 0 2 | 1 1 | 2 0 -> 0 1 2


            if(rowsCount == 3 || columnsCount == 3 || firstDiagonalCount == 3 || secondDiagonalCount == 3) {
                return new GameResult(symbol, true, symbol + " wins");
            }
            rowsCount = 0;
            columnsCount = 0;
        }

        return null;
    }
}
