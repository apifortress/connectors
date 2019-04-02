import com.apipulse.afconnector.AFConnectorWrapper
import groovy.json.JsonOutput
import groovyx.net.http.HTTPBuilder
import static groovyx.net.http.Method.POST
import static groovyx.net.http.ContentType.JSON

/**
 * datadog connector.
 * Params required:
 * API Key
 * Created by Carlo Casas on 8/16/16.
 * API Fortress
 * carlogcasas@gmail.com
 */

public class DataDog extends AFConnectorWrapper{
    /*
    *Event posting Url
     */
    private static String ROOT_URL = 'https://app.datadoghq.com/api/v1/check_run'

    public void run(){
        /*
        * Params required for Authentication
         */
        final String apiKey = params['apiKey']
        final String host = params['host']
        /*
        *Params required to post event
         */
        def check = message.projectName+' / '+message.test.name
        def tags = message.projectName
        def date = (Integer)System.currentTimeMillis()/1000

        boolean hasFailed = message.failuresCount > 0
        int status = 0 // 0 for 'ok'
        if(hasFailed) {
            status = message.criticalFailures.size() > 0 ? 2 : 1 // 'critical status' = 2 ** 'warning' = 1
        }
        String dogMessage = 'https://mastiff.apifortress.com/app/web/events/details/'+message.eventId+'?cid='+message.companyId+' \n'

        if(status == 'critical')
            message.criticalFailures.each {
                message+=it.expression+' : '+it.status+' \n'
            }

        def payload = [
                'check':check,
                'status':status,
                'time_stamp': date,
                'message': dogMessage,
                'tags': tags,
                'host_name': host
        ]
        payload = JsonOutput.toJson(payload)

        def authUrl = "$ROOT_URL?api_key=$apiKey"

        final HTTPBuilder http = new HTTPBuilder(authUrl)
        http.headers['Content-Type'] = 'application/json'
        http.request(POST, JSON){
            body = payload
            response.success = { resp, reader ->
                println "Sucess! ${resp.status}"
                System.out << reader
            }
            response.failure = {resp, reader ->
                println "Request failed with status ${resp.status}"
                System.out << reader
            }
        }

    }
}
