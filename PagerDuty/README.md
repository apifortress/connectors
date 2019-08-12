# API Fortress connector

## Name
PagerDuty

## Description
This connector will post the failed event to pagerduty event api, triggering a pager alert

## Mode
* Mode: **Event**
* Run On Successful events: **False**

## Param 1
* **Name** : routing_key
* **Description** : This is the integration key generated for a service in PagerDuty

## Param 2
* **Name** : severity
* **Description** : This is the severity level of the alert (critial, error, warning, info)

## Param 3
* **Name** : dedup_key
* **Description** : This is the key set on the alert to match back with the response to alert

## Param 4
* **Name** : event_action
* **Description** : This is the event action desired (trigger, acknowledge, resolve)

## Code
* Finished: **True**
* Open: **True**
