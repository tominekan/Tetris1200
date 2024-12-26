import java.util.Arrays;

/**
 * TetrisLogic.java
 * Import
 */
public class TetrisLogic {
    // Some useful fields
    protected Block[][] board;
    protected PieceInfo currentBlock;
    protected BlockSet cBlockSet;
    protected PieceInfo nextBlock;
    protected CurrentGameState gameState;
    protected final int BOARD_HEIGHT;
    protected final int BOARD_WIDTH;
    protected final Position startingPosition;
    protected boolean isPaused;
    protected String gameType = "Normal";


    /**
     * PieceInfo subclass,
     * used give info (color and shape) of each of the seven pieces
     * very convenient for me
     */
    protected class PieceInfo {
        private final int[][] shape;
        private final int[] color;

        public PieceInfo(int[][] shape, int[] color) {
            this.shape = shape;
            this.color = color;
        }

        int[][] getShape() {
            return Arrays.copyOf(shape, shape.length);
        }

        int[] getColor() {
            return color;
        }

        // We know that we can't really play with a PieceInfo Object
        // so we want to convert it to BlockSet so we can play with it
        public BlockSet toPlayable() {
            return new BlockSet(
                    this.shape,
                    this.color,
                    startingPosition,
                    new int[] {BOARD_WIDTH, BOARD_HEIGHT}
            );
        }
    }


    // All Block Pieces
    // TODO: Maybe add some more shapes for another game mode??
    protected final PieceInfo LShape = new PieceInfo(
            new int[][]{{1, 0}, {1, 0}, {1, 1}},
            new int[] {255, 108, 22});

    protected final PieceInfo JShape = new PieceInfo(
            new int[][] {{0, 1}, {0, 1}, {1, 1}},
            new int[] {45, 0, 249});

    protected final PieceInfo OShape = new PieceInfo(
            new int[][] {{1, 1}, {1, 1}},
            new int[] {255, 205, 24});

    protected final PieceInfo IShape = new PieceInfo(
            new int[][] {{0, 1, 0}, {0, 1, 0}, {0, 1, 0}, {0, 1, 0}},
            new int[] {0, 215, 252});

    protected final PieceInfo SShape = new PieceInfo(
            new int[][] {{0, 1, 1}, {1, 1, 0}},
            new int[] {104, 238, 19});

    protected final PieceInfo ZShape = new PieceInfo(
            new int[][] {{1, 1, 0}, {0, 1, 1}},
            new int[] {255, 26, 69});

    protected final PieceInfo TShape = new PieceInfo(
            new int[][] {{1, 1, 1}, {0, 1, 0}},
            new int[] {173, 0, 250}
    );

    // Set of all pieces
    protected final PieceInfo[] blockOptions = new PieceInfo[] {
            LShape, JShape, OShape, IShape, SShape, ZShape, TShape
    };

    /**
     * Set a custom starting position for the game of tetris
     * TODO: in the future, implement random starting position in the future??
     * */
    public TetrisLogic(int board_width, int board_height, Position startingPosition) {
        this.BOARD_WIDTH = board_width;
        this.BOARD_HEIGHT = board_height;
        this.startingPosition = startingPosition;
        reset();
    }

    public int[] getBoardDimensions() {
        return new int[] {BOARD_WIDTH, BOARD_HEIGHT};
    }

    /**
     *  Stick with starting position being offscreen
     *  */
    public TetrisLogic(int board_width, int board_height) {
        this.BOARD_WIDTH = board_width;
        this.BOARD_HEIGHT = board_height;
        this.startingPosition = new Position((BOARD_WIDTH/2)-1, -4);
        reset();
    }

    public TetrisLogic(CurrentGameState gameState) {
        isPaused = false;
        this.gameState = gameState;
        this.BOARD_WIDTH = this.gameState.getBlocks()[0].length;
        this.BOARD_HEIGHT = this.gameState.getBlocks().length;
        this.startingPosition = new Position((BOARD_WIDTH/2)-1, -4);
        this.cBlockSet = this.gameState.getCurrentBlock();
        this.board = this.gameState.getBlocks();
    }

    /**
     * Sets game state to initial conditions, updates CurrentGameState
     * */
    public void reset() {
        // We create a board that is 10 blocks wide and 40 blocks tall
        // Standard Tetris Board with greater than average height
        board = new Block[this.BOARD_HEIGHT][this.BOARD_WIDTH];
        gameState = new CurrentGameState(
                null,
                null,
                Arrays.copyOf(board, board.length),
                0,
                this.gameType);
        isPaused = false;
        genRandomBlock();
    }

    /**
     * Generates a random next block.
     * Updates CurrentGameState
     */
    public void genRandomBlock() {
        // Choose random piece
        if (!isPaused) {
            int randomIndex = (int) (Math.random() * blockOptions.length);
            // We've generated a block
            if (this.currentBlock == null) {
                this.currentBlock = blockOptions[randomIndex];
                this.cBlockSet = currentBlock.toPlayable();
                this.gameState.setCurrentBlock(cBlockSet);

                randomIndex = (int) (Math.random() * blockOptions.length);
                this.nextBlock = blockOptions[randomIndex];
                this.gameState.setNextBlock(nextBlock.toPlayable());

            } else {
                this.currentBlock = this.nextBlock;
                this.cBlockSet = this.nextBlock.toPlayable();
                this.gameState.setCurrentBlock(this.cBlockSet);

                this.nextBlock = blockOptions[randomIndex];
                this.gameState.setNextBlock(nextBlock.toPlayable());
            }
        }
    }

    /**
     * Moves the current block down one unit
     * If we are touching the bottom, then move on to the next block.
     */
    public void moveDownOne() throws GameOverException {
        if (!isPaused) {
            if (this.cBlockSet.touchingBottom() || readyToPlace()) {
                this.convertToBlocks(); // fails here
                genRandomBlock();
            } else {
                this.gameState.changeScore(1);
                this.cBlockSet.moveDown();
            }
            this.gameState.setCurrentBlock(this.cBlockSet);
        }
    }

    public void quickFall() throws GameOverException {
        if (!isPaused) {
            while (!(this.cBlockSet.touchingBottom() || readyToPlace())) {
                this.moveDownOne();
            }
            moveDownOne();
        }
    }


    /**
     * Moves the current block to the left one unit
     * If we are touching the left edge, then stop moving left
     * */
    public void moveLeft() {
        if (noneToLeft() && !isPaused) {
            this.cBlockSet.moveLeft();
            this.gameState.setCurrentBlock(this.cBlockSet);
        }
    }

    /**
     * Moves the current block to the right one unit.
     * If we are touching the right edge, then stop moving right
     * */
    public void moveRight() {
        if (noneToRight() && !isPaused) {
            this.cBlockSet.moveRight();
            this.gameState.setCurrentBlock(this.cBlockSet);
        }

    }

    /**
     * Rotates pieces 180 unless touching edges or touching other pieces
     */
    public void rotate() {
        // This is a manual fix for the bug where rotating the T shape
        // causes a Block rotate out of bounds
        if (!isPaused) {
            if (this.currentBlock == this.blockOptions[6]) {
                if (!(readyToPlace()) &&
                        !(cBlockSet.touchingRightEdge()) &&
                        !(cBlockSet.touchingLeftEdge())) {
                    this.cBlockSet.rotate();
                    this.gameState.setCurrentBlock(this.cBlockSet);
                }
            } else if (!readyToPlace()) {
                this.cBlockSet.rotate();
                this.gameState.setCurrentBlock(this.cBlockSet);
            }
        }
    }

    /**
     * Adds each block in the BlockSet to the actual board
     * Updates CurrentGameState
     * This also increments the score by 10 because it means that
     * we successfully put the block down.
     * */
    public void convertToBlocks() throws GameOverException {
        this.gameState.changeScore(10);
        for (Block[] row : this.cBlockSet.getRawBlockSet()) {
            for (Block block : row) {
                if (block != null) {
                    int cX = block.getX();
                    int cY = block.getY();


                    // INDICATOR OF GAME OVER.
                    // this is when we touch the bottom of a block
                    // and we don't have all the block in view
                    if (cY < 0) {
                        throw new GameOverException();
                    } else {
                        this.board[cY][cX] = block;
                    }
                }
            }
        }
        this.gameState.setBlocks(Arrays.copyOf(board, board.length));

    }

    /**
     * Returns current game state
     * */
    public CurrentGameState getCurrentGameState() {
        return gameState;
    }

    /**
     * Checks if any of the blocks in BlockSet has a block right below it
     * if it does, then we cannot move below that block.
     * @return boolean, true if there is a block right below any block in
     * BlockSet.
     */
    public boolean readyToPlace() {
        for (Block[] row : this.cBlockSet.getRawBlockSet()) {
            for (Block block : row) {
                if (block != null) {
                    int yPos = block.getY();
                    int xPos = block.getX();
                    // If there is a block right below it, then we are ready
                    // to place.
                    if (yPos < 0) {
                        return false;
                    }

                    if (yPos < BOARD_HEIGHT) {
                        if (this.board[yPos+1][xPos] != null) {
                            return true;
                        }
                    } else {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * Checks if there are no obstacles to the right
     */
    public boolean noneToRight() {
        for (Block[] row : this.cBlockSet.getRawBlockSet()) {
            for (Block block : row) {
                if (block != null) {
                    int yPos = block.getY();
                    int xPos = block.getX();
                    // If we are on the rightmost edge
                    if (xPos >= (BOARD_WIDTH -1) ) {
                        return false;
                    } else {
                        // if there's something to the right
                        if (yPos >= 0 && this.board[yPos][xPos+1] != null) {
                            return false;
                        }
                    }
                }
            }
        }
        return true;
    }

    public boolean noneToLeft() {
        for (Block[] row : this.cBlockSet.getRawBlockSet()) {
            for (Block block : row) {
                if (block != null) {
                    int yPos = block.getY();
                    int xPos = block.getX();
                    // If we are on the left edge.
                    if (xPos == 0 ) {
                        return false;
                    } else {
                        // if there's something to the left
                        if (yPos >= 0 && this.board[yPos][xPos-1] != null) {
                            return false;
                        }
                    }
                }
            }
        }
        return true;
    }

    /**
     * Checks if any of the rows are full, returns the row number
     * @return -1 none of the rows are full, row number otherwise.
     */
    public int hasFullRow() {
        int numFull = 0;
        int rowNum = 0;
        for (Block[] row: this.board) {
            // Check if every block in the row has an item
            numFull = 0;
            for (Block block : row) {
                if (block != null) {
                    numFull++;
                };
            }

            if (numFull == this.board[rowNum].length) {
                return rowNum;
            }
            rowNum++;
        }
        return -1;
    }

    /**
     * Clears all the full rows and moves the other ones down
     * also updates positions of each block in the Blockset.
     */
    public void clearFullRows() {
        int rowNum = hasFullRow();
        // We have a full row
        if (rowNum != -1 && !isPaused) {
            this.gameState.changeScore(100);


            // this line a lil stupid
//            // Sleep for a lil bit
//            try {
//                // MAYBE REMOVE THIS?? (INTERRUPTS GAME FLOW)
//                TimeUnit.MILLISECONDS.sleep(300);
//            } catch (InterruptedException e) {
//                System.out.println("QUICK_SLEEP WAS INTERRUPTED");
//            }

            // empty that row out
            this.board[rowNum] = new Block[BOARD_WIDTH];

            // Shift everything downwards
            for (int row = rowNum; row > 0; row--) {
                this.board[row] = this.board[row-1];
            }
            // Clear out the top row
            this.board[0] = new Block[BOARD_WIDTH];
            // Maybe move current block down
            this.cBlockSet.moveDown();
        }
        this.gameState.setBlocks(board);
    }




    /**
     * Not-so Pretty-prints the board for debugging purposes
     */
    public void pPrint() {
        System.out.println("--------------------------------------");
        for (int row = 0; row < this.board.length; row++) {
            for (int col = 0; col < this.board[row].length; col++) {
                if (this.board[row][col] != null) {
                    System.out.print("B    ");
                } else {
                    System.out.print("-    ");
                }
            }
            System.out.println("\n");
        }
        System.out.println("------------------------------------");
    }

    public void togglePause() {
        this.isPaused = !isPaused;
    }

    public void pauseGame() {
        this.isPaused = true;
    }

    public void resumeGame() {
        this.isPaused = true;
    }

    public void setGameType(String newGameType) {
        this.gameType = newGameType;
    }


}
