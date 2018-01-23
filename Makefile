# Use Makefile to build and run the code in a docker container

IMAGE_NAME="stats-challenge"
PORT=8080

.PHONY: build
build:
	docker build -t $(IMAGE_NAME) .

.PHONY: run
run:
	docker run -ti  -p $(PORT):$(PORT) --rm $(IMAGE_NAME)

