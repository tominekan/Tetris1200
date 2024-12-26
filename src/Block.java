import java.util.Arrays;

/**
 * Block.java
 * Keep in mind that the gridDimensions of the grid are 10x24
 * Blocks are by default movable
 * */
public class Block {
    private final Position pos;
    // The gridDimensions of the grid (x,y)
    private final int[] gridDimensions;

    // (r, g, b) format
    private final int[] color;

    public Block(Position pos, int[] gridDimensions,
                 int[] color) {
        this.pos = pos;
        this.gridDimensions = gridDimensions;
        this.color = color;
    }

    // Only moves the characters if they are movable by default
    public void move(int dx, int dy) {
           this.pos.changePos(dx, dy);
    }

    public int[] getColor() {
        return Arrays.copyOf(this.color, this.color.length);
    }

    // Shifts block one unit to the left
    // If the block is at leftmost edge, then we cannot move
    public void goLeft() {
        move(-1, 0);
    }

    // Shifts block one unit to the right
    // If the block is at the rightmost edge, then we cannot move
    public void goRight() {
        move(1, 0);
    }

    // Shifts block one unit to downwards
    // If we are at the bottom, then stop falling
    public void goDown() {
        // keep in mind that array indexing starts at zero
        move(0, 1);
    }

    // sets the position of the block regardless of isMovable
    // useful for shifting the position of the blocks
    public void setPosition(int x, int y) {
        this.pos.setPos(x, y);
    }

    public void setPosition(Position pos) {
        this.pos.setPos(pos.getX(), pos.getY());
    }

    // Gets current position of the block
    public Position getPosition() {
        return this.pos.copy();
    }

    public int getX() {
        return this.pos.getX();
    }

    public int getY() {
        return this.pos.getY();
    }

    public Block copy() {
        return new Block(
                this.pos,
                this.gridDimensions,
                this.color);
    }

    /**
     * Gets string representation of block to save
     * @return String representing block
     */
    public String toString() {
        String result = "";
        result += this.pos.toString() + ";";
        result += "(" + this.gridDimensions[0] + "," + this.gridDimensions[1] + ");";
        result += "(" + this.color[0] + "," + this.color[1] + "," + this.color[2] + ");";
        return result;
    }

    /**
     * Gives us BlockObject from input string
     */
    public Block(String input) {
        String[] tokens = input.split(";");
        // parse position
        this.pos = new Position(tokens[0]);

        // given dimensions (x,y) grab dimensions
        String[] gridDims = tokens[1].split(",");
        this.gridDimensions = new int[]{
                Integer.parseInt(gridDims[0].substring(1)),
                Integer.parseInt(gridDims[1].substring(0, gridDims[1].length() -1))
        };

        // given colors (r,g,b) grab colors
        String[] c = tokens[2].split(",");
        this.color = new int[]{
                Integer.parseInt(c[0].substring(1)),
                Integer.parseInt(c[1]),
                Integer.parseInt(c[2].substring(0, c[2].length() -1))
        };
    }


}
