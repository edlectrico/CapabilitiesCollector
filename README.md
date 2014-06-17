Pellet4Android
==============

- Description:
-------------
This project is a Pellet port for running in Android devices. As the port cannot be directly made due to the differences between Pellet Java classes and Android's Dalvik platform, several classes have been rewritten and adapted. This is because of the dependences from, for example, Javax and Xerces packages, which cannot be compiled in Dalvik.

The project has been compiled for Android 4.3, although I am pretty sure it will be easily compiled with any other Android version, since the main problem here was to compile Pellet classes and dependencies in Android.

- Instructions:
---------------
The MainActivity contains some examples of how to use this project. 

Every task is performed through a class called OntologyManager, which manages, configures and runs every operation that deals with the ontology. You can load the ontology (from file or from the Web), insert and delete classes, individuals, datatype and object properties, execute rules, etc. Everything you can do in Pellet is available in Pellet4Android (at least this is my intention :p).


