#!/bin/bash

# Docker Hub 用户名
DOCKER_USERNAME="qufuhxd183"
IMAGE_NAME="haidong-tuanwei"
TAG="latest"

# 颜色定义
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# 打印信息
info() {
    echo -e "${GREEN}[INFO]${NC} $1"
}

warn() {
    echo -e "${YELLOW}[WARN]${NC} $1"
}

error() {
    echo -e "${RED}[ERROR]${NC} $1"
}

# 检查 Docker 是否安装
check_docker() {
    if ! command -v docker &> /dev/null; then
        error "Docker 未安装，请先安装 Docker"
        exit 1
    fi
    info "Docker 已安装 ✓"
}

# 检查是否登录 Docker Hub
check_login() {
    info "检查 Docker Hub 登录状态..."
    if ! docker info 2>/dev/null | grep -q "Username"; then
        warn "未检测到 Docker Hub 登录状态"
        info "请先执行: docker login --username $DOCKER_USERNAME"
        exit 1
    fi
    info "Docker Hub 已登录 ✓"
}

# 构建镜像
build_image() {
    info "开始构建镜像..."
    cd ..
    docker build -f docker/Dockerfile -t $IMAGE_NAME:$TAG .
    if [ $? -ne 0 ]; then
        error "镜像构建失败"
        exit 1
    fi
    info "镜像构建成功 ✓"
    cd docker
}

# 给镜像打标签
tag_image() {
    info "给镜像打标签..."
    docker tag $IMAGE_NAME:$TAG $DOCKER_USERNAME/$IMAGE_NAME:$TAG
    if [ $? -ne 0 ]; then
        error "镜像标签失败"
        exit 1
    fi
    info "镜像标签: $DOCKER_USERNAME/$IMAGE_NAME:$TAG ✓"
}

# 推送镜像到 Docker Hub
push_image() {
    info "开始推送镜像到 Docker Hub..."
    info "目标地址: $DOCKER_USERNAME/$IMAGE_NAME:$TAG"
    docker push $DOCKER_USERNAME/$IMAGE_NAME:$TAG
    if [ $? -ne 0 ]; then
        error "镜像推送失败"
        exit 1
    fi
    info "镜像推送成功 ✓"
}

# 显示使用帮助
show_usage() {
    echo "用法: $0 [选项]"
    echo ""
    echo "选项:"
    echo "  build       仅构建镜像"
    echo "  push        构建并推送镜像到 Docker Hub"
    echo "  tag         仅给镜像打标签"
    echo "  help        显示帮助信息"
    echo ""
    echo "示例:"
    echo "  $0 build    # 只构建镜像"
    echo "  $0 push     # 构建并推送"
    echo ""
    echo "Docker Hub 仓库: https://hub.docker.com/r/$DOCKER_USERNAME/$IMAGE_NAME"
}

# 主函数
main() {
    case "${1:-push}" in
        build)
            check_docker
            build_image
            info "本地镜像: $IMAGE_NAME:$TAG"
            ;;
        push)
            check_docker
            check_login
            build_image
            tag_image
            push_image
            info ""
            info "========================================"
            info "镜像推送成功!"
            info "========================================"
            info "仓库地址: https://hub.docker.com/r/$DOCKER_USERNAME/$IMAGE_NAME"
            info "拉取命令: docker pull $DOCKER_USERNAME/$IMAGE_NAME:$TAG"
            info ""
            info "注意: 请确保仓库设置为 Public，否则他人无法拉取"
            ;;
        tag)
            check_docker
            tag_image
            info "本地镜像已打标签: $DOCKER_USERNAME/$IMAGE_NAME:$TAG"
            ;;
        help)
            show_usage
            ;;
        *)
            error "未知选项: $1"
            show_usage
            exit 1
            ;;
    esac
}

# 执行主函数
main "$@"
