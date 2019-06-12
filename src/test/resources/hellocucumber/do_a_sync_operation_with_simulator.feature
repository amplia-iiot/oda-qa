Feature: Sync operation running a EDP simulator
    To do this test we will do these steps:
    Register a fake OpenGate MQTT client, subscribed to odm/response/#.
    Register a fake EDP MQTT client, subscribed to oda/operation/write/request/deviceId/datastreamId.
    Keep current hour to compare it to final result.
    Send a message from 'OG client' to ODA with SYNC_HOUR operation.
    ODA will

    @3.18.0 @sync
    Scenario Outline: From OG I want to synchronize all devices hour to make sure that they have the correct hour
        Given the deviceId selected to synchronize the hour: "<deviceId>"
        Given the datastreamId selected to synchronize the hour: "<datastreamId>"
        Given A started EDP simulator to sync
        When I send a request to ODA to sync the hour
        Then I receive a response with approximately the actual hour
        Then disconnect ECP simulator to sync
        Examples:
            | deviceId  |   datastreamId    |
            | dev       | clock             |