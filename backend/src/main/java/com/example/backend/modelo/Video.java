package com.example.backend.modelo;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "videos")
public class Video {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String titulo;
    
    @Column(nullable = false)
    private String urlYouTube;
    
    @Column(nullable = false)
    private String youtubeId;
    
    private String descripcion;
    
    private Integer likes = 0;
    
    @ManyToOne
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;
    
    @ElementCollection
    @CollectionTable(name = "videos_favoritos", joinColumns = @JoinColumn(name = "video_id"))
    @Column(name = "usuario_id")
    private Set<Long> usuariosFavoritos = new HashSet<>();
    
    @ElementCollection
    @CollectionTable(name = "videos_likes", joinColumns = @JoinColumn(name = "video_id"))
    @Column(name = "usuario_id")
    private Set<Long> usuariosLikes = new HashSet<>();
    
    private LocalDateTime fechaCreacion;
    
    public Video() {
        this.fechaCreacion = LocalDateTime.now();
        this.likes = 0;
    }
    
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
    
    public Usuario getUsuario() {
        return usuario;
    }
    
    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }
    
    public Set<Long> getUsuariosFavoritos() {
        return usuariosFavoritos;
    }
    
    public void setUsuariosFavoritos(Set<Long> usuariosFavoritos) {
        this.usuariosFavoritos = usuariosFavoritos;
    }
    
    public Set<Long> getUsuariosLikes() {
        return usuariosLikes;
    }
    
    public void setUsuariosLikes(Set<Long> usuariosLikes) {
        this.usuariosLikes = usuariosLikes;
    }
    
    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }
    
    public void setFechaCreacion(LocalDateTime fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }
}
