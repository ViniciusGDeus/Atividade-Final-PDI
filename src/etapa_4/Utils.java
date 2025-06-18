import org.opencv.core.*;
import org.opencv.imgproc.Imgproc;
import org.opencv.videoio.VideoCapture;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.objdetect.CascadeClassifier;

import java.io.File;
import java.util.*;

public class Utils {

    public static void capturarImagem(String nome) {
        String cascadePath = "C:\\Users\\zombi\\Downloads\\opencv\\build\\etc\\haarcascades\\haarcascade_frontalface_alt.xml";
        CascadeClassifier faceDetector = new CascadeClassifier(cascadePath);

        if (faceDetector.empty()) {
            System.err.println("Erro ao carregar o classificador HaarCascade.");
            return;
        }

        VideoCapture camera = new VideoCapture(0);
        if (!camera.isOpened()) {
            System.err.println("Erro ao abrir a câmera.");
            return;
        }

        Map<String, Mat> bancoHistogramas = carregarHistogramasExistentes();
        Mat frame = new Mat();

        System.out.println("Aguardando detecção de rosto para captura automática...");

        while (true) {
            camera.read(frame);
            if (frame.empty()) continue;

            Mat cinza = new Mat();
            Imgproc.cvtColor(frame, cinza, Imgproc.COLOR_BGR2GRAY);
            MatOfRect rostos = new MatOfRect();
            faceDetector.detectMultiScale(cinza, rostos);

            if (!rostos.empty()) {
                Rect rosto = rostos.toArray()[0]; // Pega o primeiro rosto detectado
                Mat rostoImg = new Mat(cinza, rosto);
                Imgproc.resize(rostoImg, rostoImg, new Size(100, 100));

                Mat histNovo = new Mat();
                Imgproc.calcHist(List.of(rostoImg), new MatOfInt(0), new Mat(), histNovo, new MatOfInt(256), new MatOfFloat(0, 256));
                Core.normalize(histNovo, histNovo, 0, 1, Core.NORM_MINMAX);

                for (var entry : bancoHistogramas.entrySet()) {
                    double cor = Imgproc.compareHist(histNovo, entry.getValue(), Imgproc.CV_COMP_CORREL);
                    if (cor > 0.7) {
                        System.out.println("Rosto semelhante já cadastrado como: " + entry.getKey());
                        camera.release();
                        return;
                    }
                }

                Imgcodecs.imwrite("rostos/" + nome + ".jpg", new Mat(frame, rosto));
                System.out.println("Imagem salva com sucesso!");
                break;
            }
        }

        camera.release();
    }

    private static Map<String, Mat> carregarHistogramasExistentes() {
        Map<String, Mat> histMap = new HashMap<>();
        File pasta = new File("rostos/");
        File[] arquivos = pasta.listFiles();
        if (arquivos == null) return histMap;

        for (File imgFile : arquivos) {
            String nome = imgFile.getName().replace(".jpg", "");
            Mat img = Imgcodecs.imread(imgFile.getAbsolutePath());
            if (img.empty()) continue;

            Mat cinza = new Mat();
            Imgproc.cvtColor(img, cinza, Imgproc.COLOR_BGR2GRAY);
            Imgproc.resize(cinza, cinza, new Size(100, 100));

            Mat hist = new Mat();
            Imgproc.calcHist(List.of(cinza), new MatOfInt(0), new Mat(), hist, new MatOfInt(256), new MatOfFloat(0, 256));
            Core.normalize(hist, hist, 0, 1, Core.NORM_MINMAX);
            histMap.put(nome, hist);
        }

        return histMap;
    }
}
