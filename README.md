#   oda-qa

Tests to check the correct working of ODA.

## CONFIGURATION

There are two files of configuration in ODA-QA. Both are allocated on the resources of test file, in config package.

DiscoverData is the file that contains all information to configure test to works with the ODA running application.
JschData is the file that contains all information to connect to the device where ODA is running. Is needed configure it
even when we are running ODA in local.

## REQUIREMENTS

To run the ODA-QA, you have to run an ODA agent in a console of the device connected in the specified IP.

This ODA must have the bundles:

    - For test operations: JSON Parser, CBOR Parser, ODA Core, DeviceInfo Datastreams, MQTT Datastreams, MQTT Connector, 
        Events API, OG Dispatcher and the Operation Bundles that you will test. (If you will test get, Operations Get; 
        if you will test all, all bundles)
    - For test IEC104: IEC104 Connector, ODA Core

### QA Connectors

If you want to run all connectors tests, use the command:

    mvn verify -Dcucumber.options="--tags @connectors"
    
If you want to run only IEC-104 tests, use the command:

    mvn verify -Dcucumber.options="--tags @iec104"
    
If you want to run specific test of IEC-104, use:

    mvn verify -Dcucumber.options="--tags @iec104 --tags @<name_of_function>"

    <name_of_function>:
        bitstring,
        interrogate
 
If you want to run only DNP3 tests, use the command:

    mvn verify -Dcucumber.options="--tags @dnp3"
    
If you want to run specific test of DNP3, use:

    mvn verify -Dcucumber.options="--tags @dnp3 --tags @<name_of_function>"

    <name_of_function>:
        connect
        
### QA Hardware


        
### QA Operations

##### IMPORTANT: This tests work using MQTT connector. 
##### This is because the implementation of tests contains MQTT client to connect.
##### Use another connector would require new implementations of these tests.
    
If you want to run all operation tests, use command:

    mvn verify -Dcucumber.options="--tags @operations"
    (This option can have issues because incompatibilities between simulator and edp)

If you want to run test of one function use:

    mvn verify -Dcucumber.options="--tags @operations --tags @<name_of_function>"

    <name_of_function>:
        localprotocoldiscovery,
        get,
        refreshinfo,
        set,
        synchronizeclock,
        update,
        unknown,
        event,
        disabled

If you want to run test mocking the responses expected by ODA from South, use the command:

    mvn verify -Dcucumber.options="--tags @simulator"

If you want to run test using your own software that responses to ODA, use the command:

    mvn verify -Dcucumber.options="--tags @edp"

This QA implements a translator of CBOR to see the topic (inside oda/#) and its messages. To use it, enter the command:
    
    mvn test -Dcucumber.options="--tags @translator"
        
## DEBUG

If you want to run test in debug mode to see where is failing a code, use the option:

    -Dmaven.surefire.debug
    
To use this option, yo have to run any test with it, for example this will run edp tests in debug mode:

    mvn verify -Dcucumber.options="--tags @edp" -Dmaven.surefire.debug

## WARNING

utils.Translator have a while(true) to stay translating until you stop it.
            If you run all tests, the translator will run.