CREATE EXTENSION IF NOT EXISTS postgis;

CREATE TABLE app_users (
    id uuid PRIMARY KEY,
    name text NOT NULL,
    email text NOT NULL,
    password text NOT NULL,
    role varchar(32) NOT NULL,
    locked boolean NOT NULL DEFAULT false,
    enabled boolean NOT NULL DEFAULT true
);

CREATE UNIQUE INDEX ux_app_users_email_lower ON app_users (lower(email));

CREATE TABLE events (
    id uuid PRIMARY KEY,
    city text NOT NULL,
    normalized_city text,
    description text,
    address text NOT NULL,
    name text NOT NULL,
    normalized_name text,
    keywords jsonb NOT NULL DEFAULT '[]'::jsonb,
    category varchar(64) NOT NULL,
    country_code varchar(8) NOT NULL,
    host text,
    owner_id uuid REFERENCES app_users(id) ON DELETE SET NULL,
    created date,
    updated date,
    recurring boolean NOT NULL DEFAULT false,
    date date NOT NULL,
    weekday varchar(32),
    starts_at time,
    ends_at time,
    capacity integer NOT NULL DEFAULT 0,
    passed boolean NOT NULL DEFAULT false,
    active boolean NOT NULL DEFAULT true,
    cancelled boolean NOT NULL DEFAULT false,
    images jsonb NOT NULL DEFAULT '[]'::jsonb,
    source_type varchar(32),
    source_name text,
    external_id text,
    source_url text,
    last_synced_at timestamptz,
    location geography(Point, 4326)
);

CREATE TABLE event_attendees (
    event_id uuid NOT NULL REFERENCES events(id) ON DELETE CASCADE,
    user_id uuid NOT NULL REFERENCES app_users(id) ON DELETE CASCADE,
    PRIMARY KEY (event_id, user_id)
);

CREATE INDEX idx_events_normalized_city ON events (normalized_city);
CREATE INDEX idx_events_category ON events (category);
CREATE INDEX idx_events_date ON events (date);

CREATE UNIQUE INDEX ux_events_source_identity
    ON events (source_name, external_id)
    WHERE source_name IS NOT NULL AND external_id IS NOT NULL;

CREATE UNIQUE INDEX ux_events_normalized_identity
    ON events (normalized_name, date, normalized_city)
    WHERE normalized_name IS NOT NULL AND date IS NOT NULL AND normalized_city IS NOT NULL;

CREATE INDEX idx_events_location_gist ON events USING gist (location);
CREATE INDEX idx_events_keywords_gin ON events USING gin (keywords jsonb_path_ops);
