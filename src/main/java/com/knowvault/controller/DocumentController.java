package com.knowvault.controller;

import java.io.IOException;
import java.util.List;

import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.knowvault.model.Document;
import com.knowvault.model.DocumentChunk;
import com.knowvault.model.User;
import com.knowvault.model.dto.DocumentUploadForm;
import com.knowvault.repository.JdbcDocumentChunkRepository;
import com.knowvault.service.DocumentService;
import com.knowvault.service.FileStorageService;
import com.knowvault.service.PdfTextExtractorService;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

/**
 * DocumentController - handles document management endpoints.
 * Covers upload, list, download, view, and delete operations.
 *
 * @author Sebastián González Tabares
 */
@Controller
@RequestMapping("/documents")
public class DocumentController {

    private final DocumentService documentService;
    private final FileStorageService fileStorageService;
    private final PdfTextExtractorService pdfTextExtractorService;
    private final JdbcDocumentChunkRepository chunkRepository;

    public DocumentController(
            DocumentService documentService,
            FileStorageService fileStorageService,
            PdfTextExtractorService pdfTextExtractorService,
            JdbcDocumentChunkRepository chunkRepository
    ) {
        this.documentService = documentService;
        this.fileStorageService = fileStorageService;
        this.pdfTextExtractorService = pdfTextExtractorService;
        this.chunkRepository = chunkRepository;
    }

    // ==============================
    // GET /documents
    // ==============================

    @GetMapping
    public String listDocuments(
            @RequestParam(required = false) String search,
            Model model,
            HttpSession session
    ) {
        if (session.getAttribute("loggedUser") == null) {
            return "redirect:/login";
        }

        User user = (User) session.getAttribute("loggedUser");
        List<Document> documents = documentService.searchDocuments(search);

        model.addAttribute("documents", documents);
        model.addAttribute("search", search);
        model.addAttribute("user", user);

        return "documents/list";
    }

    // ==============================
    // GET /documents/upload
    // ==============================

    @GetMapping("/upload")
    public String uploadPage(Model model, HttpSession session) {
        if (session.getAttribute("loggedUser") == null) {
            return "redirect:/login";
        }

        User user = (User) session.getAttribute("loggedUser");

        model.addAttribute("form", new DocumentUploadForm());
        model.addAttribute("user", user);
        model.addAttribute("categories", documentService.getAllCategories());

        return "documents/upload";
    }

    // ==============================
    // POST /documents/upload
    // ==============================

    @PostMapping("/upload")
    public String uploadDocument(
            @Valid @ModelAttribute("form") DocumentUploadForm form,
            BindingResult bindingResult,
            HttpSession session,
            Model model
    ) throws IOException {

        if (session.getAttribute("loggedUser") == null) {
            return "redirect:/login";
        }

        User user = (User) session.getAttribute("loggedUser");

        if (bindingResult.hasErrors()) {
            model.addAttribute("categories", documentService.getAllCategories());
            model.addAttribute("user", user);
            return "documents/upload";
        }

        MultipartFile file = form.getFile();

        if (file == null || file.isEmpty()) {
            model.addAttribute("error", "Please select a file to upload.");
            model.addAttribute("categories", documentService.getAllCategories());
            model.addAttribute("user", user);
            return "documents/upload";
        }

        // Validate MIME type
        String mimeType = file.getContentType();
        if (mimeType == null || (!mimeType.equals("application/pdf") &&
                !mimeType.equals("application/msword") &&
                !mimeType.equals("application/vnd.openxmlformats-officedocument.wordprocessingml.document") &&
                !mimeType.equals("text/plain"))) {
            model.addAttribute("error", "Only PDF, DOC, DOCX, and TXT files are allowed.");
            model.addAttribute("categories", documentService.getAllCategories());
            model.addAttribute("user", user);
            return "documents/upload";
        }

        // Store file on disk
        String storedFileName = fileStorageService.storeFile(file);
        String fullPath = "uploads/" + storedFileName;

        // Save document metadata including category
        documentService.createDocument(
                form.getTitle(),
                file.getOriginalFilename(),
                storedFileName,
                fullPath,
                file.getSize(),
                mimeType,
                form.getCategory(),   // ← category now passed
                user.getUserId()
        );

        // Get the saved document to retrieve its ID
        Document savedDoc = documentService.getRecentDocuments(1).get(0);

        // Extract text and create chunks (PDF only)
        if ("application/pdf".equals(mimeType)) {
            List<DocumentChunk> chunks = pdfTextExtractorService.extractAndChunk(
                    fullPath,
                    savedDoc.getDocumentId()
            );
            chunkRepository.insertAll(chunks);
        }

        return "redirect:/documents";
    }

    // ==============================
    // GET /documents/download/{id}
    // ==============================

    @GetMapping("/download/{id}")
    public ResponseEntity<Resource> downloadDocument(@PathVariable Long id) throws IOException {
        Document doc = documentService.getDocumentById(id);
        Resource resource = fileStorageService.loadFileAsResource(doc.getFilePath());

        return ResponseEntity.ok()
                .header("Content-Disposition",
                        "attachment; filename=\"" + doc.getOriginalFileName() + "\"")
                .body(resource);
    }

    // ==============================
    // GET /documents/view/{id}
    // ==============================

    @GetMapping("/view/{id}")
    public ResponseEntity<Resource> previewDocument(@PathVariable Long id) throws IOException {
        Document doc = documentService.getDocumentById(id);
        Resource resource = fileStorageService.loadFileAsResource(doc.getFilePath());

        String mimeType = doc.getMimeType() != null
                ? doc.getMimeType()
                : MediaType.APPLICATION_OCTET_STREAM_VALUE;

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(mimeType))
                .body(resource);
    }

    // ==============================
    // POST /documents/delete/{id}
    // ==============================

    @PostMapping("/delete/{id}")
    public String deleteDocument(@PathVariable Long id, HttpSession session) {
        if (session.getAttribute("loggedUser") == null) {
            return "redirect:/login";
        }

        documentService.deleteDocument(id);
        return "redirect:/documents";
    }
}