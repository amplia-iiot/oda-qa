Feature: Sync operation

    Note: It's impossible (or inefficient) make this test checking the exactly hour that you have.
        In exchange, we will do a check with 3 seg of

    @Test @snapshot
    Scenario: From OG I want to synchronize all devices hour to make sure that they have the correct hour
        Given the deviceId selected to synchronize the hour: syncDevice
        Given the datastreamId selected to synchronize the hour: syncDatastream
        When I send a request to ODA to sync the hour
        Then I receive a response with approximately the actual hour