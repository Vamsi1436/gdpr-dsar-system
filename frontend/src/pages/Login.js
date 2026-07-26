import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';

function Login() {
const [email, setEmail] = useState('admin@dsar.local');
const [password, setPassword] = useState('');
const [error, setError] = useState('');
const [loading, setLoading] = useState(false);
const { login } = useAuth();
const navigate = useNavigate();

async function handleSubmit(e) {
e.preventDefault();
setError('');
setLoading(true);
try {
await login(email, password);
navigate('/');
} catch (err) {
setError('Invalid email or password.');
} finally {
setLoading(false);
}
}

return (
<div className="login-wrapper">
<div className="card">
<h2>DSAR Manager Login</h2>
{error && <p className="error-text">{error}</p>}
<form onSubmit={handleSubmit}>
<div className="form-group">
<label>Email</label>
<input type="email" value={email} onChange={(e) => setEmail(e.target.value)} required />
</div>
<div className="form-group">
<label>Password</label>
<input type="password" value={password} onChange={(e) => setPassword(e.target.value)} required />
</div>
<button type="submit" className="btn" disabled={loading}>
{loading ? 'Signing in...' : 'Sign In'}
</button>
</form>
</div>
</div>
);
}

export default Login;
