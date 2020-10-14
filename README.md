# molr-gui-fx

# General documentation 

TODO!

# MolrFxSupport
This featureset allows to use molr-gui fx components from other applications in order to control molr missions. The central entry point would be the class MolrFxSupport.

This class can be accessed in 2 ways:
* By importing the MolrFxSupportConfiguration into the spring configuration of the UI, which then will pick up the mole by itself (this is the recommended approach)
* For GUIs which are not primarily spring based, there is a factory (MolrFxSupportFactory) which can load the application context for you and pick up the m ole from the additional configuration classes provided as parameters.

Having an instance, it can be used e.g. like:
```
Mission m;
support.debug(m).inNewStage();
```
... this would open a new window, with the given mission.

This method chain returns also an `Optional<SimpleMissionControl>`, which is intended to allow convenient programmatically control over the mission. At the moment, the only supported command is `resume`:
```
Mission m;
support.debug(m).inNewStage().ifPresent(SimpleMissionControl::resume);
```
this would open a window and immediately run the mission.

NOTE: All this is a first proof of concept. The idea is to put more and more functionality into this (e.g. options like cleanup on close or similar), or running multiple missions....

# Mission stubs

A little additional sugar are the reactivation of mission stubs: This allows to e.g. define mission stubs as constants and then later use these stubs to run the missions. This allows typed checked parameters.

E.g. a stub with three parameters could be defined as:
```
private static VoidStub3<String, Integer, Integer> PARAMETRIZED_MISSION = stub("parametrized mission") //
            .withParameters(aString("aMessage"), anInteger("iterations"), anInteger("sleepMillis"));
```

It can then be used with the gui support class as:
```
support.debug(PARAMETRIZED_MISSION, "Hello World", 3, 200).inNewStage();
```

A demo of all this functionality can be found in the following classes:
* MolrFxSupportDemoApplication
* MolrFxSupportMinimalSpringDemo [note: for some reason I did not get this one to run without the minifx launcher ... despite it *should*


# Instructions
Make sure to have the molr projects linked in your IDE. Some features may not be released yet. (e.g. via `Composite Build Configuration` feature in IntelliJ)