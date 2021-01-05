# Developer test assignment for FleetComplete
01/2021
By Sergei Sokolov

## Build instructions
Set Google Maps API key in corresponding resource file
(```app/src/debug/res/values/google_maps_api.xml``` for debug build)

## Technical overview
  * Min sdk level 23 (Android 6.0)
  * Uses Android Architecture Components (ViewModel, LiveData)
  * Uses DataBinding - goal is to have UI layouts loosely coupled to code
  * Uses string resourses wherever possible
   
### Implementation specifics 
  * API key is saved to preferences, so it'll survive app restart

### Limitations
  * at the moment, distance is calculated only from "Distance" field, as a difference between last and first value (so if this data is missing, but "delta" data is present, trip distance will still be shown as 0)
  * No "smart" auto-zoom  
