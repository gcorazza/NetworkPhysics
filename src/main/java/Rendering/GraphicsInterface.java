package Rendering;

import java.io.InputStream;

public interface GraphicsInterface<Model3D> {
    Model3D loadOBJ(InputStream objStream);
    void draw(WorldEntity worldEntity);
}
