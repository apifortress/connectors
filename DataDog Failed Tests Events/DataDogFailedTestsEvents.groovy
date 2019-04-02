import com.apipulse.afconnector.AFConnectorWrapper
import groovy.json.JsonOutput
import groovyx.net.http.HTTPBuilder
import static groovyx.net.http.Method.POST
import static groovyx.net.http.ContentType.JSON

public class DataDogEvents extends AFConnectorWrapper{
    /*
    *Event posting Url
     */
    private static String ROOT_URL = 'https://app.datadoghq.com/api/v1/events'

    public void run(){
        /*
        * Params required for Authentication
         */
        final String apiKey = params['apiKey']

        boolean hasFailed = message.failuresCount > 0
        String status = 'OK' // 0 for 'ok'
        if(hasFailed) {
            status = message.criticalFailures.size() > 0 ? 'critical' : 'error'
        }

        String title = 'API Fortress test failure for '+projectName+'/'+message.test.name
        String msg = 'https://mastiff.apifortress.com/app/web/events/details/'+message.eventId+'?cid='+message.companyId
        String alertType = (status =='critical' || status=='error') ? 'error': 'success'

        def payload = [title:title,
                       text:msg,
                       tags: [projectName,message.test.name],
                       alert_type:alertType
        ]
        payload = JsonOutput.toJson(payload)

        def authUrl = "$ROOT_URL?api_key=$apiKey"


        final HTTPBuilder http = new HTTPBuilder(authUrl)
        http.getClient().getParams().setParameter("http.socket.timeout", new Integer(5000))
        http.getClient().getParams().setParameter("http.connection.timeout", new Integer(5000))
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
