import java.util.PriorityQueue;

public class AStar {
    //цена за диагонал и вертикал/хоризонтални движения
    public static final int DIAGONAL_COST = 15;
    public static final int V_H_COST = 10;
    //Масив от клетки съставящи кординатна мрежа
    private Cell[][] grid;
    //Дефинираме приоритетна опашка за свободните клетки
    //Свободните клетки : колекция от възли, който трябва да бъдат отценени
    //слагаме в опашката първо клетките с най-малка цена
    private PriorityQueue<Cell> openCells;
    //Заети клетки: колекция от възли,които вече са отценени
    private boolean[][] closedCells;
    //Кординати на започваща клетка
    private int startI, startJ;
    //Кординати на финална клетка
    private int endI, endJ;

    public AStar(int width, int height, int si, int sj, int ei, int ej, int[][] blocks) {
        grid = new Cell[width][height];
        closedCells = new boolean[width][height];
        openCells = new PriorityQueue<>((Cell c1, Cell c2) -> c1.finalCost < c2.finalCost ? -1 : c1.finalCost > c2.finalCost ? 1 : 0);
        startCell(si, sj);
        endCell(ei, ej);

        //инициализиране на евристичният аугоритъм
        for (int i = 0; i < grid.length; i++) {
            for (int j = 0; j < grid[i].length; j++) {
                grid[i][j] = new Cell(i, j);
                //Даване на евристична оценка на върха
                grid[i][j].heuristicCost = Math.abs(i - endI) + Math.abs(j - endJ);
                grid[i][j].solution = false;
            }
        }
        //задаваме цената на първоначалният елемент
        grid[startI][startJ].finalCost = 0;
        //слагаме блоковете в кординатната мрежа
        for (int i = 0; i < blocks.length; i++) {
            addBlockOnCell(blocks[i][0], blocks[i][1]);
        }
    }

    //Задава всички блокове(препядствия на кординатната мрежа) да бъдат равни на null
    public void addBlockOnCell(int i, int j) {
        grid[i][j] = null;
    }

    //Задава кординатите на началния връх
    public void startCell(int i, int j) {
        startI = i;
        startJ = j;
    }

    //Задава кординатите на краиния връх
    public void endCell(int i, int j) {
        endI = i;
        endJ = j;
    }

    public void updateCostIfNeeded(Cell current, Cell t, int cost) {
        if (t == null || closedCells[t.i][t.j])
            return;

        int tFinalCost = t.heuristicCost + cost;
        boolean isOpen = openCells.contains(t);

        if (!isOpen || tFinalCost < t.finalCost) {
            t.finalCost = tFinalCost;
            t.parent = current;

            if (!isOpen)
                openCells.add(t);
        }
    }

    //Инициира търсенето на най-кратък маршрут
    public void process() {
        //добавяме стартовата точка в отворената колекция
        openCells.add(grid[startI][startJ]);
        Cell current;

        while (true) {
            //Взимаме елемент на който е възможно да стъпим
            current = openCells.poll();

            //Ако елемент е равен на null,това означава, че няма къде да стъпим и напускаме цикъла
            if (current == null)
                break;

            //Отчитаме върха като оценен
            closedCells[current.i][current.j] = true;

            //Проверяваме дали сме стигнали крайния връх и ако това е така напускаме цикъла
            if (current.equals(grid[endI][endJ]))
                return;

            Cell t;
            if (current.i - 1 >= 0) {
                t = grid[current.i - 1][current.j];
                updateCostIfNeeded(current, t, current.finalCost + V_H_COST);

                if (current.j - 1 >= 0) {
                    t = grid[current.i - 1][current.j - 1];
                    updateCostIfNeeded(current, t, current.finalCost + DIAGONAL_COST);
                }

                if (current.j + 1 < grid[0].length) {
                    t = grid[current.i - 1][current.j + 1];
                    updateCostIfNeeded(current, t, current.finalCost + DIAGONAL_COST);
                }
            }

            if (current.j - 1 >= 0) {
                t = grid[current.i][current.j - 1];
                updateCostIfNeeded(current, t, current.finalCost + V_H_COST);
            }

            if (current.j + 1 < grid[0].length) {
                t = grid[current.i][current.j + 1];
                updateCostIfNeeded(current, t, current.finalCost + V_H_COST);
            }

            if (current.i + 1 < grid.length) {
                t = grid[current.i + 1][current.j];
                updateCostIfNeeded(current, t, current.finalCost + V_H_COST);

                if (current.j - 1 >= 0) {
                    t = grid[current.i + 1][current.j - 1];
                    updateCostIfNeeded(current, t, current.finalCost + DIAGONAL_COST);
                }

                if (current.j + 1 < grid[0].length) {
                    t = grid[current.i + 1][current.j + 1];
                    updateCostIfNeeded(current, t, current.finalCost + DIAGONAL_COST);
                }
            }

        }
    }

    //Функция, която показва началната и краината точка,върховете през който може да се минава,както и тези през който не може
    public void display() {
        System.out.println("Grid :");
        for (int i = 0; i < grid.length; i++) {
            for (int j = 0; j < grid[i].length; j++) {
                if (i == startI && j == startJ) {
                    System.out.print("SO ");//Локация на начална клетка
                } else if (i == endI && j == endJ) {
                    System.out.print("DE ");//Локация на финална клетка
                } else if (grid[i][j] != null) {
                    System.out.printf("%-3d", 0);
                } else {
                    System.out.print("BL ");//блок клетка
                }
            }
            System.out.println();
        }
        System.out.println();
    }

    //Функция показваща цената на всеки връх
    public void displayScores() {
        System.out.println("\nScores for cells :");

        for (int i = 0; i < grid.length; i++) {
            for (int j = 0; j < grid[i].length; j++) {
                if (grid[i][j] != null) {
                    System.out.printf("%-3d", grid[i][j].finalCost);
                } else {
                    System.out.print("BL ");
                }
            }
            System.out.println();
        }
        System.out.println();
    }

    //Функция принтираща най-късото разстояние, ако има такова или "No solution",ако няма път между двете точки
    public void displaySolution() {
        if (closedCells[endI][endJ]) {
            System.out.println("Path :");
            Cell current = grid[endI][endJ];
            System.out.print(current);
            grid[current.i][current.j].solution = true;


            while (current.parent != null) {
                System.out.print("->" + current.parent);
                grid[current.parent.i][current.parent.j].solution = true;
                current = current.parent;
            }

            System.out.println("\n");

            for (int i = 0; i < grid.length; i++) {
                for (int j = 0; j < grid[i].length; j++) {
                    if (i == startI && j == startJ) {
                        System.out.print("SO ");//Локация на начална клетка
                    } else if (i == endI && j == endJ) {
                        System.out.print("DE ");//Локация на финална клетка
                    } else if (grid[i][j] != null) {
                        System.out.printf("%-3s", grid[i][j].solution ? "X" : "0");
                    } else {
                        System.out.print("BL ");//блок клетка(препядствие)
                    }
                }
                System.out.println();
            }
            System.out.println();
        } else
            System.out.println("No possible path");
    }
}
