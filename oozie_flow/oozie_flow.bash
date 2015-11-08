#!/bin/bash

export OOZIE_URL='http://mapr1node:11000/oozie'
export OOZIE_HOME=/opt/mapr/oozie/oozie-4.1.0
export PATH=$PATH:$OOZIE_HOME/bin

/opt/mapr/oozie/oozie-4.1.0/bin/oozie job -oozie="http://localhost:11000/oozie" -config /user/user01/oozie_flow/job.properties -run

# Check job status. Replace <JobName> with the actual job name
#/opt/mapr/oozie/oozie-4.1.0/bin/oozie job -oozie http://localhost:11000/oozie -info <JobName>
#/opt/mapr/oozie/oozie-4.1.0/bin/oozie job -oozie http://localhost:11000/oozie -log <JobName>
