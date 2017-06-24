import org.lwjgl.*
import org.lwjgl.glfw.*

import org.lwjgl.system.*

import java.nio.*

import org.lwjgl.glfw.Callbacks.*
import org.lwjgl.glfw.GLFW.*
import org.lwjgl.opengl.GL11.*
import org.lwjgl.system.MemoryStack.*
import org.lwjgl.system.MemoryUtil.*
import org.lwjgl.glfw.GLFW.glfwPollEvents
import org.lwjgl.glfw.GLFW.glfwSwapBuffers
import org.lwjgl.glfw.GLFW.glfwWindowShouldClose
import org.lwjgl.opengl.GL
import org.lwjgl.glfw.GLFW.glfwSetWindowShouldClose
import org.lwjgl.glfw.GLFW.GLFW_RELEASE
import org.lwjgl.glfw.GLFW.GLFW_KEY_ESCAPE
import org.lwjgl.glfw.GLFW.glfwSetKeyCallback
import java.util.*


class SmallestCircle{
    companion object Factory {
        val windowX: Int = 1000
        val windowY: Int = 1000
    }

    var window: Long = 0
    var circles: MutableList<Circle> = mutableListOf()
    var smallestCircle: MutableList<Circle> = mutableListOf()


    fun run(){
        println("Hello LWJGL " + Version.getVersion() + "!")

        init()
        loop()

        glfwFreeCallbacks(window)
        glfwDestroyWindow(window)

        glfwTerminate()
        glfwSetErrorCallback(null).free()
    }

    fun init(){
        // Setup an error callback. The default implementation
        // will print the error message in System.err.
        GLFWErrorCallback.createPrint(System.err).set();

        // Initialize GLFW. Most GLFW functions will not work before doing this.
        if ( !glfwInit() )
            throw IllegalStateException("Unable to initialize GLFW");

        // Configure GLFW
        glfwDefaultWindowHints(); // optional, the current window hints are already the default
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE); // the window will stay hidden after creation
        glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE); // the window will be resizable

        // Create the window
        window = glfwCreateWindow(windowX, windowY, "Hello World!", NULL, NULL);
        if ( window == NULL )
            throw RuntimeException("Failed to create the GLFW window");

        // Setup a key callback. It will be called every time a key is pressed, repeated or released.

        glfwSetKeyCallback(window) { window, key, scancode, action, mods ->
            if (key == GLFW_KEY_ESCAPE && action == GLFW_RELEASE)
                glfwSetWindowShouldClose(window, true) // We will detect this in the rendering loop
        }

        // Get the thread stack and push a new frame
        try {
            val stack : MemoryStack = stackPush()
            val pWidth: IntBuffer = stack.mallocInt(1); // int*
            val pHeight: IntBuffer = stack.mallocInt(1); // int*

            // Get the window size passed to glfwCreateWindow
            glfwGetWindowSize(window, pWidth, pHeight);

            // Get the resolution of the primary monitor
            val vidmode: GLFWVidMode = glfwGetVideoMode(glfwGetPrimaryMonitor());

            // Center the window
            glfwSetWindowPos(
                    window,
                    (vidmode.width() - pWidth.get(0)) / 2,
                    (vidmode.height() - pHeight.get(0)) / 2
            )

        } catch(e : Exception){
            println(e)
        } // the stack frame is popped automatically

        // Make the OpenGL context current
        glfwMakeContextCurrent(window);
        // Enable v-sync
        glfwSwapInterval(1);

        // Make the window visible
        glfwShowWindow(window);
    }

    fun loop(){
        // This line is critical for LWJGL's interoperation with GLFW's
        // OpenGL context, or any context that is managed externally.
        // LWJGL detects the context that is current in the current thread,
        // creates the GLCapabilities instance and makes the OpenGL
        // bindings available for use.
        GL.createCapabilities()

        // Set the clear color
        glClearColor(0.2f, 0.3f, 0.3f, 1.0f)

        // Run the rendering loop until the user has attempted to close
        // the window or has pressed the ESCAPE key.

        createCircles()
        val shader = Shader("src/shader.vs", "src/shader.fs")

        while (!glfwWindowShouldClose(window)) {
            glClear(GL_COLOR_BUFFER_BIT or GL_DEPTH_BUFFER_BIT) // clear the framebuffer

            shader.use()
            val red = BufferUtils.createFloatBuffer(4).put(1.0f).put(0.0f).put(0.0f).put(0.0f)
            red.flip()
            shader.setColor(red)
            drawCircles()

            val green = BufferUtils.createFloatBuffer(4).put(0.0f).put(1.0f).put(0.0f).put(0.0f)
            green.flip()
            shader.setColor(green)
            drawSmallestCircle()


            glfwSwapBuffers(window) // swap the color buffers

            // Poll for window events. The key callback above will only be
            // invoked during this call.
            glfwPollEvents()
        }
    }

    fun createCircles(){
        val numCircles = 2
        for(i in 1..numCircles){
            val random = Random()
//            val x = 400 + 100*i
//            val y = 400 + 100*i
//            val r = 50
            val x = random.nextInt(500) + 300
            val y = random.nextInt(500) + 300
            val r = random.nextInt(200) + 50
            circles.add(Circle(x,y,r))
        }
        smallestCircle.add(smallestTwoCircle(circles[0],circles[1]))
    }

    fun drawCircles(){
        for(circle in circles){
            circle.draw()
        }
    }

    fun drawSmallestCircle(){
        for(circle in smallestCircle){
            circle.draw()
        }
    }

    fun smallestTwoCircle(a: Circle, b: Circle) : Circle {
        if(a.r > b.r){
            if(a.contains(b)){
                return Circle(a.x, a.y, a.r)
            }
        } else {
            if(b.contains(a)){
                return Circle(b.x, b.y, b.r)
            }
        }
        val arx: Double
        val ary: Double
        val brx: Double
        val bry: Double
        val signX = if(b.x > a.x) 1 else -1
        val signY = if(b.y > a.y) 1 else -1
        if(a.x == b.x){
            arx = 0.0
            ary = a.r.toDouble()*-1*signY

            brx = 0.0
            bry = b.r.toDouble()*signY
        } else if(a.y == b.y){
            arx = a.r.toDouble()*-1*signX
            ary = 0.0

            brx = b.r.toDouble()*signX
            bry = 0.0
        } else {
            val m = (b.y - a.y).toDouble() /  (b.x - a.x).toDouble()
            arx = a.r.toDouble()/Math.sqrt((m*m+1)) *(-1*signX)
            ary = Math.abs(arx*m) * (-1*signY)

            brx = b.r.toDouble()/Math.sqrt((m*m+1)) * (signX)
            bry = Math.abs(brx*m)*signY
        }


        val cx = (a.x + arx + b.x + brx)/2
        val cy = (a.y + ary + b.y + bry)/2
        val cr = Math.sqrt(Math.pow((cx - (a.x + arx)), 2.0) + Math.pow(cy - (a.y + ary), 2.0))


        val ret = Circle(cx.toInt(), cy.toInt(), Math.ceil(cr).toInt())
        println(ret)
        return ret
    }
}
/**
 * Created by viggy on 6/18/2017.
 */
