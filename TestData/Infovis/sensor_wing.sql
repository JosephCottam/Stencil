use wmd;
DROP TABLE IF EXISTS sensor_wing;
CREATE TABLE  sensor_wing (
  `sensor_id` int(10) unsigned NOT NULL default '0',
  `floor` int(11) NOT NULL default '0',
  `wing` char(1) NOT NULL default 'N',
  PRIMARY KEY  (`sensor_id`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;
LOAD DATA LOCAL INFILE 'wings.txt' INTO TABLE sensor_wing
     FIELDS TERMINATED BY ',' LINES TERMINATED BY '\n'
     (`sensor_id`, `floor`, `wing`);
