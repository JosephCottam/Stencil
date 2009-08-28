use wmd;
DROP TABLE IF EXISTS motionsensors_master;
CREATE TABLE  motionsensors_master (
  `Id` int(10) unsigned NOT NULL auto_increment,
  `sensor_id` int(10) unsigned NOT NULL default '0',
  `start_time` bigint(20) unsigned NOT NULL default '0',
  `end_time` bigint(20) unsigned NOT NULL default '0',
  `processed` timestamp NOT NULL default CURRENT_TIMESTAMP on update CURRENT_TIMESTAMP,
  `avg_mag` float NOT NULL default '0',
  PRIMARY KEY  (`Id`),
  UNIQUE KEY `sensor_uniq` (`sensor_id`,`start_time`),
  KEY `start_time_idx` (`start_time`),
  KEY `end_time_idx` (`end_time`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;
LOAD DATA LOCAL INFILE '0114.txt' IGNORE INTO TABLE motionsensors_master
     FIELDS TERMINATED BY ' ' LINES TERMINATED BY '\n'
     (`sensor_id`, `start_time`, `end_time`, `avg_mag`);
LOAD DATA LOCAL INFILE '0115.txt' IGNORE INTO TABLE motionsensors_master
     FIELDS TERMINATED BY ' ' LINES TERMINATED BY '\n'
     (`sensor_id`, `start_time`, `end_time`, `avg_mag`);
LOAD DATA LOCAL INFILE '0116.txt' IGNORE INTO TABLE motionsensors_master
     FIELDS TERMINATED BY ' ' LINES TERMINATED BY '\n'
     (`sensor_id`, `start_time`, `end_time`, `avg_mag`);
LOAD DATA LOCAL INFILE '0117.txt' IGNORE INTO TABLE motionsensors_master
     FIELDS TERMINATED BY ' ' LINES TERMINATED BY '\n'
     (`sensor_id`, `start_time`, `end_time`, `avg_mag`);
LOAD DATA LOCAL INFILE '0118.txt' IGNORE INTO TABLE motionsensors_master
     FIELDS TERMINATED BY ' ' LINES TERMINATED BY '\n'
     (`sensor_id`, `start_time`, `end_time`, `avg_mag`);
