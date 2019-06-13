package Rendering;

import de.javagl.obj.Obj;
import de.javagl.obj.ObjData;
import de.javagl.obj.ObjReader;
import de.javagl.obj.ObjUtils;

import java.io.IOException;
import java.io.InputStream;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.Arrays;

import static org.lwjgl.opengl.GL15.*;

public class BoundedObj {


    public final int vboNormal;
    public final int vboIndices;
    public final int vboVert;
    public final Obj obj;

    public BoundedObj(InputStream objInputStream) throws IOException {
        obj = ObjUtils.convertToRenderable(ObjReader.read(objInputStream));
        IntBuffer faceVertexIndices = ObjData.getFaceVertexIndices(obj);
        FloatBuffer vertices = ObjData.getVertices(obj);
        FloatBuffer texCoords = ObjData.getTexCoords(obj, 2);
        FloatBuffer normals = ObjData.getNormals(obj);

        vboVert = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, vboVert);
        glBufferData(GL_ARRAY_BUFFER, vertices, GL_STATIC_DRAW);
        glBindBuffer(GL_ARRAY_BUFFER, 0);

        vboNormal = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, vboNormal);
        glBufferData(GL_ARRAY_BUFFER, normals, GL_STATIC_DRAW);
        glBindBuffer(GL_ARRAY_BUFFER, 0);

        vboIndices = glGenBuffers();
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, vboIndices);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, faceVertexIndices, GL_STATIC_DRAW);
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0);
    }


    public void free() {
        glDeleteBuffers(vboVert);
        glDeleteBuffers(vboNormal);
        glDeleteBuffers(vboVert);
    }
}
