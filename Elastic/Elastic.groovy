import com.apipulse.afconnector.AFConnectorWrapper
import groovy.json.JsonOutput
import groovyx.net.http.*

import static groovyx.net.http.ContentType.JSON
import static groovyx.net.http.Method.POST
import static groovyx.net.http.ContentType.URLENC
/**
 * The Hipchat connector.
 * Params required:
 * username
 * roomId
 * token
 * @author Simone Pezzano - simone@apifortress.com
 *
 */
public class ElasticSearch extends AFConnectorWrapper{

	public void run(){
		/*
         * The text that is going to be displayed
         */
		message._date = new Date(message.getLong('date'))
		/*
         * The username
         */
		final String index = params['index'] ?: String.valueOf(message.projectName.toLowerCase())

		final String type = params['type'] ?: 'event'

		final String eventtype = ""

		if (message.failuresCount == 0) {
			eventtype = 'success'
		} else {
			eventtype = 'failure'
		}

		message.eventType = eventtype
		final String text = JsonOutput.toJson(message)

		final String username = params['username']

		final String password = params['password']

		final String baseUrl = params['baseUrl']
		if(!baseUrl.endsWith('/'))
			baseUrl+='/'


		final HTTPBuilder http = new HTTPBuilder(baseUrl+index+'/'+type)
		http.auth.basic(username, password)
		http.request(POST,JSON){
			body = text
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