#!/bin/bash

export OOZIE_URL='http://ip-10-0-0-139.us-west-1.compute.internal:11000/oozie'
export OOZIE_HOME=/opt/mapr/oozie/oozie-4.2.0
export PATH=$PATH:$OOZIE_HOME/bin

$OOZIE_HOME/bin/oozie job -oozie="${OOZIE_URL}" -config /user/ec2-user/oozie_flow/job_aws.properties -run

# Check job status. Replace <JobName> with the actual job name
#$OOZIE_HOME/bin/oozie job -oozie $OOZIE_URL/oozie -info <JobName>
#$OOZIE_HOME/bin/oozie job -oozie $OOZIE_URL/oozie -log <JobName>
