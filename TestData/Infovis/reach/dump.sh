HOST=rodan
USER=root
PASSWORD=''
DB=wmd

for t in 114 115 116 117 118
do
  echo -n $t 
  u=`echo $t + 1 | bc`

  /usr/local/mysql/bin/mysql -s -h $HOST -D $DB \
     -u $USER --password=$PASSWORD  \
     -e "SELECT start_tracklet_id, start_sensor_id, start_time, end_sensor_id, end_time, reach_count, end_tracklet_id FROM sensor_reach WHERE start_time > ${t}0000000000 AND start_time < ${u}0000000000 ORDER BY start_time;" \
     > reach_0${t}.txt 

  echo
done

