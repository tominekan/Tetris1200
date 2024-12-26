import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;

public class RunTetris1200 implements Runnable {
    public final String savePath = Paths.get(
            System.getProperty("user.home"),
            "Tetris1200",
            "game_data.txt").toString();
    public final int screenSize = 300;
    private final String[] allGameModes = new String[]{
            "Normal",
            "Random Start",
            "Predictable"
    };
    private int currentGameIndex = 0;
    private TetrisLogic logic;


    public void run() {
        System.out.println(savePath);
        final JFrame frame = new JFrame("Tetris 1200");
        frame.setLocation(100, 100);

        // Set an app icon
        try {
            frame.setIconImage(ImageIO.read(getClass().getResource("/Tetris1200.png")));
        } catch (IOException e) {
            System.out.println("Cannot set app icon, switching to swing default");
        }


        // Add main shii
        final JLabel status = new JLabel("Setting up...");
        // Number of blocks;
        JPanel gameMenu = new JPanel();


        gameMenu.setLayout(new BorderLayout());


        // Handles content for what's going in the center of the list
        // Center contains loadGame button and different modes
        JPanel centerContent = new JPanel();
        centerContent.setLayout(new BoxLayout(centerContent, BoxLayout.Y_AXIS));
        int contentHeight = centerContent.getPreferredSize().height;
        centerContent.setBackground(Color.BLACK);
        // Add some space on the top
        centerContent.add(Box.createVerticalGlue());

        // Tetris game title (goes up top)
        JLabel gameTitle = new JLabel("Tetris1200", SwingConstants.CENTER);
        gameTitle.setFont(getFont(56));
        gameTitle.setForeground(Color.WHITE);
        gameTitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        gameMenu.setBackground(Color.BLACK);
        gameMenu.add(gameTitle, BorderLayout.NORTH);

        // Button to load saved games
        JButton loadGameButton = new JButton("Load Game");
        // I need to do all this extra BS
        // to get the background black and text white
        loadGameButton.setFont(getFont(24));
        loadGameButton.setBackground(Color.BLACK);
        loadGameButton.setOpaque(false);
        loadGameButton.setBorderPainted(false);
        loadGameButton.setFocusPainted(false);
        loadGameButton.setForeground(Color.WHITE);
        loadGameButton.addActionListener( e -> {
            // LOAD UP DAT SAVED GAME
            File nf = new File(savePath);

            // if the file does not exist
            // then we create a new game
            if (!nf.exists()) {
                this.logic = createNewGame();
            } else {
                this.logic = loadSavedGame();
            }

            TetrisBoard tetris1200 = new TetrisBoard(
                    status,
                    this.logic,
                    this.savePath,
                    this.screenSize
            );
            frame.remove(gameMenu);

            // Add Game Board
            frame.add(tetris1200, BorderLayout.CENTER);
            // Add Control Panel
            frame.add(
                    setUpControlPanel(logic, tetris1200),
                    BorderLayout.NORTH);

            // Update this bih
            tetris1200.setFocusable(true);
            tetris1200.requestFocusInWindow();
            frame.pack();
            frame.revalidate();
            frame.repaint();
        });

        centerContent.add(loadGameButton);
        makeBiggerOnHover(loadGameButton);
        loadGameButton.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Label to select new game
        JLabel newGameFont = new JLabel("New Game");
        newGameFont.setFont(getFont(24));
        newGameFont.setForeground(Color.WHITE);
        newGameFont.setAlignmentX(Component.CENTER_ALIGNMENT);

        // spacing between loadGame and new Game sections
        centerContent.add(Box.createVerticalGlue());
        centerContent.add(newGameFont);


        // Button to start new game
        JButton currentGame = new JButton(allGameModes[currentGameIndex]);
        currentGame.setFont(getFont(24));
        currentGame.setForeground(Color.WHITE);
        currentGame.setBackground(Color.BLACK);
        currentGame.setOpaque(false);
        currentGame.setFocusPainted(false);
        currentGame.setBorderPainted(false);
        currentGame.addActionListener( e -> {
            this.logic = createNewGame();

            TetrisBoard tetris1200 = new TetrisBoard(
                    status,
                    this.logic,
                    this.savePath,
                    this.screenSize
            );

            frame.remove(gameMenu);

            // Add Game Board
            frame.add(tetris1200, BorderLayout.CENTER);
            // Add Control Panel
            frame.add(
                    setUpControlPanel(logic, tetris1200),
                    BorderLayout.NORTH);

            // Update this bih
            tetris1200.setFocusable(true);
            tetris1200.requestFocusInWindow();
            frame.pack();
            frame.revalidate();
            frame.repaint();
        });
        centerContent.add(currentGame);
        // Add vertical glue to the bottom
        currentGame.setAlignmentX(Component.CENTER_ALIGNMENT);
        makeBiggerOnHover(currentGame);
        centerContent.add(Box.createVerticalGlue());
        gameMenu.add(centerContent, BorderLayout.CENTER);

        // Create the "goLeft" Button
        JButton leftButton = new JButton("Go Left");
        try {
            BufferedImage icon = ImageIO.read(getClass().getResource("/left_button.png"));
            leftButton = new JButton(new ImageIcon(icon));
        } catch (Exception e) {
            System.out.println("Switching Left Button to text.");
        }
        //leftButton.addActionListener(e -> tetris1200.resetGame());
        leftButton.setContentAreaFilled(false);
        leftButton.setBorder(BorderFactory.createEmptyBorder());
        leftButton.setFocusable(false);
        leftButton.addActionListener(e -> {
            switchGameMode(currentGame, -1);
        });
        gameMenu.add(leftButton, BorderLayout.WEST);


        // Create the "goRight" Button
        JButton rightButton = new JButton("Go Right");
        try {
            BufferedImage icon = ImageIO.read(getClass().getResource("/right_button.png"));
            rightButton = new JButton(new ImageIcon(icon));
        } catch (Exception e) {
            System.out.println("Switching Right Button to text.");
        }
        rightButton.setContentAreaFilled(false);
        rightButton.setBorder(BorderFactory.createEmptyBorder());
        rightButton.setFocusable(false);
        rightButton.addActionListener(e -> {
            switchGameMode(currentGame, 1);
        });
        gameMenu.add(rightButton, BorderLayout.EAST);

        frame.add(gameMenu);

        // Style the frame
        frame.setBackground(Color.BLACK);
        gameMenu.setPreferredSize(new Dimension(
                (int) (this.screenSize * 1.6),
                (int) (this.screenSize * 1.2)));

        // Pack this bih
        frame.pack();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }


    public TetrisLogic loadSavedGame() {
        File file = new File(savePath);
        if (file.exists()) {
            CurrentGameState gState = CurrentGameState.loadFile(this.savePath);
            if (gState.getGameType().equals("Normal")) {
                return new TetrisLogic(gState);
            } else if (gState.getGameType().equals("Random Start")) {
                return new RandomStart(gState);
            } else if (gState.getGameType().equals("Predictable")) {
                return new Predictable(gState);
            } else {
                return new TetrisLogic(gState);
            }
        } else {
            return null;
        }
    }

    public TetrisLogic createNewGame() {
        if (currentGameIndex == 0) {
            return new TetrisLogic(10, 24);
        } else if (currentGameIndex == 1) {
            return new RandomStart(10, 24);
        } else if (currentGameIndex == 2) {
            return new Predictable(10, 24);
        } else {
            return new TetrisLogic(10, 24);
        }
    }


    public JPanel setUpControlPanel(TetrisLogic logic, TetrisBoard tetris1200) {
        // Create and style the new control panel
        // we are returning this control panel
        JPanel controls = new JPanel();
        controls.setBackground(Color.BLACK);

        // Setup reset button
        JButton resetButton = new JButton("RESET");
        try {
            BufferedImage icon = ImageIO.read(getClass().getResource("/reset_button.png"));
            resetButton = new JButton(new ImageIcon(icon));
        } catch (Exception e) {
            System.out.println("Switching Reset Button to text.");
        }
        resetButton.addActionListener(e -> tetris1200.resetGame());
        resetButton.setContentAreaFilled(false);
        resetButton.setBorder(BorderFactory.createEmptyBorder());
        resetButton.setFocusable(false);
        controls.add(resetButton);

        // Set up save later button (honestly pretty useless)
        JButton saveButton = new JButton("SAVE");
        try {
            BufferedImage icon = ImageIO.read(getClass().getResource("/save_button.png"));
            saveButton = new JButton(new ImageIcon(icon));
        } catch (Exception e) {
            System.out.println("Switching Save Button to text.");
        }
        saveButton.addActionListener(e -> logic.getCurrentGameState().saveGameState(this.savePath));
        saveButton.setContentAreaFilled(false);
        saveButton.setBorder(BorderFactory.createEmptyBorder());
        saveButton.setFocusable(false);
        controls.add(saveButton);


        // Set up pause button
        JToggleButton pauseButton = new JToggleButton("PAUSE");
        try {
            BufferedImage icon = ImageIO.read(getClass().getResource("/pause_button.png"));
            pauseButton = new JToggleButton(new ImageIcon(icon));
        } catch (Exception e) {
            System.out.println("Switching Save Button to text.");
        }


        // Make the button switch icons PAUSE/PLAY
        try {
            BufferedImage icon = ImageIO.read(getClass().getResource("/play_button.png"));
            pauseButton.setSelectedIcon(new ImageIcon(icon));
        } catch (IOException e) {
            System.out.println("Just usuing regular text then");
        }

        // Make the button PAUSE/PLAY the game
        pauseButton.addItemListener(
                e -> {logic.togglePause();});
        pauseButton.setContentAreaFilled(false);
        pauseButton.setBorder(BorderFactory.createEmptyBorder());
        pauseButton.setFocusable(false);
        controls.add(pauseButton);

        JButton infoButton = new JButton("INFO");
        try {
            BufferedImage icon = ImageIO.read(getClass().getResource("/info_button.png"));
            infoButton = new JButton(new ImageIcon(icon));
        } catch (Exception e) {
            System.out.println("Switching Save Button to text.");
        }

        infoButton.addActionListener(e -> {
            logic.pauseGame();
            tetris1200.showInfoScreen();
        });

        infoButton.setContentAreaFilled(false);
        infoButton.setBorder(BorderFactory.createEmptyBorder());
        infoButton.setFocusable(false);
        controls.add(infoButton);

        return controls;
    }

    public Font getFont(int size) {
        try {
            //File fontFile = new File("./assets/gameFont/04B_30__.ttf");
            ClassLoader loader = getClass().getClassLoader();
            Font ourFont = Font.createFont(Font.TRUETYPE_FONT, loader.getResourceAsStream("04B_30__.ttf"));
            return ourFont.deriveFont((float) size);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            System.out.println("Switching to default font.");
            return new Font("sans-serif", Font.PLAIN, size);
        }

    }

    /**
     * Loops through all game modes
     * if direction is 1, then we go forward through the list of names
     * if the direction is -1, we go backwards through the list of names
     */
    public void switchGameMode(JButton currentMode, int direction) {
        // If tryna go right but we are at greatest element,
        // then go back to beginning
        if ((currentGameIndex == (allGameModes.length -1)) && (direction == 1)) {
            currentGameIndex = 0;

        } else if (currentGameIndex == 0 && direction == -1) {
            // if trying to go left, but we are at zeroth position
            currentGameIndex = allGameModes.length - 1;
        } else if (direction == 1) {
            currentGameIndex++;
        } else if (direction == -1) {
            currentGameIndex--;
        }
        currentMode.setText(allGameModes[currentGameIndex]);
    }


    /**
     * Modifier to make a button bigger upon hover
     * @param cButton to make bigger
     */
    public void makeBiggerOnHover(JButton cButton) {
        cButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                cButton.setSize(cButton.getWidth()+10,
                        cButton.getHeight()+10);
                cButton.setFont(getFont(30));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                cButton.setSize(cButton.getWidth()-10,
                        cButton.getHeight()-10);
                cButton.setFont(getFont(24));
            }
        });
    }
}