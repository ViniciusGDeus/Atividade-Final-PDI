import org.opencv.core.*;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.videoio.VideoCapture;
import org.opencv.highgui.HighGui;

import java.io.*;
import java.util.*;

public class Recognizer {

    private static final Set<String> reconhecidosHoje = new HashSet<>();

    public static void iniciarReconhecimento() {
        String cascadePath = "C:\\Users\\zombi\\Downloads\\opencv\\build\\etc\\haarcascades\\haarcascade_frontalface_alt.xml";
        CascadeClassifier faceDetector = new CascadeClassifier(cascadePath);

        if (faceDetector.empty()) {
            System.err.println("Erro ao carregar o classificador HaarCascade.");
            return;
        }

        Map<String, Mat> bancoHistogramas = new HashMap<>();

        File pasta = new File("rostos/");
        File[] arquivos = pasta.listFiles();
        if (arquivos == null || arquivos.length == 0) {
            System.out.println("Nenhuma imagem encontrada na pasta 'rostos/'.");
            return;
        }

        for (File imgFile : arquivos) {
            String nome = imgFile.getName().replace(".jpg", "");
            Mat img = Imgcodecs.imread(imgFile.getAbsolutePath());

            if (img.empty()) {
                System.out.println("Erro ao carregar imagem: " + imgFile.getName());
                continue;
            }

            Mat cinza = new Mat();
            Imgproc.cvtColor(img, cinza, Imgproc.COLOR_BGR2GRAY);
            Imgproc.resize(cinza, cinza, new Size(100, 100));

            Mat hist = new Mat();
            Imgproc.calcHist(List.of(cinza), new MatOfInt(0), new Mat(), hist, new MatOfInt(256), new MatOfFloat(0, 256));
            Core.normalize(hist, hist, 0, 1, Core.NORM_MINMAX);
            bancoHistogramas.put(nome, hist);
        }

        VideoCapture cam = new VideoCapture(0);
        if (!cam.isOpened()) {
            System.err.println("Erro ao acessar a webcam.");
            return;
        }

        Mat frame = new Mat();
        System.out.println("Pressione ESC para encerrar.");

        while (true) {
            cam.read(frame);
            if (frame.empty()) break;

            Mat cinza = new Mat();
            Imgproc.cvtColor(frame, cinza, Imgproc.COLOR_BGR2GRAY);
            MatOfRect rostos = new MatOfRect();
            faceDetector.detectMultiScale(cinza, rostos);

            for (Rect rosto : rostos.toArray()) {
                Mat rostoImg = new Mat(cinza, rosto);
                Imgproc.resize(rostoImg, rostoImg, new Size(100, 100));

                Mat histNovo = new Mat();
                Imgproc.calcHist(List.of(rostoImg), new MatOfInt(0), new Mat(), histNovo, new MatOfInt(256), new MatOfFloat(0, 256));
                Core.normalize(histNovo, histNovo, 0, 1, Core.NORM_MINMAX);

                String nomeReconhecido = "Desconhecido";
                double melhorCorrelacao = 0.6;

                for (var entry : bancoHistogramas.entrySet()) {
                    double cor = Imgproc.compareHist(histNovo, entry.getValue(), Imgproc.CV_COMP_CORREL);
                    if (cor > melhorCorrelacao) {
                        nomeReconhecido = entry.getKey();
                        melhorCorrelacao = cor;
                    }
                }

                Scalar corBox = nomeReconhecido.equals("Desconhecido") ? new Scalar(0, 0, 255) : new Scalar(0, 255, 0);
                Imgproc.rectangle(frame, rosto, corBox, 2);
                Imgproc.putText(frame, nomeReconhecido, new Point(rosto.x, rosto.y - 10), Imgproc.FONT_HERSHEY_SIMPLEX, 0.6, corBox, 2);

                if (!nomeReconhecido.equals("Desconhecido") && !reconhecidosHoje.contains(nomeReconhecido)) {
                    try {
                        FileHandler.registrarReconhecido(nomeReconhecido);
                        reconhecidosHoje.add(nomeReconhecido);
                    } catch (IOException e) {
                        System.err.println("Erro ao registrar nome: " + e.getMessage());
                    }
                }
            }

            HighGui.imshow("Reconhecimento Facial", frame);
            if (HighGui.waitKey(30) == 27) break; // ESC
        }

        cam.release();
        HighGui.destroyAllWindows();
    }
}
