use wmd;
DROP TABLE IF EXISTS sensor_reach;
CREATE TABLE  sensor_reach (
  `id` int(10) unsigned NOT NULL auto_increment,
  `start_tracklet_id` int(10) unsigned NOT NULL default '0',
  `start_sensor_id` int(10) unsigned NOT NULL default '0',
  `start_time` bigint(20) unsigned NOT NULL default '0',
  `end_sensor_id` int(10) unsigned NOT NULL default '0',
  `end_time` bigint(20) unsigned NOT NULL default '0',
  `reach_count` int(10) unsigned NOT NULL default '0',
  `end_tracklet_id` int(10) unsigned NOT NULL default '0',
  PRIMARY KEY  (`id`),
  UNIQUE KEY `Unique` (`start_sensor_id`,`start_time`,`end_sensor_id`,`end_time`),
  KEY `start_sens_idx` (`start_sensor_id`),
  KEY `start_time_idx` (`start_time`),
  KEY `end_sens_idx` (`end_sensor_id`),
  KEY `end_time_idx` (`end_time`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;
LOAD DATA LOCAL INFILE 'reach/reach_0114.txt' 
     IGNORE INTO TABLE sensor_reach
     FIELDS TERMINATED BY '\t' LINES TERMINATED BY '\n'
     (`start_tracklet_id`, `start_sensor_id`, `start_time`, `end_sensor_id`, 
       `end_time`, `reach_count`, `end_tracklet_id`);
LOAD DATA LOCAL INFILE 'reach/reach_0115.txt' 
     IGNORE INTO TABLE sensor_reach
     FIELDS TERMINATED BY '\t' LINES TERMINATED BY '\n'
     (`start_tracklet_id`, `start_sensor_id`, `start_time`, `end_sensor_id`, 
       `end_time`, `reach_count`, `end_tracklet_id`);
LOAD DATA LOCAL INFILE 'reach/reach_0116.txt' 
     IGNORE INTO TABLE sensor_reach
     FIELDS TERMINATED BY '\t' LINES TERMINATED BY '\n'
     (`start_tracklet_id`, `start_sensor_id`, `start_time`, `end_sensor_id`, 
       `end_time`, `reach_count`, `end_tracklet_id`);
LOAD DATA LOCAL INFILE 'reach/reach_0117.txt' 
     IGNORE INTO TABLE sensor_reach
     FIELDS TERMINATED BY '\t' LINES TERMINATED BY '\n'
     (`start_tracklet_id`, `start_sensor_id`, `start_time`, `end_sensor_id`, 
       `end_time`, `reach_count`, `end_tracklet_id`);
LOAD DATA LOCAL INFILE 'reach/reach_0118.txt' 
     IGNORE INTO TABLE sensor_reach
     FIELDS TERMINATED BY '\t' LINES TERMINATED BY '\n'
     (`start_tracklet_id`, `start_sensor_id`, `start_time`, `end_sensor_id`, 
       `end_time`, `reach_count`, `end_tracklet_id`);
