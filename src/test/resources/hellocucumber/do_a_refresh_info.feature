Feature: Do a refresh info operation
    I want to gather all info of a device.

    Scenario Outline: I want to do a refresh info of a device
        Given id of target device to refresh: "<deviceId>"
        Given id of target datastream to refresh: "<datastreamId>"
        Given data that we will use to test: "<testData>"
        When I send a request to ODA to refresh the data
        Then I receive a response of all datastreams and data send to ODA is the same that received by EDP
        Examples:
            | deviceId | datastreamId | testData |
            |  thermo  | temperature  |  23.5C   |
            |  clock   |     hour     |  23:50   |