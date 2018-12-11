protobuf:
	@echo Compiling proto files
	mvn protobuf:compile -Pproto
	@cp -r target/generated-sources/protobuf/java/* src/main/java

	mvn protobuf:compile-custom -Pproto
	@cp -r target/generated-sources/protobuf/grpc-java/* src/main/java

