import static groovyx.net.http.ContentType.*
import static groovyx.net.http.Method.POST
import groovy.json.JsonOutput
import groovyx.net.http.HTTPBuilder

import com.apipulse.afconnector.AFConnectorWrapper

public class Splunk extends AFConnectorWrapper{

    public void run(){

        final String url = params['url'] //required: entire url for your HEC, URL should include the port within it.
        final String token = params['hec_token'] //required: HEC token you generated
        
        url+='/services/collector/event'

        def pb = [
                'event': message
        ]

        final String data = JsonOutput.toJson(pb)

        final HTTPBuilder http = new HTTPBuilder(url)
        http.ignoreSSLIssues()
        http.headers['Authorization'] = 'Splunk '+ token
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