import java.util.Arrays;

/**
 * BlockSet.java
 * BlockSet will have two children
 */

public class BlockSet {
    private Block[][] content;

    // (R, G, B) format
    private final int[] color;
    // (width, height) format
    private final int[] gridDimensions;
    private final Position posSet;

    public BlockSet(int[][] items, int[] color, Position initialPosition,
                    int[] gridDimensions) {
        this.color = color;
        this.posSet = initialPosition.copy();
        this.gridDimensions = gridDimensions;
        this.getBlockSet(items);
    }

    public Position getPosition() {
        return posSet;
    }


    /**
     * Converts the 1s and zeroes into actual blocks
     * @param items is the list of ones and zeroes
     *
     */
    private void getBlockSet(int[][] items) {
        content = new Block[items.length][items[0].length];
        for (int row = 0; row < items.length; row++) {
            for (int col = 0; col < items[row].length; col++) {
                if (items[row][col] == 1) {
                    // Basically
                    Position newPos =
                            new Position(this.posSet.getX() + row,
                                    this.posSet.getY() + col);
                    content[row][col] =
                            new Block(
                                    newPos,
                                    this.gridDimensions,
                                    this.color);
                }
            }
        }
    }

    /**
     * Rotate block unless obstacle prevents it, we can rotate block
     * if less than 1 block are touching either the left or right edges.
     */
    public void rotate() {
        if (!(leftEdgeInvalid() || rightEdgeInvalid() || touchingBottom())) {
            // rotate content
            int w = content[0].length;
            int h = content.length;

            Block[][] tgt = new Block[w][h]; // swap coordinates

            for (int row = 0; row < h; row++) { // row respective to the original array
                for (int col = w - 1; col >= 0; col--) { // column respective to the original array
                    tgt[w - col - 1][row] = content[row][col];
                }
            }

            content = tgt;
            this.updatePositions();
        }
    }

    /**
     * This basically checks if we have more than one block
     * touching the left edge
     * @return true if above condition is satisfied
     */
    public boolean leftEdgeInvalid() {
        int edgeCount = 0;
        for (Block[] row : content) {
            for (Block block : row) {
                if (block != null) {
                    if (block.getX() == 0) {
                        edgeCount++;
                    }
                }
            }
        }
        return (edgeCount >= 2);
    }

    /**
     * This basically checks if we have more than one block
     * touching the right edge
     * @return true if above condition is satisfied
     */
    public boolean rightEdgeInvalid() {
        int edgeCount = 0;
        for (Block[] row : content) {
            for (Block block : row) {
                if (block != null) {
                    if (block.getX() >= (gridDimensions[0] -1)) {
                        edgeCount++;
                    }
                }
            }
        }
        return (edgeCount >= 2);
    }


    // Checks if any blocks are touch the left edge
    public boolean touchingLeftEdge() {
        for (Block[] row : content) {
            for (Block block : row) {
                if (block != null) {
                    if (block.getX() <= 0) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    // Checks if any blocks are touch the right edge
    public boolean touchingRightEdge() {
        for (Block[] row : content) {
            for (Block block : row) {
                if (block != null) {
                    if (block.getX() == (gridDimensions[0] -1)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public boolean touchingBottom() {
        for (Block[] row : content) {
            for (Block block : row) {
                if (block != null) {
                    if (block.getY() >= (gridDimensions[1] - 1)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    // Updates the positions using top left as anchor point
    private void updatePositions() {
        for (int row = 0; row < content.length; row++) {
            for (int col = 0; col < content[row].length; col++) {
                // not empty lmao
                if (content[row][col] != null) {
                    // Basically
                    Position newPos =
                            new Position(this.posSet.getX() + row,
                                    this.posSet.getY() + col);
                    content[row][col].setPosition(newPos);
                }
            }
        }
    }

    // Shifts blocks one unit to the left unless touching left edge
    public void moveLeft() {
        if (!(touchingLeftEdge())) {
            this.posSet.changePos(-1, 0);
            for (Block[] row : content) {
                for (Block block : row) {
                    if (block != null) {
                        block.goLeft();
                    }
                }
            }
        }
    }

    // returns actual set of blocks consisting BlockSet
    public Block[][] getRawBlockSet() {
        return Arrays.copyOf(this.content, this.content.length);
    }

    // Shifts blocks one unit to the right unless touching right
    public void moveRight() {
        if (!(touchingRightEdge())) {
            this.posSet.changePos(1, 0);
            for (Block[] row : content) {
                for (Block block : row) {
                    if (block != null) {
                        block.goRight();
                    }
                }
            }
        }
    }

    /**
     * Shifts block down by 1 unit
     */
    public void moveDown() {
        if (!(touchingBottom())) {
            // remember that down is up
            this.posSet.changePos(0, 1);
            for (Block[] row : content) {
                for (Block block : row) {
                    if (block != null) {
                        block.goDown();
                    }
                }
            }
        }
    }

    /**
     * Shifts block downwards and to the right unless obstacles are in the way
     * */
    public void moveDownRight() {
        if (!(touchingRightEdge() || touchingBottom())) {
            this.posSet.changePos(1, 1);
            for (Block[] row : content) {
                for (Block block : row) {
                    if (block != null) {
                        block.move(1, 1);
                    }
                }
            }
        }
    }

    /**
     * Shifts block downwards and to the left unless obstacles are in the way
     */
    public void moveDownLeft() {
        if (!(touchingLeftEdge() || touchingBottom())) {
            this.posSet.changePos(-1, 1);
            for (Block[] row : content) {
                for (Block block : row) {
                    if (block != null) {
                        block.move(-1, 1);
                    }
                }
            }
        }
    }

    /**
     * Checks if a part of BlockSet is touching a block pos
     * This works by checking if any block has a point one (not diagonally though)
     * off from the given point.
     *
     * MIGHT BE USELESS AFTER ALL
     * */
    public boolean isTouching(Position pos) {
        for (Block[] row : content) {
            for (Block block : row) {
                if (block != null) {
                    // Intuition: The distance between two positions can only be
                    // less than two if they are in either same row
                    // or same column but one off in x or y direction
                    // this means that they are touching technically
                    if (block.getPosition().distance(pos) < 2) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * Gives us the bottom blocks of the BlockSet
     * @return Array of Blocks
     */
    public Block[] bottomBlocks() {
        return Arrays.copyOf(
                this.content[this.content.length - 1],
                this.content[0].length);
    }

    public String toString() {
        String result = "";
        // Place grid dimensions
        // Percent divides the fields
        result += "("+ this.gridDimensions[0] + "," + this.gridDimensions[1] + ")%";

        // Place actual block dimensions (width, height)
        result += this.content[0].length + "x" + this.content.length + "%";
        for (int row = 0; row < content.length; row++) {
            for (int col = 0; col < content[row].length; col++) {
                if (content[row][col] != null) {
                    result += content[row][col].toString() + "&"; // and separator for the columns
                } else {
                    result += "b&"; // and separator for columns
                }
            }
            result += "<"; // ^ separator for rows
        }
        result += "%";


        // Add block position
        result += this.posSet.toString() + "%";
        // Add block color
        result += "(" + this.color[0] + "," + this.color[1] + "," + this.color[2] + ")";
        return result;
    }


    // Gives us a BlockSet from a string,
    public BlockSet(String input) {
        String[] fields = input.split("%");


        // First field is gridDimensions
        String[] gridDims = fields[0].split(",");
        this.gridDimensions = new int[]{
                Integer.parseInt(gridDims[0].substring(1)),
                Integer.parseInt(gridDims[1].substring(0, gridDims[1].length() -1))
        };


        // Second field is dimensions of BlockSet
        String[] contentDims = fields[1].split("x");
        this.content = new
                Block[Integer.parseInt(contentDims[1])][Integer.parseInt(contentDims[0])];

        // Now pull all content
        String[] allStringRows = fields[2].split("<");
        for (int row = 0; row < allStringRows.length; row++) {
            String[] allSCols = allStringRows[row].split("&");
            for (int col = 0; col < allSCols.length; col++) {
                if (!(allSCols[col].equals("b"))) {
                    this.content[row][col] = new Block(allSCols[col]);
                }
            }
        }

        this.posSet = new Position(fields[3]);

        String[] c = fields[4].split(",");
        this.color = new int[]{
                Integer.parseInt(c[0].substring(1)),
                Integer.parseInt(c[1]),
                Integer.parseInt(c[2].substring(0, c[2].length() -1))
        };
    }


}
