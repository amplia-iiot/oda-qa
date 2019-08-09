Feature: Use a disabled datastream
    I want to try to use a disabled datastream.

    @3.17 @disabled @edp @operation
    Scenario Outline: I want to try to use a disabled datastream
        Given an id of device from are reading data: "<deviceId>"
        And an id of datastream from are reading data: "<datastreamId>"
        When I request to read to ODA
        Then ODA should send error
        Examples:
            | deviceId  | datastreamId |
            | edp       | fakeId       |