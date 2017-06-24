import org.lwjgl.opengl.GL11
import org.lwjgl.opengl.GL15
import org.lwjgl.opengl.GL20
import org.lwjgl.opengl.GL30
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer

public class Circle(val  x: Int, val y: Int, val r: Int){

    private var vaoId : Int = -1
    private val precision = 100

    init{
        vaoId = createCircle(x, y, r)
    }

    fun draw(){
        GL30.glBindVertexArray(vaoId)
        GL20.glEnableVertexAttribArray(0)

        GL11.glDrawArrays(GL11.GL_LINES , 0, 2*(precision + 1))

        GL20.glDisableVertexAttribArray(0)
        GL30.glBindVertexArray(0)
    }

    private fun createCircle(x: Int, y: Int, r: Int): Int{

        val rf = r.toFloat()/(SmallestCircle.windowX/2)
        val points: Array<Float> = convertPoint(x,y)

        val numSlices: Int = 10

        val circle: FloatArray = generateCirclePoints(points[0], points[1], rf, precision)

        // Generate and bind a Vertex Array
        val vaoId: Int = GL30.glGenVertexArrays()
        GL30.glBindVertexArray(vaoId)

        val buffer : ByteBuffer = ByteBuffer.allocateDirect(circle.size * 4)
        buffer.order(ByteOrder.nativeOrder())
        val fb : FloatBuffer = buffer.asFloatBuffer()
        fb.put(circle)
        fb.position(0)

        val vid: Int = GL15.glGenBuffers()
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vid)
        GL15.glBufferData(GL15.GL_ARRAY_BUFFER, fb, GL15.GL_STATIC_DRAW)

        GL20.glVertexAttribPointer(0, 2, GL11.GL_FLOAT, false, 0, 0)
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0)

        GL30.glBindVertexArray(0)

        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT)

        return vaoId
    }

    private fun convertPoint(x: Int, y: Int): Array<Float> {
        val scaleX: Int = SmallestCircle.windowX/2
        val scaleY: Int = SmallestCircle.windowY/2

        val xf: Float = (x).toFloat()/scaleX - 1
        val yf: Float = (y).toFloat()/scaleY - 1


        return arrayOf(xf, yf)
    }

    private fun generateCirclePoints(x: Float, y: Float, r: Float, slices: Int): FloatArray {
        val radius = r

        val vertexes = FloatArray(slices * 4 + 4)

        val initAngle = 0.0
        var prevXA = Math.sin(initAngle).toFloat() * radius
        var prevYA = Math.cos(initAngle).toFloat() * radius

        for (arcIndex in 0..slices) {
            var angle = Math.PI * 2.0 * arcIndex.toFloat().toDouble() / slices.toFloat()
            angle += Math.PI / 180f
            val index = arcIndex * 4

            val xa = Math.sin(angle).toFloat() * radius
            val ya = Math.cos(angle).toFloat() * radius
            vertexes[index] = x + prevXA
            vertexes[index + 1] = y + prevYA
            vertexes[index + 2] = x + xa
            vertexes[index + 3] = y + ya
            prevXA = xa
            prevYA = ya
        }

        return vertexes
    }

    fun contains(b: Circle): Boolean {
        val centerDist = Math.sqrt(Math.pow((this.x - b.x).toDouble(),2.0) + Math.pow((this.y - b.y).toDouble(),2.0))
        return (centerDist + b.r < this.r)
    }

    override fun toString(): String {
        return "x: $x, y: $y, r: $r"
    }
}