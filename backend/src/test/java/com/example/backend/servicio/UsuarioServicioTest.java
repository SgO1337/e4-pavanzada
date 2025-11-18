package com.example.backend.servicio;

import com.example.backend.modelo.Usuario;
import com.example.backend.repositorio.UsuarioRepositorio;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UsuarioServicioTest {

    @Mock
    private UsuarioRepositorio usuarioRepositorio;

    @InjectMocks
    private UsuarioServicio usuarioServicio;

    private Usuario usuarioMock;

    @BeforeEach
    void setUp() {
        usuarioMock = new Usuario();
        usuarioMock.setId(1L);
        usuarioMock.setNombreUsuario("testuser");
        usuarioMock.setContrasena("password123");
        usuarioMock.setNombre("Test User");
    }

    @Test
    void testRegistrarUsuarioExitoso() {
        when(usuarioRepositorio.existsByNombreUsuario(anyString())).thenReturn(false);
        when(usuarioRepositorio.save(any(Usuario.class))).thenReturn(usuarioMock);

        Usuario resultado = usuarioServicio.registrar("testuser", "password123", "Test User");

        assertNotNull(resultado);
        assertEquals("testuser", resultado.getNombreUsuario());
        assertEquals("Test User", resultado.getNombre());
        verify(usuarioRepositorio).existsByNombreUsuario("testuser");
        verify(usuarioRepositorio).save(any(Usuario.class));
    }

    @Test
    void testRegistrarUsuarioDuplicado() {
        when(usuarioRepositorio.existsByNombreUsuario("testuser")).thenReturn(true);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            usuarioServicio.registrar("testuser", "password123", "Test User");
        });

        assertEquals("El nombre de usuario ya existe", exception.getMessage());
        verify(usuarioRepositorio).existsByNombreUsuario("testuser");
        verify(usuarioRepositorio, never()).save(any(Usuario.class));
    }

    @Test
    void testLoginExitoso() {
        when(usuarioRepositorio.findByNombreUsuario("testuser")).thenReturn(Optional.of(usuarioMock));

        Optional<Usuario> resultado = usuarioServicio.login("testuser", "password123");

        assertTrue(resultado.isPresent());
        assertEquals("testuser", resultado.get().getNombreUsuario());
        verify(usuarioRepositorio).findByNombreUsuario("testuser");
    }

    @Test
    void testLoginConContrasenaIncorrecta() {
        when(usuarioRepositorio.findByNombreUsuario("testuser")).thenReturn(Optional.of(usuarioMock));

        Optional<Usuario> resultado = usuarioServicio.login("testuser", "wrongpassword");

        assertFalse(resultado.isPresent());
        verify(usuarioRepositorio).findByNombreUsuario("testuser");
    }

    @Test
    void testLoginConUsuarioInexistente() {
        when(usuarioRepositorio.findByNombreUsuario("noexiste")).thenReturn(Optional.empty());

        Optional<Usuario> resultado = usuarioServicio.login("noexiste", "password123");
        assertFalse(resultado.isPresent());
        verify(usuarioRepositorio).findByNombreUsuario("noexiste");
    }

    @Test
    void testObtenerPorId() {

        when(usuarioRepositorio.findById(1L)).thenReturn(Optional.of(usuarioMock));


        Optional<Usuario> resultado = usuarioServicio.obtenerPorId(1L);

        assertTrue(resultado.isPresent());
        assertEquals(1L, resultado.get().getId());
        verify(usuarioRepositorio).findById(1L);
    }
}
