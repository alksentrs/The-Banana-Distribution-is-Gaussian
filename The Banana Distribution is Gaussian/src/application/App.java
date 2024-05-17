package application;

import business.Sketch;
import presentation.MainView;

public class App {

    public static void main(String[] args) {
        Sketch sketch = new Sketch();
        MainView window = new MainView(sketch);
        window.addPaintListener(sketch);
        window.open();
    }
}
