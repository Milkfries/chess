package chess;

/**
 * Represents a single square position on a chess board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPosition {
    private int row;
    private int col;

    public ChessPosition(int row, int col) {
        this.row = row;
        this.col = col;
    }

    /**
     * @return which row this position is in
     * 1 codes for the bottom row
     */
    public int getRow() {
        return row;
    }

    /**
     * @return which column this position is in
     * 1 codes for the left column
     */
    public int getColumn() {
        return col;
    }
    public ChessPosition add(int[] direction){
        int row = this.row + direction[0];
        int col = this.col + direction[1];
        return new ChessPosition(row, col);
    }
    public ChessPosition add(int[] direction,int scale){
        int new_row =  (this.row + scale * direction[0]);
        int new_col =  (this.col + scale * direction[1]);
        return new ChessPosition(new_row, new_col);
    }

    public boolean inBoard(){
        return (row >=1 && row <=8 && col >= 1 && col <=8);    
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + row;
        result = prime * result + col;
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        ChessPosition other = (ChessPosition) obj;
        if (row != other.row)
            return false;
        if (col != other.col)
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "ChessPosition [row=" + row + ", col=" + col + "]";
    }
    


}
