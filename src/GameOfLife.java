import java.awt.*;
import javax.swing.*;
import java.util.*;

public class GameOfLife {

    private final String NAME_OF_GAME = "Conway's Game Of Life";
    private final int START_LOCATION = 200;
    private final int LIFE_SIZE = 50;
    private final int POINT_RADIUS = 10;
    private final int FIELD_SIZE = LIFE_SIZE * POINT_RADIUS + 7;
    private final int BTN_PANEL_HEIGHT = 58;
    private boolean[][] lifeGeneration = new boolean[LIFE_SIZE][LIFE_SIZE];
    private boolean[][] nextGeneration = new boolean[LIFE_SIZE][LIFE_SIZE];
    private volatile boolean goNextGeneration = false;
    private final int showDelay = 200;

    private JFrame frame;
    private Canvas canvasPanel;
    private Random random = new Random();

    public static void main(String[] args) {
        new GameOfLife().go();
    }

    void go() {
        frame = new JFrame(NAME_OF_GAME);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(FIELD_SIZE, FIELD_SIZE + BTN_PANEL_HEIGHT);
        frame.setLocation(START_LOCATION, START_LOCATION);
        frame.setResizable(false);

        canvasPanel = new Canvas();
        canvasPanel.setBackground(Color.white);

        JButton fillButton = new JButton("Fill");
        fillButton.addActionListener(actionEvent -> {
            for(int x = 0; x < LIFE_SIZE; x++) {
                for(int y = 0; y < LIFE_SIZE; y++) {
                    lifeGeneration[x][y] = random.nextBoolean();
                }
            }
            canvasPanel.repaint();
        });

        JButton stepButton = new JButton("Step");
        stepButton.addActionListener(actionEvent -> {
            processOfLife();
            canvasPanel.repaint();
        });

        JButton playButton = new JButton("Play");
        playButton.addActionListener(actionEvent -> {
            goNextGeneration = !goNextGeneration;
            playButton.setText(goNextGeneration ? "Stop" : "Play");
        });

        JPanel btnPanel = new JPanel();
        btnPanel.add(fillButton);
        btnPanel.add(stepButton);
        btnPanel.add(playButton);

        frame.getContentPane().add(BorderLayout.CENTER, canvasPanel);
        frame.getContentPane().add(BorderLayout.SOUTH, btnPanel);
        frame.setVisible(true);

        while(true) {
            if (goNextGeneration) {
                processOfLife();
                canvasPanel.repaint();
                try {
                    Thread.sleep(showDelay);
                } catch (InterruptedException e) { e.printStackTrace(); }
            }
        }
    }

    int countNeighbors(int x, int y) {
        int count = 0;
        for(int dx = -1; dx < 2; dx++) {
            for(int dy = -1; dy < 2; dy++) {
                int nX = x + dx;
                int nY = y + dy;
                nX = (nX < 0) ? LIFE_SIZE - 1 : nX;
                nY = (nY < 0) ? LIFE_SIZE - 1 : nY;
                nX = (nX > LIFE_SIZE - 1) ? 0 : nX;
                nY = (nY > LIFE_SIZE - 1) ? 0 : nY;
                count += (lifeGeneration[nX][nY] ? 1 : 0);
            }
        }
        if(lifeGeneration[x][y]) { count--; }
        return count;
    }

    void processOfLife() {
        for(int x = 0; x < LIFE_SIZE; x++) {
            for(int y = 0; y < LIFE_SIZE; y++) {
                int count = countNeighbors(x, y);
                nextGeneration[x][y] = lifeGeneration[x][y];
                nextGeneration[x][y] = (count == 3) ? true : nextGeneration[x][y];
                nextGeneration[x][y] = ((count < 2) || (count > 3)) ? false : nextGeneration[x][y];
            }
        }
        for(int x = 0; x < LIFE_SIZE; x++) {
            System.arraycopy(nextGeneration[x], 0, lifeGeneration[x], 0, LIFE_SIZE);
        }
    }

    public class Canvas extends JPanel {
        @Override
        public void paint(Graphics g) {
            super.paint(g);
            for(int x = 0; x < LIFE_SIZE; x++) {
                for(int y = 0; y < LIFE_SIZE; y++) {
                    if (lifeGeneration[x][y]) {
                        g.fillOval(x*POINT_RADIUS, y*POINT_RADIUS, POINT_RADIUS, POINT_RADIUS);
                    }
                }
            }
        }
    }
}
