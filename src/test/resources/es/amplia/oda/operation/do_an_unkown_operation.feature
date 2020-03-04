Feature: Do a unknown operation
    I wanna do a unknown operation

    @all @operations @unknown @edp
    Scenario: I wanna do a unknown operation
        Given a mqtt message with a unkown operation
        When I send the operation to dispatcher
        Then I receive a error