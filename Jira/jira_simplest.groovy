import static groovyx.net.http.ContentType.JSON
import static groovyx.net.http.Method.POST
import groovy.json.JsonOutput
import groovyx.net.http.*

import com.apipulse.afconnector.AFConnectorWrapper
public class Jira extends AFConnectorWrapper{
	
	public void run(){
		
		
		final String hostname = params['hostname']
		
		final String url = 'https://'+hostname+'/rest/api/2/issue/'

		/*
		 * The username
		 */
		final String username = params['username']
		
		final String password = params['password']

		final String projectName = params['project_name']
		
		final String issueType = params['issue_type']
		
		final String summary = "AF error for \"${message.projectName} / ${message.test.name}\""
		
		StringBuilder description = new StringBuilder()
		
		description.append("ApiFortress detected an API error while running test \"${message.test.name}\".\n")
		description.append("The number of failed assertions is: ${message.failuresCount}.\n");
		
		if (message.criticalFailures.size()>0)
			description.append('Critical failures (endpoint unavailability) has also been reported.\n')
		else
			description.append('No critical failures (endpoint unavailability) have been reported.\n')
			
		description.append("Review the event at https://mastiff.apifortress.com/app/web/events/details/${message.eventId}?cid=${message.companyId}")
		
		def payload = ['fields':['project':['key':projectName],summary:summary,description:description.toString(),	issuetype:[name:issueType]]]
		payload = JsonOutput.toJson(payload)
		
		/*
		 * The request
		 */
		final HTTPBuilder http = new HTTPBuilder(url)
		http.headers['Authorization'] = 'Basic '+"$username:$password".getBytes('iso-8859-1').encodeBase64()
		http.request(POST,JSON){
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