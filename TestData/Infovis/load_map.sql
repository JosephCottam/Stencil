use wmd;
DROP TABLE IF EXISTS sensor_map;
CREATE TABLE sensor_map (
  `sensor_id` int(10) unsigned NOT NULL default '0',
  `x1` float NOT NULL default '0',
  `y1` float NOT NULL default '0',
  `x2` float NOT NULL default '0',
  `y2` float NOT NULL default '0',
  `x3` float NOT NULL default '0',
  `y3` float NOT NULL default '0',
  `x4` float NOT NULL default '0',
  `y4` float NOT NULL default '0',
  `active` tinyint(1) NOT NULL default '1',
  PRIMARY KEY  (`sensor_id`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;
LOAD DATA LOCAL INFILE 'map.txt' INTO TABLE sensor_map
     FIELDS TERMINATED BY ',' LINES TERMINATED BY '\n'
     (`sensor_id`, `x1`, `y1`, `x2`, `y2`, `x3`, `y3`, `x4`, `y4`);
