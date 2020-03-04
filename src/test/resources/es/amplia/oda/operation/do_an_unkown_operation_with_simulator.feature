Feature: Do a unknown operation
    I wanna do a unknown operation

    @all @operations @unknown @simulator
    Scenario: I wanna do a unknown operation
        Given a mqtt message with a unkown operation
        Given A started EDP simulator to do an unknown operation
        When I send the operation to dispatcher
        Then I receive a error and disconnect the simulator