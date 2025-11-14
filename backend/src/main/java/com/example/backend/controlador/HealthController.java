package com.example.backend.controlador;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
public class HealthController {
    
    @GetMapping("/")
    public ResponseEntity<?> root() {
        Map<String, String> response = new HashMap<>();
        response.put("mensaje", "¡Bienvenido a la API de PlayList de Videos!");
        response.put("estado", "OK");
        response.put("version", "1.0.0");
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/health")
    public ResponseEntity<?> health() {
        Map<String, String> response = new HashMap<>();
        response.put("status", "UP");
        response.put("servicio", "PlayList Videos API");
        return ResponseEntity.ok(response);
    }
}
