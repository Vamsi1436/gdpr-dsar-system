import React, { useEffect, useState } from 'react';
import { Link } from 'react-router-dom';
import client from '../api/client';
import StatusBadge from '../components/StatusBadge';

function isOverdue(request) {
if (!request.legalDeadline) return false;
if (request.status === 'COMPLETED' || request.status === 'REJECTED') return false;
return new Date(request.legalDeadline) < new Date();
}

function RequestList() {
const [requests, setRequests] = useState([]);
const [statusFilter, setStatusFilter] = useState('ALL');
const [loading, setLoading] = useState(true);
const [errorMsg, setErrorMsg] = useState('');

useEffect(() => {
client.get('/requests')
.then((res) => setRequests(res.data))
.catch(() => setErrorMsg('Unable to load requests.'))
.finally(() => setLoading(false));
}, []);

const filtered = statusFilter === 'ALL'
? requests
: requests.filter((r) => r.status === statusFilter);

return (
<div>
<h1>All Requests</h1>
{errorMsg && <p className="error-text">{errorMsg}</p>}
<div className="form-group" style={{ maxWidth: 250 }}>
<label>Filter by status</label>
<select value={statusFilter} onChange={(e) => setStatusFilter(e.target.value)}>
<option value="ALL">All</option>
<option value="RECEIVED">Received</option>
<option value="IN_PROGRESS">In Progress</option>
<option value="PENDING_REVIEW">Pending Review</option>
<option value="COMPLETED">Completed</option>
<option value="REJECTED">Rejected</option>
</select>
</div>
{loading ? <p>Loading...</p> : (
<div className="card">
<table>
<thead>
<tr>
<th>Reference</th>
<th>Subject</th>
<th>Type</th>
<th>Status</th>
<th>Assigned To</th>
<th>Deadline</th>
</tr>
</thead>
<tbody>
{filtered.map((r) => (
<tr key={r.id}>
<td><Link to={`/requests/${r.id}`}>{r.referenceCode}</Link></td>
<td>{r.subjectName}</td>
<td>{r.type}</td>
<td><StatusBadge status={r.status} overdue={isOverdue(r)} /></td>
<td>{r.assignedToName || 'Unassigned'}</td>
<td>{r.legalDeadline ? new Date(r.legalDeadline).toLocaleDateString() : '-'}</td>
</tr>
))}
</tbody>
</table>
</div>
)}
</div>
);
}

export default RequestList;
