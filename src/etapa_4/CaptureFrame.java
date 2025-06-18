import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;

public class CaptureFrame extends JFrame {
    private JTextField nomeField;
    private JButton capturarBtn, iniciarReconhecimentoBtn;

    public CaptureFrame() {
        setTitle("Cadastro de Aluno");
        setSize(400, 150);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new FlowLayout());

        nomeField = new JTextField(20);
        capturarBtn = new JButton("Capturar Foto");
        iniciarReconhecimentoBtn = new JButton("Iniciar Reconhecimento");
        iniciarReconhecimentoBtn.setEnabled(false);

        add(new JLabel("Nome do Aluno:"));
        add(nomeField);
        add(capturarBtn);
        add(iniciarReconhecimentoBtn);

        capturarBtn.addActionListener(e -> {
            String nome = nomeField.getText().trim();
            if (!nome.isEmpty()) {
                try {
                    Utils.capturarImagem(nome);
                    FileHandler.salvarNome(nome);
                    JOptionPane.showMessageDialog(this, "Foto capturada com sucesso!");
                    iniciarReconhecimentoBtn.setEnabled(true);
                } catch (IOException ex) {
                    JOptionPane.showMessageDialog(this, "Erro ao salvar imagem ou nome.");
                }
            } else {
                JOptionPane.showMessageDialog(this, "Informe um nome válido.");
            }
        });

        iniciarReconhecimentoBtn.addActionListener(e -> {
            new Thread(() -> Recognizer.iniciarReconhecimento()).start();
        });
    }
}