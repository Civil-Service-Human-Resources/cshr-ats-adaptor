#! /bin/bash
##
# Due to some restrictions with the Azure platform we need to
# run filebeat alongside the application in the same container.
# In order to achieve this we need an entrypoint file

echo "cshr-ats-adaptor: In entrypoint.sh"

echo "entrypoint.sh: command passed: " ${1}

if [[ ${#} -eq 0 ]]; then
    # -E to preserve the environment
    echo "Starting filebeat"
    sudo -E filebeat -e -c /etc/filebeat/filebeat.yml &
    echo "Starting application"
    java -Djava.security.egd=file:/dev/./urandom \
      -Dats.authentication.token=${ATS_AUTHENTICATION_TOKEN} \
      -Dats.client.id=${ATS_CLIENT_ID} \
      -Dats.request.endpoint=${ATS_REQUEST_ENDPOINT} \
      -Dcshr.api.service.vacancy.save.endpoint=${CSHR_API_SERVICE_VACANCY_SAVE_ENDPOINT} \
      -Dcshr.api.service.department.findAll.endpoint=${CSHR_API_SERVICE_DEPARTMENT_FINDALL_ENDPOINT} \
      -Dcshr.api.service.search.username=${CSHR_API_SERVICE_SEARCH_USERNAME} \
      -Dcshr.api.service.search.password=${CSHR_API_SERVICE_SEARCH_PASSWORD} \
      -Dcshr.api.service.vacancy.save.username=${CSHR_API_SERVICE_VACANCY_SAVE_USERNAME} \
      -Dcshr.api.service.vacancy.save.password=${CSHR_API_SERVICE_VACANCY_SAVE_PASSWORD} \
      -Dats.jobrun.history.directory=${ATS_JOBRUN_HISTORY_FILE} \
      -Dats.jobrun.history.file=${ATS_JOBRUN_HISTORY_FILE} \
      -Dcshr.ats.vendor.id=${CSHR_ATS_VENDOR_ID} \
      -Dats.request.batch.size=${ATS_REQUEST_BATCH_SIZE} \
      -Dcshr.jobrun.audit.directory=${CSHR_JOBRUN_AUDIT_DIRECTORY} \
      -Dcshr.jobrun.audit.basefilename=${CSHR_JOBRUN_AUDIT_BASEFILENAME} \
      -Dserver.port=${SERVER_PORT} \
      -Dslack.notification.channel=${SLACK_NOTIFICATION_CHANNEL} \
      -Dslack.notification.endpoint=${SLACK_NOTIFICATION_ENDPOINT} \
      -Dcshr.jobrun.cron.schedule=${CSHR_JOBRUN_CRON_SCHEDULE} \
      -jar /app/ats-adaptor.jar
else
    echo "Running command:"
    exec "$@"
fi
