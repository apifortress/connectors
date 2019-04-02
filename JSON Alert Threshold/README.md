# API Fortress connector

## Name
JSON Alert /w Threshold

## Description
Sends an alert to a WebHook, respecting certain threshold restrictions. Designed to work with services like Zapier or IFTTT

## Mode
* **Event**
* Run On Successful events: **False**

## Param 1
* **Name** : url
* **Description** : The URL of the WebHook to hit

## Param 2
* **Name** : counter
* **Description** : Number of FAILURES in a time range before the call is actually triggered

## Param 3
* **Name** : ttl
* **Description** : The time rage in SECONDS

## Code
* Finished: **True**
* Open: **True**
