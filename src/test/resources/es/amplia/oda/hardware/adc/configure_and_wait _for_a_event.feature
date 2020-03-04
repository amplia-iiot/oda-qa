Feature: Configure and prepare an event to be notified by ODA

    To ensure that configuration is changing inside ODA and is able handle events, we will do this:
    Register an client to connect to the device.
    Put a file that is a representation of the input data of an ADC.
    Change the configuration of ODA to register the datastreamEvent.
    Wait a while.
    Change the data of the input representation.
    Check if a event is received from the ODA.

    If Client don't receive anything, test will be fail.
    If Client receive a invalid data or a wrong formatted event, test will be fail.

    @all @hardware @datastreams @diozero @adc @configuration @event
    Scenario: I want to receive an event from ODA
        Given a client connected with the ODA to receive its events
        And a file with the value of ADC
        When I change the configuration of remote datastreams of ODA to to register a datastreamEvent
        And after wait a while, I change the value of the input dramatically
        Then ODA send to me a event that contains new data of input
