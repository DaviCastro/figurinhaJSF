package br.com.util;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.imageio.ImageIO;

import net.coobird.thumbnailator.Thumbnails;

import org.apache.pdfbox.exceptions.COSVisitorException;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.edit.PDPageContentStream;
import org.apache.pdfbox.pdmodel.graphics.xobject.PDJpeg;
import org.apache.pdfbox.pdmodel.graphics.xobject.PDPixelMap;
import org.apache.pdfbox.pdmodel.graphics.xobject.PDXObjectImage;



/**
 *
 *
 */
/**
 * @author oreis
 *
 */
public class PdfUtil {

    private String caminhoPdf;

    public PdfUtil(){

    }
    public PdfUtil(String caminho){
        this.caminhoPdf = caminho;
    }



    private PDPage addPagina(PDDocument doc){
        PDPage page = new PDPage();
        page.setMediaBox(PDPage.PAGE_SIZE_A4);
        doc.addPage( page );
        return page;
    }

    private void addConteudoImagem(PDDocument doc, PDPage page, PDXObjectImage ximage ) throws IOException{
        PDPageContentStream contentStream = new PDPageContentStream(doc, page);
        contentStream.drawImage(ximage,0,40);
        contentStream.close();
    }


    /**
     *
     * Retorna uma string de sucesso, em caso de excecao por enquanto retorna a mensagem de erro para a camada de cima.
     *
     * **/
    public String geraPdf(Map<String,File> arqs){
        //Se for pdf so vai ter um arquivo e o mesmo pode ser gravado de forma normal
        String nomePdf = null;
        try{
        //Senao sao varios arquivos de imagem que serao convertidos
        PDDocument doc = null;
        doc = new PDDocument();
        for(String nomeArquivo : arqs.keySet()){
            //Adiciona pagina
            PDPage page = addPagina(doc);

            BufferedImage imagem = getBufferedImageTratada(arqs.get(nomeArquivo));


            //Se for jpg deve instanciar PDJepg caso contrario PDPixelMap
            PDXObjectImage ximage = null;
             if(nomeArquivo.endsWith("jpg"))
                 ximage = new PDJpeg(doc, imagem);
             else if(nomeArquivo.endsWith("png")){
                 //ImageIO.write(resized, "png",new File("C:\\Otavio\\TI\\imagens\\imagemPng.png"));
                 ximage = new PDPixelMap(doc,imagem);
             }
             addConteudoImagem(doc, page, ximage);
        }
            nomePdf = geraNome()+".pdf";
            doc.save(caminhoPdf+nomePdf);
            if( doc != null )
            {
                doc.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
            return "Problema na conversao das imagens para pdf";
        } catch (COSVisitorException e) {
            e.printStackTrace();
            return "Problema na gravacao do pdf";
        }
            return nomePdf;

    }

    public ByteArrayOutputStream getPdfAsOutputStream(String pdf){

        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        try {
        FileInputStream is = new FileInputStream(caminhoPdf+pdf);
        int nRead;
        byte[] data = new byte[16384];

        while ((nRead = is.read(data, 0, data.length)) != -1) {
          buffer.write(data, 0, nRead);
        }
            buffer.flush();
            is.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return buffer;

    }


    private BufferedImage getBufferedImageTratada(File file) throws IOException {
        //With 595 e Height 841 sao as dimensoes default de uma folha A4 para o pdf. Assim se a imagem original passar disso deve ser redimensionada
        //Caso contrario pode ser retornada.
        BufferedImage originalImage = ImageIO.read(file);
        if(originalImage.getWidth() < 595 || originalImage.getHeight() < 841 )
            return originalImage;
        return Thumbnails.of(originalImage)
                   .size(595, 841)
                   .asBufferedImage();

    }

    private String geraNome() {
        String[] keys = new String[] { "1", "2", "3", "4", "5", "6", "7", "8", "9",
                "0", "A", "B", "C", "D", "E","", "F", "G", "H", "I", "J", "K", "L", "M",
                "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z", "a",
                "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m", "n", "o",
                "p", "q", "r", "s", "t", "u", "v", "w", "x", "y", "z" };

        List<String> chars = new ArrayList<String>();
        Random random = new Random();
        String senha = "";

        for (int i = 0; i < keys.length; i++) {
            chars.add(keys[i].toString());
        }

        for (int i = 0; i < 15; i++) {
            senha += chars.get(random.nextInt(chars.size()));
        }

        return senha;
    }

    public static void main(String[] args) throws COSVisitorException, IOException {
        String caminho = "C:\\Otavio\\TI\\imagens";
        File fs = new File(caminho);

        Map<String,File> map = new HashMap<String, File>();
        for(File f : fs.listFiles()){
            String[] nome = f.getName().split("\\.");
            File tmp = File.createTempFile(nome[0],"."+nome[1]);
            OutputStream out = new FileOutputStream(tmp);
            int read = 0;
            byte[] bytes = new byte[1024];
            InputStream in = new FileInputStream(f);
            while((read = in.read(bytes)) != -1){
                out.write(bytes,0,read);
            }
            in.close();
            out.flush();
            out.close();

            map.put(f.getName(), tmp);
        }
        new PdfUtil().geraPdf(map);
    }
}