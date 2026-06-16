CREATE EXTENSION IF NOT EXISTS pgcrypto;

CREATE TABLE products (
    id          UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name        VARCHAR(255) NOT NULL,
    description TEXT,
    owner       VARCHAR(255),
    tags        TEXT[] NOT NULL DEFAULT '{}',
    created_at  TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at  TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    created_by  VARCHAR(255),
    updated_by  VARCHAR(255),
    version     BIGINT NOT NULL DEFAULT 0
);

CREATE INDEX idx_products_name ON products (name);
CREATE INDEX idx_products_owner ON products (owner);
CREATE INDEX idx_products_tags ON products USING GIN (tags);

CREATE TABLE engagements (
    id          UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    product_id  UUID NOT NULL REFERENCES products (id) ON DELETE CASCADE,
    name        VARCHAR(255) NOT NULL,
    description TEXT,
    target      VARCHAR(500),
    status      VARCHAR(50) NOT NULL,
    start_date  TIMESTAMPTZ NOT NULL,
    end_date    TIMESTAMPTZ,
    created_at  TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at  TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    created_by  VARCHAR(255),
    updated_by  VARCHAR(255),
    version     BIGINT NOT NULL DEFAULT 0,
    CONSTRAINT chk_engagement_dates CHECK (end_date IS NULL OR end_date >= start_date)
);

CREATE INDEX idx_engagements_product_id ON engagements (product_id);
CREATE INDEX idx_engagements_status ON engagements (status);
CREATE INDEX idx_engagements_name ON engagements (name);

CREATE TABLE findings (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    engagement_id   UUID NOT NULL REFERENCES engagements (id) ON DELETE CASCADE,
    title           VARCHAR(500) NOT NULL,
    description     TEXT NOT NULL,
    severity        VARCHAR(50) NOT NULL,
    status          VARCHAR(50) NOT NULL DEFAULT 'Open',
    cvss_score      NUMERIC(3, 1),
    cve             VARCHAR(50),
    cwe             VARCHAR(50),
    mitigation      TEXT,
    impact          TEXT,
    reference_links TEXT[] NOT NULL DEFAULT '{}',
    discovered_date DATE,
    created_at      TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    created_by      VARCHAR(255),
    updated_by      VARCHAR(255),
    version         BIGINT NOT NULL DEFAULT 0
);

CREATE INDEX idx_findings_engagement_id ON findings (engagement_id);
CREATE INDEX idx_findings_severity ON findings (severity);
CREATE INDEX idx_findings_status ON findings (status);
CREATE INDEX idx_findings_title ON findings (title);
