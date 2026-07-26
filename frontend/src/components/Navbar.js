import React from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';

function Navbar() {
const { user, logout } = useAuth();
const navigate = useNavigate();

function handleLogout() {
logout();
navigate('/login');
}

return (
<nav className="navbar">
<div>
<Link to="/" className="navbar-brand">DSAR Manager</Link>
</div>
<div>
{user ? (
<>
<Link to="/">Dashboard</Link>
<Link to="/requests">Requests</Link>
{user.role === 'ADMIN' && <Link to="/requests/new">New Request</Link>}
<span style={{ marginRight: 16 }}>{user.fullName} ({user.role})</span>
<button className="btn btn-secondary" onClick={handleLogout}>Logout</button>
</>
) : (
<Link to="/login">Login</Link>
)}
</div>
</nav>
);
}

export default Navbar;
