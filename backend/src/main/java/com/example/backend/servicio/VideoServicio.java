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
    
    public Video agregarVideo(String titulo, String urlYouTube, String descripcion, Usuario usuario) {
        Video video = new Video();
        video.setTitulo(titulo);
        video.setUrlYouTube(urlYouTube);
        video.setYoutubeId(extraerYouTubeId(urlYouTube));
        video.setDescripcion(descripcion);
        video.setUsuario(usuario);
        
        return videoRepositorio.save(video);
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
        
        if (video.getUsuariosLikes().contains(usuarioId)) {
            video.getUsuariosLikes().remove(usuarioId);
            video.setLikes(video.getLikes() - 1);
        } else {
            video.getUsuariosLikes().add(usuarioId);
            video.setLikes(video.getLikes() + 1);
        }
        
        return videoRepositorio.save(video);
    }
    
    public Video marcarFavorito(Long videoId, Long usuarioId) {
        Video video = videoRepositorio.findById(videoId)
            .orElseThrow(() -> new RuntimeException("Video no encontrado"));
        
        if (video.getUsuariosFavoritos().contains(usuarioId)) {
            video.getUsuariosFavoritos().remove(usuarioId);
        } else {
            video.getUsuariosFavoritos().add(usuarioId);
        }
        
        return videoRepositorio.save(video);
    }
    
    private String extraerYouTubeId(String url) {
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
}
