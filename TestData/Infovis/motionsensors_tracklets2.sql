use wmd;
DROP TABLE IF EXISTS motionsensors_tracklets2;
CREATE TABLE  motionsensors_tracklets2 (
  `id` int(10) unsigned NOT NULL auto_increment,
  `start_id` int(10) unsigned NOT NULL default '0',
  `end_id` int(10) unsigned NOT NULL default '0',
  `start_time` bigint(20) unsigned NOT NULL default '0',
  `end_time` bigint(20) unsigned NOT NULL default '0',
  PRIMARY KEY  (`id`),
  KEY `Index_2` (`start_time`),
  KEY `Index_3` (`start_id`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;
LOAD DATA LOCAL INFILE 'tracklets_v2/tracklets_0114.txt' 
     IGNORE INTO TABLE motionsensors_tracklets2
     FIELDS TERMINATED BY '\t' LINES TERMINATED BY '\n'
     (`id`, `start_id`, `end_id`, `start_time`, `end_time`);
LOAD DATA LOCAL INFILE 'tracklets_v2/tracklets_0115.txt' 
     IGNORE INTO TABLE motionsensors_tracklets2
     FIELDS TERMINATED BY '\t' LINES TERMINATED BY '\n'
     (`id`, `start_id`, `end_id`, `start_time`, `end_time`);
LOAD DATA LOCAL INFILE 'tracklets_v2/tracklets_0116.txt' 
     IGNORE INTO TABLE motionsensors_tracklets2
     FIELDS TERMINATED BY '\t' LINES TERMINATED BY '\n'
     (`id`, `start_id`, `end_id`, `start_time`, `end_time`);
LOAD DATA LOCAL INFILE 'tracklets_v2/tracklets_0117.txt'
     IGNORE INTO TABLE motionsensors_tracklets2
     FIELDS TERMINATED BY '\t' LINES TERMINATED BY '\n'
     (`id`, `start_id`, `end_id`, `start_time`, `end_time`);
LOAD DATA LOCAL INFILE 'tracklets_v2/tracklets_0118.txt' 
     IGNORE INTO TABLE motionsensors_tracklets2
     FIELDS TERMINATED BY '\t' LINES TERMINATED BY '\n'
     (`id`, `start_id`, `end_id`, `start_time`, `end_time`);

