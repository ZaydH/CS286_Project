#!/bin/bash

export OOZIE_URL='ip-10-0-0-115.us-west-2.compute.internal:11000/oozie'
export OOZIE_HOME=/opt/mapr/oozie/oozie-4.1.0
export PATH=$PATH:$OOZIE_HOME/bin

$OOZIE_HOME/bin/oozie job -oozie="${OOZIE_URL}" -config /home/ec2-user/CS286_Project/oozie_flow/job_aws.properties -run

# Check job status. Replace <JobName> with the actual job name
#$OOZIE_HOME/bin/oozie job -oozie $OOZIE_URL -info <JobName>
#$OOZIE_HOME/bin/oozie job -oozie $OOZIE_URL -log <JobName>
