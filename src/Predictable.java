/**
 * Predictable.java
 * This is the same as a normal version of tetris, except
 * the blocks are generated in a predictable pattern.
 */

public class Predictable extends TetrisLogic {
    private int currentBlockIndex = 0;
    public Predictable(int width, int height) {
        super(width, height);
    }

    public Predictable(CurrentGameState gState) {
        super(gState);
    }

    @Override
    public void genRandomBlock() {
        if (!isPaused) {
            // We've never generated a block before
            // then we start from zero
            if (super.currentBlock == null) {
                super.currentBlock = super.blockOptions[currentBlockIndex];
                super.cBlockSet = super.currentBlock.toPlayable();
                super.gameState.setCurrentBlock(super.cBlockSet);

                super.nextBlock = super.blockOptions[currentBlockIndex+1];
                super.gameState.setNextBlock(super.nextBlock.toPlayable());
            } else {
                super.currentBlock = super.nextBlock;
                super.cBlockSet = super.nextBlock.toPlayable();
                this.gameState.setCurrentBlock(this.cBlockSet);

                this.nextBlock = blockOptions[currentBlockIndex+1];
                this.gameState.setNextBlock(nextBlock.toPlayable());
            }

            if (currentBlockIndex == (super.blockOptions.length - 2)) {
                currentBlockIndex = 0;
            } else {
                currentBlockIndex++;
            }
        }
    }
}
