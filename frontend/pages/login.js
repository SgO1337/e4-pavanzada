import { useState } from 'react';
import { useRouter } from 'next/router';
import api from '../utils/api';

export default function Login() {
  const router = useRouter();
  const [formData, setFormData] = useState({
    nombreUsuario: '',
    contrasena: ''
  });
  const [error, setError] = useState('');

  const handleChange = (e) => {
    setFormData({
      ...formData,
      [e.target.name]: e.target.value
    });
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError('');

    try {
      const response = await api.post('/auth/login', formData);
      if (response.data.usuario) {
        router.push('/');
      }
    } catch (err) {
      setError(err.response?.data?.error || 'Error al iniciar sesión');
    }
  };

  return (
    <div className="auth-container">
      <div className="auth-card">
        <h1>🎬 PlayList de Videos</h1>
        <h2 style={{ textAlign: 'center', marginBottom: '30px', color: '#777' }}>Iniciar Sesión</h2>
        
        {error && <div className="error-message">{error}</div>}
        
        <form onSubmit={handleSubmit}>
          <div className="form-group">
            <label>Usuario</label>
            <input
              type="text"
              name="nombreUsuario"
              value={formData.nombreUsuario}
              onChange={handleChange}
              required
              placeholder="Ingresa tu usuario"
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
              placeholder="Ingresa tu contraseña"
            />
          </div>
          
          <button type="submit" className="btn">Ingresar</button>
          <button 
            type="button" 
            className="btn btn-secondary"
            onClick={() => router.push('/registro')}
          >
            Crear Cuenta
          </button>
        </form>
      </div>
    </div>
  );
}
