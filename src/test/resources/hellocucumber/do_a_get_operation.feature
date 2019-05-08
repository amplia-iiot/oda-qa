Feature: Do a get operation
    I want to do a get operation to a ODA.

    Scenario Outline: I want to get a data from a selected device
        Given id of target device: "<deviceId>"
            And id of target datastream: "<datastreamId>"
        When I send a request to ODA with required data
        Then I receive the same data that EPC Simulator send to ODA
        Examples:
            | deviceId | datastreamId |
            # Should work well
            |otroDevice| getDatastream|
            | otherId  | randomADsId  |