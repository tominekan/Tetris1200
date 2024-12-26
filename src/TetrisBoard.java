/**
 * TetrisBoard.java
 * Handles all the drawing/updating of the tetris board, score, and other shii
 */

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.File;
import java.util.Timer;
import java.util.TimerTask;

public class TetrisBoard extends JPanel {
    // Board/Game dimensions
    private final int BOARD_WIDTH;
    private final int BOARD_HEIGHT;
    private final int BOX_WIDTH = 10;
    private final int BOX_HEIGHT = 24;

    // Controls how fast we are falling
    private final int delay = 130;
    public JLabel status;
    private final TetrisLogic logic;
    private boolean gameOver;
    private final String pathToSave;

    public TetrisBoard(JLabel statusInit, TetrisLogic game, String pathToSave,
                       int size) {
        setBackground(Color.BLACK);
        setFocusable(true);
        status = statusInit; // idk why this line is necessary
        this.logic = game;
        this.pathToSave = pathToSave;
        this.BOARD_WIDTH = size;
        this.BOARD_HEIGHT = (int) (BOARD_WIDTH * 2.4);
        this.startGameFunctions();
    }


    // Renders the game
    public void startGameFunctions() {
        Thread downThread = new Thread(new Runnable() {
            public void run() {
                Timer t1 = new Timer();

                // New method to update falls
                TimerTask fallDown = new TimerTask() {
                    public void run() {
                        // Feels very hacky, but we declare the timer
                        // if the game is over, then stop drawing
                        if (gameOver) {
                            status.setText("Game Over");
                            t1.cancel();
                            t1.purge();
                        }

                        try {
                            repaint();
                            logic.moveDownOne();
                            logic.clearFullRows();
                            repaint();

                        } catch (GameOverException e2) {
                            gameOver = true;
                        }
                    }
                };

                t1.scheduleAtFixedRate(fallDown, 0, delay);



            }
        });

        // Handle keyboard inputs in a separate thread
        // honestly not sure if there's a significant performance improvement
        // but it makes me feel better about my code.
        Thread moveThread = new Thread(new Runnable() {
            public void run() {
                requestFocusInWindow();
                addKeyListener(new KeyAdapter() {
                    // Arrows and WASD
                    // R to restart
                    // P to pause
                    public void keyPressed(KeyEvent e) {
                        if ((e.getKeyCode() == KeyEvent.VK_RIGHT) ||
                                (e.getKeyCode() == KeyEvent.VK_D)) {
                            repaint();
                            logic.moveRight();
                            logic.clearFullRows();
                            repaint();
                        } else if (e.getKeyCode() == KeyEvent.VK_LEFT||
                                (e.getKeyCode() == KeyEvent.VK_A)) {
                            repaint();
                            logic.moveLeft();
                            logic.clearFullRows();
                            repaint();
                        } else if (e.getKeyCode() == KeyEvent.VK_UP ||
                                (e.getKeyCode() == KeyEvent.VK_W)) {
                            repaint();
                            logic.rotate();
                            logic.clearFullRows();
                            repaint();
                        } else if (e.getKeyCode() == KeyEvent.VK_SPACE) {
                            repaint();
                            logic.quickFall();
                            logic.clearFullRows();
                            repaint();
                        } else if (e.getKeyCode() == KeyEvent.VK_R) {
                            // Restart this bih
                            repaint();
                            resetGame();
                            repaint();
                        } else if (e.getKeyCode() == KeyEvent.VK_P) {
                            repaint();
                            logic.togglePause();
                            repaint();
                        } else if (e.getKeyCode() == KeyEvent.VK_I) {
                            repaint();
                            logic.pauseGame();
                            showInfoScreen();
                        }
                    }


                });
            }
        });


        // Performance needs to be better lmao
        downThread.start();
        moveThread.start();
    }


    // Set t
    public void resetGame() {
        if (gameOver) {
            gameOver = false;
            logic.reset();
            this.startGameFunctions();
        } else {
            logic.reset();
        }
    }

    @Override
    /**
     * This draws a bunch of squares representing each block.
     */
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        setGameFont(g, "LARGE");
        if (!gameOver) {
            renderBlocks(g);
        } else {
            gameOverScreen(g);
        }

    }


    /**
     * Displays info screen snd pauses game
     *
     */
    public void showInfoScreen() {

        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {

                GridLayout tonka = new GridLayout(5, 0, 0, 10);

                JPanel allInfo = new JPanel(tonka);
                JFrame infoScreen = new JFrame("Info Screen");
                JLabel about = new JLabel("Tetris1200 by Tomi Adenekan");

                JLabel basicInfo = new JLabel("This plays like a regular tetris game");

                JLabel movementInfo = new JLabel("WASD/Arrow Keys to move/rotate the block");

                JLabel pointInfo = new JLabel("Try to get as many points as possible");

                infoScreen.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

                allInfo.add(about);
                allInfo.add(basicInfo);
                allInfo.add(movementInfo);
                allInfo.add(pointInfo);

                infoScreen.add(allInfo);
                infoScreen.pack();
                infoScreen.setVisible(true);
                infoScreen.setFocusable(true);


            }
        });
    }



    public void gameOverScreen(Graphics g) {
        g.setColor(Color.WHITE);
        CurrentGameState gameState = logic.getCurrentGameState();
        String finalScore = "" + gameState.getScore();
        // Middle of the screen, we're hardcoding the centers
        g.drawString("Game Over", 12, 100);
        setGameFont(g, "MEDIUM");
        g.drawString("YOUR SCORE:", 12, 150);
        g.drawString(finalScore, 12, 175);
    }

    /**
     * Set up the game font, also save files
     * @param g Graphics context
     * @param size size of font
     */
    public void setGameFont(Graphics g, String size) {
        CurrentGameState gameState = logic.getCurrentGameState();
        gameState.saveGameState(this.pathToSave);
        try {
            ClassLoader loader = getClass().getClassLoader();
            Font ourFont = Font.createFont(Font.TRUETYPE_FONT, loader.getResourceAsStream("04B_30__.TTF"));
            if (size.equals("LARGE")) {
                Font finalFont = ourFont.deriveFont((float) 36);
                g.setFont(finalFont);
            } else if (size.equals("MEDIUM")) {
                Font finalFont = ourFont.deriveFont((float) 20);
                g.setFont(finalFont);
            } else {
                Font finalFont = ourFont.deriveFont((float) 20);
                g.setFont(finalFont);
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
            System.out.println("Switching to default font.");
        }
    }


    public void renderBlocks(Graphics g) {
        CurrentGameState gameState = logic.getCurrentGameState();
        int unitHeight = BOARD_HEIGHT / BOX_HEIGHT;
        int unitWidth = BOARD_WIDTH / BOX_WIDTH;
        Block[][] cBlocks = gameState.getBlocks();

        // This is the point where we add the current shape we are
        // previewing to the block list to see what's up
        for (Block[] row : gameState.getCurrentBlock().getRawBlockSet()) {
            for (Block block : row) {
                if (block != null) {
                    // Keep in mind position isn't affected by shii
                    int cX = block.getX();
                    int cY = block.getY();
                    if (cY >= 0) {
                        cBlocks[cY][cX] = block;
                    }
                }
            }
        }

        g.setColor(Color.WHITE);
        g.drawString("" + gameState.getScore(), 30, 40);
        g.setColor(Color.RED);
        g.drawLine(0, 50, BOARD_WIDTH, 50);
        // Loop through current tetris blocks and display them
        int xPos;
        int yPos = 50; // we drew the blocks above
        for (int row = 0; row < cBlocks.length; row++) {
            xPos = 0;
            for (int col = 0; col < cBlocks[row].length; col++) {
                if (cBlocks[row][col] != null) {
                    int[] cColor = cBlocks[row][col].getColor();
                    Color blockColor = new Color(
                            cColor[0], cColor[1], cColor[2]);
                    g.setColor(blockColor);
                } else {
                    g.setColor(Color.BLACK);
                }

                // Fill out some rectangles
                g.fillRect(xPos, yPos, unitWidth, unitHeight);
                xPos += unitWidth;
            }
            yPos += unitHeight;
        }
    }


    @Override
    public Dimension getPreferredSize() {
        // leave some space for the control panel
        return new Dimension(BOARD_WIDTH, BOARD_HEIGHT+50);
    }
}
