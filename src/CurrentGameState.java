/**
 * CurrentGameState.java
 * Place to save the current game state
 * It can also provide a string representation of the game file
 */

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

public class CurrentGameState {
    private BlockSet currentBlock;
    private BlockSet nextBlock;
    private Block[][] blocks;
    private int currentScore;
    public String gameType;

    public CurrentGameState(BlockSet currentBlock,
                            BlockSet nextBlock,
                            Block[][] blocks,
                            int currentScore,
                            String gameType) {
        this.currentBlock = currentBlock;
        this.nextBlock = nextBlock;
        this.blocks = blocks;
        this.currentScore = currentScore;
        this.gameType = gameType;
    }

    // Set current game state
    public BlockSet getCurrentBlock() {
        return this.currentBlock;
    }

    public BlockSet getNextBlock() {
        return this.nextBlock;
    }

    public Block[][] getBlocks() {
        Block[][] newArr = new Block[this.blocks.length][this.blocks[0].length];
        for (int row = 0; row < blocks.length; row++) {
            for (int col = 0; col < blocks[row].length; col++) {
                if (blocks[row][col] != null) {
                    newArr[row][col] = blocks[row][col].copy();
                }
            }
        }

        return newArr;
    }

    public int getScore() {
        return this.currentScore;
    }


    // Update current game state
    public void setCurrentBlock(BlockSet currentBlock) {
        this.currentBlock = currentBlock;
    }

    public void setNextBlock(BlockSet nextBlock) {
        this.nextBlock = nextBlock;
    }

    public void setBlocks(Block[][] blocks) {
        this.blocks = blocks;
    }

    public void updateGameState(
            BlockSet current,
            BlockSet next,
            Block[][] blocks) {
        this.currentBlock = current;
        this.nextBlock = next;
        this.blocks = blocks;
    }

    public void updateGameState(
            BlockSet current,
            BlockSet next,
            Block[][] blocks,
            int score) {
        this.currentBlock = current;
        this.nextBlock = next;
        this.blocks = blocks;
        this.currentScore = score;
    }

    public void setScore(int score) {
        this.currentScore = score;
    }

    public void changeScore(int dx) {
        this.currentScore += dx;
    }


    /**
     * Overwrites the file at that path (or makes a new one)
     * with conent to save
     * @param pathToSave the path save the current state to
     */
    public void saveGameState(String pathToSave) {

        try {
            FileWriter fw = new FileWriter(pathToSave);
            // Save the dimensions of the board (width, height)
            fw.write(this.gameType + "\n");
            fw.write(this.blocks[0].length + "x" + this.blocks.length + "\n");

            for (int row = 0; row < this.blocks.length; row++) {
                for (int col = 0; col < this.blocks[row].length; col++) {
                    if (this.blocks[row][col] != null) {
                        fw.write(this.blocks[row][col].toString() + " ");
                    } else {
                        fw.write("b ");
                    }
                }

                fw.write("\n");
            }

            fw.write(this.currentBlock.toString() + "\n");
            fw.write(this.nextBlock.toString() + "\n");
            fw.write(this.currentScore + "\n");
            fw.write("END");
            fw.flush();
            fw.close();

        } catch (IOException e) {
            System.out.println("Error while saving game data. Data is NOT saved!!");
            System.out.println(e);
        }
    }

    // Create current game state from a file
    public static CurrentGameState loadFile(String pathToRead){
        Block[][] leBlocks = new Block[10][24];
        BlockSet cBlocc = null;
        BlockSet nBlocc = null;
        String gType = null;
        int cScore = 0;
        try {
            File file = new File(pathToRead);
            Scanner reader = new Scanner(file);
            int lineNum = 1;
            // Gimme some BS
            while (reader.hasNextLine()) {
                String currLine = reader.nextLine();
                if (currLine.equals("END")) {
                    break;
                }

                // Get dimensions for blocks
                if (lineNum == 1) {
                    gType = currLine.trim();
                }
                if (currLine.contains("x") && (lineNum == 2)) {
                    String[] contentDims = currLine.split("x");
                    leBlocks = new
                            Block[Integer.parseInt(contentDims[1])][Integer.parseInt(contentDims[0])];
                }

                if ((lineNum > 2) && (lineNum < leBlocks.length + 3)) {
                    String[] fCols = currLine.trim().split(" ");
                    for (int col = 0; col < fCols.length; col++) {
                        if (!(fCols[col].equals("b"))) {
                            leBlocks[lineNum - 3][col] = new Block(fCols[col]);
                        }
                    }
                }

                if (lineNum == leBlocks.length + 3) {
                    cBlocc = new BlockSet(currLine);
                }

                if (lineNum == leBlocks.length + 4) {
                    nBlocc = new BlockSet(currLine);
                }

                if (lineNum == leBlocks.length + 5) {
                    cScore = Integer.parseInt(currLine);
                }


                lineNum++;
            }

        } catch (FileNotFoundException e) {
            System.out.println("CANNOT LOAD GAME STATE...");
        }

        return new CurrentGameState(
                cBlocc,
                nBlocc,
                leBlocks,
                cScore,
                gType
        );
    }

    public String getGameType() {
        return this.gameType;
    }

}
