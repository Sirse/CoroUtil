package extendedrenderer.shader;

import org.lwjgl.util.vector.Quaternion;

import javax.vecmath.Vector3f;

public interface IShaderRenderedEntity {

    Vector3f getPosition();
    Quaternion getQuaternion();
    //Vector3f getScale();
    float getScale();

}
