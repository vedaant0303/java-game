import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.*;
import java.util.List;

class GlassBridgeGame extends JFrame {
    private static final int ROWS = 2;
    private static final int COLS = 8;
    private JButton[][] glassBlocks = new JButton[ROWS][COLS];
    private boolean[][] bridge = new boolean[ROWS][COLS];
    private boolean[][] steppedOnCorrectBlock = new boolean[ROWS][COLS];
    private JPanel bridgePanel;
    private JLabel gameOverMessage;
    private JLabel characterTop, characterCenter, characterBottom;
    private JLabel[] heartIcons = new JLabel[3]; // Array for heart icons
    private int currentPlayerRow = 0;
    private int currentPlayerCol = -1;
    private int lifeCount = 3; // 3 lives for three characters
    private boolean gameOver = false;

    // Question Bank components
    private List<Map.Entry<String, String[]>> questionBankList;
    private String correctAnswer;
    private int currentQuestionIndex = 0;

    // Add reference to Try Again button and win condition check
    private JButton tryAgainButton;
    private boolean hasWon = false;

    public GlassBridgeGame() {
        setTitle("Glass Bridge Game");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Initialize the question bank
        initQuestionBank();

        // Show the initial start screen
        showStartScreen();
    }

    private void initQuestionBank() {
        Map<String, String[]> questionBank = new HashMap<>();
        // Sample questions...
        questionBank.put("What is the capital of France?", new String[]{"Paris", "London", "Rome", "Berlin"});
        questionBank.put("What is the largest planet in our solar system?", new String[]{"Jupiter", "Earth", "Mars", "Saturn"});
        questionBank.put("What is the square root of 64?", new String[]{"8", "6", "7", "9"});
        questionBank.put("Which element has the atomic number 1?", new String[]{"Hydrogen", "Helium", "Oxygen", "Carbon"});
        questionBank.put("Who wrote 'To Kill a Mockingbird'?", new String[]{"Harper Lee", "J.K. Rowling", "Ernest Hemingway", "F. Scott Fitzgerald"});
        questionBank.put("In which year did World War II end?", new String[]{"1945", "1939", "1940", "1944"});
        questionBank.put("What is the chemical symbol for gold?", new String[]{"Au", "Ag", "Fe", "Pb"});
        questionBank.put("Who painted the Mona Lisa?", new String[]{"Leonardo da Vinci", "Pablo Picasso", "Vincent van Gogh", "Michelangelo"});
        questionBank.put("Which planet is closest to the sun?", new String[]{"Mercury", "Venus", "Earth", "Mars"});
        questionBank.put("How many continents are there on Earth?", new String[]{"7", "6", "5", "8"});
        questionBank.put("What is the largest mammal in the world?", new String[]{"Blue Whale", "Elephant", "Giraffe", "Hippopotamus"});
        questionBank.put("Who developed the theory of relativity?", new String[]{"Albert Einstein", "Isaac Newton", "Galileo Galilei", "Marie Curie"});
        questionBank.put("What is the hardest natural substance on Earth?", new String[]{"Diamond", "Gold", "Iron", "Granite"});
        questionBank.put("Which gas do plants absorb from the atmosphere?", new String[]{"Carbon Dioxide", "Oxygen", "Nitrogen", "Hydrogen"});
        questionBank.put("Who discovered penicillin?", new String[]{"Alexander Fleming", "Marie Curie", "Isaac Newton", "Albert Einstein"});
        questionBank.put("What is the main ingredient in guacamole?", new String[]{"Avocado", "Tomato", "Onion", "Lime"});
        questionBank.put("Which country is known as the Land of the Rising Sun?", new String[]{"Japan", "China", "Korea", "Thailand"});
        questionBank.put("What is the currency of the United Kingdom?", new String[]{"Pound Sterling", "Euro", "Dollar", "Yen"});
        questionBank.put("What is the longest river in the world?", new String[]{"Nile", "Amazon", "Yangtze", "Mississippi"});
        questionBank.put("What is the capital of Australia?", new String[]{"Canberra", "Sydney", "Melbourne", "Brisbane"});

        // Convert the map to a list for shuffling
        questionBankList = new ArrayList<>(questionBank.entrySet());
    }

    private void shuffleQuestions() {
        Collections.shuffle(questionBankList);
        currentQuestionIndex = 0; // Reset question index after shuffling
    }

    private void showStartScreen() {
        JPanel startPanel = new JPanel(new GridBagLayout());
        startPanel.setBackground(Color.BLACK);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.insets = new Insets(20, 20, 20, 20);

        // Title Label (Top)
        JLabel titleLabel = new JLabel("<html><span style='color:red;font-size:36px;font-weight:bold;font-style:italic;'>Squid</span>"
                + " <span style='color:blue;font-size:36px;font-weight:bold;font-style:italic;'>Game</span><br>"
                + "<span style='color:green;font-size:24px;font-style:italic;'>Glass Bridge Game</span></html>", SwingConstants.CENTER);
        titleLabel.setOpaque(true);
        titleLabel.setBackground(Color.BLACK);
        startPanel.add(titleLabel, gbc);

        // Start Button (Centered)
        gbc.gridy++;
        JButton startButton = new JButton("Start");
        startButton.setFont(new Font("Arial", Font.BOLD, 24));
        startButton.setPreferredSize(new Dimension(150, 50));

        // Apply new styles to the button
        startButton.setForeground(Color.WHITE);
        startButton.setBackground(Color.MAGENTA);
        startButton.setOpaque(true);
        startButton.setBorder(BorderFactory.createEmptyBorder());
        startButton.setFocusPainted(false);
        startButton.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Making it oval-shaped
        startButton.setBorder(BorderFactory.createLineBorder(Color.MAGENTA, 1, true));

        // Hover effect
        startButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                startButton.setBackground(new Color(200, 150, 255));  // Light purple
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                startButton.setBackground(Color.MAGENTA);  // Original purple color
            }
        });

        startButton.addActionListener(e -> {
            remove(startPanel);
            showGameScreen();
        });
        startPanel.add(startButton, gbc);

        add(startPanel);
        revalidate();
        repaint();
    }

    private void showGameScreen() {
        setLayout(new BorderLayout());

        // Shuffle questions at the start of each game
        shuffleQuestions();

        // Create game title and panels for the actual game
        createTitle();
        createHeartPanel(); // Create the heart panel above the bridge
        createBridgePanel();
        createGameOverPanel();

        resetGame();
    }

    private void createTitle() {
        JPanel titlePanel = new JPanel(new GridBagLayout());
        titlePanel.setBackground(Color.BLACK);
        JLabel titleLabel = new JLabel("<html><span style='color:red;font-size:36px;font-weight:bold;font-style:italic;'>Squid</span>"
                + " <span style='color:blue;font-size:36px;font-weight:bold;font-style:italic;'>Game</span><br>"
                + "<span style='color:green;font-size:24px;font-style:italic;'>Glass Bridge Game</span></html>", SwingConstants.CENTER);
        titleLabel.setOpaque(true);
        titleLabel.setBackground(Color.BLACK);
        titlePanel.add(titleLabel);

        add(titlePanel, BorderLayout.NORTH);
    }

    // Create heart panel at the top, separate from the bridge grid
    private void createHeartPanel() {
        JPanel heartPanel = new JPanel();
        heartPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 20, 30));  // Added distance between hearts and blocks
        heartPanel.setBackground(Color.BLACK);

        // Load heart icons (assuming you have heart image files)
        for (int i = 0; i < 3; i++) {
            heartIcons[i] = new JLabel(new ImageIcon("heart.jpeg")); // Replace with actual image path
            heartPanel.add(heartIcons[i]);
        }

        // Add the heart panel at the top of the layout, leaving space from the bridge
        add(heartPanel, BorderLayout.NORTH);
    }

    private void createBridgePanel() {
        bridgePanel = new JPanel();
        bridgePanel.setLayout(new GridBagLayout());
        bridgePanel.setBackground(Color.BLACK);
        add(bridgePanel, BorderLayout.CENTER);

        createStartEndBlocks();
        createGlassBlocks();
    }

    private void createStartEndBlocks() {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;

        // Start Block (Left)
        JLabel startLabel = new JLabel("Start", SwingConstants.CENTER);
        startLabel.setOpaque(true);
        startLabel.setBackground(Color.RED);
        startLabel.setPreferredSize(new Dimension(60, 150));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridheight = 2;
        bridgePanel.add(startLabel, gbc);

        // Player represented by 3 characters
        characterTop = new JLabel(new ImageIcon("char1.jpeg")); // Replace with actual image path
        characterCenter = new JLabel(new ImageIcon("char2.jpeg")); // Replace with actual image path
        characterBottom = new JLabel(new ImageIcon("char3.jpeg")); // Replace with actual image path

        startLabel.setLayout(new GridLayout(3, 1));
        startLabel.add(characterTop);
        startLabel.add(characterCenter);
        startLabel.add(characterBottom);

        // End Block (Right)
        JLabel endLabel = new JLabel("End", SwingConstants.CENTER);
        endLabel.setOpaque(true);
        endLabel.setBackground(Color.RED);
        endLabel.setPreferredSize(new Dimension(60, 150));
        gbc.gridx = COLS + 1;
        bridgePanel.add(endLabel, gbc);
    }

    private void createGlassBlocks() {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;

        for (int row = 0; row < ROWS; row++) {
            for (int col = 0; col < COLS; col++) {
                JButton glassBlock = new JButton();
                glassBlock.setBackground(Color.CYAN);
                glassBlock.setOpaque(true);
                glassBlock.setPreferredSize(new Dimension(80, 80));
                glassBlock.addActionListener(new BlockClickListener(row, col));
                glassBlocks[row][col] = glassBlock;

                gbc.gridx = col + 1;
                gbc.gridy = row;
                bridgePanel.add(glassBlock, gbc);
            }
        }
    }

    private void createGameOverPanel() {
        // Game Over message panel
        JPanel gameOverPanel = new JPanel(new GridBagLayout());
        gameOverPanel.setBackground(Color.BLACK);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.insets = new Insets(20, 20, 20, 20); // Adds padding around components

        gameOverMessage = new JLabel("", SwingConstants.CENTER);
        gameOverMessage.setForeground(Color.WHITE);
        gameOverMessage.setFont(new Font("Arial", Font.BOLD, 24));
        gameOverMessage.setVisible(false);
        gameOverPanel.add(gameOverMessage, gbc);

        // Try Again Button
        gbc.gridy++;
        tryAgainButton = new JButton("Try Again"); // Reference the button here
        tryAgainButton.setFont(new Font("Arial", Font.BOLD, 20));
        tryAgainButton.setVisible(false); // Initially hidden, will show only after game ends
        tryAgainButton.addActionListener(e -> resetGame());
        gameOverPanel.add(tryAgainButton, gbc);

        add(gameOverPanel, BorderLayout.SOUTH);
    }

    private void resetGame() {
        gameOverMessage.setVisible(false);
        tryAgainButton.setVisible(false); // Hide Try Again button when game resets
        lifeCount = 3; // Reset lives
        gameOver = false;
        hasWon = false; // Reset win condition

        // Reset heart icons to be visible
        for (JLabel heartIcon : heartIcons) {
            heartIcon.setVisible(true);
        }

        Random random = new Random();

        // Reset glass blocks and remove all characters from them
        for (int row = 0; row < ROWS; row++) {
            for (int col = 0; col < COLS; col++) {
                bridge[row][col] = random.nextInt(100) < 70; // 70% safe blocks
                steppedOnCorrectBlock[row][col] = false;
                glassBlocks[row][col].setBackground(Color.BLUE);
                glassBlocks[row][col].setIcon(null);
                glassBlocks[row][col].removeAll();  // Remove characters from the glass blocks
                glassBlocks[row][col].setEnabled(true);
            }
        }

        // Reset player position to the start block for all three characters
        resetCharactersToStart();
    }

    private void resetCharactersToStart() {
        // Get the start block (leftmost block in the grid)
        JLabel startLabel = (JLabel) bridgePanel.getComponent(0);

        // Remove any remaining characters from the start block (in case they are still attached)
        startLabel.removeAll();

        // Re-add the characters in the correct top-center-bottom layout
        startLabel.setLayout(new GridLayout(3, 1)); // Top, center, bottom arrangement

        startLabel.add(characterTop);    // Top character
        startLabel.add(characterCenter); // Center character
        startLabel.add(characterBottom); // Bottom character

        // Revalidate and repaint the start label to reflect the changes
        startLabel.revalidate();
        startLabel.repaint();

        // Set initial positions back to the starting block
        currentPlayerRow = 0;
        currentPlayerCol = -1; // -1 indicates that the player starts outside the grid on the start block
    }

    private void movePlayerToBlock(int newRow, int newCol) {
        if (gameOver) return;

        // Remove player from current block
        if (currentPlayerCol >= 0 && currentPlayerCol < COLS) {
            glassBlocks[currentPlayerRow][currentPlayerCol].remove(getCurrentCharacter());
            glassBlocks[currentPlayerRow][currentPlayerCol].revalidate();
            glassBlocks[currentPlayerRow][currentPlayerCol].repaint();
        } else if (currentPlayerCol == -1) {
            JLabel startLabel = (JLabel) bridgePanel.getComponent(0);
            startLabel.remove(getCurrentCharacter());
            startLabel.revalidate();
            startLabel.repaint();
        }

        // Add player icon to the new block
        glassBlocks[newRow][newCol].add(getCurrentCharacter());
        glassBlocks[newRow][newCol].revalidate();
        glassBlocks[newRow][newCol].repaint();

        // Update player position
        currentPlayerRow = newRow;
        currentPlayerCol = newCol;
    }

    private JLabel getCurrentCharacter() {
        switch (lifeCount) {
            case 3:
                return characterTop;
            case 2:
                return characterCenter;
            case 1:
                return characterBottom;
            default:
                return null;
        }
    }

    private void handleCrackedBlock() {
        lifeCount--;
        updateHearts();

        if (lifeCount == 0) {
            showGameOverMessage("You lose, all lives lost!");
            gameOver = true;
        } else {
            JOptionPane.showMessageDialog(this, "Character cracked! You have " + lifeCount + " lives remaining.");
        }
    }

    // Update heart icons visibility based on lives remaining
    private void updateHearts() {
        for (int i = 0; i < heartIcons.length; i++) {
            heartIcons[i].setVisible(i < lifeCount); // Only show hearts that are "alive"
        }
    }

    private void showQuiz() {
        if (currentQuestionIndex >= questionBankList.size()) {
            currentQuestionIndex = 0; // Reset question index if out of bounds
        }

        Map.Entry<String, String[]> questionEntry = questionBankList.get(currentQuestionIndex++);
        String question = questionEntry.getKey();
        String[] options = questionEntry.getValue();
        correctAnswer = options[0]; // Assuming the first option is always correct

        // Shuffle options for display
        Random random = new Random();
        for (int i = 0; i < 4; i++) {
            int randIndex = random.nextInt(4);
            String temp = options[i];
            options[i] = options[randIndex];
            options[randIndex] = temp;
        }

        int result = JOptionPane.showOptionDialog(
                this,
                question,
                "Quiz Time!",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                options,
                options[0]
        );

        // Check if player is on the last block (win condition)
        if (options[result].equals(correctAnswer)) {
            JOptionPane.showMessageDialog(this, "Correct! You can move to the next block.");

            if (currentPlayerCol == COLS - 1) {  // If player is on the last column
                showGameOverMessage("Congratulations!! You win.");
                gameOver = true; // End the game after winning
            }
        } else {
            showGameOverMessage("You lose! The correct answer was: " + correctAnswer);
            gameOver = true;
        }
    }

    private class BlockClickListener implements ActionListener {
        private int row, col;

        public BlockClickListener(int row, int col) {
            this.row = row;
            this.col = col;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            if (gameOver || hasWon) {
                return; // Prevent further block selection if game over or won
            }

            if (Math.abs(currentPlayerRow - row) > 1 || Math.abs(currentPlayerCol - col) > 1) {
                JOptionPane.showMessageDialog(null, "You can only move to adjacent blocks!");
                return;
            }

            movePlayerToBlock(row, col);

            if (bridge[row][col]) {
                glassBlocks[row][col].setBackground(Color.GREEN);
                steppedOnCorrectBlock[row][col] = true;

                // Show quiz after a correct block
                showQuiz();

            } else {
                glassBlocks[row][col].setBackground(null);  // Remove background color to avoid blur
                glassBlocks[row][col].setIcon(new ImageIcon("crack.jpeg"));  // Show crack effect using crack image
                handleCrackedBlock();
            }

            glassBlocks[row][col].setEnabled(false);
        }
    }

    private void showGameOverMessage(String message) {
        gameOverMessage.setText(message);
        gameOverMessage.setVisible(true);

        // Show Try Again button
        tryAgainButton.setVisible(true); // Only show when the game ends
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            GlassBridgeGame game = new GlassBridgeGame();
            game.setVisible(true);
        });
    }
}
