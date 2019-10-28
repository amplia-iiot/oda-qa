Feature: Sync operation
    To do this test we will do these steps:
    Register a fake OpenGate MQTT client, subscribed to odm/response/#.
    Register a fake EDP MQTT client, subscribed to oda/operation/write/request/deviceId/datastreamId.
    Keep current hour to compare it to final result.
    Send a message from 'OG client' to ODA with SYNC_HOUR operation.
    ODA will

    @all @operations @synchronizeclock @edp
    Scenario Outline: From OG I want to synchronize all devices hour to make sure that they have the correct hour
        Given the deviceId selected to synchronize the hour: "<deviceId>"
        Given the datastreamId selected to synchronize the hour: "<datastreamId>"
        When I send a request to ODA to sync the hour
        Then I receive a response with approximately the actual hour
        Examples:
            | deviceId | datastreamId |
            | edp      | clock        |