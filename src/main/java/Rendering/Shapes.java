package Rendering;

import java.io.IOException;

public class Shapes {
    public static BoundedObj cube;
    public static BoundedObj sphere;
    public static BoundedObj plane;

    static {
        try {
            cube = new BoundedObj(Class.forName(Shapes.class.getName()).getResourceAsStream("/cube.obj"));
            sphere = new BoundedObj(Class.forName(Shapes.class.getName()).getResourceAsStream("/sphere.obj"));
            plane = new BoundedObj(Class.forName(Shapes.class.getName()).getResourceAsStream("/plane.obj"));
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}
