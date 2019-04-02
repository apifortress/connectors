import static groovyx.net.http.ContentType.JSON
import static groovyx.net.http.Method.POST
import groovy.json.JsonOutput
import groovyx.net.http.HTTPBuilder

import com.apipulse.afconnector.AFConnectorWrapper

public class GTTDigestor extends AFConnectorWrapper{

    private static final String alertSource = 'APIFortress'
    private static final String origin =' APIFortress'

    public void run(){
        final String url = params['url']

        final String alertGroup = params['Alert_Group']

        int score = 0
        String state = 'good'
        if(message.failuresCount>0) {
            score = 10
            state = 'bad'
        }
        if(message.criticalFailures.size()>0)
            score = 50

        String name = 'APIFortress '+message.projectName+' / '+message.test.name+' Alert'

        String details = 'https://mastiff.apifortress.com/app/web/events/details/'+message.eventId+'?cid='+message.companyId
        if(score == 50)
            message.criticalFailures.each {
                details+='\n'+it.expression+' : '+it.status
            }

        def output = ['Alert_Group':alertGroup,
                      'Alert_Score':score,
                      'Alert_Source':alertSource,
                      'Alert_State':state,
                      'Detail':details,
                      'Name':name,
                      'Origin':origin]


        final String data = JsonOutput.toJson(output)

        final HTTPBuilder http = new HTTPBuilder(url)
        http.getClient().getParams().setParameter("http.connection.timeout", 10000)
        http.getClient().getParams().setParameter("http.socket.timeout", 10000)
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