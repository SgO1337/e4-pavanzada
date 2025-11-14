package com.example.backend.controlador;

import com.example.backend.dto.VideoRequest;
import com.example.backend.dto.VideoResponse;
import com.example.backend.modelo.Usuario;
import com.example.backend.modelo.Video;
import com.example.backend.servicio.UsuarioServicio;
import com.example.backend.servicio.VideoServicio;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/videos")
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:3001"}, allowCredentials = "true")
public class VideoControlador {
    
    @Autowired
    private VideoServicio videoServicio;
    
    @Autowired
    private UsuarioServicio usuarioServicio;
    
    @GetMapping
    public ResponseEntity<?> obtenerTodos(HttpSession session) {
        Long usuarioId = (Long) session.getAttribute("usuarioId");
        
        if (usuarioId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("No autenticado");
        }
        
        List<VideoResponse> videos = videoServicio.obtenerTodos(usuarioId);
        return ResponseEntity.ok(videos);
    }
    
    @GetMapping("/favoritos")
    public ResponseEntity<?> obtenerFavoritos(HttpSession session) {
        Long usuarioId = (Long) session.getAttribute("usuarioId");
        
        if (usuarioId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("No autenticado");
        }
        
        List<VideoResponse> videos = videoServicio.obtenerFavoritos(usuarioId);
        return ResponseEntity.ok(videos);
    }
    
    @GetMapping("/mis-videos")
    public ResponseEntity<?> obtenerMisVideos(HttpSession session) {
        Long usuarioId = (Long) session.getAttribute("usuarioId");
        
        if (usuarioId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("No autenticado");
        }
        
        List<VideoResponse> videos = videoServicio.obtenerPorUsuario(usuarioId, usuarioId);
        return ResponseEntity.ok(videos);
    }
    
    @PostMapping
    public ResponseEntity<?> agregarVideo(@RequestBody VideoRequest request, HttpSession session) {
        Long usuarioId = (Long) session.getAttribute("usuarioId");
        
        if (usuarioId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("No autenticado");
        }
        
        try {
            Optional<Usuario> usuarioOpt = usuarioServicio.obtenerPorId(usuarioId);
            
            if (usuarioOpt.isEmpty()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Usuario no encontrado");
            }
            
            Video video = videoServicio.agregarVideo(
                request.getTitulo(),
                request.getUrlYouTube(),
                request.getDescripcion(),
                usuarioOpt.get()
            );
            
            Map<String, Object> response = new HashMap<>();
            response.put("mensaje", "Video agregado exitosamente");
            response.put("videoId", video.getId());
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminarVideo(@PathVariable Long id, HttpSession session) {
        Long usuarioId = (Long) session.getAttribute("usuarioId");
        
        if (usuarioId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("No autenticado");
        }
        
        try {
            videoServicio.eliminarVideo(id, usuarioId);
            
            Map<String, String> response = new HashMap<>();
            response.put("mensaje", "Video eliminado exitosamente");
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
    }
    
    @PostMapping("/{id}/like")
    public ResponseEntity<?> darLike(@PathVariable Long id, HttpSession session) {
        Long usuarioId = (Long) session.getAttribute("usuarioId");
        
        if (usuarioId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("No autenticado");
        }
        
        try {
            Video video = videoServicio.darLike(id, usuarioId);
            
            Map<String, Object> response = new HashMap<>();
            response.put("likes", video.getLikes());
            response.put("tieneLike", video.getUsuariosLikes().contains(usuarioId));
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
    }
    
    @PostMapping("/{id}/favorito")
    public ResponseEntity<?> marcarFavorito(@PathVariable Long id, HttpSession session) {
        Long usuarioId = (Long) session.getAttribute("usuarioId");
        
        if (usuarioId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("No autenticado");
        }
        
        try {
            Video video = videoServicio.marcarFavorito(id, usuarioId);
            
            Map<String, Object> response = new HashMap<>();
            response.put("esFavorito", video.getUsuariosFavoritos().contains(usuarioId));
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
    }
}
