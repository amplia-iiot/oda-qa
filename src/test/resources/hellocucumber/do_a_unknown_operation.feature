Feature: Do a unknown operation
  I wanna do a unknown operation

  Scenario: I wanna do a unknown operation
      Given a mqtt message
      When I send to dispatcher
      Then I receive a error