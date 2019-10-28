Feature: Use a disabled datastream
    I want to try to use a disabled datastream.

    @all @operations @disabled @simulator
    Scenario Outline: I want to try to use a disabled datastream
        Given an id of device from are reading data: "<deviceId>"
        And an id of datastream from are reading data: "<datastreamId>"
        And A started EDP simulator to use a disabled datastream
        When I request to read to ODA
        Then ODA should send error
        Then disconnect ECP simulator to use a disabled datastream
        Examples:
            | deviceId  | datastreamId |
            | dev       | fakeId       |