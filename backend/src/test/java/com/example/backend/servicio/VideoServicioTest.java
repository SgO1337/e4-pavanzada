package com.example.backend.servicio;

import com.example.backend.dto.VideoResponse;
import com.example.backend.modelo.Usuario;
import com.example.backend.modelo.Video;
import com.example.backend.repositorio.VideoRepositorio;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class VideoServicioTest {

    @Mock
    private VideoRepositorio videoRepositorio;

    @InjectMocks
    private VideoServicio videoServicio;

    private Usuario usuarioMock;
    private Video videoMock;

    @BeforeEach
    void setUp() {
        usuarioMock = new Usuario();
        usuarioMock.setId(1L);
        usuarioMock.setNombreUsuario("testuser");
        usuarioMock.setNombre("Test User");

        videoMock = new Video();
        videoMock.setId(1L);
        videoMock.setTitulo("Test Video");
        videoMock.setUrlYouTube("https://www.youtube.com/watch?v=dQw4w9WgXcQ");
        videoMock.setYoutubeId("dQw4w9WgXcQ");
        videoMock.setDescripcion("Test description");
        videoMock.setUsuario(usuarioMock);
        videoMock.setLikes(0);
        videoMock.setUsuariosLikes(new ArrayList<>());
        videoMock.setUsuariosFavoritos(new ArrayList<>());
    }

    @Test
    void testAgregarVideo() {
        // Given
        when(videoRepositorio.save(any(Video.class))).thenReturn(videoMock);

        // When
        Video resultado = videoServicio.agregarVideo(
            "Test Video",
            "https://www.youtube.com/watch?v=dQw4w9WgXcQ",
            "Test description",
            usuarioMock
        );

        // Then
        assertNotNull(resultado);
        assertEquals("Test Video", resultado.getTitulo());
        assertEquals("dQw4w9WgXcQ", resultado.getYoutubeId());
        verify(videoRepositorio).save(any(Video.class));
    }

    @Test
    void testExtraerYouTubeIdFormatoWatch() {
        // Given
        when(videoRepositorio.save(any(Video.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        Video resultado = videoServicio.agregarVideo(
            "Test",
            "https://www.youtube.com/watch?v=dQw4w9WgXcQ",
            "Desc",
            usuarioMock
        );

        // Then
        assertEquals("dQw4w9WgXcQ", resultado.getYoutubeId());
    }

    @Test
    void testExtraerYouTubeIdFormatoCorto() {
        // Given
        when(videoRepositorio.save(any(Video.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        Video resultado = videoServicio.agregarVideo(
            "Test",
            "https://youtu.be/dQw4w9WgXcQ",
            "Desc",
            usuarioMock
        );

        // Then
        assertEquals("dQw4w9WgXcQ", resultado.getYoutubeId());
    }

    @Test
    void testObtenerTodos() {
        // Given
        List<Video> videos = Arrays.asList(videoMock);
        when(videoRepositorio.findAllByOrderByFechaCreacionDesc()).thenReturn(videos);

        // When
        List<VideoResponse> resultado = videoServicio.obtenerTodos(1L);

        // Then
        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        assertEquals("Test Video", resultado.get(0).getTitulo());
        verify(videoRepositorio).findAllByOrderByFechaCreacionDesc();
    }

    @Test
    void testDarLikeNuevo() {
        // Given
        when(videoRepositorio.findById(1L)).thenReturn(Optional.of(videoMock));
        when(videoRepositorio.save(any(Video.class))).thenReturn(videoMock);

        // When
        Video resultado = videoServicio.darLike(1L, 2L);

        // Then
        assertEquals(1, resultado.getLikes());
        assertTrue(resultado.getUsuariosLikes().contains(2L));
        verify(videoRepositorio).save(videoMock);
    }

    @Test
    void testQuitarLike() {
        // Given
        videoMock.getUsuariosLikes().add(2L);
        videoMock.setLikes(1);
        when(videoRepositorio.findById(1L)).thenReturn(Optional.of(videoMock));
        when(videoRepositorio.save(any(Video.class))).thenReturn(videoMock);

        // When
        Video resultado = videoServicio.darLike(1L, 2L);

        // Then
        assertEquals(0, resultado.getLikes());
        assertFalse(resultado.getUsuariosLikes().contains(2L));
        verify(videoRepositorio).save(videoMock);
    }

    @Test
    void testMarcarFavorito() {
        // Given
        when(videoRepositorio.findById(1L)).thenReturn(Optional.of(videoMock));
        when(videoRepositorio.save(any(Video.class))).thenReturn(videoMock);

        // When
        Video resultado = videoServicio.marcarFavorito(1L, 2L);

        // Then
        assertTrue(resultado.getUsuariosFavoritos().contains(2L));
        verify(videoRepositorio).save(videoMock);
    }

    @Test
    void testEliminarVideoExitoso() {
        // Given
        when(videoRepositorio.findById(1L)).thenReturn(Optional.of(videoMock));
        doNothing().when(videoRepositorio).delete(videoMock);

        // When
        videoServicio.eliminarVideo(1L, 1L);

        // Then
        verify(videoRepositorio).delete(videoMock);
    }

    @Test
    void testEliminarVideoSinPermiso() {
        // Given
        when(videoRepositorio.findById(1L)).thenReturn(Optional.of(videoMock));

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            videoServicio.eliminarVideo(1L, 999L);
        });

        assertEquals("No tienes permiso para eliminar este video", exception.getMessage());
        verify(videoRepositorio, never()).delete(any());
    }

    @Test
    void testEliminarVideoNoEncontrado() {
        // Given
        when(videoRepositorio.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            videoServicio.eliminarVideo(999L, 1L);
        });

        assertEquals("Video no encontrado", exception.getMessage());
        verify(videoRepositorio, never()).delete(any());
    }
}
