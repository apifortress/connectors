import com.apipulse.afconnector.AFConnectorWrapper

import groovy.json.JsonOutput;
import groovyx.net.http.*
import static groovyx.net.http.Method.POST
import static groovyx.net.http.ContentType.URLENC
/**
 * The Slack Webhook connector.
 * Params required:
 * username
 * channel
 * hook
 * @author Simone Pezzano - simone@apifortress.com
 *
 */
public class SlackWebhooks extends AFConnectorWrapper{

	/**
	 * The slack endpoint for webhooks
	 */
	private static ROOTURL = 'https://hooks.slack.com/services/'

	public void run(){

		String title = 'Test failure detected \''+message.test.name+'\''
		String pretext = 'This is an automated notification caused by the failure of an API Fortress test in project *'+message.companyName+' / '+message.projectName+'*'
		/*
		 * The text that is going to be displayed
		 */
		final String text = 'Review the event at https://mastiff.apifortress.com/app/web/events/details/'+message.eventId+'?cid='+message.companyId;
		/*
		 * The username
		 */
		final String username = params['username']
		/*
		 * The hook codes
		 */
		final String url = ROOTURL+params['hook']

		/*
		 * We prepare the payload
		 */
		def payload = [:]
		payload['username'] = username
		payload['attachments'] = []
		def attachment = [:]
		attachment['title'] = title
		attachment['pretext'] = pretext
		attachment['text'] = text
		attachment['mrkdwn_in'] = ['text','pretext']
		attachment['color'] = message.criticalFailures.size()>0 ? '#FF0000': '#FF9100'
		attachment['fields'] = []
		attachment['fields'] += [title:'failures',value:message.failuresCount,short:true]
		attachment['fields'] += [title:'critical',value:message.criticalFailures.size(),short:true]
		payload['attachments'] += attachment

		payload = JsonOutput.toJson(payload)
		/*
		 * The request
		 */
		final HTTPBuilder http = new HTTPBuilder(url)

		http.request(POST,URLENC){
			body = [payload:payload]
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
