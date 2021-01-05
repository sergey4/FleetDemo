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
  * Uses string resources wherever possible
   
### Implementation specifics 
  * API key is saved to preferences, so it'll survive app restart

### Limitations
  * No "smart" auto-zoom for map  
