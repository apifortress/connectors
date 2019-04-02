import com.apipulse.afconnector.AFConnectorWrapper
import groovyx.net.http.HTTPBuilder

import static groovyx.net.http.ContentType.URLENC
import static groovyx.net.http.Method.*

public class StatusPage extends AFConnectorWrapper{

    private static String ROOT_URL = 'https://api.statuspage.io/v1/pages/'
    @Override
    void run() {
        String pageId = params['pageId']
        String apikey = params['apikey']

        String urlPostIncident = ROOT_URL+pageId+'/incidents.json'
        String urlRetrieveIncidents = ROOT_URL+pageId+'/incidents/unresolved.json'

        String status = message.failuresCount > 0 ? 'identified' : 'resolved'

        String name = 'API Fortress test execution for '+message.projectName+' / '+message.test.name


        def postBody = ['incident[name]': name, 'incident[status]':status]
        if(message.criticalFailures.size()>0){
            postBody['incident[message]'] = ''
            message.criticalFailures.each {
                postBody['incident[message]'] = postBody['incident[message]'] + it.expression+' : '+it.status+' \n'
            }
        }


        final HTTPBuilder http = new HTTPBuilder(urlRetrieveIncidents)
        http.headers['Authorization'] = 'OAuth '+apikey
        http.headers['Accept'] = 'application/json'
        /**
         * Search for existing, unresolved events
         */
        http.request(GET){
            response.success = { resp,json ->
                def event = json.find { it.name == name}
                /**
                 * Event exists
                 */
                if(event){
                    // we resolve the incident
                    if(status == 'resolved') {
                        HTTPBuilder http2 = new HTTPBuilder(ROOT_URL + pageId + '/incidents/' + event.id + '.json')
                        http2.headers['Authorization'] = 'OAuth ' + apikey
                        http2.request(PATCH,URLENC){
                            body = postBody
                            response.success = { resp2 ->
                                println "Success! ${resp2.status}"
                            }
                            response.failure = { resp2,reader2 ->
                                println "Request failed with status ${resp2.status}"
                                System.out << reader2
                            }
                        }
                    }
                } else
                // Event does not exist
                {
                    if(status == 'identified'){
                        HTTPBuilder http2 = new HTTPBuilder(urlPostIncident)
                        http2.headers['Authorization'] = 'OAuth ' + apikey
                        http2.request(POST,URLENC){
                            body = postBody
                            response.success = { resp2 ->
                                println "Success! ${resp2.status}"
                            }
                            response.failure = { resp2,reader2 ->
                                println "Request failed with status ${resp2.status}"
                                System.out << reader
                            }
                        }
                    }
                }
            }
            response.failure = { resp,reader ->
                println "Request failed with status ${resp.status}"
                System.out << reader
            }
        }
    }
}