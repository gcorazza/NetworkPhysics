package Rendering;

import NetworkedPhysics.Common.PhysicsObject;
import NetworkedPhysics.Common.Dto.NetworkedPhysicsObjectDto;
import com.bulletphysics.linearmath.Transform;
import org.joml.Matrix4f;
import org.joml.Vector3f;

public class PhysicsWorldEntity {

    public final BoundedObj boundedObj;
    private Vector3f scale;
    private PhysicsObject physicsObject;
    private ShaderProgram shader;

    public PhysicsWorldEntity(PhysicsObject physicsObject, ShaderProgram shader) {
        this.physicsObject = physicsObject;
        this.shader = shader;
        NetworkedPhysicsObjectDto physicsObjectDto = physicsObject.getDto();

        switch (physicsObjectDto.shape) {
            case CUBE: {
                boundedObj = Shapes.cube;
                scale = new Vector3f(physicsObjectDto.a, physicsObjectDto.b, physicsObjectDto.c);
                break;
            }
            case SPHERE: {
                boundedObj = Shapes.sphere;
                scale = new Vector3f(physicsObjectDto.a, physicsObjectDto.a, physicsObjectDto.a);
                break;
            }
            case PLANE: {
                boundedObj = Shapes.plane;
                scale = new Vector3f(1, 1, 1);
                break;
            }
            default:
                throw new RuntimeException("shape not set");
        }
    }

    public void free() {
        boundedObj.free();
    }

    public Matrix4f getModel() {
        Transform transform = new Transform();
        physicsObject.getBody().getMotionState().getWorldTransform(transform);
        float[] m = new float[16];
        transform.getOpenGLMatrix(m);
        Matrix4f model = new Matrix4f();
        model.set(m);
        model.scale(scale);
        return model;
    }

    public ShaderProgram getShader() {
        return shader;
    }

    //pos
    //rot
}
