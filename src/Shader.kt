import org.lwjgl.BufferUtils
import org.lwjgl.opengl.GL11
import org.lwjgl.opengl.GL20
import java.io.File
import java.nio.FloatBuffer


class Shader(val vertexShaderPath: String, val fragmentShaderPath: String){

    var shaderProgram: Int = -1

    init{
        initializeShaders()
    }

    fun initializeShaders(){
        val vertexShaderSource = File(vertexShaderPath).readText(Charsets.UTF_8)
        val vertexShader = GL20.glCreateShader(GL20.GL_VERTEX_SHADER)
        GL20.glShaderSource(vertexShader, vertexShaderSource)
        GL20.glCompileShader(vertexShader)
        checkForCompileErrors(vertexShader, "vertex")

        val fragmentShaderSource = File(fragmentShaderPath).readText(Charsets.UTF_8)
        val fragmentShader = GL20.glCreateShader(GL20.GL_FRAGMENT_SHADER)
        GL20.glShaderSource(fragmentShader, fragmentShaderSource)
        GL20.glCompileShader(fragmentShader)
        checkForCompileErrors(fragmentShader, "fragment")

        shaderProgram = GL20.glCreateProgram()
        GL20.glAttachShader(shaderProgram, vertexShader)
        GL20.glAttachShader(shaderProgram, fragmentShader)

        GL20.glLinkProgram(shaderProgram)

        GL20.glDeleteShader(vertexShader)
        GL20.glDeleteShader(fragmentShader)
    }

    fun checkForCompileErrors(id: Int, shaderType: String){
        var status: IntArray = IntArray(1)
        GL20.glGetShaderiv(id, GL20.GL_COMPILE_STATUS, status)
        if(status[0] == GL11.GL_FALSE){
            val infoLog = GL20.glGetShaderInfoLog(id, 512)
            println("Compilation error! with type: $shaderType: $infoLog")
        }
    }

    fun checkForLinkErrors(id: Int){
        var status: IntArray = IntArray(0)
        GL20.glGetProgramiv(id, GL20.GL_LINK_STATUS, status)

        if(status[0] == GL11.GL_FALSE){
            val infoLog = GL20.glGetShaderInfoLog(id, 512)
            println("Linker error!" + infoLog)
        }
    }

    fun use(){
        var colorLocation: Int = -1
        colorLocation = GL20.glGetUniformLocation(shaderProgram, "ourColor")
        GL20.glUseProgram(shaderProgram)
        val color = BufferUtils.createFloatBuffer(4).put(0.0f).put(1.0f).put(0.0f).put(1.0f)
        color.flip()
        GL20.glUniform4fv(colorLocation, color)
    }

    fun setColor(color: FloatBuffer){
        val colorLocation = GL20.glGetUniformLocation(shaderProgram, "ourColor")
        GL20.glUniform4fv(colorLocation, color)
    }
}