# vertigo-bot-factory

An application booster to easily create your chatbots.

# Docker

Docker images are avaiable at https://hub.docker.com/u/vertigoio

You can found a docker-compose sample file [here](vertigo-bot-docker/docker-compose.yml.sample)

# Quick install

Here is a quick install procedure to install on Linux :

Install Docker :
- Centos : https://docs.docker.com/engine/install/centos/#install-using-the-repository
- Debian : https://docs.docker.com/engine/install/debian/#install-using-the-repository
- Fedora : https://docs.docker.com/engine/install/fedora/#install-using-the-repository
- Ubuntu : https://docs.docker.com/engine/install/ubuntu/#install-using-the-repository

Make sure Docker service is running.

We recommand that you start Docker engine on system boot. https://docs.docker.com/engine/install/linux-postinstall/#configure-docker-to-start-on-boot

Unzip the installer (install.zip) and start `install.sh` with root privilege.

After completion, you can access the designer at `http://IP:8080/designer` and login with credentials Admin/changeme