Feature: Write operation running a EDP simulator
    To handle write operations we will do the next:
    A fake client of EDP must have subscribed to oda/operation/write/request/# *1  ;
    An assistant MQTT client, doing the role of OpenGate, must be subscribed to odm/responses/# *2  ;
    Enable device datastream that we will read during the test;
    Send a message requesting a GET_PARAMETER with the "OpenGate client";
    ODA will process the OG message and it will send a message to EDP. "EDP client" will take the id and send a value;
    ODA will receive the value and send it to OG. Now we have to compare the value received by OG and
    the value sent by EDP. If they are the same, the test will be correct;

    Note 1: # represents each datastream of each device registered.
    Note 2: # represents all the devices.

    @3.17.0 @set @simulator
    Scenario Outline: I want to set a new value to a value of a datastream
        Given new value for the datastream: "<value>"
        Given id of target device to write: "<deviceId>"
        Given id of target datastream to write: "<datastreamId>"
        Given A started EDP simulator to set
        When I send a request to ODA to set the data
        Then data send to ODA is the same that received by EDP
        And I receive a response
        Examples:
            | deviceId  | datastreamId | value |
            | dev       | datastreamId | 42    |