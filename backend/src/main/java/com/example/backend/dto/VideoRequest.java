package com.example.backend.dto;

public class VideoRequest {
    private String titulo;
    private String urlYouTube;
    private String descripcion;
    
    public VideoRequest() {}
    
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
    
    public String getDescripcion() {
        return descripcion;
    }
    
    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }
}
