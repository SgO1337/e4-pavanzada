package com.example.backend.servicio;

import com.example.backend.modelo.Usuario;
import com.example.backend.repositorio.UsuarioRepositorio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UsuarioServicio {
    
    @Autowired
    private UsuarioRepositorio usuarioRepositorio;
    
    public Usuario registrar(String nombreUsuario, String contrasena, String nombre) {
        if (usuarioRepositorio.existsByNombreUsuario(nombreUsuario)) {
            throw new RuntimeException("El nombre de usuario ya existe");
        }
        
        Usuario usuario = new Usuario();
        usuario.setNombreUsuario(nombreUsuario);
        usuario.setContrasena(contrasena); // En producción, usar encriptación
        usuario.setNombre(nombre);
        
        return usuarioRepositorio.save(usuario);
    }
    
    public Optional<Usuario> login(String nombreUsuario, String contrasena) {
        Optional<Usuario> usuario = usuarioRepositorio.findByNombreUsuario(nombreUsuario);
        
        if (usuario.isPresent() && usuario.get().getContrasena().equals(contrasena)) {
            return usuario;
        }
        
        return Optional.empty();
    }
    
    public Optional<Usuario> obtenerPorId(Long id) {
        return usuarioRepositorio.findById(id);
    }
}
