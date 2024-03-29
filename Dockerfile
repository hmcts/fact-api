ARG APP_INSIGHTS_AGENT_VERSION=3.2.10
FROM hmctspublic.azurecr.io/base/java:17-distroless

COPY lib/AI-Agent.xml /opt/app/
COPY build/libs/fact-api.jar /opt/app/

EXPOSE 8080
CMD [ "fact-api.jar" ]
