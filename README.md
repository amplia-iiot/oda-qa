#   oda-qa
Tests to use ODA with EDP.

### QA operations

If you want to run test mocking EDP use the command:
    mvn verify -Dcucumber.options="--tags @simulator"

If you want to run test using your own EDP, use the command:
    mvn verify -Dcucumber.options="--tags @edp"
    
If you want to run all operation tests, use command:
    mvn verify -Dcucumber.options="--tags @operation"
    (This option can have issues because incompatibilities between simulator and edp)

If you want to run test of one function use:
    mvn verify -Dcucumber.options="--tags @<name_of_function>"

    <name_of_function>:
        discover,
        get,
        refresh,
        set,
        sync,
        update,
        unknown,
        event,
        disabled

This QA implements a translator of CBOR to see the topic (inside oda/#) and its message. To use it, enter the command:
    mvn test -Dcucumber.options="--tags @Translator"

### QA Connectors

Under construction.
We expect to add IEC104 here, be patient.

## WARNING

Translator have a while(true) to stay translating until you stop it.
            If you run all tests, the translator will run.