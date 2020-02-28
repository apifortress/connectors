import static groovyx.net.http.ContentType.*
import static groovyx.net.http.Method.POST
import groovy.json.JsonOutput
import groovyx.net.http.HTTPBuilder

import com.apipulse.afconnector.AFConnectorWrapper

public class Twilio extends AFConnectorWrapper{

    public void run(){

        final String credentials = params['credentials'] //required: Account SID and Auth Token
        final String from = params['from'] //required: Number you own in Twilio to send message from, use format +(country_code)#######
        final String to = params['to'] //required: Number you would like the alert message to go to +(country_code)#######

        final String source = 'Test failure detected in \''+message.test.name+'\''
        
        def login = credentials.split(':')

        final String url = 'https://api.twilio.com/2010-04-01/Accounts/'+login[0]+'/Messages.json'

        final HTTPBuilder http = new HTTPBuilder(url)
        http.ignoreSSLIssues()
        http.request(POST,URLENC){
            body = [Body: source + '\n\nPlease login to your API Fortress instance to see more detail on this error.', From: from, To: to]
            headers['Authorization'] = 'Basic ' + credentials.bytes.encodeBase64().toString()
            response.success = { resp ->
                println "Success! ${resp.status}"
            }
            response.failure = { resp,reader ->
                println "Request failed with status ${resp.status}"
                System.out << reader
            }
        }
    }
}