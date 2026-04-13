#!/bin/bash

set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "$0")" && pwd)"
IMAGE_NAME="haidong-tuanwei-app:latest"

GREEN='\033[0;32m'
YELLOW='\033[1;33m'
RED='\033[0;31m'
NC='\033[0m'

info() {
    echo -e "${GREEN}[INFO]${NC} $1"
}

warn() {
    echo -e "${YELLOW}[WARN]${NC} $1"
}

error() {
    echo -e "${RED}[ERROR]${NC} $1"
}

check_docker() {
    if ! command -v docker >/dev/null 2>&1; then
        error "Docker 未安装，请先安装 Docker"
        exit 1
    fi

    if ! docker info >/dev/null 2>&1; then
        error "Docker 未启动，请先启动 Docker Desktop 或 Docker Engine"
        exit 1
    fi
}

prepare_env() {
    cd "$SCRIPT_DIR"

    if [ ! -f ".env" ]; then
        cp .env.example .env
        warn "未检测到 .env，已根据 .env.example 自动生成"
        warn "如需修改端口、数据库名或密码，请编辑 docker/.env 后重新执行脚本"
    fi
}

check_artifact() {
    cd "$SCRIPT_DIR"

    if [ ! -f "haidong-tuanwei-0.0.1-SNAPSHOT.jar" ]; then
        error "缺少 haidong-tuanwei-0.0.1-SNAPSHOT.jar，请先将可执行 JAR 放入 docker 目录"
        exit 1
    fi
}

build_and_deploy() {
    cd "$SCRIPT_DIR"
    info "开始构建镜像并启动 Docker Compose..."
    docker compose up -d --build
}

show_summary() {
    cd "$SCRIPT_DIR"

    local app_port
    local mysql_port
    app_port="$(grep '^APP_PORT=' .env 2>/dev/null | cut -d'=' -f2- || true)"
    mysql_port="$(grep '^MYSQL_EXPOSE_PORT=' .env 2>/dev/null | cut -d'=' -f2- || true)"

    app_port="${app_port:-8080}"
    mysql_port="${mysql_port:-13306}"

    info "部署完成"
    info "本地镜像: ${IMAGE_NAME}"
    info "登录页: http://localhost:${app_port}/login"
    info "管理后台: http://localhost:${app_port}/dashboard"
    info "MySQL 端口: ${mysql_port}"
    warn "数据库表结构和初始化数据不会自动导入，请在容器启动后手工执行 SQL 脚本"
}

main() {
    check_docker
    prepare_env
    check_artifact
    build_and_deploy
    show_summary
}

main "$@"
