package enemy;

public class Cell {
    public final int row, col;
    public final double baseX, baseY;
    public final String preferredType;
    public int counterRemaining;
    public Enemy occupant;

    public Cell(int row, int col, double baseX, double baseY, String preferredType, int counterRemaining) {
        this.row = row;
        this.col = col;
        this.baseX = baseX;
        this.baseY = baseY;
        this.preferredType = preferredType;
        this.counterRemaining = counterRemaining;
    }

    public boolean isSpent() {
        if (occupant == null) {
            if (counterRemaining <= 0) {
                return true;
            }
        }
        return false;
    }
}