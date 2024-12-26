/**
 * Position.java
 * Just a basic position tracking object, very convenient.
 */
public class Position {
    public int x;
    public int y;

    public Position(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    // sets the current position to x and y
    public void setPos(int newX, int newY) {
        this.x = newX;
        this.y = newY;
    }

    // Changes current position by dx and dy
    public void changePos(int dx, int dy) {
        this.x += dx;
        this.y += dy;
    }

    /**
     * Checks if another Position has same position
     * @param other
     * @return true if positions are equal, false otherwise
     */
    public boolean equals(Object other) {
        if (other ==  null || getClass() != other.getClass()) {
            return false;
        }

        if (this == other) {
            return true;
        }

        // Cast and check if the contents are equal
        Position otherP = (Position) other;
        return ((this.x == otherP.x ) && (this.y == otherP.y));
    }

    /**
     * returns distance between two positions
     * @param other position to measure distance from
     * @return distance between two positions
     */
    public int distance(Position other) {
        return (Math.abs(this.x - other.x) + Math.abs(this.y - other.y));
    }

    // returns a copy of the same position
    public Position copy() {
        return new Position(this.x, this.y);
    }

    public String toString() {
        return "(" + this.x + "," + this.y + ")";
    }

    // given (x,y) give us position
    public Position(String inputString) {
        String[] str = inputString.split(",");
        this.x = Integer.parseInt(str[0].substring(1));
        this.y = Integer.parseInt(str[1].substring(0, str[1].length() -1));
    }
}
