Feature: Configure and get data of datastreams

    To ensure that configuration is changing inside ODA and is able to do a get of a ADC input, we will do this:
    Register an client to connect to the device.
    Put a file that is a representation of the input data of a ADC.
    Change the configuration of ODA
    Do a get operation to receive the expected data for that datastream.
    Check that the received information is the same as the one configured.

    REQUIRES: Get operation and MQTT local protocol must be running and be working correctly.

    If Client don't receive anything, test will be fail.
    If Client receive a invalid data or a message saying that the datastream doesn't exist, test will be fail.

    @all @hardware @datastreams @diozero @adc @configuration @get
    Scenario: I want to set a configuration with at least one datastream and device
        Given a client connected with the ODA to receive its responses
        When I change the configuration of remote datastreams of ODA to local configuration
        And I send a discover operation
        Then ODA send to me a discovery that contains configured datastreams
