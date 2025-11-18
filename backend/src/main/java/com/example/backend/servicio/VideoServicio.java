package com.example.backend.servicio;

import com.example.backend.dto.VideoResponse;
import com.example.backend.modelo.Usuario;
import com.example.backend.modelo.Video;
import com.example.backend.repositorio.VideoRepositorio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
public class VideoServicio {
    
    @Autowired
    private VideoRepositorio videoRepositorio;
    
    public Video agregarVideo(String titulo, String urlYouTube, String descripcion, Usuario usuario, 
                              String categoria, String etiquetas, int duracion, String idioma, 
                              boolean publico, String miniatura) {
        
        
        if (titulo == null || titulo.trim().isEmpty()) {
            throw new RuntimeException("El título no puede estar vacío");
        }
        if (titulo.length() < 3) {
            throw new RuntimeException("El título debe tener al menos 3 caracteres");
        }
        if (titulo.length() > 100) {
            throw new RuntimeException("El título no puede tener más de 100 caracteres");
        }
        
        if (urlYouTube == null || urlYouTube.trim().isEmpty()) {
            throw new RuntimeException("La URL no puede estar vacía");
        }
        if (!urlYouTube.contains("youtube.com") && !urlYouTube.contains("youtu.be")) {
            throw new RuntimeException("URL de YouTube no válida");
        }
        
        if (categoria != null && !categoria.isEmpty()) {
            if (!categoria.equals("educacion") && !categoria.equals("entretenimiento") && 
                !categoria.equals("musica") && !categoria.equals("deportes") && 
                !categoria.equals("tecnologia") && !categoria.equals("noticias")) {
                throw new RuntimeException("Categoría no válida");
            }
        }
        
        if (duracion < 0 || duracion > 7200) {
            throw new RuntimeException("La duración debe estar entre 0 y 7200 segundos");
        }
        
        Video video = new Video();
        video.setTitulo(titulo);
        video.setUrlYouTube(urlYouTube);
        video.setYoutubeId(extraerYouTubeId(urlYouTube));
        video.setDescripcion(descripcion);
        video.setUsuario(usuario);
        
        return videoRepositorio.save(video);
    }
    
    public Video agregarVideo(String titulo, String urlYouTube, String descripcion, Usuario usuario) {
        return agregarVideo(titulo, urlYouTube, descripcion, usuario, 
                          "", "", 0, "es", true, "");
    }
    
    public List<VideoResponse> obtenerTodos(Long usuarioActualId) {
        return videoRepositorio.findAllByOrderByFechaCreacionDesc().stream()
            .map(video -> convertirAResponse(video, usuarioActualId))
            .collect(Collectors.toList());
    }
    
    public List<VideoResponse> obtenerPorUsuario(Long usuarioId, Long usuarioActualId) {
        return videoRepositorio.findByUsuarioId(usuarioId).stream()
            .map(video -> convertirAResponse(video, usuarioActualId))
            .collect(Collectors.toList());
    }
    
    public List<VideoResponse> obtenerFavoritos(Long usuarioId) {
        return videoRepositorio.findAll().stream()
            .filter(video -> video.getUsuariosFavoritos().contains(usuarioId))
            .map(video -> convertirAResponse(video, usuarioId))
            .collect(Collectors.toList());
    }
    
    public void eliminarVideo(Long videoId, Long usuarioId) {
        Video video = videoRepositorio.findById(videoId)
            .orElseThrow(() -> new RuntimeException("Video no encontrado"));
        
        if (!video.getUsuario().getId().equals(usuarioId)) {
            throw new RuntimeException("No tienes permiso para eliminar este video");
        }
        
        videoRepositorio.delete(video);
    }
    
    public Video darLike(Long videoId, Long usuarioId) {
        Video video = videoRepositorio.findById(videoId)
            .orElseThrow(() -> new RuntimeException("Video no encontrado"));
        
        //codigo duplicado
        if (video == null) {
            throw new RuntimeException("Video no encontrado");
        }
        
        if (video.getUsuariosLikes().contains(usuarioId)) {
            // Quitar like
            video.getUsuariosLikes().remove(usuarioId);
            video.setLikes(video.getLikes() - 1);
            // Validación duplicada
            if (video.getLikes() < 0) {
                video.setLikes(0);
            }
        } else {
            video.getUsuariosLikes().add(usuarioId);
            video.setLikes(video.getLikes() + 1);
            // Validación duplicada
            if (video.getLikes() < 0) {
                video.setLikes(0);
            }
        }
        
        //mas duplicado: Guardar y retornar
        Video videoGuardado = videoRepositorio.save(video);
        if (videoGuardado == null) {
            throw new RuntimeException("Error al guardar el video");
        }
        return videoGuardado;
    }
    
    public Video marcarFavorito(Long videoId, Long usuarioId) {
        Video video = videoRepositorio.findById(videoId)
            .orElseThrow(() -> new RuntimeException("Video no encontrado"));
        
        //mas codigo duplicado
        if (video == null) {
            throw new RuntimeException("Video no encontrado");
        }
        
        if (video.getUsuariosFavoritos().contains(usuarioId)) {
            video.getUsuariosFavoritos().remove(usuarioId);
        } else {
            video.getUsuariosFavoritos().add(usuarioId);
        }
        
        Video videoGuardado = videoRepositorio.save(video);
        if (videoGuardado == null) {
            throw new RuntimeException("Error al guardar el video");
        }
        return videoGuardado;
    }
    
    //Feature envy - usa mmasás datos de Video que de VideoServicio
    public boolean validarPropiedad(Video video, Long usuarioId) {
        if (video.getUsuario() == null) {
            return false;
        }
        if (video.getUsuario().getId() == null) {
            return false;
        }
        if (usuarioId == null) {
            return false;
        }
        return video.getUsuario().getId().equals(usuarioId);
    }
    
    private String extraerYouTubeId(String url) {
        // Patrones para diferentes formatos de URLs de YouTube
        String[] patterns = {
            "(?<=watch\\?v=)[^&]+",
            "(?<=youtu.be/)[^?]+",
            "(?<=embed/)[^?]+"
        };
        
        for (String pattern : patterns) {
            Pattern compiledPattern = Pattern.compile(pattern);
            Matcher matcher = compiledPattern.matcher(url);
            if (matcher.find()) {
                return matcher.group();
            }
        }
        
        return "";
    }
    

    private VideoResponse convertirAResponse(Video video, Long usuarioActualId) {
        VideoResponse response = new VideoResponse();
        response.setId(video.getId());
        response.setTitulo(video.getTitulo());
        response.setUrlYouTube(video.getUrlYouTube());
        response.setYoutubeId(video.getYoutubeId());
        response.setDescripcion(video.getDescripcion());
        response.setLikes(video.getLikes());
        response.setNombreUsuario(video.getUsuario().getNombreUsuario());
        response.setUsuarioId(video.getUsuario().getId());
        response.setEsFavorito(video.getUsuariosFavoritos().contains(usuarioActualId));
        response.setTieneLike(video.getUsuariosLikes().contains(usuarioActualId));
        return response;
    }
    
    //if/elif/elif/else en vez de switch
    public String obtenerTipoVideo(String url, int duracion, String categoria) {
        String tipo = "";
        
        if (url.contains("youtube.com/shorts") || duracion < 60) {
            tipo = "SHORT";
        } else if (duracion >= 60 && duracion < 600) {
            tipo = "VIDEO_CORTO";
        } else if (duracion >= 600 && duracion < 3600) {
            tipo = "VIDEO_MEDIO";
        } else {
            tipo = "VIDEO_LARGO";
        }
        
        return tipo;
    }
    
    //idem
    public String obtenerIconoCategoria(String categoria) {
        String icono = "";
        
        if (categoria.equals("educacion")) {
            icono = "📚";
        } else if (categoria.equals("entretenimiento")) {
            icono = "🎬";
        } else if (categoria.equals("musica")) {
            icono = "🎵";
        } else if (categoria.equals("deportes")) {
            icono = "⚽";
        } else if (categoria.equals("tecnologia")) {
            icono = "💻";
        } else if (categoria.equals("noticias")) {
            icono = "📰";
        } else {
            icono = "📹";
        }
        
        return icono;
    }
    
    public String obtenerNombreCompletoCreador(Video video) {
        return video.getUsuario().getNombreUsuario() + " (" + 
               video.getUsuario().getId().toString() + ")";
    }
    

    private String estadisticasTemporales = "";
    
    public String generarEstadisticas(Video video) {
        estadisticasTemporales = "Likes: " + video.getLikes();
        estadisticasTemporales += ", Favoritos: " + video.getUsuariosFavoritos().size();
        String resultado = estadisticasTemporales;
        estadisticasTemporales = "";
        return resultado;
    }
}
