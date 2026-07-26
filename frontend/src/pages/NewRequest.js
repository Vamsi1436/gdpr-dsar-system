import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import client from '../api/client';

function NewRequest() {
const [subjectName, setSubjectName] = useState('');
const [subjectEmail, setSubjectEmail] = useState('');
const [type, setType] = useState('ACCESS');
const [description, setDescription] = useState('');
const [error, setError] = useState('');
const [submitting, setSubmitting] = useState(false);
const navigate = useNavigate();

async function handleSubmit(e) {
e.preventDefault();
setError('');
setSubmitting(true);
try {
const res = await client.post('/requests', {
subjectName,
subjectEmail,
type,
description,
});
navigate(`/requests/${res.data.id}`);
} catch (err) {
setError('Unable to create request. Please check the fields and try again.');
} finally {
setSubmitting(false);
}
}

return (
<div className="card" style={{ maxWidth: 600 }}>
<h1>Log New DSAR Request</h1>
{error && <p className="error-text">{error}</p>}
<form onSubmit={handleSubmit}>
<div className="form-group">
<label>Data Subject Name</label>
<input value={subjectName} onChange={(e) => setSubjectName(e.target.value)} required />
</div>
<div className="form-group">
<label>Data Subject Email</label>
<input type="email" value={subjectEmail} onChange={(e) => setSubjectEmail(e.target.value)} required />
</div>
<div className="form-group">
<label>Request Type</label>
<select value={type} onChange={(e) => setType(e.target.value)}>
<option value="ACCESS">Access</option>
<option value="ERASURE">Erasure</option>
<option value="RECTIFICATION">Rectification</option>
<option value="PORTABILITY">Portability</option>
<option value="RESTRICTION">Restriction</option>
<option value="OBJECTION">Objection</option>
</select>
</div>
<div className="form-group">
<label>Description / Notes</label>
<textarea rows="4" value={description} onChange={(e) => setDescription(e.target.value)} />
</div>
<button type="submit" className="btn" disabled={submitting}>
{submitting ? 'Creating...' : 'Create Request'}
</button>
</form>
</div>
);
}

export default NewRequest;
