import java.util.ArrayList;

/**
 * This class represents a table in which each row has a variable size.
 * @param <T> Type of elements in table cells
 */
public class Table <T> {

    ArrayList<ArrayList<T>> rows = new ArrayList<ArrayList<T>>();

    public Table(int nRows) {
        for (int i = 0; i < nRows; i++)
            this.rows.add(new ArrayList<T>());
    }

    public int getNrows() { return rows.size(); }

    public T get(int row, int col) {
        return rows.get(row).get(col);
    }

    public void add(int row, T element) {
        rows.get(row).add(element);
    }

    public void addAll(int row, ArrayList<T> elements) { rows.get(row).addAll(elements); }

    public void set(int row, int col, T value) { rows.get(row).set(col, value); }

    public int rowLength(int row) {
        return rows.get(row).size();
    }

    public ArrayList<T> getRow(int row) {
        return rows.get(row);
    }

    void addTable(Table<T> table) {
        for (int row = 0; row < table.rows.size(); row++) {
            addAll(row, table.getRow(row));
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this)
            return true;
        if (obj == null || getClass() != obj.getClass())
            return false;
        Table that = (Table) obj;
        for (int row = 0; row < that.getNrows(); row++) {
            ArrayList rowList = that.getRow(row);
            if (!rowList.equals(getRow(row)))
                return false;
        }
        return true;
    }
}
