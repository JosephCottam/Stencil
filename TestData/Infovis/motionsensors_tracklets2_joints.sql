use wmd;
DROP TABLE IF EXISTS motionsensors_tracklets2_joints;
CREATE TABLE  motionsensors_tracklets2_joints (
  `id` int(10) unsigned NOT NULL auto_increment,
  `id_from` int(10) unsigned NOT NULL default '0',
  `id_to` int(10) unsigned NOT NULL default '0',
  PRIMARY KEY  (`id`),
  KEY `Index_2` (`id_to`),
  KEY `Index_3` (`id_from`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;
LOAD DATA LOCAL INFILE 'tracklets_v2/joints_0114.txt' 
     IGNORE INTO TABLE motionsensors_tracklets2_joints
     FIELDS TERMINATED BY '\t' LINES TERMINATED BY '\n'
     (`id`, `id_from`, `id_to`);
LOAD DATA LOCAL INFILE 'tracklets_v2/joints_0115.txt' 
     IGNORE INTO TABLE motionsensors_tracklets2_joints
     FIELDS TERMINATED BY '\t' LINES TERMINATED BY '\n'
     (`id`, `id_from`, `id_to`);
LOAD DATA LOCAL INFILE 'tracklets_v2/joints_0116.txt' 
     IGNORE INTO TABLE motionsensors_tracklets2_joints
     FIELDS TERMINATED BY '\t' LINES TERMINATED BY '\n'
     (`id`, `id_from`, `id_to`);
LOAD DATA LOCAL INFILE 'tracklets_v2/joints_0117.txt' 
     IGNORE INTO TABLE motionsensors_tracklets2_joints
     FIELDS TERMINATED BY '\t' LINES TERMINATED BY '\n'
     (`id`, `id_from`, `id_to`);
LOAD DATA LOCAL INFILE 'tracklets_v2/joints_0118.txt' 
     IGNORE INTO TABLE motionsensors_tracklets2_joints
     FIELDS TERMINATED BY '\t' LINES TERMINATED BY '\n'
     (`id`, `id_from`, `id_to`);
