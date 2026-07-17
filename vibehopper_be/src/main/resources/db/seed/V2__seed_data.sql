-- Local development seed data
-- All passwords are: password

INSERT INTO app_users (id, name, email, password, role, locked, enabled) VALUES
  ('a0000000-0000-0000-0000-000000000001', 'Admin User',  'admin@vibehopper.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'ADMIN', false, true),
  ('a0000000-0000-0000-0000-000000000002', 'Alice Smith',  'alice@example.com',   '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'USER',  false, true),
  ('a0000000-0000-0000-0000-000000000003', 'Bob Jones',    'bob@example.com',     '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'USER',  false, true),
  ('a0000000-0000-0000-0000-000000000004', 'Carol White',  'carol@example.com',   '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'USER',  false, true);

INSERT INTO events (id, name, normalized_name, city, normalized_city, address, description, category, country_code, owner_id, date, weekday, starts_at, ends_at, capacity, active, recurring, passed, location) VALUES
  (
    'e0000000-0000-0000-0000-000000000001',
    'Stockholm Tech Meetup', 'stockholm tech meetup',
    'Stockholm', 'stockholm', 'Kungsgatan 12, Stockholm',
    'Monthly meetup for developers in Stockholm. All stacks welcome.',
    'MEETUP', 'SE', 'a0000000-0000-0000-0000-000000000001',
    '2026-05-10', 'SUNDAY', '18:00', '21:00', 100, true, true, false,
    ST_SetSRID(ST_MakePoint(18.0686, 59.3293), 4326)::geography
  ),
  (
    'e0000000-0000-0000-0000-000000000002',
    'Berlin Electronic Night', 'berlin electronic night',
    'Berlin', 'berlin', 'Warschauer Str. 65, Berlin',
    'An unforgettable night of electronic music in the heart of Berlin.',
    'MUSIC', 'DE', 'a0000000-0000-0000-0000-000000000002',
    '2026-05-15', 'FRIDAY', '22:00', '06:00', 500, true, false, false,
    ST_SetSRID(ST_MakePoint(13.4050, 52.5200), 4326)::geography
  ),
  (
    'e0000000-0000-0000-0000-000000000003',
    'Barcelona Art Festival', 'barcelona art festival',
    'Barcelona', 'barcelona', 'Passeig de Gràcia 92, Barcelona',
    'Three-day open art festival featuring local and international artists.',
    'ART', 'ES', 'a0000000-0000-0000-0000-000000000003',
    '2026-06-01', 'MONDAY', '10:00', '22:00', 1000, true, false, false,
    ST_SetSRID(ST_MakePoint(2.1734, 41.3851), 4326)::geography
  ),
  (
    'e0000000-0000-0000-0000-000000000004',
    'New York Startup Networking', 'new york startup networking',
    'New York', 'new york', '220 5th Ave, New York',
    'Connect with founders, investors, and builders in NYC.',
    'NETWORKING', 'US', 'a0000000-0000-0000-0000-000000000001',
    '2026-05-20', 'WEDNESDAY', '18:30', '21:30', 150, true, false, false,
    ST_SetSRID(ST_MakePoint(-74.0060, 40.7128), 4326)::geography
  ),
  (
    'e0000000-0000-0000-0000-000000000005',
    'Buenos Aires Tango Night', 'buenos aires tango night',
    'Buenos Aires', 'buenos aires', 'Av. Corrientes 1234, Buenos Aires',
    'Social tango milonga open to all levels.',
    'PARTY', 'AR', 'a0000000-0000-0000-0000-000000000004',
    '2026-05-25', 'MONDAY', '21:00', '02:00', 200, true, false, false,
    ST_SetSRID(ST_MakePoint(-58.3816, -34.6037), 4326)::geography
  ),
  (
    'e0000000-0000-0000-0000-000000000006',
    'London Spring Conference', 'london spring conference',
    'London', 'london', 'ExCeL London, Royal Docks',
    'Annual conference covering cloud, AI, and distributed systems.',
    'CONFERENCE', 'UK', 'a0000000-0000-0000-0000-000000000002',
    '2026-03-15', 'SUNDAY', '09:00', '18:00', 800, true, false, true,
    ST_SetSRID(ST_MakePoint(-0.1278, 51.5074), 4326)::geography
  ),
  (
    'e0000000-0000-0000-0000-000000000007',
    'Tokyo Java Workshop', 'tokyo java workshop',
    'Tokyo', 'tokyo', 'Shibuya Stream, 3 Chome-21-3 Shibuya',
    'Hands-on Java 21 workshop covering virtual threads and structured concurrency.',
    'WORKSHOP', 'JP', 'a0000000-0000-0000-0000-000000000001',
    '2026-06-10', 'WEDNESDAY', '10:00', '17:00', 40, true, false, false,
    ST_SetSRID(ST_MakePoint(139.6917, 35.6895), 4326)::geography
  );

INSERT INTO event_attendees (event_id, user_id) VALUES
  ('e0000000-0000-0000-0000-000000000001', 'a0000000-0000-0000-0000-000000000001'),
  ('e0000000-0000-0000-0000-000000000001', 'a0000000-0000-0000-0000-000000000002'),
  ('e0000000-0000-0000-0000-000000000001', 'a0000000-0000-0000-0000-000000000003'),
  ('e0000000-0000-0000-0000-000000000002', 'a0000000-0000-0000-0000-000000000003'),
  ('e0000000-0000-0000-0000-000000000002', 'a0000000-0000-0000-0000-000000000004'),
  ('e0000000-0000-0000-0000-000000000003', 'a0000000-0000-0000-0000-000000000004'),
  ('e0000000-0000-0000-0000-000000000004', 'a0000000-0000-0000-0000-000000000002'),
  ('e0000000-0000-0000-0000-000000000005', 'a0000000-0000-0000-0000-000000000003'),
  ('e0000000-0000-0000-0000-000000000006', 'a0000000-0000-0000-0000-000000000002'),
  ('e0000000-0000-0000-0000-000000000006', 'a0000000-0000-0000-0000-000000000004'),
  ('e0000000-0000-0000-0000-000000000007', 'a0000000-0000-0000-0000-000000000003');
