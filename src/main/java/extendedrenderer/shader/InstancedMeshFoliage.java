package extendedrenderer.shader;

import extendedrenderer.particle.ShaderManager;
import net.minecraft.client.renderer.OpenGlHelper;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;

import java.nio.FloatBuffer;

public class InstancedMeshFoliage extends Mesh {

    public static final int FLOAT_SIZE_BYTES = 4;

    public static final int VECTOR4F_SIZE_BYTES = 4 * FLOAT_SIZE_BYTES;

    public static final int MATRIX_SIZE_FLOATS = 4 * 4;

    public static final int MATRIX_SIZE_BYTES = MATRIX_SIZE_FLOATS * FLOAT_SIZE_BYTES;

    public static final int INSTANCE_SIZE_BYTES = MATRIX_SIZE_BYTES + FLOAT_SIZE_BYTES * 1/* * 2 + FLOAT_SIZE_BYTES * 2*/;

    public static final int INSTANCE_SIZE_FLOATS = MATRIX_SIZE_FLOATS + 1;// * 2 + 2;

    public static final int INSTANCE_SIZE_FLOATS_SELDOM = 4;

    public static final int INSTANCE_SIZE_BYTES_SELDOM = FLOAT_SIZE_BYTES * 4/* * 2 + FLOAT_SIZE_BYTES * 2*/;

    public final int numInstances;

    public final int instanceDataVBO;
    public final int instanceDataVBOSeldom;

    public FloatBuffer instanceDataBuffer;
    public FloatBuffer instanceDataBufferSeldom;

    public int curBufferPos = 0;

    public static int vboSizeMesh = 2;

    public InstancedMeshFoliage(float[] positions, float[] textCoords, int[] indices, int numInstances) {
        super(positions, textCoords, indices);

        this.numInstances = numInstances;

        ShaderManager.glBindVertexArray(vaoId);

        // Model View Matrix
        instanceDataVBO = GL15.glGenBuffers();
        vboIdList.add(instanceDataVBO);
        instanceDataBuffer = BufferUtils.createFloatBuffer(numInstances * INSTANCE_SIZE_FLOATS);//MemoryUtil.memAllocFloat(numInstances * INSTANCE_SIZE_FLOATS);
        OpenGlHelper.glBindBuffer(GL15.GL_ARRAY_BUFFER, instanceDataVBO);
        int start = vboSizeMesh;
        int strideStart = 0;
        for (int i = 0; i < 4; i++) {
            GL20.glVertexAttribPointer(start, 4, GL11.GL_FLOAT, false, INSTANCE_SIZE_BYTES, strideStart);
            ShaderManager.glVertexAttribDivisor(start, 1);
            start++;
            strideStart += VECTOR4F_SIZE_BYTES;
        }

        //TODO: might become UV lightmap coord in future
        //brightness
        GL20.glVertexAttribPointer(start, 1, GL11.GL_FLOAT, false, INSTANCE_SIZE_BYTES, strideStart);
        ShaderManager.glVertexAttribDivisor(start, 1);
        start++;
        strideStart += FLOAT_SIZE_BYTES;

        /**
         * TODO: rbg and alpha for colorization
         * storm darkening uses lower rgb values to darken
         * everything uses alpha for fading in and out
         *
         */

        //rgba
        /*glVertexAttribPointer(start, 4, GL_FLOAT, false, INSTANCE_SIZE_BYTES, strideStart);
        ShaderManager.glVertexAttribDivisor(start, 1);
        start++;
        strideStart += VECTOR4F_SIZE_BYTES;*/

        // Light view matrix
        /*for (int i = 0; i < 4; i++) {
            glVertexAttribPointer(start, 4, GL_FLOAT, false, INSTANCE_SIZE_BYTES, strideStart);
            glVertexAttribDivisor(start, 1);
            start++;
            strideStart += VECTOR4F_SIZE_BYTES;
        }*/

        // Texture offsets
        /*glVertexAttribPointer(start, 2, GL_FLOAT, false, INSTANCE_SIZE_BYTES, strideStart);
        glVertexAttribDivisor(start, 1);*/

        //test color to its own vbo
        instanceDataBufferSeldom = BufferUtils.createFloatBuffer(numInstances * INSTANCE_SIZE_FLOATS_SELDOM);

        FloatBuffer colorBuffer = null;
        instanceDataVBOSeldom = OpenGlHelper.glGenBuffers();
        vboIdList.add(instanceDataVBOSeldom);
        colorBuffer = BufferUtils.createFloatBuffer(4);
        float[] floats = new float[4];
        floats[0] = 1F;
        floats[1] = 1F;
        floats[2] = 1F;
        floats[3] = 1F;

        colorBuffer.put(floats).flip();
        OpenGlHelper.glBindBuffer(GL15.GL_ARRAY_BUFFER, instanceDataVBOSeldom);
        ShaderManager.glBufferData(GL15.GL_ARRAY_BUFFER, colorBuffer, GL15.GL_DYNAMIC_DRAW);
        GL20.glVertexAttribPointer(start, 4, GL11.GL_FLOAT, false, INSTANCE_SIZE_BYTES_SELDOM, 0);
        ShaderManager.glVertexAttribDivisor(start, 1);
        start++;

        OpenGlHelper.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
        ShaderManager.glBindVertexArray(0);
    }

    @Override
    public void cleanup() {
        super.cleanup();
        if (this.instanceDataBuffer != null) {
            //MemoryUtil.memFree(this.instanceDataBuffer);
            this.instanceDataBuffer = null;
        }

        if (this.instanceDataBufferSeldom != null) {
            //MemoryUtil.memFree(this.instanceDataBuffer);
            this.instanceDataBufferSeldom = null;
        }
    }

    @Override
    public void initRender() {
        super.initRender();
    }

    @Override
    public void endRender() {

        super.endRender();
    }

    public void initRenderVBO1() {
        int start = vboSizeMesh;
        //model matrix + brightness
        int numElements = 5;
        for (int i = 0; i < numElements; i++) {
            GL20.glEnableVertexAttribArray(start + i);
        }
    }

    public void endRenderVBO1() {

        int start = vboSizeMesh;
        //model matrix + brightness
        int numElements = 5;
        for (int i = 0; i < numElements; i++) {
            GL20.glDisableVertexAttribArray(start + i);
        }
    }

    public void initRenderVBO2() {
        int start = vboSizeMesh;
        //model matrix + brightness
        int numElementsVBO1 = 5;
        int numElementsVBO2 = 1;
        for (int i = 0; i < numElementsVBO2; i++) {
            GL20.glEnableVertexAttribArray(start + numElementsVBO1 + i);
        }
    }

    public void endRenderVBO2() {
        int start = vboSizeMesh;
        //model matrix + brightness
        int numElementsVBO1 = 5;
        int numElementsVBO2 = 1;
        for (int i = 0; i < numElementsVBO2; i++) {
            GL20.glDisableVertexAttribArray(start + numElementsVBO1 + i);
        }
    }
}