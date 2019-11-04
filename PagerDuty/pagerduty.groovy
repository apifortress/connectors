import static groovyx.net.http.ContentType.JSON
import static groovyx.net.http.Method.POST
import groovy.json.JsonOutput
import groovyx.net.http.HTTPBuilder

import com.apipulse.afconnector.AFConnectorWrapper

public class PagerDuty extends AFConnectorWrapper{

    public void run(){

        final String url = 'https://events.pagerduty.com/v2/enqueue'

        final String summary = 'This is an automated notification caused by the failure of an API Fortress test in project *'+message.companyName+' / '+message.projectName+'*'
        final String source = 'Test failure detected in \''+message.test.name+'\''
        final String severity = params['severity'] //required: critical, error, warning, or info
        final String key = params['routing_key'] //required: Integration Key for given service in pagerduty
        final String action = params['event_action'] //required: trigger, acknowledge, resolve
        final String dedupe = params['dedup_key'] //optional: key to match response with
        final String link = 'https://mastiff.apifortress.com/app/web/events/details/'+message.eventId+'?cid='+message.companyId

        final String[] tags = message.tags

        if (tags.contains("critical")) {
            severity = "critical"
        }else if (tags.contains("error")) {
            severity = "error"
        }else if (tags.contains("warning")) {
            severity = "warning"
        }else if (tags.contains("info")) {
            severity = "info"
        }else {
            severity = params['severity']
        }

        def pb = [
                'payload': [
                        'summary': summary,
                        'timestamp': new Date(),
                        'source': source,
                        'severity': severity
                ],
                'routing_key': key,
                'dedup_key': dedupe,
                'links': [[
                  'href': link,
                  'text': 'Review the event here on API Fortress'
                ]],
                'event_action': action
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
