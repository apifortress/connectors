# API Fortress connector

## Name
Jira

## Notes
Two implementations for the Jira connector. A simple one that will create a ticket for each failure,
and a more complex one that will create one ticket for the failure of a specific test and then add up
to 10 comments for each subsequent failure.

As Jira setups can vary a lot based on configuration and requirements, consider these as the code base
to create a more sophisticated connector that better suits your needs.

## Description
Creates Jira reports for test failures.


## Mode
* **Event**
* Run On Successful events: **False**

## Param 1
* **Name** : hostname
* **Description** : The hostname of your Jira instance

## Param 2
* **Name** : username
* **Description** : A valid Jira username with permissions to create tickets

## Param 3
* **Name** : password
* **Description** : A password for the given username

## Param 4
* **Name** : project_name
* **Description** : The identifier of the project to send the reports to

## Param 5
* **Name** : issue_type
* **Description** : The identifier of the issue type to be used

## Code
* Finished: **True**
* Open: **True**
