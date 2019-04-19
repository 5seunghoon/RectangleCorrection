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
                                                                                   jlong outputImageAddr) {

    Mat* imageInput = (Mat*) inputImageAddr;
    Mat* outputInput = (Mat*) outputImageAddr;

    cvtColor(*imageInput, *outputInput, COLOR_RGBA2GRAY);

}