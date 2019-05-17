Feature: Use a disabled datastream
    I want to try to use a disabled datastream.

    @Single @Testing
    Scenario: I want to try to use a disabled datastream
        Given an id of device from are reading data: otherDevice
        And an id of datastream from are reading data: datastreamId
        When I request to read to ODA
        Then ODA shouldn't send anything