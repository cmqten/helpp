## Documentation: The Meeting Minutes

### Objectives

Assign user stories to either individuals or pairs of people depending on the level of difficulty of each job. 

### Overview

In this sprint, we focused on finalizing the app by integrating the multiple pieces with each other into a single app. Also, we worked displaying the last bits of information that haven't been implemented prior to the sprint, such as compensation data, charity logos, charity websites, events, and user profiles. Finally, we worked on overhauling the UI, to make it much easier for the user to navigate The goal of this sprint is to have a single, fully-functioning Android application which implements all the features we listed out during sprint 0.

## Participation
- Jonathan Wang 
- Kenny Chen 
- Carl Marquez 
- Edmond Umolu 
- Shuprio Shourov
- Gary Liton 
- Maxim Chipeev

## Sprint Backlog

#### Scrape revenue, expenses, and compensation data from the CRA website
- Assigned to : Carl Marquez
- Tasks :
  - Added a function in the scraper that scrapes for compensation data, and adds this data in the json returned
#### Scrape logo and website link for each charity
- Assigned to : Edmond Umolu
- Tasks :
  - Use nodejs to cache server responses
  - adjust code on android
#### Integrate multiple servers that serve content into one server
- Assigned to : Edmond Umolu, Shuprio Shourov
- Tasks :
  - talk to rio about server access
  - put code on rio's server
#### Visualize revenue, expenses, compensation data, and activities for each charity
- Assigned to : Shuprio Shourov
- Tasks :
  - Combine financial data and charity information
#### Display upcoming events for a chosen charity
- Assigned to : Jonathan Wang, Kenny Chen
- Tasks :
  - Make a new events page
  - Integrate the event into the database
#### Add category picker button on search bar, options for selecting categories pop up when pressed
- Assigned to : Jonathan Wang
- Tasks :
#### Integrate google admob as a feature in the app 
- Assigned to : Jonathan Wang
- Tasks :
  - figure out how to use admob
  - create the watch ads activity
  - create the different fragments for the different types of ads people would watch
#### Add category picker button on search bar, options for selecting categories pop up when pressed
- Assigned to : Jonathan Wang
- Tasks :
  - Understanding fragments
  - Creating dynamic number of tabs
  - Query for different catagory based off current tab
#### Log in with Facebook or Google accounts
- Assigned to : Gary Liton
- Tasks :
  - created a button for google login and facebook login
#### Auto log in after logging in for the first time
- Assigned to : Gary Liton
- Tasks :
  - keep track of the last user logged in
#### Register with own email account and specifying first and last names
- Assigned to : Gary Liton
- Tasks :
  - authenticate a user given their email and password
#### "Skip log in screen" button
- Assigned to : Gary Liton
- Tasks :
  - created a button that skips the login process
#### Add a profile sandwich button
- Assigned to : Maxim Chipeev
- Tasks :
  - adjusted the layout within the ui
#### Move refresh and center-on-location button to bottom
- Assigned to : Maxim Chipeev
- Tasks :
  - moved the button to a different location in the ui
#### Add a charity/event toggle below search bar
- Assigned to : Maxim Chipeev
- Tasks :
  - added an charity/event button
#### Display secondary search bar when user intends to search, bottom search bar acts as the location-filter, top search bar acts as the general-filter
- Assigned to : Maxim Chipeev
- Tasks :
  - created a second search bar for location
