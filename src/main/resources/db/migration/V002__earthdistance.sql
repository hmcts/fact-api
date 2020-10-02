CREATE EXTENSION IF NOT EXISTS cube WITH SCHEMA public;
COMMENT ON EXTENSION cube IS 'data type for multidimensional cubes';

CREATE EXTENSION IF NOT EXISTS earthdistance WITH SCHEMA public;
COMMENT ON EXTENSION earthdistance IS 'calculate great-circle distances on the surface of the Earth';
