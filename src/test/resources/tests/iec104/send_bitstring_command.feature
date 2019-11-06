Feature: Send a bitstring command to oda

    To handle the interrogation operation we will do next steps:
        Register an IEC104 client and connect it to ODA server.
        Send from local Client a message to request a bitstring command (this will be a specific ASDU).
        ODA prepare the data from cache and send it with a sequential data.
        Local client will receive data and that data will have ASDU format.

    IMPORTANT: Local Client can't know what data is receiving ODA because it will have from datastreams. We can create a
        datastream for QA that send data specified by QA, but we won't know the quantity of times that the data has been
        requested during the QA tests and we won't know the specific value returned by ODA.

    If Client don't receive anything, test will be fail.
    If Client receive any data incompatible with ASDU types, test wil be fail.

    @all @connectors @iec104 @bitstring
    Scenario: I want to send to ODA a bitstring command
        Given An IEC client connected to ODA server channel to use a bitstring command
        When I send a bitstring command to ODA
        Then I receive a sequential data that match with ASDU form from bitstring command