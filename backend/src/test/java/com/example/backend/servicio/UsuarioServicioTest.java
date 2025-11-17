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
        // Given
        when(usuarioRepositorio.existsByNombreUsuario(anyString())).thenReturn(false);
        when(usuarioRepositorio.save(any(Usuario.class))).thenReturn(usuarioMock);

        // When
        Usuario resultado = usuarioServicio.registrar("testuser", "password123", "Test User");

        // Then
        assertNotNull(resultado);
        assertEquals("testuser", resultado.getNombreUsuario());
        assertEquals("Test User", resultado.getNombre());
        verify(usuarioRepositorio).existsByNombreUsuario("testuser");
        verify(usuarioRepositorio).save(any(Usuario.class));
    }

    @Test
    void testRegistrarUsuarioDuplicado() {
        // Given
        when(usuarioRepositorio.existsByNombreUsuario("testuser")).thenReturn(true);

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            usuarioServicio.registrar("testuser", "password123", "Test User");
        });

        assertEquals("El nombre de usuario ya existe", exception.getMessage());
        verify(usuarioRepositorio).existsByNombreUsuario("testuser");
        verify(usuarioRepositorio, never()).save(any(Usuario.class));
    }

    @Test
    void testLoginExitoso() {
        // Given
        when(usuarioRepositorio.findByNombreUsuario("testuser")).thenReturn(Optional.of(usuarioMock));

        // When
        Optional<Usuario> resultado = usuarioServicio.login("testuser", "password123");

        // Then
        assertTrue(resultado.isPresent());
        assertEquals("testuser", resultado.get().getNombreUsuario());
        verify(usuarioRepositorio).findByNombreUsuario("testuser");
    }

    @Test
    void testLoginConContrasenaIncorrecta() {
        // Given
        when(usuarioRepositorio.findByNombreUsuario("testuser")).thenReturn(Optional.of(usuarioMock));

        // When
        Optional<Usuario> resultado = usuarioServicio.login("testuser", "wrongpassword");

        // Then
        assertFalse(resultado.isPresent());
        verify(usuarioRepositorio).findByNombreUsuario("testuser");
    }

    @Test
    void testLoginConUsuarioInexistente() {
        // Given
        when(usuarioRepositorio.findByNombreUsuario("noexiste")).thenReturn(Optional.empty());

        // When
        Optional<Usuario> resultado = usuarioServicio.login("noexiste", "password123");

        // Then
        assertFalse(resultado.isPresent());
        verify(usuarioRepositorio).findByNombreUsuario("noexiste");
    }

    @Test
    void testObtenerPorId() {
        // Given
        when(usuarioRepositorio.findById(1L)).thenReturn(Optional.of(usuarioMock));

        // When
        Optional<Usuario> resultado = usuarioServicio.obtenerPorId(1L);

        // Then
        assertTrue(resultado.isPresent());
        assertEquals(1L, resultado.get().getId());
        verify(usuarioRepositorio).findById(1L);
    }
}
