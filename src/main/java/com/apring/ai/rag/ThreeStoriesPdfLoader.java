package com.apring.ai.rag;

import jakarta.annotation.PostConstruct;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.ai.document.Document;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.util.List;


@Component
public class ThreeStoriesPdfLoader {


    @Autowired
    private VectorStore vectorStore;

    @Value("classpath:Eazybytes_HR_Policies.pdf")
    Resource pdfFile;


//    @PostConstruct
    public void loadPDF() {

        try (InputStream is = pdfFile.getInputStream();
             PDDocument pdf = Loader.loadPDF(is.readAllBytes())) {

            PDFTextStripper stripper = new PDFTextStripper();
            String text = stripper.getText(pdf);

            Document document = new Document(text);

            TokenTextSplitter splitter = TokenTextSplitter.builder()
                    .withChunkSize(200)
                    .withMaxNumChunks(400)
                    .build();

            List<Document> chunks = splitter.split(List.of(document));

            vectorStore.add(chunks);
            System.out.println("Loaded chunks: " + chunks.size());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
