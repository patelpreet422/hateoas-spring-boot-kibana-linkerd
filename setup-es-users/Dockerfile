FROM ubuntu:18.04

# Update the package list and install bash and curl
RUN apt-get update && \
    apt-get install -y bash curl

ENTRYPOINT ["/scripts/setup-users.sh"]
