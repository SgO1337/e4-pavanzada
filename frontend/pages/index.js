import { useState, useEffect } from 'react';
import { useRouter } from 'next/router';
import api from '../utils/api';

export default function Home() {
  const router = useRouter();
  const [usuario, setUsuario] = useState(null);
  const [videos, setVideos] = useState([]);
  const [activeTab, setActiveTab] = useState('todos');
  const [loading, setLoading] = useState(true);
  const [formData, setFormData] = useState({
    titulo: '',
    urlYouTube: '',
    descripcion: ''
  });
  const [error, setError] = useState('');

  useEffect(() => {
    verificarSesion();
  }, []);

  useEffect(() => {
    if (usuario) {
      cargarVideos();
    }
  }, [activeTab, usuario]);

  const verificarSesion = async () => {
    try {
      const response = await api.get('/auth/sesion');
      if (response.data.autenticado) {
        setUsuario(response.data.usuario);
      } else {
        router.push('/login');
      }
    } catch (err) {
      router.push('/login');
    } finally {
      setLoading(false);
    }
  };

  const cargarVideos = async () => {
    try {
      let endpoint = '/videos';
      if (activeTab === 'favoritos') {
        endpoint = '/videos/favoritos';
      } else if (activeTab === 'mis-videos') {
        endpoint = '/videos/mis-videos';
      }
      
      const response = await api.get(endpoint);
      setVideos(response.data);
    } catch (err) {
      console.error('Error al cargar videos:', err);
    }
  };

  const handleLogout = async () => {
    try {
      await api.post('/auth/logout');
      router.push('/login');
    } catch (err) {
      console.error('Error al cerrar sesión:', err);
    }
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError('');

    if (!formData.titulo || !formData.urlYouTube) {
      setError('Por favor completa los campos obligatorios');
      return;
    }

    try {
      await api.post('/videos', formData);
      setFormData({ titulo: '', urlYouTube: '', descripcion: '' });
      cargarVideos();
    } catch (err) {
      setError(err.response?.data?.error || 'Error al agregar video');
    }
  };

  const handleChange = (e) => {
    setFormData({
      ...formData,
      [e.target.name]: e.target.value
    });
  };

  const handleDelete = async (videoId) => {
    if (confirm('¿Estás seguro de eliminar este video?')) {
      try {
        await api.delete(`/videos/${videoId}`);
        cargarVideos();
      } catch (err) {
        alert('Error al eliminar video');
      }
    }
  };

  const handleLike = async (videoId) => {
    try {
      await api.post(`/videos/${videoId}/like`);
      cargarVideos();
    } catch (err) {
      console.error('Error al dar like:', err);
    }
  };

  const handleFavorite = async (videoId) => {
    try {
      await api.post(`/videos/${videoId}/favorito`);
      cargarVideos();
    } catch (err) {
      console.error('Error al marcar favorito:', err);
    }
  };

  if (loading) {
    return <div className="container">Cargando...</div>;
  }

  return (
    <div>
      <nav className="navbar">
        <div className="navbar-content">
          <h1>🎬 Mi PlayList de Videos</h1>
          <div className="navbar-user">
            <span>Hola, {usuario?.nombre}</span>
            <button onClick={handleLogout} className="btn btn-small">
              Cerrar Sesión
            </button>
          </div>
        </div>
      </nav>

      <div className="container">
        <div className="add-video-form">
          <h2>➕ Agregar Nuevo Video</h2>
          {error && <div className="error-message">{error}</div>}
          <form onSubmit={handleSubmit}>
            <div className="form-row">
              <div className="form-group">
                <label>Título del Video *</label>
                <input
                  type="text"
                  name="titulo"
                  value={formData.titulo}
                  onChange={handleChange}
                  placeholder="Ej: Mi canción favorita"
                  required
                />
              </div>
              <div className="form-group">
                <label>URL de YouTube *</label>
                <input
                  type="url"
                  name="urlYouTube"
                  value={formData.urlYouTube}
                  onChange={handleChange}
                  placeholder="https://www.youtube.com/watch?v=..."
                  required
                />
              </div>
            </div>
            <div className="form-group">
              <label>Descripción (opcional)</label>
              <input
                type="text"
                name="descripcion"
                value={formData.descripcion}
                onChange={handleChange}
                placeholder="Agrega una descripción"
              />
            </div>
            <button type="submit" className="btn">Agregar Video</button>
          </form>
        </div>

        <div className="tabs">
          <button
            className={`tab ${activeTab === 'todos' ? 'active' : ''}`}
            onClick={() => setActiveTab('todos')}
          >
            📺 Todos los Videos
          </button>
          <button
            className={`tab ${activeTab === 'mis-videos' ? 'active' : ''}`}
            onClick={() => setActiveTab('mis-videos')}
          >
            🎥 Mis Videos
          </button>
          <button
            className={`tab ${activeTab === 'favoritos' ? 'active' : ''}`}
            onClick={() => setActiveTab('favoritos')}
          >
            ⭐ Favoritos
          </button>
        </div>

        {videos.length === 0 ? (
          <div className="empty-state">
            <h3>No hay videos aquí</h3>
            <p>
              {activeTab === 'todos' && 'Sé el primero en agregar un video'}
              {activeTab === 'mis-videos' && 'Aún no has agregado videos'}
              {activeTab === 'favoritos' && 'No tienes videos favoritos'}
            </p>
          </div>
        ) : (
          <div className="video-grid">
            {videos.map((video) => (
              <div key={video.id} className="video-card">
                <div className="video-embed">
                  <iframe
                    src={`https://www.youtube.com/embed/${video.youtubeId}`}
                    title={video.titulo}
                    allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture"
                    allowFullScreen
                  />
                </div>
                <div className="video-info">
                  <h3>{video.titulo}</h3>
                  {video.descripcion && <p>{video.descripcion}</p>}
                  <div className="video-meta">
                    <span className="video-author">Por: {video.nombreUsuario}</span>
                    <div className="video-actions">
                      <button
                        className={`icon-btn ${video.tieneLike ? 'active' : ''}`}
                        onClick={() => handleLike(video.id)}
                        title="Me gusta"
                      >
                        👍 {video.likes}
                      </button>
                      <button
                        className={`icon-btn favorite ${video.esFavorito ? 'active' : ''}`}
                        onClick={() => handleFavorite(video.id)}
                        title="Favorito"
                      >
                        {video.esFavorito ? '⭐' : '☆'}
                      </button>
                      {video.usuarioId === usuario?.id && (
                        <button
                          className="icon-btn"
                          onClick={() => handleDelete(video.id)}
                          title="Eliminar"
                        >
                          🗑️
                        </button>
                      )}
                    </div>
                  </div>
                </div>
              </div>
            ))}
          </div>
        )}
      </div>
    </div>
  );
}
