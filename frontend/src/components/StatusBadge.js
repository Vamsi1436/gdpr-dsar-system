import React from 'react';

const LABELS = {
RECEIVED: 'Received',
IN_PROGRESS: 'In Progress',
PENDING_REVIEW: 'Pending Review',
COMPLETED: 'Completed',
REJECTED: 'Rejected',
};

const CLASS_MAP = {
RECEIVED: 'badge-received',
IN_PROGRESS: 'badge-in-progress',
PENDING_REVIEW: 'badge-pending-review',
COMPLETED: 'badge-completed',
REJECTED: 'badge-rejected',
};

function StatusBadge({ status, overdue }) {
const className = overdue ? 'badge-overdue' : (CLASS_MAP[status] || 'badge-received');
const label = overdue ? 'Overdue' : (LABELS[status] || status);

return <span className={`badge ${className}`}>{label}</span>;
}

export default StatusBadge;
