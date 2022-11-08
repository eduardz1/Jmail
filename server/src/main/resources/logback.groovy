def LOG_PATH = "logs"
def LOG_ARCHIVE = "${LOG_PATH}/archive"
def LOG_FILE = "${LOG_PATH}/server.log"
def LOG_FILE_PATTERN = "${LOG_ARCHIVE}/server-%d{yyyy-MM-dd}.%i.log.gz"

// TODO: non funziona, il codice qui è quasi tutto opera di copilot perché non so come farlo funzionare
// file appender
appender("FILE", RollingFileAppender) {
    file = LOG_FILE
    rollingPolicy(SizeAndTimeBasedRollingPolicy) {
        fileNamePattern = LOG_FILE_PATTERN
        maxFileSize = "10MB"
    }
    encoder(PatternLayoutEncoder) {
        pattern = "%d{yyyy-MM-dd HH:mm:ss.SSS} [%thread] %-5level %logger{36} - %msg%n"
    }
}
