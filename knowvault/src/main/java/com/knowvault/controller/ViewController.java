package com.knowvault.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * ViewController Esto es un Controlador temporal para visualizar templates
 * 
 * Este es un controlador básico creado por Kevin para permitir
 * ver los templates HTML mientras Adrian desarrolla los controladores finales.
 * 
 * @author Kevin García Gutiérrez
 * @version 1.0 (temporal)
 */
@Controller
public class ViewController {
    
    @GetMapping("/")
    public String index() {
        return "login";
    }
    
    @GetMapping("/login")
    public String login() {
        return "login";
    }
    
    @GetMapping("/dashboard")
    public String dashboard() {
        return "dashboard";
    }
    
    @GetMapping("/documents")
    public String documents() {
        return "documents-list";
    }
    
    @GetMapping("/upload")
    public String upload() {
        return "upload-document";
    }
    
    @GetMapping("/ask")
    public String ask() {
        return "ask-ai";
    }
    
    @GetMapping("/history")
    public String history() {
        return "history";
    }
}