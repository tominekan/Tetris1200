import java.util.Arrays;

/**
 * RandomStart.java
 * This is a new game mode where we initialize random blocks
 * at the start, and then start playing from there.
 */
public class RandomStart extends TetrisLogic{
    public RandomStart(int board_width, int board_height) {
        super(board_width, board_height);
        super.gameType = "Random Start";
    }

    public RandomStart(CurrentGameState cGameState) {
        super(cGameState);
        super.gameType = "Random Start";
    }

    /**
     * Here is where we implement the reset method, creating
     * a new non-empty block.
     *
     * Replace the first 4 rows with a block
     */
    @Override
    public void reset() {
        super.board = new Block[super.BOARD_HEIGHT][super.BOARD_WIDTH];

        // Populate the board with some random shii
        for (int row = BOARD_HEIGHT - 1; row >= 14; row--) {
            for (int col = 0; col < BOARD_WIDTH; col++) {
                int coinFlip = (int) (Math.random() * 2);
                if (coinFlip == 1) {
                    super.board[row][col] = new Block(
                            new Position(col, row),
                            new int[] {10, 24},
                            new int[] {168, 167, 171}
                    );
                }
            }
        }


        super.gameState = new CurrentGameState(
                null,
                null,
                Arrays.copyOf(super.board, super.board.length),
                0,
                super.gameType
        );
        super.isPaused = false;
        genRandomBlock();
    }
}
