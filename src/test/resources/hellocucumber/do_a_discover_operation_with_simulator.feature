Feature: Discover operation running a EDP simulator
    To handle the discover operation we will do next steps:
    Register a fake OpenGate MQTT client.
    Register a fake EDP MQTT client.
    Send from 'OG client' a message to request a Discover Operation.
    ODA send the request (a void message to the topic oda/request/discover).
    'EDP client' receive that message and send enable messages to enable all datastreams that it have.
    ODA enable all datastreams send by 'EDP client'.
    'OG client' receive a message with successful code.
    Test went well if there are so many datastreams activated (we use DiscoverManager to see that) than we expect and
        OG receive a SUCCESSFUL response.

    If DiscoverManager have different quantity of datastreams that expected, test will be fail.
    If OG receive anything else a successful message, test will be fail.

    @3.18.0 @discover @simulator
    Scenario: I want to discover datastreams allowed using my own EDP
        Given A started EDP simulator to discover
        When I send a request to discover to ODA
        Then I have the expected datastreams
        And I receive a successful message of discover operation