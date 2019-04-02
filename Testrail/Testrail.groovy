import com.apipulse.afconnector.AFConnectorWrapper

import groovy.json.JsonOutput;
import groovyx.net.http.*
import static groovyx.net.http.Method.POST
import static groovyx.net.http.ContentType.URLENC
/**
 * The Testrail Webhook connector.
 * Params required:
 * username
 * password
 * project id
 * assigned user id
 * @author Simone Pezzano & Jason Ioannides - simone@apifortress.com/jason@apifortress.com
 *
 */
public class Testrail extends AFConnectorWrapper{

    public void run(){

        /*
         * The text that is going to be displayed
         */
        final String text = 'Review the event at https://mastiff.apifortress.com/app/web/events/details/'+message.eventId+'?cid='+message.companyId;
        /*
         * The params
         */

        final String domain = params['domain']

        final String username = params['username']

        final String password = params['password']

        final String projId = params['project_id']

        /*
         * We prepare the payload
         */
        def payload = [:]
        payload['status_id'] = message.failuresCount>0 ? '5' : '1'
        payload['comment'] = text

        payload = JsonOutput.toJson(payload)

        /*
        Endpoint
        */

        String url = 'https://'+domain+'/index.php?/api/v2/add_result/'+projId

        /*
         * The request
         */
        final HTTPBuilder http = new HTTPBuilder(url)
        http.headers['Content-Type'] = 'application/json'
        http.headers['Authorization'] = 'Basic '+"$username:$password".getBytes('iso-8859-1').encodeBase64()
        http.request(POST,URLENC){
            body = payload
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