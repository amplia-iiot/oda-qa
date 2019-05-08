Feature: Do a set operation
    I want to do a set operation. ODA will send a message to oda/operation/write/request/+.
    To response to this request, EDP (this test) will have to be subscribed to the topic.
    The way to test this, will send a request of set to ODA and check that data send to EDP is the expected,
    after that, we will send the response and check that the response from ODA to the request is correct.

    Scenario: I want to set a new value to a value of a datastream
        Given new value for the datastream: 22
            And id of target device to write: otherDevice
            And id of target datastream to write: testDatastream
        When I send a request to ODA to set the data
        Then I receive a response and data send to ODA is the same that received by EDP
#
#    Scenario Outline: I want to set a new value to a value of a selected datastream
#        Given new value for the datastream: "<value>"
#            And id of target device to write: "<deviceId>"
#            And id of target datastream to write: "<datastreamId>"
#        When I send a request to ODA with required data
#        Then I receive the same data that EPC Simulator send to ODA
#        Examples:
#            | value | deviceId | datastreamId |
#            # Should work well
#            | 4     |testDevice|testDatastream|
#            # Should not work at all, bc id's doesn't exist
#            | error | invalidId| notADsId     |