import { useState } from 'react';
import { useRouter } from 'next/router';
import api from '../utils/api';

export default function Registro() {
  const router = useRouter();
  const [formData, setFormData] = useState({
    nombreUsuario: '',
    contrasena: '',
    nombre: ''
  });
  const [error, setError] = useState('');
  const [success, setSuccess] = useState('');

  const handleChange = (e) => {
    setFormData({
      ...formData,
      [e.target.name]: e.target.value
    });
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError('');
    setSuccess('');

    try {
      const response = await api.post('/auth/registro', formData);
      setSuccess('¡Cuenta creada exitosamente! Redirigiendo...');
      setTimeout(() => {
        router.push('/login');
      }, 2000);
    } catch (err) {
      setError(err.response?.data?.error || 'Error al registrar usuario');
    }
  };

  return (
    <div className="auth-container">
      <div className="auth-card">
        <h1>🎬 PlayList de Videos</h1>
        <h2 style={{ textAlign: 'center', marginBottom: '30px', color: '#777' }}>Crear Cuenta</h2>
        
        {error && <div className="error-message">{error}</div>}
        {success && <div className="success-message">{success}</div>}
        
        <form onSubmit={handleSubmit}>
          <div className="form-group">
            <label>Nombre Completo</label>
            <input
              type="text"
              name="nombre"
              value={formData.nombre}
              onChange={handleChange}
              required
              placeholder="Ej: Juan Pérez"
            />
          </div>
          
          <div className="form-group">
            <label>Usuario</label>
            <input
              type="text"
              name="nombreUsuario"
              value={formData.nombreUsuario}
              onChange={handleChange}
              required
              placeholder="Elige un nombre de usuario"
            />
          </div>
          
          <div className="form-group">
            <label>Contraseña</label>
            <input
              type="password"
              name="contrasena"
              value={formData.contrasena}
              onChange={handleChange}
              required
              placeholder="Crea una contraseña"
              minLength="4"
            />
          </div>
          
          <button type="submit" className="btn">Registrarse</button>
          <button 
            type="button" 
            className="btn btn-secondary"
            onClick={() => router.push('/login')}
          >
            Ya tengo cuenta
          </button>
        </form>
      </div>
    </div>
  );
}
