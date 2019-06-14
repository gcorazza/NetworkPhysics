package Rendering;

import java.io.IOException;

public class Shapes {
    public static BoundedObj cube;
    public static BoundedObj sphere;
    public static BoundedObj plane;

    static {
        try {
            cube = new BoundedObj(System.class.getResource("/cube.obj").openStream());
            sphere = new BoundedObj(System.class.getResource("/sphere.obj").openStream());
            plane = new BoundedObj(System.class.getResource("/plane.obj").openStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
