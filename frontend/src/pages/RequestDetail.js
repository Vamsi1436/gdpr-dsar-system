import React, { useCallback, useEffect, useState } from 'react';
import { useParams } from 'react-router-dom';
import client from '../api/client';
import StatusBadge from '../components/StatusBadge';
import { useAuth } from '../context/AuthContext';

const NEXT_STATUS = {
RECEIVED: ['IN_PROGRESS', 'REJECTED'],
IN_PROGRESS: ['PENDING_REVIEW', 'REJECTED'],
PENDING_REVIEW: ['IN_PROGRESS', 'COMPLETED', 'REJECTED'],
COMPLETED: [],
REJECTED: [],
};

function isOverdue(request) {
if (!request || !request.legalDeadline) return false;
if (request.status === 'COMPLETED' || request.status === 'REJECTED') return false;
return new Date(request.legalDeadline) < new Date();
}

function RequestDetail() {
const { id } = useParams();
const { user } = useAuth();
const [request, setRequest] = useState(null);
const [auditLogs, setAuditLogs] = useState([]);
const [attachments, setAttachments] = useState([]);
const [loading, setLoading] = useState(true);
const [errorMsg, setErrorMsg] = useState('');
const [draft, setDraft] = useState('');
const [draftLoading, setDraftLoading] = useState(false);
const [file, setFile] = useState(null);

const loadData = useCallback(() => {
setLoading(true);
Promise.all([
client.get(`/requests/${id}`),
client.get(`/audit-logs/request/${id}`).catch(() => ({ data: [] })),
client.get(`/attachments/request/${id}`).catch(() => ({ data: [] })),
])
.then(([reqRes, auditRes, attRes]) => {
setRequest(reqRes.data);
setAuditLogs(auditRes.data);
setAttachments(attRes.data);
})
.catch(() => setErrorMsg('Unable to load request details.'))
.finally(() => setLoading(false));
}, [id]);

useEffect(() => {
loadData();
}, [loadData]);

async function handleStatusChange(newStatus) {
try {
await client.patch(`/requests/${id}/status`, { status: newStatus });
loadData();
} catch (err) {
setErrorMsg('Unable to update status.');
}
}

async function handleFileUpload(e) {
e.preventDefault();
if (!file) return;
const formData = new FormData();
formData.append('file', file);
formData.append('requestId', id);
try {
await client.post('/attachments', formData, {
headers: { 'Content-Type': 'multipart/form-data' },
});
setFile(null);
loadData();
} catch (err) {
setErrorMsg('Unable to upload attachment.');
}
}

async function handleDraftLetter() {
setDraftLoading(true);
try {
const res = await client.post(`/requests/${id}/draft-letter`);
setDraft(res.data.draft || res.data.message || '');
} catch (err) {
setDraft('Unable to generate draft at this time.');
} finally {
setDraftLoading(false);
}
}

if (loading) return <p>Loading...</p>;
if (errorMsg && !request) return <p className="error-text">{errorMsg}</p>;
if (!request) return null;

const nextStatuses = NEXT_STATUS[request.status] || [];

return (
<div>
<h1>{request.referenceCode}</h1>
{errorMsg && <p className="error-text">{errorMsg}</p>}
<div className="card">
<p><strong>Subject:</strong> {request.subjectName} ({request.subjectEmail})</p>
<p><strong>Type:</strong> {request.type}</p>
<p><strong>Status:</strong> <StatusBadge status={request.status} overdue={isOverdue(request)} /></p>
<p><strong>Received:</strong> {new Date(request.receivedAt).toLocaleString()}</p>
<p><strong>Legal Deadline:</strong> {request.legalDeadline ? new Date(request.legalDeadline).toLocaleString() : '-'}</p>
<p><strong>Assigned To:</strong> {request.assignedToName || 'Unassigned'}</p>
<p><strong>Description:</strong> {request.description || '-'}</p>

{nextStatuses.length > 0 && (user.role === 'ADMIN' || user.role === 'CASE_HANDLER') && (
<div>
<strong>Change status:</strong>{' '}
{nextStatuses.map((s) => (
<button key={s} className="btn" style={{ marginRight: 8 }} onClick={() => handleStatusChange(s)}>
{s}
</button>
))}
</div>
)}
</div>

<div className="card">
<h2>Attachments</h2>
<ul>
{attachments.map((a) => (
<li key={a.id}>{a.fileName} {a.redacted ? '(redacted)' : ''}</li>
))}
</ul>
{(user.role === 'ADMIN' || user.role === 'CASE_HANDLER') && (
<form onSubmit={handleFileUpload}>
<input type="file" onChange={(e) => setFile(e.target.files[0])} />
<button type="submit" className="btn" style={{ marginLeft: 8 }}>Upload</button>
</form>
)}
</div>

<div className="card">
<h2>Draft Response Letter (optional AI assist)</h2>
<button className="btn" onClick={handleDraftLetter} disabled={draftLoading}>
{draftLoading ? 'Generating...' : 'Generate Draft'}
</button>
{draft && <p style={{ whiteSpace: 'pre-wrap', marginTop: 12 }}>{draft}</p>}
</div>

<div className="card">
<h2>Audit Trail</h2>
<table>
<thead>
<tr>
<th>Action</th>
<th>Performed By</th>
<th>Details</th>
<th>When</th>
</tr>
</thead>
<tbody>
{auditLogs.map((log) => (
<tr key={log.id}>
<td>{log.action}</td>
<td>{log.performedBy}</td>
<td>{log.details}</td>
<td>{new Date(log.createdAt).toLocaleString()}</td>
</tr>
))}
</tbody>
</table>
</div>
</div>
);
}

export default RequestDetail;
