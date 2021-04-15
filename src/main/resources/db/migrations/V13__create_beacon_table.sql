create table beacons (
      num_items INT,
      latitude FLOAT NOT NULL,
      longitude FLOAT NOT NULL,
      PRIMARY KEY (latitude, longitude)
);