
//създаваме клетка,за нашата кординатна мрежа
public class Cell {
    //кординати
    public int i, j;
    //родителска клетка която сочи към настоящата
    public Cell parent;
    //Евристична цена на настоящата клетка
    public int heuristicCost;
    //Финална цена на настоящата клетка
    public int finalCost;
    //ture-ако клетката е част от правилния път
    public boolean solution;

    public Cell(int i, int j) {
        this.i = i;
        this.j = j;
    }

    @Override
    public String toString() {
        return "[" + i + ", " + j + "]";
    }
}
