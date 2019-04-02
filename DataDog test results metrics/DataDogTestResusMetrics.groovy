import com.apipulse.afconnector.AFConnectorWrapper
import groovy.json.JsonOutput
import groovyx.net.http.HTTPBuilder
import static groovyx.net.http.Method.POST
import static groovyx.net.http.ContentType.JSON

public class DataDogAsMetrics extends AFConnectorWrapper{
    /*
    *Event posting Url
     */
    private static String ROOT_URL = 'https://app.datadoghq.com/api/v1/series'

    public void run(){
        /*
        * Params required for Authentication
         */
        final String apiKey = params['apiKey']
        final String host = params['host']

        boolean hasFailed = message.failuresCount > 0
        String status = 'success' // 0 for 'ok'
        if(hasFailed) {
            status = message.criticalFailures.size() > 0 ? 'critical' : 'failure'
        }

        def payload = [series:[[
                                       'metric':'apifortress.events',
                                       'type':'rate',
                                       'tags': ['project.'+projectName,'test.'+message.test.name,'status.'+status],
                                       'host': host
                               ]]
        ]
        if(message.location)
            payload['series'][0]['tags'].add('location.'+message.location)
        perform(payload,apiKey)

    }


    private static synchronized void perform(def payload,String apiKey){
        long date = (long)System.currentTimeMillis()/1000
        payload['series'][0]['points'] = [[date,1]]
        payload = JsonOutput.toJson(payload)
        println(payload)
        def authUrl = "$ROOT_URL?api_key=$apiKey"

        final HTTPBuilder http = new HTTPBuilder(authUrl)
        http.headers['Content-Type'] = 'application/json'
        http.getClient().getParams().setParameter("http.socket.timeout", new Integer(5000))
        http.getClient().getParams().setParameter("http.connection.timeout", new Integer(5000))
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
        Thread.sleep(1000)
    }
}
