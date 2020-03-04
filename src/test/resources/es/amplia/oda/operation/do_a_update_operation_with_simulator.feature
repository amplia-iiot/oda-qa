Feature: Update operation
    To handle read operations we will do the next:
    An assistant MQTT client, doing the role of OpenGate, must be subscribed to odm/responses/# *1  ;
    A session of SHH that can access to
    Enable device datastream that we will read during the test;
    Send a message requesting a GET_PARAMETER with the "OpenGate client";
    ODA will process the OG message and it will send a message to EDP. "EDP client" will take the id and send a value;
    ODA will receive the value and send it to OG. Now we have to compare the value received by OG and
    the value sent by EDP. If they are the same, the test will be correct;

    Note 1: # represents all the devices.

    @all @operations @update @update-configuration @simulator
    Scenario: I want to change one bundle configuration to a new file
        When I send a request to ODA to change the configuration
        Then the new configuration is the same that the file

    @all @operations @update @update-rule @simulator @version-4-or-greater
    Scenario Outline: I want to create a new rule for the rule engine
        Given a "<name>" for the rule we want to create
        Given the "<id>" of the datastream we want to create
        When I send a request to ODA to create the rule
        Then the new rule is the same that the file
        Examples:
            |  name     | id  |
            |coldLimit  |temp |
            |  danger   |alarm|