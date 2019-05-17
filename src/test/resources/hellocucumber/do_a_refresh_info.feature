Feature: Do a refresh info operation
    I want to gather all info of a device.

    @Single
    Scenario: I want to do a refresh info of a device
        Given id of target device to refresh: anotherDevice
        When I send a request to ODA to refresh the data
        Then I receive a response of all datastreams and data send to ODA is the same that received by EDP