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
import com.knowvault.model.User;
import com.knowvault.model.dto.DocumentUploadForm;
import com.knowvault.service.DocumentService;
import com.knowvault.service.FileStorageService;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

@Controller
@RequestMapping("/documents")
public class DocumentController {

    private final DocumentService documentService;
    private final FileStorageService fileStorageService;

    public DocumentController(DocumentService documentService,
                              FileStorageService fileStorageService) {
        this.documentService = documentService;
        this.fileStorageService = fileStorageService;
    }

    @GetMapping
    public String listDocuments(
            @RequestParam(required = false) String search,
            Model model,
            HttpSession session
    ) {
        if (session.getAttribute("loggedUser") == null) {
            return "redirect:/login";
        }

        List<Document> documents = documentService.searchDocuments(search);

        model.addAttribute("documents", documents);
        model.addAttribute("search", search);

        return "documents/list";
    }

    @GetMapping("/upload")
    public String uploadPage(Model model, HttpSession session) {
        if (session.getAttribute("loggedUser") == null) {
            return "redirect:/login";
        }

        model.addAttribute("form", new DocumentUploadForm());
        return "documents/upload";
    }

    @PostMapping("/upload")
    public String uploadDocument(
            @Valid @ModelAttribute("form") DocumentUploadForm form,
            BindingResult bindingResult,
            HttpSession session
    ) throws IOException {
        if (session.getAttribute("loggedUser") == null) {
            return "redirect:/login";
        }

        if (bindingResult.hasErrors()) {
            return "documents/upload";
        }

        MultipartFile file = form.getFile();

        if (file == null || file.isEmpty()) {
            return "documents/upload";
        }

        String storedFileName = fileStorageService.storeFile(file);
        String fullPath = "uploads/" + storedFileName;

        User user = (User) session.getAttribute("loggedUser");

        documentService.createDocument(
                form.getTitle(),
                file.getOriginalFilename(),
                storedFileName,
                fullPath,
                file.getSize(),
                file.getContentType(),
                user.getUserId()
        );

        return "redirect:/documents";
    }

    @GetMapping("/download/{id}")
    public ResponseEntity<Resource> downloadDocument(@PathVariable Long id) throws IOException {
        Document doc = documentService.getDocumentById(id);
        Resource resource = fileStorageService.loadFileAsResource(doc.getFilePath());

        return ResponseEntity.ok()
                .header("Content-Disposition",
                        "attachment; filename=\"" + doc.getOriginalFileName() + "\"")
                .body(resource);
    }

    @GetMapping("/view/{id}")
    public ResponseEntity<Resource> previewDocument(@PathVariable Long id) throws IOException {
        Document doc = documentService.getDocumentById(id);
        Resource resource = fileStorageService.loadFileAsResource(doc.getFilePath());

        String mimeType = doc.getMimeType() != null ? doc.getMimeType() : MediaType.APPLICATION_OCTET_STREAM_VALUE;

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(mimeType))
                .body(resource);
    }

    @PostMapping("/delete/{id}")
    public String deleteDocument(@PathVariable Long id, HttpSession session) {
        if (session.getAttribute("loggedUser") == null) {
            return "redirect:/login";
        }

        documentService.deleteDocument(id);
        return "redirect:/documents";
    }
}