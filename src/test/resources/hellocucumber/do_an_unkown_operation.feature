Feature: Do a unknown operation
    I wanna do a unknown operation

    @3.17.0 @unknown @edp
    Scenario: I wanna do a unknown operation
        Given a mqtt message with a unkown operation
        When I send the operation to dispatcher
        Then I receive a error