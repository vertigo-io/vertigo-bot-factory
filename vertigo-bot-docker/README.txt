
# Build image
docker build -t cf_runner -f .\DockerfileRunner .
docker build -t cf_designer -f .\DockerfileDesigner .

# Run image
docker run -p 127.0.0.1:8080:8080 -d --name designer cf_designer:latest
docker run -p 127.0.0.1:8081:8080 -e designerUrl=http://host.docker.internal:8080/ -d --name runner cf_runner:latest