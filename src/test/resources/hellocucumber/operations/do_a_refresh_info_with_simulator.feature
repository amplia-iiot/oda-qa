Feature: Refresh info operation running a EDP simulator
    To handle refresh operations we wil do the next:
    A fake client of EDP must have subscribed to oda/operation/read/request/# *1  ;
    An assistant MQTT client, doing the role of OpenGate, must be subscribed to odm/responses/# *2  ;
    Enable device datastream that we will refresh during the test;
    Send a message requesting a REFRESH_INFO with the "OpenGate client";
    ODA will process the OG message and it will send a message to EDP. "EDP client" will take the id and send a value;
    ODA will receive the value and send it to OG. Now we have to compare the value received by OG and
    the value sent by EDP. If they are the same, the test will be correct;

    Note 1: # represents each datastream of each device registered.
    Note 2: # represents all the devices.

    @3.17 @refresh @simulator @operation
    Scenario Outline: I want to do a refresh info of a device
        Given id of target device to refresh: "<deviceId>"
        Given id of target datastream to refresh: "<datastreamId>"
        Given data that we will use to test: "<testData>"
        Given A started EDP simulator to refresh
        When I send a request to ODA to refresh the data
        Then I receive a response of all datastreams
        And Data send to ODA is the same that received by EDP
        Examples:
            | deviceId | datastreamId | testData |
            |  dev     | dataId       | data     |