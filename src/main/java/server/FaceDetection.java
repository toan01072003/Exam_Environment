package server;

import org.opencv.core.*;
import org.opencv.dnn.Dnn;
import org.opencv.dnn.Net;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;
import java.util.List;

public class FaceDetection {
    private Net mtcnnNet;

    public FaceDetection(String modelPath) {
        // Tải mô hình MTCNN từ file ONNX
        this.mtcnnNet = Dnn.readNetFromONNX(modelPath);
    }

    public List<Rect> detectFaces(Mat image) {
    	
    	// Resize image to the required input size for the model
    	Mat resizedImage = new Mat();
    	Imgproc.resize(image, resizedImage, new Size(320, 320)); // Make sure to use the expected size

    	// Create a blob from the resized image
    	Mat inputBlob = Dnn.blobFromImage(resizedImage, 1.0, new Size(320, 320), new Scalar(104, 177, 123), true, false);
    	mtcnnNet.setInput(inputBlob);

    	// Forward pass
    	Mat detections = mtcnnNet.forward();

        List<Rect> faces = new ArrayList<>();
        float confidenceThreshold = 0.5f; // Adjust this threshold as needed

        // Flatten the detections matrix to iterate over it
        float[] data = new float[(int) (detections.total() * detections.channels())];
        detections.get(0, 0, data);

        // Iterate over each detection (each detection is 7 values)
        for (int i = 0; i < data.length / 7; i++) {
            float confidence = data[i * 7 + 2]; // Confidence is at index 2
            if (confidence > confidenceThreshold) {
                int x1 = (int) (data[i * 7 + 3] * image.cols());
                int y1 = (int) (data[i * 7 + 4] * image.rows());
                int x2 = (int) (data[i * 7 + 5] * image.cols());
                int y2 = (int) (data[i * 7 + 6] * image.rows());

                Rect faceRect = new Rect(new Point(x1, y1), new Point(x2, y2));
                faces.add(faceRect);
            }
        }
        return faces;
    }


    public static void main(String[] args) {
        // Khởi tạo OpenCV
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);

        // Đường dẫn tới mô hình MTCNN
        String modelPath = "C:\\Users\\Toan\\Downloads\\version-RFB-320-int8.onnx"; // Cập nhật đường dẫn
        FaceDetection faceDetection = new FaceDetection(modelPath);

        // Đọc ảnh từ file
        Mat image = Imgcodecs.imread("C:\\Users\\Toan\\Pictures\\toan.jpg"); // Cập nhật đường dẫn
        if (image.empty()) {
            System.out.println("Không thể mở ảnh!");
            return;
        }

        // Phát hiện khuôn mặt
        List<Rect> faces = faceDetection.detectFaces(image);
        for (Rect face : faces) {
            // Vẽ hình chữ nhật quanh khuôn mặt
            Imgproc.rectangle(image, face.tl(), face.br(), new Scalar(0, 255, 0), 2);
        }

        // Lưu ảnh kết quả
        Imgcodecs.imwrite("C:\\Users\\Toan\\Pictures\\detect.jpg", image); // Cập nhật đường dẫn
        System.out.println("Kết quả đã được lưu!");
    }
}
