import React from 'react';
import { BrowserRouter, Routes, Route } from 'react-router-dom';
import { AuthProvider } from './context/AuthContext';
import Navbar from './components/Navbar';
import PrivateRoute from './components/PrivateRoute';
import Login from './pages/Login';
import Dashboard from './pages/Dashboard';
import RequestList from './pages/RequestList';
import RequestDetail from './pages/RequestDetail';
import NewRequest from './pages/NewRequest';
import './App.css';

function App() {
return (
<BrowserRouter>
<AuthProvider>
<Navbar />
<div className="container">
<Routes>
<Route path="/login" element={<Login />} />
<Route path="/" element={
<PrivateRoute>
<Dashboard />
</PrivateRoute>
} />
<Route path="/requests" element={
<PrivateRoute>
<RequestList />
</PrivateRoute>
} />
<Route path="/requests/new" element={
<PrivateRoute roles={['ADMIN']}>
<NewRequest />
</PrivateRoute>
} />
<Route path="/requests/:id" element={
<PrivateRoute>
<RequestDetail />
</PrivateRoute>
} />
</Routes>
</div>
</AuthProvider>
</BrowserRouter>
);
}

export default App;
