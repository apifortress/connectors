# API Fortress connector

## Name
xMatters

## Description
This connector will send an alert via xmatters API based on your workflow, to alert a test failure.

## Mode
* Mode: **Event**
* Run On Successful events: **False**

## Param 1
* **Name** : trigger_url
* **Description** : This is the entire trigger url that is generated in your workflow. The URL should include the api key within it.

## Param 2
* **Name** : recipients
* **Description** : This is the recipient you would like to alert. Please pass in userid (multiple users should be comma separated), groupid (multiple groups should be comma separated), or userid|deviceid (in this format)

## Code
* Finished: **True**
* Open: **True**
