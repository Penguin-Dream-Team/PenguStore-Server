create table beacons (
      num_items INT,
      latitude DOUBLE NOT NULL,
      longitude DOUBLE NOT NULL,
      PRIMARY KEY (latitude, longitude)
);