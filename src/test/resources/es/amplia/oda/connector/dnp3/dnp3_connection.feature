Feature: Check that dnp3 can connect correctly to oda implementation.

    To handle the connection check, test will do the next steps:
        Register an DNP3 client (master role) to connect to the existent ODA' DNP3 server.
        Realise the connect enabling the client.
        If connecting is achieved, client will register a event in the SOEHandler.

    IMPORTANT: ODA DNP3 server must be connected with library database "DatabaseConfig.allValues(5)".

    If Client can't connect to anything, test will be fail.

    @all @conectors @dnp3 @connect
    Scenario: Try to connect oda-qa to the oda through dnp3.
        Given loaded dnp3 libraries
        Given data for the connection
        When start the connection with ODA
        Then connection is achieved