#!/bin/bash

/opt/mapr/oozie/oozie-4.1.0/bin/oozie job -oozie="http://localhost:11000/oozie" -config /user/user01/CS286_Project/oozie_flow/job.properties -run
