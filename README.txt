Deploying on AWS

* Setup EC2 to host your webserver
Create EC2 instance running Amazon Linux on t2.micro
Instructions to SSH to EC2 instance found in 'Connect' button on your EC2 dashboard
Install Java 8:
Download Java SE 8 Server JRE rpm from http://www.oracle.com/technetwork/java/javase/downloads/index.html
scp rpm to server
sudo yum install jre-<version>-linux-x64.rpm
Deploy web server:
./gradlew distZip
scp to server and unzip
Confirm in var/conf/server.yml: appName, dataPath, users
chmod u+x *.sh
./start.sh

* Use EFS to serve web assets
Create EFS file system in the same availability zone as your EC2 instance with a 'Name' tag (eg 'webapp')
Add EFS to your Security Group
Add inbound Custom TCP rule for port 2049, source Security Group ID
On EC2 instance:
sudo yum install amazon-efc-utils
sudo mount -t nfs -o nfsvers=4.1,rsize=1048576,wsize=1048576,hard,timeo=600,retrans=2 <efs-system-id>.<availability-zone>.amazonaws.com:/ /var/www/html/efs-mount-point

* Get a Domain Name
Purchase a domain name from a provider (e.g. https://domains.google.com/)
Add resource record: [name = '@', type = 'A', data = 'EC IPv4 Public IP' ]
Add resource record: [name = 'www', type = 'CNAME', data = 'mydomain.com' ]

* Configure Apache httpd with SSL
https://github.com/Neilpang/acme.sh for SSL certs signed by letsencrypt.org
sudo ln -s /home/ec2-user/.acme.sh/mywebapp.com/fullchain.cer /etc/pki/tls/certs/mywebapp_fullchain.crt
sudo ln -s /home/ec2-user/.acme.sh/mywebapp.com/mywebapp.com.key /etc/pki/tls/private/mywebapp.key
sudo yum install mod_ssl

Add the following to /etc/httpd/conf/httpd.conf:
DocumentRoot "/var/www/html/efs-mount-point/mywebapp"
RewriteEngine On
RewriteCond %{HTTPS} off
RewriteRule (.*) https://%{SERVER_NAME}$1 [R,L]

Add the following to /etc/httpd/conf.d/ssl.conf:
SSLCertificateFile /etc/pki/tls/certs/mywebapp_fullchain.crt
SSLCertificateKeyFile /etc/pki/tls/private/mywebapp.key
ProxyPreserveHost On
ProxyPass "/api" "http://localhost:8080"
ProxyPassReverse "/api" "http://localhost:8080"

sudo service httpd restart

Add HTTP and HTTPS inbound rules to AWS Security Group

Auto-deploy via circleci
https://circleci.com/docs/2.0/deployment-integrations/