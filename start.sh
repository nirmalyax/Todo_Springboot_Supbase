#!/bin/bash
# ==============================================================================
# Todo Application Startup Script
# ==============================================================================
# This script helps with starting the Todo application in different modes:
# - local: Run with Maven (for development)
# - docker: Run with Docker Compose
# ==============================================================================

set -e  # Exit immediately if a command exits with a non-zero status

# Default values
MODE="local"
ENV_FILE=".env"
APP_PORT=8080
DB_PORT=5432

# Text formatting
BOLD="\033[1m"
GREEN="\033[0;32m"
YELLOW="\033[0;33m"
BLUE="\033[0;34m"
RED="\033[0;31m"
NC="\033[0m" # No Color

# Show help message
show_help() {
    echo -e "${BOLD}Todo Application Starter${NC}"
    echo "Usage: ./start.sh [OPTIONS]"
    echo ""
    echo "Options:"
    echo "  -m, --mode MODE       Startup mode: local, docker (default: local)"
    echo "  -e, --env-file FILE   Environment file (default: .env)"
    echo "  -p, --port PORT       Application port (default: 8080)"
    echo "  -d, --db-port PORT    Database port (default: 5432)"
    echo "  -h, --help            Show this help message"
    echo ""
    echo "Examples:"
    echo "  ./start.sh --mode local    # Start locally with Maven"
    echo "  ./start.sh --mode docker   # Start with Docker Compose"
}

# Parse arguments
while [[ "$#" -gt 0 ]]; do
    case $1 in
        -m|--mode) MODE="$2"; shift ;;
        -e|--env-file) ENV_FILE="$2"; shift ;;
        -p|--port) APP_PORT="$2"; shift ;;
        -d|--db-port) DB_PORT="$2"; shift ;;
        -h|--help) show_help; exit 0 ;;
        *) echo "Unknown parameter: $1"; show_help; exit 1 ;;
    esac
    shift
done

# Banner
echo -e "${BOLD}${BLUE}"
echo "  _______          _         _____             "
echo " |__   __|        | |       / ____|            "
echo "    | | ___   ___ | | ___  | (___  _   _ _ __  "
echo "    | |/ _ \ / _ \| |/ _ \  \___ \| | | | '_ \ "
echo "    | | (_) | (_) | | (_) | ____) | |_| | |_) |"
echo "    |_|\___/ \___/|_|\___/ |_____/ \__,_| .__/ "
echo "                                        | |    "
echo "                                        |_|    "
echo -e "${NC}"

# Load environment variables if file exists
if [ -f "$ENV_FILE" ]; then
    echo -e "${GREEN}Loading environment from $ENV_FILE...${NC}"
    export $(grep -v '^#' $ENV_FILE | xargs)
else
    echo -e "${YELLOW}Warning: $ENV_FILE not found, using default or system environment variables.${NC}"
    # Create basic .env file
    echo "# Generated .env file - customize as needed" > $ENV_FILE
    echo "APP_PORT=$APP_PORT" >> $ENV_FILE
    echo "DB_PORT=$DB_PORT" >> $ENV_FILE
    echo "POSTGRES_USER=postgres" >> $ENV_FILE
    echo "POSTGRES_PASSWORD=postgres" >> $ENV_FILE
    echo "POSTGRES_DB=todo_db" >> $ENV_FILE
    echo -e "${GREEN}Created basic $ENV_FILE file.${NC}"
fi

# Make script executable
if [ ! -x "$0" ]; then
    chmod +x "$0"
    echo -e "${GREEN}Made script executable.${NC}"
fi

# Start the application based on mode
case $MODE in
    local)
        echo -e "${GREEN}Starting application in local mode with Maven...${NC}"
        echo -e "${YELLOW}Checking dependencies...${NC}"
        
        # Check if Maven is installed
        if ! command -v mvn &> /dev/null; then
            echo -e "${RED}Maven is not installed. Please install Maven first.${NC}"
            exit 1
        fi
        
        # Check if Java is installed
        if ! command -v java &> /dev/null; then
            echo -e "${RED}Java is not installed. Please install Java first.${NC}"
            exit 1
        fi
        
        # Set environment variables for local run
        export SERVER_PORT=$APP_PORT
        
        echo -e "${BLUE}Starting application on port $APP_PORT...${NC}"
        mvn spring-boot:run
        ;;
        
    docker)
        echo -e "${GREEN}Starting application in Docker mode...${NC}"
        echo -e "${YELLOW}Checking dependencies...${NC}"
        
        # Check if Docker is installed
        if ! command -v docker &> /dev/null; then
            echo -e "${RED}Docker is not installed. Please install Docker first.${NC}"
            exit 1
        fi
        
        # Check if Docker Compose is installed
        if ! command -v docker-compose &> /dev/null; then
            echo -e "${RED}Docker Compose is not installed. Please install Docker Compose first.${NC}"
            exit 1
        fi
        
        # Export .env file variables to environment
        export APP_PORT=$APP_PORT
        export DB_PORT=$DB_PORT
        
        echo -e "${BLUE}Starting Docker containers...${NC}"
        docker-compose down || true  # Stop existing containers if any
        docker-compose up --build  # Start services with rebuilding
        ;;
        
    *)
        echo -e "${RED}Invalid mode: $MODE${NC}"
        show_help
        exit 1
        ;;
esac

