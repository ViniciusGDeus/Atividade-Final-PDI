public class Main {
    public static void main(String[] args) {
        System.loadLibrary(org.opencv.core.Core.NATIVE_LIBRARY_NAME);
        javax.swing.SwingUtilities.invokeLater(() -> new CaptureFrame().setVisible(true));
    }
}