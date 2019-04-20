#include <jni.h>
#include <string>
#include <opencv2/opencv.hpp>

using namespace cv;

extern "C"
JNIEXPORT void JNICALL
Java_com_tistory_deque_rectanglecorrection_camera_CameraActivity_convertRGBtoGray(JNIEnv *env,
                                                                                  jobject instance,
                                                                                  jlong matAddrInput,
                                                                                  jlong matAddrResult) {
    // 입력 RGBA 이미지를 GRAY 이미지로 변환
    Mat &matInput = *(Mat *) matAddrInput;
    Mat &matResult = *(Mat *) matAddrResult;
    cvtColor(matInput, matResult, COLOR_RGBA2GRAY);
}

extern "C"
JNIEXPORT void JNICALL
Java_com_tistory_deque_rectanglecorrection_rect_RectCorrectionActivity_loadImage(JNIEnv *env,
                                                                                   jobject instance,
                                                                                   jstring imageFileName_,
                                                                                   jlong img) {
    const char *imageFileName = env->GetStringUTFChars(imageFileName_, 0);
    Mat &imageInput = *(Mat *) img;
    imageInput = imread(imageFileName, IMREAD_COLOR);

    env->ReleaseStringUTFChars(imageFileName_, imageFileName);
}

extern "C"
JNIEXPORT void JNICALL
Java_com_tistory_deque_rectanglecorrection_rect_RectCorrectionActivity_imageProcessing(JNIEnv *env,
                                                                                   jobject instance,
                                                                                   jlong inputImageAddr,
                                                                                   jlong outputImageAddr,
                                                                                   jint lowThreshold,
                                                                                   jint highThreshold) {

    Mat* imageInput = (Mat*) inputImageAddr;
    Mat* imageOutput = (Mat*) outputImageAddr;

    Mat grayScaleImage;
    cvtColor(*imageInput, grayScaleImage, COLOR_RGBA2GRAY);
    medianBlur(grayScaleImage, grayScaleImage, 9);

    Mat edgeImage;
    //threshold(grayScaleImage, edgeImage, lowThreshold, 255, THRESH_BINARY_INV | THRESH_OTSU);
    Canny(grayScaleImage, edgeImage, lowThreshold, highThreshold);
    //threshold(edgeImage, edgeImage, 0, 255,  THRESH_BINARY_INV);

    *imageOutput = edgeImage;
}

extern "C"
JNIEXPORT void JNICALL
        Java_com_tistory_deque_rectanglecorrection_rect_RectCorrectionActivity_imagePerspectiveProcessing(JNIEnv *env,
                                                                                                          jobject instance,
                                                                                                          jlong inputImageAddr,
                                                                                                          jlong outputImageAddr,
                                                                                                          jint lowThreshold,
                                                                                                          jint highThreshold) {

    Point2f inputQuad[4];
    Point2f outputQuad[4];

}
