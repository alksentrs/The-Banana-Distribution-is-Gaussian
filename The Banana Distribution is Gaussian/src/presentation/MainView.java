package presentation;

import business.Sketch;
import presentation.util.JPaintListener;
import presentation.util.JPanelAlk;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class MainView {

    public static Color white = new Color(255,255,255);
    public static Color dgreen = new Color(0,88,0);
    public static Color dblue = new Color(0,44,122);
    public static Color xblue = new Color(0,110,255);
    public static Color mag = new Color(255,0,255);
    public static Color red = new Color(255,00,00);
    public static Color lred = new Color(255,140,140);
    public static Color abobora = new Color(255,110,00);
    public static Color yellow = new Color(230,230,00);
    public static Color green = new Color(0,220,00);
    public static Color cyan = new Color(0,210,210);
    public static Color dgray = Color.DARK_GRAY;
    public static Color gray = Color.GRAY;
    public static Color lgray = Color.LIGHT_GRAY;
    public static Color dyellow = new Color(140, 103, 47);
    public static Color black = Color.BLACK;

    private JFrame jFrame;
    private JPanel jPanelMain;
    private JPanelAlk jPanelDraw;
    private Sketch sketch;

    public MainView(Sketch sketch) {
        this.sketch = sketch;
    }

    public void open() {
        jFrame = new JFrame(sketch.getWindowName());
        jFrame.setPreferredSize(new Dimension(sketch.getWidth()+20, sketch.getHeight()+40));
        jFrame.add(jPanelMain);

        jFrame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        jFrame.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent evt) {
                close();
            }
        });
        jFrame.pack();
        jFrame.setVisible(true);
    }

    public void createUIComponents() {
        jPanelDraw = new JPanelAlk();
        jPanelDraw.setBackground(Color.BLACK);
    }

    public void addPaintListener(JPaintListener paintListener) {
        jPanelDraw.addPaintListener(paintListener);
    }

    private void close() {
        System.exit(0);
    }

}
