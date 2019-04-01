import com.apipulse.afconnector.AFConnectorWrapper

import groovy.json.JsonOutput;
import groovyx.net.http.*
import static groovyx.net.http.Method.POST
import static groovyx.net.http.ContentType.URLENC
/**
 * The BigPanda Webhook connector.
 * Params required:
 * authorization
 * appkey
 * @author Simone Pezzano - simone@apifortress.com
 *
 */
public class BigPanda extends AFConnectorWrapper{

    /**
     * The BigPanda endpoint for webhooks
     */
    private static ROOTURL = 'https://api.bigpanda.io/data/v2/alerts'

    public void run(){

        /*
         * The username
         */
        final String authorization = params['authorization']

        final String appKey = params['appkey']

        boolean hasFailed = message.failuresCount > 0

        String service = 'API Fortress'

        String checkText = message.projectName+' / '+message.test.name

        String status = 'ok'
        if(hasFailed) {
            status = message.criticalFailures.size() > 0 ? 'critical' : 'warning'
        }
        String description = 'https://mastiff.apifortress.com/app/web/events/details/'+message.eventId+'?cid='+message.companyId+' \n'

        if(status == 'critical')
            message.criticalFailures.each {
                description+=it.expression+' : '+it.status+' \n'
            }

        def payload = ['app_key':appKey,'status':status,'service':service,'timestamp':(Integer)System.currentTimeMillis()/1000,'check': checkText,'description':description]


        payload = JsonOutput.toJson(payload)

        println payload
        /*
         * The request
         */
        final HTTPBuilder http = new HTTPBuilder(ROOTURL)
        http.headers['Authorization'] = authorization
        http.headers['Content-Type'] = 'application/json'
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
