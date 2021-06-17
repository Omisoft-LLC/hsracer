
CREATE TABLE profile ( _id INTEGER NOT NULL, email TEXT, password TEXT, personalId TEXT, alias TEXT, firstName TEXT, lastName TEXT, age INTEGER, city TEXT, country TEXT, PRIMARY KEY(_id) );
CREATE TABLE car ( _id INTEGER NOT NULL, manufacturer TEXT, model TEXT, alias TEXT, year INTEGER, engine INTEGER, volume INTEGER, hpr INTEGER, fuel TEXT, profileId INTEGER, carId TEXT, PRIMARY KEY(_id) );
CREATE TABLE race ( _id INTEGER, name TEXT, description TEXT, locationId INTEGER, raceTimeInMills INTEGER, carTelemetryId INTEGER, gpsDataId INTEGER, videoDataId INTEGER, raceDistance INTEGER, finishPosition INTEGER, carId INTEGER, profileId INTEGER, PRIMARY KEY(_id) );
CREATE TABLE videodata ( _id INTEGER, videoURL TEXT, PRIMARY KEY(_id) );
CREATE TABLE carTelemetry ( _id INTEGER, meanSpeed INTEGER, maxSpeed INTEGER, totalDistance INTEGER, PRIMARY KEY(_id) );
CREATE TABLE locationData ( _id INTEGER, time INTEGER, raceId TEXT,latitude REAL, longitude REAL, distance REAL, PRIMARY KEY(_id) );