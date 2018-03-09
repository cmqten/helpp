## Documentation: the meeting minutes

Objectives: Assign user stories to either individuals or pairs of people depending on the level of difficulty of each job. 

Overview: This sprint we focused on inegration of all our different parts from sprint 1, along with changing our approach regarding data storage. We realized that it would be inefficient, to query the charitiy data from Firebase, so we decided to mirgrate the data into a MySQL database while still using Firebase to keep track of user profiles and user authentication. The goal of this sprint is to have the base functionality of the app completed this sprint.


## Participation:
Jonathan Wang
Kenny Chen
Carl Marquez
Edmond Umolu 
Shuprio Shourov
Gary Liton
Maxim Chipeev

## Sprint Backlog:
Assigned to:
Gary Liton
Story:
Be able to create/edit user profile data in the firebase json objects
Tasks:
-create a profile activity

Assigned to:
Gary Liton
Story:
Create user profile page
Tasks:
-watch a tutorial on facebook log in for android

Assigned to:
Edmond Umolu 
Story:
Integrate the social media, website, donation links, summary and logo scraping into 1 activity
Tasks:
-create description getter
-merge description getter and links getter
-merge everything to location services 

Assigned to:
Carl Marquez
Story:
Get all the longitude and latitude values of each charity's location
Tasks:
-Created a python script that would generate a json file containing the longitude and latitude of each charity

Assigned to:
Shuprio Shourov
Story:
Migrate charity data into sql database
Tasks:
-clean/convert the json data structure into table schema

Assigned to:
Jonathan Wang
Story:
Create UI/UX experience for user navigation between different pages of the app
Tasks: 
-Figure out how to user fragments in android 
-Create user swipe page functionality
-Create menu for charity type and category selection

Assigned to:
Jonathan Wang
Story:
Integrate google admob as a feature in the app
Tasks:
-figure out how to use admod
-create the watch ads activity
-create the different fragments for the different types of ads people would watch

Assigned to:
Shuprio Shourov
Story:
Visualize organization activities data 
Tasks:
-create fragmenet to contain organization activities
-create async task to scrap for the data

Assigned to:
Gary Liton
Story:
Login/Signup (Gmail, Facebook, or own authentication service) 
Tasks:
-create an activity for the sign up ad log in page
-connect app to firebase

Assigned to:
Carl Marquez, Maxim Chipeev
Story:
Use the search feature from the Canada revenue website as our search feature in the app
Tasks:
-Created a python script that would generate a json file containing the longitude and latitude of each charity

Assigned to:
Edmond Umolu, Kenny Chen
Story:
Create visuals for the statistics about the organization finances
Tasks:
-Create Revenue Pie chart
-Create Expenses Pie Chart
-Create Activities
