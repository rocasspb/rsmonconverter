# RSMonConverter
Converter for RS Monitor data logs for RenaultSport cars.

## Description
First take on this task: the code can extract most of the values from the .run file and export these values as [RaceRender CSV](http://racerender.com/Developer/DataFormat.html)
For now, this allows to overlay the RS Monitor data over an onboard video. [Click here to see a sample](https://youtu.be/NIescnEe7I0)
Potentially, this will allow to export the data to .VBO and load into the Circuit Tools software for analysis.

## Supported data points
- Speed(km/h)
- Throttle
- Brake
- Steering angle
- RPM
- Gear
- Boost
- Torque
- Power
- Individual wheel speeds
- GPS Lat/Lon
- Intake temp
- Oil Temp
- Coolant Temp
- Gearbox Temp
- Clutch Temp
- External air temp

## Further plans
- Data points
  - export temperatures
  - figure out accelerometer format
  - find other data points:
    - Yaw rate
- Export
 - Add all known values to export
 - Support VBO format 
- Misc
 - Refactor export for better ordering coordination
 - Triple check on other RS cars
 - Write proper tests

## Basic usage
main accepts either one or two parameters: 
- Single parameter is considered as a source path
    - if it is a file, the output file will have the same name and .csv extension
    - if it is a folder, all the files with .run extension will be converted
- Two parameters: the filename of the original RS Monitor data log and the name of the output csv file


## Kudos
- to @ravenstarter for helping me discovering some of the tricky values
- to Mr. Sinelnikoff for creating this awesome steering wheel overlay art :)

## Disclaimers
- this has only been tested with Clio RS Trophy - please send me the logs of other cars equipped with RS Monitor to check if the code works well
- yeah, I know, the code is hacky and dirty - I will be improving it over time, but now it is in the state of a research, so this is not something I care too much
