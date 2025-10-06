package game.collection;

import java.awt.AWTException;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.DisplayMode;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.image.BufferedImage;
import java.awt.image.BufferStrategy;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;

public class House3D extends GameCore {

    public static void main(String args[]) {
        new House3D().run();
    }

    protected PolygonRenderer polygonRenderer;
    protected ViewWindow viewWindow;
    protected List polygons;
    private boolean drawFrameRate = false;
    private boolean drawInstructions = true;
    private int numFrames;// for calculating frame rate
    private long startTime;
    private float frameRate;
    protected InputManager inputManager;
    private GameAction exit = new GameAction("exit");
    private GameAction smallerView = new GameAction("smallerView", GameAction.DETECT_INITAL_PRESS_ONLY);
    private GameAction largerView = new GameAction("largerView", GameAction.DETECT_INITAL_PRESS_ONLY);
    private GameAction frameRateToggle = new GameAction("frameRateToggle", GameAction.DETECT_INITAL_PRESS_ONLY);
    private GameAction goForward = new GameAction("goForward");
    private GameAction goBackward = new GameAction("goBackward");
    private GameAction goUp = new GameAction("goUp");
    private GameAction goDown = new GameAction("goDown");
    private GameAction goLeft = new GameAction("goLeft");
    private GameAction goRight = new GameAction("goRight");
    private GameAction turnLeft = new GameAction("turnLeft");
    private GameAction turnRight = new GameAction("turnRight");
    private GameAction tiltUp = new GameAction("tiltUp");
    private GameAction tiltDown = new GameAction("tiltDown");
    private GameAction tiltLeft = new GameAction("tiltLeft");
    private GameAction tiltRight = new GameAction("tiltRight");

    public void init(DisplayMode[] modes) {
        super.init(modes);

        inputManager = new InputManager(screen.getFullScreenWindow());
        inputManager.setRelativeMouseMode(true);
        inputManager.setCursor(InputManager.INVISIBLE_CURSOR);

        inputManager.mapToKey(exit, KeyEvent.VK_ESCAPE);
        inputManager.mapToKey(goForward, KeyEvent.VK_W);
        inputManager.mapToKey(goForward, KeyEvent.VK_UP);
        inputManager.mapToKey(goBackward, KeyEvent.VK_S);
        inputManager.mapToKey(goBackward, KeyEvent.VK_DOWN);
        inputManager.mapToKey(goLeft, KeyEvent.VK_A);
        inputManager.mapToKey(goLeft, KeyEvent.VK_LEFT);
        inputManager.mapToKey(goRight, KeyEvent.VK_D);
        inputManager.mapToKey(goRight, KeyEvent.VK_RIGHT);
        inputManager.mapToKey(goUp, KeyEvent.VK_PAGE_UP);
        inputManager.mapToKey(goDown, KeyEvent.VK_PAGE_DOWN);
        inputManager.mapToMouse(turnLeft, InputManager.MOUSE_MOVE_LEFT);
        inputManager.mapToMouse(turnRight, InputManager.MOUSE_MOVE_RIGHT);
        inputManager.mapToMouse(tiltUp, InputManager.MOUSE_MOVE_UP);
        inputManager.mapToMouse(tiltDown, InputManager.MOUSE_MOVE_DOWN);

        inputManager.mapToKey(tiltLeft, KeyEvent.VK_INSERT);
        inputManager.mapToKey(tiltRight, KeyEvent.VK_DELETE);

        inputManager.mapToKey(smallerView, KeyEvent.VK_SUBTRACT);
        inputManager.mapToKey(smallerView, KeyEvent.VK_MINUS);
        inputManager.mapToKey(largerView, KeyEvent.VK_ADD);
        inputManager.mapToKey(largerView, KeyEvent.VK_PLUS);
        inputManager.mapToKey(largerView, KeyEvent.VK_EQUALS);
        inputManager.mapToKey(frameRateToggle, KeyEvent.VK_R);

        // create the polygon renderer
        createPolygonRenderer();

        // create polygons
        polygons = new ArrayList();
        createPolygons();
    }

    public void createPolygons() {
        SolidPolygon3D poly;

        // walls
        poly = new SolidPolygon3D(new Vector3D(-200, 0, -1000), new Vector3D(200, 0, -1000), new Vector3D(200, 250, -1000), new Vector3D(-200, 250, -1000));

        poly.setColor(Color.WHITE);
        polygons.add(poly);
        poly = new SolidPolygon3D(new Vector3D(-200, 0, -1400), new Vector3D(-200, 250, -1400), new Vector3D(200, 250, -1400), new Vector3D(200, 0, -1400));

        poly.setColor(Color.WHITE);
        polygons.add(poly);
        poly = new SolidPolygon3D(new Vector3D(-200, 0, -1400), new Vector3D(-200, 0, -1000), new Vector3D(-200, 250, -1000), new Vector3D(-200, 250, -1400));

        poly.setColor(Color.GRAY);
        polygons.add(poly);
        poly = new SolidPolygon3D(new Vector3D(200, 0, -1000), new Vector3D(200, 0, -1400), new Vector3D(200, 250, -1400), new Vector3D(200, 250, -1000));

        poly.setColor(Color.GRAY);
        polygons.add(poly);

        // door and windows
        poly = new SolidPolygon3D(new Vector3D(0, 0, -1000), new Vector3D(75, 0, -1000), new Vector3D(75, 125, -1000), new Vector3D(0, 125, -1000));
        poly.setColor(new Color(0x660000));

        polygons.add(poly);
        poly = new SolidPolygon3D(new Vector3D(-150, 150, -1000), new Vector3D(-100, 150, -1000), new Vector3D(-100, 200, -1000), new Vector3D(-150, 200, -1000));

        poly.setColor(new Color(0x660000));
        polygons.add(poly);

        // roof
        poly = new SolidPolygon3D(new Vector3D(-200, 250, -1000), new Vector3D(200, 250, -1000), new Vector3D(75, 400, -1200), new Vector3D(-75, 400, -1200));
        poly.setColor(new Color(0x660000));
        polygons.add(poly);
        poly = new SolidPolygon3D(new Vector3D(-200, 250, -1400), new Vector3D(-200, 250, -1000), new Vector3D(-75, 400, -1200));
        poly.setColor(new Color(0x330000));
        polygons.add(poly);
        poly = new SolidPolygon3D(new Vector3D(200, 250, -1400), new Vector3D(-200, 250, -1400), new Vector3D(-75, 400, -1200), new Vector3D(75, 400, -1200));
        poly.setColor(new Color(0x660000));
        polygons.add(poly);
        poly = new SolidPolygon3D(new Vector3D(200, 250, -1000), new Vector3D(200, 250, -1400), new Vector3D(75, 400, -1200));
        poly.setColor(new Color(0x330000));
        polygons.add(poly);
    }

    public void createPolygonRenderer() {
        // make the view window the entire screen
        viewWindow = new ViewWindow(0, 0, screen.getWidth(),
                screen.getHeight(), (float) Math.toRadians(75));

        Transform3D camera = new Transform3D(0, 100, 0);
        polygonRenderer = new SolidPolygonRenderer(camera, viewWindow);
    }

    public void setViewBounds(int width, int height) {
        width = Math.min(width, screen.getWidth());
        height = Math.min(height, screen.getHeight());
        width = Math.max(64, width);
        height = Math.max(48, height);
        viewWindow.setBounds((screen.getWidth() - width) / 2, (screen.getHeight() - height) / 2, width, height);
    }

    public void update(long elapsedTime) {
        if (exit.isPressed()) {
            stop();
            return;
        }

        if (largerView.isPressed()) {
            setViewBounds(viewWindow.getWidth() + 64,
                    viewWindow.getHeight() + 48);
        } else if (smallerView.isPressed()) {
            setViewBounds(viewWindow.getWidth() - 64,
                    viewWindow.getHeight() - 48);
        }

        if (frameRateToggle.isPressed()) {
            drawFrameRate = !drawFrameRate;
        }

        // cap elapsedTime
        elapsedTime = Math.min(elapsedTime, 100);

        float angleChange = 0.0002f * elapsedTime;
        float distanceChange = .5f * elapsedTime;

        Transform3D camera = polygonRenderer.getCamera();
        Vector3D cameraLoc = camera.getLocation();

        // apply movement
        if (goForward.isPressed()) {
            cameraLoc.x -= distanceChange * camera.getSinAngleY();
            cameraLoc.z -= distanceChange * camera.getCosAngleY();
        }
        if (goBackward.isPressed()) {
            cameraLoc.x += distanceChange * camera.getSinAngleY();
            cameraLoc.z += distanceChange * camera.getCosAngleY();
        }
        if (goLeft.isPressed()) {
            cameraLoc.x -= distanceChange * camera.getCosAngleY();
            cameraLoc.z += distanceChange * camera.getSinAngleY();
        }
        if (goRight.isPressed()) {
            cameraLoc.x += distanceChange * camera.getCosAngleY();
            cameraLoc.z -= distanceChange * camera.getSinAngleY();
        }
        if (goUp.isPressed()) {
            cameraLoc.y += distanceChange;
        }
        if (goDown.isPressed()) {
            cameraLoc.y -= distanceChange;
        }

        // look up/down (rotate around x)
        int tilt = tiltUp.getAmount() - tiltDown.getAmount();
        tilt = Math.min(tilt, 200);
        tilt = Math.max(tilt, -200);

        // limit how far you can look up/down
        float newAngleX = camera.getAngleX() + tilt * angleChange;
        newAngleX = Math.max(newAngleX, (float) -Math.PI / 2);
        newAngleX = Math.min(newAngleX, (float) Math.PI / 2);
        camera.setAngleX(newAngleX);

        // turn (rotate around y)
        int turn = turnLeft.getAmount() - turnRight.getAmount();
        turn = Math.min(turn, 200);
        turn = Math.max(turn, -200);
        camera.rotateAngleY(turn * angleChange);

        // tilet head left/right (rotate around z)
        if (tiltLeft.isPressed()) {
            camera.rotateAngleZ(10 * angleChange);
        }
        if (tiltRight.isPressed()) {
            camera.rotateAngleZ(-10 * angleChange);
        }
    }

    public void draw(Graphics2D g) {
        // draw polygons
        polygonRenderer.startFrame(g);
        for (int i = 0; i < polygons.size(); i++) {
            polygonRenderer.draw(g, (Polygon3D) polygons.get(i));
        }
        polygonRenderer.endFrame(g);

        drawText(g);
    }

    public void drawText(Graphics g) {
        // draw text
        g.setColor(Color.WHITE);
        if (drawInstructions) {
            g.drawString("Use the mouse/arrow keys to move. " + "Press Esc to exit.", 5, fontSize);
        }

        // (you may have to turn off the BufferStrategy in
        // ScreenManager for more accurate tests)
        if (drawFrameRate) {
            calcFrameRate();
            g.drawString(frameRate + " frames/sec", 5, screen.getHeight() - 5);
        }
    }

    public void calcFrameRate() {
        numFrames++;
        long currTime = System.currentTimeMillis();

        // calculate the frame rate every 500 milliseconds
        if (currTime > startTime + 500) {
            frameRate = (float) numFrames * 1000 / (currTime - startTime);
            startTime = currTime;
            numFrames = 0;
        }
    }

}
//-----------------------------------------------------------------

abstract class GameCore {

    protected static final int DEFAULT_FONT_SIZE = 24;

    // various lists of modes, ordered by preference
    protected static final DisplayMode[] MID_RES_MODES = {
        new DisplayMode(800, 600, 16, 0), new DisplayMode(800, 600, 32, 0),
        new DisplayMode(800, 600, 24, 0), new DisplayMode(640, 480, 16, 0),
        new DisplayMode(640, 480, 32, 0), new DisplayMode(640, 480, 24, 0),
        new DisplayMode(1024, 768, 16, 0),
        new DisplayMode(1024, 768, 32, 0),
        new DisplayMode(1024, 768, 24, 0),};

    protected static final DisplayMode[] LOW_RES_MODES = {
        new DisplayMode(640, 480, 16, 0), new DisplayMode(640, 480, 32, 0),
        new DisplayMode(640, 480, 24, 0), new DisplayMode(800, 600, 16, 0),
        new DisplayMode(800, 600, 32, 0), new DisplayMode(800, 600, 24, 0),
        new DisplayMode(1024, 768, 16, 0),
        new DisplayMode(1024, 768, 32, 0),
        new DisplayMode(1024, 768, 24, 0),};

    protected static final DisplayMode[] VERY_LOW_RES_MODES = {
        new DisplayMode(320, 240, 16, 0), new DisplayMode(400, 300, 16, 0),
        new DisplayMode(512, 384, 16, 0), new DisplayMode(640, 480, 16, 0),
        new DisplayMode(800, 600, 16, 0),};

    private boolean isRunning;
    protected ScreenManager screen;
    protected int fontSize = DEFAULT_FONT_SIZE;

    public void stop() {
        isRunning = false;
    }

    public void run() {
        try {
            init();
            gameLoop();
        } finally {
            if (screen != null) {
                screen.restoreScreen();
            }
            lazilyExit();
        }
    }

    public void lazilyExit() {
        Thread thread = new Thread() {
            public void run() {
                // first, wait for the VM exit on its own.
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException ex) {
                }
                // system is still running, so force an exit
                System.exit(0);
            }
        };
        thread.setDaemon(true);
        thread.start();
    }

    public void init() {
        init(MID_RES_MODES);
    }

    public void init(DisplayMode[] possibleModes) {
        screen = new ScreenManager();
        DisplayMode displayMode = screen.findFirstCompatibleMode(possibleModes);
        screen.setFullScreen(displayMode);

        Window window = screen.getFullScreenWindow();
        window.setFont(new Font("Dialog", Font.PLAIN, fontSize));
        window.setBackground(Color.blue);
        window.setForeground(Color.white);

        isRunning = true;
    }

    public Image loadImage(String fileName) {
        return new ImageIcon(fileName).getImage();
    }

    public void gameLoop() {
        long startTime = System.currentTimeMillis();
        long currTime = startTime;

        while (isRunning) {
            long elapsedTime = System.currentTimeMillis() - currTime;
            currTime += elapsedTime;

            // update
            update(elapsedTime);

            // draw the screen
            Graphics2D g = screen.getGraphics();
            draw(g);
            g.dispose();
            screen.update();

        }
    }

    public void update(long elapsedTime) {
        // do nothing
    }

    public abstract void draw(Graphics2D g);
}
//-----------------------------------------------------------------

abstract class PolygonRenderer {

    protected ScanConverter scanConverter;
    protected Transform3D camera;
    protected ViewWindow viewWindow;
    protected boolean clearViewEveryFrame;
    protected Polygon3D sourcePolygon;
    protected Polygon3D destPolygon;

    public PolygonRenderer(Transform3D camera, ViewWindow viewWindow) {
        this(camera, viewWindow, true);
    }

    public PolygonRenderer(Transform3D camera, ViewWindow viewWindow, boolean clearViewEveryFrame) {
        this.camera = camera;
        this.viewWindow = viewWindow;
        this.clearViewEveryFrame = clearViewEveryFrame;
        init();
    }

    protected void init() {
        destPolygon = new Polygon3D();
        scanConverter = new ScanConverter(viewWindow);
    }

    public Transform3D getCamera() {
        return camera;
    }

    public void startFrame(Graphics2D g) {
        if (clearViewEveryFrame) {
            g.setColor(Color.black);
            g.fillRect(viewWindow.getLeftOffset(), viewWindow.getTopOffset(),
                    viewWindow.getWidth(), viewWindow.getHeight());
        }
    }

    public void endFrame(Graphics2D g) {
        // do nothing, for now.
    }

    public boolean draw(Graphics2D g, Polygon3D poly) {
        if (poly.isFacing(camera.getLocation())) {
            sourcePolygon = poly;
            destPolygon.setTo(poly);
            destPolygon.subtract(camera);
            boolean visible = destPolygon.clip(-1);
            if (visible) {
                destPolygon.project(viewWindow);
                visible = scanConverter.convert(destPolygon);
                if (visible) {
                    drawCurrentPolygon(g);
                    return true;
                }
            }
        }
        return false;
    }

    protected abstract void drawCurrentPolygon(Graphics2D g);
}
//-----------------------------------------------------------------

class SolidPolygonRenderer extends PolygonRenderer {

    public SolidPolygonRenderer(Transform3D camera, ViewWindow viewWindow) {
        this(camera, viewWindow, true);
    }

    public SolidPolygonRenderer(Transform3D camera, ViewWindow viewWindow, boolean clearViewEveryFrame) {
        super(camera, viewWindow, clearViewEveryFrame);
    }

    protected void drawCurrentPolygon(Graphics2D g) {
        // set the color
        if (sourcePolygon instanceof SolidPolygon3D) {
            g.setColor(((SolidPolygon3D) sourcePolygon).getColor());
        } else {
            g.setColor(Color.GREEN);
        }

        // draw the scans
        int y = scanConverter.getTopBoundary();
        while (y <= scanConverter.getBottomBoundary()) {
            ScanConverter.Scan scan = scanConverter.getScan(y);
            if (scan.isValid()) {
                g.drawLine(scan.left, y, scan.right, y);
            }
            y++;
        }
    }
}
//-----------------------------------------------------------------

class ScanConverter {

    private static final int SCALE_BITS = 16;
    private static final int SCALE = 1 << SCALE_BITS;
    private static final int SCALE_MASK = SCALE - 1;
    protected ViewWindow view;
    protected Scan[] scans;
    protected int top;
    protected int bottom;

    public static class Scan {

        public int left;
        public int right;

        public void setBoundary(int x) {
            if (x < left) {
                left = x;
            }
            if (x - 1 > right) {
                right = x - 1;
            }
        }

        public void clear() {
            left = Integer.MAX_VALUE;
            right = Integer.MIN_VALUE;
        }

        public boolean isValid() {
            return (left <= right);
        }

        public void setTo(int left, int right) {
            this.left = left;
            this.right = right;
        }

        public boolean equals(int left, int right) {
            return (this.left == left && this.right == right);
        }
    }

    public ScanConverter(ViewWindow view) {
        this.view = view;
    }

    public int getTopBoundary() {
        return top;
    }

    public int getBottomBoundary() {
        return bottom;
    }

    public Scan getScan(int y) {
        return scans[y];
    }

    protected void ensureCapacity() {
        int height = view.getTopOffset() + view.getHeight();
        if (scans == null || scans.length != height) {
            scans = new Scan[height];
            for (int i = 0; i < height; i++) {
                scans[i] = new Scan();
            }
            // set top and bottom so clearCurrentScan clears all
            top = 0;
            bottom = height - 1;
        }

    }

    private void clearCurrentScan() {
        for (int i = top; i <= bottom; i++) {
            scans[i].clear();
        }
        top = Integer.MAX_VALUE;
        bottom = Integer.MIN_VALUE;
    }

    public boolean convert(Polygon3D polygon) {

        ensureCapacity();
        clearCurrentScan();

        int minX = view.getLeftOffset();
        int maxX = view.getLeftOffset() + view.getWidth() - 1;
        int minY = view.getTopOffset();
        int maxY = view.getTopOffset() + view.getHeight() - 1;

        int numVertices = polygon.getNumVertices();
        for (int i = 0; i < numVertices; i++) {
            Vector3D v1 = polygon.getVertex(i);
            Vector3D v2;
            if (i == numVertices - 1) {
                v2 = polygon.getVertex(0);
            } else {
                v2 = polygon.getVertex(i + 1);
            }

            // ensure v1.y < v2.y
            if (v1.y > v2.y) {
                Vector3D temp = v1;
                v1 = v2;
                v2 = temp;
            }
            float dy = v2.y - v1.y;

            // ignore horizontal lines
            if (dy == 0) {
                continue;
            }

            int startY = Math.max(MoreMath.ceil(v1.y), minY);
            int endY = Math.min(MoreMath.ceil(v2.y) - 1, maxY);
            top = Math.min(top, startY);
            bottom = Math.max(bottom, endY);
            float dx = v2.x - v1.x;

            // special case: vertical line
            if (dx == 0) {
                int x = MoreMath.ceil(v1.x);
                // ensure x within view bounds
                x = Math.min(maxX + 1, Math.max(x, minX));
                for (int y = startY; y <= endY; y++) {
                    scans[y].setBoundary(x);
                }
            } else {
                // scan-convert this edge (line equation)
                float gradient = dx / dy;

                // (faster version)
                // trim start of line
                float startX = v1.x + (startY - v1.y) * gradient;
                if (startX < minX) {
                    int yInt = (int) (v1.y + (minX - v1.x) / gradient);
                    yInt = Math.min(yInt, endY);
                    while (startY <= yInt) {
                        scans[startY].setBoundary(minX);
                        startY++;
                    }
                } else if (startX > maxX) {
                    int yInt = (int) (v1.y + (maxX - v1.x) / gradient);
                    yInt = Math.min(yInt, endY);
                    while (startY <= yInt) {
                        scans[startY].setBoundary(maxX + 1);
                        startY++;
                    }
                }

                if (startY > endY) {
                    continue;
                }

                // trim back of line
                float endX = v1.x + (endY - v1.y) * gradient;
                if (endX < minX) {
                    int yInt = MoreMath.ceil(v1.y + (minX - v1.x) / gradient);
                    yInt = Math.max(yInt, startY);
                    while (endY >= yInt) {
                        scans[endY].setBoundary(minX);
                        endY--;
                    }
                } else if (endX > maxX) {
                    int yInt = MoreMath.ceil(v1.y + (maxX - v1.x) / gradient);
                    yInt = Math.max(yInt, startY);
                    while (endY >= yInt) {
                        scans[endY].setBoundary(maxX + 1);
                        endY--;
                    }
                }

                if (startY > endY) {
                    continue;
                }

                // line equation using integers
                int xScaled = (int) (SCALE * v1.x + SCALE * (startY - v1.y)
                        * dx / dy)
                        + SCALE_MASK;
                int dxScaled = (int) (dx * SCALE / dy);

                for (int y = startY; y <= endY; y++) {
                    scans[y].setBoundary(xScaled >> SCALE_BITS);
                    xScaled += dxScaled;
                }
            }
        }

        // check if visible (any valid scans)
        for (int i = top; i <= bottom; i++) {
            if (scans[i].isValid()) {
                return true;
            }
        }
        return false;
    }
}
//-----------------------------------------------------------------

class MoreMath {

    /**
     * Returns the sign of the number. Returns -1 for negative, 1 for positive,
     * and 0 otherwise.
     */
    public static int sign(short v) {
        return (v > 0) ? 1 : (v < 0) ? -1 : 0;
    }

    /**
     * Returns the sign of the number. Returns -1 for negative, 1 for positive,
     * and 0 otherwise.
     */
    public static int sign(int v) {
        return (v > 0) ? 1 : (v < 0) ? -1 : 0;
    }

    /**
     * Returns the sign of the number. Returns -1 for negative, 1 for positive,
     * and 0 otherwise.
     */
    public static int sign(long v) {
        return (v > 0) ? 1 : (v < 0) ? -1 : 0;
    }

    /**
     * Returns the sign of the number. Returns -1 for negative, 1 for positive,
     * and 0 otherwise.
     */
    public static int sign(float v) {
        return (v > 0) ? 1 : (v < 0) ? -1 : 0;
    }

    /**
     * Returns the sign of the number. Returns -1 for negative, 1 for positive,
     * and 0 otherwise.
     */
    public static int sign(double v) {
        return (v > 0) ? 1 : (v < 0) ? -1 : 0;
    }

    /**
     * Faster ceil function to convert a float to an int. Contrary to the
     * java.lang.Math ceil function, this function takes a float as an argument,
     * returns an int instead of a double, and does not consider special cases.
     */
    public static int ceil(float f) {
        if (f > 0) {
            return (int) f + 1;
        } else {
            return (int) f;
        }
    }

    /**
     * Faster floor function to convert a float to an int. Contrary to the
     * java.lang.Math floor function, this function takes a float as an
     * argument, returns an int instead of a double, and does not consider
     * special cases.
     */
    public static int floor(float f) {
        if (f >= 0) {
            return (int) f;
        } else {
            return (int) f - 1;
        }
    }

    /**
     * Returns true if the specified number is a power of 2.
     */
    public static boolean isPowerOfTwo(int n) {
        return ((n & (n - 1)) == 0);
    }

    /**
     * Gets the number of "on" bits in an integer.
     */
    public static int getBitCount(int n) {
        int count = 0;
        while (n > 0) {
            count += (n & 1);
            n >>= 1;
        }
        return count;
    }
}
//-----------------------------------------------------------------

/**
 * The GameAction class is an abstract to a user-initiated action, like jumping
 * or moving. GameActions can be mapped to keys or the mouse with the
 * InputManager.
 */
class GameAction {

    /**
     * Normal behavior. The isPressed() method returns true as long as the key
     * is held down.
     */
    public static final int NORMAL = 0;

    /**
     * Initial press behavior. The isPressed() method returns true only after
     * the key is first pressed, and not again until the key is released and
     * pressed again.
     */
    public static final int DETECT_INITAL_PRESS_ONLY = 1;

    private static final int STATE_RELEASED = 0;

    private static final int STATE_PRESSED = 1;

    private static final int STATE_WAITING_FOR_RELEASE = 2;

    private String name;

    private int behavior;

    private int amount;

    private int state;

    /**
     * Create a new GameAction with the NORMAL behavior.
     */
    public GameAction(String name) {
        this(name, NORMAL);
    }

    /**
     * Create a new GameAction with the specified behavior.
     */
    public GameAction(String name, int behavior) {
        this.name = name;
        this.behavior = behavior;
        reset();
    }

    /**
     * Gets the name of this GameAction.
     */
    public String getName() {
        return name;
    }

    /**
     * Resets this GameAction so that it appears like it hasn't been pressed.
     */
    public void reset() {
        state = STATE_RELEASED;
        amount = 0;
    }

    /**
     * Taps this GameAction. Same as calling press() followed by release().
     */
    public synchronized void tap() {
        press();
        release();
    }

    /**
     * Signals that the key was pressed.
     */
    public synchronized void press() {
        press(1);
    }

    /**
     * Signals that the key was pressed a specified number of times, or that the
     * mouse move a spcified distance.
     */
    public synchronized void press(int amount) {
        if (state != STATE_WAITING_FOR_RELEASE) {
            this.amount += amount;
            state = STATE_PRESSED;
        }

    }

    /**
     * Signals that the key was released
     */
    public synchronized void release() {
        state = STATE_RELEASED;
    }

    /**
     * Returns whether the key was pressed or not since last checked.
     */
    public synchronized boolean isPressed() {
        return (getAmount() != 0);
    }

    /**
     * For keys, this is the number of times the key was pressed since it was
     * last checked. For mouse movement, this is the distance moved.
     */
    public synchronized int getAmount() {
        int retVal = amount;
        if (retVal != 0) {
            if (state == STATE_RELEASED) {
                amount = 0;
            } else if (behavior == DETECT_INITAL_PRESS_ONLY) {
                state = STATE_WAITING_FOR_RELEASE;
                amount = 0;
            }
        }
        return retVal;
    }
}
//-----------------------------------------------------------------

/**
 * The Vector3D class implements a 3D vector with the floating-point values x,
 * y, and z. Vectors can be thought of either as a (x,y,z) point or as a vector
 * from (0,0,0) to (x,y,z).
 */
class Vector3D implements Transformable {

    public float x;

    public float y;

    public float z;

    /**
     * Creates a new Vector3D at (0,0,0).
     */
    public Vector3D() {
        this(0, 0, 0);
    }

    /**
     * Creates a new Vector3D with the same values as the specified Vector3D.
     */
    public Vector3D(Vector3D v) {
        this(v.x, v.y, v.z);
    }

    /**
     * Creates a new Vector3D with the specified (x, y, z) values.
     */
    public Vector3D(float x, float y, float z) {
        setTo(x, y, z);
    }

    /**
     * Checks if this Vector3D is equal to the specified Object. They are equal
     * only if the specified Object is a Vector3D and the two Vector3D's x, y,
     * and z coordinates are equal.
     */
    public boolean equals(Object obj) {
        Vector3D v = (Vector3D) obj;
        return (v.x == x && v.y == y && v.z == z);
    }

    /**
     * Checks if this Vector3D is equal to the specified x, y, and z
     * coordinates.
     */
    public boolean equals(float x, float y, float z) {
        return (this.x == x && this.y == y && this.z == z);
    }

    /**
     * Sets the vector to the same values as the specified Vector3D.
     */
    public void setTo(Vector3D v) {
        setTo(v.x, v.y, v.z);
    }

    /**
     * Sets this vector to the specified (x, y, z) values.
     */
    public void setTo(float x, float y, float z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    /**
     * Adds the specified (x, y, z) values to this vector.
     */
    public void add(float x, float y, float z) {
        this.x += x;
        this.y += y;
        this.z += z;
    }

    /**
     * Subtracts the specified (x, y, z) values to this vector.
     */
    public void subtract(float x, float y, float z) {
        add(-x, -y, -z);
    }

    /**
     * Adds the specified vector to this vector.
     */
    public void add(Vector3D v) {
        add(v.x, v.y, v.z);
    }

    /**
     * Subtracts the specified vector from this vector.
     */
    public void subtract(Vector3D v) {
        add(-v.x, -v.y, -v.z);
    }

    /**
     * Multiplies this vector by the specified value. The new length of this
     * vector will be length()*s.
     */
    public void multiply(float s) {
        x *= s;
        y *= s;
        z *= s;
    }

    /**
     * Divides this vector by the specified value. The new length of this vector
     * will be length()/s.
     */
    public void divide(float s) {
        x /= s;
        y /= s;
        z /= s;
    }

    /**
     * Returns the length of this vector as a float.
     */
    public float length() {
        return (float) Math.sqrt(x * x + y * y + z * z);
    }

    /**
     * Converts this Vector3D to a unit vector, or in other words, a vector of
     * length 1. Same as calling v.divide(v.length()).
     */
    public void normalize() {
        divide(length());
    }

    /**
     * Converts this Vector3D to a String representation.
     */
    public String toString() {
        return "(" + x + ", " + y + ", " + z + ")";
    }

    /**
     * Rotate this vector around the x axis the specified amount. The specified
     * angle is in radians. Use Math.toRadians() to convert from degrees to
     * radians.
     */
    public void rotateX(float angle) {
        rotateX((float) Math.cos(angle), (float) Math.sin(angle));
    }

    /**
     * Rotate this vector around the y axis the specified amount. The specified
     * angle is in radians. Use Math.toRadians() to convert from degrees to
     * radians.
     */
    public void rotateY(float angle) {
        rotateY((float) Math.cos(angle), (float) Math.sin(angle));
    }

    /**
     * Rotate this vector around the z axis the specified amount. The specified
     * angle is in radians. Use Math.toRadians() to convert from degrees to
     * radians.
     */
    public void rotateZ(float angle) {
        rotateZ((float) Math.cos(angle), (float) Math.sin(angle));
    }

    /**
     * Rotate this vector around the x axis the specified amount, using
     * pre-computed cosine and sine values of the angle to rotate.
     */
    public void rotateX(float cosAngle, float sinAngle) {
        float newY = y * cosAngle - z * sinAngle;
        float newZ = y * sinAngle + z * cosAngle;
        y = newY;
        z = newZ;
    }

    /**
     * Rotate this vector around the y axis the specified amount, using
     * pre-computed cosine and sine values of the angle to rotate.
     */
    public void rotateY(float cosAngle, float sinAngle) {
        float newX = z * sinAngle + x * cosAngle;
        float newZ = z * cosAngle - x * sinAngle;
        x = newX;
        z = newZ;
    }

    /**
     * Rotate this vector around the y axis the specified amount, using
     * pre-computed cosine and sine values of the angle to rotate.
     */
    public void rotateZ(float cosAngle, float sinAngle) {
        float newX = x * cosAngle - y * sinAngle;
        float newY = x * sinAngle + y * cosAngle;
        x = newX;
        y = newY;
    }

    /**
     * Adds the specified transform to this vector. This vector is first
     * rotated, then translated.
     */
    public void add(Transform3D xform) {

        // rotate
        addRotation(xform);

        // translate
        add(xform.getLocation());
    }

    /**
     * Subtracts the specified transform to this vector. This vector translated,
     * then rotated.
     */
    public void subtract(Transform3D xform) {

        // translate
        subtract(xform.getLocation());

        // rotate
        subtractRotation(xform);
    }

    /**
     * Rotates this vector with the angle of the specified transform.
     */
    public void addRotation(Transform3D xform) {
        rotateX(xform.getCosAngleX(), xform.getSinAngleX());
        rotateZ(xform.getCosAngleZ(), xform.getSinAngleZ());
        rotateY(xform.getCosAngleY(), xform.getSinAngleY());
    }

    /**
     * Rotates this vector with the opposite angle of the specified transform.
     */
    public void subtractRotation(Transform3D xform) {
        // note that sin(-x) == -sin(x) and cos(-x) == cos(x)
        rotateY(xform.getCosAngleY(), -xform.getSinAngleY());
        rotateZ(xform.getCosAngleZ(), -xform.getSinAngleZ());
        rotateX(xform.getCosAngleX(), -xform.getSinAngleX());
    }

    /**
     * Returns the dot product of this vector and the specified vector.
     */
    public float getDotProduct(Vector3D v) {
        return x * v.x + y * v.y + z * v.z;
    }

    /**
     * Sets this vector to the cross product of the two specified vectors.
     * Either of the specified vectors can be this vector.
     */
    public void setToCrossProduct(Vector3D u, Vector3D v) {
        // assign to local vars first in case u or v is 'this'
        float x = u.y * v.z - u.z * v.y;
        float y = u.z * v.x - u.x * v.z;
        float z = u.x * v.y - u.y * v.x;
        this.x = x;
        this.y = y;
        this.z = z;
    }

}
//-----------------------------------------------------------------

/**
 * The Polygon3D class represents a polygon as a series of vertices.
 */
class Polygon3D implements Transformable {

    // temporary vectors used for calculation
    private static Vector3D temp1 = new Vector3D();

    private static Vector3D temp2 = new Vector3D();

    private Vector3D[] v;

    private int numVertices;

    private Vector3D normal;

    /**
     * Creates an empty polygon that can be used as a "scratch" polygon for
     * transforms, projections, etc.
     */
    public Polygon3D() {
        numVertices = 0;
        v = new Vector3D[0];
        normal = new Vector3D();
    }

    /**
     * Creates a new Polygon3D with the specified vertices.
     */
    public Polygon3D(Vector3D v0, Vector3D v1, Vector3D v2) {
        this(new Vector3D[]{v0, v1, v2});
    }

    /**
     * Creates a new Polygon3D with the specified vertices. All the vertices are
     * assumed to be in the same plane.
     */
    public Polygon3D(Vector3D v0, Vector3D v1, Vector3D v2, Vector3D v3) {
        this(new Vector3D[]{v0, v1, v2, v3});
    }

    /**
     * Creates a new Polygon3D with the specified vertices. All the vertices are
     * assumed to be in the same plane.
     */
    public Polygon3D(Vector3D[] vertices) {
        this.v = vertices;
        numVertices = vertices.length;
        calcNormal();
    }

    /**
     * Sets this polygon to the same vertices as the specfied polygon.
     */
    public void setTo(Polygon3D polygon) {
        numVertices = polygon.numVertices;
        normal.setTo(polygon.normal);

        ensureCapacity(numVertices);
        for (int i = 0; i < numVertices; i++) {
            v[i].setTo(polygon.v[i]);
        }
    }

    /**
     * Ensures this polgon has enough capacity to hold the specified number of
     * vertices.
     */
    protected void ensureCapacity(int length) {
        if (v.length < length) {
            Vector3D[] newV = new Vector3D[length];
            System.arraycopy(v, 0, newV, 0, v.length);
            for (int i = v.length; i < newV.length; i++) {
                newV[i] = new Vector3D();
            }
            v = newV;
        }
    }

    /**
     * Gets the number of vertices this polygon has.
     */
    public int getNumVertices() {
        return numVertices;
    }

    /**
     * Gets the vertex at the specified index.
     */
    public Vector3D getVertex(int index) {
        return v[index];
    }

    /**
     * Projects this polygon onto the view window.
     */
    public void project(ViewWindow view) {
        for (int i = 0; i < numVertices; i++) {
            view.project(v[i]);
        }
    }

    // methods from the Transformable interface.
    public void add(Vector3D u) {
        for (int i = 0; i < numVertices; i++) {
            v[i].add(u);
        }
    }

    public void subtract(Vector3D u) {
        for (int i = 0; i < numVertices; i++) {
            v[i].subtract(u);
        }
    }

    public void add(Transform3D xform) {
        addRotation(xform);
        add(xform.getLocation());
    }

    public void subtract(Transform3D xform) {
        subtract(xform.getLocation());
        subtractRotation(xform);
    }

    public void addRotation(Transform3D xform) {
        for (int i = 0; i < numVertices; i++) {
            v[i].addRotation(xform);
        }
        normal.addRotation(xform);
    }

    public void subtractRotation(Transform3D xform) {
        for (int i = 0; i < numVertices; i++) {
            v[i].subtractRotation(xform);
        }
        normal.subtractRotation(xform);
    }

    /**
     * Calculates the unit-vector normal of this polygon. This method uses the
     * first, second, and third vertices to calcuate the normal, so if these
     * vertices are collinear, this method will not work. In this case, you can
     * get the normal from the bounding rectangle. Use setNormal() to explicitly
     * set the normal. This method uses static objects in the Polygon3D class
     * for calculations, so this method is not thread-safe across all instances
     * of Polygon3D.
     */
    public Vector3D calcNormal() {
        if (normal == null) {
            normal = new Vector3D();
        }
        temp1.setTo(v[2]);
        temp1.subtract(v[1]);
        temp2.setTo(v[0]);
        temp2.subtract(v[1]);
        normal.setToCrossProduct(temp1, temp2);
        normal.normalize();
        return normal;
    }

    /**
     * Gets the normal of this polygon. Use calcNormal() if any vertices have
     * changed.
     */
    public Vector3D getNormal() {
        return normal;
    }

    /**
     * Sets the normal of this polygon.
     */
    public void setNormal(Vector3D n) {
        if (normal == null) {
            normal = new Vector3D(n);
        } else {
            normal.setTo(n);
        }
    }

    /**
     * Tests if this polygon is facing the specified location. This method uses
     * static objects in the Polygon3D class for calculations, so this method is
     * not thread-safe across all instances of Polygon3D.
     */
    public boolean isFacing(Vector3D u) {
        temp1.setTo(u);
        temp1.subtract(v[0]);
        return (normal.getDotProduct(temp1) >= 0);
    }

    /**
     * Clips this polygon so that all vertices are in front of the clip plane,
     * clipZ (in other words, all vertices have z <= clipZ). The value of clipZ
     * should not be 0, as this causes divide-by-zero problems. Returns true if
     * the polygon is at least partially in front of the clip plane.
     */
    public boolean clip(float clipZ) {
        ensureCapacity(numVertices * 3);

        boolean isCompletelyHidden = true;

        // insert vertices so all edges are either completly
        // in front or behind the clip plane
        for (int i = 0; i < numVertices; i++) {
            int next = (i + 1) % numVertices;
            Vector3D v1 = v[i];
            Vector3D v2 = v[next];
            if (v1.z < clipZ) {
                isCompletelyHidden = false;
            }
            // ensure v1.z < v2.z
            if (v1.z > v2.z) {
                Vector3D temp = v1;
                v1 = v2;
                v2 = temp;
            }
            if (v1.z < clipZ && v2.z > clipZ) {
                float scale = (clipZ - v1.z) / (v2.z - v1.z);
                insertVertex(next, v1.x + scale * (v2.x - v1.x), v1.y + scale
                        * (v2.y - v1.y), clipZ);
                // skip the vertex we just created
                i++;
            }
        }

        if (isCompletelyHidden) {
            return false;
        }

        // delete all vertices that have z > clipZ
        for (int i = numVertices - 1; i >= 0; i--) {
            if (v[i].z > clipZ) {
                deleteVertex(i);
            }
        }

        return (numVertices >= 3);
    }

    /**
     * Inserts a new vertex at the specified index.
     */
    protected void insertVertex(int index, float x, float y, float z) {
        Vector3D newVertex = v[v.length - 1];
        newVertex.x = x;
        newVertex.y = y;
        newVertex.z = z;
        for (int i = v.length - 1; i > index; i--) {
            v[i] = v[i - 1];
        }
        v[index] = newVertex;
        numVertices++;
    }

    /**
     * Delete the vertex at the specified index.
     */
    protected void deleteVertex(int index) {
        Vector3D deleted = v[index];
        for (int i = index; i < v.length - 1; i++) {
            v[i] = v[i + 1];
        }
        v[v.length - 1] = deleted;
        numVertices--;
    }

    /**
     * Inserts a vertex into this polygon at the specified index. The exact
     * vertex in inserted (not a copy).
     */
    public void insertVertex(int index, Vector3D vertex) {
        Vector3D[] newV = new Vector3D[numVertices + 1];
        System.arraycopy(v, 0, newV, 0, index);
        newV[index] = vertex;
        System.arraycopy(v, index, newV, index + 1, numVertices - index);
        v = newV;
        numVertices++;
    }

}
//-----------------------------------------------------------------

/**
 * The SolidPolygon3D class is a Polygon with a color.
 */
class SolidPolygon3D extends Polygon3D {

    private Color color = Color.GREEN;

    public SolidPolygon3D() {
        super();
    }

    public SolidPolygon3D(Vector3D v0, Vector3D v1, Vector3D v2) {
        this(new Vector3D[]{v0, v1, v2});
    }

    public SolidPolygon3D(Vector3D v0, Vector3D v1, Vector3D v2, Vector3D v3) {
        this(new Vector3D[]{v0, v1, v2, v3});
    }

    public SolidPolygon3D(Vector3D[] vertices) {
        super(vertices);
    }

    public void setTo(Polygon3D polygon) {
        super.setTo(polygon);
        if (polygon instanceof SolidPolygon3D) {
            color = ((SolidPolygon3D) polygon).color;
        }
    }

    /**
     * Gets the color of this solid-colored polygon used for rendering this
     * polygon.
     */
    public Color getColor() {
        return color;
    }

    /**
     * Sets the color of this solid-colored polygon used for rendering this
     * polygon.
     */
    public void setColor(Color color) {
        this.color = color;
    }

}

/**
 * The ViewWindow class represents the geometry of a view window for 3D viewing.
 */
class ViewWindow {

    private Rectangle bounds;

    private float angle;

    private float distanceToCamera;

    /**
     * Creates a new ViewWindow with the specified bounds on the screen and
     * horizontal view angle.
     */
    public ViewWindow(int left, int top, int width, int height, float angle) {
        bounds = new Rectangle();
        this.angle = angle;
        setBounds(left, top, width, height);
    }

    /**
     * Sets the bounds for this ViewWindow on the screen.
     */
    public void setBounds(int left, int top, int width, int height) {
        bounds.x = left;
        bounds.y = top;
        bounds.width = width;
        bounds.height = height;
        distanceToCamera = (bounds.width / 2) / (float) Math.tan(angle / 2);
    }

    /**
     * Sets the horizontal view angle for this ViewWindow.
     */
    public void setAngle(float angle) {
        this.angle = angle;
        distanceToCamera = (bounds.width / 2) / (float) Math.tan(angle / 2);
    }

    /**
     * Gets the horizontal view angle of this view window.
     */
    public float getAngle() {
        return angle;
    }

    /**
     * Gets the width of this view window.
     */
    public int getWidth() {
        return bounds.width;
    }

    /**
     * Gets the height of this view window.
     */
    public int getHeight() {
        return bounds.height;
    }

    /**
     * Gets the y offset of this view window on the screen.
     */
    public int getTopOffset() {
        return bounds.y;
    }

    /**
     * Gets the x offset of this view window on the screen.
     */
    public int getLeftOffset() {
        return bounds.x;
    }

    /**
     * Gets the distance from the camera to to this view window.
     */
    public float getDistance() {
        return distanceToCamera;
    }

    /**
     * Converts an x coordinate on this view window to the corresponding x
     * coordinate on the screen.
     */
    public float convertFromViewXToScreenX(float x) {
        return x + bounds.x + bounds.width / 2;
    }

    /**
     * Converts a y coordinate on this view window to the corresponding y
     * coordinate on the screen.
     */
    public float convertFromViewYToScreenY(float y) {
        return -y + bounds.y + bounds.height / 2;
    }

    /**
     * Converts an x coordinate on the screen to the corresponding x coordinate
     * on this view window.
     */
    public float convertFromScreenXToViewX(float x) {
        return x - bounds.x - bounds.width / 2;
    }

    /**
     * Converts an y coordinate on the screen to the corresponding y coordinate
     * on this view window.
     */
    public float convertFromScreenYToViewY(float y) {
        return -y + bounds.y + bounds.height / 2;
    }

    /**
     * Projects the specified vector to the screen.
     */
    public void project(Vector3D v) {
        // project to view window
        v.x = distanceToCamera * v.x / -v.z;
        v.y = distanceToCamera * v.y / -v.z;

        // convert to screen coordinates
        v.x = convertFromViewXToScreenX(v.x);
        v.y = convertFromViewYToScreenY(v.y);
    }
}
//-----------------------------------------------------------------

/**
 * The Transform3D class represents a rotation and translation.
 */
class Transform3D {

    protected Vector3D location;

    private float cosAngleX;

    private float sinAngleX;

    private float cosAngleY;

    private float sinAngleY;

    private float cosAngleZ;

    private float sinAngleZ;

    /**
     * Creates a new Transform3D with no translation or rotation.
     */
    public Transform3D() {
        this(0, 0, 0);
    }

    /**
     * Creates a new Transform3D with the specified translation and no rotation.
     */
    public Transform3D(float x, float y, float z) {
        location = new Vector3D(x, y, z);
        setAngle(0, 0, 0);
    }

    /**
     * Creates a new Transform3D
     */
    public Transform3D(Transform3D v) {
        location = new Vector3D();
        setTo(v);
    }

    public Object clone() {
        return new Transform3D(this);
    }

    /**
     * Sets this Transform3D to the specified Transform3D.
     */
    public void setTo(Transform3D v) {
        location.setTo(v.location);
        this.cosAngleX = v.cosAngleX;
        this.sinAngleX = v.sinAngleX;
        this.cosAngleY = v.cosAngleY;
        this.sinAngleY = v.sinAngleY;
        this.cosAngleZ = v.cosAngleZ;
        this.sinAngleZ = v.sinAngleZ;
    }

    /**
     * Gets the location (translation) of this transform.
     */
    public Vector3D getLocation() {
        return location;
    }

    public float getCosAngleX() {
        return cosAngleX;
    }

    public float getSinAngleX() {
        return sinAngleX;
    }

    public float getCosAngleY() {
        return cosAngleY;
    }

    public float getSinAngleY() {
        return sinAngleY;
    }

    public float getCosAngleZ() {
        return cosAngleZ;
    }

    public float getSinAngleZ() {
        return sinAngleZ;
    }

    public float getAngleX() {
        return (float) Math.atan2(sinAngleX, cosAngleX);
    }

    public float getAngleY() {
        return (float) Math.atan2(sinAngleY, cosAngleY);
    }

    public float getAngleZ() {
        return (float) Math.atan2(sinAngleZ, cosAngleZ);
    }

    public void setAngleX(float angleX) {
        cosAngleX = (float) Math.cos(angleX);
        sinAngleX = (float) Math.sin(angleX);
    }

    public void setAngleY(float angleY) {
        cosAngleY = (float) Math.cos(angleY);
        sinAngleY = (float) Math.sin(angleY);
    }

    public void setAngleZ(float angleZ) {
        cosAngleZ = (float) Math.cos(angleZ);
        sinAngleZ = (float) Math.sin(angleZ);
    }

    public void setAngle(float angleX, float angleY, float angleZ) {
        setAngleX(angleX);
        setAngleY(angleY);
        setAngleZ(angleZ);
    }

    public void rotateAngleX(float angle) {
        if (angle != 0) {
            setAngleX(getAngleX() + angle);
        }
    }

    public void rotateAngleY(float angle) {
        if (angle != 0) {
            setAngleY(getAngleY() + angle);
        }
    }

    public void rotateAngleZ(float angle) {
        if (angle != 0) {
            setAngleZ(getAngleZ() + angle);
        }
    }

    public void rotateAngle(float angleX, float angleY, float angleZ) {
        rotateAngleX(angleX);
        rotateAngleY(angleY);
        rotateAngleZ(angleZ);
    }

}
//-----------------------------------------------------------------

/**
 * The InputManager manages input of key and mouse events. Events are mapped to
 * GameActions.
 */
class InputManager implements KeyListener, MouseListener, MouseMotionListener, MouseWheelListener {

    /**
     * An invisible cursor.
     */
    public static final Cursor INVISIBLE_CURSOR = Toolkit.getDefaultToolkit()
            .createCustomCursor(Toolkit.getDefaultToolkit().getImage(""),
                    new Point(0, 0), "invisible");

    // mouse codes
    public static final int MOUSE_MOVE_LEFT = 0;

    public static final int MOUSE_MOVE_RIGHT = 1;

    public static final int MOUSE_MOVE_UP = 2;

    public static final int MOUSE_MOVE_DOWN = 3;

    public static final int MOUSE_WHEEL_UP = 4;

    public static final int MOUSE_WHEEL_DOWN = 5;

    public static final int MOUSE_BUTTON_1 = 6;

    public static final int MOUSE_BUTTON_2 = 7;

    public static final int MOUSE_BUTTON_3 = 8;

    private static final int NUM_MOUSE_CODES = 9;

    // key codes are defined in java.awt.KeyEvent.
    // most of the codes (except for some rare ones like
    // "alt graph") are less than 600.
    private static final int NUM_KEY_CODES = 600;

    private GameAction[] keyActions = new GameAction[NUM_KEY_CODES];

    private GameAction[] mouseActions = new GameAction[NUM_MOUSE_CODES];

    private Point mouseLocation;

    private Point centerLocation;

    private Component comp;

    private Robot robot;

    private boolean isRecentering;

    /**
     * Creates a new InputManager that listens to input from the specified
     * component.
     */
    public InputManager(Component comp) {
        this.comp = comp;
        mouseLocation = new Point();
        centerLocation = new Point();

        // register key and mouse listeners
        comp.addKeyListener(this);
        comp.addMouseListener(this);
        comp.addMouseMotionListener(this);
        comp.addMouseWheelListener(this);

        // allow input of the TAB key and other keys normally
        // used for focus traversal
        comp.setFocusTraversalKeysEnabled(false);
    }

    /**
     * Sets the cursor on this InputManager's input component.
     */
    public void setCursor(Cursor cursor) {
        comp.setCursor(cursor);
    }

    /**
     * Sets whether realtive mouse mode is on or not. For relative mouse mode,
     * the mouse is "locked" in the center of the screen, and only the changed
     * in mouse movement is measured. In normal mode, the mouse is free to move
     * about the screen.
     */
    public void setRelativeMouseMode(boolean mode) {
        if (mode == isRelativeMouseMode()) {
            return;
        }

        if (mode) {
            try {
                robot = new Robot();
                mouseLocation.x = comp.getWidth() / 2;
                mouseLocation.y = comp.getHeight() / 2;
                recenterMouse();
            } catch (AWTException ex) {
                // couldn't create robot!
                robot = null;
            }
        } else {
            robot = null;
        }
    }

    /**
     * Returns whether or not relative mouse mode is on.
     */
    public boolean isRelativeMouseMode() {
        return (robot != null);
    }

    /**
     * Maps a GameAction to a specific key. The key codes are defined in
     * java.awt.KeyEvent. If the key already has a GameAction mapped to it, the
     * new GameAction overwrites it.
     */
    public void mapToKey(GameAction gameAction, int keyCode) {
        keyActions[keyCode] = gameAction;
    }

    /**
     * Maps a GameAction to a specific mouse action. The mouse codes are defined
     * herer in InputManager (MOUSE_MOVE_LEFT, MOUSE_BUTTON_1, etc). If the
     * mouse action already has a GameAction mapped to it, the new GameAction
     * overwrites it.
     */
    public void mapToMouse(GameAction gameAction, int mouseCode) {
        mouseActions[mouseCode] = gameAction;
    }

    /**
     * Clears all mapped keys and mouse actions to this GameAction.
     */
    public void clearMap(GameAction gameAction) {
        for (int i = 0; i < keyActions.length; i++) {
            if (keyActions[i] == gameAction) {
                keyActions[i] = null;
            }
        }

        for (int i = 0; i < mouseActions.length; i++) {
            if (mouseActions[i] == gameAction) {
                mouseActions[i] = null;
            }
        }

        gameAction.reset();
    }

    /**
     * Gets a List of names of the keys and mouse actions mapped to this
     * GameAction. Each entry in the List is a String.
     */
    public List getMaps(GameAction gameCode) {
        ArrayList list = new ArrayList();

        for (int i = 0; i < keyActions.length; i++) {
            if (keyActions[i] == gameCode) {
                list.add(getKeyName(i));
            }
        }

        for (int i = 0; i < mouseActions.length; i++) {
            if (mouseActions[i] == gameCode) {
                list.add(getMouseName(i));
            }
        }
        return list;
    }

    /**
     * Resets all GameActions so they appear like they haven't been pressed.
     */
    public void resetAllGameActions() {
        for (int i = 0; i < keyActions.length; i++) {
            if (keyActions[i] != null) {
                keyActions[i].reset();
            }
        }

        for (int i = 0; i < mouseActions.length; i++) {
            if (mouseActions[i] != null) {
                mouseActions[i].reset();
            }
        }
    }

    /**
     * Gets the name of a key code.
     */
    public static String getKeyName(int keyCode) {
        return KeyEvent.getKeyText(keyCode);
    }

    /**
     * Gets the name of a mouse code.
     */
    public static String getMouseName(int mouseCode) {
        switch (mouseCode) {
            case MOUSE_MOVE_LEFT:
                return "Mouse Left";
            case MOUSE_MOVE_RIGHT:
                return "Mouse Right";
            case MOUSE_MOVE_UP:
                return "Mouse Up";
            case MOUSE_MOVE_DOWN:
                return "Mouse Down";
            case MOUSE_WHEEL_UP:
                return "Mouse Wheel Up";
            case MOUSE_WHEEL_DOWN:
                return "Mouse Wheel Down";
            case MOUSE_BUTTON_1:
                return "Mouse Button 1";
            case MOUSE_BUTTON_2:
                return "Mouse Button 2";
            case MOUSE_BUTTON_3:
                return "Mouse Button 3";
            default:
                return "Unknown mouse code " + mouseCode;
        }
    }

    /**
     * Gets the x position of the mouse.
     */
    public int getMouseX() {
        return mouseLocation.x;
    }

    /**
     * Gets the y position of the mouse.
     */
    public int getMouseY() {
        return mouseLocation.y;
    }

    /**
     * Uses the Robot class to try to postion the mouse in the center of the
     * screen.
     * <p>
     * Note that use of the Robot class may not be available on all platforms.
     */
    private synchronized void recenterMouse() {
        if (robot != null && comp.isShowing()) {
            centerLocation.x = comp.getWidth() / 2;
            centerLocation.y = comp.getHeight() / 2;
            SwingUtilities.convertPointToScreen(centerLocation, comp);
            isRecentering = true;
            robot.mouseMove(centerLocation.x, centerLocation.y);
        }
    }

    private GameAction getKeyAction(KeyEvent e) {
        int keyCode = e.getKeyCode();
        if (keyCode < keyActions.length) {
            return keyActions[keyCode];
        } else {
            return null;
        }
    }

    /**
     * Gets the mouse code for the button specified in this MouseEvent.
     */
    public static int getMouseButtonCode(MouseEvent e) {
        switch (e.getButton()) {
            case MouseEvent.BUTTON1:
                return MOUSE_BUTTON_1;
            case MouseEvent.BUTTON2:
                return MOUSE_BUTTON_2;
            case MouseEvent.BUTTON3:
                return MOUSE_BUTTON_3;
            default:
                return -1;
        }
    }

    private GameAction getMouseButtonAction(MouseEvent e) {
        int mouseCode = getMouseButtonCode(e);
        if (mouseCode != -1) {
            return mouseActions[mouseCode];
        } else {
            return null;
        }
    }

    // from the KeyListener interface
    public void keyPressed(KeyEvent e) {
        GameAction gameAction = getKeyAction(e);
        if (gameAction != null) {
            gameAction.press();
        }
        // make sure the key isn't processed for anything else
        e.consume();
    }

    // from the KeyListener interface
    public void keyReleased(KeyEvent e) {
        GameAction gameAction = getKeyAction(e);
        if (gameAction != null) {
            gameAction.release();
        }
        // make sure the key isn't processed for anything else
        e.consume();
    }

    // from the KeyListener interface
    public void keyTyped(KeyEvent e) {
        // make sure the key isn't processed for anything else
        e.consume();
    }

    // from the MouseListener interface
    public void mousePressed(MouseEvent e) {
        GameAction gameAction = getMouseButtonAction(e);
        if (gameAction != null) {
            gameAction.press();
        }
    }

    // from the MouseListener interface
    public void mouseReleased(MouseEvent e) {
        GameAction gameAction = getMouseButtonAction(e);
        if (gameAction != null) {
            gameAction.release();
        }
    }

    // from the MouseListener interface
    public void mouseClicked(MouseEvent e) {
        // do nothing
    }

    // from the MouseListener interface
    public void mouseEntered(MouseEvent e) {
        mouseMoved(e);
    }

    // from the MouseListener interface
    public void mouseExited(MouseEvent e) {
        mouseMoved(e);
    }

    // from the MouseMotionListener interface
    public void mouseDragged(MouseEvent e) {
        mouseMoved(e);
    }

    // from the MouseMotionListener interface
    public synchronized void mouseMoved(MouseEvent e) {
        // this event is from re-centering the mouse - ignore it
        if (isRecentering && centerLocation.x == e.getX()
                && centerLocation.y == e.getY()) {
            isRecentering = false;
        } else {
            int dx = e.getX() - mouseLocation.x;
            int dy = e.getY() - mouseLocation.y;
            mouseHelper(MOUSE_MOVE_LEFT, MOUSE_MOVE_RIGHT, dx);
            mouseHelper(MOUSE_MOVE_UP, MOUSE_MOVE_DOWN, dy);

            if (isRelativeMouseMode()) {
                recenterMouse();
            }
        }

        mouseLocation.x = e.getX();
        mouseLocation.y = e.getY();

    }

    // from the MouseWheelListener interface
    public void mouseWheelMoved(MouseWheelEvent e) {
        mouseHelper(MOUSE_WHEEL_UP, MOUSE_WHEEL_DOWN, e.getWheelRotation());
    }

    private void mouseHelper(int codeNeg, int codePos, int amount) {
        GameAction gameAction;
        if (amount < 0) {
            gameAction = mouseActions[codeNeg];
        } else {
            gameAction = mouseActions[codePos];
        }
        if (gameAction != null) {
            gameAction.press(Math.abs(amount));
            gameAction.release();
        }
    }

}
//-----------------------------------------------------------------

/**
 * The ScreenManager class manages initializing and displaying full screen
 * graphics modes.
 */
class ScreenManager {

    private GraphicsDevice device;

    /**
     * Creates a new ScreenManager object.
     */
    public ScreenManager() {
        GraphicsEnvironment environment = GraphicsEnvironment
                .getLocalGraphicsEnvironment();
        device = environment.getDefaultScreenDevice();
    }

    /**
     * Returns a list of compatible display modes for the default device on the
     * system.
     */
    public DisplayMode[] getCompatibleDisplayModes() {
        return device.getDisplayModes();
    }

    /**
     * Returns the first compatible mode in a list of modes. Returns null if no
     * modes are compatible.
     */
    public DisplayMode findFirstCompatibleMode(DisplayMode modes[]) {
        DisplayMode goodModes[] = device.getDisplayModes();
        for (int i = 0; i < modes.length; i++) {
            for (int j = 0; j < goodModes.length; j++) {
                if (displayModesMatch(modes[i], goodModes[j])) {
                    return modes[i];
                }
            }

        }

        return null;
    }

    /**
     * Returns the current display mode.
     */
    public DisplayMode getCurrentDisplayMode() {
        return device.getDisplayMode();
    }

    /**
     * Determines if two display modes "match". Two display modes match if they
     * have the same resolution, bit depth, and refresh rate. The bit depth is
     * ignored if one of the modes has a bit depth of
     * DisplayMode.BIT_DEPTH_MULTI. Likewise, the refresh rate is ignored if one
     * of the modes has a refresh rate of DisplayMode.REFRESH_RATE_UNKNOWN.
     */
    public boolean displayModesMatch(DisplayMode mode1, DisplayMode mode2) {
        if (mode1.getWidth() != mode2.getWidth()
                || mode1.getHeight() != mode2.getHeight()) {
            return false;
        }

        if (mode1.getBitDepth() != DisplayMode.BIT_DEPTH_MULTI
                && mode2.getBitDepth() != DisplayMode.BIT_DEPTH_MULTI
                && mode1.getBitDepth() != mode2.getBitDepth()) {
            return false;
        }

        if (mode1.getRefreshRate() != DisplayMode.REFRESH_RATE_UNKNOWN
                && mode2.getRefreshRate() != DisplayMode.REFRESH_RATE_UNKNOWN
                && mode1.getRefreshRate() != mode2.getRefreshRate()) {
            return false;
        }

        return true;
    }

    /**
     * Enters full screen mode and changes the display mode. If the specified
     * display mode is null or not compatible with this device, or if the
     * display mode cannot be changed on this system, the current display mode
     * is used.
     * <p>
     * The display uses a BufferStrategy with 2 buffers.
     */
    public void setFullScreen(DisplayMode displayMode) {
        final JFrame frame = new JFrame();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setUndecorated(true);
        frame.setIgnoreRepaint(true);
        frame.setResizable(false);

        device.setFullScreenWindow(frame);

        if (displayMode != null && device.isDisplayChangeSupported()) {
            try {
                device.setDisplayMode(displayMode);
            } catch (IllegalArgumentException ex) {
            }
            // fix for mac os x
            frame.setSize(displayMode.getWidth(), displayMode.getHeight());
        }
        // avoid potential deadlock in 1.4.1_02
        try {
            EventQueue.invokeAndWait(new Runnable() {
                public void run() {
                    frame.createBufferStrategy(2);
                }
            });
        } catch (InterruptedException ex) {
            // ignore
        } catch (InvocationTargetException ex) {
            // ignore
        }

    }

    /**
     * Gets the graphics context for the display. The ScreenManager uses double
     * buffering, so applications must call update() to show any graphics drawn.
     * <p>
     * The application must dispose of the graphics object.
     */
    public Graphics2D getGraphics() {
        Window window = device.getFullScreenWindow();
        if (window != null) {
            BufferStrategy strategy = window.getBufferStrategy();
            return (Graphics2D) strategy.getDrawGraphics();
        } else {
            return null;
        }
    }

    /**
     * Updates the display.
     */
    public void update() {
        Window window = device.getFullScreenWindow();
        if (window != null) {
            BufferStrategy strategy = window.getBufferStrategy();
            if (!strategy.contentsLost()) {
                strategy.show();
            }
        }
        // Sync the display on some systems.
        // (on Linux, this fixes event queue problems)
        //Toolkit.getDefaultToolkit().sync();
    }

    /**
     * Returns the window currently used in full screen mode. Returns null if
     * the device is not in full screen mode.
     */
    public JFrame getFullScreenWindow() {
        return (JFrame) device.getFullScreenWindow();
    }

    /**
     * Returns the width of the window currently used in full screen mode.
     * Returns 0 if the device is not in full screen mode.
     */
    public int getWidth() {
        Window window = device.getFullScreenWindow();
        if (window != null) {
            return window.getWidth();
        } else {
            return 0;
        }
    }

    /**
     * Returns the height of the window currently used in full screen mode.
     * Returns 0 if the device is not in full screen mode.
     */
    public int getHeight() {
        Window window = device.getFullScreenWindow();
        if (window != null) {
            return window.getHeight();
        } else {
            return 0;
        }
    }

    /**
     * Restores the screen's display mode.
     */
    public void restoreScreen() {
        Window window = device.getFullScreenWindow();
        if (window != null) {
            window.dispose();
        }
        device.setFullScreenWindow(null);
    }

    /**
     * Creates an image compatible with the current display.
     */
    public BufferedImage createCompatibleImage(int w, int h, int transparancy) {
        Window window = device.getFullScreenWindow();
        if (window != null) {
            GraphicsConfiguration gc = window.getGraphicsConfiguration();
            return gc.createCompatibleImage(w, h, transparancy);
        }
        return null;
    }
}
//-----------------------------------------------------------------

interface Transformable {
    public void add(Vector3D u);
    public void subtract(Vector3D u);
    public void add(Transform3D xform);
    public void subtract(Transform3D xform);
    public void addRotation(Transform3D xform);
    public void subtractRotation(Transform3D xform);
}
