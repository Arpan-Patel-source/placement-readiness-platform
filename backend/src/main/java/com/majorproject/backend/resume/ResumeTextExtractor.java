package com.majorproject.backend.resume;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.poi.xwpf.extractor.XWPFWordExtractor;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;

@Component
public class ResumeTextExtractor {

    public String extractText(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("Uploaded resume file is empty or missing");
        }

        String originalFilename = file.getOriginalFilename();
        String filenameLower = originalFilename != null ? originalFilename.toLowerCase() : "";

        try {
            if (filenameLower.endsWith(".pdf")) {
                return extractTextFromPdf(file);
            } else if (filenameLower.endsWith(".docx")) {
                return extractTextFromDocx(file);
            } else if (filenameLower.endsWith(".txt") || filenameLower.endsWith(".md")) {
                return extractTextFromPlainText(file);
            } else {
                // Fallback attempt based on content type
                String contentType = file.getContentType();
                if (contentType != null) {
                    if (contentType.equals("application/pdf")) {
                        return extractTextFromPdf(file);
                    } else if (contentType.contains("wordprocessingml")) {
                        return extractTextFromDocx(file);
                    } else if (contentType.startsWith("text/")) {
                        return extractTextFromPlainText(file);
                    }
                }
                throw new IllegalArgumentException("Unsupported file format. Please upload a PDF (.pdf) or Word document (.docx)");
            }
        } catch (IllegalArgumentException e) {
            throw e;
        } catch (Exception e) {
            throw new IllegalArgumentException("Failed to read text from resume: " + e.getMessage(), e);
        }
    }

    private String extractTextFromPdf(MultipartFile file) throws Exception {
        byte[] bytes = file.getBytes();
        try (PDDocument document = Loader.loadPDF(bytes)) {
            if (document.isEncrypted()) {
                throw new IllegalArgumentException("The uploaded PDF is password protected. Please upload an unlocked PDF.");
            }
            PDFTextStripper stripper = new PDFTextStripper();
            stripper.setSortByPosition(true);
            String text = stripper.getText(document);

            if (text == null || text.trim().isEmpty()) {
                throw new IllegalArgumentException("No readable text found in this PDF. If this is a scanned image, please upload a text-searchable PDF or DOCX.");
            }
            return text.trim();
        }
    }

    private String extractTextFromDocx(MultipartFile file) throws Exception {
        try (InputStream is = file.getInputStream();
             XWPFDocument doc = new XWPFDocument(is);
             XWPFWordExtractor extractor = new XWPFWordExtractor(doc)) {
            String text = extractor.getText();
            if (text == null || text.trim().isEmpty()) {
                throw new IllegalArgumentException("No readable text found in this DOCX document.");
            }
            return text.trim();
        }
    }

    private String extractTextFromPlainText(MultipartFile file) throws Exception {
        byte[] bytes = file.getBytes();
        String text = new String(bytes, StandardCharsets.UTF_8);
        if (text.trim().isEmpty()) {
            throw new IllegalArgumentException("Uploaded text file is empty.");
        }
        return text.trim();
    }
}
