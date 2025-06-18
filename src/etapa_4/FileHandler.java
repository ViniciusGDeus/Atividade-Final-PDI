import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.io.FileWriter;
import java.io.IOException;


public class FileHandler {
    public static void salvarNome(String nome) throws IOException {
        Files.write(Paths.get("alunos.txt"), (nome + System.lineSeparator()).getBytes(), StandardOpenOption.CREATE, StandardOpenOption.APPEND);
    }

    private static final String CAMINHO_ARQUIVO = "alunos_reconhecidos.txt";

    public static void registrarReconhecido(String nome) throws IOException {
        Set<String> nomes = lerNomesExistentes();
        if (!nomes.contains(nome)) {
            try (BufferedWriter bw = new BufferedWriter(new FileWriter(CAMINHO_ARQUIVO, true))) {
                bw.write(nome);
                bw.newLine();
            }
        }
    }

    private static Set<String> lerNomesExistentes() throws IOException {
        Set<String> nomes = new HashSet<>();
        File arquivo = new File(CAMINHO_ARQUIVO);
        if (!arquivo.exists()) return nomes;

        try (BufferedReader br = new BufferedReader(new FileReader(arquivo))) {
            String linha;
            while ((linha = br.readLine()) != null) {
                nomes.add(linha.trim());
            }
        }
        return nomes;
    }

    public static List<String> carregarNomes() throws IOException {
        return Files.readAllLines(Paths.get("alunos.txt"));
    }
}