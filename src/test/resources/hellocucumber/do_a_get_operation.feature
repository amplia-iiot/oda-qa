Feature: Read operation
    To handle read operations we will do the next:
    A fake client of EDP must have subscribed to oda/operation/read/request/# *1  ;
    An assistant MQTT client, doing the role of OpenGate, must be subscribed to odm/responses/# *2  ;
    Enable device datastream that we will read during the test;
    Send a message requesting a GET_PARAMETER with the "OpenGate client";
    ODA will process the OG message and it will send a message to EDP. "EDP client" will take the id and send a value;
    ODA will receive the value and send it to OG. Now we have to compare the value received by OG and
        the value sent by EDP. If they are the same, the test will be correct;

    Note 1: # represents each datastream of each device registered.
    Note 2: # represents all the devices.

    @3.17.0 @get @edp
    Scenario Outline: I want to get a data from a selected device
        Given id of target device: "<deviceId>"
            And id of target datastream: "<datastreamId>"
        When I send a request to ODA with required data
        Then I receive the same data that EPC Simulator send to ODA
        Examples:
            | deviceId  | datastreamId  |
            | edp       | q             |
            | edp       | s             |
            | edp       | smoke         |
            | edp       |transformerTemp|
            | edp       | voltage1      |
            | edp       | voltage2      |
            | edp       | voltage3      |