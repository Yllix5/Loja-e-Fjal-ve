import javax.swing.JFrame;
import javax.swing.JTextArea;
import javax.swing.JScrollPane;
import javax.swing.JPanel;
import java.awt.Graphics;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.*;
import java.util.*;

public class GamePanel extends JPanel implements Runnable {
    static final int SCREEN_WIDTH = 800;
    static final int SCREEN_HEIGHT = 600;
    static final int DELAY = 15;
    static final int WORD_GEN_DELAY = 1000; // Ndrrohet se sa shpejt fjalet vijn njera pas tjetres!
    static final String SCORE_FILE = "rezultati.txt"; // vendi ku ruhen rezultatet
    static final String WORDS_FILE = "fjalët.txt"; // vendi ku ngarkohen fjalët
    
    Thread gameThread;
    Player player;
    ArrayList<Word> words;
    boolean running = false;
    boolean inMenu = true;
    boolean gameOver = false;
    String userInput = "";
    int score = 0;
    List<Integer> topScores = new ArrayList<>();
    String menuSelection = "start"; // Current menu selection
    List<String> wordsList = new ArrayList<>();
    
    GamePanel() {
        this.setPreferredSize(new Dimension(SCREEN_WIDTH, SCREEN_HEIGHT));
        this.setBackground(Color.BLACK);
        this.setFocusable(true);
        this.addKeyListener(new MyKeyAdapter());
        loadScores();
        loadWords();
        startGame();
    }
    
    public void startGame() {
        player = new Player(SCREEN_WIDTH / 2, SCREEN_HEIGHT - 100);
        words = new ArrayList<>();
        running = true;
        gameThread = new Thread(this);
        gameThread.start();
    }

    public void restartGame() {
        score = 0;
        userInput = "";
        words.clear();
        startGame();
    }
    
    public void loadScores() {
        File scoreFile = new File(SCORE_FILE);
        if (!scoreFile.exists()) {
            try {
                scoreFile.createNewFile();
            } catch (IOException e) {
                System.out.println("ERROR, krijimi i file rezultatet.txt nuk mund të realizohet: " + e.getMessage());
            }
        }
        try (BufferedReader reader = new BufferedReader(new FileReader(SCORE_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                topScores.add(Integer.parseInt(line));
            }
        } catch (IOException e) {
            System.out.println("ERROR, nuk mund të lexohet File 'rezultatet.txt': " + e.getMessage());
        }
        Collections.sort(topScores, Collections.reverseOrder());
    }

    public void saveScore() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(SCORE_FILE, true))) {
            writer.write(String.valueOf(score));
            writer.newLine();
        } catch (IOException e) {
            System.out.println("ERROR, nuk mund të ngarkohen fjalët në File 'fjalët.txt': " + e.getMessage());
        }
    }
    
    public void loadWords() {
        File wordsFile = new File(WORDS_FILE);
        if (!wordsFile.exists()) {
            System.out.println("ERROR, File 'fjalët.txt' nuk u gjend!.");
            return;
        }
        try (BufferedReader reader = new BufferedReader(new FileReader(WORDS_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                wordsList.add(line.trim());
            }
        } catch (IOException e) {
            System.out.println("ERROR, fjalët nuk mund të lexohen nga File 'fjalët.txt': " + e.getMessage());
        }
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (inMenu) {
            drawMenu(g);
        } else if (gameOver) {
            drawGameOver(g);
        } else {
            draw(g);
        }
    }

    public void draw(Graphics g) {
        if (running) {
           player.draw(g);
            for (Word word : words) {
                word.move();
                word.draw(g);
            }
            g.setColor(Color.WHITE);
            g.setFont(new Font("Ink Free", Font.PLAIN, 24));
            g.drawString("Fjalët: " + userInput, 10, SCREEN_HEIGHT - 50);
            g.drawString("Rezultati: " + score, SCREEN_WIDTH - 150, 50);
        }
    }

    public void drawMenu(Graphics g) {
    g.setColor(Color.WHITE);
    g.setFont(new Font("Ink Free", Font.BOLD, 48));
    g.drawString("Mirsevjen ne lojen e shpejtësisë", 100, 200);
    g.setFont(new Font("Ink Free", Font.PLAIN, 24));
    g.drawString("Prek ENTER për të startuar!", 300, 300);
    
    // Shfaqi rezultatet më të mëdha
    g.setFont(new Font("Ink Free", Font.PLAIN, 20));
    g.drawString("Rezultati më i madh:", 340, 360);
    for (int i = 0; i < Math.min(topScores.size(), 4); i++) {
        g.drawString((i + 1) + ". " + topScores.get(i), 350, 390 + i * 30);
    }
}
    public void drawGameOver(Graphics g) {
        g.setColor(Color.WHITE);
        g.setFont(new Font("Ink Free", Font.BOLD, 48));
        g.drawString("Loja përfundoi!", 250, 200);
        g.setFont(new Font("Ink Free", Font.PLAIN, 24));
        g.drawString("Prek ENTER për restartim!", 290, 300);
    }

    public void checkCollisions() {
        for (int i = 0; i < words.size(); i++) {
            if (words.get(i).getWord().equals(userInput)) {
                words.remove(i);
                userInput = "";
                score += 10;
                break;
            }
        }
        for (Word word : words) {
            if (word.getY() >= SCREEN_HEIGHT) {
                gameOver = true;
                running = false;
                saveScore();
                loadScores();
                break;
            }
        }
    }

    public void generateNewWord() {
        Random random = new Random();
        int x = random.nextInt(SCREEN_WIDTH - 100);
        int speed = random.nextInt(4) + 1; // Ndrrohet shpejtesia e fjaleve
        String word = wordsList.get(random.nextInt(wordsList.size()));
        words.add(new Word(word, x, speed));
    }

    public void run() {
        long lastWordGenerationTime = System.currentTimeMillis();
        while (running) {
            if (!inMenu && !gameOver) {
                checkCollisions();
                repaint();
                
                // Gjeneron rregullisht fjalët
                if (System.currentTimeMillis() - lastWordGenerationTime > WORD_GEN_DELAY) {
                    generateNewWord();
                    lastWordGenerationTime = System.currentTimeMillis();
                }
            }
            try {
                Thread.sleep(DELAY);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

  public class MyKeyAdapter extends KeyAdapter {
    public void keyPressed(KeyEvent e) {
        if (inMenu) {
            if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                if (menuSelection.equals("start")) {
                    inMenu = false;
                }
              
            }
        } else if (gameOver && e.getKeyCode() == KeyEvent.VK_ENTER) {
            gameOver = false;
            restartGame();
        } else if (!inMenu && !gameOver) {
            char keyChar = e.getKeyChar();
            if (Character.isLetterOrDigit(keyChar)) {
                userInput += keyChar;
                if (userInput.length() > 20) { // Limit input length
                    userInput = userInput.substring(userInput.length() - 20);
                }
            } else if (e.getKeyCode() == KeyEvent.VK_BACK_SPACE) {
                if (userInput.length() > 0) {
                    userInput = userInput.substring(0, userInput.length() - 1);
                }
             }
         }
      }
   }
}
