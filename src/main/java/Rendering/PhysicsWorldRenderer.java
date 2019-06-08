/*
 * Copyright LWJGL. All rights reserved.
 * License terms: https://www.lwjgl.org/license
 */
package Rendering;

import NetworkedPhysics.Common.NetworkedPhysics;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.BufferUtils;
import org.lwjgl.glfw.*;
import org.lwjgl.opengl.*;
import org.lwjgl.system.*;

import java.io.IOException;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

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
    private Matrix4f identity;
    private GLFWCursorPosCallback mousePosCallback;
    private double camLookXRad;
    private double camLookYRad;
    private Vector3f camPos = new Vector3f(0, 0, -10);
    private double camSensivity = 200;
    //    private WavefrontMeshLoader.Mesh ship;
    private int shipPositionVbo;
    private int shipNormalVbo;
    private float camSpeed = 0.05f;
    private WorldEntity ship;
    private WavefrontMeshLoader.Mesh shipObj;

    public PhysicsWorldRenderer(NetworkedPhysics networkedPhysics) {
        this.networkedPhysics = networkedPhysics;
    }

    public void run() throws Exception {
        try {
            init();
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
                camLookYRad -= dy / camSensivity;
                camLookXRad += dx / camSensivity;

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
    }


    private void drawOBJ(BoundedObj obj) {

        glBindBuffer(GL_ARRAY_BUFFER, obj.vboVert);
        glVertexPointer(3, GL_FLOAT, 0, 0);
        glBindBuffer(GL_ARRAY_BUFFER, 0);

        glEnableClientState(GL_NORMAL_ARRAY);

        glBindBuffer(GL_ARRAY_BUFFER, obj.vboNormal);
        glNormalPointer(GL_FLOAT, 0, 0);
        glBindBuffer(GL_ARRAY_BUFFER, 0);

        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, obj.vboIndices);
        int numFaces = obj.obj.getNumFaces();
        glDrawElements(GL_TRIANGLES, numFaces * 3, GL_UNSIGNED_INT, 0);

        glDisableClientState(GL_NORMAL_ARRAY);
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

    private void loop() throws Exception {
        // This line is critical for LWJGL's interoperation with GLFW's
        // OpenGL context, or any context that is managed externally.
        // LWJGL detects the context that is current in the current thread,
        // creates the ContextCapabilities instance and makes the OpenGL
        // bindings available for use.
        GL.createCapabilities();
        glEnable(GL_DEPTH_TEST);

        debugProc = GLUtil.setupDebugMessageCallback();

        // Set the clear color
        glClearColor(0.0f, 0.0f, 0.0f, 0.0f);

        int vbo = glGenBuffers();
        int ibo = glGenBuffers();
        float[] vertices =
                {-0.5f, -0.5f, -1f,
                        0.5f, -0.5f, -1f,
                        0.5f, 0.5f, -1f};

        int[] indices = {0, 1, 2};

        glBindBuffer(GL_ARRAY_BUFFER, vbo);
        glBufferData(GL_ARRAY_BUFFER, (FloatBuffer) BufferUtils.createFloatBuffer(vertices.length).put(vertices).flip(), GL_STATIC_DRAW);
        glEnableClientState(GL_VERTEX_ARRAY);
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, ibo);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, (IntBuffer) BufferUtils.createIntBuffer(indices.length).put(indices).flip(), GL_STATIC_DRAW);
        glVertexPointer(3, GL_FLOAT, 0, 0L);

        shaderProgram = new ShaderProgram();
        shaderProgram.createVertexShader(Utils.loadResource("/vertex.vs"));
        shaderProgram.createFragmentShader(Utils.loadResource("/fragment.fs"));
        shaderProgram.link();
        shaderProgram.bind();

        uniformLocationProjection = shaderProgram.getUniformLocation("proj");
        uniformLocationView = shaderProgram.getUniformLocation("view");
        uniformLocationModel = shaderProgram.getUniformLocation("model");

        identity = new Matrix4f();
        glUniformMatrix4fv(uniformLocationModel, false, identity.get(mat4Buffer));
        ship = new WorldEntity(new BoundedObj(System.class.getResource("/dice.obj").openStream()));
        //glfwSetCursorPos(window,width/2, height/2);

        // Run the rendering loop until the user has attempted to close
        // the window or has pressed the ESCAPE key.
        printVector(getLookAtDirection());


        while (!glfwWindowShouldClose(window)) {
            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT); // clear the framebuffer
            glViewport(0, 0, width, height);

            updateControlls();

            setPerspectiveProjection();
            setCamera();

//            drawShip();
            drawOBJ(ship.boundedObj);
            //glDrawElements(GL_TRIANGLES, 3, GL_UNSIGNED_INT, 0L);

            glfwSwapBuffers(window); // swap the color buffers

            // Poll for window events. The key callback above will only be
            // invoked during this call.
            glfwPollEvents();
        }
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

    public static void main(String[] args) throws Exception {
        new PhysicsWorldRenderer(null).run();
    }
}
