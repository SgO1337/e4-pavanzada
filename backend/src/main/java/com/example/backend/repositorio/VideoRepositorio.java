package com.example.backend.repositorio;

import com.example.backend.modelo.Video;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VideoRepositorio extends JpaRepository<Video, Long> {
    List<Video> findByUsuarioId(Long usuarioId);
    List<Video> findAllByOrderByFechaCreacionDesc();
}
