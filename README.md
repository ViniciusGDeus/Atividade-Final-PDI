# Sistema de Reconhecimento Facial para Transporte Escolar

Este projeto em Java usa OpenCV para realizar reconhecimento facial em tempo real, com o objetivo de substituir o uso de carteirinhas em ônibus escolares. 
O sistema permite capturar e registrar rostos, reconhecer alunos automaticamente e registrar sua presença de forma eficiente.
---

## Funcionalidades

- ✅ Captura de imagem facial com webcam
- ✅ Verificação de rostos duplicados
- ✅ Reconhecimento facial em tempo real
- ✅ Sistema offline (não depende da internet)
- ✅ Código 100% em Java com OpenCV

---

## Como Funciona

1. O sistema usa `haarcascade_frontalface_alt.xml` para detectar rostos.
2. Quando um rosto é capturado, é comparado com o banco de imagens salvas.
3. Se o rosto for novo, é salvo na pasta `rostos/`.
4. Durante o reconhecimento, os rostos são comparados por histograma de intensidade de pixels.

---

## Requisitos

- Java 8+
- [OpenCV 4.x](https://opencv.org/releases/) (incluindo arquivos `.dll` e `.jar`)
- WebCam funcional

---

## Configuração do Ambiente

1. **Instale o OpenCV** e extraia os arquivos:
   - Coloque o arquivo `opencv-xxx.jar` no classpath do seu projeto.
   - Certifique-se de que a DLL (`opencv_java455.dll`, por exemplo) esteja acessível (adicione ao PATH).

2. **Configure o classificador Haar**:
   - Caminho usado no projeto:
     ```
     C:\Users\zombi\Downloads\opencv\build\etc\haarcascades\haarcascade_frontalface_alt.xml
     ```
   - Ajuste conforme o local onde extraiu o OpenCV.

3. **Crie a pasta `rostos/`** na raiz do projeto, onde serão salvas as imagens dos alunos.

---

## Capturar Imagem de um Novo Aluno

```java
Utils.capturarImagem("nome_do_aluno");
