package com.example.backend.dto;

public class VideoResponse {
    private Long id;
    private String titulo;
    private String urlYouTube;
    private String youtubeId;
    private String descripcion;
    private Integer likes;
    private String nombreUsuario;
    private Long usuarioId;
    private boolean esFavorito;
    private boolean tieneLike;
    
    public VideoResponse() {}
    
    // Getters y Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getTitulo() {
        return titulo;
    }
    
    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }
    
    public String getUrlYouTube() {
        return urlYouTube;
    }
    
    public void setUrlYouTube(String urlYouTube) {
        this.urlYouTube = urlYouTube;
    }
    
    public String getYoutubeId() {
        return youtubeId;
    }
    
    public void setYoutubeId(String youtubeId) {
        this.youtubeId = youtubeId;
    }
    
    public String getDescripcion() {
        return descripcion;
    }
    
    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }
    
    public Integer getLikes() {
        return likes;
    }
    
    public void setLikes(Integer likes) {
        this.likes = likes;
    }
    
    public String getNombreUsuario() {
        return nombreUsuario;
    }
    
    public void setNombreUsuario(String nombreUsuario) {
        this.nombreUsuario = nombreUsuario;
    }
    
    public Long getUsuarioId() {
        return usuarioId;
    }
    
    public void setUsuarioId(Long usuarioId) {
        this.usuarioId = usuarioId;
    }
    
    public boolean isEsFavorito() {
        return esFavorito;
    }
    
    public void setEsFavorito(boolean esFavorito) {
        this.esFavorito = esFavorito;
    }
    
    public boolean isTieneLike() {
        return tieneLike;
    }
    
    public void setTieneLike(boolean tieneLike) {
        this.tieneLike = tieneLike;
    }
}
