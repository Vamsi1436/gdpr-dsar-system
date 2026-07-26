CREATE TABLE users (
  id BIGSERIAL PRIMARY KEY,
  email VARCHAR(255) NOT NULL UNIQUE,
  password VARCHAR(255) NOT NULL,
  full_name VARCHAR(255) NOT NULL,
  role VARCHAR(50) NOT NULL,
  enabled BOOLEAN NOT NULL DEFAULT TRUE,
  created_at TIMESTAMPTZ NOT NULL DEFAULT now()
  );

CREATE TABLE dsar_requests (
  id BIGSERIAL PRIMARY KEY,
  reference_code VARCHAR(50) NOT NULL UNIQUE,
  subject_name VARCHAR(255) NOT NULL,
  subject_email VARCHAR(255) NOT NULL,
  type VARCHAR(50) NOT NULL,
  status VARCHAR(50) NOT NULL,
  description TEXT,
  assigned_to BIGINT REFERENCES users(id),
  received_at TIMESTAMPTZ NOT NULL,
  legal_deadline TIMESTAMPTZ NOT NULL,
  completed_at TIMESTAMPTZ,
  created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
  updated_at TIMESTAMPTZ
  );

CREATE TABLE audit_log (
  id BIGSERIAL PRIMARY KEY,
  request_id BIGINT NOT NULL REFERENCES dsar_requests(id),
  performed_by BIGINT REFERENCES users(id),
  action VARCHAR(50) NOT NULL,
  details TEXT,
  created_at TIMESTAMPTZ NOT NULL DEFAULT now()
  );

CREATE TABLE attachments (
  id BIGSERIAL PRIMARY KEY,
  request_id BIGINT NOT NULL REFERENCES dsar_requests(id),
  file_name VARCHAR(255) NOT NULL,
  content_type VARCHAR(255),
  storage_path VARCHAR(500) NOT NULL,
  redacted BOOLEAN NOT NULL DEFAULT FALSE,
  uploaded_by BIGINT,
  uploaded_at TIMESTAMPTZ NOT NULL DEFAULT now()
  );

CREATE INDEX idx_dsar_requests_status ON dsar_requests(status);
CREATE INDEX idx_dsar_requests_assigned_to ON dsar_requests(assigned_to);
CREATE INDEX idx_audit_log_request_id ON audit_log(request_id);
CREATE INDEX idx_attachments_request_id ON attachments(request_id);
