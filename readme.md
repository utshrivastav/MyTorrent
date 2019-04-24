# Conferece talk survey (rest app)
>Epic Link : https://jira2.cerner.com/browse/ACADEM-35864

### Table of content
- [Description](#description)
- [Technologies Used](#technologies-used)
- [How to install](#how-to-install)


## Description
As a Conference Talk volunteer, we would like a system that encourages and captures survey comments from Conference talks rapidly so that we are not manually entering in 100 cards per hour as it is very boring. We know, we are done when we have a system that will automatically enter 60-100% of the comments without sacrificing response rate.

We need a way to gather comments from participants. We've found that online surveys have a very low response rate, so we've been favoring paper cards. The paper cards are labor-intensive. We either need a solution that scans most of the comments from the paper cards or that incentivizes participants to enter them digitally.

## Technologies Used
- Backend : Springboot
- Frontend : React
- Database : Postgresql

## How to install
#### Installation
1.Clone the repository to your machine by running the command git clone (clone link) using cmd/git bash/terminal.

2.Install [JDK](https://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html) on to your machine.

3.Set your JAVA_HOME and JRE_HOME environement variable set up based on your Operaing System environment variable setup.

4.Install [PostgreSQL](https://www.postgresql.org/download/) on to your machine.

	a.Make sure to install pgAdmin (checked default) which helps manage database connection.
	b.Set the default user to "user" and password to "password" which is used in Springboot api's connection detatils.
	 
4.Creating the database.
	
	a.Open pgAdmin from the Start Menu/Application Center or command prompt/terminal.
	b.A local webpage will be launched to manage the database.
	c.Click 'servers' from the left panel.
	d.You will be prompted to enter the password which is 'password'.
	e.Select Database and then right click on Datatbase. Select Create > Database.
	f.Write Database Name 'conferencetalksurvey' and Save.

5.Install [Eclipse](https://www.eclipse.org/downloads/) on to your machine.

	a.Open help > eclipse marketplace.
	b.Search "Spring Tools" from search and install "Spring Tools (aka Spring IDE and Spring Tool Suite)".
	c.Note: the version used for initial setup was 3.9.4 RELEASE, as long as new version does not contain major changes latest version should be fine.
	d.

6.Run the following command on the to download all the dependencies for the web app.
```
	npm install
```

7.Run the following command to get the web app up and running while being inside the same directory.
```
	npm start
```
