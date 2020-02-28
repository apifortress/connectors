import static groovyx.net.http.ContentType.*
import static groovyx.net.http.Method.POST
import groovy.json.JsonOutput
import groovyx.net.http.HTTPBuilder

import com.apipulse.afconnector.AFConnectorWrapper

public class Twilio extends AFConnectorWrapper{

    public void run(){

        final String url = params['trigger_url'] //required: entire trigger url that is genertated in your workflow, URL should include the api key within it.
        final String recipients = params['recipients'] //required: Please pass in userid (multiple users should be comma separated), groupid (multiple groups should be comma separated), or userid|deviceid (in this format)

        final String link = 'https://mastiff.apifortress.com/app/web/events/details/'+message.eventId+'?cid='+message.companyId
        final String subject = 'Test failure detected in *'+message.companyName+' / '+message.projectName+'*'
        final String message = 'This is an automated notification caused by the failure of the API Fortress test \''+message.test.name+'\' click here to see the failure: ' +link

         def pb = [
                'Summary': subject,
                'Message': message,
                'Recipients': recipients
        ]

        final String data = JsonOutput.toJson(pb)

        final HTTPBuilder http = new HTTPBuilder(url)
        http.ignoreSSLIssues()
        http.request(POST,JSON){
            body = data
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