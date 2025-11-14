package com.example.backend.controlador;

import com.example.backend.dto.LoginRequest;
import com.example.backend.dto.RegistroRequest;
import com.example.backend.modelo.Usuario;
import com.example.backend.servicio.UsuarioServicio;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:3001"}, allowCredentials = "true")
public class AuthControlador {
    
    @Autowired
    private UsuarioServicio usuarioServicio;
    
    @PostMapping("/registro")
    public ResponseEntity<?> registro(@RequestBody RegistroRequest request) {
        try {
            Usuario usuario = usuarioServicio.registrar(
                request.getNombreUsuario(),
                request.getContrasena(),
                request.getNombre()
            );
            
            Map<String, Object> response = new HashMap<>();
            response.put("mensaje", "Usuario registrado exitosamente");
            response.put("usuario", crearUsuarioResponse(usuario));
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
    }
    
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request, HttpSession session) {
        Optional<Usuario> usuarioOpt = usuarioServicio.login(
            request.getNombreUsuario(),
            request.getContrasena()
        );
        
        if (usuarioOpt.isPresent()) {
            Usuario usuario = usuarioOpt.get();
            session.setAttribute("usuarioId", usuario.getId());
            session.setAttribute("nombreUsuario", usuario.getNombreUsuario());
            
            Map<String, Object> response = new HashMap<>();
            response.put("mensaje", "Login exitoso");
            response.put("usuario", crearUsuarioResponse(usuario));
            
            return ResponseEntity.ok(response);
        } else {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Credenciales inválidas");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
        }
    }
    
    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpSession session) {
        session.invalidate();
        
        Map<String, String> response = new HashMap<>();
        response.put("mensaje", "Logout exitoso");
        
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/sesion")
    public ResponseEntity<?> verificarSesion(HttpSession session) {
        Long usuarioId = (Long) session.getAttribute("usuarioId");
        
        if (usuarioId != null) {
            Optional<Usuario> usuarioOpt = usuarioServicio.obtenerPorId(usuarioId);
            
            if (usuarioOpt.isPresent()) {
                Map<String, Object> response = new HashMap<>();
                response.put("autenticado", true);
                response.put("usuario", crearUsuarioResponse(usuarioOpt.get()));
                return ResponseEntity.ok(response);
            }
        }
        
        Map<String, Object> response = new HashMap<>();
        response.put("autenticado", false);
        return ResponseEntity.ok(response);
    }
    
    private Map<String, Object> crearUsuarioResponse(Usuario usuario) {
        Map<String, Object> usuarioMap = new HashMap<>();
        usuarioMap.put("id", usuario.getId());
        usuarioMap.put("nombreUsuario", usuario.getNombreUsuario());
        usuarioMap.put("nombre", usuario.getNombre());
        return usuarioMap;
    }
}
