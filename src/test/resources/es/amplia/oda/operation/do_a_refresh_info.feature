Feature: Refresh info operation
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

    @all @operations @refreshinfo @edp
    Scenario Outline: I want to do a refresh info of a device
        Given id of target device to refresh: "<deviceId>"
        When I send a request to ODA to refresh the data
        Then I receive a response of all datastreams
        Examples:
            | deviceId |
            | Tm1234   |