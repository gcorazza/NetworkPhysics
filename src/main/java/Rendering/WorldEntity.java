package Rendering;

import org.joml.Matrix4f;
import org.lwjgl.system.Configuration;

import java.nio.FloatBuffer;

public class WorldEntity {

    public final BoundedObj boundedObj;
    private Matrix4f model= new Matrix4f();

    public WorldEntity(BoundedObj boundedObj) {
        this.boundedObj = boundedObj;
    }

    public void free(){
        boundedObj.free();
    }

    public Matrix4f getPosition() {
        return model;
    }

    //pos
    //rot
}
