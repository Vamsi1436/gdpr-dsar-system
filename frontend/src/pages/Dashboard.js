import React, { useEffect, useState } from 'react';
import { Link } from 'react-router-dom';
import client from '../api/client';
import StatusBadge from '../components/StatusBadge';

function isOverdue(request) {
if (!request.legalDeadline) return false;
if (request.status === 'COMPLETED' || request.status === 'REJECTED') return false;
return new Date(request.legalDeadline) < new Date();
}

function isAtRisk(request) {
if (!request.legalDeadline) return false;
if (request.status === 'COMPLETED' || request.status === 'REJECTED') return false;
const deadline = new Date(request.legalDeadline);
const now = new Date();
const daysLeft = (deadline - now) / (1000 * 60 * 60 * 24);
return daysLeft >= 0 && daysLeft <= 5;
}

function Dashboard() {
const [requests, setRequests] = useState([]);
const [loading, setLoading] = useState(true);
const [errorMsg, setErrorMsg] = useState('');

useEffect(() => {
client.get('/requests')
.then((res) => setRequests(res.data))
.catch(() => setErrorMsg('Unable to load requests.'))
.finally(() => setLoading(false));
}, []);

const overdueCount = requests.filter(isOverdue).length;
const atRiskCount = requests.filter(isAtRisk).length;
const completedCount = requests.filter((r) => r.status === 'COMPLETED').length;
const openCount = requests.filter((r) => r.status !== 'COMPLETED' && r.status !== 'REJECTED').length;

const recent = [...requests]
.sort((a, b) => new Date(b.receivedAt) - new Date(a.receivedAt))
.slice(0, 8);

return (
<div>
<h1>Dashboard</h1>
{errorMsg && <p className="error-text">{errorMsg}</p>}
{loading ? <p>Loading...</p> : (
<>
<div className="summary-cards">
<div className="summary-card">
<h3>{openCount}</h3>
<p>Open Requests</p>
</div>
<div className="summary-card">
<h3>{atRiskCount}</h3>
<p>At Risk (&lt;= 5 days)</p>
</div>
<div className="summary-card">
<h3>{overdueCount}</h3>
<p>Overdue</p>
</div>
<div className="summary-card">
<h3>{completedCount}</h3>
<p>Completed</p>
</div>
</div>
<div className="card">
<h2>Recent Requests</h2>
<table>
<thead>
<tr>
<th>Reference</th>
<th>Subject</th>
<th>Type</th>
<th>Status</th>
<th>Deadline</th>
</tr>
</thead>
<tbody>
{recent.map((r) => (
<tr key={r.id}>
<td><Link to={`/requests/${r.id}`}>{r.referenceCode}</Link></td>
<td>{r.subjectName}</td>
<td>{r.type}</td>
<td><StatusBadge status={r.status} overdue={isOverdue(r)} /></td>
<td>{r.legalDeadline ? new Date(r.legalDeadline).toLocaleDateString() : '-'}</td>
</tr>
))}
</tbody>
</table>
</div>
</>
)}
</div>
);
}

export default Dashboard;
