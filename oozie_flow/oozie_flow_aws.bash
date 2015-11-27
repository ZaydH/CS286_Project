#!/bin/bash

#export SYSTEM_FULL_URL=ip-10-0-0-147.us-west-2.compute.internal

export OOZIE_URL="http://ip-10-196-77-178:11000/oozie"
export OOZIE_HOME=/opt/mapr/oozie/oozie-4.0.1
export PATH=$PATH:$OOZIE_HOME/bin

$OOZIE_HOME/bin/oozie job -oozie="${OOZIE_URL}" -config /home/ec2-user/CS286_Project/oozie_flow/job_aws.properties -run

# Check job status. Replace <JobName> with the actual job name
#$OOZIE_HOME/bin/oozie job -oozie $OOZIE_URL -info <JobName>
#$OOZIE_HOME/bin/oozie job -oozie $OOZIE_URL -log <JobName>

#/opt/mapr/oozie/oozie-4.0.1/bin/oozie job -oozie $OOZIE_URL -info <JobName>

