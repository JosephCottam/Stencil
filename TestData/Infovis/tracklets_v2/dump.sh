HOST=rodan
USER=root
PASSWORD=''
DB=wmd

for t in 114 115 116 117 118
do
  echo -n $t ": " 
  u=`echo $t + 1 | bc`

  echo -n "track " 
  /usr/local/mysql/bin/mysql -s -h $HOST -D $DB \
     -u $USER --password=$PASSWORD  \
     -e "SELECT id, start_id, end_id, start_time, end_time FROM motionsensors_tracklets2 WHERE start_time > ${t}0000000000 AND start_time < ${u}0000000000 ORDER BY start_time;" \
     > tracklets_0${t}.txt 
 
 echo -n "master " 
  /usr/local/mysql/bin/mysql -s -h $HOST -D $DB \
     -u $USER --password=$PASSWORD  \
     -e "SELECT m.sensor_id, m.start_time, m.tracklet_id FROM motionsensors_master2tracklet m, motionsensors_tracklets2 t WHERE t.start_time > ${t}0000000000 AND t.start_time < ${u}0000000000 AND t.id = m.tracklet_id ORDER BY m.start_time;" \
      > master2track_0${t}.txt 

  echo -n "joints " 
  /usr/local/mysql/bin/mysql -s -h $HOST -D $DB \
     -u $USER --password=$PASSWORD  \
     -e "SELECT distinct(j.id), j.id_from, j.id_to FROM motionsensors_tracklets2_joints j, motionsensors_tracklets2 t WHERE t.start_time > ${t}0000000000 AND t.start_time < ${u}0000000000 AND ( t.id = j.id_from or t.id = j.id_to ) ORDER BY j.id_from;" \
      > joints_0${t}.txt 
  echo -n "compress " 
  gzip *0${t}.txt 
  echo
done

echo "id, start_id, end_id, start_time, end_time" > tracklets_fields.txt 
echo "sensor_id, start_time, tracklet_id" > master2track_fields.txt
echo "id, id_from, id_to" > joints_fields.txt
