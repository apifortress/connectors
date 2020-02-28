# API Fortress connector

## Name
Twilio

## Description
This connector will send a text via twilio messages API, to alert a test failure.

## Mode
* Mode: **Event**
* Run On Successful events: **False**

## Param 1
* **Name** : credentials
* **Description** : This is the API credentials generated in Twilio. Use the format AccountSID:AuthToken.

## Param 2
* **Name** : from
* **Description** : This is the "from" phone number that you own in Twilio. Please use the format "+(country_code)#######" i.e. +15555555555

## Param 3
* **Name** : to
* **Description** : This is the phone number you would like to receive the message. Please use the format "+(country_code)#######" i.e. +15555555555

## Code
* Finished: **True**
* Open: **True**
