/*
 * Copyright LWJGL. All rights reserved.
 * License terms: https://www.lwjgl.org/license
 */
package Rendering;

import NetworkedPhysics.Common.NetworkedPhysics;
import NetworkedPhysics.Common.NetworkedPhysicsObject;
import NetworkedPhysics.Common.Protocol.Dto.NetworkedPhysicsObjectDto;
import NetworkedPhysics.Common.Protocol.Shape;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.BufferUtils;
import org.lwjgl.glfw.*;
import org.lwjgl.opengl.*;
import org.lwjgl.system.*;

import javax.vecmath.Quat4f;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.glUniformMatrix4fv;
import static org.lwjgl.system.MemoryUtil.*;

public class PhysicsWorldRenderer {

    // We need to strongly reference callback instances.
    private GLFWErrorCallback errorCallback;
    private GLFWKeyCallback keyCallback;
    private GLFWWindowSizeCallback wsCallback;
    private Callback debugProc;

    // The window handle
    private long window;
    private int width, height;

    private NetworkedPhysics networkedPhysics;
    private ShaderProgram shaderProgram;
    private Matrix4f cam = new Matrix4f();
    private boolean[] keyDown = new boolean[GLFW.GLFW_KEY_LAST];


    private float FOV = (float) Math.PI / 4;
    private float Z_NEAR = 0.05f;
    private float Z_FAR = 100f;
    private int uniformLocationProjection;
    private int uniformLocationModel;
    private int uniformLocationView;

    private FloatBuffer mat4Buffer = BufferUtils.createFloatBuffer(16);
    //    private Matrix4f identity;
    private GLFWCursorPosCallback mousePosCallback;
    private double camLookXRad;
    private double camLookYRad;
    private Vector3f camPos = new Vector3f(0, 0, -10);
    private double camSensitivity = 200;
    private float camSpeed = 0.05f;
    private List<PhysicsWorldEntity> entities = new ArrayList<>();

    public PhysicsWorldRenderer(NetworkedPhysics networkedPhysics) throws Exception {
        this.networkedPhysics = networkedPhysics;
        init();
    }

    public void run()  {
        try {
            loop();

            // Release window and window callbacks
            glfwDestroyWindow(window);
            keyCallback.free();
            wsCallback.free();
            if (debugProc != null)
                debugProc.free();
        } finally {
            // Terminate GLFW and release the GLFWerrorfun
            glfwTerminate();
            errorCallback.free();
            networkedPhysics.shutDown();
        }
    }

    private void init() throws Exception {


        // Setup an error callback. The default implementation
        // will print the error message in System.err.
        glfwSetErrorCallback(errorCallback = GLFWErrorCallback.createPrint(System.err));

        // Initialize GLFW. Most GLFW functions will not work before doing this.
        if (!glfwInit())
            throw new IllegalStateException("Unable to initialize GLFW");

        // Configure our window
        glfwDefaultWindowHints(); // optional, the current window hints are already the default
        glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 1);
        glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 5);
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE); // the window will stay hidden after creation
        glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE); // the window will be resizable

        int WIDTH = 600;
        int HEIGHT = 600;

        // Create the window
        window = glfwCreateWindow(WIDTH, HEIGHT, "Hello World!", NULL, NULL);
        if (window == NULL)
            throw new RuntimeException("Failed to create the GLFW window");

        // Setup a key callback. It will be called every time a key is pressed, repeated or released.
        glfwSetKeyCallback(window, keyCallback = new GLFWKeyCallback() {
            public void invoke(long window, int key, int scancode, int action, int mods) {
                if (key == GLFW_KEY_UNKNOWN)
                    return;
                if (key == GLFW_KEY_ESCAPE && action == GLFW_RELEASE) {
                    glfwSetWindowShouldClose(window, true);
                }
                if (action == GLFW_PRESS || action == GLFW_REPEAT) {
                    keyDown[key] = true;
                } else {
                    keyDown[key] = false;
                }
            }
        });
        glfwSetWindowSizeCallback(window, wsCallback = new GLFWWindowSizeCallback() {
            @Override
            public void invoke(long window, int w, int h) {
                if (w > 0 && h > 0) {
                    width = w;
                    height = h;
                }
            }
        });

        glfwSetCursorPosCallback(window, mousePosCallback = new GLFWCursorPosCallback() {
            @Override
            public void invoke(long l, double x, double y) {
                glfwSetCursorPos(window, width / 2, height / 2);
                double dx = width / 2 - x;
                double dy = height / 2 - y;
                camLookYRad -= dy / camSensitivity;
                camLookXRad += dx / camSensitivity;

                if (camLookYRad > Math.PI / 2)
                    camLookYRad = Math.PI / 2 - 0.001;
                if (camLookYRad < -Math.PI / 2)
                    camLookYRad = -Math.PI / 2 + 0.001;
            }
        });

        GLFWVidMode vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor());
        glfwSetWindowPos(window, (vidmode.width() - width) / 2, (vidmode.height() - height) / 2);
        try (MemoryStack frame = MemoryStack.stackPush()) {
            IntBuffer framebufferSize = frame.mallocInt(2);
            nglfwGetFramebufferSize(window, memAddress(framebufferSize), memAddress(framebufferSize) + 4);
            width = framebufferSize.get(0);
            height = framebufferSize.get(1);
        }
        // Make the OpenGL context current
        glfwMakeContextCurrent(window);
        // Enable v-sync
        glfwSwapInterval(1);

        // Make the window visible
        glfwShowWindow(window);


        GL.createCapabilities();
        glEnable(GL_DEPTH_TEST);

        debugProc = GLUtil.setupDebugMessageCallback();

        // Set the clear color
        glClearColor(0.0f, 0.0f, 0.0f, 0.0f);

        shaderProgram = new ShaderProgram();
        shaderProgram.createVertexShader(Utils.loadResource("/vertex.vs"));
        shaderProgram.createFragmentShader(Utils.loadResource("/fragment.fs"));
        shaderProgram.link();
        shaderProgram.bind();

        uniformLocationProjection = shaderProgram.getUniformLocation("proj");
        uniformLocationView = shaderProgram.getUniformLocation("view");
        uniformLocationModel = shaderProgram.getUniformLocation("model");
        setPerspectiveProjection();

        glEnableClientState(GL_VERTEX_ARRAY);
        NetworkedPhysicsObjectDto physicsObjectDto= new NetworkedPhysicsObjectDto(0, Shape.CUBE, 1,1,1,0,0,0,
                new javax.vecmath.Vector3f(0,0,0),new Quat4f(0,0,0,1 ));
        PhysicsWorldEntity physicsWorldEntity = new PhysicsWorldEntity(new NetworkedPhysicsObject(physicsObjectDto));
        entities.add(physicsWorldEntity);
    }


    private void printVector(Vector3f vec) {
        System.out.format("LookAt: %f,%f,%f\n", vec.x, vec.y, vec.z);
    }

    private Vector3f getLookAtDirection() {
        Vector3f vector3f = new Vector3f(0, 0, 1f);
        vector3f.rotateX((float) camLookYRad);
        vector3f.rotateY((float) camLookXRad);
        return vector3f;
    }

    private void updateControlls() {
        glfwPollEvents();

        if (keyDown[GLFW_KEY_W]) {
            Vector3f direction = new Vector3f(0, 0, 1f);
            direction.rotateY((float) (camLookXRad));
            camPos.add(direction.mul(camSpeed), camPos);
        }
        if (keyDown[GLFW_KEY_S]) {
            Vector3f direction = new Vector3f(0, 0, 1f);
            direction.rotateY((float) (camLookXRad));
            camPos.add(direction.negate().mul(camSpeed), camPos);
        }
        if (keyDown[GLFW_KEY_A]) {
            Vector3f direction = new Vector3f(0, 0, 1f);
            direction.rotateY((float) (camLookXRad + (2 * Math.PI / 4)));
            camPos.add(direction.mul(camSpeed), camPos);
        }
        if (keyDown[GLFW_KEY_D]) {
            Vector3f direction = new Vector3f(0, 0, 1f);
            direction.rotateY((float) (camLookXRad - (2 * Math.PI / 4)));
            camPos.add(direction.mul(camSpeed), camPos);
        }
        if (keyDown[GLFW_KEY_SPACE]) {
            camPos.add(new Vector3f(0, camSpeed, 0), camPos);
        }
        if (keyDown[GLFW_KEY_LEFT_SHIFT]) {
            camPos.add(new Vector3f(0, -camSpeed, 0), camPos);
        }
    }

    public void renderAFrame(){
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT); // clear the framebuffer
        glViewport(0, 0, width, height);
        entities.forEach(this::drawWorldEntity);
        glfwSwapBuffers(window);
    }

    private void loop() {

        while (!glfwWindowShouldClose(window)) {
            updateControlls();
            setCamera();
            renderAFrame();
            networkedPhysics.update();
        }
    }

    private void drawWorldEntity(PhysicsWorldEntity worldEntity){
        glUniformMatrix4fv(uniformLocationModel, false, worldEntity.getModel().get(mat4Buffer));

        glEnableClientState(GL_NORMAL_ARRAY);

        glBindBuffer(GL_ARRAY_BUFFER, worldEntity.boundedObj.vboVert);
        glVertexPointer(3, GL_FLOAT, 0, 0);
        glBindBuffer(GL_ARRAY_BUFFER, 0);


        glBindBuffer(GL_ARRAY_BUFFER, worldEntity.boundedObj.vboNormal);
        glNormalPointer(GL_FLOAT, 0, 0);
        glBindBuffer(GL_ARRAY_BUFFER, 0);

        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, worldEntity.boundedObj.vboIndices);
        int numFaces = worldEntity.boundedObj.obj.getNumFaces();
        glDrawElements(GL_TRIANGLES, numFaces * 3, GL_UNSIGNED_INT, 0);

        glDisableClientState(GL_NORMAL_ARRAY);
    }

    private void setCamera() {
        cam = new Matrix4f();
        Vector3f add = getLookAtDirection().add(camPos);
        cam.setLookAt(camPos, add, new Vector3f(0, 1, 0));
        glUniformMatrix4fv(uniformLocationView, false, cam.get(mat4Buffer));
    }

    private void setPerspectiveProjection() {
        float aspect = (float) width / height;
        Matrix4f perspective = new Matrix4f().perspective(FOV, aspect, Z_NEAR, Z_FAR);
        glUniformMatrix4fv(uniformLocationProjection, false, perspective.get(mat4Buffer));
    }

    public void newObject(NetworkedPhysicsObject physicsObject) {
        PhysicsWorldEntity physicsWorldEntity = new PhysicsWorldEntity(physicsObject);
        entities.add(physicsWorldEntity);
    }


    public void syncObjects(){
        Collection<NetworkedPhysicsObject> objects = networkedPhysics.getObjects().values();
        entities.removeAll(entities);
        objects.forEach( physicsObject -> entities.add(new PhysicsWorldEntity(physicsObject)));
    }

}
