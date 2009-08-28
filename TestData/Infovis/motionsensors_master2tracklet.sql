use wmd;
DROP TABLE IF EXISTS motionsensors_master2tracklet;
CREATE TABLE  motionsensors_master2tracklet (
  `sensor_id` int(10) unsigned NOT NULL default '0',
  `start_time` bigint(20) unsigned NOT NULL default '0',
  `tracklet_id` int(10) unsigned NOT NULL default '0',
  PRIMARY KEY  (`sensor_id`,`start_time`),
  KEY `byTracklet` (`tracklet_id`,`start_time`,`sensor_id`),
  KEY `byTime` (`start_time`,`sensor_id`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;
LOAD DATA LOCAL INFILE 'tracklets_v2/master2track_0114.txt' 
     IGNORE INTO TABLE motionsensors_master2tracklet
     FIELDS TERMINATED BY '\t' LINES TERMINATED BY '\n'
     (`sensor_id`, `start_time`, `tracklet_id`);
LOAD DATA LOCAL INFILE 'tracklets_v2/master2track_0115.txt' 
     IGNORE INTO TABLE motionsensors_master2tracklet
     FIELDS TERMINATED BY '\t' LINES TERMINATED BY '\n'
     (`sensor_id`, `start_time`, `tracklet_id`);
LOAD DATA LOCAL INFILE 'tracklets_v2/master2track_0116.txt' 
     IGNORE INTO TABLE motionsensors_master2tracklet
     FIELDS TERMINATED BY '\t' LINES TERMINATED BY '\n'
     (`sensor_id`, `start_time`, `tracklet_id`);
LOAD DATA LOCAL INFILE 'tracklets_v2/master2track_0117.txt' 
     IGNORE INTO TABLE motionsensors_master2tracklet
     FIELDS TERMINATED BY '\t' LINES TERMINATED BY '\n'
     (`sensor_id`, `start_time`, `tracklet_id`);
LOAD DATA LOCAL INFILE 'tracklets_v2/master2track_0118.txt' 
     IGNORE INTO TABLE motionsensors_master2tracklet
     FIELDS TERMINATED BY '\t' LINES TERMINATED BY '\n'
     (`sensor_id`, `start_time`, `tracklet_id`);
