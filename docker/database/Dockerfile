FROM postgres:15.2-alpine

HEALTHCHECK --interval=10s --timeout=10s --retries=10 CMD psql -c 'select 1' -d fact -U fact

EXPOSE 5432
