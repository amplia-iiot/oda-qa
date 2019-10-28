Feature: Send event operation
    I want to send to ODA an event (as EDP) with a data without it have to request me.

    @all @operations @event @simple @simulator
    Scenario Outline: I want to send a simple data to ODA without wait that it request me
        Given a value to send: "<value>"
        And an id of device what are sending data: "<deviceId>"
        And an id of datastream from are sending data: "<datastreamId>"
        When I send a event to ODA
        Then ODA receive data and send it
        Examples:
            | deviceId  | datastreamId | value |
            | edp       | q            | 42    |

    @all @operations @event @multiple @simulator
    Scenario Outline: I want to send various values to ODA
        Given various values to send: 1.0, 2.0, 3.0
        And an id of device what are sending data: "<deviceId>"
        And respective id of datastreams from are sending data: voltage1, voltage2, voltage3
        When I send various events to ODA
        Then ODA receive data and send it
        Examples:
            | deviceId |
            | edp      |