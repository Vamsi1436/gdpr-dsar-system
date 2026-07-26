import React, { createContext, useContext, useState } from 'react';
import client from '../api/client';

const AuthContext = createContext(null);

export function AuthProvider({ children }) {
const [user, setUser] = useState(() => {
const stored = localStorage.getItem('dsar_user');
return stored ? JSON.parse(stored) : null;
});

async function login(email, password) {
const response = await client.post('/auth/login', { email, password });
const { token, fullName, role, email: userEmail } = response.data;
const loggedInUser = { fullName, role, email: userEmail };
localStorage.setItem('dsar_token', token);
localStorage.setItem('dsar_user', JSON.stringify(loggedInUser));
setUser(loggedInUser);
return loggedInUser;
}

function logout() {
localStorage.removeItem('dsar_token');
localStorage.removeItem('dsar_user');
setUser(null);
}

const value = { user, login, logout };

return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
}

export function useAuth() {
return useContext(AuthContext);
}
