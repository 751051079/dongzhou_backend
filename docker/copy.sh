#!/bin/sh

# 复制项目的文件到对应docker路径，便于一键生成镜像。
usage() {
	echo "Usage: sh copy.sh"
	exit 1
}


# copy sql
echo "begin copy sql "
cp ../sql/ry_20220814.sql ./mysql/db
cp ../sql/ry_config_20220510.sql ./mysql/db

# copy html
echo "begin copy html "
cp -r ../smarterp-ui/dist/** ./nginx/html/dist


# copy jar
echo "begin copy smarterp-gateway "
cp ../smarterp-gateway/target/smarterp-gateway.jar ./smarterp/gateway/jar

echo "begin copy smarterp-auth "
cp ../smarterp-auth/target/smarterp-auth.jar ./smarterp/auth/jar

echo "begin copy smarterp-visual "
cp ../smarterp-visual/smarterp-monitor/target/smarterp-visual-monitor.jar  ./smarterp/visual/monitor/jar

echo "begin copy smarterp-modules-system "
cp ../smarterp-modules/smarterp-system/target/smarterp-modules-system.jar ./smarterp/modules/system/jar

echo "begin copy smarterp-modules-file "
cp ../smarterp-modules/smarterp-file/target/smarterp-modules-file.jar ./smarterp/modules/file/jar

echo "begin copy smarterp-modules-job "
cp ../smarterp-modules/smarterp-job/target/smarterp-modules-job.jar ./smarterp/modules/job/jar

echo "begin copy smarterp-modules-gen "
cp ../smarterp-modules/smarterp-gen/target/smarterp-modules-gen.jar ./smarterp/modules/gen/jar

