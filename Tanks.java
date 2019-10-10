import javax.swing.*;
import java.awt.*;
import java.util.Arrays;
import java.util.Random;

public class Tanks extends JPanel {

    private final boolean COLORED_MODE = false;
    private final boolean IS_GRID = true;

    private final int MSG_MODE_LEVEL = 1;
        private final int MANDATORY_MODE = 0;
        private final int CRITICAL_MODE = 1;
        private final int INFO_MODE = 2;
        private final int DEBUG_MODE = 3;

    private final int BF_CELLS_VERTICAL = 10;
    private final int BF_CELLS_HORIZONTAL = 15;
    private final int BF_CELL_SIZE = 64;
    private final int BF_WIDTH = BF_CELLS_HORIZONTAL* BF_CELL_SIZE;
    private final int BF_HEIGHT = BF_CELLS_VERTICAL* BF_CELL_SIZE;

    private final boolean IS_TARGER_QUADRANT = true;
    private int[] targetQuadrant = {-1,-1};
    private int tankDirection = 1;
        private final int UP = 1;
        private final int DOWN = 2;
        private final int LEFT = 3;
        private final int RIGHT = 4;

    private int tankStep = 1; //1,2,4,8,16,32,64
    private int tankX = 0;
    private int tankY = 0;
    private int tankSpeed = 2;

    private int bulletX = -100;
    private int bulletY = -100;
    private int bulletSpeed = tankSpeed/2;

    private final String BRICK = "B";
    private final String BLANK = " ";

    private String[][] battleField;

    private void runTheGame() throws Exception {
        long startTime = System.currentTimeMillis();
        Random rnd = new Random();

        battleField = createRandomMap();
        if (MSG_MODE_LEVEL>=3) {
            printCurrentBattleField();
        }

        setTankToQuadrant(rnd.nextInt(BF_CELLS_VERTICAL) + 1,rnd.nextInt(BF_CELLS_HORIZONTAL) + 1);

        int screenPlay = rnd.nextInt(3);
        //int screenPlay = 2;

        if (screenPlay == 0) {
            printConsole(MANDATORY_MODE,"Сценарий случайной чистки");
            cleanRandom();
        } else if (screenPlay == 1) {
            printConsole(MANDATORY_MODE,"Сценарий запрограммированной чистки");
            clean();
        } else {
            screenPlay = rnd.nextInt(100);
            printConsole(MANDATORY_MODE,"Сценарий из " + screenPlay + " случайнных ходов");
            for (int i=1; i <= screenPlay; i++) {
                moveToQuadrant(rnd.nextInt(BF_CELLS_VERTICAL) + 1, rnd.nextInt(BF_CELLS_HORIZONTAL) + 1);
            }
        }
        printConsole(MANDATORY_MODE,String.format("Время выполнения программы: %d мсек",System.currentTimeMillis()-startTime));
    }

    private void printConsole(int debugLevel, String msg) {
        if (MSG_MODE_LEVEL>=debugLevel) {
            if (debugLevel == CRITICAL_MODE) {
                System.err.println(msg);
            } else {
                System.out.println(msg);
            }
        }
    }
    private String[][] createRandomMap() {
        String[][] newMap = new String[BF_CELLS_VERTICAL][BF_CELLS_HORIZONTAL];
        Random rnd = new Random();
        for (int i=0; i<newMap.length; i++) {
            for (int j=0; j<newMap[i].length;j++) {
                if (rnd.nextInt(2)==0) {
                    newMap[i][j] = BLANK;
                } else {
                    newMap[i][j] = BRICK;
                }
            }
        }
        return newMap;
    }

    private void setTankToQuadrant(int v, int h) {
        if (v<=0) {
            v = 1;
        } else if (v>BF_CELLS_VERTICAL) {
            v = BF_CELLS_VERTICAL;
        }
        if (h<=0) {
            h = 1;
        } else if (h>BF_CELLS_HORIZONTAL) {
            h = BF_CELLS_HORIZONTAL;
        }
        battleField[v-1][h-1] = BLANK;
        tankX = (h-1)*BF_CELL_SIZE;
        tankY = (v-1)*BF_CELL_SIZE;
        repaint();
        printConsole(INFO_MODE,"(setTankToQuadrant) v:h = " + v + ":" + h);
    }

    private void moveToQuadrant(int v, int h) throws Exception {
        printConsole(INFO_MODE,"(moveToQuadrant) v:h = " + v + ":" + h);
        int vTo, hTo;
        String crd = getQuadrantXY(v,h);
        vTo = Integer.parseInt(crd.substring(0,crd.indexOf("_")));
        hTo = Integer.parseInt(crd.substring((crd.indexOf("_")+1)));
        targetQuadrant = new int[]{v, h};
        int[] xyQ;
        while (tankX!=hTo) {
            if (tankX<hTo) {
                tankDirection=RIGHT;
                xyQ = getQuadrantArray(tankX + BF_CELL_SIZE, tankY + BF_CELL_SIZE - tankStep);
            } else {
                tankDirection=LEFT;
                xyQ = getQuadrantArray(tankX - tankStep, tankY);
            }
            if (!battleField[xyQ[0]][xyQ[1]].trim().isEmpty()) {
                fire();
            }
            if (!move(tankDirection)) {
                printConsole(CRITICAL_MODE,String.format("DIRECTION %d tankX %d -> hTo %d, tankY %d -> vTo %d",tankDirection,tankX,hTo,tankY,vTo));
            }
        }
        while (tankY!=vTo) {
            if (tankY<vTo) {
                tankDirection=DOWN;
                xyQ = getQuadrantArray(tankX + BF_CELL_SIZE - tankStep, tankY + BF_CELL_SIZE);
            } else {
                tankDirection=UP;
                xyQ = getQuadrantArray(tankX, tankY - tankStep);
            }
            if (!battleField[xyQ[0]][xyQ[1]].trim().isEmpty()) {
                fire();
            }
            if (!move(tankDirection)) {
                printConsole(CRITICAL_MODE,String.format("DIRECTION %d tankX %d -> hTo %d, tankY %d -> vTo %d",tankDirection,tankX,hTo,tankY,vTo));
            }
        }
    }

    private boolean haveTargets() {
        for (String[] line : battleField) {
            for (String ch : line) {
                if (ch.equals(BRICK)) {
                    return true;
                }
            }
        }
        return false;
    }

    private void cleanRandom() throws Exception {
        Random rnd = new Random();
        while (haveTargets()) {
            moveToQuadrant(rnd.nextInt(BF_CELLS_VERTICAL)+1,rnd.nextInt(BF_CELLS_HORIZONTAL)+1);
            cleanDirection(UP);
            cleanDirection(DOWN);
            cleanDirection(LEFT);
            cleanDirection(RIGHT);
        }
    }
    private void clean() throws Exception {
        int[] xyQ = getQuadrantArray(tankX,tankY);
        for (int i=0;i<BF_CELLS_VERTICAL && i<battleField.length;i++) {
            moveToQuadrant(i+1,xyQ[1]+1);
            cleanDirection(LEFT);
            cleanDirection(RIGHT);
        }
    }

    private void cleanDirection(int direction) throws Exception {
        int[] xyQ = getQuadrantArray(tankX,tankY);
        turn(direction);
        if (direction==UP) {
            for (int i=xyQ[0]-1;i>=0 && i<battleField.length && xyQ[1]<battleField[i].length;i--) {
                if (!battleField[i][xyQ[1]].trim().isEmpty()) {
                    printConsole(INFO_MODE,"(cleanDirection) UP. Target V:H = " + (i+1) + ":" + (xyQ[1]+1));
                    fire();
                }
            }
        } else if (direction==DOWN) {
            for (int i=xyQ[0]+1;i<BF_CELLS_VERTICAL && i<battleField.length && xyQ[1]<battleField[i].length;i++) {
                if (!battleField[i][xyQ[1]].trim().isEmpty()) {
                    printConsole(INFO_MODE,"(cleanDirection) DOWN. Target V:H = " + (i+1) + ":" + (xyQ[1]+1));
                    fire();
                }
            }
        } else if (direction==LEFT) {
            for (int i=xyQ[1]-1;i>=0 && xyQ[0]<battleField.length && i<battleField[xyQ[0]].length;i--) {
                if (!battleField[xyQ[0]][i].trim().isEmpty()) {
                    printConsole(INFO_MODE,"(cleanDirection) LEFT. Target V:H = " + (xyQ[0]+1) + ":" + (i+1));
                    fire();
                }
            }
        } else if (direction==RIGHT) {
            for (int i=xyQ[1]+1;i<BF_CELLS_HORIZONTAL && xyQ[0]<battleField.length && i<battleField[xyQ[0]].length;i++) {
                if (!battleField[xyQ[0]][i].trim().isEmpty()) {
                    printConsole(INFO_MODE,"(cleanDirection) RIGHT. Target V:H = " + (xyQ[0]+1) + ":" + (i+1));
                    fire();
                }
            }
        }
    }
    private void fire() throws Exception {
        if (!canMove(tankDirection)) {
            printConsole(CRITICAL_MODE,"[ILLEGAL FIRE] Direction: " + tankDirection + ", X: " + tankX + ", Y: " + tankY);
            return;
        }
        bulletX = tankX+BF_CELL_SIZE/2-BF_CELL_SIZE/16;
        bulletY = tankY+BF_CELL_SIZE/2-BF_CELL_SIZE/16;

        while (!processInterception() && bulletX>=0 && bulletX<BF_WIDTH && bulletY>=0 && bulletY<BF_HEIGHT) {
            if (tankDirection==UP) {
                printConsole(DEBUG_MODE,"[FIRE UP] Direction: " + tankDirection + ", X: " + bulletX + ", Y: " + bulletY);
                bulletY -= tankStep;
            } else if (tankDirection==DOWN) {
                printConsole(DEBUG_MODE,"[FIRE DOWN] Direction: " + tankDirection + ", X: " + bulletX + ", Y: " + bulletY);
                bulletY += tankStep;
            } else if (tankDirection==LEFT) {
                printConsole(DEBUG_MODE,"[FIRE LEFT] Direction: " + tankDirection + ", X: " + bulletX + ", Y: " + bulletY);
                bulletX -= tankStep;
            } else { //if (tankDirection==RIGHT) {
                printConsole(DEBUG_MODE,"[FIRE RIGHT] Direction: " + tankDirection + ", X: " + bulletX + ", Y: " + bulletY);
                bulletX += tankStep;
            }
            Thread.sleep(bulletSpeed);
            repaint();
        }
        bulletX = -100;
        bulletY = -100;
        repaint();
    }

    private void printCurrentBattleField() {
        for (String[] row : battleField) {
            System.out.println(Arrays.toString(row));
        }
    }

    private String getQuadrantXY(int v, int h) {
        return (v - 1) * BF_CELL_SIZE + "_" + (h - 1) * BF_CELL_SIZE;
    }
    private String getQuadrant(int x, int y) {
        return y / BF_CELL_SIZE + "_" + x / BF_CELL_SIZE;
    }
    private int[] getQuadrantArray(int x, int y) {
        return new int[] {y / BF_CELL_SIZE, x / BF_CELL_SIZE};
    }

    private boolean processInterception () {
        String q = getQuadrant(bulletX,bulletY);
        int y = Integer.parseInt(q.substring(0,q.indexOf("_")));
        int x = Integer.parseInt(q.substring(q.indexOf("_")+1));
        if (x>=0 && x<BF_CELLS_HORIZONTAL && y>=0 && y<BF_CELLS_VERTICAL) {
            if (!battleField[y][x].trim().isEmpty()) {
                battleField[y][x] = " ";
                printConsole(DEBUG_MODE,"[processInterception] Direction: " + tankDirection +
                        ", tX: " + tankX + ", tY: " + tankY +
                        ", bX: " + bulletX + ", bY: " + bulletY +
                        ", x: " + x + ", y: " + y );
                return true;
            }
        }
        return false;
    }

    private boolean canMove(int direction) {
        return !( (direction == 1 && tankY <= 0) ||
                  (direction == 2 && tankY >= (BF_HEIGHT - BF_CELL_SIZE)) ||
                  (direction == 3 && tankX <= 0) ||
                  (direction == 4 && tankX >= (BF_WIDTH - BF_CELL_SIZE))
                );
    }
    private boolean move(int direction) throws Exception {
        if (!canMove(direction)) {
            printConsole(CRITICAL_MODE,"[ILLEGAL MOVE] Direction: " + direction + ", X: " + tankX + ", Y: " + tankY);
            turn(direction);
            return false;
        }
        tankDirection = direction;
        int covered = 0;
        while (covered< BF_CELL_SIZE) {
            if (tankDirection==UP) { // up
                printConsole(DEBUG_MODE,"[MOVE UP] Direction: " + direction + ", X: " + tankX + ", Y: " + tankY);
                tankY -= tankStep;
            } else if (tankDirection==DOWN) { // down
                printConsole(DEBUG_MODE,"[MOVE DOWN] Direction: " + direction + ", X: " + tankX + ", Y: " + tankY);
                tankY += tankStep;
            } else if (tankDirection==LEFT) { // left
                printConsole(DEBUG_MODE,"[MOVE LEFT] Direction: " + direction + ", X: " + tankX + ", Y: " + tankY);
                tankX -= tankStep;
            } else if (tankDirection==RIGHT) { // right
                printConsole(DEBUG_MODE,"[MOVE RIGHT] Direction: " + direction + ", X: " + tankX + ", Y: " + tankY);
                tankX += tankStep;
            }
            covered+=tankStep;
            Thread.sleep(tankSpeed);
            repaint();
        }
        return true;
    }
    private void turn(int direction) {
        tankDirection = direction;
        repaint();
    }

    // Magic bellow. Do not worry about this now, you will understand everything in this course.
    // Please concentrate on your tasks only.

    public static void main(String[] args) throws Exception {
        Tanks bf = new Tanks();
        bf.runTheGame();
    }

    public Tanks() {
        JFrame frame = new JFrame("YOUR TANK SHOULD FIRE!!!");
        //frame.setLocation(750, 150);
        frame.setMinimumSize(new Dimension(BF_WIDTH+15, BF_HEIGHT + 40));
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.getContentPane().add(this);
        frame.pack();
        frame.setVisible(true);
    }

    @Override
    protected void paintComponent(Graphics g) {
        int gunWidth = BF_CELL_SIZE/8;
        int gunLength = BF_CELL_SIZE/2;
        int gunDelta = BF_CELL_SIZE/2-gunWidth/2;

        paintBF(g);
        paintBorders(g);

        g.setColor(new Color(156, 97, 0));
        g.fillRoundRect(tankX, tankY, BF_CELL_SIZE, BF_CELL_SIZE,BF_CELL_SIZE/2,BF_CELL_SIZE/2);

        g.setColor(new Color(0, 0, 0));
        g.fillOval(tankX + BF_CELL_SIZE/2-BF_CELL_SIZE/4,tankY + BF_CELL_SIZE/2-BF_CELL_SIZE/4,BF_CELL_SIZE/2,BF_CELL_SIZE/2);
        if (tankDirection == UP){
            g.fillRect(tankX + gunDelta, tankY, gunWidth, gunLength);
        } else if (tankDirection == DOWN) {
            g.fillRect(tankX + gunDelta, tankY + BF_CELL_SIZE/2, gunWidth, gunLength);
        } else if (tankDirection == LEFT) {
            g.fillRect(tankX, tankY + gunDelta, gunLength, gunWidth);
        } else {
            g.fillRect(tankX + BF_CELL_SIZE/2, tankY + gunDelta, gunLength, gunWidth);
        }

        g.setColor(new Color(255, 0, 0 ));
        g.fillRect(bulletX, bulletY, gunWidth, gunWidth);
    }

    private void paintBorders(Graphics g) {
        for (int j = 0; j < battleField.length && j<BF_CELLS_VERTICAL; j++) {
            for (int k = 0; k < battleField[j].length && k<BF_CELLS_HORIZONTAL; k++) {
                String coordinates = getQuadrantXY(j + 1, k + 1);
                int separator = coordinates.indexOf("_");
                int y = Integer.parseInt(coordinates.substring(0, separator));
                int x = Integer.parseInt(coordinates.substring(separator + 1));
                if (battleField[j][k].equals(BRICK)) {
                    g.setColor(new Color(0, 0, 255));
                    g.fillRect(x, y, BF_CELL_SIZE, BF_CELL_SIZE);

                    if (IS_GRID) {
                        g.setColor(new Color(0, 0, 0));
                        g.drawRect(x, y, BF_CELL_SIZE, BF_CELL_SIZE);
                    }
                }
                if (IS_TARGER_QUADRANT && targetQuadrant[0]-1==j && targetQuadrant[1]-1==k) {
                    g.setColor(new Color(255, 255, 0));
                    g.drawRect(x, y, BF_CELL_SIZE, BF_CELL_SIZE);
                }
            }
        }
    }

    private void paintBF(Graphics g) {
        super.paintComponent(g);

        int i = 0;
        Color cc;
        for (int v = 0; v < BF_CELLS_VERTICAL; v++) {
            for (int h = 0; h < BF_CELLS_HORIZONTAL; h++) {
                if (COLORED_MODE) {
                    if (i % 2 == 0) {
                        cc = new Color(252, 241, 177);
                    } else {
                        cc = new Color(233, 243, 255);
                    }
                } else {
                    cc = new Color(180, 180, 180);
                }
                i++;
                g.setColor(cc);
                g.fillRect(h * BF_CELL_SIZE, v * BF_CELL_SIZE, BF_CELL_SIZE, BF_CELL_SIZE);
            }
        }
    }
}