Feature: Create a new rule
    To handle the operation of create a new rule for the rule engine, we have to follow the next steps:
    Register a fake OpenGate MQTT client.
    Send from 'OG client' a message to request the creation of a Rule.
    ODA receive and process the request, checking if the new rule exists and creating it if it's not.
    'OG client' receive a message with a OK code.
    Test went well if there is a file in the datastream directory with the namerule as name of file and OG
        and OG receive a OK response.

    If rule is already registered, test will be fail.
    If OG receive anything else a OK message, test will be fail.

    @all @operations @createrule
    Scenario Template: I want to create a new rule
        Given the device id of the rule we want to create: "<deviceId>"
        Given the datastream id of the rule we want to create: "<datastreamId>"
        Given the name of the rule we want to create: "<namerule>"
        Given a when instruction of the rule we want to create: "<when>"
        Given a then instruction of the rule we want to create: "<then>"
        When I send a request to ODA with the rule
        Then I receive an OK message
        Examples:
            | deviceId  | datastreamId  | namerule  | when          | then          |
            | testDevice| testDatastream| rule      | return true;  | return state; |