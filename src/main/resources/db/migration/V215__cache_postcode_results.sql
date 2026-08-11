CREATE TABLE court_road_distance_cache (
  postcode        VARCHAR(16) PRIMARY KEY,
  response_json   TEXT NOT NULL,
  computed_at     TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);
