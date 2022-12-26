#!/usr/bin/env bash
set -e

export POSTGRES_USER=postgres;

psql -h localhost -p 5432 -v ON_ERROR_STOP=1 --username "$POSTGRES_USER" <<-EOSQL
    CREATE DATABASE "klog";
    CREATE ROLE "klog" WITH SUPERUSER;
    GRANT ALL PRIVILEGES ON DATABASE "klog" TO $POSTGRES_USER;
    GRANT ALL PRIVILEGES ON DATABASE "klog" TO "klog";
    ALTER DATABASE "klog" SET search_path TO public,"klog";

    \c "klog";
    CREATE SCHEMA "klog";
    GRANT ALL ON SCHEMA "klog" TO $POSTGRES_USER;
    GRANT ALL ON SCHEMA "klog" TO "klog";
    ALTER DATABASE "klog" SET search_path TO public,"klog";
EOSQL
