Feature: Send event operation
    I want to send to ODA an event (as EDP) with a data without it have to request me.

    @Single
    Scenario: I want to send a simple data to ODA without wait that it request me
        Given a value to send: today it is raining
        And an id of device what are sending data: otherDevice
        And an id of datastream from are sending data: datastreamId
        When I send a event to ODA
        Then ODA receive data and send it

    @Multidatastream
    Scenario: I want to send various values to ODA
        Given various values to send: raining, sunny and cloudy
        And an id of device what are sending data: otherDevice
        And respective id of datastreams from are sending data: weatherNorth, weatherSouth, weatherWest
        When I send various events to ODA
        Then ODA receive data and send it