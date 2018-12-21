package com.example.chenkengyu.test;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Point;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.GLUtils;
import android.opengl.Matrix;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

import static android.opengl.Matrix.orthoM;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import static android.opengl.GLES20.*;
import static android.opengl.Matrix.perspectiveM;

public class EGLActivity extends AppCompatActivity {

    private EGLRenderer renderer;
    private GLSurfaceView demoGlv;
    private float angleX = 0;
    private float angleY = 0;
    private int screenHeight;
    private int screenWidth;

    enum OperationType {
        TRANSLATION,
        ROTATION
    }

    private OperationType[] operationTypes = new OperationType[]{OperationType.TRANSLATION, OperationType.ROTATION};

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_egl);

        Resources resources = this.getResources();
        DisplayMetrics dm = resources.getDisplayMetrics();
        screenWidth = dm.widthPixels;
        screenHeight = dm.heightPixels;

        demoGlv = (GLSurfaceView) findViewById(R.id.glsv);

        // 设置OpenGL版本(一定要设置)
        demoGlv.setEGLContextClientVersion(2);
        // 设置渲染器(后面会着重讲这个渲染器的类)
        renderer = new EGLRenderer();
        demoGlv.setRenderer(renderer);
        // 设置渲染模式为连续模式(会以60fps的速度刷新)
        demoGlv.setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);

        demoGlv.setOnTouchListener(new View.OnTouchListener() {
            float sx;
            float sy;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    sx = event.getX();
                    sy = event.getY();
                } else if (event.getAction() == MotionEvent.ACTION_MOVE) {
                    float dx = event.getX() - sx;
                    float dy = event.getY() - sy;
                    sx = event.getX();
                    sy = event.getY();
//
//                    if (((sy > screenHeight / 2))) {
//                        renderer.translate(dx / (float) screenWidth, dy / (float) screenHeight);
//                        return true;
//                    }

                    if (angleX < 180f || angleX > -180f)
                        angleX += dx / ((float) screenWidth) * 180f;
                    if (angleY < 180f || angleY > -180f)
                        angleY += dy / ((float) screenHeight) * 180f;

                } else if (event.getAction() == MotionEvent.ACTION_UP) {
//                    angleX = 0;
//                    angleY = 0;
                }
                return true;
            }
        });
    }

    int index = 0;
    public void onClick(View view) {
        float[] directions = new float[]{0.1f, -0.1f, -0.1f, 0.1f};
        renderer.translate(directions[index % 4], directions[index % 2]);
        index++;
    }


    private class EGLRenderer implements GLSurfaceView.Renderer {

        private int program;
        private int vPosition;
        private int vNormalPosition;
        private int uLightPosition;
        private int Kd;
        private int Ld;
        private int ModelViewMatrix;
        private int NormalMatrix;
        private int uColor;
        private int texture;
        private int uTextureUnitLocation;
        private int aTextureCoordinatesLocation;
        private float[] projectionMatrix = new float[16];
        private int uMatrixLocation;

        private float y = 0f;
        private float x = 0f;

        float l = 0.2f;
        final float[] cubePosition = {
                        // In OpenGL counter-clockwise winding is default. This means that when we look at a triangle,
                        // if the points are counter-clockwise we are looking at the "front". If not we are looking at
                        // the back. OpenGL has an optimization where all back-facing triangles are culled, since they
                        // usually represent the backside of an object and aren't visible anyways.

                        // Front face
                        -l, l, l,
                        -l, -l, l,
                        l, l, l,
                        -l, -l, l,
                        l, -l, l,
                        l, l, l,

                        // Right face
                        l, l, l,
                        l, -l, l,
                        l, l, -l,
                        l, -l, l,
                        l, -l, -l,
                        l, l, -l,

                        // Back face
                        l, l, -l,
                        l, -l, -l,
                        -l, l, -l,
                        l, -l, -l,
                        -l, -l, -l,
                        -l, l, -l,

                        // Left face
                        -l, l, -l,
                        -l, -l, -l,
                        -l, l, l,
                        -l, -l, -l,
                        -l, -l, l,
                        -l, l, l,

                        // Top face
                        -l, l, -l,
                        -l, l, l,
                        l, l, -l,
                        -l, l, l,
                        l, l, l,
                        l, l, -l,

                        // Bottom face
                        l, -l, -l,
                        l, -l, l,
                        -l, -l, -l,
                        l, -l, l,
                        -l, -l, l,
                        -l, -l, -l,
                };

        float[] textruePosition = new float[]{
                // Front face
                0.0f, 0.0f,
                0.0f, 1.0f,
                1.0f, 0.0f,
                0.0f, 1.0f,
                1.0f, 1.0f,
                1.0f, 0.0f,

                // Right face
                0.0f, 0.0f,
                0.0f, 1.0f,
                1.0f, 0.0f,
                0.0f, 1.0f,
                1.0f, 1.0f,
                1.0f, 0.0f,

                // Back face
                0.0f, 0.0f,
                0.0f, 1.0f,
                1.0f, 0.0f,
                0.0f, 1.0f,
                1.0f, 1.0f,
                1.0f, 0.0f,

                // Left face
                0.0f, 0.0f,
                0.0f, 1.0f,
                1.0f, 0.0f,
                0.0f, 1.0f,
                1.0f, 1.0f,
                1.0f, 0.0f,

                // Top face
                0.0f, 0.0f,
                0.0f, 1.0f,
                1.0f, 0.0f,
                0.0f, 1.0f,
                1.0f, 1.0f,
                1.0f, 0.0f,

                // Bottom face
                0.0f, 0.0f,
                0.0f, 1.0f,
                1.0f, 0.0f,
                0.0f, 1.0f,
                1.0f, 1.0f,
                1.0f, 0.0f
        };
        private float[] normalPosition = new float[]{
                // Front face
                0, 0, 1,
                0, 0, 1,
                0, 0, 1,
                0, 0, 1,
                0, 0, 1,
                0, 0, 1,

                // Right face
                1, 0, 0,
                1, 0, 0,
                1, 0, 0,
                1, 0, 0,
                1, 0, 0,
                1, 0, 0,

                // Back face
                0, 0, -1,
                0, 0, -1,
                0, 0, -1,
                0, 0, -1,
                0, 0, -1,
                0, 0, -1,

                // Left face
                -1, 0, 0,
                -1, 0, 0,
                -1, 0, 0,
                -1, 0, 0,
                -1, 0, 0,
                -1, 0, 0,

                // Top face
                0, 1, 0,
                0, 1, 0,
                0, 1, 0,
                0, 1, 0,
                0, 1, 0,
                0, 1, 0,

                // Bottom face
                0, -1, 0,
                0, -1, 0,
                0, -1, 0,
                0, -1, 0,
                0, -1, 0,
                0, -1, 0
        };

        public void translate(float x, float y) {
            this.x += x;
            this.y -= y;
        }

        /**
         * 加载制定shader的方法
         *
         * @param shaderType shader的类型  GLES20.GL_VERTEX_SHADER   GLES20.GL_FRAGMENT_SHADER
         * @param sourceCode shader的脚本
         * @return shader索引
         */
        private int loadShader(int shaderType, String sourceCode) {
            // 创建一个新shader
            int shader = GLES20.glCreateShader(shaderType);
            // 若创建成功则加载shader
            if (shader != 0) {
                // 加载shader的源代码
                GLES20.glShaderSource(shader, sourceCode);
                // 编译shader
                GLES20.glCompileShader(shader);
            }
            return shader;
        }

        /**
         * 创建shader程序的方法
         */
        private int createProgram(String vertexSource, String fragmentSource) {
            //加载顶点着色器
            int vertexShader = loadShader(GLES20.GL_VERTEX_SHADER, vertexSource);
            if (vertexShader == 0) {
                return 0;
            }

            // 加载片元着色器
            int pixelShader = loadShader(GLES20.GL_FRAGMENT_SHADER, fragmentSource);
            if (pixelShader == 0) {
                return 0;
            }

            // 创建程序
            int program = GLES20.glCreateProgram();
            // 若程序创建成功则向程序中加入顶点着色器与片元着色器
            if (program != 0) {
                // 向程序中加入顶点着色器
                GLES20.glAttachShader(program, vertexShader);
                // 向程序中加入片元着色器
                GLES20.glAttachShader(program, pixelShader);
                // 链接程序
                GLES20.glLinkProgram(program);
            }
            uMatrixLocation = glGetUniformLocation(program, "u_Matrix");
            return program;
        }

        /**
         * 获取图形的顶点
         * 特别提示：由于不同平台字节顺序不同数据单元不是字节的一定要经过ByteBuffer
         * 转换，关键是要通过ByteOrder设置nativeOrder()，否则有可能会出问题
         *
         * @return 顶点Buffer
         */

        private FloatBuffer getFBVertices(float[] vertices) {
            // 创建顶点坐标数据缓冲
            // vertices.length*4是因为一个float占四个字节
            ByteBuffer vbb = ByteBuffer.allocateDirect(vertices.length * 4);
            vbb.order(ByteOrder.nativeOrder());             //设置字节顺序
            FloatBuffer vertexBuf = vbb.asFloatBuffer();    //转换为Float型缓冲
            vertexBuf.put(vertices);                        //向缓冲区中放入顶点坐标数据
            vertexBuf.position(0);                          //设置缓冲区起始位置

            return vertexBuf;
        }

        /**
         * 当GLSurfaceView中的Surface被创建的时候(界面显示)回调此方法，一般在这里做一些初始化
         *
         * @param gl10      1.0版本的OpenGL对象，这里用于兼容老版本，用处不大
         * @param eglConfig egl的配置信息(GLSurfaceView会自动创建egl，这里可以先忽略)
         */
        @Override
        public void onSurfaceCreated(GL10 gl10, EGLConfig eglConfig) {
            Log.i("GL_TAG", "onSurfaceCreated");
            // 初始化着色器
            // 基于顶点着色器与片元着色器创建程序
            program = createProgram(verticesShader, fragmentShader);
            // 获取着色器中的属性引用id(传入的字符串就是我们着色器脚本中的属性名)
            vPosition = GLES20.glGetAttribLocation(program, "vPosition");

            vNormalPosition = GLES20.glGetAttribLocation(program, "vNormalPosition");
            uLightPosition = GLES20.glGetUniformLocation(program, "LightPosition");
            Kd = GLES20.glGetUniformLocation(program, "Kd");
            Ld = GLES20.glGetUniformLocation(program, "Ld");
            ModelViewMatrix = GLES20.glGetUniformLocation(program, "ModelViewMatrix");
            NormalMatrix = GLES20.glGetUniformLocation(program, "NormalMatrix");

            uColor = GLES20.glGetUniformLocation(program, "uColor");
            uTextureUnitLocation = GLES20.glGetUniformLocation(program, "u_TextureUnit");
            aTextureCoordinatesLocation = GLES20.glGetAttribLocation(program, "a_TextureCoordinates");

            // 设置clear color颜色RGBA(这里仅仅是设置清屏时GLES20.glClear()用的颜色值而不是执行清屏)
            GLES20.glClearColor(1.0f, 1, 0.5f, 1.0f);
            GLES20.glUseProgram(program);

            GLES20.glEnable(GLES20.GL_DEPTH_TEST);
            // Use culling to remove back faces.
            GLES20.glEnable(GLES20.GL_CULL_FACE);

//            // 为画笔指定顶点位置数据(vPosition)
//            GLES20.glVertexAttribPointer(vPosition, 2, GLES20.GL_FLOAT, false, 0, vertices);
//            // 允许顶点位置数据数组
//            GLES20.glEnableVertexAttribArray(vPosition);
//            // 设置属性uColor(颜色 索引,R,G,B,A)

            GLES20.glUniform3f(Kd, 1.0f, 1.0f, 1.0f);
            GLES20.glUniform3f(Ld, 1.0f, 1.0f, 1.0f);
            GLES20.glUniform4f(uLightPosition, 0.3f, 0.7f, 1f, 1.0f);
            glVertexAttribPointer(vNormalPosition, 3, GL_FLOAT, false, 0, getFBVertices(normalPosition));
            glEnableVertexAttribArray(vNormalPosition);
            //vNormalPosition

//            //激活纹理单元，GL_TEXTURE0代表纹理单元0，GL_TEXTURE1代表纹理单元1，以此类推。OpenGL使用纹理单元来表示被绘制的纹理
            texture = loadTexture(EGLActivity.this, R.drawable.winter_outfits);
            glActiveTexture(GL_TEXTURE0);
            //绑定纹理到这个纹理单元
            glBindTexture(GL_TEXTURE_2D, texture);
            //把选定的纹理单元传给片段着色器中的u_TextureUnit，
            glUniform1i(uTextureUnitLocation, 0);
            glVertexAttribPointer(aTextureCoordinatesLocation, 2, GL_FLOAT, false, 0, getFBVertices(textruePosition));
            glEnableVertexAttribArray(aTextureCoordinatesLocation);

            glVertexAttribPointer(vPosition, 3, GL_FLOAT, false, 0, getFBVertices(cubePosition));
            glEnableVertexAttribArray(vPosition);


        }


        /**
         * 当GLSurfaceView中的Surface被改变的时候回调此方法(一般是大小变化)
         *
         * @param gl10   同onSurfaceCreated()
         * @param width  Surface的宽度
         * @param height Surface的高度
         */
        @Override
        public void onSurfaceChanged(GL10 gl10, int width, int height) {
            Log.i("GL_TAG", "onSurfaceChanged");
            // 设置绘图的窗口(可以理解成在画布上划出一块区域来画图)
            glViewport(0, 0, width, height);
            final float aspectRatio = width > height ?
                    (float) width / (float) height :
                    (float) height / (float) width;

            if (width > height) {
                // Landscape
                orthoM(projectionMatrix, 0, -aspectRatio, aspectRatio, -1f, 1f, -1, 1);
            } else {
                // Portrait or square
                orthoM(projectionMatrix, 0, -1f, 1f, -aspectRatio, aspectRatio, -1, 1);
            }
        }


        /**
         * 当Surface需要绘制的时候回调此方法
         * 根据GLSurfaceView.setRenderMode()设置的渲染模式不同回调的策略也不同：
         * GLSurfaceView.RENDERMODE_CONTINUOUSLY : 固定一秒回调60次(60fps)
         * GLSurfaceView.RENDERMODE_WHEN_DIRTY   : 当调用GLSurfaceView.requestRender()之后回调一次
         *
         * @param gl10 同onSurfaceCreated()
         */
        @Override
        public void onDrawFrame(GL10 gl10) {
            Log.i("GL_TAG", "onDrawFrame");
            GLES20.glClear(GLES20.GL_DEPTH_BUFFER_BIT | GLES20.GL_COLOR_BUFFER_BIT);
            drawCube(0, 0);
        }

        private void drawCube(float offsetX, float offsetY) {
            float[] MVPM = new float[16];
            float[] MM = new float[16];
            float[] VM = new float[16];
            float[] MVM = new float[16];
            Matrix.setIdentityM(MM, 0);
            Matrix.translateM(MM, 0, x + offsetX, y + offsetY, 0);
            Matrix.rotateM(MM, 0, angleY, 1, 0, 0);
            Matrix.rotateM(MM, 0, angleX, 0, 1, 0);
            Matrix.setLookAtM(VM, 0, 0, 0, 0, -1, -0.5f, -1, 0, 1, 0);
            Matrix.multiplyMM(MVM, 0, VM, 0, MM, 0);
            Matrix.multiplyMM(MVPM, 0, projectionMatrix, 0, MVM, 0);

            GLES20.glUniformMatrix4fv(uMatrixLocation, 1, false, MVPM, 0);

            GLES20.glUniformMatrix4fv(ModelViewMatrix, 1, false, MM, 0);

            GLES20.glUniformMatrix3fv(NormalMatrix, 1, false, mat4ToMat3(MM), 0);

            // 绘制
            GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, 36);
        }

        private float[] mat4ToMat3(float[] input) {
            if (input.length == 16) {
                float[] out = new float[9];
                out[0] = input[0];
                out[1] = input[1];
                out[2] = input[2];
                out[3] = input[4];
                out[4] = input[5];
                out[5] = input[6];
                out[6] = input[8];
                out[7] = input[9];
                out[8] = input[10];
                return out;
            }
            return null;
        }

        // 顶点着色器的脚本
        private static final String verticesShader
                = "attribute vec3 vPosition;            \n" // 顶点位置属性vPosition
                + "attribute vec3 vNormalPosition;            \n"
                + "attribute vec2 a_TextureCoordinates; \n"
                + "varying vec2 v_TextureCoordinates; \n"
                + "varying vec3 LightIntensity;  \n"
                + "uniform vec4 LightPosition;  \n"
                + "uniform vec3 Kd;             \n"
                + "uniform vec3 Ld;            \n"
                + "uniform mat4 ModelViewMatrix;\n"
                + "uniform mat3 NormalMatrix;\n"
                + "uniform mat4 u_Matrix; \n"
                + "void main(){                         \n"
                + "vec3 tnorm = normalize( NormalMatrix * vNormalPosition); \n"
                + "vec4 eyeCoords = ModelViewMatrix * vec4(vPosition,1.0);\n"
                + "vec3 s = normalize(vec3(LightPosition - eyeCoords));\n"
                + "LightIntensity = Ld * Kd * max( dot( s, tnorm ), 0.5); "
                + " v_TextureCoordinates = a_TextureCoordinates;\n"
                + "   gl_Position = u_Matrix * vec4(vPosition,1.0);\n" // 确定顶点位置
                + "}";

        // 片元着色器的脚本
        private static final String fragmentShader
                = "precision mediump float;         \n" // 声明float类型的精度为中等(精度越高越耗资源)
                + "uniform vec4 uColor;             \n" // uniform的属性uColor
                + "varying vec3 LightIntensity;   \n"
                + "uniform sampler2D u_TextureUnit;\n"
                + "varying vec2 v_TextureCoordinates;"
                + "void main(){                     \n"
                + "   gl_FragColor = vec4(LightIntensity, 1.0) *texture2D(u_TextureUnit, v_TextureCoordinates) + vec4(0.3, 0.3, 0.3, 1.0) * texture2D(u_TextureUnit, v_TextureCoordinates);        \n" // 给此片元的填充色
                + "}";

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    //  context用户解析纹理资源时使用，resourceId为纹理资源的ID
    public static int loadTexture(Context context, int resourceId) {
        //textureObjectIds用于存储OpenGL生成纹理对象的ID，我们只需要一个纹理
        final int[] textureObjectIds = new int[1];
        //1代表生成一个纹理
        glGenTextures(1, textureObjectIds, 0);
        //判断是否生成成功
        if (textureObjectIds[0] == 0) {
            return 0;
        }
        //加载纹理资源，解码成bitmap形式
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inScaled = false;
        final Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), resourceId, options);

        if (bitmap == null) {
            //删除指定的纹理对象
            glDeleteTextures(1, textureObjectIds, 0);
            return 0;
        }
        //第一个参数代表这是一个2D纹理，第二个参数就是OpenGL要绑定的纹理对象ID，也就是让OpenGL后面的纹理调用都使用此纹理对象
        glBindTexture(GL_TEXTURE_2D, textureObjectIds[0]);
        //设置纹理过滤参数，GL_TEXTURE_MIN_FILTER代表纹理缩写的情况，GL_LINEAR_MIPMAP_LINEAR代表缩小时使用三线性过滤的方式，至于过滤方式以后再详解
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR_MIPMAP_LINEAR);
        //GL_TEXTURE_MAG_FILTER代表纹理放大，GL_LINEAR代表双线性过滤
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
        //加载实际纹理图像数据到OpenGL ES的纹理对象中，这个函数是Android封装好的，可以直接加载bitmap格式，
        GLUtils.texImage2D(GL_TEXTURE_2D, 0, bitmap, 0);
        //我们为纹理生成MIP贴图，提高渲染性能，但是可占用较多的内存
        glGenerateMipmap(GL_TEXTURE_2D);
        //现在OpenGL已经完成了纹理的加载，不需要再绑定此纹理了，后面使用此纹理时通过纹理对象的ID即可
        glBindTexture(GL_TEXTURE_2D, 0);
        bitmap.recycle();
        //返回OpenGL生成的纹理对象ID
        return textureObjectIds[0];
    }


}
