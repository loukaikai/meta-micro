#!/bin/bash

# 删除旧版本JDK
sudo yum remove -y java-1.7.0-openjdk
sudo yum remove -y java-1.8.0-openjdk
# 安装 JDK
sudo yum update -y
sudo yum install -y java-1.8.0-openjdk-devel.x86_6


# 检查 wget 是否已经安装
if command -v wget &> /dev/null; then
    echo "wget is already installed."
else
    # 安装 wget
    sudo yum install -y wget
fi

# 下载和解压 Nacos
NACOS_VERSION=2.1.2
NACOS_FILE=nacos-server-$NACOS_VERSION.tar.gz
NACOS_DOWNLOAD_URL=https://github.com/alibaba/nacos/releases/download/$NACOS_VERSION/$NACOS_FILE

if [ -f $NACOS_FILE ]; then
    echo "$NACOS_FILE already exists, skipping download."
else
    if ! wget --no-check-certificate -O $NACOS_FILE $NACOS_DOWNLOAD_URL; then
        echo "Error: failed to download Nacos $NACOS_VERSION"
        exit 1
    fi
fi

tar -zxvf $NACOS_FILE

# 配置 Nacos
cd nacos/bin
sudo sh startup.sh -m standalone

# 检查 Nacos 是否启动
if [ $? -eq 0 ]; then
    echo "Nacos installation completed!"
else
    echo "Failed to install Nacos. Please check the error message."
fi