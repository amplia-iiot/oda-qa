Feature: Do a get operation
    I wanna do a get to a datastream

    Scenario: I wanna get a data from a device
        Given a device where I want to get a data
        When I send a request to get the data
        Then I receive a data