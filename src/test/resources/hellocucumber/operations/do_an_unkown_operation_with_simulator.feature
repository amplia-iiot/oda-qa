Feature: Do a unknown operation
    I wanna do a unknown operation

    @3.17 @unknown @simulator @operation
    Scenario: I wanna do a unknown operation
        Given a mqtt message with a unkown operation
        Given A started EDP simulator to do an unknown operation
        When I send the operation to dispatcher
        Then I receive a error
        Then I disconnect the simulator