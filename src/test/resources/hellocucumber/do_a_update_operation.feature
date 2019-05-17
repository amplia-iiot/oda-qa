Feature: Update operation
    I have no idea at the moment

    @Single
    Scenario: I want to change one bundle configuration to a new file
        When I send a request to ODA to change the configuration
        Then the new configuration is the same that the file