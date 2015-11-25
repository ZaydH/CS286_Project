sudo su

hadoop fs -chmod -R 777 /user

export OOZIE_IP="ip-10-0-0-139.us-west-1.compute.internal"

yum install -y mapr-oozie 
sudo service mapr-warden restart
export OOZIE_URL="http://${OOZIE_IP}:11000/oozie"

/opt/mapr/oozie/oozie-4.2.0/bin/oozie admin -status

# Update Job tracker to jobTracker=<OOZIE_IP>:8032
# Update OOZIE_HOME to 4.2.0
# Update command to call oozie job to 4.2.0
# Ensure to use the update the command when checking the log and info to represent the updated version (4.2.0) and the correct OOZIE_URL

# Amazon base install of Oozie on MAPR did not work.  Had to reinstall to 4.2.0.

# May need to reinstall Maven for the preprocessor.  See here: http://preilly.me/2013/05/10/how-to-install-maven-on-centos/


su ec2-user
git clone https://<YourUsername>@github.com/ZaydH/CS286_Project.git
cd CS286_Project
bash rebuild_all_aws.bash
bash oozie_flow/oozie_flow_aws.bash