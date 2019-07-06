/*
 * Copyright LWJGL. All rights reserved.
 * License terms: https://www.lwjgl.org/license
 */
package Rendering;

import NetworkedPhysics.Common.Protocol.clientCommands.InputArguments;
import NetworkedPhysics.Common.Protocol.serverCommands.WorldState;
import NetworkedPhysics.Common.RewindablePhysicsWorld;
import NetworkedPhysics.Common.PhysicsObject;
import NetworkedPhysics.NetworkedPhysicsClient;
import Util.Utils;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.BufferUtils;
import org.lwjgl.glfw.*;
import org.lwjgl.opengl.*;
import org.lwjgl.system.*;

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

    private RewindablePhysicsWorld rewindablePhysicsWorld;
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
    private Vector3f camPos = new Vector3f(0, 0, -20);
    private double camSensitivity = 200;
    private float camSpeed = 0.10f;
    private List<PhysicsWorldEntity> entities = new ArrayList<>();

    private FPS fps = new FPS();
    private WorldState ws;
    private double lastX, lastY;
    private boolean camMode2 = false;


    private NetworkedPhysicsClient game;

    public PhysicsWorldRenderer(RewindablePhysicsWorld rewindablePhysicsWorld) throws Exception {
        init();
        setNetworkedPhysics(rewindablePhysicsWorld);
    }

    public PhysicsWorldRenderer() throws Exception {
        init();
    }

    public void free() {
        // Release window and window callbacks
        glfwDestroyWindow(window);
        keyCallback.free();
        wsCallback.free();
        if (debugProc != null)
            debugProc.free();
        glfwTerminate();
        errorCallback.free();
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
        glfwWindowHint(GLFW_SAMPLES, 4);

        int WIDTH = 600;
        int HEIGHT = 600;

        // Create the window
        window = glfwCreateWindow(WIDTH, HEIGHT, "Physics World Renderer", NULL, NULL);
        if (window == NULL)
            throw new RuntimeException("Failed to create the GLFW window");
        glfwMakeContextCurrent(window);
        // Setup a key callback. It will be called every time a key is pressed, repeated or released.
        glfwSetKeyCallback(window, keyCallback = new GLFWKeyCallback() {
            public void invoke(long window, int key, int scancode, int action, int mods) {
                if (key == GLFW_KEY_UNKNOWN) {
                    return;
                }
                if (game != null && key == GLFW_KEY_U && action == GLFW_RELEASE) {
                    InputArguments clientInput = new InputArguments();
                    clientInput.click=false;
                    game.sendMyInput(clientInput);
                }
                if (key == GLFW_KEY_ESCAPE && action == GLFW_RELEASE) {
                    glfwSetWindowShouldClose(window, true);
                }
                if (action == GLFW_PRESS || action == GLFW_REPEAT) {
                    keyDown[key] = true;
                    if (game != null && key == GLFW_KEY_U) {
                        InputArguments clientInput = new InputArguments();
                        clientInput.click=true;
                        game.sendMyInput(clientInput);
                    }
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
                //glfwSetCursorPos(window, width / 2, height / 2);
                double dx = 0;
                double dy = 0;

                if (camMode2) {
                    glfwSetCursorPos(window, width / 2, height / 2);
                    dx = width / 2 - x;
                    dy = height / 2 - y;
                }

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
        glEnable(GL_MULTISAMPLE);

        debugProc = GLUtil.setupDebugMessageCallback();

        // Set the clear color
        glClearColor(66f / 255f, 134f / 255f, 244f / 255f, 1f);

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
        if (keyDown[GLFW_KEY_C]) {
            camMode2 = !camMode2;
        }
    }

    public void renderAFrame() {
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT); // clear the framebuffer
        glViewport(0, 0, width, height);
        drawCoordinateSystem();
        glFlush();
        entities.forEach(this::drawWorldEntity);
        glfwSwapBuffers(window);
        fps.tick();
    }

    public void update() {
        glfwMakeContextCurrent(window);
        updateControlls();
        setCamera();
        renderAFrame();
    }

    public boolean shouldClose() {
        return glfwWindowShouldClose(window);
    }

    private void rewindAtStep300to100() {
        int step = rewindablePhysicsWorld.getStep();
        //            int step=rewindablePhysicsWorld.step();
        if (step > 100 && ws == null) {
            ws = new WorldState();
            rewindablePhysicsWorld.saveState();
            System.out.println("saved world state");
        }

        if (step > 300) {
            rewindablePhysicsWorld.rewindToLastState();
            System.out.println("restored world??");
        }
    }

    private void drawWorldEntity(PhysicsWorldEntity worldEntity) {
        worldEntity.getShader().bind();
        glUniformMatrix4fv(uniformLocationModel, false, worldEntity.getModel().get(mat4Buffer));

        glEnableClientState(GL_NORMAL_ARRAY);

        glBindBuffer(GL_ARRAY_BUFFER, worldEntity.boundedObj.vboVert);
        glVertexPointer(3, GL_FLOAT, 0, 0);
        glBindBuffer(GL_ARRAY_BUFFER, 0);

        glBindBuffer(GL_ARRAY_BUFFER, worldEntity.boundedObj.vboNormal);
        glNormalPointer(GL_FLOAT, 0, 0);
        glBindBuffer(GL_ARRAY_BUFFER, 0);

        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, worldEntity.boundedObj.vboIndices);
        int faceIndicesCount = worldEntity.boundedObj.faceIndicesCount;
        glDrawElements(GL_TRIANGLES, faceIndicesCount, GL_UNSIGNED_INT, 0);
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

    public void newObject(int physicsObject) {
        if (rewindablePhysicsWorld == null)
            return;
        PhysicsObject object = rewindablePhysicsWorld.getObject(physicsObject);
        if (object == null) {
            return;
        }
        PhysicsWorldEntity physicsWorldEntity = new PhysicsWorldEntity(object, shaderProgram);
        entities.add(physicsWorldEntity);

    }


    public void syncObjects() {
        Collection<PhysicsObject> objects = rewindablePhysicsWorld.getObjects().values();
        entities.removeAll(entities);
        objects.forEach(physicsObject -> entities.add(new PhysicsWorldEntity(physicsObject, shaderProgram)));
    }

    private void drawCoordinateSystem() {
        glUniformMatrix4fv(uniformLocationModel, false, new Matrix4f().get(mat4Buffer));
        glColor3f(1.0f, 0.0f, 0.0f); // red x
        glBegin(GL_LINES);
        // x aix

        glVertex3f(-4.0f, 0.0f, 0.0f);
        glVertex3f(4.0f, 0.0f, 0.0f);

        // arrow
        glVertex3f(4.0f, 0.0f, 0.0f);
        glVertex3f(3.0f, 1.0f, 0.0f);

        glVertex3f(4.0f, 0.0f, 0.0f);
        glVertex3f(3.0f, -1.0f, 0.0f);
        glEnd();


        // y
        glColor3f(0.0f, 1.0f, 0.0f); // green y
        glBegin(GL_LINES);
        glVertex3f(0.0f, -4.0f, 0.0f);
        glVertex3f(0.0f, 4.0f, 0.0f);

        // arrow
        glVertex3f(0.0f, 4.0f, 0.0f);
        glVertex3f(1.0f, 3.0f, 0.0f);

        glVertex3f(0.0f, 4.0f, 0.0f);
        glVertex3f(-1.0f, 3.0f, 0.0f);
        glEnd();

        // z
        glColor3f(0.0f, 0.0f, 1.0f); // blue z
        glBegin(GL_LINES);
        glVertex3f(0.0f, 0.0f, -4.0f);
        glVertex3f(0.0f, 0.0f, 4.0f);

        // arrow
        glVertex3f(0.0f, 0.0f, 4.0f);
        glVertex3f(0.0f, 1.0f, 3.0f);

        glVertex3f(0.0f, 0.0f, 4.0f);
        glVertex3f(0.0f, -1.0f, 3.0f);
        glEnd();
    }

    public void setNetworkedPhysics(RewindablePhysicsWorld networkedPhysicsClient) throws Exception {
        this.rewindablePhysicsWorld = networkedPhysicsClient;
        syncObjects();
    }

    public void setGame(NetworkedPhysicsClient game) {
        this.game = game;
    }
}
