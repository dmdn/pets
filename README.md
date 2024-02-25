# ZOO
Test Task

## Project Description
The project was created as a test task at one of the interviews

When connecting to the Internet, the app takes data from the server. If there is an error on the server side or there is no Internet, the app takes data from the Realm database. In the app, you can create objects (PETs), edit and delete them. All changes are put to the Realm database and sent to the server. When there is no internet changes are put to Realm, and when you connect the Internet, changes are sent to the server.
The app works with the PET category SOLD. In the app, you can search for PETs by NAME in the local database.
In the menu, you can choose to scroll the list in the END or the START of the list. The list sorted ascending PETs ID. In the menu item you can display the number of PETs in the list.

In addition, Notifications about updating application data from the server were displayed. The Notification is displayed after 60 seconds and shows the last updatings data. The Notification is displayed using Services.

## Technical Task
+ Create the project which one will work [Swagger Petstore](http://petstore.swagger.io/) API
+ Use all API under the PET categories
+ Create screen which one will show all created pet in the system
+ Create screen which one will let create and edit PET
+ Need to be able to upload the image for PET
+ Use Realm for storing your pet on the mobile device

### Basic skills for the project creation
+ RESTful APIs (GET, POST, DELETE, PUT)
+ CardView
+ [Retrofit 2](http://square.github.io/retrofit/)
+ [RxJava 2](https://github.com/ReactiveX/RxJava)
+ Databases [Realm](https://realm.io/docs/java/latest/)
+ [Picasso](http://square.github.io/picasso/)
+ Service
+ Notifications

![screenshot_2017-09-24-15-44-21](https://user-images.githubusercontent.com/19373990/30782831-020053c4-a142-11e7-852b-576097023fdb.png)
![screenshot_2017-09-24-15-45-12](https://user-images.githubusercontent.com/19373990/30782830-01ffaf28-a142-11e7-9eef-ba6bb67d3672.png)
![screenshot_2017-09-24-15-44-50](https://user-images.githubusercontent.com/19373990/30782832-020076b0-a142-11e7-97f3-474eabd9ee45.png)
![screenshot_2017-09-24-15-51-19](https://user-images.githubusercontent.com/19373990/30782829-01fd0bc4-a142-11e7-8aed-33fbddada4c6.png)
![screenshot_2017-10-02-15-12-19](https://user-images.githubusercontent.com/19373990/31076965-5a56e10c-a785-11e7-93df-ae0aba7f57ad.png)


The installation file `.apk` can be found in `./storage/zoo_beta_0.3.apk`
